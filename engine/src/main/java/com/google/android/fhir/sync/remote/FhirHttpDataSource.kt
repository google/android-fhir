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

package com.google.android.fhir.sync.remote

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.sync.BundleRequest
import com.google.android.fhir.sync.BundleUploadRequest
import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.sync.DeleteUploadRequest
import com.google.android.fhir.sync.PatchUploadRequest
import com.google.android.fhir.sync.PostUploadRequest
import com.google.android.fhir.sync.PutUploadRequest
import com.google.android.fhir.sync.Request
import com.google.android.fhir.sync.UploadRequest
import com.google.android.fhir.sync.UrlRequest
import org.hl7.fhir.r4.model.Resource

/**
 * Implementation of [DataSource] to sync data with the FHIR server using HTTP method calls.
 * @param fhirHttpService Http service to make requests to the server.
 */
internal class FhirHttpDataSource(private val fhirHttpService: FhirHttpService) : DataSource {

  private val fhirContext = FhirContext.forCached(FhirVersionEnum.R4)
  private val jsonParser = fhirContext.newJsonParser()

  override suspend fun download(request: Request) =
    when (request) {
      is UrlRequest -> fhirHttpService.get(request.url, request.headers)
      is BundleRequest -> fhirHttpService.post(".", request.bundle, request.headers)
    }

  override suspend fun upload(request: UploadRequest): Resource =
    when (request) {
      is BundleUploadRequest -> fhirHttpService.post(".", request.bundle, request.headers)
      is PatchUploadRequest ->
        fhirHttpService.patch(
          request.resourceType,
          request.resourceId,
          request.patchBody,
          request.headers
        )
      is PutUploadRequest ->
        fhirHttpService.put(
          request.resourceType,
          request.resourceId,
          request.resource,
          request.headers
        )
      is DeleteUploadRequest ->
        fhirHttpService.delete(request.resourceType, request.resourceId, request.headers)
      is PostUploadRequest ->
        fhirHttpService.post(request.resourceType, request.resource, request.headers)
    }
}
