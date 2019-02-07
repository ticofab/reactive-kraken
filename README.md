# Reactive Kraken

Scala library based on [Akka](http://akka.io) to help connect reactively to the [Kraken API](https://www.kraken.com/help/api).

The Kraken API and its available data is described here: https://www.kraken.com/help/api . Main features:

1. Signing functionality
2. Actor based usage
3. Stream based usage

#### Signing functionality only

If you only need the logic to evaluate the signature, you can simply use

```scala
val signature = Signer.getSignature(path, nonce, postData, apiSecret)
```
See the [Signer](https://github.com/ticofab/reactive-kraken/blob/master/src/main/scala/io/ticofab/reactivekraken/signature/Signer.scala) or an example usage below. 

#### REST API usage

There are two objects here: the `[HttpPublicApi](https://github.com/ticofab/reactive-kraken/blob/master/src/main/scala/io/ticofab/reactivekraken/http/v0/HttpPublicApi.scala)`
and the `[HttpPrivateApi](https://github.com/ticofab/reactive-kraken/blob/master/src/main/scala/io/ticofab/reactivekraken/http/v0/HttpPrivateApi.scala)`

Each methods does the HTTP request for you and returns a `Future[T]`, where `T` is the type of message returned by each endpoint.
All such types are in the [model](https://github.com/ticofab/reactive-kraken/tree/master/src/main/scala/io/ticofab/reactivekraken/http/v0/model) package. 

See below for example usages.

#### Websocket API usage

You can open a websocket connection using the following method in the `WesocketPublicApi` object, which is based on Akka Stream's `Source` and `Sink`.
 
```scala
def openConnection[Mat](source: Source[KrakenWsMessage, Mat],
                        sink: Sink[KrakenWsMessage, Future[Done]],
                        actorSystem: ActorSystem = ActorSystem("reactive-kraken"))

```

See example below for a full explanation.

## Full example

This is an example that you can copy/paste to test the library. 

```scala
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import io.ticofab.reactivekraken.http.v0.{KrakenPrivateApi, KrakenPublicApi}
import io.ticofab.reactivekraken.websocket.v01.WebsocketPublicApi
import io.ticofab.reactivekraken.websocket.v01.model.Subscription._
import io.ticofab.reactivekraken.websocket.v01.model._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

object TestApp extends App {

  // necessary implicits for Akka Http
  implicit val as = ActorSystem("wsTest")
  implicit val ec = as.dispatcher
  implicit val am = ActorMaterializer()

  // section about the HTTP API
  def awaitAndPrint[T](f: Future[T]): Unit = println(Await.result(f, 10.seconds))

  val publicApi = new KrakenPublicApi(as)
  awaitAndPrint(publicApi.getCurrentAssets)
  awaitAndPrint(publicApi.getServerTime)
  awaitAndPrint(publicApi.getCurrentAssetPair("ETH", "EUR"))
  awaitAndPrint(publicApi.getCurrentTicker("ETH", "EUR"))
  awaitAndPrint(publicApi.getOHLC("ETH", "EUR"))
  awaitAndPrint(publicApi.getOrderBook("ETH", "EUR"))
  awaitAndPrint(publicApi.getRecentTrades("ETH", "EUR"))
  awaitAndPrint(publicApi.getRecentSpread("ETH", "EUR"))

  // your Kraken credentials
  val apiKey     = "YOUR_API_KEY"
  val apiSecret  = "YOUR_API_SECRET"
  val privateApi = new KrakenPrivateApi(apiKey, apiSecret, () => System.currentTimeMillis(), as)
  awaitAndPrint(privateApi.getCurrentAccountBalance)
  awaitAndPrint(privateApi.getCurrentTradeBalance())
  awaitAndPrint(privateApi.getCurrentOpenOrders)
  awaitAndPrint(privateApi.getCurrentClosedOrders)

  // WEBSOCKET API

  // this block returns a source which will publish any message sent to the publisher actorRef.
  // every message emitted by this source will be sent up to the websocket API. 
  // when you want to change something in your subscription, send the appropriate message to the publisher actor.
  val (publisher, source) = {
    val (actor, publisher) = Source
      .actorRef[KrakenWsMessage](100, OverflowStrategy.dropBuffer)
      .toMat(Sink.asPublisher(fanout = false))(Keep.both)
      .run()
    val source = Source.fromPublisher(publisher)
    (actor, source)
  }

  // this sink will receive all messages coming from the websocket API.
  // in this example, we simply print messages out.
  val sink                = Sink.foreach[KrakenWsMessage](println)

  // this method returns futures for
  //   . connection establishment
  //   . connection closure
  val (futureConnected, futureClosed) = WebsocketPublicApi.openConnection(source, sink, as)

  // once the connection has been established, we subscribe to a variety of topics
  futureConnected.onComplete {
    case Success(_) =>
      println(s"websocket connected!")
      publisher ! Ping(Some(89))
      publisher ! Subscribe(List(CurrencyPair("ETH", "EUR")), Subscription(TopicOHLC))
      publisher ! Subscribe(List(CurrencyPair("ETH", "EUR")), Subscription(TopicSpread))
      publisher ! Subscribe(List(CurrencyPair("ETH", "EUR")), Subscription(TopicTrade))
      publisher ! Subscribe(List(CurrencyPair("ETH", "EUR")), Subscription(TopicBook))
      publisher ! Subscribe(List(CurrencyPair("ETH", "EUR")), Subscription(TopicTicker))

      // after 10 seconds, unsubscribe from all topics except book
      as.scheduler.scheduleOnce(10.seconds) {
        publisher ! Unsubscribe(List(CurrencyPair("ETH", "EUR")), Some(Subscription(TopicOHLC)))
        publisher ! Unsubscribe(List(CurrencyPair("ETH", "EUR")), Some(Subscription(TopicSpread)))
        publisher ! Unsubscribe(List(CurrencyPair("ETH", "EUR")), Some(Subscription(TopicTrade)))
        publisher ! Unsubscribe(List(CurrencyPair("ETH", "EUR")), Some(Subscription(TopicTicker)))

        // after 5 additional seconds, close connection client side
        as.scheduler.scheduleOnce(5.seconds, publisher, akka.actor.Status.Success)
      }

    // there was some error connecting
    case Failure(error) => println(s"error connecting: ${error.getMessage}")
  }

  // once the connection has been closed, we shut down the entire actor system.
  futureClosed.foreach { _ =>
    println("connection closed, shutting down.")
    as.terminate()
  }
}
```

## Import via SBT

Available for Scala 2.11 and 2.12. In your build.sbt file,

```sbt
resolvers += Resolver.jcenterRepo // you might not need this line
libraryDependencies += "io.ticofab" %% "reactive-kraken" % "1.0.0"
```

## Dependencies

* [Akka](http://akka.io)
* [Spray Json](https://github.com/spray/spray-json)
* [Apache Commons Codec](https://commons.apache.org/proper/commons-codec/)
* [ScalaTest](http://www.scalatest.org)

## Contributing

Contributions are most welcome. Please use the Issues section of this project and fire PRs away!

## License

    Copyright 2017-2019 Fabio Tiriticco - Fabway

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
