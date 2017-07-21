package io.ticofab.reactivekraken.messages

import io.ticofab.reactivekraken.model.{Asset, AssetPair, Ticker, TradeBalance}

sealed trait Message

case object GetCurrentAssets extends Message

final case class GetCurrentAssetPair(currency: String, respectToCurrency: String) extends Message

final case class GetCurrentTicker(currency: String, respectToCurrency: String) extends Message

case object GetCurrentAccountBalance extends Message

final case class GetCurrentTradeBalance(asset: Option[String] = None) extends Message


abstract class MessageResponse[T](result: Either[List[String], Map[String, T]]) extends Message

case class CurrentAssets(result: Either[List[String], Map[String, Asset]]) extends MessageResponse[Asset](result)

case class CurrentAssetPair(result: Either[List[String], Map[String, AssetPair]]) extends MessageResponse[AssetPair](result)

case class CurrentTicker(result: Either[List[String], Map[String, Ticker]]) extends MessageResponse[Ticker](result)

case class CurrentAccountBalance(result: Either[List[String], Map[String, String]]) extends MessageResponse[String](result)

case class CurrentTradeBalance(result: Either[List[String], Map[String, TradeBalance]]) extends MessageResponse[TradeBalance](result)

