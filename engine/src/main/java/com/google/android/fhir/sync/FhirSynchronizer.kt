/*
 * Copyright 2021 Google LLC
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
import com.google.android.fhir.sync.bundle.BundleUploader
import com.google.android.fhir.sync.bundle.TransactionBundleGenerator
import java.time.OffsetDateTime
import java.util.LinkedList
import java.util.Queue
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

sealed class Result {
  val timestamp: OffsetDateTime = OffsetDateTime.now()

  object Success : Result()
  data class Error(val exceptions: List<ResourceSyncException>) : Result()
}

sealed class State {
  object Started : State()

  data class InProgress(val resourceType: ResourceType?) : State()
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
  private val downloadManager: DownloadManager,
  private val uploader: Uploader =
    BundleUploader(dataSource, TransactionBundleGenerator.getDefault())
) {
  private var flow: MutableSharedFlow<State>? = null
  private val datastoreUtil = DatastoreUtil(context)
  private val resourceTypeList = ResourceType.values().map { it.name }

  private fun isSubscribed(): Boolean {
    return flow != null
  }

  fun subscribe(flow: MutableSharedFlow<State>) {
    if (isSubscribed()) {
      throw IllegalStateException("Already subscribed to a flow")
    }

    this.flow = flow
  }

  private suspend fun emit(state: State) {
    flow?.emit(state)
  }

  private suspend fun emitResult(result: Result): Result {
    datastoreUtil.writeLastSyncTimestamp(result.timestamp)

    when (result) {
      is Result.Success -> emit(State.Finished(result))
      is Result.Error -> emit(State.Failed(result))
    }

    return result
  }

  suspend fun synchronize(): Result {
    emit(State.Started)

    return listOf(upload(), download())
      .filterIsInstance<Result.Error>()
      .flatMap { it.exceptions }
      .let {
        if (it.isEmpty()) {
          emitResult(Result.Success)
        } else {
          emitResult(Result.Error(it))
        }
      }
  }

  private suspend fun download(): Result {
    var resourceTypeToDownload: ResourceType = ResourceType.Bundle
    val exceptions = mutableListOf<ResourceSyncException>()
    emit(State.InProgress(resourceTypeToDownload))

    fhirEngine.syncDownload {
      flow {
        val listOfUrls: Queue<String> = LinkedList()
        listOfUrls.add(downloadManager.getInitialUrl())
        while (listOfUrls.isNotEmpty()) {
          try {
            val preprocessedUrl = listOfUrls.remove()
            resourceTypeToDownload =
              ResourceType.fromCode(
                preprocessedUrl.findAnyOf(resourceTypeList, ignoreCase = true)!!.second
              )

            val lastUpdate = it.getLatestTimestampFor(resourceTypeToDownload)
            val downloadUrl = downloadManager.createDownloadUrl(preprocessedUrl, lastUpdate)
            val resourceReturned: Resource = dataSource.loadData(downloadUrl)
            val resourceCollection = downloadManager.extractResourcesFromResponse(resourceReturned)
            val additionalUrls = downloadManager.extractNextUrlsFromResource(resourceReturned)

            emit(resourceCollection.toList())
            listOfUrls.addAll(additionalUrls)
          } catch (exception: Exception) {
            exceptions.add(ResourceSyncException(resourceTypeToDownload, exception))
          }
        }
      }
    }
    return if (exceptions.isEmpty()) {
      Result.Success
    } else {
      emit(State.Glitch(exceptions))

      Result.Error(exceptions)
    }
  }

  private suspend fun upload(): Result {
    val exceptions = mutableListOf<ResourceSyncException>()
    fhirEngine.syncUpload { list ->
      flow {
        uploader.upload(list).collect {
          when (it) {
            is UploadResult.Success -> emit(it.localChangeToken to it.resource)
            is UploadResult.Failure -> exceptions.add(it.syncError)
          }
        }
      }
    }
    return if (exceptions.isEmpty()) {
      Result.Success
    } else {
      emit(State.Glitch(exceptions))
      Result.Error(exceptions)
    }
  }
}
