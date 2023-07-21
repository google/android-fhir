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

import com.google.android.fhir.sync.BundleDownloadRequest
import com.google.android.fhir.sync.BundleUploadRequest
import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.sync.DownloadRequest
import com.google.android.fhir.sync.UploadRequest
import com.google.android.fhir.sync.UrlDownloadRequest
import org.hl7.fhir.r4.model.Resource

/**
 * Implementation of [DataSource] to sync data with the FHIR server using HTTP method calls.
 * @param fhirHttpService Http service to make requests to the server.
 */
internal class FhirHttpDataSource(private val fhirHttpService: FhirHttpService) : DataSource {

  override suspend fun download(downloadRequest: DownloadRequest) =
    when (downloadRequest) {
      is UrlDownloadRequest -> fhirHttpService.get(downloadRequest.url, downloadRequest.headers)
      is BundleDownloadRequest ->
        fhirHttpService.post(downloadRequest.bundle, downloadRequest.headers)
    }

  override suspend fun upload(request: UploadRequest): Resource =
    fhirHttpService.post((request as BundleUploadRequest).bundle, request.headers)
}
