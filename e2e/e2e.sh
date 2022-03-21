#!/bin/bash

set -e

function pull_db {
  echo "Pulling db from app..."
  while [[ ! -f "fhirEngine"  ]]
  do
    adb pull /data/data/com.google.android.fhir.demo/databases/fhirEngine || \
      echo "db does not exist yet. Will try again in 10 seconds..."
    sleep 10
  done
}

adb root
sleep 10
./gradlew installDebug

adb -s emulator-5554 shell am start -n "com.google.android.fhir.demo/.MainActivity"
sleep infinity
pull_db
sqlite3 fhirEngine "select count(*) from ResourceEntity"
