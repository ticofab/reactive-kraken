Reactive Kraken
===============

Scala library based on [Akka](http://akka.io) to help connect to the [Kraken API](https://www.kraken.com/help/api) in a reactive way. Work in progress and contributions are very welcome.

Import via SBT
--------------

Available for Scala 2.11 and 2.12. In your build.sbt file,

```sbt
resolvers += Resolver.jcenterRepo // you might not need this line

libraryDependencies += "io.ticofab" %% "reactive-kraken" % "0.2.0"
```

Usage
-----

The Kraken API and its available data is described here: https://www.kraken.com/help/api . Check the test package of this project for some examples.

If you want to access your private data, you need to provide your API key & API secret as environment variables, namely
 
```bash
KRAKEN_API_KEY=<your api key>
KRAKEN_API_SECRET=<your api secret>
```

You can use this library in three ways:

**Only the signing functionality**

If you only need the logic to evaluate the signature, you can simply use

```scala
val signature = Signer.getSignature(path, nonce, postData, apiSecret)
```
See how the [KrakenApiActor](https://github.com/ticofab/reactive-kraken/blob/master/src/main/scala/io/ticofab/reactivekraken/KrakenApiActor.scala) uses it.

**Actor based usage**

Instantiate a `KrakenApiActor` and talk to it. As per specs, you need to pass a nonce generator.
 
Follows a table with the messages it can receive and the responses it will output, linked to the API endpoints as per listed here: https://www.kraken.com/help/api . Each response message contains `Either` a `Left` with a failure or a `Right` with the API response parsed to a case class.  

| Message | Example | Response | 
| ------- | ------- | -------- |
| GetCurrentAssets | `GetCurrentAssets` | CurrentAssets | 
| GetCurrentAssetPair | `GetCurrentAssetPair("ETH", "EUR")` | CurrentAssetPair |
| GetCurrentTicker | `GetCurrentTicker("ETH", "EUR")` | CurrentTicker |
| GetCurrentAccountBalance  | `GetCurrentAccountBalance` | CurrentAccountBalance |
| GetCurrentTradeBalance  | `GetCurrentTradeBalance` | CurrentTradeBalance |
| GetCurrentOpenOrders  | `GetCurrentOpenOrders` | CurrentOpenOrders |
| GetCurrentClosedOrder  | `GetCurrentClosedOrders` | CurrentClosedOrders |

Example:
```scala
def nonceGenerator = () => System.currentTimeMillis
val apiActor = system.actorOf(KrakenApiActor(nonceGenerator))
(apiActor ? GetCurrentAccountBalance)(3.seconds).mapTo[CurrentAccountBalance]
```

**Stream based usage**

The stream approach uses `akka-stream` and it builds upon the `KrakenApiActor`. These streams will check every 2 seconds for data, but I plan to make it customisable. 

You can obtain a number of streams via the `KrakenApiStream` object:
 
```scala
KrakenApiStream
  .tickerStream("ETH", "EUR")
  .runForeach(println)
```

Dependencies
------------

* [Akka](http://akka.io)
* [Spray Json](https://github.com/spray/spray-json)
* [Apache Commons Codec](https://commons.apache.org/proper/commons-codec/)
* [Mockito](http://site.mockito.org)
* [ScalaTest](http://www.scalatest.org)

License
--------

    Copyright 2017 Fabio Tiriticco - Fabway

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
