/*
 * Copyright 2023-2026 Google LLC
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

import io.ktor.http.encodeURLQueryComponent
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Class that holds what type of resources we need to synchronise and what are the parameters of
 * that type. e.g. we only want to synchronise patients that live in United States
 * `ResourceSyncParams(ResourceType.Patient, mapOf("address-country" to "United States")`
 */
typealias ParamMap = Map<String, String>

/** Constant for the max number of retries in case of sync failure */
@PublishedApi internal const val MAX_RETRIES_ALLOWED = "max_retries"

/** Constant for the Greater Than Search Prefix */
@PublishedApi internal const val GREATER_THAN_PREFIX = "gt"

@PublishedApi internal const val UNIQUE_WORK_NAME = "unique_work_name"

val defaultRetryConfiguration =
  RetryConfiguration(BackoffCriteria(BackoffPolicy.LINEAR, 30.seconds), 3)

object SyncDataParams {
  const val SORT_KEY = "_sort"
  const val LAST_UPDATED_KEY = "_lastUpdated"
  const val SUMMARY_KEY = "_summary"
  const val SUMMARY_COUNT_VALUE = "count"
}

enum class NetworkType {
  NOT_REQUIRED,
  CONNECTED,
  UNMETERED,
  NOT_ROAMING,
  METERED,
}

data class SyncConstraints(
  val requiredNetworkType: NetworkType = NetworkType.CONNECTED,
  val requiresBatteryNotLow: Boolean = false,
  val requiresCharging: Boolean = false,
  val requiresDeviceIdle: Boolean = false,
  val requiresStorageNotLow: Boolean = false,
)

/** Configuration for period synchronisation */
class PeriodicSyncConfiguration(
  /**
   * Constraints that specify the requirements needed before the synchronization is triggered. E.g.
   * network type (Wi-Fi, 3G etc.), the device should be charging etc.
   */
  val syncConstraints: SyncConstraints = SyncConstraints(),

  /** The interval at which the sync should be triggered in. */
  val repeat: RepeatInterval,

  /** Configuration for synchronization retry */
  val retryConfiguration: RetryConfiguration? = defaultRetryConfiguration,
)

data class RepeatInterval(
  /** The interval at which the sync should be triggered in */
  val interval: Duration,
)

fun ParamMap.concatParams(): String {
  return this.entries.joinToString("&") { (key, value) ->
    "$key=${value.encodeURLQueryComponent()}"
  }
}

/** Configuration for synchronization retry */
data class RetryConfiguration(
  /** The criteria to retry failed synchronization work. */
  val backoffCriteria: BackoffCriteria,

  /** Maximum retries for a failing sync worker */
  val maxRetries: Int,
)

enum class BackoffPolicy {
  EXPONENTIAL,
  LINEAR,
}

/** The criteria for sync worker failure retry. */
data class BackoffCriteria(
  /** Backoff policy */
  val backoffPolicy: BackoffPolicy,

  /** Backoff delay for each retry attempt. */
  val backoffDelay: Duration,
)
