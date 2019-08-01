package org.spark.lakshman

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.log4j.Logger
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions.broadcast
import org.apache.spark.sql.SparkSession
import org.lakshman.common.sampledata.SampleData.{
  productIdToNameMap,
  visitsData
}
import org.lakshman.common.model.UserActivity
import org.lakshman.common.exceptions.ActivityEnrichmentException
import org.lakshman.common.utils.JsonUtil._

/**
  * @author ${user.name}
  */
object ActivityEnricher {

  val log = Logger.getLogger(getClass.getName)

  val sparkSession = SparkSession
    .builder()
    .master("local[4]")
    .appName("User Activity Log Enricher")
    .getOrCreate()

  val sc = sparkSession.sparkContext
  import sparkSession.implicits._

  sc.setLogLevel("ERROR")

  /**
    * Utility method to enrich User Activity
    *
    * @param rawActivity  raw JSON String with User Activity
    * @param productCatalog A product id to name lookup.
    * @return An enriched user activity of type UserActivity
    * @throws ActivityEnrichmentException when we receive a malformed JSON
    */

  def enrichActivity(rawActivity: String, productCatalog: Map[String, String] = productIdToNameMap): UserActivity = {
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

  /**
    * User Activity Log Enrichment main function (Using Spark-Core)
    *
    * @param activityList  Sequence of raw User Activity JSON Strings
    * @param productCatalog A product id to name lookup (Spark Broadcast value).
    * @return An enriched user activity String
    * @throws ActivityEnrichmentException when we receive a malformed JSON
    */

  def enrichedUserActivity(activityList: Seq[String], productCatalog: Map[String, String] = productIdToNameMap) = {
    val activityRDD = sc.parallelize(visitsData)
    val broadcastCatalog = sc.broadcast(productCatalog)
    activityRDD.mapPartitions { iter =>
      iter.map { rawActivity =>
        enrichActivity(rawActivity, broadcastCatalog.value).toJSON
      }
    }
  }

  /**
    * User Activity Log Enrichment main function (Using Spark-SQL)
    *
    * @param activityList  Sequence of raw User Activity JSON Strings
    * @param productCatalog A product id to name lookup (Spark Broadcast value).
    * @return An enriched user activity String
    * @throws ActivityEnrichmentException when we received a malformed JSON
    */
  def enrichedUserActivitySQL(activityList: Seq[String], productCatalog: Map[String, String] = productIdToNameMap) = {
    //Broadcast Product Catalog
    val productTable = productCatalog.toSeq.toDF("id", "name")
    val broadcastCatalog = broadcast(productTable.as("products"))

    //convert Input Data to DataFrames
    val activityRDD = sc.parallelize(activityList).mapPartitions { iter =>
      iter.map(fromJson[UserActivity])
    }
    val visitorTable = activityRDD.map(_.visitorId).toDF("visitorid")
    val activityTable = activityRDD.flatMap { a =>
        a.products.map { p => (a.visitorId, p.id, p.interest) }
      }.toDF("visitorid", "productid", "interest")

    //Join Visitor, Activity and Product tables
    val finalRDD = visitorTable
      .join(activityTable, Seq("visitorid"), "left")
      .join(broadcastCatalog, activityTable("productid") === broadcastCatalog("id"), "left")

    finalRDD.select("visitorid", "productid", "name", "interest")
  }

  def main(args: Array[String]) {
    log.info("Enriched Activity Log: \n")
    enrichedUserActivity(visitsData).take(10).foreach(println)
    log.info("Enriched Activity Log using SparkSQL: \n")
    enrichedUserActivitySQL(visitsData).show(10)
  }
}
