package io.ticofab.reactivekraken.model

case class BookEntry(price: String, volume: String, timestamp: Long)

case class AsksAndBids(asks: Seq[BookEntry], bids: Seq[BookEntry])
