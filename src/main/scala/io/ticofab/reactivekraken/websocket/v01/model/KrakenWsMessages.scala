package io.ticofab.reactivekraken.websocket.v01.model

import spray.json.{DefaultJsonProtocol, JsObject, JsString, JsValue, RootJsonFormat}

object KrakenWsMessages extends DefaultJsonProtocol {

  sealed trait KrakenWsMessage {
    def event: String
  }

  case class Ping(reqId: Option[Int] = None) extends KrakenWsMessage {
    override def event = "ping"
  }

  case class SystemStatus(connectionID: BigInt, status: String, version: String) extends KrakenWsMessage {
    override def event = "systemStatus"
  }

  case class Pong(reqId: Option[Int]) extends KrakenWsMessage {
    override def event = "pong"
  }

  case class HeartBeat() extends KrakenWsMessage {
    override def event = "heartbeat"
  }

  trait KrakenWsMessageJson {
    implicit val pingFormat         = jsonFormat1(Ping)
    implicit val pongFormat         = jsonFormat1(Pong)
    implicit val systemStatusFormat = jsonFormat3(SystemStatus)
    implicit val heartbeatFormat    = jsonFormat0(HeartBeat)

    // format that discriminates based on an additional
    // field "type" that can either be "Cat" or "Dog"
    implicit val krakenWsMessageFormat = new RootJsonFormat[KrakenWsMessage] {
      def write(obj: KrakenWsMessage): JsValue =
        JsObject((obj match {
          case c: Ping => c.toJson
          case p: Pong => p.toJson
          case s: SystemStatus => s.toJson
          case h: HeartBeat => h.toJson
        }).asJsObject.fields + ("event" -> JsString(obj.event)))

      def read(json: JsValue): KrakenWsMessage =
        json.asJsObject.getFields("event") match {
          case Seq(JsString("ping")) => json.convertTo[Ping]
          case Seq(JsString("pong")) => json.convertTo[Pong]
          case Seq(JsString("systemStatus")) => json.convertTo[SystemStatus]
          case Seq(JsString("heartbeat")) => json.convertTo[HeartBeat]
          case _ => Ping()
        }
    }
  }

}
