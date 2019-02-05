package io.ticofab.reactivekraken.http.v0.model

case class BookEntry(price: String, volume: String, timestamp: Long)

case class OrderBook(asks: Seq[BookEntry], bids: Seq[BookEntry])
