/*
 * Copyright 2023-2024 Google LLC
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
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.FhirSyncDbInteractorImpl
import com.google.android.fhir.OffsetDateTimeTypeAdapter
import com.google.android.fhir.sync.download.DownloaderImpl
import com.google.android.fhir.sync.upload.DefaultResourceConsolidator
import com.google.android.fhir.sync.upload.LocalChangeFetcherFactory
import com.google.android.fhir.sync.upload.UploadStrategy
import com.google.android.fhir.sync.upload.Uploader
import com.google.android.fhir.sync.upload.patch.PatchGeneratorFactory
import com.google.android.fhir.sync.upload.request.UploadRequestGeneratorFactory
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import java.time.OffsetDateTime
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach

/** A WorkManager Worker that handles periodic sync. */
abstract class FhirSyncWorker(appContext: Context, workerParams: WorkerParameters) :
  CoroutineWorker(appContext, workerParams) {
  abstract fun getDownloadWorkManager(): DownloadWorkManager

  abstract fun getConflictResolver(): ConflictResolver

  abstract fun getUploadStrategy(): UploadStrategy

  private val gson =
    GsonBuilder()
      .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeTypeAdapter().nullSafe())
      .setExclusionStrategies(StateExclusionStrategy())
      .create()

  /** The purpose of this api makes it easy to stub [FhirSyncWorker] for testing. */
  internal open fun getDataSource() = FhirEngineProvider.getDataSource(applicationContext)

  override suspend fun doWork(): Result {
    val dataSource =
      getDataSource()
        ?: return Result.failure(
          buildWorkData(
            IllegalStateException(
              "FhirEngineConfiguration.ServerConfiguration is not set. Call FhirEngineProvider.init to initialize with appropriate configuration.",
            ),
          ),
        )

    val database = FhirEngineProvider.getFhirDatabase(applicationContext)
    val fhirDataStore = FhirEngineProvider.getFhirDataStore(applicationContext)

    val fhirSyncDbInteractor =
      FhirSyncDbInteractorImpl(
        database = database,
        localChangeFetcher =
          LocalChangeFetcherFactory.byMode(
            getUploadStrategy().localChangesFetchMode,
            database,
          ),
        resourceConsolidator = DefaultResourceConsolidator(database),
        conflictResolver = getConflictResolver(),
      )
    val uploader =
      Uploader(
        dataSource = dataSource,
        patchGenerator = PatchGeneratorFactory.byMode(getUploadStrategy().patchGeneratorMode),
        requestGenerator =
          UploadRequestGeneratorFactory.byMode(getUploadStrategy().requestGeneratorMode),
      )
    val downloader = DownloaderImpl(dataSource, getDownloadWorkManager())

    val terminalSyncJobStatus =
      FhirSynchronizer(
          uploader = uploader,
          downloader = downloader,
          fhirSyncDbInteractor = fhirSyncDbInteractor,
        )
        .synchronize()
        .onEach { setProgress(buildWorkData(it)) }
        .first { it is SyncJobStatus.Failed || it is SyncJobStatus.Succeeded }

    fhirDataStore.writeLastSyncTimestamp(terminalSyncJobStatus.timestamp)
    val uniqueWorkerName = inputData.getString(UNIQUE_WORK_NAME)
    // While creating periodicSync request if
    // putString(SYNC_STATUS_PREFERENCES_DATASTORE_KEY, uniqueWorkName) is not present,
    // then inputData.getString(SYNC_STATUS_PREFERENCES_DATASTORE_KEY) can be null.
    if (uniqueWorkerName != null) {
      fhirDataStore.writeTerminalSyncJobStatus(uniqueWorkerName, terminalSyncJobStatus)
    }

    /**
     * In case of failure, we can check if its worth retrying and do retry based on
     * [RetryConfiguration.maxRetries] set by user.
     */
    val retries = inputData.getInt(MAX_RETRIES_ALLOWED, 0)
    val output = buildWorkData(terminalSyncJobStatus)
    return when (terminalSyncJobStatus) {
      is SyncJobStatus.Succeeded -> Result.success(output)
      else -> {
        if (retries > runAttemptCount) Result.retry() else Result.failure(output)
      }
    }
  }

  private fun buildWorkData(state: SyncJobStatus): Data {
    return workDataOf(
      // send serialized state and type so that consumer can convert it back
      "StateType" to state::class.java.name,
      "State" to gson.toJson(state),
    )
  }

  private fun buildWorkData(exception: Exception): Data {
    return workDataOf("error" to exception::class.java.name, "reason" to exception.message)
  }

  /**
   * Exclusion strategy for [Gson] that handles field exclusions for [SyncJobStatus] returned by
   * FhirSynchronizer. It should skip serializing the exceptions to avoid exceeding WorkManager
   * WorkData limit
   *
   * @see <a
   *   href="https://github.com/google/android-fhir/issues/707">https://github.com/google/android-fhir/issues/707</a>
   */
  internal class StateExclusionStrategy : ExclusionStrategy {
    override fun shouldSkipField(field: FieldAttributes) = field.name.equals("exceptions")

    override fun shouldSkipClass(clazz: Class<*>?) = false
  }
}
