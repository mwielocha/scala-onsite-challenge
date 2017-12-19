package com.example.service

import java.util.UUID

import akka.actor.ActorRef
import akka.pattern._
import akka.util.Timeout
import com.example.actors.StateActor.{Query, UpdateBudget}
import com.example.model.{Campaign, ConnectionTypes}
import com.example.{Bid, BidResponse, NoBid}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class BiddingService(private val state: ActorRef) {

  private implicit val timeout: akka.util.Timeout = Timeout(200 millis)

  private def bid: Future[BigDecimal] = {
    Future.successful {
      val rnd = math.random() * 20
      BigDecimal(0.035) + BigDecimal("" + rnd / 100)
    }
  }

  private def resolveIp(ip: String): Future[String] = {
    Future.successful("DE")
  }

  def notify(auctionId: String): Future[Unit] = {
    state ! UpdateBudget(UUID.fromString(auctionId))
    Future.unit
  }

  def query(
    id: String,
    bundle: String,
    currency: String,
    ip: String,
    connType: String
  )(implicit ec: ExecutionContext): Future[BidResponse] = {

    for {
      amount <- bid
      country <- resolveIp(ip)
      campaign <- (state ? Query(
        UUID.fromString(id),
        amount, country,
        bundle, ConnectionTypes
          .withName(connType)
      )).mapTo[Option[Campaign]]
      response: BidResponse = campaign match {
        case None => NoBid(id)
        case Some(_) =>
          Bid(
            id, amount.toDouble,
            currency,
            "http://creative.com",
            s"http://localhost:9000/winner/$id"
          )
      }
    } yield response
  }
}
