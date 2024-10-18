/*
 * Copyright 2024 Google LLC
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

package com.google.android.fhir.demo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import com.google.android.fhir.demo.data.DemoFhirSyncWorker
import com.google.android.fhir.demo.extensions.formatSyncTimestamp
import com.google.android.fhir.demo.helpers.ProgressHelper
import com.google.android.fhir.sync.CurrentSyncJobStatus
import com.google.android.fhir.sync.LastSyncJobStatus
import com.google.android.fhir.sync.PeriodicSyncConfiguration
import com.google.android.fhir.sync.PeriodicSyncJobStatus
import com.google.android.fhir.sync.RepeatInterval
import com.google.android.fhir.sync.Sync
import com.google.android.fhir.sync.SyncJobStatus
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class PeriodicSyncViewModel(application: Application) : AndroidViewModel(application) {

  val pollPeriodicSyncJobStatus: SharedFlow<PeriodicSyncJobStatus> =
    Sync.periodicSync<DemoFhirSyncWorker>(
        application.applicationContext,
        periodicSyncConfiguration =
          PeriodicSyncConfiguration(
            syncConstraints = Constraints.Builder().build(),
            repeat = RepeatInterval(interval = 15, timeUnit = TimeUnit.MINUTES),
          ),
      )
      .shareIn(viewModelScope, SharingStarted.Eagerly, 10)

  private val _lastSyncStatusFlow = MutableStateFlow<String?>(null)
  val lastSyncStatusFlow: StateFlow<String?> = _lastSyncStatusFlow

  private val _lastSyncTimeFlow = MutableStateFlow<String?>(null)
  val lastSyncTimeFlow: StateFlow<String?> = _lastSyncTimeFlow

  private val _currentSyncStatusFlow = MutableStateFlow<String?>(null)
  val currentSyncStatusFlow: StateFlow<String?> = _currentSyncStatusFlow

  private val _progressFlow = MutableStateFlow<Int?>(null)
  val progressFlow: StateFlow<Int?> = _progressFlow

  init {
    observePeriodicSyncJobStatus()
  }

  private fun observePeriodicSyncJobStatus() {
    viewModelScope.launch {
      pollPeriodicSyncJobStatus.collect { periodicSyncJobStatus ->
        updateLastSyncJobStatusUi(periodicSyncJobStatus.lastSyncJobStatus)

        // refresh current sync status ui
        _currentSyncStatusFlow.value =
          getApplication<FhirApplication>()
            .getString(
              R.string.current_status,
              periodicSyncJobStatus.currentSyncJobStatus::class.java.simpleName,
            )

        // refresh progress ui based on the current sync job status
        if (periodicSyncJobStatus.currentSyncJobStatus is CurrentSyncJobStatus.Running) {
          updateProgressUiForRunningSync(
            (periodicSyncJobStatus.currentSyncJobStatus as CurrentSyncJobStatus.Running)
              .inProgressSyncJob,
          )
        } else {
          _progressFlow.value = null
        }
      }
    }
  }

  private fun updateLastSyncJobStatusUi(lastSyncJobStatus: LastSyncJobStatus?) {
    lastSyncJobStatus?.let {
      // refresh last sync status ui
      _lastSyncStatusFlow.value =
        when (it) {
          is LastSyncJobStatus.Succeeded ->
            getApplication<FhirApplication>()
              .getString(
                R.string.last_sync_status,
                LastSyncJobStatus.Succeeded::class.java.simpleName,
              )
          is LastSyncJobStatus.Failed ->
            getApplication<FhirApplication>()
              .getString(R.string.last_sync_status, LastSyncJobStatus.Failed::class.java.simpleName)
          else -> null
        }
      // refresh last sync time ui
      _lastSyncTimeFlow.value =
        getApplication<FhirApplication>()
          .getString(
            R.string.last_sync_timestamp,
            it.timestamp.formatSyncTimestamp(getApplication()),
          )
    }
  }

  private fun updateProgressUiForRunningSync(inProgressSyncJob: SyncJobStatus) {
    if (inProgressSyncJob is SyncJobStatus.InProgress) {
      val progressPercentage =
        ProgressHelper.calculateProgressPercentage(
          inProgressSyncJob.total,
          inProgressSyncJob.completed,
        )
      _progressFlow.value = progressPercentage
    } else {
      _progressFlow.value = null
    }
  }
}
