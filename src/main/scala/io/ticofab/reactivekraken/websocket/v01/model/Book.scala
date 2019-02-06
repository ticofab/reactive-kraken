package io.ticofab.reactivekraken.websocket.v01.model

import io.ticofab.reactivekraken.websocket.v01.model.Book.{BookEntry, BookMessageType}

object Book {

  sealed trait BookMessageType

  case object Snapshot extends BookMessageType

  case object Update extends BookMessageType

  case class BookEntry(price: Double, volume: Double, timestamp: Long)

}

case class Book(channelId: Int,
                bookMessageType: BookMessageType,
                asks: List[BookEntry],
                bids: List[BookEntry]) extends KrakenWsMessage
