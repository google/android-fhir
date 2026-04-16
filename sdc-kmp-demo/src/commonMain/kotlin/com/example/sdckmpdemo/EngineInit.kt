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

package com.example.sdckmpdemo

import com.google.android.fhir.FhirEngineConfiguration
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.ServerConfiguration
import com.google.android.fhir.registerResourceType
import com.google.fhir.model.r4.Bundle
import com.google.fhir.model.r4.Patient
import com.google.fhir.model.r4.terminologies.ResourceType

fun initializeFhirEngine(serverBaseUrl: String? = null) {
  registerResourceType(Patient::class, ResourceType.Patient)
  registerResourceType(Bundle::class, ResourceType.Bundle)
  FhirEngineProvider.init(
    FhirEngineConfiguration(
      serverConfiguration = serverBaseUrl?.let { ServerConfiguration(baseUrl = it) },
    ),
  )
}
