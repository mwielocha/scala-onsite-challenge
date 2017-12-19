package com.example.repository

import com.example.model.Campaign

import scala.concurrent.Future

trait CampaignRepository {

  def getById(id: Campaign.Id): Future[Option[Campaign]]

  def getAll: Future[Seq[Campaign]]

}
