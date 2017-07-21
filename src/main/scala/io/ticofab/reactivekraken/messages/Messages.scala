package io.ticofab.reactivekraken.messages

import io.ticofab.reactivekraken.model.{Asset, AssetPair, Ticker}

sealed trait Message

case object GetCurrentAssets extends Message

final case class CurrentAssets(assets: Either[List[String], Map[String, Asset]]) extends Message

final case class GetCurrentAssetPair(currency: String, respectToCurrency: String) extends Message

final case class CurrentAssetPair(assetPair: Either[List[String], Map[String, AssetPair]]) extends Message

final case class GetCurrentTicker(currency: String, respectToCurrency: String) extends Message

final case class CurrentTicker(ticker: Either[List[String], Map[String, Ticker]]) extends Message

case object GetCurrentAccountBalance extends Message

final case class CurrentAccountBalance(assets: Either[List[String], Map[String, String]]) extends Message

final case class GetCurrentTradeBalance(asset: Option[String] = None) extends Message

final case class CurrentTradeBalance(tradeBalance: Either[List[String], Any]) extends Message
