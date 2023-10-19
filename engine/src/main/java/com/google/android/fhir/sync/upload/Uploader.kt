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
import com.google.android.fhir.sync.upload.patch.PatchGenerator
import com.google.android.fhir.sync.upload.request.BundleUploadRequestGeneratorOutput
import com.google.android.fhir.sync.upload.request.UploadRequestGenerator
import com.google.android.fhir.sync.upload.request.UploadRequestGeneratorOutput
import com.google.android.fhir.sync.upload.request.UrlUploadRequestGeneratorOutput
import java.lang.IllegalStateException
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.instance.model.api.IBase
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.DomainResource
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Resource
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
  private val patchGenerator: PatchGenerator,
  private val requestGenerator: UploadRequestGenerator,
) {
  suspend fun upload(localChanges: List<LocalChange>): UploadSyncResult {
    val patches = patchGenerator.generate(localChanges)
    val mappedRequests = requestGenerator.generateUploadRequests(patches)
    val token = LocalChangeToken(localChanges.flatMap { it.token.ids })

    val successfulMappedResponses = mutableListOf<SuccessfulUploadRequestResponse>()

    for (mappedRequest in mappedRequests) {
      when (val result = handleUploadRequest(mappedRequest)) {
        is UploadRequestResult.Success ->
          successfulMappedResponses.addAll(result.successfulUploadRequestResponses)
        is UploadRequestResult.Failure -> return UploadSyncResult.Failure(result.exception, token)
      }
    }
    return UploadSyncResult.Success(successfulMappedResponses)
  }

  private fun handleUploadResponse(
    mappedUploadRequest: UploadRequestGeneratorOutput,
    response: Resource,
  ): UploadRequestResult {
    val responsesList =
      when {
        mappedUploadRequest is UrlUploadRequestGeneratorOutput && response is DomainResource ->
          listOf(ResourceUploadResponse(mappedUploadRequest.localChanges, response))
        mappedUploadRequest is BundleUploadRequestGeneratorOutput &&
          response is Bundle &&
          response.type == Bundle.BundleType.TRANSACTIONRESPONSE ->
          handleBundleUploadResponse(mappedUploadRequest, response)
        else ->
          throw IllegalStateException(
            "Unknown mapping for request and response. Request Type: ${mappedUploadRequest.javaClass}, Response Type: ${response.resourceType}",
          )
      }
    return UploadRequestResult.Success(responsesList)
  }

  private fun handleBundleUploadResponse(
    mappedUploadRequest: BundleUploadRequestGeneratorOutput,
    bundleResponse: Bundle,
  ): List<SuccessfulUploadRequestResponse> {
    require(mappedUploadRequest.splitLocalChanges.size == bundleResponse.entry.size)
    return mappedUploadRequest.splitLocalChanges.mapIndexed { index, localChanges ->
      val bundleEntry = bundleResponse.entry[index]
      when {
        bundleEntry.hasResource() && bundleEntry.resource is DomainResource ->
          ResourceUploadResponse(localChanges, bundleEntry.resource as DomainResource)
        bundleEntry.hasResponse() ->
          BundleComponentUploadResponse(localChanges, bundleEntry.response)
        else ->
          throw IllegalStateException(
            "Unknown response: $bundleEntry for Bundle Request at index $index",
          )
      }
    }
  }

  private suspend fun handleUploadRequest(
    mappedUploadRequest: UploadRequestGeneratorOutput,
  ): UploadRequestResult {
    return try {
      val response = dataSource.upload(mappedUploadRequest.generatedRequest)
      when {
        response is OperationOutcome && response.issue.isNotEmpty() ->
          UploadRequestResult.Failure(
            ResourceSyncException(
              mappedUploadRequest.generatedRequest.resource.resourceType,
              FHIRException(response.issueFirstRep.diagnostics),
            ),
          )
        response is DomainResource || response is Bundle ->
          handleUploadResponse(mappedUploadRequest, response)
        else ->
          UploadRequestResult.Failure(
            ResourceSyncException(
              mappedUploadRequest.generatedRequest.resource.resourceType,
              FHIRException(
                "Unknown response for ${mappedUploadRequest.generatedRequest.resource.resourceType}",
              ),
            ),
          )
      }
    } catch (e: Exception) {
      Timber.e(e)
      UploadRequestResult.Failure(
        ResourceSyncException(mappedUploadRequest.generatedRequest.resource.resourceType, e),
      )
    }
  }

  private sealed class UploadRequestResult {
    data class Success(
      val successfulUploadRequestResponses: List<SuccessfulUploadRequestResponse>,
    ) : UploadRequestResult()

    data class Failure(val exception: ResourceSyncException) : UploadRequestResult()
  }
}

sealed class UploadSyncResult {
  data class Success(
    val uploadResponses: List<SuccessfulUploadRequestResponse>,
  ) : UploadSyncResult()

  data class Failure(val syncError: ResourceSyncException, val localChangeToken: LocalChangeToken) :
    UploadSyncResult()
}

sealed class SuccessfulUploadRequestResponse(
  open val localChanges: List<LocalChange>,
  open val output: IBase,
)

internal data class ResourceUploadResponse(
  override val localChanges: List<LocalChange>,
  override val output: DomainResource,
) : SuccessfulUploadRequestResponse(localChanges, output)

internal data class BundleComponentUploadResponse(
  override val localChanges: List<LocalChange>,
  override val output: Bundle.BundleEntryResponseComponent,
) : SuccessfulUploadRequestResponse(localChanges, output)
