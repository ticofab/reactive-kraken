# Reactive Kraken

Scala library based on [Akka](http://akka.io) to help connect to the [Kraken API](https://www.kraken.com/help/api) in a reactive way. Work in progress and contributions are very welcome.

## Import via SBT

Available for Scala 2.11 and 2.12. In your build.sbt file,

```sbt
resolvers += Resolver.jcenterRepo // you might not need this line

libraryDependencies += "io.ticofab" %% "reactive-kraken" % "0.4.0"
```

## Usage

The Kraken API and its available data is described here: https://www.kraken.com/help/api . Check the test package of this project for some examples. You can use this library in three ways.

1. Signing functionality only
2. Actor based usage
3. Stream based usage

#### Signing functionality only

If you only need the logic to evaluate the signature, you can simply use

```scala
val signature = Signer.getSignature(path, nonce, postData, apiSecret)
```
See how the [KrakenPrivateApiActor](https://github.com/ticofab/reactive-kraken/blob/master/src/main/scala/io/ticofab/reactivekraken/KrakenPrivateApiActor.scala) uses it.

#### Actor based usage

There are two actors you can use: the `KrakenPublicApiActor` talks to the public APIs, while the `KrakenPrivateApiActor` speaks the authenticated language of Kraken's private APIs, where sensible user data is exposed. The main difference between the two is that the private one needs credentials for authentication. As per specs, you need to pass both a nonce generator that emits always-increasing numeric values - see below.
 
Follows a table with the messages that these actors can process and the responses they will output, linked to the API endpoints as per listed here: https://www.kraken.com/help/api . Each response message contains an `Either`: a `Left` object in case of failure or `Right` with the API response parsed to a case class.  

| Actor | Message | Response | 
| ------| ------- | -------- |
| Public | `GetCurrentAssets` | `CurrentAssets` | 
| Public | `GetServerTime` | `CurrentServerTime` | 
| Public | `GetOHLC` | `OHLCResponse` | 
| Public | `GetOrderBook` | `OrderBookResponse` | 
| Public | `GetRecentTrades` | `RecentTradesResponse` | 
| Public | `GetRecentSpread` | `RecentSpreadResponse` | 
| Public | `GetCurrentAssetPair("ETH", "EUR")` | `CurrentAssetPair` |
| Public | `GetCurrentTicker("ETH", "EUR")` | `CurrentTicker` |
| Private | `GetCurrentAccountBalance` | `CurrentAccountBalance` |
| Private | `GetCurrentTradeBalance` | `CurrentTradeBalance` |
| Private | `GetCurrentOpenOrders` | `CurrentOpenOrders` |
| Private | `GetCurrentClosedOrders` | `CurrentClosedOrders` |

Example:
```scala
def nonceGenerator = () => System.currentTimeMillis

// public api actor
val publicApiActor = system.actorOf(KrakenPublicApiActor(nonceGenerator))
(publicApiActor ? GetCurrentAssets)(3.seconds).mapTo[CurrentAssets]

// private api actor
val privateApiActor = system.actorOf(KrakenPrivateApiActor(nonceGenerator, Some(myApiKey), Some(myApiSecret)))
(privateApiActor ? GetCurrentAccountBalance)(3.seconds).mapTo[CurrentAccountBalance]
```

#### Stream based usage

The stream approach uses `akka-stream` and it builds upon the other actors. These streams will check every 2 seconds for data, but I plan to make it customisable. 

You can obtain a number of streams via the `KrakenApiStream` object:
 
```scala
KrakenApiStream
  .tickerStream("ETH", "EUR")
  .runForeach(println)
```

## Dependencies

* [Akka](http://akka.io)
* [Spray Json](https://github.com/spray/spray-json)
* [Apache Commons Codec](https://commons.apache.org/proper/commons-codec/)
* [Mockito](http://site.mockito.org)
* [ScalaTest](http://www.scalatest.org)

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
