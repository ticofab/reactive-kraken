package io.ticofab.reactivekraken

/**
  * Copyright 2017 Fabio Tiriticco, Fabway
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */


import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import io.ticofab.reactivekraken.model.{AssetResponse, JsonSupport}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

class KrakenAPIIntegrationTest extends TestKit(ActorSystem("KrakenApiIntegrationTest"))
  with WordSpecLike with Matchers with BeforeAndAfterAll with JsonSupport {

  "The KrakenAPIActor" should {

    "Return a correct asset response" in {

      val apiActor = system.actorOf(KrakenAPIActor())
      val probe = TestProbe()
      probe.send(apiActor, GetAssets)

      probe.expectMsgPF(5.second) {
        case a@AssetResponse(error, currencies) =>
          println(a)
          assert(error.isEmpty && currencies.nonEmpty || error.nonEmpty && currencies.isEmpty)
          if (currencies.nonEmpty) {
            // assuming that Bitcoin will always be there :)
            assert(currencies.contains("XXBT"))
            assert(currencies("XXBT").aClass == "currency")
            assert(currencies("XXBT").altName == "XBT")
          }

        case _ => fail("got wrong message back")
      }

    }

  }
}
