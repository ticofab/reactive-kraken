
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

name := """reactive-kraken"""

version := "0.4.0"

scalaVersion := "2.12.8"

organization := "io.ticofab"

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

crossScalaVersions := Seq("2.11.11", scalaVersion.value)

libraryDependencies ++= {

  val akkaVersion = "2.5.20"
  val akkaHttpVersion = "10.1.7"

  Seq(

    // akka stuff
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,

    // json parsing stuff
    "io.spray" %% "spray-json" % "1.3.5",

    // for de/encoding in Base64
    "commons-codec" % "commons-codec" % "1.11",

    // ---- test
    "org.scalatest" %% "scalatest" % "3.0.1" % Test,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test

  )

}

bintrayPackageLabels := Seq("scala", "akka", "kraken", "reactive")
