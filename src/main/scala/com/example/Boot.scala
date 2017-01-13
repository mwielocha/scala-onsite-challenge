package com.example

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

object Boot {
  def main(args: Array[String]): Unit = {
    // we need an ActorSystem to host our application in
    implicit val system = ActorSystem("simple-dsp")
    implicit val materializer = ActorMaterializer()

    Http().bindAndHandle(DspFrontend(), "localhost", 8080)
  }
}
