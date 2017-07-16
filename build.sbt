name := "reactive-kraken"

version := "0.1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= {

  val akkaVersion = "2.5.3"
  val circeVersion = "0.8.0"
  val akkaHttpVersion = "10.0.8"

  Seq(
    // akka stuff
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,

    // json parsing stuff
    "io.spray" %% "spray-json" % "1.3.3",

    // ---- test
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test",
    "org.mockito" % "mockito-core" % "2.3.11" % "test"

  )

}

