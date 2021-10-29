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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import com.google.android.fhir.reference.data.FhirPeriodicSyncWorker
import com.google.android.fhir.sync.PeriodicSyncConfiguration
import com.google.android.fhir.sync.RepeatInterval
import com.google.android.fhir.sync.State
import com.google.android.fhir.sync.Sync
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/** View model for [MainActivity]. */
class MainActivityViewModel(application: Application, private val state: SavedStateHandle) :
  AndroidViewModel(application) {
  val lastSyncTimestampLiveData = MutableLiveData<String>()
  private val job = Sync.basicSyncJob(application.applicationContext)
  private val _pollState = MutableSharedFlow<State>()
  val pollState: Flow<State>
    get() = _pollState

  init {
    poll()
  }

  /** Requests periodic sync. */
  fun poll() {
    viewModelScope.launch {
      job.poll(
          PeriodicSyncConfiguration(
            syncConstraints = Constraints.Builder().build(),
            repeat = RepeatInterval(interval = 15, timeUnit = TimeUnit.MINUTES)
          ),
          FhirPeriodicSyncWorker::class.java
        )
        .collect { _pollState.emit(it) }
    }
  }

  /** Emits last sync time. */
  fun updateLastSyncTimestamp() {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    lastSyncTimestampLiveData.value =
      job.lastSyncTimestamp()?.toLocalDateTime()?.format(formatter) ?: ""
  }
}
