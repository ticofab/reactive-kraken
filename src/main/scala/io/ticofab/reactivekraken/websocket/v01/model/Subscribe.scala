package io.ticofab.reactivekraken.websocket.v01.model

case class Subscribe(pair: List[CurrencyPair],
                     subscription: Subscription,
                     reqId: Option[Int] = None) extends KrakenWsEvent {
  override def event = "subscribe"
}




