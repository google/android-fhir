#!/bin/bash

# Copyright 2021 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


# Script to run an end-to-end test that launches app, downloads Patient?address-city=NAIROBI
# from hapi.fhir.org, and then compares the stored patient count to what is on
# the public server. The number should be the same.

set -e

log_file=log_${GITHUB_RUN_ID}.log

function setup {
  adb root
  sleep 10
  echo "Disabling encryption on DB..."
  sed -i '' 's/enableEncryptionIfSupported = true/enableEncryptionIfSupported = false/g' \
  demo/src/main/java/com/google/android/fhir/demo/FhirApplication.kt
}

function upload_artifacts {
  mkdir -p ./test-results

  echo "Uploading App logs..."
  get_logs_from_device
  mv "${log_file}" test-results/"${log_file}"

  echo "Uploading Screenshot..."
  adb exec-out screencap -p > test-results/screen_"${GITHUB_RUN_ID}".png

  echo "Uploading DB..."
  cp resources.db test-results/resources_"${GITHUB_RUN_ID}".db

  echo "Enabling encryption on DB..."
  sed -i '' 's/enableEncryptionIfSupported = false/enableEncryptionIfSupported = true/g' \
  demo/src/main/java/com/google/android/fhir/demo/FhirApplication.kt
}

function get_logs_from_device {
  echo "Fetching logs..."
  adb logcat com.google.android.fhir.demo:D -d > "${log_file}"
}

function pull_db {
  echo "Pulling db from app..."
  while [[ ! -f "resources.db"  ]]
  do
    adb pull /data/data/com.google.android.fhir.demo/databases/resources.db || \
      echo "db does not exist yet. Will try again in 10 seconds..."
    sleep 10
  done
}

# Upload logs, screenshot, and db
trap upload_artifacts EXIT

setup

./gradlew demo:installDebug
adb -s emulator-5554 shell am start -n "com.google.android.fhir.demo/.MainActivity"
while [[ ! $(grep "Worker result SUCCESS for Work" ${log_file}) ]]
do
  echo "Waiting for sync. Sleeping for 60 seconds..."
  sleep 60
  get_logs_from_device
  echo "App sync hasn't succeeded. Waiting again..."
  adb -s emulator-5554 shell am start -n "com.google.android.fhir.demo/.MainActivity"
done

pull_db
patient_count_in_app=$(sqlite3 resources.db "select count(*) from ResourceEntity where resourceType = 'Patient';")
echo "Number of patients in App's DB is: ${patient_count_in_app}"
patient_count_in_hapi=$(curl -s "http://hapi.fhir.org/baseR4/Patient?address-city=NAIROBI&_summary=count" | jq .total)
echo "Number of patients in HAPI server is: ${patient_count_in_hapi}."

if [[ ${patient_count_in_app} -ne ${patient_count_in_hapi} ]]; then
  echo "FAILED: Number of patients in app not equal to number of patients in server..."
  exit 1
fi
