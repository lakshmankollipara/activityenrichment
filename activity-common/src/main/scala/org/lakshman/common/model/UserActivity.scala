package org.lakshman.common.model

import org.lakshman.common.utils.JsonUtil

trait Entity {
  def toJSON: String         = JsonUtil.toJson(this)
}

case class Products(id: String, name: Option[String] = None, interest: Double = 0.0) extends Entity

case class UserActivity(visitorId: String, products: List[Products]) extends  Entity
