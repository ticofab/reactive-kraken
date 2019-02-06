package io.ticofab.reactivekraken.websocket.v01.model

import io.ticofab.reactivekraken.websocket.v01.model.SubscriptionStatus.SubscriptionEvent

case class SubscriptionStatus(channelID: Int,
                              status: SubscriptionEvent,
                              pair: CurrencyPair,
                              subscription: Subscription) extends KrakenWsEvent {
  override def event = "subscriptionStatus"
}

case object SubscriptionStatus {

  sealed trait SubscriptionEvent

  case object Subscribed extends SubscriptionEvent

  case object Unsubscribed extends SubscriptionEvent

  case object Error extends SubscriptionEvent

}
