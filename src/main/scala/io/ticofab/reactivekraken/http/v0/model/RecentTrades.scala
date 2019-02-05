package io.ticofab.reactivekraken.http.v0.model

case class RecentTrade(price: String, volume: String, time: Double, buyOrSell: String, orderType: String, miscellaneous: String)

case class RecentTrades(trades: List[RecentTrade], last: Long)
