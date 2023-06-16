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

import com.google.android.fhir.logicalId
import com.google.android.fhir.sync.BundleRequest
import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.sync.Request
import com.google.android.fhir.sync.ResourceRequest
import com.google.android.fhir.sync.UrlRequest
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Resource

/**
 * Implementation of [DataSource] to sync data with the FHIR server using HTTP method calls.
 * @param fhirHttpService Http service to make requests to the server.
 */
internal class FhirHttpDataSource(private val fhirHttpService: FhirHttpService) : DataSource {

  override suspend fun download(request: Request) =
    when (request) {
      is UrlRequest -> fhirHttpService.get(request.url, request.headers)
      is BundleRequest -> fhirHttpService.post(".", request.bundle, request.headers)
      is ResourceRequest ->
        throw java.lang.UnsupportedOperationException(
          "Download not permitted: ResourceRequest is meant for uploads only."
        )
    }

  override suspend fun upload(request: Request): Resource =
    when (request) {
      is UrlRequest ->
        throw java.lang.UnsupportedOperationException(
          "Upload not permitted: UrlRequest is meant for downloads only."
        )
      is BundleRequest -> fhirHttpService.post(".", request.bundle, request.headers)
      is ResourceRequest -> {
        if (request.http == Bundle.HTTPVerb.POST)
          fhirHttpService.post(
            request.resource.resourceType.name,
            request.resource,
            request.headers
          )
        else if (request.http == Bundle.HTTPVerb.PUT)
          fhirHttpService.put(
            "${request.resource.resourceType.name}/${request.resource.logicalId}",
            request.resource,
            request.headers
          )
        else throw IllegalArgumentException("Code goes Brr....")
      }
    }
}
