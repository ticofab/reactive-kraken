
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

name := """reactive-kraken"""

version := "0.1.0"

scalaVersion := "2.12.2"

organization := "io.ticofab"

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

crossScalaVersions := Seq("2.11.8", scalaVersion.value)

libraryDependencies ++= {

  val akkaVersion = "2.5.3"
  val akkaHttpVersion = "10.0.9"

  Seq(

    // akka stuff
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,

    // json parsing stuff
    "io.spray" %% "spray-json" % "1.3.3",

    // for de/encoding in Base64
    "commons-codec" % "commons-codec" % "1.10",

    // ---- test
    "org.scalatest" %% "scalatest" % "3.0.1" % "test, it",
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test, it",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test, it",
    "org.mockito" % "mockito-core" % "2.3.11" % "test, it"

  )

}

lazy val root = Project(id = "reactive-kraken", base = file("."))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .settings(
    sourceDirectory in IntegrationTest := baseDirectory.value / "src/integrationTest",
    parallelExecution in IntegrationTest := false
  )

bintrayPackageLabels := Seq("scala", "akka", "kraken", "reactive")
