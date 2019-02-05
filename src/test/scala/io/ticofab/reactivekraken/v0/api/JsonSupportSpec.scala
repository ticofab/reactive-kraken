package io.ticofab.reactivekraken.v0.api

import io.ticofab.reactivekraken.v0.model._
import org.scalatest.WordSpec

/**
  * Copyright 2017-2019 Fabio Tiriticco, Fabway
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

      val parsed = jsonStr.parseJson.convertTo[OHLC]
      assert(parsed.ohlcRows.size == 6)
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

    "Parse RecentSpreads correctly" in {
      val jsonStr =
        """
          |{
          |    "XETHZEUR": [
          |      [
          |        1549133893,
          |        "93.14000",
          |        "93.22000"
          |      ],
          |      [
          |        1549133895,
          |        "93.14000",
          |        "93.20000"
          |      ],
          |      [
          |        1549133899,
          |        "93.14000",
          |        "93.23000"
          |      ],
          |      [
          |        1549135335,
          |        "93.23000",
          |        "93.25000"
          |      ]
          |    ],
          |    "last": 1549135335
          |  }
          |
        """.stripMargin
      val spreads = jsonStr.parseJson.convertTo[RecentSpreads]
      assert(spreads.spreads.size == 4)
    }

    "Parse a CLosedOrder correctly" in {
      val jsonStr =
        """
          | {
          |    "closed": {
          |      "OE5A2O-GS6V6-3XMSBM": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "closed",
          |        "reason": null,
          |        "opentm": 1547234427.4382,
          |        "closetm": 1547247575.0205,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "sell",
          |          "ordertype": "stop-loss",
          |          "price": "108.11",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "sell 5.00000000 ETHEUR @ stop loss 108.11",
          |          "close": ""
          |        },
          |        "vol": "5.00000000",
          |        "vol_exec": "5.00000000",
          |        "cost": "539.20",
          |        "fee": "1.40",
          |        "price": "107.84",
          |        "stopprice": "108.09",
          |        "limitprice": "0.00000",
          |        "misc": "stopped",
          |        "oflags": "fciq"
          |      },
          |      "OOISRZ-ET3OT-7KSHMA": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "canceled",
          |        "reason": "User requested",
          |        "opentm": 1547220086.1378,
          |        "closetm": 1547234414.4717,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "sell",
          |          "ordertype": "stop-loss",
          |          "price": "109.11",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "sell 5.00000000 ETHEUR @ stop loss 109.11",
          |          "close": ""
          |        },
          |        "vol": "5.00000000",
          |        "vol_exec": "0.00000000",
          |        "cost": "0.00000",
          |        "fee": "0.00000",
          |        "price": "0.00000",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      },
          |      "OVMPQ5-CRFP6-O2M35C": {
          |        "refid": null,
          |        "userref": 0,
          |        "status": "canceled",
          |        "reason": "User requested",
          |        "opentm": 1516666621.3743,
          |        "closetm": 1516804465.4403,
          |        "starttm": 0,
          |        "expiretm": 0,
          |        "descr": {
          |          "pair": "ETHEUR",
          |          "type": "sell",
          |          "ordertype": "limit",
          |          "price": "869.33",
          |          "price2": "0",
          |          "leverage": "none",
          |          "order": "sell 0.20000000 ETHEUR @ limit 869.33",
          |          "close": ""
          |        },
          |        "vol": "0.20000000",
          |        "vol_exec": "0.00000000",
          |        "cost": "0.00000",
          |        "fee": "0.00000",
          |        "price": "0.00000",
          |        "stopprice": "0.00000",
          |        "limitprice": "0.00000",
          |        "misc": "",
          |        "oflags": "fciq"
          |      }
          |    },
          |    "count": 363
          |  }
        """.stripMargin

      val parsed = jsonStr.parseJson.convertTo[ClosedOrder]
      assert(parsed.closed.getOrElse(Map()).size == 3)
    }
  }

}
