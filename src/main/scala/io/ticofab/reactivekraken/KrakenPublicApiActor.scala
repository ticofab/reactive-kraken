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
import akka.http.scaladsl.model._
import akka.pattern.pipe
import akka.stream.ActorMaterializer
import io.ticofab.reactivekraken.api.RequestHelper
import io.ticofab.reactivekraken.model._

import scala.language.postfixOps

class KrakenPublicApiActor(val nonceGenerator: () => Long) extends Actor with RequestHelper {

  import KrakenPublicApiActor._

  protected implicit val actorSystem = context.system
  protected implicit val materializer = ActorMaterializer()
  protected implicit val executionContext = scala.concurrent.ExecutionContext.Implicits.global

  override def receive = {

    case GetServerTime =>
      val path = "/0/public/Time"
      val request = HttpRequest(uri = getUri(path))
      handleRequest[ServerTime](request)
        .map(extractMessage[ServerTime, CurrentServerTime, ServerTime](_, CurrentServerTime, _.result.get))
        .pipeTo(sender)

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

    case GetOHLC(currency, respectToCurrency, interval) =>
      val path = "/0/public/OHLC"
      val params = Map("pair" -> (currency + respectToCurrency), "interval" -> interval.toString)
      val request = HttpRequest(uri = getUri(path, Some(params)))
      handleRequest[OHLCData](request)
        .map(extractMessage[OHLCData, OHLCResponse, OHLCData](_, OHLCResponse, _.result.get))
        .pipeTo(sender)

    case GetOHLCSince(currency, respectToCurrency, timeStamp) =>
      val path = "/0/public/OHLC"
      val params = Map("pair" -> (currency + respectToCurrency), "since" -> timeStamp.toString)
      val request = HttpRequest(uri = getUri(path, Some(params)))
      handleRequest[OHLCData](request)
        .map(extractMessage[OHLCData, OHLCResponse, OHLCData](_, OHLCResponse, _.result.get))
        .pipeTo(sender)
  }

}

object KrakenPublicApiActor {
  def apply(nonceGenerator: () => Long) = Props(new KrakenPublicApiActor(nonceGenerator))

  case object GetServerTime extends Message
  case object GetCurrentAssets extends Message
  case class GetCurrentAssetPair(currency: String, respectToCurrency: String) extends Message
  case class GetCurrentTicker(currency: String, respectToCurrency: String) extends Message
  case class GetOHLC(currency: String, respectToCurrency: String, interval: Int = 1) extends Message
  case class GetOHLCSince(currency: String, respectToCurrency: String, timeStamp: Long) extends Message

  case class CurrentServerTime(result: Either[List[String], ServerTime]) extends MessageResponse
  case class CurrentAssets(result: Either[List[String], Map[String, Asset]]) extends MessageResponse
  case class CurrentAssetPair(result: Either[List[String], Map[String, AssetPair]]) extends MessageResponse
  case class CurrentTicker(result: Either[List[String], Map[String, Ticker]]) extends MessageResponse
  case class OHLCResponse(result: Either[List[String], OHLCData]) extends MessageResponse
}
