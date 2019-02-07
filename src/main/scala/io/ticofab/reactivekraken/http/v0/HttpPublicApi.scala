package io.ticofab.reactivekraken.http.v0

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpRequest
import akka.stream.ActorMaterializer
import io.ticofab.reactivekraken.http.v0.api.RequestHelper
import io.ticofab.reactivekraken.http.v0.model._

/**
  * Gateway for the private Kraken APIs.
  *
  * @param actorSystem The Actor System. Note that if you don't provide any, you will need to manually shutdown the one that is created by this class
  */
class KrakenPublicApi(actorSystem: ActorSystem = ActorSystem("reactive-kraken")) extends RequestHelper {

  implicit val as = actorSystem
  implicit val ec = as.dispatcher
  implicit val am = ActorMaterializer()

  /**
    * Shuts the actor system down.
    *
    * @return A Future[Terminated]
    */
  def shutdown = actorSystem.terminate()

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
    val request = HttpRequest(uri = getUri(path, params))
    handleRequest[Map[String, AssetPair]](request)
      .map(extractMessage[Map[String, AssetPair], CurrentAssetPair, Map[String, AssetPair]](_, CurrentAssetPair, _.result.get))
  }

  def getCurrentTicker(currency: String, respectToCurrency: String) = {
    val path = "/0/public/Ticker"
    val params = Map("pair" -> (currency + respectToCurrency))
    val request = HttpRequest(uri = getUri(path, params))
    handleRequest[Map[String, Ticker]](request)
      .map(extractMessage[Map[String, Ticker], CurrentTicker, Map[String, Ticker]](_, CurrentTicker, _.result.get))
  }

  def getOHLC(currency: String, respectToCurrency: String, interval: Option[Int] = None, timeStamp: Option[Long] = None) = {
    val path = "/0/public/OHLC"
    val params = Map("pair" -> (currency + respectToCurrency)) ++ timeStamp.fold[Map[String, String]](Map())(c => Map("since" -> c.toString)) ++ interval.fold[Map[String, String]](Map())(c => Map("interval" -> c.toString))
    val request = HttpRequest(uri = getUri(path, params))
    handleRequest[OHLC](request)
      .map(extractMessage[OHLC, OHLCResponse, OHLC](_, OHLCResponse, _.result.get))
  }

  def getOrderBook(currency: String, respectToCurrency: String, count: Option[Int] = None) = {
    val path = "/0/public/Depth"
    val params = Map("pair" -> (currency + respectToCurrency)) ++ count.fold[Map[String, String]](Map())(c => Map("count" -> c.toString))
    val request = HttpRequest(uri = getUri(path, params))
    handleRequest[OrderBook](request)
      .map(extractMessage[OrderBook, OrderBookResponse, OrderBook](_, OrderBookResponse, _.result.get))
  }

  def getRecentTrades(currency: String, respectToCurrency: String, timeStamp: Option[Long] = None) = {
    val path = "/0/public/Trades"
    val params = Map("pair" -> (currency + respectToCurrency)) ++ timeStamp.fold[Map[String, String]](Map())(c => Map("since" -> c.toString))
    val request = HttpRequest(uri = getUri(path, params))
    handleRequest[RecentTrades](request)
      .map(extractMessage[RecentTrades, RecentTradesResponse, RecentTrades](_, RecentTradesResponse, _.result.get))
  }

  def getRecentSpread(currency: String, respectToCurrency: String, timeStamp: Option[Long] = None) = {
    val path = "/0/public/Spread"
    val params = Map("pair" -> (currency + respectToCurrency)) ++ timeStamp.fold[Map[String, String]](Map())(c => Map("since" -> c.toString))
    val request = HttpRequest(uri = getUri(path, params))
    handleRequest[RecentSpreads](request)
      .map(extractMessage[RecentSpreads, RecentSpreadResponse, RecentSpreads](_, RecentSpreadResponse, _.result.get))
  }
}

case class CurrentServerTime(result: Either[List[String], ServerTime])

case class CurrentAssets(result: Either[List[String], Map[String, Asset]])

case class CurrentAssetPair(result: Either[List[String], Map[String, AssetPair]])

case class CurrentTicker(result: Either[List[String], Map[String, Ticker]])

case class OHLCResponse(result: Either[List[String], OHLC])

case class OrderBookResponse(result: Either[List[String], OrderBook])

case class RecentTradesResponse(result: Either[List[String], RecentTrades])

case class RecentSpreadResponse(result: Either[List[String], RecentSpreads])
