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

import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import akka.actor.{Actor, Props}
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.pattern.pipe
import akka.stream.ActorMaterializer
import io.ticofab.reactivekraken.api.JsonSupport.{orderResponseFormat, responseFormat}
import io.ticofab.reactivekraken.api.{HttpRequestor, JsonSupport, OrderResponse, Response}
import io.ticofab.reactivekraken.messages._
import io.ticofab.reactivekraken.model._
import org.apache.commons.codec.binary.Base64
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.Properties

class KrakenApiActor(nonceGenerator: () => String) extends Actor with JsonSupport with HttpRequestor {

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

  private def getSignature(path: String, nonce: String, postData: String) = {
    // Message signature using HMAC-SHA512 of (URI path + SHA256(nonce + POST data)) and base64 decoded secret API key
    val md = MessageDigest.getInstance("SHA-256")
    md.update((nonce + postData).getBytes)
    val mac = Mac.getInstance("HmacSHA512")
    mac.init(new SecretKeySpec(Base64.decodeBase64(apiSecret), "HmacSHA512"))
    mac.update(path.getBytes)
    new String(Base64.encodeBase64(mac.doFinal(md.digest())))
  }

  private def getSignedRequest(path: String, uri: Uri) = {
    val nonce = nonceGenerator.apply
    val postData = "nonce=" + nonce
    val signature = getSignature(path, nonce, postData)
    val headers = List(RawHeader("API-Key", apiKey), RawHeader("API-Sign", signature))
    HttpRequest(HttpMethods.POST, uri, headers, FormData(Map("nonce" -> nonce)).toEntity)
  }

  def handleOrderRequest[A: JsonFormat](request: HttpRequest): Future[OrderResponse[A]] =
    fireRequest(request)
      .map(_.parseJson.convertTo[OrderResponse[A]])
      .recover { case t: Throwable => OrderResponse[A](List(t.getMessage), None) }

  private def apiResponse[U: JsonFormat, M <: MessageResponse[U]](path: String,
                                                                  params: Option[Map[String, String]],
                                                                  mkM: Either[List[String], Map[String, U]] => M,
                                                                  sign: Boolean = false): Future[M] = {

    def handleRequest[T: JsonFormat](request: HttpRequest): Future[Response[T]] =
      fireRequest(request)
        .map(_.parseJson.convertTo[Response[T]])
        .recover { case t: Throwable => Response[T](List(t.getMessage), None) }

    def extractResponse[T <: U](resp: Response[T]): M =
      if (resp.error.nonEmpty) mkM(Left(resp.error))
      else if (resp.result.isDefined) mkM(Right(resp.result.get))
      else mkM(Left(List("Something went wrong: response has no content.")))

    val uri = params match {
      case Some(value) => Uri(basePath + path).withQuery(Query(value))
      case None => Uri(basePath + path)
    }

    val request = if (sign) getSignedRequest(path, uri) else HttpRequest(uri = uri)

    handleRequest[U](request).map(extractResponse)
  }


  implicit def toOption(m: Map[String, String]): Option[Map[String, String]] = Some(m)

  override def receive = {

    case GetCurrentAssets =>
      val path = "/0/public/Assets"
      apiResponse[Asset, CurrentAssets](path, None, CurrentAssets).pipeTo(sender)

    case GetCurrentAssetPair(currency, respectToCurrency) =>
      val path = "/0/public/AssetPairs"
      val params = Map("pair" -> (currency + respectToCurrency))
      apiResponse[AssetPair, CurrentAssetPair](path, params, CurrentAssetPair).pipeTo(sender)

    case GetCurrentTicker(currency, respectToCurrency) =>
      val path = "/0/public/Ticker"
      val params = Map("pair" -> (currency + respectToCurrency))
      apiResponse[Ticker, CurrentTicker](path, params, CurrentTicker).pipeTo(sender)

    case GetCurrentAccountBalance =>
      val path = "/0/private/Balance"
      apiResponse[String, CurrentAccountBalance](path, None, CurrentAccountBalance, sign = true).pipeTo(sender)

    case GetCurrentTradeBalance(asset) =>
      val path = "/0/private/TradeBalance"
      val params = asset.flatMap(value => Map("asset" -> value))
      apiResponse[TradeBalance, CurrentTradeBalance](path, params, CurrentTradeBalance, sign = true).pipeTo(sender)

    case GetCurrentOpenOrders =>
      val path = "/0/private/OpenOrders"

      handleOrderRequest[OpenOrder](getSignedRequest(path, Uri(basePath + path))).map { resp =>
        if (resp.error.nonEmpty) CurrentOpenOrders(Left(resp.error))
        else if (resp.result.isDefined) CurrentOpenOrders(Right(resp.result.get.open.get))
        else CurrentOpenOrders(Left(List("Something went wrong: response has no content.")))
      }.pipeTo(sender)

    case GetCurrentClosedOrders =>
      val path = "/0/private/ClosedOrders"
      handleOrderRequest[ClosedOrder](getSignedRequest(path, Uri(basePath + path))).map { resp =>
        if (resp.error.nonEmpty) CurrentClosedOrders(Left(resp.error))
        else if (resp.result.isDefined) CurrentClosedOrders(Right(resp.result.get.closed.get))
        else CurrentClosedOrders(Left(List("Something went wrong: response has no content.")))
      }.pipeTo(sender)

  }


}

object KrakenApiActor {
  def apply(nonceGenerator: () => String) = Props(new KrakenApiActor(nonceGenerator))
}
