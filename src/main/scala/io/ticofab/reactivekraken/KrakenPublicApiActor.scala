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

    case GetOHLC(currency, respectToCurrency, interval, timeStamp) =>
      val path = "/0/public/OHLC"
      val params = Map("pair" -> (currency + respectToCurrency)) ++ timeStamp.fold[Map[String,String]](Map())(c => Map("since" -> c.toString))++ interval.fold[Map[String,String]](Map())(c => Map("interval" -> c.toString))
      val request = HttpRequest(uri = getUri(path, Some(params)))
      handleRequest[DataWithTime[OHLCRow]](request)
        .map(extractMessage[DataWithTime[OHLCRow], OHLCResponse, DataWithTime[OHLCRow]](_, OHLCResponse, _.result.get))
        .pipeTo(sender)

    case GetOrderBook(currency, respectToCurrency, count) =>
      val path = "/0/public/Depth"
      val params = Map("pair" -> (currency + respectToCurrency)) ++ count.fold[Map[String,String]](Map())(c => Map("count" -> c.toString))
      val request = HttpRequest(uri = getUri(path, Some(params)))
      handleRequest[Map[String, AsksAndBids]](request)
        .map(extractMessage[Map[String, AsksAndBids], OrderBookResponse, Map[String, AsksAndBids]](_, OrderBookResponse, _.result.get))
        .pipeTo(sender)

    case GetRecentTrades(currency, respectToCurrency, timeStamp) =>
      val path = "/0/public/Trades"
      val params = Map("pair" -> (currency + respectToCurrency)) ++ timeStamp.fold[Map[String,String]](Map())(c => Map("since" -> c.toString))
      val request = HttpRequest(uri = getUri(path, Some(params)))
      handleRequest[DataWithTime[RecentTradeRow]](request)
        .map(extractMessage[DataWithTime[RecentTradeRow], RecentTradesResponse, DataWithTime[RecentTradeRow]](_, RecentTradesResponse, _.result.get))
        .pipeTo(sender)

    case GetRecentSpread(currency, respectToCurrency, timeStamp) =>
      val path = "/0/public/Spread"
      val params = Map("pair" -> (currency + respectToCurrency)) ++ timeStamp.fold[Map[String,String]](Map())(c => Map("since" -> c.toString))
      val request = HttpRequest(uri = getUri(path, Some(params)))
      handleRequest[DataWithTime[RecentSpreadRow]](request)
        .map(extractMessage[DataWithTime[RecentSpreadRow], RecentSpreadResponse, DataWithTime[RecentSpreadRow]](_, RecentSpreadResponse, _.result.get))
        .pipeTo(sender)
  }

}

object KrakenPublicApiActor {
  def apply(nonceGenerator: () => Long) = Props(new KrakenPublicApiActor(nonceGenerator))

  case object GetServerTime extends Message
  case object GetCurrentAssets extends Message
  case class GetCurrentAssetPair(currency: String, respectToCurrency: String) extends Message
  case class GetCurrentTicker(currency: String, respectToCurrency: String) extends Message
  case class GetOHLC(currency: String, respectToCurrency: String, interval: Option[Int] = None, timeStamp: Option[Long] = None) extends Message
  case class GetOrderBook(currency: String, respectToCurrency: String, count: Option[Int] = None) extends Message
  case class GetRecentTrades(currency: String, respectToCurrency: String, timeStamp: Option[Long] = None) extends Message
  case class GetRecentSpread(currency: String, respectToCurrency: String, timeStamp: Option[Long] = None) extends Message

  case class CurrentServerTime(result: Either[List[String], ServerTime]) extends MessageResponse
  case class CurrentAssets(result: Either[List[String], Map[String, Asset]]) extends MessageResponse
  case class CurrentAssetPair(result: Either[List[String], Map[String, AssetPair]]) extends MessageResponse
  case class CurrentTicker(result: Either[List[String], Map[String, Ticker]]) extends MessageResponse
  case class OHLCResponse(result: Either[List[String], DataWithTime[OHLCRow]]) extends MessageResponse
  case class OrderBookResponse(result: Either[List[String], Map[String, AsksAndBids]]) extends MessageResponse
  case class RecentTradesResponse(result: Either[List[String], DataWithTime[RecentTradeRow]]) extends MessageResponse
  case class RecentSpreadResponse(result: Either[List[String], DataWithTime[RecentSpreadRow]]) extends MessageResponse
}
