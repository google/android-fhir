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
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.sync.Result.Success
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import net.sf.saxon.event.Sender.send

/** A WorkManager Worker that handles periodic sync. */
abstract class PeriodicSyncWorker(appContext: Context, workerParams: WorkerParameters) :
  CoroutineWorker(appContext, workerParams) {

  private lateinit var fhirSynchronizer: FhirSynchronizer

  abstract fun getFhirEngine(): FhirEngine
  abstract fun getDataSource(): DataSource
  abstract fun getSyncData(): ResourceSyncParams

  init {
    fhirSynchronizer = FhirSynchronizer(getFhirEngine(), getDataSource(), getSyncData())
  }

  // worker job, initiated by WorkManager in background,
  // and application initiated this does not have any reference to this,
  // and can not get any reference from work manager,
  // so unless there is any static/companion singleton there is no way to access data without
  // WManager
  override suspend fun doWork(): Result {
    // TODO handle retry

    val flow = MutableSharedFlow<State>()

    // collect is suspend and hence can not be called without this async
    val job =
      // is it correct to use async in this fashion ???
      GlobalScope.async {
        flow.collect {

          // only here we can subscribe and emit Progress via WorkManager
          // now send Progress to work manager so caller app can listen
          setProgress(
            workDataOf(
              "StateType" to it::class.java.name, // only way of knowing what class is
              "State" to Gson().toJson(it) // we can convert it back to state of above type
            )
          )

          // not cancelling blocks the await below
          // removing await just returns without collecting states completely
          if (it is State.Success || it is State.Error) {
            cancel() // what is better way to stop or kill flow ???
          }
        }
      }

    // is it right way to subscribe i.e. sending flow from here ???
    fhirSynchronizer.subscribe(flow)

    val result = fhirSynchronizer.synchronize()

    // cancel from flow collect above throws exception and not cancelling blocks this
    // removing await just returns without collecting states completely
    kotlin.runCatching { job.await() }

    if (result is Success) {
      return Result.success()
    }
    return Result.failure()
  }
}
