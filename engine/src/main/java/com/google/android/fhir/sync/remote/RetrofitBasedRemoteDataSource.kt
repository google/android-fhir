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

import com.google.android.fhir.sync.DataSource
import org.hl7.fhir.r4.model.Bundle

internal class RetrofitBasedRemoteDataSource(private val remoteFhirService: RemoteFhirService) :
  DataSource {
  override suspend fun download(path: String) = remoteFhirService.get(path)

  override suspend fun download(bundle: Bundle) = remoteFhirService.post(bundle)

  override suspend fun upload(bundle: Bundle) = remoteFhirService.post(bundle)
}
