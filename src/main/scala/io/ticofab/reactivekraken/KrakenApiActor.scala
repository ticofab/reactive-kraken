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

class KrakenApiActor(nonceGenerator: () => Long,
                     maybeApiKey: Option[String] = None,
                     maybeApiSecret: Option[String] = None) extends Actor with JsonSupport with HttpRequestor {

  protected implicit val as = context.system
  protected implicit val am = ActorMaterializer()

  // convenient structure to merge the two options
  private val credentials: Option[(String, String)] = for {key <- maybeApiKey; secret <- maybeApiSecret} yield (key, secret)

  /**
    *
    * @param path   The relative path of the request
    * @param params Optional request parameters
    * @return The correct Uri for this request
    */
  private def getUri(path: String, params: Option[Map[String, String]] = None): Uri = {
    val basePath = "https://api.kraken.com"
    params match {
      case Some(value) => Uri(basePath + path).withQuery(Query(value))
      case None => Uri(basePath + path)
    }
  }

  /**
    * Creates the signed HTTP request to fire
    *
    * @param path      The request path.
    * @param params    The request query params.
    * @param apiKey    The user's API key
    * @param apiSecret The user's API secret
    * @return The appropriate HTTP request to fire.
    */
  private def getSignedRequest(path: String,
                               apiKey: String,
                               apiSecret: String,
                               params: Option[Map[String, String]] = None) = {
    val nonce = nonceGenerator.apply
    val postData = "nonce=" + nonce.toString
    val signature = Signer.getSignature(path, nonce, postData, apiSecret)
    val headers = List(RawHeader("API-Key", apiKey), RawHeader("API-Sign", signature))
    val uri = getUri(path, params)
    HttpRequest(HttpMethods.POST, uri, headers, FormData(Map("nonce" -> nonce.toString)).toEntity)
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

  /**
    * Manages the lifecycle of a request for an authenticated endpoint
    *
    * @param path        The relative path of the API request.
    * @param getResponse The function that will transform a request into a response
    * @param params      The optionals parameters of the HTTP request
    * @return A message to send back to the original sender.
    */
  def getAuthenticatedAPIResponseMessage(path: String,
                                         getResponse: HttpRequest => Future[MessageResponse],
                                         params: Option[Map[String, String]] = None): Future[MessageResponse] =
    credentials match {
      case None => Future(KrakenApiActorError(s"Credentials are required for this request"))
      case Some((key, secret)) => getResponse(getSignedRequest(path, key, secret, params))
    }

  override def receive = {

    case GetCurrentAssets =>
      val path = "/0/public/Assets"
      val request = HttpRequest(uri = getUri(path))
      handleRequest[Map[String, Asset]](request)
        .map(extractMessage[Map[String, Asset], CurrentAssets, Map[String, Asset]](_, CurrentAssets, _.result.get))
        .pipeTo(sender)

    case GetCurrentAssetPair(currency, respectToCurrency) =>
      val path = "/0/public/AssetPairs"
      val params = Map("pair" -> (currency + respectToCurrency))
      val request = HttpRequest(uri = getUri(path, Some(params)))
      handleRequest[Map[String, AssetPair]](request)
        .map(extractMessage[Map[String, AssetPair], CurrentAssetPair, Map[String, AssetPair]](_, CurrentAssetPair, _.result.get))
        .pipeTo(sender)

    case GetCurrentTicker(currency, respectToCurrency) =>
      val path = "/0/public/Ticker"
      val params = Map("pair" -> (currency + respectToCurrency))
      val request = HttpRequest(uri = getUri(path, Some(params)))
      handleRequest[Map[String, Ticker]](request)
        .map(extractMessage[Map[String, Ticker], CurrentTicker, Map[String, Ticker]](_, CurrentTicker, _.result.get))
        .pipeTo(sender)

    case GetCurrentAccountBalance =>
      val path = "/0/private/Balance"
      val f = (request: HttpRequest) => handleRequest[Map[String, String]](request)
        .map(extractMessage[Map[String, String], CurrentAccountBalance, Map[String, String]](_, CurrentAccountBalance, _.result.get))
      getAuthenticatedAPIResponseMessage(path, f).pipeTo(sender)

    case GetCurrentTradeBalance(asset) =>
      val path = "/0/private/TradeBalance"
      val params = asset.flatMap(value => Some(Map("asset" -> value)))
      val f = (request: HttpRequest) => handleRequest[TradeBalance](request)
        .map(extractMessage[TradeBalance, CurrentTradeBalance, TradeBalance](_, CurrentTradeBalance, _.result.get))
      getAuthenticatedAPIResponseMessage(path, f, params).pipeTo(sender)

    case GetCurrentOpenOrders =>
      val path = "/0/private/OpenOrders"
      val f = (request: HttpRequest) => handleRequest[OpenOrder](request)
        .map(extractMessage[OpenOrder, CurrentOpenOrders, Map[String, Order]](_, CurrentOpenOrders, _.result.get.open.get))
      getAuthenticatedAPIResponseMessage(path, f).pipeTo(sender)

    case GetCurrentClosedOrders =>
      val path = "/0/private/ClosedOrders"
      val f = (request: HttpRequest) => handleRequest[ClosedOrder](request)
        .map(extractMessage[ClosedOrder, CurrentClosedOrders, Map[String, Order]](_, CurrentClosedOrders, _.result.get.closed.get))
      getAuthenticatedAPIResponseMessage(path, f).pipeTo(sender)
  }

}

object KrakenApiActor {
  def apply(nonceGenerator: () => Long,
            apikey: Option[String] = None,
            apiSecret: Option[String] = None) = Props(new KrakenApiActor(nonceGenerator, apikey, apiSecret))
}
