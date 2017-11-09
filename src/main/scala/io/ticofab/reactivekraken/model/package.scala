package io.ticofab.reactivekraken

package object model {
  case class ServerTime(unixTime: Long, rfc1123: String)
  case class AsksAndBids(asks: Seq[BookEntry], bids: Seq[BookEntry])
  case class BookEntry(price: String, volume: String, timestamp: Long)
}
