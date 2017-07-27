package io.ticofab.reactivekraken.messages

import io.ticofab.reactivekraken.model._

sealed trait Message

case object GetCurrentAssets extends Message

case class GetCurrentAssetPair(currency: String, respectToCurrency: String) extends Message

case class GetCurrentTicker(currency: String, respectToCurrency: String) extends Message

case object GetCurrentAccountBalance extends Message

case class GetCurrentTradeBalance(asset: Option[String] = None) extends Message

case object GetCurrentOpenOrders

case object GetCurrentClosedOrders

case class CurrentAssets(result: Either[List[String], Map[String, Asset]])

case class CurrentAssetPair(result: Either[List[String], Map[String, AssetPair]])

case class CurrentTicker(result: Either[List[String], Map[String, Ticker]])

case class CurrentAccountBalance(result: Either[List[String], Map[String, String]])

case class CurrentTradeBalance(result: Either[List[String], TradeBalance])

abstract class OrderMessageResponse(result: Either[List[String], Map[String, Order]])

case class CurrentOpenOrders(result: Either[List[String], Map[String, Order]])

case class CurrentClosedOrders(result: Either[List[String], Map[String, Order]])
