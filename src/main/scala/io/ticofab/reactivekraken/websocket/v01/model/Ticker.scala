package io.ticofab.reactivekraken.websocket.v01.model

case class PriceAndVolume(price: Double, lotVolume: Double, wholeLotVolume: Option[Double] = None)

case class Value(today: Double, last24Hours: Double)

case class Ticker(channelId: Int,
                  ask: PriceAndVolume,
                  bid: PriceAndVolume,
                  close: PriceAndVolume,
                  volume: Value,
                  volumeWeightedAveragePrice: Value,
                  numberOfTrades: Value,
                  lowPrice: Value,
                  highPrice: Value,
                  openPrice: Value) extends KrakenWsMessage
