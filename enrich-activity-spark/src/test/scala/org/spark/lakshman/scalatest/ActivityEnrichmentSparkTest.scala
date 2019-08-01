/*
 * Copyright 2001-2009 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.spark.lakshman.scalatest

import org.spark.lakshman.ActivityEnricher._
import org.lakshman.common.exceptions.ActivityEnrichmentException
import org.lakshman.common.model.{Products, UserActivity}
import org.lakshman.common.utils.JsonUtil._
import org.scalatest.{FlatSpecLike, MustMatchers}
import org.lakshman.common.sampledata.SampleData._

class ActivityEnrichmentSparkTest extends FlatSpecLike with MustMatchers with TestData {

  import sparkSession.implicits._

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

  it should "enrich activity list RDD using broadcast productCatalog" in {
    val rdd = sc.parallelize(activityList)
    val broadcast = sc.broadcast(productCatalog)
    val enrichedActivity = rdd.map(enrichActivity(_, broadcast.value))
    broadcast.value must be(productCatalog)
    enrichedActivity.collect().foreach { activity =>
      activity.products.map(validateProduct)
    }
  }

  it should "enrich activity list RDD using spark SQL" in {
    val enrichedActivity = enrichedUserActivitySQL(activityList, productCatalog)
    enrichedActivity.show(10)

    enrichedActivity
      .select("productid", "name")
      .as[(String, String)]
      .collect
      .foreach { row =>
        val productId = row._1
        val productName = row._2
        productName must be(productCatalog.getOrElse(productId, null))
      }
  }

  it should "Scale Test for Spark" in {
    val enrichedActivity = enrichedUserActivity(largeActivityLog, largeCatalog)
    val enrichedObj = enrichedActivity.map{x => fromJson[UserActivity](x)}
    enrichedObj.collect().foreach { activity =>
      activity.products.map(validateProduct)
    }
  }

  it should "Scale Test for spark SQL" in {
    val enrichedActivity = enrichedUserActivitySQL(largeActivityLog, largeCatalog)
    enrichedActivity.show(20)

    enrichedActivity
      .select("productid", "name")
      .as[(String, String)]
      .collect
      .foreach { row =>
        val productId = row._1
        val productName = row._2
        productName must be(largeCatalog.getOrElse(productId, null))
      }
  }
}
