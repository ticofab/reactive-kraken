package io.ticofab.reactivekraken.http

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpRequest
import akka.stream.ActorMaterializer
import io.ticofab.reactivekraken.api.RequestHelper
import io.ticofab.reactivekraken.model._

class KrakenPublicApi(actorSystem: ActorSystem = ActorSystem("reactive-kraken")) extends RequestHelper {

  implicit val as = actorSystem
  implicit val ec = as.dispatcher
  implicit val am = ActorMaterializer()

  def getServerTime = {
    val path = "/0/public/Time"
    val request = HttpRequest(uri = getUri(path))
    handleRequest[ServerTime](request)
      .map(extractMessage[ServerTime, CurrentServerTime, ServerTime](_, CurrentServerTime, _.result.get))
  }

  def getCurrentAssets = {
    val path = "/0/public/Assets"
    val request = HttpRequest(uri = getUri(path))
    handleRequest[Map[String, Asset]](request)
      .map(extractMessage[Map[String, Asset], CurrentAssets, Map[String, Asset]](_, CurrentAssets, _.result.get))
  }

  def getCurrentAssetPair(currency: String, respectToCurrency: String) = {
    val path = "/0/public/AssetPairs"
    val params = Map("pair" -> (currency + respectToCurrency))
    val request = HttpRequest(uri = getUri(path, Some(params)))
    handleRequest[Map[String, AssetPair]](request)
      .map(extractMessage[Map[String, AssetPair], CurrentAssetPair, Map[String, AssetPair]](_, CurrentAssetPair, _.result.get))
  }

  def getCurrentTicker(currency: String, respectToCurrency: String) = {
    val path = "/0/public/Ticker"
    val params = Map("pair" -> (currency + respectToCurrency))
    val request = HttpRequest(uri = getUri(path, Some(params)))
    handleRequest[Map[String, Ticker]](request)
      .map(extractMessage[Map[String, Ticker], CurrentTicker, Map[String, Ticker]](_, CurrentTicker, _.result.get))
  }

  def getOHLC(currency: String, respectToCurrency: String, interval: Option[Int] = None, timeStamp: Option[Long] = None) = {
    val path = "/0/public/OHLC"
    val params = Map("pair" -> (currency + respectToCurrency)) ++ timeStamp.fold[Map[String, String]](Map())(c => Map("since" -> c.toString)) ++ interval.fold[Map[String, String]](Map())(c => Map("interval" -> c.toString))
    val request = HttpRequest(uri = getUri(path, Some(params)))
    handleRequest[OHLC](request)
      .map(extractMessage[OHLC, OHLCResponse, OHLC](_, OHLCResponse, _.result.get))
  }

  def getOrderBook(currency: String, respectToCurrency: String, count: Option[Int] = None) = {
    val path = "/0/public/Depth"
    val params = Map("pair" -> (currency + respectToCurrency)) ++ count.fold[Map[String, String]](Map())(c => Map("count" -> c.toString))
    val request = HttpRequest(uri = getUri(path, Some(params)))
    handleRequest[OrderBook](request)
      .map(extractMessage[OrderBook, OrderBookResponse, OrderBook](_, OrderBookResponse, _.result.get))
  }

  def getRecentTrades(currency: String, respectToCurrency: String, timeStamp: Option[Long] = None) = {
    val path = "/0/public/Trades"
    val params = Map("pair" -> (currency + respectToCurrency)) ++ timeStamp.fold[Map[String, String]](Map())(c => Map("since" -> c.toString))
    val request = HttpRequest(uri = getUri(path, Some(params)))
    handleRequest[DataWithTime[RecentTradeRow]](request)
      .map(extractMessage[DataWithTime[RecentTradeRow], RecentTradesResponse, DataWithTime[RecentTradeRow]](_, RecentTradesResponse, _.result.get))
  }

  def getRecentSpread(currency: String, respectToCurrency: String, timeStamp: Option[Long] = None) = {
    val path = "/0/public/Spread"
    val params = Map("pair" -> (currency + respectToCurrency)) ++ timeStamp.fold[Map[String, String]](Map())(c => Map("since" -> c.toString))
    val request = HttpRequest(uri = getUri(path, Some(params)))
    handleRequest[DataWithTime[RecentSpreadRow]](request)
      .map(extractMessage[DataWithTime[RecentSpreadRow], RecentSpreadResponse, DataWithTime[RecentSpreadRow]](_, RecentSpreadResponse, _.result.get))
  }
}

case class CurrentServerTime(result: Either[List[String], ServerTime])

case class CurrentAssets(result: Either[List[String], Map[String, Asset]])

case class CurrentAssetPair(result: Either[List[String], Map[String, AssetPair]])

case class CurrentTicker(result: Either[List[String], Map[String, Ticker]])

case class OHLCResponse(result: Either[List[String], OHLC])

case class OrderBookResponse(result: Either[List[String], OrderBook])

case class RecentTradesResponse(result: Either[List[String], DataWithTime[RecentTradeRow]])

case class RecentSpreadResponse(result: Either[List[String], DataWithTime[RecentSpreadRow]])
