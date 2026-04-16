/*
 * Copyright 2025-2026 Google LLC
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
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import co.touchlab.kermit.Logger
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.sync.download.DownloaderImpl
import com.google.android.fhir.sync.upload.UploadStrategy
import com.google.android.fhir.sync.upload.Uploader
import com.google.android.fhir.sync.upload.patch.PatchGeneratorFactory
import com.google.android.fhir.sync.upload.request.UploadRequestGeneratorFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Handles FHIR data synchronization between local database and remote server.
 *
 * Extend this abstract [CoroutineWorker] and implement the abstract methods to define your specific
 * synchronization behavior. The custom worker class can then be used to schedule periodic
 * synchronization jobs using [Sync].
 */
abstract class FhirSyncWorker(appContext: Context, workerParams: WorkerParameters) :
  CoroutineWorker(appContext, workerParams) {

  /** Returns the [FhirEngine] instance used for interacting with the local FHIR data store. */
  abstract fun getFhirEngine(): FhirEngine

  /** Returns the [DownloadWorkManager] instance that manages the download process. */
  abstract fun getDownloadWorkManager(): DownloadWorkManager

  /**
   * Returns the [ConflictResolver] instance that defines how to handle conflicts between local and
   * remote data during synchronization.
   */
  abstract fun getConflictResolver(): ConflictResolver

  /**
   * Returns the [UploadStrategy] instance that defines how local changes are uploaded to the
   * server.
   */
  abstract fun getUploadStrategy(): UploadStrategy

  /** Returns the [DataSource] instance from [FhirEngineProvider]. */
  internal open fun getDataSource(): DataSource? = FhirEngineProvider.getDataSource()

  /** Returns the [FhirDataStore] instance for persisting sync state and metadata. */
  internal open fun getFhirDataStore(): FhirDataStore =
    FhirDataStore(createDataStore(applicationContext))

  private val json = Json { ignoreUnknownKeys = true }

  override suspend fun doWork(): Result {
    val dataSource =
      getDataSource()
        ?: return Result.failure(
          buildErrorData(
            IllegalStateException(
              "FhirEngineConfiguration.ServerConfiguration is not set. Call FhirEngineProvider.init to initialize with appropriate configuration.",
            ),
          ),
        )

    val fhirDataStore = getFhirDataStore()

    val synchronizer =
      FhirSynchronizer(
        getFhirEngine(),
        UploadConfiguration(
          uploader =
            Uploader(
              dataSource = dataSource,
              patchGenerator = PatchGeneratorFactory.byMode(getUploadStrategy().patchGeneratorMode),
              requestGenerator =
                UploadRequestGeneratorFactory.byMode(getUploadStrategy().requestGeneratorMode),
            ),
          uploadStrategy = getUploadStrategy(),
        ),
        DownloadConfiguration(
          DownloaderImpl(dataSource, getDownloadWorkManager()),
          getConflictResolver(),
        ),
        fhirDataStore,
      )

    val job =
      CoroutineScope(Dispatchers.IO).launch {
        synchronizer.syncState.collect { syncJobStatus ->
          val uniqueWorkerName = inputData.getString(UNIQUE_WORK_NAME)
          when (syncJobStatus) {
            is SyncJobStatus.Succeeded,
            is SyncJobStatus.Failed, -> {
              if (uniqueWorkerName != null) {
                fhirDataStore.writeTerminalSyncJobStatus(uniqueWorkerName, syncJobStatus)
              }
              cancel()
            }
            else -> {
              setProgress(buildWorkData(syncJobStatus))
            }
          }
        }
      }

    val result = synchronizer.synchronize()
    val output = buildWorkData(result)

    kotlin.runCatching { job.join() }.onFailure { Logger.w(it) { "Failed to join sync job" } }

    Logger.d { "Received result from worker $result and sending output $output" }

    /**
     * In case of failure, we can check if its worth retrying and do retry based on
     * [RetryConfiguration.maxRetries] set by user.
     */
    val retries = inputData.getInt(MAX_RETRIES_ALLOWED, 0)
    return when (result) {
      is SyncJobStatus.Succeeded -> Result.success(output)
      else -> {
        if (retries > runAttemptCount) Result.retry() else Result.failure(output)
      }
    }
  }

  private fun buildWorkData(state: SyncJobStatus): Data {
    return workDataOf(
      "StateType" to state::class.java.name,
      "State" to json.encodeToString(state),
    )
  }

  private fun buildErrorData(exception: Exception): Data {
    return workDataOf("error" to exception::class.java.name, "reason" to exception.message)
  }
}
