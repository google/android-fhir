/*
 * Copyright 2022 Google LLC
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
import com.google.android.fhir.sync.download.DownloaderImpl
import com.google.android.fhir.sync.upload.BundleUploader
import com.google.android.fhir.sync.upload.TransactionBundleGenerator
import java.time.OffsetDateTime
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.hl7.fhir.r4.model.ResourceType

enum class SyncOperation {
  DOWNLOAD,
  UPLOAD
}

sealed class Result {
  val timestamp: OffsetDateTime = OffsetDateTime.now()

  class Success : Result()
  data class Error(val exceptions: List<ResourceSyncException>) : Result()
}

sealed class State {
  object Started : State()

  data class Spawned(
    val syncOperation: SyncOperation,
    val total: Number,
    val details: Map<String, Number>
  ) : State()
  data class InProgress(
    val syncOperation: SyncOperation,
    val percentCompleted: Double,
    val details: Progress?
  ) : State()
  data class Glitch(val exceptions: List<ResourceSyncException>) : State()

  data class Finished(val result: Result.Success) : State()
  data class Failed(val result: Result.Error) : State()
}

data class ResourceSyncException(val resourceType: ResourceType, val exception: Exception)

/** Class that helps synchronize the data source and save it in the local database */
internal class FhirSynchronizer(
  context: Context,
  private val fhirEngine: FhirEngine,
  private val dataSource: DataSource,
  private val downloadManager: DownloadWorkManager,
  private val uploader: Uploader =
    BundleUploader(dataSource, TransactionBundleGenerator.getDefault()),
  private val downloader: Downloader = DownloaderImpl(dataSource, downloadManager),
  private val conflictResolver: ConflictResolver
) {
  private var syncState: MutableSharedFlow<State>? = null
  private val datastoreUtil = DatastoreUtil(context)

  private fun isSubscribed(): Boolean {
    return syncState != null
  }

  fun subscribe(flow: MutableSharedFlow<State>) {
    if (isSubscribed()) {
      throw IllegalStateException("Already subscribed to a flow")
    }

    this.syncState = flow
  }

  private suspend fun setSyncState(state: State) {
    syncState?.emit(state)
  }

  private suspend fun setSyncState(result: Result): Result {
    datastoreUtil.writeLastSyncTimestamp(result.timestamp)

    when (result) {
      is Result.Success -> setSyncState(State.Finished(result))
      is Result.Error -> setSyncState(State.Failed(result))
    }

    return result
  }

  suspend fun synchronize(): Result {
    setSyncState(State.Started)

    return listOf(download(), upload()) // TODO ????????????? upload first or download
      .filterIsInstance<Result.Error>()
      .flatMap { it.exceptions }
      .let {
        if (it.isEmpty()) {
          setSyncState(Result.Success())
        } else {
          setSyncState(Result.Error(it))
        }
      }
  }

  private suspend fun download(): Result {
    val exceptions = mutableListOf<ResourceSyncException>()
    fhirEngine.syncDownload(conflictResolver) {
      flow {
        downloader.download(it, progressCallback(SyncOperation.DOWNLOAD)).collect {
          when (it) {
            is DownloadState.Success -> {
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
      Result.Success()
    } else {
      setSyncState(State.Glitch(exceptions))
      Result.Error(exceptions)
    }
  }

  private suspend fun upload(): Result {
    val exceptions = mutableListOf<ResourceSyncException>()
    fhirEngine.syncUpload { list ->
      flow {
        uploader.upload(list, progressCallback(SyncOperation.UPLOAD)).collect {
          when (it) {
            is UploadResult.Success -> emit(it.localChangeToken to it.resource)
            is UploadResult.Failure -> exceptions.add(it.syncError)
          }
        }
      }
    }
    return if (exceptions.isEmpty()) {
      Result.Success()
    } else {
      setSyncState(State.Glitch(exceptions))
      Result.Error(exceptions)
    }
  }

  fun progressCallback(syncOperation: SyncOperation) =
    object : ProgressCallback {
      override suspend fun onStart(totalRecords: Int, details: Map<String, Number>) {
        setSyncState(State.Spawned(syncOperation, totalRecords, details))
      }

      override suspend fun onProgress(percentCompleted: Double, details: Progress?) {
        setSyncState(State.InProgress(syncOperation, percentCompleted, details))
      }
    }
}
