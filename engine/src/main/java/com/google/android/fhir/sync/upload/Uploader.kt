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

package com.google.android.fhir.sync.upload

import com.google.android.fhir.LocalChange
import com.google.android.fhir.db.LocalChangeResourceReference
import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.sync.ResourceSyncException
import com.google.android.fhir.sync.upload.patch.PatchGenerator
import com.google.android.fhir.sync.upload.request.BundleUploadRequestMapping
import com.google.android.fhir.sync.upload.request.UploadRequestGenerator
import com.google.android.fhir.sync.upload.request.UploadRequestMapping
import com.google.android.fhir.sync.upload.request.UrlUploadRequestMapping
import java.lang.IllegalStateException
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.transformWhile
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.instance.model.api.IBase
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome
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
  suspend fun upload(
    localChanges: List<LocalChange>,
    localChangesReferences: List<LocalChangeResourceReference>,
  ) =
    localChanges
      .let { patchGenerator.generate(it, localChangesReferences) }
      .let { requestGenerator.generateUploadRequests(it) }
      .asFlow()
      .transformWhile {
        with(handleUploadRequest(it)) {
          emit(this)
          this !is UploadRequestResult.Failure
        }
      }

  private fun handleSuccessfulUploadResponse(
    mappedUploadRequest: UploadRequestMapping,
    response: Resource,
  ): UploadRequestResult.Success {
    val responsesList =
      when {
        mappedUploadRequest is UrlUploadRequestMapping && response is DomainResource ->
          listOf(ResourceUploadResponseMapping(mappedUploadRequest.localChanges, response))
        mappedUploadRequest is BundleUploadRequestMapping &&
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
    mappedUploadRequest: BundleUploadRequestMapping,
    bundleResponse: Bundle,
  ): List<SuccessfulUploadResponseMapping> {
    require(mappedUploadRequest.splitLocalChanges.size == bundleResponse.entry.size)
    return mappedUploadRequest.splitLocalChanges.mapIndexed { index, localChanges ->
      val bundleEntry = bundleResponse.entry[index]
      when {
        bundleEntry.hasResource() && bundleEntry.resource is DomainResource ->
          ResourceUploadResponseMapping(localChanges, bundleEntry.resource as DomainResource)
        bundleEntry.hasResponse() ->
          BundleComponentUploadResponseMapping(localChanges, bundleEntry.response)
        else ->
          throw IllegalStateException(
            "Unknown response: $bundleEntry for Bundle Request at index $index",
          )
      }
    }
  }

  private suspend fun handleUploadRequest(
    mappedUploadRequest: UploadRequestMapping,
  ): UploadRequestResult {
    return try {
      val response = dataSource.upload(mappedUploadRequest.generatedRequest)
      when {
        response is OperationOutcome && response.issue.isNotEmpty() ->
          UploadRequestResult.Failure(
            mappedUploadRequest.localChanges,
            ResourceSyncException(
              mappedUploadRequest.generatedRequest.resource.resourceType,
              FHIRException(response.issueFirstRep.diagnostics),
            ),
          )
        (response is DomainResource || response is Bundle) &&
          (response !is IBaseOperationOutcome) ->
          handleSuccessfulUploadResponse(mappedUploadRequest, response)
        else ->
          UploadRequestResult.Failure(
            mappedUploadRequest.localChanges,
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
        mappedUploadRequest.localChanges,
        ResourceSyncException(mappedUploadRequest.generatedRequest.resource.resourceType, e),
      )
    }
  }
}

sealed class UploadRequestResult {
  data class Success(
    val successfulUploadResponseMappings: List<SuccessfulUploadResponseMapping>,
  ) : UploadRequestResult()

  data class Failure(
    val localChanges: List<LocalChange>,
    val uploadError: ResourceSyncException,
  ) : UploadRequestResult()
}

sealed class SuccessfulUploadResponseMapping(
  open val localChanges: List<LocalChange>,
  open val output: IBase,
)

internal data class ResourceUploadResponseMapping(
  override val localChanges: List<LocalChange>,
  override val output: DomainResource,
) : SuccessfulUploadResponseMapping(localChanges, output)

internal data class BundleComponentUploadResponseMapping(
  override val localChanges: List<LocalChange>,
  override val output: Bundle.BundleEntryResponseComponent,
) : SuccessfulUploadResponseMapping(localChanges, output)
