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
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.fhir.FhirEngine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

class SyncJobImpl(
  private val dispatcher: CoroutineDispatcher,
  private val fhirEngine: FhirEngine,
  dataSource: DataSource,
  resourceSyncParams: ResourceSyncParams) : SyncJob {
  private val TAG = javaClass.name

  private var fhirSynchronizer: FhirSynchronizer =
    FhirSynchronizer(fhirEngine, dataSource, resourceSyncParams)

  @ExperimentalCoroutinesApi
  override fun poll(delay: Long, initialDelay: Long?): Flow<Result> {
    Log.i(TAG, "Initiating polling")

    return channelFlow {
        if (initialDelay != null && initialDelay > 0) {
          Log.i(TAG, "Injecting a delay of $initialDelay millis")

          delay(initialDelay)
        }

        while (!isClosedForSend) {
          Log.i(TAG, "Running channel flow")

          val result = Result.Success // todo//repository.getData()
          send(result)
          delay(delay)
        }
      }
      .flowOn(dispatcher)
  }

  @ExperimentalCoroutinesApi
  override suspend fun <W : PeriodicSyncWorker> poll(repeatInterval: RepeatInterval, context: Context, clazz: Class<W>) {
    val periodicWorkRequest = PeriodicWorkRequest
      .Builder(clazz, repeatInterval.interval, repeatInterval.timeUnit)
      .setConstraints(Constraints.Builder().build())
      .build()

    WorkManager.getInstance(context)
      .enqueueUniquePeriodicWork(
        SyncWorkType.DOWNLOAD.workerName,
        ExistingPeriodicWorkPolicy.KEEP,
        periodicWorkRequest
      )
  }

  /**
   * Run fhir synchronizer immediately with given sync params
   */
  override suspend fun run(resourceSyncParams: ResourceSyncParams): Result {
    return fhirSynchronizer.synchronize(resourceSyncParams)
  }

  /**
   * Run fhir synchronizer immediately with default sync params configured on initialization
   */
  override suspend fun run(): Result {
    return fhirSynchronizer.synchronize()
  }

  /**
   * Subscribe to updates on fhir synchronizer sync progress
   */
  override fun subscribe(): StateFlow<State> {
    return fhirSynchronizer.subscribe()
  }

  override fun close() {//todo name?
    dispatcher.cancel()
  }
}
