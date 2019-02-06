package io.ticofab.reactivekraken.websocket.v01.model

case class OHLC(channelId: Int,
                time: Long,
                endtime: Long,
                open: Double,
                high: Double,
                low: Double,
                close: Double,
                vwap: Double,
                volume: Double,
                count: Int) extends KrakenWsMessage
