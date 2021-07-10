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

import android.util.Log
import com.google.android.fhir.FhirEngine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn

class SyncJobImpl(
  private val dispatcher: CoroutineDispatcher,
  fhirEngine: FhirEngine,
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
  override fun poll(delay: Long): Flow<Result> {
    return poll(delay, null)
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
  override fun subscribe(): StateFlow<Result> {
    return fhirSynchronizer.state
  }

  override fun close() {//todo name?
    dispatcher.cancel()
  }
}
