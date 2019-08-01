# Activity Enrichment

Enrich the User Activity Log with Product details from Product Catalog.

# Sample Input
User Activity List = List of raw user activity Strings
```
val rec1: String = """{
    "visitorId": "v1",
    "products": [{
         "id": "i1",
         "interest": 0.68
    }, {
         "id": "i2",
         "interest": 0.42
    }]
}"""
 
val rec2: String = """{
    "visitorId": "v2",
    "products": [{
         "id": "i1",
         "interest": 0.78
    }, {
         "id": "i3",
         "interest": 0.11
    }]
}"""
 
val visitsData: Seq[String] = Seq(rec1, rec2)
```
Product Catalog = A dictionary with the product id and product name
```
val productIdToNameMap = Map("i1" -> "Nike Shoes", "i2" -> "Umbrella", "i3" -> "Jeans")
```

# Expected Output
List of Enriched User Activity Log with Product details.
```
enrichedRec1 =
"""{
    "visitorId": "v1",
    "products": [{
         "id": "i1",
         "name": "Nike Shoes",
         "interest": 0.68
    }, {
         "id": "i2",
         "name": "Umbrella",
         "interest": 0.42
    }]
}"""
 
enrichedRec2 =
"""{
    "visitorId": "v2",
    "products": [{
         "id": "i1",
         "name": "Nike Shoes",
         "interest": 0.78
    }, {
         "id": "i3",
         "name": "Jeans",
         "interest": 0.11
    }]
}"""


val output: Seq[String] = Seq(enrichedRec1, enrichedRec2)
 
```
# Code Modules

# activity-common
    Contains the common core module defining UserActivity Model and conversion of UserActivity Object to and from JSON.
    Also contains sample data given in the coding test for test suite

# enrich-activity
    Scala Application to enrich the user activity with product details
Execution:
Run the shell script file in ActivityEnrichment/enrich-activity directory
```
  #cd ActivityEnrichment/enrich-activity
  #./run_enrich_activity.sh
```
  
    
# enrich-activity-spark
    Spark(Scala) Application to enrich the user activity with product details parallelly
    
Execution:
Run the shell script file in ActivityEnrichment/enrich-activity-spark directory
```
  #cd ActivityEnrichment/enrich-activity-spark
  #./run_enrich_activity_job.sh
```
