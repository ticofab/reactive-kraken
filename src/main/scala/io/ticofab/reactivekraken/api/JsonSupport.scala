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

  implicit val timeFormat = jsonFormat(ServerTime, "unixtime", "rfc1123")
  implicit val assetFormat = jsonFormat(Asset, "aclass", "altname", "decimals", "display_decimals")
  implicit val assetPairFormat = jsonFormat(AssetPair, "altname", "aclass_base", "base", "aclass_quote", "quote", "lot",
    "pair_decimals", "lot_decimals", "lot_multiplier", "leverage_buy", "leverage_sell", "fees", "fees_maker",
    "fee_volume_currency", "margin_call", "margin_stop")
  implicit val tickerFormat = jsonFormat(Ticker, "a", "b", "c", "v", "p", "t", "l", "h", "o")
  implicit val tradeBalanceFormat = jsonFormat(TradeBalance, "eb", "tb", "m", "n", "c", "v", "e", "mf", "ml")
  implicit val orderTypeFormat = new EnumJsonConverter(OrderType)
  implicit val buyOrSellFormat = new EnumJsonConverter(BuyOrSell)
  implicit val orderStatusFormat = new EnumJsonConverter(OrderStatus)
  implicit val descriptionFormat = jsonFormat(OrderDescription, "pair", "type", "ordertype", "price", "price2", "leverage", "order")
  implicit val orderFormat = jsonFormat(Order, "refid", "userref", "status", "opentm", "starttm", "expiretm", "descr",
    "vol", "vol_exec", "cost", "fee", "price", "misc", "stopprice", "limitprice", "oflags", "trades")
  implicit val openOrderFormat = jsonFormat(OpenOrder, "open")
  implicit val closedOrderFormat = jsonFormat(ClosedOrder, "closed")

  implicit def tuple8Format[A :JsonFormat, B :JsonFormat, C :JsonFormat, D :JsonFormat, E :JsonFormat, F: JsonFormat, G: JsonFormat, H:JsonFormat] = {
    new RootJsonFormat[(A, B, C, D, E, F, G, H)] {
      def write(t: (A, B, C, D, E, F, G, H)) = JsArray(t._1.toJson, t._2.toJson, t._3.toJson, t._4.toJson, t._5.toJson, t._6.toJson, t._7.toJson, t._8.toJson)
      def read(value: JsValue) = value match {
        case JsArray(Seq(a, b, c, d, e, f, g, h)) =>
          (a.convertTo[A], b.convertTo[B], c.convertTo[C], d.convertTo[D], e.convertTo[E], f.convertTo[F], g.convertTo[G], h.convertTo[H])
        case x => deserializationError("Expected Tuple8 as JsArray, but got " + x)
      }
    }
  }

  implicit val bookEntriesFormat = new JsonFormat[BookEntry] {
    type BookEntryTuple = Tuple3[String, String, Long]

    override def read(js: JsValue) = {
      BookEntry.tupled(js.convertTo[BookEntryTuple])
    }

    override def write(obj: BookEntry) = BookEntry.unapply(obj).get.toJson
  }

  implicit val asksAndBidsFormat = jsonFormat2(AsksAndBids)

  implicit val ohlcRowFormat = new JsonFormat[OHLCRow] {
    type OHLCRowTuple = Tuple8[Long, String, String, String, String, String, String, Int]

    override def read(js: JsValue) = {
      OHLCRow.tupled(js.convertTo[OHLCRowTuple])
    }

    override def write(obj: OHLCRow) = OHLCRow.unapply(obj).get.toJson
  }

  implicit val recentTradeRowFormat = new JsonFormat[RecentTradeRow] {
    type RecentTradeRowTuple = Tuple6[String, String, Double, String, String, String]

    override def read(js: JsValue) = {
      RecentTradeRow.tupled(js.convertTo[RecentTradeRowTuple])
    }

    override def write(obj: RecentTradeRow) = RecentTradeRow.unapply(obj).get.toJson
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
      val id = fields("last").toString().replace("\"","").toLong
      val data = fields.filterKeys(_ != "last").mapValues(v => v.convertTo[Seq[T]])
      DataWithTime(data,id)
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

