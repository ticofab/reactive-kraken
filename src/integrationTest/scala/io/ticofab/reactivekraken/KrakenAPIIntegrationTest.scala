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
import io.ticofab.reactivekraken.api.JsonSupport
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

class KrakenAPIIntegrationTest extends TestKit(ActorSystem("KrakenApiIntegrationTest"))
  with WordSpecLike with Matchers with BeforeAndAfterAll with JsonSupport {

  "The KrakenAPIActor" should {

    "Return a correct Asset response" in {

      val apiActor = system.actorOf(KrakenApiActor())
      val probe = TestProbe()
      probe.send(apiActor, GetCurrentAssets)

      probe.expectMsgPF(5.second) {
        case ca: CurrentAssets => println(ca)
        case _ => fail("got wrong message back")
      }

    }

    "Return a correct AssetPair response" in {
      val apiActor = system.actorOf(KrakenApiActor())
      val probe = TestProbe()
      probe.send(apiActor, GetCurrentAssetPair("ETH", "EUR"))
      probe.expectMsgPF(5.seconds) {
        case cap: CurrentAssetPair => println(cap)
        case _ => fail("wrong response")
      }
    }

    "Return a correct Ticker response" in {
      val apiActor = system.actorOf(KrakenApiActor())
      val probe = TestProbe()
      probe.send(apiActor, GetCurrentTicker("ETH", "EUR"))
      probe.expectMsgPF(5.seconds) {
        case ct: CurrentTicker => println(ct)
        case _ => fail("wrong response")
      }
    }
  }
}
