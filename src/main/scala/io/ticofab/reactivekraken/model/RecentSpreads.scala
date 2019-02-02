package io.ticofab.reactivekraken.model

case class RecentSpreadRow(time: Long, bid: String, ask: String)

case class RecentSpreads(spreadRows: List[RecentSpreadRow], last: Long)