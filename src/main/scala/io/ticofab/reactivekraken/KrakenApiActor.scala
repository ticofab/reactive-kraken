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

  private def getRequest(path: String, params: Option[Map[String, String]] = None, sign: Boolean = false): HttpRequest = {
    val uri = params match {
      case Some(value) => Uri(basePath + path).withQuery(Query(value))
      case None => Uri(basePath + path)
    }
    if (sign) getSignedRequest(path, uri) else HttpRequest(uri = uri)
  }

  private def getSignedRequest(path: String, uri: Uri) = {
    val nonce = nonceGenerator.apply
    val postData = "nonce=" + nonce.toString
    val signature = Signer.getSignature(path, nonce, postData, apiSecret)
    val headers = List(RawHeader("API-Key", apiKey), RawHeader("API-Sign", signature))
    HttpRequest(HttpMethods.POST, uri, headers, FormData(Map("nonce" -> nonce.toString)).toEntity)
  }

  private def handleRequest[T: JsonFormat](request: HttpRequest): Future[Response[T]] =
    fireRequest(request)
      .map(_.parseJson.convertTo[Response[T]])
      .recover { case t: Throwable => Response[T](List(t.getMessage), None) }

  private def extractMessage[TYPE, MESSAGE, CONTENT](resp: Response[TYPE],
                                                     messageFactory: Either[List[String], CONTENT] => MESSAGE,
                                                     contentFactory: Response[TYPE] => CONTENT): MESSAGE = {
    if (resp.error.nonEmpty) messageFactory(Left(resp.error))
    else if (resp.result.isDefined) messageFactory(Right(contentFactory(resp)))
    else messageFactory(Left(List("Something went wrong: response has no content.")))
  }

  implicit def toOption(m: Map[String, String]): Option[Map[String, String]] = Some(m)

  override def receive = {

    case GetCurrentAssets =>
      val path = "/0/public/Assets"
      val request = getRequest(path)
      handleRequest[Map[String, Asset]](request).map { resp =>
        extractMessage[Map[String, Asset], CurrentAssets, Map[String, Asset]](resp, CurrentAssets, _.result.get)
      }.pipeTo(sender)

    case GetCurrentAssetPair(currency, respectToCurrency) =>
      val path = "/0/public/AssetPairs"
      val params = Map("pair" -> (currency + respectToCurrency))
      val request = getRequest(path, params)
      handleRequest[Map[String, AssetPair]](request).map { resp =>
        extractMessage[Map[String, AssetPair], CurrentAssetPair, Map[String, AssetPair]](resp, CurrentAssetPair, _.result.get)
      }

//    case GetCurrentTicker(currency, respectToCurrency) =>
//      val path = "/0/public/Ticker"
//      val params = Map("pair" -> (currency + respectToCurrency))
//      val request = getRequest(path, params)
//      apiResponse[Ticker, CurrentTicker](path, params, CurrentTicker).pipeTo(sender)

    //    case GetCurrentAccountBalance =>
    //      val path = "/0/private/Balance"
    //      apiResponse[String, CurrentAccountBalance](path, None, CurrentAccountBalance, sign = true).pipeTo(sender)
    //
    //    case GetCurrentTradeBalance(asset) =>
    //      val path = "/0/private/TradeBalance"
    //      val params = asset.flatMap(value => Map("asset" -> value))
    //      apiResponse[TradeBalance, CurrentTradeBalance](path, params, CurrentTradeBalance, sign = true).pipeTo(sender)

    case GetCurrentOpenOrders =>
      val path = "/0/private/OpenOrders"
      val request = getRequest(path, None, sign = true)
      handleRequest[OpenOrder](request).map { resp =>
        extractMessage[OpenOrder, CurrentOpenOrders, Map[String, Order]](resp, CurrentOpenOrders, _.result.get.open.get)
      }.pipeTo(sender)

    case GetCurrentClosedOrders =>
      val path = "/0/private/ClosedOrders"
      val request = getRequest(path, None, sign = true)
      handleRequest[ClosedOrder](request).map { resp =>
        extractMessage[ClosedOrder, CurrentClosedOrders, Map[String, Order]](resp, CurrentClosedOrders, _.result.get.closed.get)
      }.pipeTo(sender)

  }


}

object KrakenApiActor {
  def apply(nonceGenerator: () => Long) = Props(new KrakenApiActor(nonceGenerator))
}
