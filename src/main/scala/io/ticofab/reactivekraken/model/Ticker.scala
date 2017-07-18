package io.ticofab.reactivekraken.model

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

case class Ticker(askArray: List[String],
                  bidArray: List[String],
                  lastTradeClosed: List[String],
                  volume: List[String],
                  volumeWeightedAveragePrice: List[String],
                  numberOfTrades: List[Int],
                  low: List[String],
                  high: List[String],
                  openingPrice: String)


/*

<pair_name> = pair name
    a = ask array(<price>, <whole lot volume>, <lot volume>),
    b = bid array(<price>, <whole lot volume>, <lot volume>),
    c = last trade closed array(<price>, <lot volume>),
    v = volume array(<today>, <last 24 hours>),
    p = volume weighted average price array(<today>, <last 24 hours>),
    t = number of trades array(<today>, <last 24 hours>),
    l = low array(<today>, <last 24 hours>),
    h = high array(<today>, <last 24 hours>),
    o = today's opening price

{
   "error":[

   ],
   "result":{
      "XETHZEUR":{
         "a":[
            "159.97894",
            "1",
            "1.000"
         ],
         "b":[
            "159.01361",
            "1",
            "1.000"
         ],
         "c":[
            "159.15600",
            "16.00000000"
         ],
         "v":[
            "350356.09690020",
            "365231.89997022"
         ],
         "p":[
            "150.57278",
            "149.96665"
         ],
         "t":[
            43266,
            46067
         ],
         "l":[
            "134.90013",
            "133.22718"
         ],
         "h":[
            "161.88367",
            "161.88367"
         ],
         "o":"136.03001"
      }
   }
}
 */
