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
import io.ticofab.reactivekraken.KrakenPublicApiActor._
import io.ticofab.reactivekraken.api.JsonSupport
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.util.Properties

class KrakenPublicApiItSpec extends TestKit(ActorSystem("KrakenApiIntegrationSpec"))
  with WordSpecLike with Matchers with BeforeAndAfterAll with JsonSupport {

  val timeout = 10.seconds

  def nonceGenerator = () => System.currentTimeMillis
  val apiActor = system.actorOf(KrakenPublicApiActor(nonceGenerator))

  "The KrakenAPIActor" should {

    "Return a correct ServerTime response" in {
      val probe = TestProbe()
      probe.send(apiActor, GetServerTime)
      probe.expectMsgPF(timeout) {
        case ca: CurrentServerTime => println(ca)
        case a: MessageResponse => fail("wrong message: " + a)
      }

    }

    "Return a correct Asset response" in {
      val probe = TestProbe()
      probe.send(apiActor, GetCurrentAssets)
      probe.expectMsgPF(timeout) {
        case ca: CurrentAssets => println(ca)
        case a: MessageResponse => fail("wrong message: " + a)
      }

    }

    "Return a correct AssetPair response" in {
      val probe = TestProbe()
      probe.send(apiActor, GetCurrentAssetPair("ETH", "EUR"))
      probe.expectMsgPF(timeout) {
        case cap: CurrentAssetPair => println(cap)
        case a: MessageResponse => fail("wrong message: " + a)
      }
    }

    "Return a correct Ticker response" in {
      val probe = TestProbe()
      probe.send(apiActor, GetCurrentTicker("ETH", "EUR"))
      probe.expectMsgPF(timeout) {
        case ct: CurrentTicker => println(ct)
        case a: MessageResponse => fail("wrong message: " + a)
      }
    }

    "Return a correct OHCL response with interval" in {
      val probe = TestProbe()
      probe.send(apiActor, GetOHLC("ETH", "EUR"))
      probe.expectMsgPF(timeout) {
        case ct: OHLCResponse => println(ct)
        case a: MessageResponse => fail("wrong message: " + a)
      }
    }

    "Return a correct OHCL response with since" in {
      val probe = TestProbe()
      probe.send(apiActor, GetOHLCSince("ETH", "EUR", System.currentTimeMillis()/1000L - 60L))
      probe.expectMsgPF(timeout) {
        case ct: OHLCResponse => println(ct)
        case a: MessageResponse => fail("wrong message: " + a)
      }
    }

  }
}
