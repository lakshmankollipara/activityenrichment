package org.lakshman

import org.apache.log4j.Logger
import org.lakshman.common.sampledata.SampleData.{
  productIdToNameMap,
  visitsData
}
import org.lakshman.common.model.UserActivity
import org.lakshman.common.exceptions.ActivityEnrichmentException
import org.lakshman.common.utils.JsonUtil._

/**
  * @author ${lakshman}
  */
object ActivityEnricher extends App {

  val log = Logger.getLogger(getClass.getName)

  /**
    * Utility method to enrich User Activity
    *
    * @param rawActivity  raw JSON String with User Activity
    * @param productCatalog A product id to name lookup.
    * @return An enriched user activity of type UserActivity
    * @throws ActivityEnrichmentException when we receive a malformd JSON
    */

  def enrichActivity(rawActivity: String, productCatalog: Map[String, String]): UserActivity = {
    try {
      val activityObject = fromJson[UserActivity](rawActivity)
      val enrichedProducts = activityObject.products.map { product =>
        product.copy(name = productCatalog.get(product.id))
      }
      activityObject.copy(products = enrichedProducts)
    } catch {
      case ex: Exception => throw new ActivityEnrichmentException("Error enriching User Activity: " + ex.getMessage)
    }
  }

  def enrichedUserActivity(activityList: Seq[String], productCatalog: Map[String, String] = productIdToNameMap) = {
    activityList.par.map(enrichActivity(_, productCatalog).toJSON)
  }

  log.info("Enriched Activity Log: \n" + enrichedUserActivity(visitsData).mkString(",\n"))

}
