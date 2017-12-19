package com.example.actors

import akka.actor.{Actor, ActorRef, Props}
import com.example.actors.StateActor.Query
import akka.pattern._
import akka.util.Timeout
import com.example.model.Campaign
import scala.concurrent.duration._

import scala.concurrent.Future

class StateRoutingActor extends Actor {

  private implicit val timeout: akka.util.Timeout = Timeout(200 millis)

  import context.dispatcher

  private val stateActors = (1 to 10).map {
    _ => context.actorOf(Props[StateActor])
  }

  def stateActor(m: Any): ActorRef = {
    val idx = StateActor.hashMapping(m).hashCode() % 10
    stateActors(idx)
  }

  override def receive: Receive = {

    case q: Query =>

      val localSender = sender()

      val fu = Future.sequence {
        stateActors.map {
          ref =>
            (ref ? q)
              .mapTo[Option[Campaign]]
        }
      }

      for(responses <- fu) {
        localSender ! responses.flatten.headOption
      }

    case x => stateActor(x) forward x
  }
}
