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

import com.google.android.fhir.sync.BundleRequest
import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.sync.Request
import com.google.android.fhir.sync.UrlRequest

/**
 * Implementation of [DataSource] to sync data with the FHIR server using HTTP method calls.
 * @param fhirHttpService Http service to make requests to the server.
 */
internal class FhirHttpDataSource(private val fhirHttpService: FhirHttpService) : DataSource {

  override suspend fun download(request: Request) =
    when (request) {
      is UrlRequest -> fhirHttpService.get(request.url, request.headers)
      is BundleRequest -> fhirHttpService.post(request.bundle, request.headers)
    }

  override suspend fun upload(request: BundleRequest) =
    fhirHttpService.post(request.bundle, request.headers)
}
