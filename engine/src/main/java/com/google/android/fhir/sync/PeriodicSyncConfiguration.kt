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

package com.google.android.fhir.sync

import androidx.work.Constraints
import java.util.concurrent.TimeUnit

/** Configuration for period synchronisation */
class PeriodicSyncConfiguration(
  val syncConfiguration: SyncConfiguration,
  /**
   * Constraints that specify the requirements needed before the synchronisation is triggered. E.g.
   * network type (Wifi, 3G etc), the device should be charging etc.
   */
  val syncConstraints: Constraints = Constraints.Builder().build(),

  /** Worker that will execute the periodic sync */
  val periodicSyncWorker: Class<out PeriodicSyncWorker>,

  /** The interval at which the sync should be triggered in */
  val repeat: RepeatInterval
)

data class RepeatInterval(
  /** The interval at which the sync should be triggered in */
  val interval: Long,

  /** The time unit for the repeat interval */
  val timeUnit: TimeUnit
)
