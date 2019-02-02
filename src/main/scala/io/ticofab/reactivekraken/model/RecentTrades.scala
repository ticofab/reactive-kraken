package io.ticofab.reactivekraken.model

import io.ticofab.reactivekraken.model.BuyOrSell.BuyOrSell

case class RecentTrade(price: String, volume: String, time: Double, buyOrSell: BuyOrSell, orderType: String, miscellaneous: String)

case class RecentTrades(trades: List[RecentTrade], last: Long)
