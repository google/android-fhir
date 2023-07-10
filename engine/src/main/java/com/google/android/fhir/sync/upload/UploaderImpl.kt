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

package com.google.android.fhir.sync.upload

import com.google.android.fhir.LocalChange
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.sync.ResourceSyncException
import com.google.android.fhir.sync.UploadResult
import com.google.android.fhir.sync.UploadWorkManager
import com.google.android.fhir.sync.Uploader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.DomainResource
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import timber.log.Timber

/**
 * Implementation of the [Uploader]. It orchestrates the pre processing of [LocalChange] and
 * constructing appropriate upload requests via [UploadWorkManager] and uploading of requests via
 * [DataSource]. [Uploader] clients should call upload and listen to the various states emitted by
 * [UploadWorkManager] as [UploadResult].
 */
internal class UploaderImpl(
  private val dataSource: DataSource,
  private val uploadWorkManager: UploadWorkManager
) : Uploader {

  companion object {
    private val erroneousOperationOutcomesCodes =
      setOf<OperationOutcome.IssueSeverity>(
        OperationOutcome.IssueSeverity.WARNING,
        OperationOutcome.IssueSeverity.FATAL,
        OperationOutcome.IssueSeverity.ERROR
      )
  }

  override suspend fun upload(localChanges: List<LocalChange>): Flow<UploadResult> = flow {
    val transformedChanges = uploadWorkManager.preprocessLocalChanges(localChanges)
    val uploadRequests = uploadWorkManager.createUploadRequestsFromChanges(transformedChanges)
    val total = uploadRequests.size
    var completed = 0
    emit(UploadResult.Started(total))
    uploadRequests.forEach { uploadRequest ->
      try {
        val response = dataSource.upload(uploadRequest)
        completed += 1
        emit(
          getUploadResult(
            uploadRequest.resourceType,
            response,
            uploadRequest.localChangeToken,
            total,
            completed
          )
        )
      } catch (e: Exception) {
        Timber.e(e)
        emit(UploadResult.Failure(ResourceSyncException(ResourceType.Bundle, e)))
      }
    }
  }

  private fun getUploadResult(
    requestResourceType: ResourceType,
    response: Resource,
    localChangeToken: LocalChangeToken,
    total: Int,
    completed: Int
  ) =
    when {
      response is Bundle && response.type == Bundle.BundleType.TRANSACTIONRESPONSE -> {
        UploadResult.Success(localChangeToken, response, total, completed)
      }
      response is OperationOutcome &&
        response.issue.isNotEmpty() &&
        response.issue.any { erroneousOperationOutcomesCodes.contains(it.severity) } -> {
        UploadResult.Failure(
          ResourceSyncException(
            requestResourceType,
            FHIRException(response.issueFirstRep.diagnostics)
          )
        )
      }
      response is DomainResource -> {
        UploadResult.Success(localChangeToken, response, total, completed)
      }
      else -> {
        UploadResult.Failure(
          ResourceSyncException(
            requestResourceType,
            FHIRException("Unknown response for ${response.resourceType}")
          )
        )
      }
    }
}
