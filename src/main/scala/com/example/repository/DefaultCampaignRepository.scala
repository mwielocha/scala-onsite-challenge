package com.example.repository

import com.example.model.Campaign.Id
import com.example.model.{Campaign, ConnectionTypes}

import scala.concurrent.Future

class DefaultCampaignRepository extends CampaignRepository {

  /*
  1;CocaCola Life;50000;DE;com.rovio.angry_birds;WiFi
  2;CocaCola Life;50000;DE;com.spotify;WiFi
  3;CocaCola Life;50000;DE;com.facebook;WiFi
   */

  private val campaigns: Seq[Campaign] = Seq(
    Campaign(
      Campaign.Id(1L),
      "CocaCola Life",
      BigDecimal(50000),
      "DE",
      "com.rovio.angry_birds",
      ConnectionTypes.WiFi
    ),
    Campaign(
      Campaign.Id(2L),
      "CocaCola Life",
      BigDecimal(50000),
      "DE",
      "com.spotify",
      ConnectionTypes.WiFi
    ),
    Campaign(
      Campaign.Id(3L),
      "CocaCola Life",
      BigDecimal(50000),
      "DE",
      "com.facebook",
      ConnectionTypes.WiFi
    )
  )

  override def getById(id: Id): Future[Option[Campaign]] = {
    Future.successful(campaigns.find(_.id == id))
  }

  override def getAll: Future[Seq[Campaign]] = {
    Future.successful(campaigns)
  }
}
