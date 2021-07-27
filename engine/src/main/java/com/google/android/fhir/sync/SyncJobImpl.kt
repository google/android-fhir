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
import android.util.Log
import androidx.lifecycle.asFlow
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.fhir.FhirEngine
import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.mapNotNull

class SyncJobImpl(
  private val context: Context,
  private val fhirEngine: FhirEngine,
  private val dataSource: DataSource,
  private val resourceSyncParams: ResourceSyncParams
) : SyncJob {
  private val TAG = javaClass.name
  private val syncWorkType = SyncWorkType.DOWNLOAD_UPLOAD

  /** Periodically sync the data with given configuration for given worker class */
  @ExperimentalCoroutinesApi
  override fun <W : FhirSyncWorker> poll(
    periodicSyncConfiguration: PeriodicSyncConfiguration,
    clazz: Class<W>
  ): Flow<WorkInfo> {
    Log.i(TAG, "Initiating polling")

    val periodicWorkRequest =
      PeriodicWorkRequest.Builder(
          clazz,
          periodicSyncConfiguration.repeat.interval,
          periodicSyncConfiguration.repeat.timeUnit
        )
        .setConstraints(periodicSyncConfiguration.syncConstraints)
        .build()

    val workerUniqueName = syncWorkType.workerName
    val workManager = WorkManager.getInstance(context)

    val flow = workInfoFlow()

    workManager.enqueueUniquePeriodicWork(
      workerUniqueName,
      ExistingPeriodicWorkPolicy.KEEP,
      periodicWorkRequest
    )

    return flow
  }

  override fun stateFlow(): Flow<State> {
    return workInfoFlow().mapNotNull { convertToState(it) }
  }

  override fun workInfoFlow(): Flow<WorkInfo> {
    return WorkManager.getInstance(context)
      .getWorkInfosForUniqueWorkLiveData(syncWorkType.workerName)
      .asFlow()
      .flatMapConcat { it.asFlow() }
      .mapNotNull { it }
  }

  private fun convertToState(workInfo: WorkInfo): State? {
    return workInfo
      .takeIf { it.state != WorkInfo.State.ENQUEUED && it.progress.keyValueMap.isNotEmpty() }
      ?.let {
        val state = workInfo.progress.getString("StateType")!!
        val stateData = workInfo.progress.getString("State")!!
        Gson().fromJson(stateData, Class.forName(state)) as State
      }
  }

  /**
   * Run fhir synchronizer immediately with default sync params configured on initialization and
   * subscribe to given flow
   */
  override suspend fun run(subscribeTo: MutableSharedFlow<State>?): Result {
    val fhirSynchronizer = FhirSynchronizer(context, fhirEngine, dataSource, resourceSyncParams)

    if (subscribeTo != null) fhirSynchronizer.subscribe(subscribeTo)

    return fhirSynchronizer.synchronize()
  }
}
