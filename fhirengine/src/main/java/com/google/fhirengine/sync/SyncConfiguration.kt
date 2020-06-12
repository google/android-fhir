/*
 * Copyright 2020 Google LLC
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

package com.google.fhirengine.sync

import androidx.work.Constraints

data class SyncConfiguration(
  /**
   *  Data that needs to be synchronised
   */
  val syncData: List<SyncData> = emptyList(),
    // using WorkManager constraints here until we decide if we want to write our own
  val syncConstraints: Constraints = Constraints.Builder().build(),
  /**
   *  true if the SDK needs to retry a failed sync attempt, false otherwise
   *  If this is set to true, then the result of the sync will be reported after the retry.
   */
  val retry: Boolean = false,

  val dataSource: FhirDataSource
)
