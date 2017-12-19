package com.example.actors

import akka.actor.{Actor, ActorRef, Props}
import com.example.actors.StateActor.{Add, Query}
import akka.pattern._
import akka.util.Timeout
import com.example.model.Campaign
import com.example.repository.CampaignRepository

import scala.concurrent.duration._
import scala.concurrent.Future

class StateRoutingActor(campaignRepository: CampaignRepository) extends Actor {

  private implicit val timeout: akka.util.Timeout = Timeout(200 millis)

  import context.dispatcher

  override def preStart(): Unit = {
    super.preStart()

    campaignRepository.getAll
      .foreach(_.foreach(x => self ! Add(x)))

  }
  
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
