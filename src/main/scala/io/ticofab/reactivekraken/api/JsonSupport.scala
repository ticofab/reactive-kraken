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

import io.ticofab.reactivekraken.model.{OrderType, _}
import spray.json._

class EnumJsonConverter[T <: scala.Enumeration](enu: T) extends RootJsonFormat[T#Value] {
  override def write(obj: T#Value): JsValue = JsString(obj.toString)

  override def read(json: JsValue): T#Value = {
    json match {
      case JsString(txt) => enu.withName(txt)
      case somethingElse => throw DeserializationException(s"Expected a value from enum $enu instead of $somethingElse")
    }
  }
}

trait JsonSupport extends DefaultJsonProtocol {
  implicit def pimpedJsonObject(jsObj: JsObject) = new PimpedJsonObject(jsObj)

  implicit val timeFormat         = jsonFormat(ServerTime, "unixtime", "rfc1123")
  implicit val assetFormat        = jsonFormat(Asset, "aclass", "altname", "decimals", "display_decimals")
  implicit val assetPairFormat    = jsonFormat(AssetPair, "altname", "aclass_base", "base", "aclass_quote", "quote", "lot",
    "pair_decimals", "lot_decimals", "lot_multiplier", "leverage_buy", "leverage_sell", "fees", "fees_maker",
    "fee_volume_currency", "margin_call", "margin_stop")
  implicit val tickerFormat       = jsonFormat(Ticker, "a", "b", "c", "v", "p", "t", "l", "h", "o")
  implicit val tradeBalanceFormat = jsonFormat(TradeBalance, "eb", "tb", "m", "n", "c", "v", "e", "mf", "ml")

  implicit val ohlcRowFormat: RootJsonFormat[OHLCRow] = new RootJsonFormat[OHLCRow] {
    // (time: Long, open: String, high: String, low: String, close: String, vwap: String, volume: String, count: Int)
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
    // (time: Long, open: String, high: String, low: String, close: String, vwap: String, volume: String, count: Int)
    override def write(o: RecentTrade) = JsArray(o.price.toJson, o.volume.toJson, o.time.toJson, o.buyOrSell.toJson, o.orderType.toJson, o.miscellaneous.toJson)

    override def read(json: JsValue) = json match {
      case JsArray(Vector(a, b, c, d, e, f)) =>
        // case class RecentTrade(price: String, volume: String, time: Double, buyOrSell: String, orderType: String, miscellaneous: String)
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


  implicit val orderTypeFormat   = new EnumJsonConverter(OrderType)
  implicit val buyOrSellFormat   = new EnumJsonConverter(BuyOrSell)
  implicit val orderStatusFormat = new EnumJsonConverter(OrderStatus)
  implicit val descriptionFormat = jsonFormat(OrderDescription, "pair", "type", "ordertype", "price", "price2", "leverage", "order")
  implicit val orderFormat       = jsonFormat(Order, "refid", "userref", "status", "opentm", "starttm", "expiretm", "descr",
    "vol", "vol_exec", "cost", "fee", "price", "misc", "stopprice", "limitprice", "oflags", "trades")
  implicit val openOrderFormat   = jsonFormat(OpenOrder, "open")
  implicit val closedOrderFormat = jsonFormat(ClosedOrder, "closed")

  implicit def tuple8Format[A: JsonFormat, B: JsonFormat, C: JsonFormat, D: JsonFormat, E: JsonFormat, F: JsonFormat, G: JsonFormat, H: JsonFormat] = {
    new RootJsonFormat[(A, B, C, D, E, F, G, H)] {
      def write(t: (A, B, C, D, E, F, G, H)) = JsArray(t._1.toJson, t._2.toJson, t._3.toJson, t._4.toJson, t._5.toJson, t._6.toJson, t._7.toJson, t._8.toJson)

      def read(value: JsValue) = value match {
        case JsArray(Seq(a, b, c, d, e, f, g, h)) =>
          (a.convertTo[A], b.convertTo[B], c.convertTo[C], d.convertTo[D], e.convertTo[E], f.convertTo[F], g.convertTo[G], h.convertTo[H])
        case x => deserializationError("Expected Tuple8 as JsArray, but got " + x)
      }
    }
  }

  implicit val recentSpreadRowFormat = new JsonFormat[RecentSpreadRow] {
    type RecentSpreadRowTuple = Tuple3[Long, String, String]

    override def read(js: JsValue) = {
      RecentSpreadRow.tupled(js.convertTo[RecentSpreadRowTuple])
    }

    override def write(obj: RecentSpreadRow) = RecentSpreadRow.unapply(obj).get.toJson
  }

  implicit def dataWithTimeReader[T](implicit tFormat: JsonFormat[T]) = new JsonFormat[DataWithTime[T]] {
    override def read(js: JsValue) = {
      val fields = js.asJsObject.fields
      val id = fields("last").toString().replace("\"", "").toLong
      val data = fields.filterKeys(_ != "last").mapValues(v => v.convertTo[Seq[T]])
      DataWithTime(data, id)
    }

    override def write(obj: DataWithTime[T]) = obj.data.toJson.asJsObject ++ ("last" -> obj.timeStamp).toJson.asJsObject
  }


  implicit def httpResponseTFormat[T: JsonFormat]: RootJsonFormat[Response[T]] = jsonFormat2(Response.apply[T])

  class PimpedJsonObject(jsObj: JsObject) {
    def mergeWith(other: JsObject): JsObject = {
      new JsObject(jsObj.fields ++ other.fields)
    }

    def ++(other: JsObject): JsObject = jsObj mergeWith other
  }

}

case class Response[T](error: List[String], result: Option[T])

