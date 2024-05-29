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

import com.google.android.fhir.FhirEngine
import com.google.android.fhir.sync.download.DownloadState
import com.google.android.fhir.sync.download.Downloader
import com.google.android.fhir.sync.upload.UploadStrategy
import com.google.android.fhir.sync.upload.Uploader
import java.time.OffsetDateTime
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flow
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

internal data class UploadConfiguration(
  val uploader: Uploader,
  val uploadStrategy: UploadStrategy,
)

internal class DownloadConfiguration(
  val downloader: Downloader,
  val conflictResolver: ConflictResolver,
)

/** Class that helps synchronize the data source and save it in the local database */
internal class FhirSynchronizer(
  private val fhirEngine: FhirEngine,
  private val uploadConfiguration: UploadConfiguration,
  private val downloadConfiguration: DownloadConfiguration,
  private val datastoreUtil: FhirDataStore,
) {

  private val _syncState = MutableSharedFlow<SyncJobStatus>()
  val syncState: SharedFlow<SyncJobStatus> = _syncState

  private suspend fun setSyncState(state: SyncJobStatus) = _syncState.emit(state)

  private suspend fun setSyncState(result: SyncResult): SyncJobStatus {
    // todo: emit this properly instead of using datastore?
    datastoreUtil.writeLastSyncTimestamp(result.timestamp)

    val state =
      when (result) {
        is SyncResult.Success -> SyncJobStatus.Succeeded()
        is SyncResult.Error -> SyncJobStatus.Failed(result.exceptions)
      }

    setSyncState(state)
    return state
  }

  /**
   * Manages the sequential execution of downloading and uploading for coordinated operation. This
   * function is coroutine-safe, ensuring that multiple invocations will not interfere with each
   * other.
   */
  suspend fun synchronize(): SyncJobStatus {
    mutex.withLock {
      setSyncState(SyncJobStatus.Started())

      return listOf(download(), upload())
        .filterIsInstance<SyncResult.Error>()
        .flatMap { it.exceptions }
        .let {
          if (it.isEmpty()) {
            setSyncState(SyncResult.Success())
          } else {
            setSyncState(SyncResult.Error(it))
          }
        }
    }
  }

  private suspend fun download(): SyncResult {
    val exceptions = mutableListOf<ResourceSyncException>()
    fhirEngine.syncDownload(downloadConfiguration.conflictResolver) {
      flow {
        downloadConfiguration.downloader.download().collect {
          when (it) {
            is DownloadState.Started -> {
              setSyncState(SyncJobStatus.InProgress(SyncOperation.DOWNLOAD, it.total))
            }
            is DownloadState.Success -> {
              setSyncState(SyncJobStatus.InProgress(SyncOperation.DOWNLOAD, it.total, it.completed))
              emit(it.resources)
            }
            is DownloadState.Failure -> {
              exceptions.add(it.syncError)
            }
          }
        }
      }
    }
    return if (exceptions.isEmpty()) {
      SyncResult.Success()
    } else {
      SyncResult.Error(exceptions)
    }
  }

  private suspend fun upload(): SyncResult {
    val exceptions = mutableListOf<ResourceSyncException>()
    fhirEngine
      .syncUpload(uploadConfiguration.uploadStrategy, uploadConfiguration.uploader::upload)
      .collect { progress ->
        progress.uploadError?.let { exceptions.add(it) }
          ?: setSyncState(
            SyncJobStatus.InProgress(
              SyncOperation.UPLOAD,
              progress.initialTotal,
              progress.initialTotal - progress.remaining,
            ),
          )
      }

    return if (exceptions.isEmpty()) {
      SyncResult.Success()
    } else {
      SyncResult.Error(exceptions)
    }
  }

  companion object {
    private val mutex = Mutex()
  }
}
