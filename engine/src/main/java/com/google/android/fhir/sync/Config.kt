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

package com.google.android.fhir.sync

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

/**
 * Class that holds what type of resources we need to synchronise and what are the parameters of
 * that type. e.g. we only want to synchronise patients that live in United States
 * `ResourceSyncParams(ResourceType.Patient, mapOf("address-country" to "United States")`
 */
typealias ParamMap = Map<String, String>

/** Constant for the max number of retries in case of sync failure */
@PublishedApi internal const val MAX_RETRIES_ALLOWED = "max_retires"

/** Constant for the Greater Than Search Prefix */
@PublishedApi internal const val GREATER_THAN_PREFIX = "gt"

/** Constant for the default number of resource entries in a singe Bundle for upload. */
const val DEFAULT_BUNDLE_SIZE = 500

val defaultRetryConfiguration =
  RetryConfiguration(BackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.SECONDS), 3)

object SyncDataParams {
  const val SORT_KEY = "_sort"
  const val LAST_UPDATED_KEY = "_lastUpdated"
  const val ADDRESS_COUNTRY_KEY = "address-country"
  const val SUMMARY_KEY = "_summary"
  const val SUMMARY_COUNT_VALUE = "count"
}

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

/**
 * Configuration for max number of resources to be uploaded in a Bundle.The default size is
 * [DEFAULT_BUNDLE_SIZE]. The application developer may also configure if the eTag should be used
 * for edit and delete requests during the upload. Default is to use the eTag.
 */
data class UploadConfiguration(
  /**
   * Number of [Resource]s to be added in a singe [Bundle] for upload and default is
   * [DEFAULT_BUNDLE_SIZE]
   */
  val uploadBundleSize: Int = DEFAULT_BUNDLE_SIZE,

  /**
   * Use if-match http header with e-tag for upload requests. See ETag
   * [section](https://hl7.org/fhir/http.html#Http-Headers) for more details.
   */
  val useETagForUpload: Boolean = true,
)
