package io.ticofab.reactivekraken.model

/**
  * Created by Fabio Tiriticco on 15/07/2017.
  */
case class Asset(aClass: String,
                 altName: String,
                 decimals: Int,
                 displayDecimals: Int)

case class AssetResponse(error: List[String], currencies: Map[String, Asset])
