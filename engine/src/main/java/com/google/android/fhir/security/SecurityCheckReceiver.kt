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

package com.google.android.fhir.security

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import timber.log.Timber

/** A receiver to kick off security check periodically. */
class SecurityCheckReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    Timber.i("Schedule periodic work for checking security requirement.")
    WorkManager.getInstance(context)
      .enqueueUniquePeriodicWork(
        SECURITY_CHECK_WORKER_NAME,
        ExistingPeriodicWorkPolicy.REPLACE,
        PERIODIC_REQUEST
      )
  }

  companion object {
    const val SECURITY_CHECK_WORKER_NAME =
      "com.google.android.fhir.security.FhirSecurityRequirementsCheck"
    private val PERIODIC_REQUEST =
      PeriodicWorkRequestBuilder<SecurityCheckWorker>(1, TimeUnit.DAYS)
        .setInitialDelay(10, TimeUnit.SECONDS)
        .build()
  }
}
