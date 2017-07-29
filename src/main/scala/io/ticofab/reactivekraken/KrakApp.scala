package io.ticofab.reactivekraken

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.stream.ActorMaterializer
import io.ticofab.reactivekraken.messages._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Properties

/**
  * Created by Fabio Tiriticco on 20/07/2017.
  */
object KrakApp extends App {
  implicit val as = ActorSystem()
  implicit val am = ActorMaterializer()

  private val apiKey = Properties.envOrNone("KRAKEN_API_KEY")
  private val apiSecret = Properties.envOrNone("KRAKEN_API_SECRET")

  val a = as.actorOf(KrakenApiActor(nonceGenerator = () => System.currentTimeMillis, apiKey, apiSecret))

  val timeout = 5.seconds
  (a ? GetCurrentAssets) (timeout).mapTo[CurrentAssets].foreach(println)
  Thread.sleep(2000)
  println("------")
  (a ? GetCurrentAssetPair("ETH", "EUR")) (timeout).mapTo[CurrentAssetPair].foreach(println)
  Thread.sleep(2000)
  println("------")
  (a ? GetCurrentTicker("ETH", "EUR")) (timeout).mapTo[CurrentTicker].foreach(println)
  Thread.sleep(2000)
  println("------")
  //  (a ? GetCurrentAccountBalance)(3.seconds).mapTo[CurrentAccountBalance].foreach(println)
  //  (a ? GetCurrentTradeBalance(None))(3.seconds).mapTo[CurrentTradeBalance].foreach(println)
  (a ? GetCurrentOpenOrders) (timeout).mapTo[CurrentOpenOrders].foreach(println)
  Thread.sleep(2000)
  println("------")
  (a ? GetCurrentClosedOrders) (timeout).mapTo[CurrentClosedOrders].foreach(println)
  println("------")
}
