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

# Script to check if code passes builds and tests. When running manually, run
# without passing any parameter. Kokoro sends one parameter, the API Level, as
# part of its CI pipeline to run certain Gradle tasks for the API Level passed.

# Example usage:
#   ./build.sh
#   ./build.sh --record_screen
#   ./build.sh 30 --record_screen

# Fail on any error.
set -e

function cleanup() {
  if [[ -n "${RECORD_SCREEN}"  ]]; then
    echo "INFO: Stopping screen recording and pulling video from device"
    $ADB shell ls "${RECORD_DIR}"
    $ADB pull "${RECORD_DIR}" || echo "No videos on phone"
  fi
}

function validate_args() {
  if [[ $# -ge 3 ]]; then
    echo "ERROR: Invalid number of args passed."
    exit 1
  fi

  RECORD_SCREEN=""
  if [[ $1 == "--record_screen" || $2 == "--record_screen" ]]; then
    RECORD_SCREEN="on"
    RECORD_DIR="/sdcard/Recording/"
  fi

  regex_for_api_level='^[0-9]+$'
  if [[ $1 =~ $regex_for_api_level ]] ; then
     API_LEVEL=$1
  fi
}

function check_adb_exists() {
  if [[ $(type -p adb) == *"adb"* ]]; then
    ADB=adb
  elif [[ -n "${ANDROID_HOME}" ]] && [[ -f $(find "${ANDROID_HOME}" -name "adb" -type f) ]]; then
    ADB=$(find "${ANDROID_HOME}" -name "adb" -type f)
 else
    echo "ERROR: ANDROID_HOME is not set and cannot find 'adb' in your PATH."
    exit 1
  fi
}

function run_build() {
  if [[ $API_LEVEL == 30 || -z $API_LEVEL ]]; then
    ./gradlew spotlessCheck --scan
    ./gradlew build --scan --stacktrace
  fi

  ./gradlew check --scan -stacktrace

  if [[ -n "${RECORD_SCREEN}"  ]]; then
    echo "INFO: Recording screen for instrumentation tests"
    $ADB shell mkdir "${RECORD_DIR}"
    $ADB shell screenrecord "${RECORD_DIR}"/debug-${API_LEVEL}.mp4 &
  fi

  ./gradlew connectedCheck --scan --stacktrace

  if [[ "${API_LEVEL}" == 30 ]]; then
    ./gradlew jacocoTestReport --scan --info
  fi
}

trap cleanup EXIT
validate_args "$@"
check_adb_exists
run_build
