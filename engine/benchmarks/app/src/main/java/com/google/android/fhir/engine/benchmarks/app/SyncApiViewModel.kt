/*
 * Copyright 2025 Google LLC
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

package com.google.android.fhir.engine.benchmarks.app

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.engine.benchmarks.app.data.BundleUploadFhirSyncWorker
import com.google.android.fhir.engine.benchmarks.app.data.DownloadFhirSyncWorker
import com.google.android.fhir.engine.benchmarks.app.data.PerResourceUploadFhirSyncWorker
import com.google.android.fhir.engine.benchmarks.app.data.ResourcesDataProvider
import com.google.android.fhir.engine.benchmarks.app.data.SYNC_BUNDLE_UPLOAD_BENCHMARK_BROADCAST_ACTION
import com.google.android.fhir.engine.benchmarks.app.data.SYNC_DOWNLOAD_BENCHMARK_BROADCAST_ACTION
import com.google.android.fhir.engine.benchmarks.app.data.SYNC_INDIVIDUAL_UPLOAD_BENCHMARK_BROADCAST_ACTION
import com.google.android.fhir.sync.CurrentSyncJobStatus
import com.google.android.fhir.sync.CurrentSyncJobStatus.Cancelled
import com.google.android.fhir.sync.CurrentSyncJobStatus.Failed
import com.google.android.fhir.sync.CurrentSyncJobStatus.Running
import com.google.android.fhir.sync.CurrentSyncJobStatus.Succeeded
import com.google.android.fhir.sync.Sync
import com.google.android.fhir.sync.SyncJobStatus
import com.google.android.fhir.sync.SyncOperation
import com.google.android.fhir.sync.defaultRetryConfiguration
import kotlin.time.Duration
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
internal class SyncApiViewModel(
  application: Application,
  private val resourcesDataProvider: ResourcesDataProvider,
  private val fhirEngine: FhirEngine,
) : AndroidViewModel(application) {

  private val _downloadBenchmarkSyncMutableStateFlow = MutableStateFlow(BenchmarkSyncState())
  val downloadBenchmarkSyncStateFlow = _downloadBenchmarkSyncMutableStateFlow.asStateFlow()

  private val _bundleUploadBenchmarkSyncMutableStateFlow = MutableStateFlow(BenchmarkSyncState())
  val bundleUploadBenchmarkSyncStateFlow = _bundleUploadBenchmarkSyncMutableStateFlow.asStateFlow()

  private val _perResourceChangeUploadBenchmarkSyncMutableStateFlow =
    MutableStateFlow(BenchmarkSyncState())
  val perResourceChangeUploadBenchmarkSyncStateFlow =
    _perResourceChangeUploadBenchmarkSyncMutableStateFlow.asStateFlow()

  private val syncBenchmarkBroadcastReceiver =
    object : BroadcastReceiver() {
      override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
          SYNC_DOWNLOAD_BENCHMARK_BROADCAST_ACTION -> {
            val benchmarkDuration = Duration.parse(intent.getStringExtra("benchmarkDuration")!!)
            _downloadBenchmarkSyncMutableStateFlow.update {
              it.copy(benchmarkDuration = benchmarkDuration)
            }
          }
          SYNC_BUNDLE_UPLOAD_BENCHMARK_BROADCAST_ACTION -> {
            val benchmarkDuration = Duration.parse(intent.getStringExtra("benchmarkDuration")!!)
            _bundleUploadBenchmarkSyncMutableStateFlow.update {
              it.copy(benchmarkDuration = benchmarkDuration)
            }
          }
          SYNC_INDIVIDUAL_UPLOAD_BENCHMARK_BROADCAST_ACTION -> {
            val benchmarkDuration = Duration.parse(intent.getStringExtra("benchmarkDuration")!!)
            _perResourceChangeUploadBenchmarkSyncMutableStateFlow.update {
              it.copy(benchmarkDuration = benchmarkDuration)
            }
          }
        }
      }
    }

  init {
    registerSyncBroadcastReceiver()
    val downloadSyncCompletableDeferred = CompletableDeferred<Boolean>()

    viewModelScope.launch(benchmarkingViewModelWorkDispatcher) {
      Sync.oneTimeSync<DownloadFhirSyncWorker>(
          getApplication(),
          defaultRetryConfiguration.copy(maxRetries = 0),
          ExistingWorkPolicy.REPLACE,
        )
        .collect { currentSyncJobStatus ->
          _downloadBenchmarkSyncMutableStateFlow.update {
            it.updateWith(currentSyncJobStatus, SyncOperation.DOWNLOAD)
          }

          if (currentSyncJobStatus.isCompleted()) {
            downloadSyncCompletableDeferred.complete(currentSyncJobStatus is Succeeded)
          }
        }
    }

    val bundleUploadCompletableDeferred = CompletableDeferred<Boolean>()
    viewModelScope.launch(benchmarkingViewModelWorkDispatcher) {
      downloadSyncCompletableDeferred.await()
      Sync.oneTimeSync<BundleUploadFhirSyncWorker>(
          getApplication(),
          defaultRetryConfiguration.copy(maxRetries = 0),
          ExistingWorkPolicy.REPLACE,
        )
        .collect { currentSyncJobStatus ->
          _bundleUploadBenchmarkSyncMutableStateFlow.update {
            it.updateWith(currentSyncJobStatus, SyncOperation.UPLOAD)
          }

          if (currentSyncJobStatus.isCompleted()) {
            bundleUploadCompletableDeferred.complete(currentSyncJobStatus is Succeeded)
          }
          println(currentSyncJobStatus)
        }
    }

    viewModelScope.launch(benchmarkingViewModelWorkDispatcher) {
      bundleUploadCompletableDeferred.await()
      Sync.oneTimeSync<PerResourceUploadFhirSyncWorker>(
          getApplication(),
          defaultRetryConfiguration.copy(maxRetries = 0),
          ExistingWorkPolicy.REPLACE,
        )
        .collect { currentSyncJobStatus ->
          _perResourceChangeUploadBenchmarkSyncMutableStateFlow.update {
            it.updateWith(currentSyncJobStatus, SyncOperation.UPLOAD)
          }
          println(currentSyncJobStatus)
        }
    }
  }

  override fun onCleared() {
    cancelWorkers()
    unRegisterSyncBroadcastReceiver()
    super.onCleared()
  }

  private fun cancelWorkers() {
    viewModelScope.launch(benchmarkingViewModelWorkDispatcher) {
      Sync.cancelOneTimeSync<PerResourceUploadFhirSyncWorker>(getApplication())
      Sync.cancelOneTimeSync<BundleUploadFhirSyncWorker>(getApplication())
      Sync.cancelOneTimeSync<DownloadFhirSyncWorker>(getApplication())
    }
  }

  private fun registerSyncBroadcastReceiver() {
    ContextCompat.registerReceiver(
      getApplication(),
      syncBenchmarkBroadcastReceiver,
      IntentFilter(SYNC_DOWNLOAD_BENCHMARK_BROADCAST_ACTION),
      ContextCompat.RECEIVER_NOT_EXPORTED,
    )
    ContextCompat.registerReceiver(
      getApplication(),
      syncBenchmarkBroadcastReceiver,
      IntentFilter(SYNC_BUNDLE_UPLOAD_BENCHMARK_BROADCAST_ACTION),
      ContextCompat.RECEIVER_NOT_EXPORTED,
    )
    ContextCompat.registerReceiver(
      getApplication(),
      syncBenchmarkBroadcastReceiver,
      IntentFilter(SYNC_INDIVIDUAL_UPLOAD_BENCHMARK_BROADCAST_ACTION),
      ContextCompat.RECEIVER_NOT_EXPORTED,
    )
  }

  private fun unRegisterSyncBroadcastReceiver() {
    (getApplication() as Context).unregisterReceiver(syncBenchmarkBroadcastReceiver)
  }
}

internal data class BenchmarkSyncState(
  val syncStatus: CurrentSyncJobStatus = Cancelled,
  val completedResources: Int = 0,
  val benchmarkDuration: Duration = Duration.ZERO,
) {
  val isComplete = syncStatus.isCompleted()
}

internal fun BenchmarkSyncState.updateWith(
  currentSyncJobStatus: CurrentSyncJobStatus,
  operation: SyncOperation,
) =
  when {
    currentSyncJobStatus is Running &&
      currentSyncJobStatus.inProgressSyncJob is SyncJobStatus.InProgress -> {
      val syncJobStatus = currentSyncJobStatus.inProgressSyncJob as SyncJobStatus.InProgress
      if (syncJobStatus.syncOperation == operation) {
        copy(
          syncStatus = currentSyncJobStatus,
          completedResources = syncJobStatus.completed,
        )
      } else {
        copy()
      }
    }
    else -> copy(syncStatus = currentSyncJobStatus)
  }

internal fun CurrentSyncJobStatus.isCompleted() = this is Succeeded || this is Failed
