package com.example

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.example.actors.StateRoutingActor
import com.example.repository.DefaultCampaignRepository
import com.example.service.BiddingService

object Boot {

  def main(args: Array[String]): Unit = {
    // we need an ActorSystem to host our application in
    implicit val system = ActorSystem("simple-dsp")
    implicit val materializer = ActorMaterializer()

    val repo = new DefaultCampaignRepository
    val state = system.actorOf(Props(classOf[StateRoutingActor], repo))
    val service = new BiddingService(state)
    val frontend = new DspFrontend(service)

    Http().bindAndHandle(frontend(), "localhost", 8080)
  }
}
