package org.lakshman.common.sampledata

import org.lakshman.common.model.{Products, UserActivity}

import scala.collection.immutable
import scala.util.Random

object SampleData {

  val rec1: String = """{
    "visitorId": "v1",
    "products": [{
         "id": "i1",
         "interest": 0.68
    }, {
         "id": "i2",
         "interest": 0.42
    }]
  }""".stripMargin

  val rec2: String = """{
    "visitorId": "v2",
    "products": [{
         "id": "i1",
         "interest": 0.78
    }, {
         "id": "i3",
         "interest": 0.11
    }]
  }""".stripMargin

  val productIdToNameMap = Map("i1" -> "Nike Shoes", "i2" -> "Umbrella", "i3" -> "Jeans")
  val visitsData: Seq[String] = Seq(rec1, rec2)

  //Large DataSets

  val largeCatalog: Map[String, String] = (0 to 100).map{ i => ("p" + i, "Product Name" + i)}.toMap[String, String]

  def getProducts(i: Int) = {
    val products = if(i %2 == 0)
      (0 to largeCatalog.size/2)
    else
      (largeCatalog.size/2 to largeCatalog.size)

    products.map( j => Products("p" + j, None, new Random().nextDouble())).toList
  }

  val largeActivityLog: Seq[String] = (0 to 100000).map{ i => UserActivity("v"+i, getProducts(i)).toJSON}

}
