package io.ticofab.reactivekraken.v0.api

/**
  * Copyright 2017-2019 Fabio Tiriticco, Fabway
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

import io.ticofab.reactivekraken.v0.model._
import spray.json._

trait JsonSupport extends DefaultJsonProtocol {

  implicit val timeFormat         = jsonFormat(ServerTime, "unixtime", "rfc1123")
  implicit val assetFormat        = jsonFormat(Asset, "aclass", "altname", "decimals", "display_decimals")
  implicit val assetPairFormat    = jsonFormat(AssetPair, "altname", "aclass_base", "base", "aclass_quote", "quote", "lot",
    "pair_decimals", "lot_decimals", "lot_multiplier", "leverage_buy", "leverage_sell", "fees", "fees_maker",
    "fee_volume_currency", "margin_call", "margin_stop")
  implicit val tickerFormat       = jsonFormat(Ticker, "a", "b", "c", "v", "p", "t", "l", "h", "o")
  implicit val tradeBalanceFormat = jsonFormat(TradeBalance, "eb", "tb", "m", "n", "c", "v", "e", "mf", "ml")

  implicit val descriptionFormat = jsonFormat(OrderDescription, "pair", "type", "ordertype", "price", "price2", "leverage", "order")
  implicit val orderFormat       = jsonFormat(Order, "refid", "userref", "status", "opentm", "starttm", "expiretm", "descr",
    "vol", "vol_exec", "cost", "fee", "price", "misc", "stopprice", "limitprice", "oflags", "trades")
  implicit val openOrderFormat   = jsonFormat(OpenOrder, "open")
  implicit val closedOrderFormat = jsonFormat(ClosedOrder, "closed")

  implicit val ohlcRowFormat: RootJsonFormat[OHLCRow] = new RootJsonFormat[OHLCRow] {
    override def write(o: OHLCRow) = JsArray(o.time.toJson, o.open.toJson, o.high.toJson, o.low.toJson, o.close.toJson, o.vwap.toJson, o.volume.toJson, o.count.toJson)

    override def read(json: JsValue) = json match {
      case JsArray(Vector(a, b, c, d, e, f, g, h)) =>
        OHLCRow(a.convertTo[Long], b.convertTo[String], c.convertTo[String], d.convertTo[String], e.convertTo[String], f.convertTo[String], g.convertTo[String], h.convertTo[Int])
      case x => deserializationError("Expected JsArray, but got " + x)
    }
  }

  implicit val ohlcFormat: RootJsonFormat[OHLC] = new RootJsonFormat[OHLC] {
    override def write(obj: OHLC) = ??? // TODO

    override def read(json: JsValue) = {
      // NOTE: this is very brittle and strictly based on the Kraken API
      val fields = json.asJsObject.fields
      val rows = fields.filterKeys(_ != "last").toList.headOption.getOrElse(("a", JsArray(Vector())))._2.convertTo[List[OHLCRow]]
      val last = fields.getOrElse("last", JsNumber(0)).convertTo[Long]
      OHLC(rows, last)
    }
  }

  implicit val bookEntryFormat: RootJsonFormat[BookEntry] = new RootJsonFormat[BookEntry] {
    override def write(obj: BookEntry) = ???

    override def read(json: JsValue) = json match {
      case JsArray(Vector(a, b, c)) =>
        BookEntry(a.convertTo[String], b.convertTo[String], c.convertTo[Long])
      case x => deserializationError("Expected JsArray, but got " + x)
    }
  }

  implicit val orderBookFormat: RootJsonFormat[OrderBook] = new RootJsonFormat[OrderBook] {
    override def write(obj: OrderBook) = ???

    override def read(json: JsValue) = {
      // NOTE: this is very brittle and strictly based on the Kraken API
      val base = json.asJsObject.fields.toList.headOption.getOrElse(("a", JsArray(Vector())))._2.asJsObject.fields
      val asks = base.getOrElse("asks", JsArray(Vector())).convertTo[List[BookEntry]]
      val bids = base.getOrElse("bids", JsArray(Vector())).convertTo[List[BookEntry]]
      OrderBook(asks, bids)
    }
  }

  implicit val tradeFormat: RootJsonFormat[RecentTrade] = new RootJsonFormat[RecentTrade] {
    override def write(o: RecentTrade) = JsArray(o.price.toJson, o.volume.toJson, o.time.toJson, o.buyOrSell.toJson, o.orderType.toJson, o.miscellaneous.toJson)

    override def read(json: JsValue) = json match {
      case JsArray(Vector(a, b, c, d, e, f)) =>
        RecentTrade(a.convertTo[String], b.convertTo[String], c.convertTo[Double], d.convertTo[String], e.convertTo[String], f.convertTo[String])
      case x => deserializationError("Expected JsArray, but got " + x)
    }
  }

  implicit val recentTradesFormat: RootJsonFormat[RecentTrades] = new RootJsonFormat[RecentTrades] {
    override def write(obj: RecentTrades) = ??? // TODO

    override def read(json: JsValue) = {
      // NOTE: this is very brittle and strictly based on the Kraken API
      val fields = json.asJsObject.fields
      val rows = fields.filterKeys(_ != "last").toList.headOption.getOrElse(("a", JsArray(Vector())))._2.convertTo[List[RecentTrade]]
      val last = fields.getOrElse("last", JsString("0")).convertTo[String].toLong
      RecentTrades(rows, last)
    }
  }

  implicit val spreadFormat: RootJsonFormat[RecentSpread] = new RootJsonFormat[RecentSpread] {
    override def write(o: RecentSpread) = JsArray(o.time.toJson, o.bid.toJson, o.ask.toJson)

    override def read(json: JsValue) = json match {
      case JsArray(Vector(a, b, c)) =>
        RecentSpread(a.convertTo[Long], b.convertTo[String], c.convertTo[String])
      case x => deserializationError("Expected JsArray, but got " + x)
    }
  }

  implicit val recentSpreadsFormat: RootJsonFormat[RecentSpreads] = new RootJsonFormat[RecentSpreads] {
    override def write(obj: RecentSpreads) = ??? // TODO

    override def read(json: JsValue) = {
      // NOTE: this is very brittle and strictly based on the Kraken API
      val fields = json.asJsObject.fields
      val rows = fields.filterKeys(_ != "last").toList.headOption.getOrElse(("a", JsArray(Vector())))._2.convertTo[List[RecentSpread]]
      val last = fields.getOrElse("last", JsNumber(0)).convertTo[Long]
      RecentSpreads(rows, last)
    }
  }

  implicit def httpResponseTFormat[T: JsonFormat]: RootJsonFormat[Response[T]] = jsonFormat2(Response.apply[T])

}

case class Response[T](error: List[String], result: Option[T])

