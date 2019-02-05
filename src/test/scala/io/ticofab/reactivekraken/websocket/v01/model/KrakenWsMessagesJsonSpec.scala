package io.ticofab.reactivekraken.websocket.v01.model

import io.ticofab.reactivekraken.websocket.v01.model.KrakenWsMessages.{HeartBeat, SystemStatus}
import org.scalatest.WordSpec
import spray.json._

class KrakenWsMessagesJsonSpec extends WordSpec with KrakenWsMessages.KrakenWsMessageJson {

  "A KrakenWsMessage" should {

    "Convert SystemStatus messages properly" in {
      val jsonStr =
        """
          |{"connectionID":17865194737501073182,"event":"systemStatus","status":"online","version":"0.1.1"}
        """.stripMargin

      val systemStatus = jsonStr.parseJson.convertTo[SystemStatus]
      assert(systemStatus.connectionID == BigInt("17865194737501073182"))
    }

    "Convert correctly a HearBeat event" in {
      val jsonStr =
        """
          |{"event":"heartbeat"}
        """.stripMargin
      val hb = jsonStr.parseJson.convertTo[HeartBeat]
      assert(hb.event == "heartbeat")
    }

  }
}
