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

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.fhir.FhirEngine

object Sync {
  suspend fun oneTimeSync(
    fhirEngine: FhirEngine,
    dataSource: DataSource,
    resourceSyncParams: ResourceSyncParams
  ): Result {
    return FhirSynchronizer(fhirEngine, dataSource, resourceSyncParams).download()
  }

  inline fun <reified W : PeriodicSyncWorker> periodicSync(
    context: Context,
    periodicSyncConfiguration: PeriodicSyncConfiguration
  ) {
    val periodicWorkRequest =
      PeriodicWorkRequestBuilder<W>(
          periodicSyncConfiguration.repeat.interval,
          periodicSyncConfiguration.repeat.timeUnit
        )
        .setConstraints(periodicSyncConfiguration.syncConstraints)
        .build()
    WorkManager.getInstance(context)
      .enqueueUniquePeriodicWork(
        SyncWorkType.DOWNLOAD.workerName,
        ExistingPeriodicWorkPolicy.KEEP,
        periodicWorkRequest
      )
  }
}

/** Defines different types of synchronisation workers: download and upload */
enum class SyncWorkType(val workerName: String) {
  DOWNLOAD("download"),
  UPLOAD("upload")
}
