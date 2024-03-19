#!/usr/bin/env bash
# SPDX-License-Identifier: Apache-2.0
#
# Copyright 2023-2024 The Enola <https://enola.dev> Authors
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

set -euox pipefail

rm -rf site/
mkdir -p site/

# TODO https://github.com/google/android-fhir/issues/2232 Add mkdocs generation

./gradlew dokkaHtml
mkdir -p site/api/
mv docs/data-capture site/api/
mv docs/engine site/api/
mv docs/knowledge site/api/
mv docs/workflow site/api/

cp -R docs/index.html site/

