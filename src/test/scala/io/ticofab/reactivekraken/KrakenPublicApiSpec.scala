package io.ticofab.reactivekraken

import io.ticofab.reactivekraken.api.JsonSupport
import io.ticofab.reactivekraken.model.OHLC
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

class KrakenPublicApiSpec extends WordSpec with JsonSupport {
  
  "test" should {
    "parse Json" in {

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
  }

}
