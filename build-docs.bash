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

# We need to generate the Dokka API Doc into docs/use/api/ *before* running mkdocs,
# so that it can validate the links to it and make sure that they are not broken.
./gradlew dokkaHtml

# "install --deploy" is better than "sync", because it checks that the Pipfile.lock
# is up-to-date with the Pipfile before installing. If it's not, it will fail the
# installation. This is useful for ensuring strict dependency control during CI.
pipenv install --deploy
pipenv run mkdocs build --strict
