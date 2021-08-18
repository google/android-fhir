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
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.OffsetDateTimeTypeAdapter
import com.google.android.fhir.sync.Result.Error
import com.google.android.fhir.sync.Result.Success
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import java.time.OffsetDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/** A WorkManager Worker that handles periodic sync. */
abstract class FhirSyncWorker(appContext: Context, workerParams: WorkerParameters) :
  CoroutineWorker(appContext, workerParams) {
  private val TAG = javaClass.name

  abstract fun getFhirEngine(): FhirEngine
  abstract fun getDataSource(): DataSource
  abstract fun getSyncData(): ResourceSyncParams

  private val gson =
    GsonBuilder()
      .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeTypeAdapter().nullSafe())
      .setExclusionStrategies(StateExclusionStrategy())
      .create()
  private var fhirSynchronizer: FhirSynchronizer =
    FhirSynchronizer(appContext, getFhirEngine(), getDataSource(), getSyncData())

  override suspend fun doWork(): Result {
    val flow = MutableSharedFlow<State>()

    val job =
      CoroutineScope(Dispatchers.IO).launch {
        flow.collect {
          // now send Progress to work manager so caller app can listen
          setProgress(buildWorkData(it))

          if (it is State.Finished || it is State.Failed) {
            this@launch.cancel()
          }
        }
      }

    fhirSynchronizer.subscribe(flow)

    Log.v(TAG, "Subscribed to flow for progress")

    val result = fhirSynchronizer.synchronize()
    val output = buildOutput(result)

    // await/join is needed to collect states completely
    kotlin.runCatching { job.join() }.onFailure { Log.w(TAG, it) }

    setProgress(output)

    Log.d(TAG, "Received result from worker $result and sending output $output")

    /**
     * In case of failure, we can check if its worth retrying and do retry based on
     * [RetryConfiguration.maxRetries] set by user.
     */
    val retries = inputData.getInt(MAX_RETRIES_ALLOWED, 0)
    return when {
      result is Success -> {
        Result.success(output)
      }
      retries > runAttemptCount -> {
        Result.retry()
      }
      else -> {
        Result.failure(output)
      }
    }
  }

  private fun buildOutput(result: com.google.android.fhir.sync.Result): Data {
    return when (result) {
      is Success -> buildWorkData(State.Finished(result))
      is Error -> buildWorkData(State.Failed(result))
    }
  }

  private fun buildWorkData(state: State): Data {
    return workDataOf(
      // send serialized state and type so that consumer can convert it back
      "StateType" to state::class.java.name,
      "State" to gson.toJson(state)
    )
  }

  /**
   * Exclusion strategy for [Gson] that handles field exclusions for [State] returned by
   * FhirSynchronizer. It should skip serializing the exceptions to avoid exceeding WorkManager
   * WorkData limit
   * @see <a
   * href="https://github.com/google/android-fhir/issues/707">https://github.com/google/android-fhir/issues/707</a>
   */
  internal class StateExclusionStrategy : ExclusionStrategy {
    override fun shouldSkipField(field: FieldAttributes) = field.name.equals("exceptions")

    override fun shouldSkipClass(clazz: Class<*>?) = false
  }
}
