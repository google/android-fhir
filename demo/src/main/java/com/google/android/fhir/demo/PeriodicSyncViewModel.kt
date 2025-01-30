/*
 * Copyright 2024-2025 Google LLC
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class PeriodicSyncViewModel(application: Application) : AndroidViewModel(application) {

  private val _uiStateFlow = MutableStateFlow(PeriodicSyncUiState())
  val uiStateFlow: StateFlow<PeriodicSyncUiState> = _uiStateFlow

  private val _pollPeriodicSyncJobStatus = MutableSharedFlow<PeriodicSyncJobStatus>(replay = 10)

  init {
    viewModelScope.launch { initializePeriodicSync() }
  }

  private suspend fun initializePeriodicSync() {
    val periodicSyncJobStatusFlow =
      Sync.periodicSync<DemoFhirSyncWorker>(
        context = getApplication<Application>().applicationContext,
        periodicSyncConfiguration =
          PeriodicSyncConfiguration(
            syncConstraints = Constraints.Builder().build(),
            repeat = RepeatInterval(interval = 15, timeUnit = TimeUnit.MINUTES),
          ),
      )

    periodicSyncJobStatusFlow.collect { status -> _pollPeriodicSyncJobStatus.emit(status) }
  }

  fun collectPeriodicSyncJobStatus() {
    viewModelScope.launch {
      _pollPeriodicSyncJobStatus.collect { periodicSyncJobStatus ->
        Timber.d(
          "currentSyncJobStatus: ${periodicSyncJobStatus.currentSyncJobStatus}  lastSyncJobStatus ${periodicSyncJobStatus.lastSyncJobStatus}",
        )
        val lastSyncStatus = getLastSyncStatus(periodicSyncJobStatus.lastSyncJobStatus)
        val lastSyncTime = getLastSyncTime(periodicSyncJobStatus.lastSyncJobStatus)
        val currentSyncStatus =
          getApplication<FhirApplication>()
            .getString(
              R.string.current_status,
              periodicSyncJobStatus.currentSyncJobStatus::class.java.simpleName,
            )
        val progress = getProgress(periodicSyncJobStatus.currentSyncJobStatus)

        // Update the UI state
        _uiStateFlow.value =
          _uiStateFlow.value.copy(
            lastSyncStatus = lastSyncStatus,
            lastSyncTime = lastSyncTime,
            currentSyncStatus = currentSyncStatus,
            progress = progress,
          )
      }
    }
  }

  fun cancelPeriodicSyncJob() {
    viewModelScope.launch {
      Sync.cancelPeriodicSync<DemoFhirSyncWorker>(
        getApplication<FhirApplication>().applicationContext,
      )
    }
  }

  private fun getLastSyncStatus(lastSyncJobStatus: LastSyncJobStatus?): String? {
    return when (lastSyncJobStatus) {
      is LastSyncJobStatus.Succeeded ->
        getApplication<FhirApplication>()
          .getString(
            R.string.last_sync_status,
            LastSyncJobStatus.Succeeded::class.java.simpleName,
          )
      is LastSyncJobStatus.Failed ->
        getApplication<FhirApplication>()
          .getString(R.string.last_sync_status, LastSyncJobStatus.Failed::class.java.simpleName)
      else -> getApplication<FhirApplication>().getString(R.string.last_sync_status_na)
    }
  }

  private fun getLastSyncTime(lastSyncJobStatus: LastSyncJobStatus?): String {
    val applicationContext = getApplication<FhirApplication>()
    return lastSyncJobStatus?.let { status ->
      applicationContext.getString(
        R.string.last_sync_timestamp,
        status.timestamp.formatSyncTimestamp(applicationContext),
      )
    }
      ?: applicationContext.getString(R.string.last_sync_status_na)
  }

  private fun getProgress(currentSyncJobStatus: CurrentSyncJobStatus): Int? {
    val inProgressSyncJob =
      (currentSyncJobStatus as? CurrentSyncJobStatus.Running)?.inProgressSyncJob
    return (inProgressSyncJob as? SyncJobStatus.InProgress)?.let {
      ProgressHelper.calculateProgressPercentage(it.total, it.completed)
    }
  }
}

data class PeriodicSyncUiState(
  val lastSyncStatus: String? = null,
  val lastSyncTime: String? = null,
  val currentSyncStatus: String? = null,
  val progress: Int? = null,
)
