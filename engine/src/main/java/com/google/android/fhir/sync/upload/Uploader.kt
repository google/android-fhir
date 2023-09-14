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

package com.google.android.fhir.sync.upload

import com.google.android.fhir.LocalChange
import com.google.android.fhir.LocalChangeToken
import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.sync.ResourceSyncException
import com.google.android.fhir.sync.upload.patch.PerResourcePatchGenerator
import com.google.android.fhir.sync.upload.request.TransactionBundleGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import timber.log.Timber

/**
 * Uploads changes made locally to FHIR resources to server in the following steps:
 * 1. fetching local changes from the on-device SQLite database,
 * 2. creating patches to be sent to the server using the local changes,
 * 3. generating HTTP requests to be sent to the server,
 * 4. processing the responses from the server and consolidate any changes (i.e. updates resource
 *    IDs).
 */
internal class Uploader(
  private val dataSource: DataSource,
) {
  private val patchGenerator = PerResourcePatchGenerator
  private val requestGenerator = TransactionBundleGenerator.getDefault()

  suspend fun upload(localChanges: List<LocalChange>): Flow<UploadState> = flow {
    val patches = patchGenerator.generate(localChanges)
    val requests = requestGenerator.generateUploadRequests(patches)
    val token = LocalChangeToken(localChanges.flatMap { it.token.ids })
    val total = requests.size
    emit(UploadState.Started(total))
    requests.forEachIndexed { index, uploadRequest ->
      try {
        val response = dataSource.upload(uploadRequest)
        emit(
          getUploadResult(uploadRequest.resource.resourceType, response, token, total, index + 1),
        )
      } catch (e: Exception) {
        Timber.e(e)
        emit(UploadState.Failure(ResourceSyncException(ResourceType.Bundle, e)))
      }
    }
  }

  private fun getUploadResult(
    requestResourceType: ResourceType,
    response: Resource,
    localChangeToken: LocalChangeToken,
    total: Int,
    completed: Int,
  ) =
    when {
      response is Bundle && response.type == Bundle.BundleType.TRANSACTIONRESPONSE -> {
        UploadState.Success(localChangeToken, response, total, completed)
      }
      response is OperationOutcome && response.issue.isNotEmpty() -> {
        UploadState.Failure(
          ResourceSyncException(
            requestResourceType,
            FHIRException(response.issueFirstRep.diagnostics),
          ),
        )
      }
      else -> {
        UploadState.Failure(
          ResourceSyncException(
            requestResourceType,
            FHIRException("Unknown response for ${response.resourceType}"),
          ),
        )
      }
    }
}

internal sealed class UploadState {
  data class Started(val total: Int) : UploadState()

  data class Success(
    val localChangeToken: LocalChangeToken,
    val resource: Resource,
    val total: Int,
    val completed: Int,
  ) : UploadState()

  data class Failure(val syncError: ResourceSyncException) : UploadState()
}
