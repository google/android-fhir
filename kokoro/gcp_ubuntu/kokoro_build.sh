#!/bin/bash
#
# Copyright 2022 Google LLC
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
#

# This script should NOT be run locally.

# Script to setup Kokoro to run CI tests for the Android FHIR SDK. The script
# downloads dependencies, and for each API_LEVEL specified below, creates a new
# emulator, then runs the ./build script.

# A full build is done for API Level 30. For subsequent API levels, Gradle
# only runs the instrumentation tests.

# Fail on any error.
set -e
# Display commands being run.
# WARNING: please only enable 'set -x' if necessary for debugging, and be very
#  careful if you handle credentials (e.g. from Keystore) with 'set -x':
#  statements like "export VAR=$(cat /tmp/keystore/credentials)" will result in
#  the credentials being printed in build logs.
#  Additionally, recursive invocation with credentials as command-line
#  parameters, will print the full command, with credentials, in the build logs.
# set -x
# Code under repo is checked out to ${KOKORO_ARTIFACTS_DIR}/git.
# The final directory name in this path is determined by the scm name specified
# in the job configuration.
export JAVA_HOME="/usr/lib/jvm/java-1.11.0-openjdk-amd64"
export ANDROID_HOME=${HOME}/android_sdk
export PATH=$PATH:$JAVA_HOME/bin:${ANDROID_HOME}/cmdline-tools/latest/bin
export GCS_BUCKET="android-fhir-build-artifacts"

function zip_artifacts() {
  mkdir -p test-results
  zip test-results/build.zip ./*/build -r -q
  find . -type f -regex ".*[t|androidT]est-results/.*xml" \
    -exec cp {} test-results/ \;

  echo "URLs for screen capture videos:"
  gsutil ls gs://$GCS_BUCKET/$KOKORO_BUILD_ARTIFACTS_SUBDIR/**/*.mp4 \
    | sed 's|gs://|https://storage.googleapis.com/|'
}

function setup() {
  # Install npm for spotlessApply
  sudo npm cache clean -f
  sudo npm install -g n
  sudo n stable

  # Setup Android CLI tools
  wget https://dl.google.com/android/repository/commandlinetools-linux-8512546_latest.zip \
    -O ${HOME}/android_sdk.zip -q
  unzip ${HOME}/android_sdk.zip -d ${ANDROID_HOME}
  mkdir ${ANDROID_HOME}/cmdline-tools/latest
  mv ${ANDROID_HOME}/cmdline-tools/bin ${ANDROID_HOME}/cmdline-tools/latest
  mv ${ANDROID_HOME}/cmdline-tools/lib ${ANDROID_HOME}/cmdline-tools/latest

  yes | sdkmanager --licenses > /dev/null
  sdkmanager --update > /dev/null
  sdkmanager "platforms;android-30" "build-tools;30.0.2" > /dev/null
}

function build_only() {
  ./gradlew spotlessCheck --scan --stacktrace
  ./gradlew build --scan --stacktrace
  ./gradlew check --scan --stacktrace
}

function run_firebase_testlab() {
  ./gradlew packageDebugAndroidTest --scan --stacktrace
  local lib_names=("datacapture" "engine" "workflow")
  firebase_pids=()
  for lib_name in "${lib_names[@]}"; do
   gcloud firebase test android run --type instrumentation \
      --app demo/build/outputs/apk/androidTest/debug/demo-debug-androidTest.apk \
      --test $lib_name/build/outputs/apk/androidTest/debug/$lib_name-debug-androidTest.apk \
      --timeout 30m \
      --device model=Nexus6P,version=24 \
      --device model=Nexus6P,version=27 \
      --device model=Pixel2,version=30 \
      --environment-variables coverage=true,coverageFile="/sdcard/Download/coverage.ec" \
      --directories-to-pull /sdcard/Download  \
      --results-bucket=$GCS_BUCKET \
      --results-dir=$KOKORO_BUILD_ARTIFACTS_SUBDIR/firebase/$lib_name \
      --project=android-fhir-instrumeted-tests \
      --no-use-orchestrator &
      firebase_pids+=("$!")
  done

  for firebase_pid in ${firebase_pids[*]}; do
    wait $firebase_pid
  done

  mkdir -p {datacapture,engine,workflow}/build/outputs/code_coverage/debugAndroidTest/connected/firebase
  for lib_name in "${lib_names[@]}"; do
    gsutil -m cp -R gs://$GCS_BUCKET/$KOKORO_BUILD_ARTIFACTS_SUBDIR/firebase/$lib_name/Pixel2-30-en-portrait/**/coverage.ec \
      $lib_name/build/outputs/code_coverage/debugAndroidTest/connected/firebase
  done
}

function run_codecov() {
  ./gradlew jacocoTestReport --exclude-task createDebugCoverageReport --scan --stacktrace
  # Don't write secrets to the logs
  set +x
  curl -Os https://uploader.codecov.io/latest/linux/codecov
  chmod +x codecov
  ./codecov  \
    -f common/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml \
    -f datacapture/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml \
    -f engine/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml \
    -f workflow/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml \
    -t "$(cat "${KOKORO_KEYSTORE_DIR}/76773_android-fhir-codecov-token")"
}

setup
cd ${KOKORO_ARTIFACTS_DIR}/github/android-fhir
trap zip_artifacts EXIT

build_only
run_firebase_testlab
run_codecov
./gradlew publishReleasePublicationToCIRepository --scan
