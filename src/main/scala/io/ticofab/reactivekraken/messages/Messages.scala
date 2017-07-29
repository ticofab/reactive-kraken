package io.ticofab.reactivekraken.messages

import io.ticofab.reactivekraken.model._

sealed trait Message

sealed trait MessageResponse

// ---- message that can be sent to the KrakenApiActor

case object GetCurrentAssets extends Message

case class GetCurrentAssetPair(currency: String, respectToCurrency: String) extends Message

case class GetCurrentTicker(currency: String, respectToCurrency: String) extends Message

case object GetCurrentAccountBalance extends Message

case class GetCurrentTradeBalance(asset: Option[String] = None) extends Message

case object GetCurrentOpenOrders extends Message

case object GetCurrentClosedOrders extends Message

// ---- responses from the KrakenApiActor

case class CurrentAssets(result: Either[List[String], Map[String, Asset]]) extends MessageResponse

case class CurrentAssetPair(result: Either[List[String], Map[String, AssetPair]]) extends MessageResponse

case class CurrentTicker(result: Either[List[String], Map[String, Ticker]]) extends MessageResponse

case class CurrentAccountBalance(result: Either[List[String], Map[String, String]]) extends MessageResponse

case class CurrentTradeBalance(result: Either[List[String], TradeBalance]) extends MessageResponse

abstract class OrderMessageResponse(result: Either[List[String], Map[String, Order]]) extends MessageResponse

case class CurrentOpenOrders(result: Either[List[String], Map[String, Order]]) extends MessageResponse

case class CurrentClosedOrders(result: Either[List[String], Map[String, Order]]) extends MessageResponse

case class KrakenApiActorError(error: String) extends MessageResponse
