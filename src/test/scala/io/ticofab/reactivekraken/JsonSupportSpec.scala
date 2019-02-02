package io.ticofab.reactivekraken

import io.ticofab.reactivekraken.api.JsonSupport
import io.ticofab.reactivekraken.model.{OHLC, OrderBook, RecentTrades}
import org.scalatest.WordSpec

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
import spray.json._

class JsonSupportSpec extends WordSpec with JsonSupport {

  "The JsonSupport logic" should {
    "Parse OHLC correctly" in {

      val jsonStr =
        """
          | {
          |    "XETHZEUR": [
          |      [
          |        1549077540,
          |        "92.75",
          |        "92.75",
          |        "92.75",
          |        "92.75",
          |        "0.00",
          |        "0.00000000",
          |        0
          |      ],
          |      [
          |        1549077600,
          |        "92.75",
          |        "92.75",
          |        "92.75",
          |        "92.75",
          |        "0.00",
          |        "0.00000000",
          |        0
          |      ],
          |      [
          |        1549109340,
          |        "93.51",
          |        "93.51",
          |        "93.40",
          |        "93.40",
          |        "93.40",
          |        "96.93735225",
          |        2
          |      ],
          |      [
          |        1549109400,
          |        "93.40",
          |        "93.40",
          |        "93.39",
          |        "93.39",
          |        "93.39",
          |        "0.82500000",
          |        1
          |      ],
          |      [
          |        1549112640,
          |        "93.33",
          |        "93.33",
          |        "93.33",
          |        "93.33",
          |        "0.00",
          |        "0.00000000",
          |        0
          |      ],
          |      [
          |        1549120680,
          |        "93.38",
          |        "93.38",
          |        "93.36",
          |        "93.36",
          |        "93.36",
          |        "1.00080000",
          |        1
          |      ]
          |    ],
          |    "last": 1549120620
          | }
        """.stripMargin

      val parsed = jsonStr.parseJson
      parsed.prettyPrint

      val aaa = parsed.convertTo[OHLC]

      succeed

    }

    "Parse OrderBook correctly" in {
      val jsonStr =
        """
          |
          |{
          |    "XETHZEUR": {
          |      "asks": [
          |        [
          |          "93.01000",
          |          "49.210",
          |          1549132309
          |        ],
          |        [
          |          "89.90000",
          |          "1.100",
          |          1549114562
          |        ],
          |        [
          |          "89.88000",
          |          "0.557",
          |          1549114500
          |        ]
          |      ],
          |      "bids": [
          |        [
          |          "89.90000",
          |          "1.100",
          |          1549114562
          |        ],
          |        [
          |          "89.88000",
          |          "0.557",
          |          1549114500
          |        ]
          |      ]
          |    }
          | }
          |
          |
          |
        """.stripMargin

      val parsed = jsonStr.parseJson
      val book = parsed.convertTo[OrderBook]
      assert(book.asks.size == 3)
      assert(book.bids.size == 2)
      succeed
    }

    "Parse RecentTraes correctly" in {
      val jsonStr =
        """
          | {
          |    "XETHZEUR": [
          |      [
          |        "93.50000",
          |        "2.00000000",
          |        1549108576.2065,
          |        "b",
          |        "l",
          |        ""
          |      ],
          |      [
          |        "93.50000",
          |        "33.18559893",
          |        1549108576.3651,
          |        "s",
          |        "l",
          |        ""
          |      ],
          |      [
          |        "93.18000",
          |        "0.03530351",
          |        1549134324.3117,
          |        "s",
          |        "m",
          |        ""
          |      ],
          |      [
          |        "93.18000",
          |        "0.00009125",
          |        1549134324.314,
          |        "s",
          |        "m",
          |        ""
          |      ]
          |    ],
          |    "last": "1549134324313951917"
          |  }
        """.stripMargin

      val parsed = jsonStr.parseJson
      val trades = parsed.convertTo[RecentTrades]
      assert(trades.trades.size == 4)
    }
  }

}
