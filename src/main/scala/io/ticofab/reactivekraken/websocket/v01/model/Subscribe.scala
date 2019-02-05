package io.ticofab.reactivekraken.websocket.v01.model

import io.ticofab.reactivekraken.websocket.v01.model.KrakenWsMessages.KrakenWsMessage
import io.ticofab.reactivekraken.websocket.v01.model.Subscription.{Depth, Interval, SubscriptionTopic}

object Subscription {

  sealed trait SubscriptionTopic

  case object Ticker extends SubscriptionTopic

  case object OHLC extends SubscriptionTopic

  case object Trade extends SubscriptionTopic

  case object Book extends SubscriptionTopic

  case object Spread extends SubscriptionTopic

  case object AllTopics extends SubscriptionTopic

  sealed trait Interval

  case object OneMinute extends Interval

  case object FiveMinutes extends Interval

  case object FifteenMinutes extends Interval

  case object ThirtyMinutes extends Interval

  case object SixtyMinutes extends Interval

  case object TwoHunderdFortyMinutes extends Interval

  case object OneThousandFourHandredFortyMinutes extends Interval

  case object TenThousandEightyMinutes extends Interval

  case object TwentyOneThousandSixHundredMinutes extends Interval

  sealed trait Depth

  case object Ten extends Depth

  case object TwentyFive extends Depth

  case object OneHundred extends Depth

  case object FiveHundred extends Depth

  case object OneThousand extends Depth

}

case class Subscription(name: SubscriptionTopic,
                        interval: Option[Interval] = None,
                        depth: Option[Depth] = None)

case class Subscribe(pair: List[CurrencyPair], subscription: Subscription, reqId: Option[Int] = None) extends KrakenWsMessage {
  override def event = "subscribe"
}

sealed trait SubscriptionEvent
case object Subscribed extends SubscriptionEvent
case object Unsubscribed extends SubscriptionEvent
case object Error extends SubscriptionEvent

case class SubscriptionStatus(channelID: Int, status: SubscriptionEvent, pair: CurrencyPair, subscription: Subscription) extends KrakenWsMessage {
  override def event = "subscriptionStatus"
}


