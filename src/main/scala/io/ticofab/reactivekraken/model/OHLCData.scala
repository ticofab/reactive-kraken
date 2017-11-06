package io.ticofab.reactivekraken.model

case class OHLCData(data: Map[String, Seq[OHLCRow]], timeStamp: Long)

case class OHLCRow(time: Long, open: String, high: String, low: String, close: String, vwap: String, volume: String, count: Int)
