/*
 * Copyright 2025-2026 Google LLC
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

import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.sync.download.BundleDownloadRequest
import com.google.android.fhir.sync.download.DownloadRequest
import com.google.android.fhir.sync.download.UrlDownloadRequest
import com.google.android.fhir.sync.upload.request.BundleUploadRequest
import com.google.android.fhir.sync.upload.request.UploadRequest
import com.google.android.fhir.sync.upload.request.UrlUploadRequest
import com.google.fhir.model.r4.Binary
import com.google.fhir.model.r4.Bundle
import com.google.fhir.model.r4.Resource
import io.ktor.util.decodeBase64String
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray

/**
 * Implementation of [DataSource] to sync data with the FHIR server using HTTP method calls.
 *
 * @param fhirHttpService Http service to make requests to the server.
 */
internal class FhirHttpDataSource(private val fhirHttpService: FhirHttpService) : DataSource {

  override suspend fun download(downloadRequest: DownloadRequest): Resource =
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
      Bundle.HTTPVerb.Post -> fhirHttpService.post(request.url, request.resource, request.headers)
      Bundle.HTTPVerb.Put -> fhirHttpService.put(request.url, request.resource, request.headers)
      Bundle.HTTPVerb.Patch ->
        fhirHttpService.patch(request.url, request.resource.toJsonPatch(), request.headers)
      Bundle.HTTPVerb.Delete -> fhirHttpService.delete(request.url, request.headers)
      else -> error("The method, ${request.httpVerb}, is not supported for upload")
    }

  private fun Resource.toJsonPatch(): JsonArray {
    return when (this) {
      is Binary -> {
        val jsonString =
          this.data?.value?.decodeBase64String()
            ?: error("Binary resource for PATCH must have data")
        Json.decodeFromString<JsonArray>(jsonString)
      }
      else -> error("This resource cannot have the PATCH operation be applied to it")
    }
  }
}
