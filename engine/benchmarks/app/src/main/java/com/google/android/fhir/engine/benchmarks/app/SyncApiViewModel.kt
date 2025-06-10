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
import androidx.tracing.traceAsync
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkerParameters
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.engine.benchmarks.app.data.ResourcesDataProvider
import com.google.android.fhir.sync.AcceptLocalConflictResolver
import com.google.android.fhir.sync.ConflictResolver
import com.google.android.fhir.sync.CurrentSyncJobStatus
import com.google.android.fhir.sync.DownloadWorkManager
import com.google.android.fhir.sync.FhirSyncWorker
import com.google.android.fhir.sync.Sync
import com.google.android.fhir.sync.SyncJobStatus
import com.google.android.fhir.sync.SyncOperation
import com.google.android.fhir.sync.defaultRetryConfiguration
import com.google.android.fhir.sync.download.ResourceParamsBasedDownloadWorkManager
import com.google.android.fhir.sync.upload.HttpCreateMethod
import com.google.android.fhir.sync.upload.HttpUpdateMethod
import com.google.android.fhir.sync.upload.UploadStrategy
import kotlin.time.Duration
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType

@ExperimentalCoroutinesApi
internal class SyncApiViewModel(
  application: Application,
  private val resourcesDataProvider: ResourcesDataProvider,
  private val fhirEngine: FhirEngine,
) : AndroidViewModel(application) {

  private val _downloadBenchmarkSyncMutableStateFlow = MutableStateFlow(BenchmarkSyncState())
  val downloadBenchmarkSyncStateFlow = _downloadBenchmarkSyncMutableStateFlow.asStateFlow()

  private val syncBenchmarkBroadcastReceiver =
    object : BroadcastReceiver() {
      override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == SYNC_BENCHMARK_BROADCAST_ACTION) {
          val benchmarkDuration = Duration.parse(intent.getStringExtra("benchmarkDuration")!!)
          _downloadBenchmarkSyncMutableStateFlow.update {
            it.copy(benchmarkDuration = benchmarkDuration)
          }
        }
      }
    }

  init {
    registerSyncBroadcastReceiver()
    viewModelScope.launch(benchmarkingViewModelWorkDispatcher) {
      Sync.oneTimeSync<DownloadFhirSyncWorker>(
          getApplication(),
          existingWorkPolicy = ExistingWorkPolicy.REPLACE,
          retryConfiguration = defaultRetryConfiguration.copy(maxRetries = 0),
        )
        .collect { currentSyncJobStatus ->
          _downloadBenchmarkSyncMutableStateFlow.update {
            when {
              currentSyncJobStatus is CurrentSyncJobStatus.Running &&
                currentSyncJobStatus.inProgressSyncJob is SyncJobStatus.InProgress -> {
                val syncJobStatus =
                  currentSyncJobStatus.inProgressSyncJob as SyncJobStatus.InProgress
                if (syncJobStatus.syncOperation == SyncOperation.DOWNLOAD) {
                  it.copy(
                    syncStatus = currentSyncJobStatus,
                    completedResources = syncJobStatus.completed,
                  )
                } else {
                  it
                }
              }
              else -> it.copy(syncStatus = currentSyncJobStatus)
            }
          }
        }
    }
  }

  override fun onCleared() {
    viewModelScope.launch(benchmarkingViewModelWorkDispatcher) {
      Sync.cancelOneTimeSync<DownloadFhirSyncWorker>(getApplication())
    }
    unRegisterSyncBroadcastReceiver()
    super.onCleared()
  }

  private fun registerSyncBroadcastReceiver() {
    ContextCompat.registerReceiver(
      getApplication(),
      syncBenchmarkBroadcastReceiver,
      IntentFilter(SYNC_BENCHMARK_BROADCAST_ACTION),
      ContextCompat.RECEIVER_NOT_EXPORTED,
    )
  }

  private fun unRegisterSyncBroadcastReceiver() {
    (getApplication() as Context).unregisterReceiver(syncBenchmarkBroadcastReceiver)
  }
}

internal const val SYNC_BENCHMARK_BROADCAST_ACTION =
  "com.google.android.fhir.engine.benchmarks.app.ACTION_SYNC_BENCHMARK_DATA"

class DownloadFhirSyncWorker(appContext: Context, workerParams: WorkerParameters) :
  FhirSyncWorker(appContext, workerParams) {

  override fun getFhirEngine(): FhirEngine = MainApplication.fhirEngine(applicationContext)

  override fun getDownloadWorkManager(): DownloadWorkManager =
    ResourceParamsBasedDownloadWorkManager(
      syncParams =
        mapOf(
          ResourceType.Patient to mapOf(Patient.ADDRESS_CITY.paramName to "NAIROBI"),
          ResourceType.Patient to emptyMap(),
          ResourceType.Encounter to emptyMap(),
          ResourceType.Practitioner to emptyMap(),
          ResourceType.Organization to emptyMap(),
          ResourceType.Location to emptyMap(),
        ),
      context =
        object : ResourceParamsBasedDownloadWorkManager.TimestampContext {
          override suspend fun saveLastUpdatedTimestamp(
            resourceType: ResourceType,
            timestamp: String?,
          ) {
            // no-op
          }

          override suspend fun getLasUpdateTimestamp(resourceType: ResourceType): String? = null
        },
    )

  override fun getConflictResolver(): ConflictResolver = AcceptLocalConflictResolver

  override fun getUploadStrategy(): UploadStrategy =
    UploadStrategy.forBundleRequest(
      methodForCreate = HttpCreateMethod.PUT,
      methodForUpdate = HttpUpdateMethod.PATCH,
      squash = true,
      bundleSize = 0,
    )

  override suspend fun doWork(): Result {
    getFhirEngine().clearDatabase()

    val (result, duration) =
      measureTimedValueAsync { traceAsync(DOWNLOAD_SYNC_TRACE_NAME, 14) { super.doWork() } }

    applicationContext.sendBroadcast(
      Intent(SYNC_BENCHMARK_BROADCAST_ACTION).apply {
        putExtra("benchmarkDuration", duration.toString())
        setPackage("com.google.android.fhir.engine.benchmarks.app")
      },
    )

    return result
  }

  companion object {
    const val DOWNLOAD_SYNC_TRACE_NAME = "DownloadFhirSyncWorkerTrace"
  }
}

data class BenchmarkSyncState(
  val syncStatus: CurrentSyncJobStatus = CurrentSyncJobStatus.Cancelled,
  val completedResources: Int = -1,
  val benchmarkDuration: Duration = Duration.ZERO,
)
