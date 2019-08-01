#!/bin/sh
#
# Spark submit script for Enriching User Activity Log job.
# Created by lakshman
#

echo "Building User Activity Log JAR"

#cd ../activity-common
#
#mvn clean compile package
#
#cd ../enrich-activity-spark

mvn clean compile package

echo "Running the User Activity Log Enrichment"

jar_file=./target/enrich-activity-spark-1.0-SNAPSHOT-jar-with-dependencies.jar

spark-submit --name "User Activity Log" \
 --master local[4] \
 --deploy-mode client \
 --executor-memory 1g \
 --class org.spark.lakshman.ActivityEnricher \
 ${jar_file}