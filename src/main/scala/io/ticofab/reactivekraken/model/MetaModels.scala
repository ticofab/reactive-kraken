package io.ticofab.reactivekraken.model

case class ServerTime(unixTime: Long, rfc1123: String)

case class AsksAndBids(asks: Seq[BookEntry], bids: Seq[BookEntry])

case class BookEntry(price: String, volume: String, timestamp: Long)

case class DataWithTime[T](data: Map[String, Seq[T]], timeStamp: Long)

case class OHLCRow(time: Long, open: String, high: String, low: String, close: String, vwap: String, volume: String, count: Int)

case class RecentTradeRow(price: String, volume: String, time: Double, buyOrSell: String, orderType: String, miscellaneous: String)

case class RecentSpreadRow(time: Long, bid: String, ask: String)
