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
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.asFlow
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.fhir.FhirEngine
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class SyncJobImpl(
  private val fhirEngine: FhirEngine,
  private val dataSource: DataSource,
  private val resourceSyncParams: ResourceSyncParams
) : SyncJob {
  private val TAG = javaClass.name

  /** Periodically sync the data with given configuration for given worker class */
  @ExperimentalCoroutinesApi
  override fun <W : PeriodicSyncWorker> poll(
    periodicSyncConfiguration: PeriodicSyncConfiguration,
    context: Context,
    clazz: Class<W>
  ): Flow<MutableList<WorkInfo>> {
    Log.i(TAG, "Initiating polling")

    val periodicWorkRequest =
      PeriodicWorkRequest.Builder(
          clazz,
          periodicSyncConfiguration.repeat.interval,
          periodicSyncConfiguration.repeat.timeUnit
        )
        .setConstraints(periodicSyncConfiguration.syncConstraints)
        .build()

    val workerUniqueName = SyncWorkType.DOWNLOAD_UPLOAD.workerName
    val workManager = WorkManager.getInstance(context)

    // Return LiveData as flow
    val flow = workManager.getWorkInfosForUniqueWorkLiveData(workerUniqueName).asFlow()
     // .map { convertToState(it) }

    // now we lost track of job, do not have,
    // and can not have an instance of fhir-synchronizer
    // can not subscribe to any flow here
    // that's why it emits progress inside doWork of worker

    workManager.enqueueUniquePeriodicWork(
        workerUniqueName,
        ExistingPeriodicWorkPolicy.KEEP,
        periodicWorkRequest
      )

    return flow
  }

  override fun stateFlowFor(uniqueWorkerName: String, context: Context): Flow<State> {
    return WorkManager.getInstance(context).getWorkInfosForUniqueWorkLiveData(uniqueWorkerName)
      .asFlow()
      .mapNotNull { convertToState(it) }
  }

  override fun workInfoFlowFor(uniqueWorkerName: String, context: Context): Flow<WorkInfo> {
    return WorkManager.getInstance(context).getWorkInfosForUniqueWorkLiveData(uniqueWorkerName)
      .asFlow()
      .mapNotNull { if (it.isEmpty()) null else it[0] } // todo its always 0 ... would it be??
  }

  private fun convertToState(workInfos: MutableList<WorkInfo>): State? {
    for (wi in workInfos) {
      if(wi.state != WorkInfo.State.ENQUEUED && wi.progress.keyValueMap.isNotEmpty()){
        val state = wi.progress.getString("StateType")!!
        val stateData = wi.progress.getString("State")!!

        val data: State = Gson().fromJson(stateData, Class.forName(state)) as State

        return data
      }
    }
    return null
  }

  /**
   * Run fhir synchronizer immediately with default sync params configured on initialization and
   * subscribe to given flow
   */
  override suspend fun run(subscribeTo: MutableSharedFlow<State>?): Result {
    val fhirSynchronizer = FhirSynchronizer(fhirEngine, dataSource, resourceSyncParams)

    if (subscribeTo != null) fhirSynchronizer.subscribe(subscribeTo)

    return fhirSynchronizer.synchronize()
  }
}
