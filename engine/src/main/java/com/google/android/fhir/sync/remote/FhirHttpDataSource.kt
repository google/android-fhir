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

package com.google.android.fhir.sync.remote

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jsonpatch.JsonPatch
import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.sync.download.BundleDownloadRequest
import com.google.android.fhir.sync.download.DownloadRequest
import com.google.android.fhir.sync.download.UrlDownloadRequest
import com.google.android.fhir.sync.upload.request.BundleUploadRequest
import com.google.android.fhir.sync.upload.request.UploadRequest
import com.google.android.fhir.sync.upload.request.UrlUploadRequest
import org.hl7.fhir.r4.model.Binary
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.codesystems.HttpVerb

/**
 * Implementation of [DataSource] to sync data with the FHIR server using HTTP method calls.
 *
 * @param fhirHttpService Http service to make requests to the server.
 */
internal class FhirHttpDataSource(private val fhirHttpService: FhirHttpService) : DataSource {

  override suspend fun download(downloadRequest: DownloadRequest) =
    when (downloadRequest) {
      is UrlDownloadRequest -> fhirHttpService.get(downloadRequest.url, downloadRequest.headers)
      is BundleDownloadRequest ->
        fhirHttpService.post(".", downloadRequest.bundle, downloadRequest.headers)
    }

  override suspend fun upload(request: UploadRequest): Resource =
    when (request) {
      is BundleUploadRequest -> fhirHttpService.post(request.url, request.resource, request.headers)
      is UrlUploadRequest -> uploadIndividualRequest(request)
    }

  private suspend fun uploadIndividualRequest(request: UrlUploadRequest): Resource =
    when (request.httpVerb) {
      HttpVerb.POST -> fhirHttpService.post(request.url, request.resource, request.headers)
      HttpVerb.PUT -> fhirHttpService.put(request.url, request.resource, request.headers)
      HttpVerb.PATCH ->
        fhirHttpService.patch(request.url, request.resource.toJsonPatch(), request.headers)
      HttpVerb.DELETE -> fhirHttpService.delete(request.url, request.headers)
      else -> error("The method, ${request.httpVerb}, is not supported for upload")
    }
}

private fun Resource.toJsonPatch(): JsonPatch =
  when (this) {
    is Binary -> {
      val objectMapper = ObjectMapper()
      objectMapper.readValue(String(this.data), JsonPatch::class.java)
    }
    else -> error("This resource cannot have the PATCH operation be applied to it")
  }
