/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

object Sdk {
  const val compileSdk = 31
  const val targetSdk = 31

  // Engine and SDC must support API 24.
  // Remove desugaring when upgrading it to 26.
  const val minSdk = 26

  // Workflow requires minSDK 26
  const val minSdkWorkflow = 26
}
