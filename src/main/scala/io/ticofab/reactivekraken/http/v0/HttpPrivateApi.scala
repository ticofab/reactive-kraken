package io.ticofab.reactivekraken.http.v0

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import io.ticofab.reactivekraken.http.v0.api.RequestHelper
import io.ticofab.reactivekraken.http.v0.model.{ClosedOrder, OpenOrder, Order, TradeBalance}

import scala.concurrent.Future

/**
  * Gateway for the private Kraken APIs.
  *
  * @param apiKey         Your unique API Key
  * @param apiSecret      Your unique API Secret
  * @param nonceGenerator A generator of sequential nonces (see Kraken API documentation)
  * @param actorSystem    The Actor System. Note that if you don't provide any, you will need to manually shutdown the one that is created by this class
  */
class KrakenPrivateApi(apiKey: String,
                       apiSecret: String,
                       nonceGenerator: () => Long,
                       actorSystem: ActorSystem = ActorSystem("reactive-kraken")) extends RequestHelper {

  implicit val as = actorSystem
  implicit val ec = as.dispatcher
  implicit val am = ActorMaterializer()

  /**
    * Shuts the actor system down.
    *
    * @return A Future[Terminated]
    */
  def shutdown = actorSystem.terminate()

  def getCurrentAccountBalance: Future[CurrentAccountBalance] = {
    val path = "/0/private/Balance"
    val nonce = nonceGenerator.apply
    val signedRequest = getSignedRequest(path, apiKey, apiSecret, nonce)
    handleRequest[Map[String, String]](signedRequest)
      .map(extractMessage[Map[String, String], CurrentAccountBalance, Map[String, String]](_, CurrentAccountBalance, _.result.get))
  }

  def getCurrentTradeBalance(asset: Option[String] = None): Future[CurrentTradeBalance] = {
    val path = "/0/private/TradeBalance"
    val nonce = nonceGenerator.apply
    val params = asset.fold(Map[String, String]())(asset => Map("asset" -> asset))
    val signedRequest = getSignedRequest(path, apiKey, apiSecret, nonce, params)
    handleRequest[TradeBalance](signedRequest)
      .map(extractMessage[TradeBalance, CurrentTradeBalance, TradeBalance](_, CurrentTradeBalance, _.result.get))
  }

  def getCurrentOpenOrders: Future[CurrentOpenOrders] = {
    val path = "/0/private/OpenOrders"
    val nonce = nonceGenerator.apply
    val signedRequest = getSignedRequest(path, apiKey, apiSecret, nonce)
    handleRequest[OpenOrder](signedRequest)
      .map(extractMessage[OpenOrder, CurrentOpenOrders, Map[String, Order]](_, CurrentOpenOrders, _.result.get.open.get))
  }

  def getCurrentClosedOrders: Future[CurrentClosedOrders] = {
    val path = "/0/private/ClosedOrders"
    val nonce = nonceGenerator.apply
    val signedRequest = getSignedRequest(path, apiKey, apiSecret, nonce)
    handleRequest[ClosedOrder](signedRequest)
      .map(extractMessage[ClosedOrder, CurrentClosedOrders, Map[String, Order]](_, CurrentClosedOrders, _.result.get.closed.get))
  }

}

case class CurrentAccountBalance(result: Either[List[String], Map[String, String]])

case class CurrentTradeBalance(result: Either[List[String], TradeBalance])

case class CurrentOpenOrders(result: Either[List[String], Map[String, Order]])

case class CurrentClosedOrders(result: Either[List[String], Map[String, Order]])
