package org.lakshman.scalatest

import org.scalatest._
import org.lakshman.ActivityEnricher._
import org.lakshman.common.exceptions.ActivityEnrichmentException
import org.lakshman.common.model.Products

class ActivityEnrichmentTest extends FlatSpecLike with MustMatchers with TestData {

  def validateProduct(product: Products) = {
    product.name must be(productCatalog.get(product.id))
  }

  "UserActivityEnrichment" should "convert raw activity string to enriched Activity object" in {
    val enrichedActivity = enrichActivity(raw1, productCatalog)
    enrichedActivity.products.map(validateProduct)
  }

  it should "enrich list of User activity" in {
    val enrichedActivityList =
      activityList.map(enrichActivity(_, productCatalog))
    enrichedActivityList.map(_.products.map(validateProduct))
  }

  it should "throw ActivityEnrichmentException for malformed JSON" in {
    assertThrows[ActivityEnrichmentException](enrichActivity(malformedJson, productCatalog))
  }
}
