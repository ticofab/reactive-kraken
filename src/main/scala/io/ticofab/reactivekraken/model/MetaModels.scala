package io.ticofab.reactivekraken.model

case class DataWithTime[T](data: Map[String, Seq[T]], timeStamp: Long)


