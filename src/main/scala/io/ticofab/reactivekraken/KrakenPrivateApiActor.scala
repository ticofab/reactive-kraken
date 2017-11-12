package io.ticofab.reactivekraken


import akka.actor.{Actor, Props}
import akka.http.scaladsl.model._
import akka.pattern.pipe
import akka.stream.ActorMaterializer
import io.ticofab.reactivekraken.api.RequestHelper
import io.ticofab.reactivekraken.model._

import scala.language.postfixOps

class KrakenPrivateApiActor(val nonceGenerator: () => Long,
                            apiKey: String,
                            apiSecret: String) extends Actor with RequestHelper {

  import KrakenPrivateApiActor._

  protected implicit val actorSystem = context.system
  protected implicit val materializer = ActorMaterializer()
  protected implicit val executionContext = scala.concurrent.ExecutionContext.Implicits.global

  val credentials = (apiKey, apiSecret)

  override def receive = {

    case GetCurrentAccountBalance =>
      val path = "/0/private/Balance"
      val f = (request: HttpRequest) => handleRequest[Map[String, String]](request)
        .map(extractMessage[Map[String, String], CurrentAccountBalance, Map[String, String]](_, CurrentAccountBalance, _.result.get))
      getAuthenticatedAPIResponseMessage(credentials, path, f).pipeTo(sender)

    case GetCurrentTradeBalance(asset) =>
      val path = "/0/private/TradeBalance"
      val params = asset.flatMap(value => Some(Map("asset" -> value)))
      val f = (request: HttpRequest) => handleRequest[TradeBalance](request)
        .map(extractMessage[TradeBalance, CurrentTradeBalance, TradeBalance](_, CurrentTradeBalance, _.result.get))
      getAuthenticatedAPIResponseMessage(credentials, path, f, params).pipeTo(sender)

    case GetCurrentOpenOrders =>
      val path = "/0/private/OpenOrders"
      val f = (request: HttpRequest) => handleRequest[OpenOrder](request)
        .map(extractMessage[OpenOrder, CurrentOpenOrders, Map[String, Order]](_, CurrentOpenOrders, _.result.get.open.get))
      getAuthenticatedAPIResponseMessage(credentials, path, f).pipeTo(sender)

    case GetCurrentClosedOrders =>
      val path = "/0/private/ClosedOrders"
      val f = (request: HttpRequest) => handleRequest[ClosedOrder](request)
        .map(extractMessage[ClosedOrder, CurrentClosedOrders, Map[String, Order]](_, CurrentClosedOrders, _.result.get.closed.get))
      getAuthenticatedAPIResponseMessage(credentials, path, f).pipeTo(sender)
  }

}

object KrakenPrivateApiActor {
  def apply(nonceGenerator: () => Long,
            apikey: String,
            apiSecret: String) = Props(new KrakenPrivateApiActor(nonceGenerator, apikey, apiSecret))


  case object GetCurrentAccountBalance extends Message
  case class GetCurrentTradeBalance(asset: Option[String] = None) extends Message
  case object GetCurrentOpenOrders extends Message
  case object GetCurrentClosedOrders extends Message

  case class CurrentAccountBalance(result: Either[List[String], Map[String, String]]) extends MessageResponse
  case class CurrentTradeBalance(result: Either[List[String], TradeBalance]) extends MessageResponse
  abstract class OrderMessageResponse(result: Either[List[String], Map[String, Order]]) extends MessageResponse
  case class CurrentOpenOrders(result: Either[List[String], Map[String, Order]]) extends MessageResponse
  case class CurrentClosedOrders(result: Either[List[String], Map[String, Order]]) extends MessageResponse
}
