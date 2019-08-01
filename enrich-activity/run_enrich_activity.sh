#!/bin/sh
#
# Script for Enriching User Activity Log scala App.
# Created by lakshman
#

echo "Building User Activity JAR"

mvn clean compile package

echo "Running the User Activity Log Enrichment"

jar_file=./target/enrich-activity-1.0-SNAPSHOT-jar-with-dependencies.jar

java -jar ${jar_file}