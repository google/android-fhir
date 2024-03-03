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

import com.google.android.fhir.FhirSyncDbInteractor
import com.google.android.fhir.sync.download.DownloadState
import com.google.android.fhir.sync.download.Downloader
import com.google.android.fhir.sync.upload.UploadRequestResult
import com.google.android.fhir.sync.upload.Uploader
import java.time.OffsetDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.hl7.fhir.r4.model.ResourceType

enum class SyncOperation {
  DOWNLOAD,
  UPLOAD,
}

private sealed class SyncResult {
  val timestamp: OffsetDateTime = OffsetDateTime.now()

  class Success : SyncResult()

  data class Error(val exceptions: List<ResourceSyncException>) : SyncResult()
}

data class ResourceSyncException(val resourceType: ResourceType, val exception: Exception)

/** Class that helps synchronize the data source and save it in the local database */
internal class FhirSynchronizer(
  private val fhirSyncDbInteractor: FhirSyncDbInteractor,
  private val uploader: Uploader,
  private val downloader: Downloader,
) {
  /**
   * Manages the sequential execution of downloading and uploading for coordinated operation. This
   * function is coroutine-safe, ensuring that multiple invocations will not interfere with each
   * other.
   */
  suspend fun synchronize(): Flow<SyncJobStatus> = flow {
    mutex.withLock {
      emit(SyncJobStatus.Started())
      emitAll(download())
      emitAll(upload())
      emit(SyncJobStatus.Succeeded())
    }
  }

  private suspend fun download(): Flow<SyncJobStatus> = flow {
    val initialSyncJobStatus =
      SyncJobStatus.InProgress(SyncOperation.DOWNLOAD, 0, 0) as SyncJobStatus
    downloader
      .download()
      .onEach { fhirSyncDbInteractor.consolidateDownloadResult(it) }
      .runningFold(initialSyncJobStatus) { lastStatus, downloadState ->
        with(lastStatus as SyncJobStatus.InProgress) {
          when (downloadState) {
            is DownloadState.Started -> {
              SyncJobStatus.InProgress(
                syncOperation = SyncOperation.DOWNLOAD,
                total = downloadState.total,
                completed = 0,
              )
            }
            is DownloadState.Success -> {
              SyncJobStatus.InProgress(
                syncOperation = syncOperation,
                total = downloadState.total,
                completed = downloadState.completed,
              )
            }
            is DownloadState.Failure -> {
              SyncJobStatus.Failed(
                exceptions = listOf(downloadState.syncError),
              )
            }
          }
        }
      }
      .onEach { emit(it) }
      .firstOrNull { it is SyncJobStatus.Failed }
  }

  private suspend fun upload(): Flow<SyncJobStatus> = flow {
    var localChanges = fhirSyncDbInteractor.getLocalChanges()
    val initialSyncJobStatus =
      SyncJobStatus.InProgress(SyncOperation.UPLOAD, 0, fhirSyncDbInteractor.getLocalChangesCount())
        as SyncJobStatus
    while (localChanges.isNotEmpty()) {
      val syncJobStatus =
        uploader
          .upload(localChanges)
          .onEach { fhirSyncDbInteractor.consolidateUploadResult(it) }
          .runningFold(initialSyncJobStatus) { lastStatus, uploadRequestResult ->
            when (uploadRequestResult) {
              is UploadRequestResult.Success -> {
                val localChangesCount =
                  uploadRequestResult.successfulUploadResponseMappings
                    .flatMap { it.localChanges }
                    .size
                with(lastStatus as SyncJobStatus.InProgress) {
                  SyncJobStatus.InProgress(
                    syncOperation = syncOperation,
                    completed = completed + localChangesCount,
                    total = total,
                  )
                }
              }
              is UploadRequestResult.Failure -> {
                SyncJobStatus.Failed(
                  exceptions = listOf(uploadRequestResult.uploadError),
                )
              }
            }
          }
          .onEach { emit(it) }
          .firstOrNull { it is SyncJobStatus.Failed }
      if (syncJobStatus is SyncJobStatus.Failed) {
        return@flow
      }
      localChanges = fhirSyncDbInteractor.getLocalChanges()
    }
  }

  companion object {
    private val mutex = Mutex()
  }
}
