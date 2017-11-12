package io.ticofab.reactivekraken

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import io.ticofab.reactivekraken.KrakenPrivateApiActor._
import io.ticofab.reactivekraken.api.JsonSupport
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.util.Properties

class KrakenPrivateApiItSpec extends TestKit(ActorSystem("KrakenApiIntegrationSpec"))
  with WordSpecLike with Matchers with BeforeAndAfterAll with JsonSupport {

  val timeout = 10.seconds

  def nonceGenerator = () => System.currentTimeMillis
  val apiKey = Properties.envOrNone("KRAKEN_API_KEY")
  val apiSecret = Properties.envOrNone("KRAKEN_API_SECRET")
  val apiActor = system.actorOf(KrakenPrivateApiActor(nonceGenerator, apiKey.get, apiSecret.get))

  "The KrakenAPIActor" should {

    "Return the current account balance" in {
      val probe = TestProbe()
      probe.send(apiActor, GetCurrentAccountBalance)
      probe.expectMsgPF(timeout) {
        case cab: CurrentAccountBalance => println(cab)
        case a: MessageResponse => fail("wrong message: " + a)
      }
    }

    "Return the current trade balance" in {
      val probe = TestProbe()
      probe.send(apiActor, GetCurrentTradeBalance())
      probe.expectMsgPF(timeout) {
        case ctb: CurrentTradeBalance => println(ctb)
        case a: MessageResponse => fail("wrong message: " + a)
      }
    }

    "Return the current open orders" in {
      val probe = TestProbe()
      probe.send(apiActor, GetCurrentOpenOrders)
      probe.expectMsgPF(timeout) {
        case coo: CurrentOpenOrders => println(coo)
        case a: MessageResponse => fail("wrong message: " + a)
      }
    }

    "Return the current closed orders" in {
      val probe = TestProbe()
      probe.send(apiActor, GetCurrentClosedOrders)
      probe.expectMsgPF(timeout) {
        case cco: CurrentClosedOrders => println(cco)
        case a: MessageResponse => fail("wrong message: " + a)
      }
    }

  }

}
