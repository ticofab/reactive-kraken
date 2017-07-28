package io.ticofab.reactivekraken

/**
  * Copyright 2017 Fabio Tiriticco, Fabway
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

import akka.actor.{Actor, Props}
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.pattern.pipe
import akka.stream.ActorMaterializer
import io.ticofab.reactivekraken.api.JsonSupport.responseFormat
import io.ticofab.reactivekraken.api.{HttpRequestor, JsonSupport, Response}
import io.ticofab.reactivekraken.messages._
import io.ticofab.reactivekraken.model._
import io.ticofab.reactivekraken.signature.Signer
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.Properties

class KrakenApiActor(nonceGenerator: () => Long) extends Actor with JsonSupport with HttpRequestor {

  implicit val as = context.system
  implicit val am = ActorMaterializer()

  def loadVar(name: String) =
    Properties.envOrNone(name) match {
      case None =>
        context.parent ! "error" // TODO proper error message
        "dummyo"
      case Some(value) => value
    }

  private val apiKey = loadVar("KRAKEN_API_KEY")
  private val apiSecret = loadVar("KRAKEN_API_SECRET")
  private val basePath = "https://api.kraken.com"

  /**
    * Creates the HTTP request to fire
    *
    * @param path   The request path.
    * @param params The request query params.
    * @param sign   Boolean that specifies whether the request has to be signed for authentication or not.
    * @return The appropriate HTTP request to fire.
    */
  private def getRequest(path: String, params: Option[Map[String, String]] = None, sign: Boolean = false): HttpRequest = {

    def getSignedRequest(path: String, uri: Uri) = {
      val nonce = nonceGenerator.apply
      val postData = "nonce=" + nonce.toString
      val signature = Signer.getSignature(path, nonce, postData, apiSecret)
      val headers = List(RawHeader("API-Key", apiKey), RawHeader("API-Sign", signature))
      HttpRequest(HttpMethods.POST, uri, headers, FormData(Map("nonce" -> nonce.toString)).toEntity)
    }

    val uri = params match {
      case Some(value) => Uri(basePath + path).withQuery(Query(value))
      case None => Uri(basePath + path)
    }

    if (sign) getSignedRequest(path, uri) else HttpRequest(uri = uri)
  }

  /**
    * Fires an HTTP request and converts the result to the appropriate type
    *
    * @param request The HTTP request to fire
    * @tparam RESPONSE_CONTENT_TYPE The type of the content expected in case of successful response
    * @return A future of the typed Response
    */
  private def handleRequest[RESPONSE_CONTENT_TYPE: JsonFormat](request: HttpRequest): Future[Response[RESPONSE_CONTENT_TYPE]] =
    fireRequest(request)
      .map(_.parseJson.convertTo[Response[RESPONSE_CONTENT_TYPE]])
      .recover { case t: Throwable => Response[RESPONSE_CONTENT_TYPE](List(t.getMessage), None) }

  /**
    * Extracts the content of a parsed HTTP response and encapsulates it in the proper response message class.
    *
    * @param resp           The typed response from the HTTP request
    * @param messageFactory Function to create the message
    * @param contentFactory Function to create the message content
    * @tparam RESPONSE_CONTENT_TYPE The content of the response
    * @tparam MESSAGE_TYPE          Type of the message 
    * @tparam MESSAGE_CONTENT_TYPE  Type of the message content
    * @return The message to send back to the sender
    */
  private def extractMessage[RESPONSE_CONTENT_TYPE, MESSAGE_TYPE, MESSAGE_CONTENT_TYPE]
  (resp: Response[RESPONSE_CONTENT_TYPE],
   messageFactory: Either[List[String], MESSAGE_CONTENT_TYPE] => MESSAGE_TYPE,
   contentFactory: Response[RESPONSE_CONTENT_TYPE] => MESSAGE_CONTENT_TYPE): MESSAGE_TYPE = {
    if (resp.error.nonEmpty) messageFactory(Left(resp.error))
    else if (resp.result.isDefined) messageFactory(Right(contentFactory(resp)))
    else messageFactory(Left(List("Something went wrong: response has no content.")))
  }

  override def receive = {

    case GetCurrentAssets =>
      val path = "/0/public/Assets"
      val request = getRequest(path)
      handleRequest[Map[String, Asset]](request)
        .map(extractMessage[Map[String, Asset], CurrentAssets, Map[String, Asset]](_, CurrentAssets, _.result.get))
        .pipeTo(sender)

    case GetCurrentAssetPair(currency, respectToCurrency) =>
      val path = "/0/public/AssetPairs"
      val params = Map("pair" -> (currency + respectToCurrency))
      val request = getRequest(path, Some(params))
      handleRequest[Map[String, AssetPair]](request)
        .map(extractMessage[Map[String, AssetPair], CurrentAssetPair, Map[String, AssetPair]](_, CurrentAssetPair, _.result.get))
        .pipeTo(sender)

    case GetCurrentTicker(currency, respectToCurrency) =>
      val path = "/0/public/Ticker"
      val params = Map("pair" -> (currency + respectToCurrency))
      val request = getRequest(path, Some(params))
      handleRequest[Map[String, Ticker]](request)
        .map(extractMessage[Map[String, Ticker], CurrentTicker, Map[String, Ticker]](_, CurrentTicker, _.result.get))
        .pipeTo(sender)

    case GetCurrentAccountBalance =>
      val path = "/0/private/Balance"
      val request = getRequest(path, None, sign = true)
      handleRequest[Map[String, String]](request)
        .map(extractMessage[Map[String, String], CurrentAccountBalance, Map[String, String]](_, CurrentAccountBalance, _.result.get))
        .pipeTo(sender)

    case GetCurrentTradeBalance(asset) =>
      val path = "/0/private/TradeBalance"
      val params = asset.flatMap(value => Some(Map("asset" -> value)))
      val request = getRequest(path, params, sign = true)
      handleRequest[TradeBalance](request)
        .map(extractMessage[TradeBalance, CurrentTradeBalance, TradeBalance](_, CurrentTradeBalance, _.result.get))
        .pipeTo(sender)

    case GetCurrentOpenOrders =>
      val path = "/0/private/OpenOrders"
      val request = getRequest(path, None, sign = true)
      handleRequest[OpenOrder](request)
        .map(extractMessage[OpenOrder, CurrentOpenOrders, Map[String, Order]](_, CurrentOpenOrders, _.result.get.open.get))
        .pipeTo(sender)

    case GetCurrentClosedOrders =>
      val path = "/0/private/ClosedOrders"
      val request = getRequest(path, None, sign = true)
      handleRequest[ClosedOrder](request)
        .map(extractMessage[ClosedOrder, CurrentClosedOrders, Map[String, Order]](_, CurrentClosedOrders, _.result.get.closed.get))
        .pipeTo(sender)

  }

}

object KrakenApiActor {
  def apply(nonceGenerator: () => Long) = Props(new KrakenApiActor(nonceGenerator))
}
