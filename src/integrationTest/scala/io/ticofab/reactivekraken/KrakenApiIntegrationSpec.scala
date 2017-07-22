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
import io.ticofab.reactivekraken.messages._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

class KrakenApiIntegrationSpec extends TestKit(ActorSystem("KrakenApiIntegrationSpec"))
  with WordSpecLike with Matchers with BeforeAndAfterAll with JsonSupport {

  val timeout = 10.seconds

  def nonceGenerator = () => System.currentTimeMillis.toString

  val apiActor = system.actorOf(KrakenApiActor(nonceGenerator))
  val probe = TestProbe()

  "The KrakenAPIActor" should {

    "Return a correct Asset response" in {
      probe.send(apiActor, GetCurrentAssets)
      probe.expectMsgPF(timeout) {
        case ca: CurrentAssets => println(ca)
        case _ => fail("got wrong message back")
      }

    }

    "Return a correct AssetPair response" in {
      probe.send(apiActor, GetCurrentAssetPair("ETH", "EUR"))
      probe.expectMsgPF(timeout) {
        case cap: CurrentAssetPair => println(cap)
        case _ => fail("wrong response")
      }
    }

    "Return a correct Ticker response" in {
      probe.send(apiActor, GetCurrentTicker("ETH", "EUR"))
      probe.expectMsgPF(timeout) {
        case ct: CurrentTicker => println(ct)
        case _ => fail("wrong response")
      }
    }

    "Return the current account balance" in {
      probe.send(apiActor, GetCurrentAccountBalance)
      probe.expectMsgPF(timeout) {
        case cab: CurrentAccountBalance => println(cab)
        case _ => fail("wrong message")
      }
    }

    "Return the current trade balance" in {
      probe.send(apiActor, GetCurrentTradeBalance())
      probe.expectMsgPF(timeout) {
        case ctb: CurrentTradeBalance => println(ctb)
        case _ => fail("wrong message")
      }
    }

    "Return the current open orders" in {
      probe.send(apiActor, GetCurrentOpenOrders)
      probe.expectMsgPF(timeout) {
        case coo: CurrentOpenOrders => println(coo)
        case _ => fail("wrong message")
      }
    }

    "Return the current closed orders" in {
      probe.send(apiActor, GetCurrentClosedOrders)
      probe.expectMsgPF(timeout) {
        case cco: CurrentClosedOrders => println(cco)
        case _ => fail("wrong message")
      }
    }

  }
}
