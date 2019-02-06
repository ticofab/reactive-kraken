package io.ticofab.reactivekraken.websocket.v01.model

import io.ticofab.reactivekraken.websocket.v01.model.Trade.{TriggeringOrderSide, TriggeringOrderType}

object Trade {

  sealed trait TriggeringOrderSide

  case object Buy extends TriggeringOrderSide

  case object Sell extends TriggeringOrderSide

  sealed trait TriggeringOrderType

  case object Market extends TriggeringOrderType

  case object Limit extends TriggeringOrderType

}

case class Trade(price: Double, volume: Double, time: Long, side: TriggeringOrderSide, orderType: TriggeringOrderType, misc: String)

case class Trades(channelId: Int, trades: List[Trade]) extends KrakenWsMessage
