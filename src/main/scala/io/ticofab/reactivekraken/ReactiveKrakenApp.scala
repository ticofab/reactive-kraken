package io.ticofab.reactivekraken

import akka.actor.{Actor, ActorSystem, Props}

/**
  * Created by Fabio Tiriticco on 16/07/2017.
  */

class TestActor extends Actor {

  val aPIActor = context.system.actorOf(KrakenAPIActor())
  aPIActor ! GetAssets

  override def receive = {
    case a: Any => println(a)
  }
}

object ReactiveKrakenApp extends App {
  val as = ActorSystem()
  as.actorOf(Props(new TestActor))

}
