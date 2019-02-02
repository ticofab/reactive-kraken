package io.ticofab.reactivekraken.http

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpRequest
import akka.stream.ActorMaterializer
import io.ticofab.reactivekraken.api.RequestHelper
import io.ticofab.reactivekraken.model.{ClosedOrder, OpenOrder, Order, TradeBalance}

import scala.concurrent.Future

class KrakenPrivateApi(nonceGenerator: () => Long,
                       apiKey: String,
                       apiSecret: String,
                       actorSystem: ActorSystem = ActorSystem("reactive-kraken")) extends RequestHelper {

  implicit val as = actorSystem
  implicit val ec = as.dispatcher
  implicit val am = ActorMaterializer()

  def GetCurrentAccountBalance(): Future[CurrentAccountBalance] = {
    val path = "/0/private/Balance"
    val nonce = nonceGenerator.apply
    val getResponse = (request: HttpRequest) => handleRequest[Map[String, String]](request)
      .map(extractMessage[Map[String, String], CurrentAccountBalance, Map[String, String]](_, CurrentAccountBalance, _.result.get))
    val signedRequest = getSignedRequest(path, apiKey, apiSecret, nonce)
    getResponse(signedRequest)

  }

  def GetCurrentTradeBalance(asset: Option[String]): Future[CurrentTradeBalance] = {
    val path = "/0/private/TradeBalance"
    val nonce = nonceGenerator.apply
    val params = asset.flatMap(value => Some(Map("asset" -> value)))
    val getResponse = (request: HttpRequest) => handleRequest[TradeBalance](request)
      .map(extractMessage[TradeBalance, CurrentTradeBalance, TradeBalance](_, CurrentTradeBalance, _.result.get))
    val signedRequest = getSignedRequest(path, apiKey, apiSecret, nonce, params)
    getResponse(signedRequest)

  }

  def GetCurrentOpenOrders(): Future[CurrentOpenOrders] = {
    val path = "/0/private/OpenOrders"
    val nonce = nonceGenerator.apply
    val getResponse = (request: HttpRequest) => handleRequest[OpenOrder](request)
      .map(extractMessage[OpenOrder, CurrentOpenOrders, Map[String, Order]](_, CurrentOpenOrders, _.result.get.open.get))
    val signedRequest = getSignedRequest(path, apiKey, apiSecret, nonce)
    getResponse(signedRequest)

  }

  def GetCurrentClosedOrders(): Future[CurrentClosedOrders] = {
    val path = "/0/private/ClosedOrders"
    val nonce = nonceGenerator.apply
    val getResponse = (request: HttpRequest) => handleRequest[ClosedOrder](request)
      .map(extractMessage[ClosedOrder, CurrentClosedOrders, Map[String, Order]](_, CurrentClosedOrders, _.result.get.closed.get))
    val signedRequest = getSignedRequest(path, apiKey, apiSecret, nonce)
    getResponse(signedRequest)

  }

}

case class CurrentAccountBalance(result: Either[List[String], Map[String, String]])

case class CurrentTradeBalance(result: Either[List[String], TradeBalance])

case class CurrentOpenOrders(result: Either[List[String], Map[String, Order]])

case class CurrentClosedOrders(result: Either[List[String], Map[String, Order]])
