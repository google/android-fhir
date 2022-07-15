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

# Script to setup Kokoro to run CI tests for Android FHIR SDK. The script
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
export ANDROID_HOME=${HOME}/android_sdk
export PATH=$PATH:${ANDROID_HOME}/cmdline-tools/latest/bin
export PATH=$PATH:${ANDROID_HOME}/platform-tools
export PATH=$PATH:${ANDROID_HOME}/emulator

function zip_artifacts() {
  mkdir -p test-results
  zip test-results/build.zip ./*/build -r -q
  find . -type f -regex ".*[t|androidT]est-results/.*xml" \
    -exec cp {} test-results/ \;

  gsutil -q -m cp ./Recording/*.mp4 gs://android-fhir-build-artifacts/$KOKORO_BUILD_ARTIFACTS_SUBDIR/
  echo "URLs for screen capture videos:"
  gsutil ls gs://android-fhir-build-artifacts/$KOKORO_BUILD_ARTIFACTS_SUBDIR/*.mp4 \
    | sed 's|gs://|https://storage.googleapis.com/|'
}

function setup() {
  # Resize partition from 100 GB to max
  sudo apt -y install cloud-guest-utils
  sudo growpart /dev/sda 1
  sudo resize2fs /dev/sda1

  # Set Java version to 11
  sudo update-java-alternatives --set "/usr/lib/jvm/java-1.11.0-openjdk-amd64"

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

function create_emulator() {
  # Kill any running emulators
  adb devices | grep emulator | cut -f1 | while read line; do adb -s $line emu kill; sleep 5; done

  # Install Android image and install on emulator, then turn on emulator
  local system_image="system-images;android-$1;google_apis;x86"
  sdkmanager  --install ${system_image} > /dev/null
  echo "no" | avdmanager create avd  -f --name test_avd \
      --tag google_apis --package ${system_image}
  sudo chown ${USER} /dev/kvm

  # TODO(omarismail): remove when https://github.com/google/android-fhir/issues/1482 fixed
  echo "hw.lcd.width=1080" >> $HOME/.android/avd/test_avd.avd/config.ini
  echo "hw.lcd.height=1920" >> $HOME/.android/avd/test_avd.avd/config.ini

  emulator @test_avd -no-snapshot-save -no-window \
        -gpu swiftshader_indirect -noaudio -no-boot-anim -cores 4 \
        -wipe-data -camera-back none -partition-size 6144 -memory 4096 &
  timeout 500 adb -s emulator-5554 \
        wait-for-device \
        shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 10; done'

  adb -s emulator-5554 shell settings put global window_animation_scale 0.0
  adb -s emulator-5554 shell settings put global transition_animation_scale 0.0
  adb -s emulator-5554 shell settings put global animator_duration_scale 0.0
}

function run_build_script() {
  cp .github/kokoro-gradle.properties ./gradle.properties
  API_LEVELS=(30 27 24)
  for api_level in "${API_LEVELS[@]}"; do
    create_emulator "${api_level}"
    ./build.sh "${api_level}" "--record_screen"
  done
}

function run_codecov() {
  # Don't write secrets to the logs
  set +x
  curl -Os https://uploader.codecov.io/latest/linux/codecov
  chmod +x codecov
  CODECOV_TOKEN="$(cat "${KOKORO_KEYSTORE_DIR}/76773_android-fhir-codecov-token")"

  ./codecov  -f engine/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml \
    -f datacapture/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml \
    -f common/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml \
    -t ${CODECOV_TOKEN}
}

setup
cd ${KOKORO_ARTIFACTS_DIR}/github/android-fhir
trap zip_artifacts EXIT
run_build_script
run_codecov
./gradlew publishReleasePublicationToCIRepository --scan
