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

# Script to run CI pipeline for the Android FHIR SDK. The script downloads the
# dependencies it needs, compiles, builds, and unit tests the code, and then
# uses Firebase Test Lab to run instrumentation tests. Code coverage reports are
# then uploaded to Codecov, where they are displayed on the GitHub Pull Request.

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

# Uploads files generated from builds and tests to GCS when this script exits.
# If videos were recorded from Firebase, prints their URL to the console so users
# can click on and view.
# See: https://cloud.google.com/storage/docs/gsutil/commands/cp
# for documentation on the gsutil command used in this function
function zip_artifacts() {
  mkdir -p test-results
  zip test-results/build.zip ./*/build -r -q
  find . -type f -regex ".*[t|androidT]est-results/.*xml" \
    -exec cp {} test-results/ \;

  echo "URLs for screen capture videos:"
  gsutil ls gs://$GCS_BUCKET/$KOKORO_BUILD_ARTIFACTS_SUBDIR/**/*.mp4 \
    | sed 's|gs://|https://storage.googleapis.com/|'
}

# Installs dependencies to run CI pipeline. Dependencies are:
#   1. npm to run spotlessApply
#   2. Android Command Line tools, accepting its licenses
#   3. Build tools to compile code
function setup() {
  sudo npm cache clean -f
  sudo npm install -g n
  sudo n 16.18.0

  gcloud components update --quiet

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

# Checks if code conforms to style guide, builds the code, then runs unit tests.
function build_only() {
  ./gradlew spotlessCheck --scan --stacktrace
  ./gradlew build --scan --stacktrace
  ./gradlew check --scan --stacktrace
}

# Runs instrumentation tests using Firebase Test Lab, and retrieves the code
# coverage reports.
function device_tests() {
  ./gradlew packageDebugAndroidTest --scan --stacktrace
  ./gradlew packageReleaseAndroidTest --scan --stacktrace
    local lib_names=("datacapture" "engine" "workflow" "benchmark")
    firebase_pids=()
    for lib_name in "${lib_names[@]}"; do
      ./gradlew :$lib_name:runFlank  --scan --stacktrace &
      firebase_pids+=("$!")
    done

    for firebase_pid in ${firebase_pids[*]}; do
        wait $firebase_pid
    done
}

# Generates JaCoCo reports and uploads to Codecov: https://about.codecov.io/
# Before uploading to Codecov, run an Integrity Check on the Uploader binary.
# See: https://docs.codecov.com/docs/codecov-uploader#using-the-uploader-with-codecovio-cloud
function code_coverage() {
  ./gradlew jacocoTestReport --exclude-task createDebugCoverageReport --scan --stacktrace

  curl https://keybase.io/codecovsecurity/pgp_keys.asc \
    | gpg --no-default-keyring --keyring trustedkeys.gpg --import
  curl -Os https://uploader.codecov.io/latest/linux/codecov
  curl -Os https://uploader.codecov.io/latest/linux/codecov.SHA256SUM
  curl -Os https://uploader.codecov.io/latest/linux/codecov.SHA256SUM.sig
  gpgv codecov.SHA256SUM.sig codecov.SHA256SUM
  shasum -a 256 -c codecov.SHA256SUM
  chmod +x codecov

  # Don't write secrets to the logs
  set +x
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
device_tests
code_coverage
