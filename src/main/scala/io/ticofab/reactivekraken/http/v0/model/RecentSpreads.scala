package io.ticofab.reactivekraken.http.v0.model

case class RecentSpread(time: Long, bid: String, ask: String)

case class RecentSpreads(spreads: List[RecentSpread], last: Long)
