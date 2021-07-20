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
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.sync.Result.Success
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable.cancel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/** A WorkManager Worker that handles periodic sync. */
abstract class FhirSyncWorker(appContext: Context, workerParams: WorkerParameters) :
  CoroutineWorker(appContext, workerParams) {

  abstract fun getFhirEngine(): FhirEngine
  abstract fun getDataSource(): DataSource
  abstract fun getSyncData(): ResourceSyncParams

  private var fhirSynchronizer: FhirSynchronizer =
    FhirSynchronizer(getFhirEngine(), getDataSource(), getSyncData())

  // worker job, initiated by WorkManager in background,
  // and application initiated this does not have any reference to this,
  // and can not get any reference from work manager,
  // so unless there is any static/companion singleton there is no way to access data without
  // WManager
  override suspend fun doWork(): Result {
    val flow = MutableSharedFlow<State>()

    val job =
      CoroutineScope(Dispatchers.Default).launch {
        flow.collect {
          // only here we can subscribe and emit Progress via WorkManager
          // now send Progress to work manager so caller app can listen
          setProgress(
            workDataOf(
              "StateType" to it::class.java.name, // only way of knowing what class is
              "State" to Gson().toJson(it) // we can convert it back to state of above type
            )
          )

          if (it is State.Success || it is State.Error) {
            this@launch.cancel()
          }
        }
      }

    fhirSynchronizer.subscribe(flow)

    val result = fhirSynchronizer.synchronize()

    // removing await/join just returns without collecting states completely
    kotlin.runCatching { job.join() }.onFailure { Log.w(javaClass.name, it) }

    /**
     * In case of failure, we can check if its worth retrying and do retry based on
     * [RetryConfiguration.maxRetries] set by user.
     */
    val retries = inputData.getInt(MAX_RETRIES_ALLOWED, 0)
    return when {
      result is Success -> {
        Result.success()
      }
      retries > runAttemptCount -> {
        Result.retry()
      }
      else -> {
        Result.failure()
      }
    }
  }
}
