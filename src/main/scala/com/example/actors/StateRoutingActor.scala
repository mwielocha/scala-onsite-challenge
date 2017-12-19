package com.example.actors

import akka.actor.{Actor, Props}
import akka.routing.ConsistentHashingPool

class StateRoutingActor extends Actor {

  private val stateActors = context.actorOf(
    ConsistentHashingPool(10,
      hashMapping = StateActor.hashMapping)
        .props(Props[StateActor]),
    name = "cache"
  )

  override def receive: Receive = {
    case x => stateActors forward x
  }
}
