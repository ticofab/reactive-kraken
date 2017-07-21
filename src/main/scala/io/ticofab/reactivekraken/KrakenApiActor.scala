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
import io.ticofab.reactivekraken.api.JsonSupport.responseFormat
import io.ticofab.reactivekraken.api.{HttpRequestor, JsonSupport, Response}
import io.ticofab.reactivekraken.messages._
import io.ticofab.reactivekraken.model.{Asset, AssetPair, Ticker, TradeBalance}
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

  private def typedResponse[U: JsonFormat, M <: MessageResponse[U]](path: String,
                                                                    params: Option[Map[String, String]],
                                                                    mkM: Either[List[String], Map[String, U]] => M,
                                                                    sign: Boolean = false): Future[M] = {

    def getSignature(path: String, nonce: String, postData: String) = {
      // Message signature using HMAC-SHA512 of (URI path + SHA256(nonce + POST data)) and base64 decoded secret API key
      val md = MessageDigest.getInstance("SHA-256")
      md.update((nonce + postData).getBytes)
      val mac = Mac.getInstance("HmacSHA512")
      mac.init(new SecretKeySpec(Base64.decodeBase64(apiSecret), "HmacSHA512"))
      mac.update(path.getBytes)
      new String(Base64.encodeBase64(mac.doFinal(md.digest())))
    }

    def getSignedRequest(path: String, uri: Uri) = {
      val nonce = nonceGenerator.apply
      val postData = "nonce=" + nonce
      val signature = getSignature(path, nonce, postData)
      val headers = List(RawHeader("API-Key", apiKey), RawHeader("API-Sign", signature))
      HttpRequest(HttpMethods.POST, uri, headers, FormData(Map("nonce" -> nonce)).toEntity)
    }

    def handle[T: JsonFormat](request: HttpRequest): Future[Response[T]] =
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

    handle[U](request).map(extractResponse)
  }

  implicit def toOption[T](t: T): Option[T] = Some(t)

  override def receive = {

    case GetCurrentAssets =>
      val path = "/0/public/Assets"
      typedResponse[Asset, CurrentAssets](path, None, CurrentAssets).pipeTo(sender)

    case GetCurrentAssetPair(currency, respectToCurrency) =>
      val path = "/0/public/AssetPairs"
      val params = Map("pair" -> (currency + respectToCurrency))
      typedResponse[AssetPair, CurrentAssetPair](path, params, CurrentAssetPair).pipeTo(sender)

    case GetCurrentTicker(currency, respectToCurrency) =>
      val path = "/0/public/Ticker"
      val params = Map("pair" -> (currency + respectToCurrency))
      typedResponse[Ticker, CurrentTicker](path, params, CurrentTicker).pipeTo(sender)

    case GetCurrentAccountBalance =>
      val path = "/0/private/Balance"
      typedResponse[String, CurrentAccountBalance](path, None, CurrentAccountBalance, sign = true).pipeTo(sender)

    case GetCurrentTradeBalance(asset) =>
      val path = "/0/private/TradeBalance"
      val params = Map("asset" -> "ZEUR")
      typedResponse[TradeBalance, CurrentTradeBalance](path, params, CurrentTradeBalance, sign = true).pipeTo(sender)

  }


}

object KrakenApiActor {
  def apply(nonceGenerator: () => String) = Props(new KrakenApiActor(nonceGenerator))
}
