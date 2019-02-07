package io.ticofab.reactivekraken.websocket.v01.model

trait KrakenWsEvent extends KrakenWsMessage {
  def event: String
}

