package io.ticofab.reactivekraken.websocket.v01.model

case class Unsubscribe(pair: List[CurrencyPair],
                       subscription: Option[Subscription],
                       reqId: Option[Int] = None,
                       channelID: Option[Int] = None) extends KrakenWsEvent {
  override def event = "unsubscribe"
}
