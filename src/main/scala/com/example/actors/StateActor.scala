package com.example.actors

import java.util.UUID

import akka.actor.Actor
import com.example.model.Campaign
import StateActor._
import akka.routing.ConsistentHashingRouter.ConsistentHashMapping
import com.example.model.ConnectionTypes.ConnectionType

case class Lock(
  auctionId: UUID,
  amount: BigDecimal,
  at: Long = System.currentTimeMillis()
)

object Lock {
  val expired: Lock => Boolean = {
    case Lock(_, _, at) =>
      System.currentTimeMillis() - at < 100L
  }
}

case class CampaignState(
  campaign: Campaign,
  locks: Seq[Lock]
) {

  def id: Campaign.Id = campaign.id

  def locked: BigDecimal = locks.filter(Lock.expired).map(_.amount).sum

  def budget: BigDecimal = campaign.budget - locked

  def lock(auctionId: UUID, amount: BigDecimal): CampaignState =
    CampaignState(campaign, locks.filter(Lock.expired) :+ Lock(auctionId, amount))

  def update(auctionId: UUID): CampaignState = {
    (for {
      lock <- locks.find(_.auctionId == auctionId)
      amount = lock.amount
    } yield {
      CampaignState(
        campaign.updateBudget(amount),
        locks.filter(_.auctionId != auctionId))
    }).getOrElse(this)
  }
}

object StateActor {

  case class Add(c: Campaign)
  case class Get(id: Campaign.Id)
  case object GetAll

  case class Query(
    auctionId: UUID,
    bid: BigDecimal,
    country: String,
    bundleName: String,
    connectionType: ConnectionType
  )

  case class LockBudget(id: Campaign.Id, auctionId: UUID, amount: BigDecimal)
  case class UpdateBudget(auctionId: UUID)

  val hashMapping: ConsistentHashMapping = {
    case Add(c) => c.id
    case Get(id) => id
    case LockBudget(id, _, _) => id
  }
}

class StateActor extends Actor {

  private var campaigns: Map[Campaign.Id, CampaignState] = Map.empty

  override def receive: Receive = {

    case Add(c) => campaigns = campaigns + (c.id -> CampaignState(c, Nil))

    case Get(id) => sender() ! campaigns.get(id).map(_.campaign)

    case Query(auctionId, bid, country, bundleName, connectionType) =>

      val found = campaigns.find {

        case (_, CampaignState(c, _)) =>
          c.budget > bid &&
            c.country == country &&
            c.mobileName == bundleName &&
            c.connectionType == connectionType

      }.map(_._2.campaign)

      found match {
        case None => //
        case Some(campaign) =>
          self ! LockBudget(campaign.id, auctionId, bid)
      }

      sender() ! found

    case LockBudget(id, auctionId, amount) =>

      for {
        state <- campaigns.get(id)
        updated = state.lock(auctionId, amount)
      } yield campaigns + (state.id -> updated)

    case UpdateBudget(auctionId) =>

      for {
        state <- campaigns.values.find(
          _.locks.map(_.auctionId)
            .contains(auctionId))
        updated = state.update(auctionId)
      } yield campaigns + (state.id -> updated)

  }
}
