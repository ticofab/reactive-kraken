package io.ticofab.reactivekraken.websocket.v01.model

case class Spread(channelId: Int, bid: Double, ask: Double, timestamp: Long) extends KrakenWsMessage
