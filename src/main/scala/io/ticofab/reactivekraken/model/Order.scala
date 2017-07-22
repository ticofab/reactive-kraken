package io.ticofab.reactivekraken.model

/**
  * Copyright 2017 Fabio Tiriticco, Fabway
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

import io.ticofab.reactivekraken.model.BuyOrSell.BuyOrSell
import io.ticofab.reactivekraken.model.OrderStatus.OrderStatus
import io.ticofab.reactivekraken.model.OrderType.OrderType

object OrderStatus extends Enumeration {
  type OrderStatus = Value
  val pending, open, closed, canceled, expired = Value
}

object BuyOrSell extends Enumeration {
  type BuyOrSell = Value
  val buy, sell = Value
}

object OrderType extends Enumeration {
  type OrderType = Value
  val market, limit, stop_loss, take_profit, stop_loss_profit, stop_loss_profit_limit, stop_loss_limit,
  take_profit_limit, trailing_stop, trailing_stop_limit, stop_loss_and_limit, settle_position = Value
}

case class OrderDescription(pair: String,
                            buyOrSell: BuyOrSell,
                            orderType: OrderType,
                            price: Double,
                            price2: Double,
                            leverage: String,
                            order: String                           )

case class Order(referralTransactionId: String,
                 userReferenceId: String,
                 status: OrderStatus,
                 timestamp: Double,
                 startTime: Double,
                 expireTime: Double,
                 description: OrderDescription,
                 volume: Double,
                 volumenExecuted: Double,
                 cost: Double,
                 fee: Double,
                 averagePrice: Double,
                 misc: List[String],
                 stopPrice: Option[Double],
                 limitPrice: Option[Double],
                 orderFlags: List[String],
                 trades: Option[List[String]])

/*

  refid = Referral order transaction id that created this order
  userref = user reference id
  status = status of order:
      pending = order pending book entry
      open = open order
      closed = closed order
      canceled = order canceled
      expired = order expired
  opentm = unix timestamp of when order was placed
  starttm = unix timestamp of order start time (or 0 if not set)
  expiretm = unix timestamp of order end time (or 0 if not set)
  descr = order description info
      pair = asset pair
      type = type of order (buy/sell)
      ordertype = order type (See Add standard order)
      price = primary price
      price2 = secondary price
      leverage = amount of leverage
      order = order description
      close = conditional close order description (if conditional close set)
  vol = volume of order (base currency unless viqc set in oflags)
  vol_exec = volume executed (base currency unless viqc set in oflags)
  cost = total cost (quote currency unless unless viqc set in oflags)
  fee = total fee (quote currency)
  price = average price (quote currency unless viqc set in oflags)
  stopprice = stop price (quote currency, for trailing stops)
  limitprice = triggered limit price (quote currency, when limit based order type triggered)
  misc = comma delimited list of miscellaneous info
      stopped = triggered by stop price
      touched = triggered by touch price
      liquidated = liquidation
      partial = partial fill
  oflags = comma delimited list of order flags
      viqc = volume in quote currency
      fcib = prefer fee in base currency (default if selling)
      fciq = prefer fee in quote currency (default if buying)
      nompp = no market price protection
  trades = array of trade ids related to order (if trades info requested and data available)

 */
