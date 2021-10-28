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

package com.google.android.fhir.reference

import android.app.Application
import android.text.format.DateFormat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.work.Constraints
import com.google.android.fhir.reference.data.FhirPeriodicSyncWorker
import com.google.android.fhir.sync.PeriodicSyncConfiguration
import com.google.android.fhir.sync.RepeatInterval
import com.google.android.fhir.sync.State
import com.google.android.fhir.sync.Sync
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.Flow

/** View model for [MainActivity]. */
class MainActivityViewModel(application: Application, private val state: SavedStateHandle) :
  AndroidViewModel(application) {
  private val _lastSyncLiveData = MutableLiveData<String>()

  val lastSyncLiveData: LiveData<String>
    get() = _lastSyncLiveData

  private val job = Sync.basicSyncJob(application.applicationContext)

  /** Requests periodic sync. */
  fun poll(): Flow<State> {
    return job.poll(
      PeriodicSyncConfiguration(
        syncConstraints = Constraints.Builder().build(),
        repeat = RepeatInterval(interval = 15, timeUnit = TimeUnit.MINUTES)
      ),
      FhirPeriodicSyncWorker::class.java
    )
  }

  /** Emits last sync time. */
  fun getLastSyncTime() {
    val formatter =
      DateTimeFormatter.ofPattern(
        if (DateFormat.is24HourFormat(getApplication())) formatString24 else formatString12
      )
    _lastSyncLiveData.value = job.lastSyncTimestamp()?.toLocalDateTime()?.format(formatter) ?: ""
  }

  companion object {
    private const val formatString24 = "yyyy-MM-dd HH:mm:ss"
    private const val formatString12 = "yyyy-MM-dd HH:mm:ss a"
  }
}
