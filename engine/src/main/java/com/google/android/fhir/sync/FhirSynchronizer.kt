/*
 * Copyright 2023 Google LLC
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
import com.google.android.fhir.DatastoreUtil
import com.google.android.fhir.FhirEngine
import java.time.OffsetDateTime
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.hl7.fhir.r4.model.ResourceType

enum class SyncOperation {
  DOWNLOAD,
  UPLOAD
}

private sealed class SyncResult {
  val timestamp: OffsetDateTime = OffsetDateTime.now()

  class Success : SyncResult()
  data class Error(val exceptions: List<ResourceSyncException>) : SyncResult()
}

data class ResourceSyncException(val resourceType: ResourceType, val exception: Exception)

/** Class that helps synchronize the data source and save it in the local database */
internal class FhirSynchronizer(
  context: Context,
  private val fhirEngine: FhirEngine,
  private val uploader: Uploader,
  private val downloader: Downloader,
  private val conflictResolver: ConflictResolver
) {
  private var syncState: MutableSharedFlow<SyncJobStatus>? = null
  private val datastoreUtil = DatastoreUtil(context)

  private fun isSubscribed(): Boolean {
    return syncState != null
  }

  fun subscribe(flow: MutableSharedFlow<SyncJobStatus>) {
    if (isSubscribed()) {
      throw IllegalStateException("Already subscribed to a flow")
    }

    this.syncState = flow
  }

  private suspend fun setSyncState(state: SyncJobStatus) {
    syncState?.emit(state)
  }

  private suspend fun setSyncState(result: SyncResult): SyncJobStatus {
    // todo: emit this properly instead of using datastore?
    datastoreUtil.writeLastSyncTimestamp(result.timestamp)

    val state =
      when (result) {
        is SyncResult.Success -> SyncJobStatus.Finished()
        is SyncResult.Error -> SyncJobStatus.Failed(result.exceptions)
      }

    setSyncState(state)
    return state
  }

  suspend fun synchronize(): SyncJobStatus {
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

  private suspend fun download(): SyncResult {
    val exceptions = mutableListOf<ResourceSyncException>()
    fhirEngine.syncDownload(conflictResolver) {
      flow {
        downloader.download().collect {
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
      setSyncState(SyncJobStatus.Glitch(exceptions))
      SyncResult.Error(exceptions)
    }
  }

  private suspend fun upload(): SyncResult {
    val exceptions = mutableListOf<ResourceSyncException>()
    fhirEngine.syncUpload { list ->
      flow {
        uploader.upload(list).collect { result ->
          when (result) {
            is UploadState.Started ->
              setSyncState(SyncJobStatus.InProgress(SyncOperation.UPLOAD, result.total))
            is UploadState.Success ->
              emit(result.localChangeToken to result.resource).also {
                setSyncState(
                  SyncJobStatus.InProgress(SyncOperation.UPLOAD, result.total, result.completed)
                )
              }
            is UploadState.Failure -> exceptions.add(result.syncError)
          }
        }
      }
    }
    return if (exceptions.isEmpty()) {
      SyncResult.Success()
    } else {
      setSyncState(SyncJobStatus.Glitch(exceptions))
      SyncResult.Error(exceptions)
    }
  }
}
