package io.ticofab.reactivekraken.websocket.v01.model

case class Ping(reqid: Option[Int] = None) extends KrakenWsEvent {
  override def event = "ping"
}

case class SystemStatus(connectionID: BigInt, status: String, version: String) extends KrakenWsEvent {
  override def event = "systemStatus"
}

case class Pong(reqid: Option[Int]) extends KrakenWsEvent {
  override def event = "pong"
}

case class HeartBeat() extends KrakenWsEvent {
  override def event = "heartbeat"
}
