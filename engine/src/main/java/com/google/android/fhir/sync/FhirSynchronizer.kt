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
import com.google.android.fhir.sync.download.Downloader
import com.google.android.fhir.sync.upload.Uploader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
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

data class ResourceSyncException(val resourceType: ResourceType, val exception: Exception)

/** Class that helps synchronize the data source and save it in the local database */
internal class FhirSynchronizer(
  private val uploader: Uploader,
  private val downloader: Downloader,
  private val fhirSyncDbInteractor: FhirSyncDbInteractor,
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
    // Following is to bootstrap new state calculation based on previous "InProgress" states
    val initialSyncJobStatus =
      SyncJobStatus.InProgress(SyncOperation.DOWNLOAD, 0, 0) as SyncJobStatus
    downloader
      .download()
      .onEach { fhirSyncDbInteractor.consolidateDownloadResult(it) }
      .runningFold(initialSyncJobStatus, fhirSyncDbInteractor::updateSyncJobStatus)
      // initialSyncJobStatus is dropped
      .drop(1)
      .onEach { emit(it) }
      .firstOrNull { it is SyncJobStatus.Failed }
  }

  private suspend fun upload(): Flow<SyncJobStatus> = flow {
    var localChanges = fhirSyncDbInteractor.getLocalChanges()
    // Following is to bootstrap new state calculation based on previous "InProgress" states
    val initialSyncJobStatus =
      SyncJobStatus.InProgress(SyncOperation.UPLOAD, 0, localChanges.size) as SyncJobStatus
    while (localChanges.isNotEmpty()) {
      val failedOrNullSyncJobStatus =
        uploader
          .upload(localChanges)
          .onEach { fhirSyncDbInteractor.consolidateUploadResult(it) }
          .runningFold(initialSyncJobStatus, fhirSyncDbInteractor::updateSyncJobStatus)
          // initialSyncJobStatus is dropped
          .drop(1)
          .onEach { emit(it) }
          .firstOrNull { it is SyncJobStatus.Failed }
      if (failedOrNullSyncJobStatus is SyncJobStatus.Failed) {
        return@flow
      }
      localChanges = fhirSyncDbInteractor.getLocalChanges()
    }
  }

  companion object {
    private val mutex = Mutex()
  }
}
