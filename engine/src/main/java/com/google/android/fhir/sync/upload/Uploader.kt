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
import com.google.android.fhir.sync.upload.request.UploadRequest
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
internal class Uploader(private val dataSource: DataSource) {
  private val patchGenerator = PerResourcePatchGenerator
  private val requestGenerator = TransactionBundleGenerator.getDefault()

  suspend fun upload(localChanges: List<LocalChange>): UploadSyncResult {
    val patches = patchGenerator.generate(localChanges)
    val requests = requestGenerator.generateUploadRequests(patches)
    val token = LocalChangeToken(localChanges.flatMap { it.token.ids })

    val successfulResponses = mutableListOf<Resource>()

    for (uploadRequest in requests) {
      when (val result = handleUploadRequest(uploadRequest)) {
        is UploadRequestResult.Success -> successfulResponses.add(result.resource)
        is UploadRequestResult.Failure -> return UploadSyncResult.Failure(result.exception, token)
      }
    }

    return UploadSyncResult.Success(localChanges, successfulResponses)
  }

  private suspend fun handleUploadRequest(uploadRequest: UploadRequest): UploadRequestResult {
    return try {
      val response = dataSource.upload(uploadRequest)
      when {
        response is Bundle && response.type == Bundle.BundleType.TRANSACTIONRESPONSE ->
          UploadRequestResult.Success(response)
        response is OperationOutcome && response.issue.isNotEmpty() ->
          UploadRequestResult.Failure(
            ResourceSyncException(
              uploadRequest.resource.resourceType,
              FHIRException(response.issueFirstRep.diagnostics),
            ),
          )
        else ->
          UploadRequestResult.Failure(
            ResourceSyncException(
              uploadRequest.resource.resourceType,
              FHIRException("Unknown response for ${uploadRequest.resource.resourceType}"),
            ),
          )
      }
    } catch (e: Exception) {
      Timber.e(e)
      UploadRequestResult.Failure(ResourceSyncException(ResourceType.Bundle, e))
    }
  }

  private sealed class UploadRequestResult {
    data class Success(val resource: Resource) : UploadRequestResult()

    data class Failure(val exception: ResourceSyncException) : UploadRequestResult()
  }
}

sealed class UploadSyncResult {
  data class Success(
    val localChanges: List<LocalChange>,
    val responseResources: List<Resource>,
  ) : UploadSyncResult()

  data class Failure(val syncError: ResourceSyncException, val localChangeToken: LocalChangeToken) :
    UploadSyncResult()
}
