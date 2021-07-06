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

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import org.hl7.fhir.r4.model.ResourceType

/**
 * Class that holds what type of resources we need to synchronise and what are the parameters of
 * that type. e.g. we only want to synchronise patients that live in United States
 * `ResourceSyncParams(ResourceType.Patient, mapOf("address-country" to "United States")`
 */
typealias ParamMap = Map<String, String>

typealias ResourceSyncParams = Map<ResourceType, ParamMap>
/** Constant for the max number of retries in case of sync failure */
@PublishedApi internal const val MAX_RETRIES_ALLOWED = "max_retires"

val defaultRetryConfiguration =
  RetryConfiguration(BackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.SECONDS), 3)

object SyncDataParams {
  const val SORT_KEY = "_sort"
  const val LAST_UPDATED_KEY = "_lastUpdated"
  const val ADDRESS_COUNTRY_KEY = "address-country"
  const val LAST_UPDATED_ASC_VALUE = "_lastUpdated"
}

/** Configuration for synchronization. */
data class SyncConfiguration(
  /** Data that needs to be synchronised */
  val resourceSyncParams: List<ResourceSyncParams> = emptyList(),
  /**
   * true if the SDK needs to retry a failed sync attempt, false otherwise If this is set to true,
   * then the result of the sync will be reported after the retry.
   */
  val retry: Boolean = false
)

/** Configuration for period synchronisation */
class PeriodicSyncConfiguration(
  /**
   * Constraints that specify the requirements needed before the synchronisation is triggered. E.g.
   * network type (Wifi, 3G etc), the device should be charging etc.
   */
  val syncConstraints: Constraints = Constraints.Builder().build(),

  /**
   * The interval at which the sync should be triggered in. It must be greater than or equal to
   * [androidx.work.PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS]
   */
  val repeat: RepeatInterval,

  /** Configuration for synchronization retry */
  val retryConfiguration: RetryConfiguration? = defaultRetryConfiguration
)

/** Repeat interval for periodic sync trigger. Check [androidx.work.PeriodicWorkRequest.Builder] */
data class RepeatInterval(
  /** The interval at which the sync should be triggered in */
  val interval: Long,

  /** The time unit for the repeat interval */
  val timeUnit: TimeUnit
)

fun ParamMap.concatParams(): String {
  return this.entries.joinToString("&") { (key, value) ->
    "$key=${URLEncoder.encode(value, "UTF-8")}"
  }
}

/** Configuration for synchronization retry */
data class RetryConfiguration(
  /**
   * The criteria to retry failed synchronization work based on
   * [androidx.work.WorkRequest.Builder.setBackoffCriteria]
   */
  val backoffCriteria: BackoffCriteria,

  /** Maximum retries for a failing [FhirSyncWorker] */
  val maxRetries: Int
)

/**
 * The criteria for [FhirSyncWorker] failure retry based on
 * [androidx.work.WorkRequest.Builder.setBackoffCriteria]
 */
data class BackoffCriteria(
  /** Backoff policy [androidx.work.BackoffPolicy] */
  val backoffPolicy: BackoffPolicy,

  /**
   * Backoff delay for each retry attempt. Check
   * [androidx.work.PeriodicWorkRequest.MIN_BACKOFF_MILLIS] and
   * [androidx.work.PeriodicWorkRequest.MAX_BACKOFF_MILLIS] for the min-max supported values
   */
  val backoffDelay: Long,

  /** The time unit for [backoffDelay] */
  val timeUnit: TimeUnit
)
