package com.example.model

import com.example.model.ConnectionTypes.ConnectionType
import shapeless.tag
import tag.@@

case class Campaign(
  id: Campaign.Id,
  title: String,
  budget: BigDecimal,
  country: String,
  mobileName: String,
  connectionType: ConnectionType
) {

  def updateBudget(amount: BigDecimal): Campaign = {
    copy(budget = budget - amount)
  }
}

object Campaign {

  type Id = Long @@ Campaign

  object Id {
    def apply(in: Long): Id = tag[Campaign][Long](in)
  }
}
