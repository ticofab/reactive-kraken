package io.ticofab.reactivekraken.model

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

case class TradeBalance(equivalentBalance: String,
                        tradeBalance: String,
                        marginAmount: String,
                        unrealizedNetProfit: String,
                        costBasis: String,
                        floatingValuation: String,
                        equity: String,
                        freeMargin: String,
                        marginLevel: Option[String])

/*
eb = equivalent balance (combined balance of all currencies)
tb = trade balance (combined balance of all equity currencies)
m = margin amount of open positions
n = unrealized net profit/loss of open positions
c = cost basis of open positions
v = current floating valuation of open positions
e = equity = trade balance + unrealized net profit/loss
mf = free margin = equity - initial margin (maximum margin available to open new positions)
ml = margin level = (equity / initial margin) * 100
 */
