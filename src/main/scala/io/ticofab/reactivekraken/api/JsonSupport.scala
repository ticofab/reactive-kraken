package io.ticofab.reactivekraken.api

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

import io.ticofab.reactivekraken.model.{Asset, AssetPair, Ticker, TradeBalance}
import spray.json.{DefaultJsonProtocol, JsonFormat}

case class Response[T](error: List[String], result: Option[Map[String, T]])

trait JsonSupport extends DefaultJsonProtocol {
  implicit val assetFormat = jsonFormat(Asset, "aclass", "altname", "decimals", "display_decimals")
  implicit val assetPairFormat = jsonFormat(AssetPair, "altname", "aclass_base", "base", "aclass_quote", "quote", "lot",
    "pair_decimals", "lot_decimals", "lot_multiplier", "leverage_buy", "leverage_sell", "fees", "fees_maker",
    "fee_volume_currency", "margin_call", "margin_stop")
  implicit val tickerFormat = jsonFormat(Ticker, "a", "b", "c", "v", "p", "t", "l", "h", "o")
  implicit val tradeBalanceFormat = jsonFormat(TradeBalance, "eb", "tb", "m", "n", "c", "v", "e", "mf", "ml")
}

object JsonSupport extends DefaultJsonProtocol {
  implicit def responseFormat[T: JsonFormat] = jsonFormat2(Response.apply[T])
}

