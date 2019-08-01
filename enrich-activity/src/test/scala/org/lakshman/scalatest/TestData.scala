package org.lakshman.scalatest

trait TestData {

  val productCatalog = Map("p1" -> "Product1", "p2" -> "Product2", "p3" -> "Product3")

  val raw1: String = """{
    "visitorId": "v1",
    "products": [{
         "id": "p1",
         "interest": 0.68
    }, {
         "id": "p2",
         "interest": 0.42
    }]
  }"""


  val raw2: String = """{
    "visitorId": "v2",
    "products": [{
         "id": "p1",
         "interest": 0.78
    }, {
         "id": "p3",
         "interest": 0.11
    }]
  }"""

  val raw3: String = """{
    "visitorId": "v3",
    "products": [{
         "id": "p2",
         "interest": 0.78
    }, {
         "id": "p4",
         "interest": 0.11
    }]
  }"""

  val malformedJson: String = """{
    "visitorId": "v3",
    "products": [{
         "id": "p2",
         "interest": 0.78
    },
         "id": "p4",
         "interest": 0.11
    }]
  }"""
  val activityList = Seq(raw1, raw2, raw3)

}
