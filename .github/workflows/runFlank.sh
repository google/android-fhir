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

# Fail on any error.
set -e

lib_names=("workflow:benchmark" "engine:benchmark" "datacapture" "engine" "knowledge" "workflow")
firebase_pids=()

for lib_name in "${lib_names[@]}"; do
  ./gradlew :$lib_name:runFlank --scan --stacktrace &
  firebase_pids+=("$!")
done

for firebase_pid in ${firebase_pids[*]}; do
    wait $firebase_pid
done
