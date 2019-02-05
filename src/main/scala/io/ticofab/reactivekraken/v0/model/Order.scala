package io.ticofab.reactivekraken.v0.model

/**
  * Copyright 2017-2019 Fabio Tiriticco, Fabway
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

case class OrderDescription(pair: String,
                            buyOrSell: String,
                            orderType: String,
                            price: String,
                            price2: String,
                            leverage: String,
                            order: String)

case class Order(referralTransactionId: Option[String],
                 userReferenceId: Option[Int],
                 status: String,
                 timestamp: Double,
                 startTime: Double,
                 expireTime: Double,
                 description: OrderDescription,
                 volume: String,
                 volumeExecuted: String,
                 cost: String,
                 fee: String,
                 averagePrice: String,
                 misc: String,
                 stopPrice: Option[String],
                 limitPrice: Option[String],
                 orderFlags: String,
                 trades: Option[List[String]])

case class ClosedOrder(closed: Option[Map[String, Order]])

case class OpenOrder(open: Option[Map[String, Order]])


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
