package io.ticofab.reactivekraken.websocket.v01.model

object KrakenWsMessages {

  trait KrakenWsMessage {
    def event: String
  }

  case class Ping(reqid: Option[Int] = None) extends KrakenWsMessage {
    override def event = "ping"
  }

  case class SystemStatus(connectionID: BigInt, status: String, version: String) extends KrakenWsMessage {
    override def event = "systemStatus"
  }

  case class Pong(reqid: Option[Int]) extends KrakenWsMessage {
    override def event = "pong"
  }

  case class HeartBeat() extends KrakenWsMessage {
    override def event = "heartbeat"
  }

}
