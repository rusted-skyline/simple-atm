#!/bin/bash

JAR_FILE="build/libs/atm-0.0.1-SNAPSHOT.jar"

if [ -f "$JAR_FILE" ]; then
	java -jar $JAR_FILE
else
	echo "Please run ./build.sh first to build the jar artifact"
fi
