package io.ticofab.reactivekraken.model

case class RecentTradeRow(price: String, volume: String, time: Double, buyOrSell: String, orderType: String, miscellaneous: String)

case class RecentTrades(tradesRows: List[RecentTradeRow], last: Long)
