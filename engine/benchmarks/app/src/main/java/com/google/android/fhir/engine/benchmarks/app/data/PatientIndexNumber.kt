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

package com.google.android.fhir.engine.benchmarks.app.data

import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.SearchParameter

val patientIndexNumberCustomSearchParameter =
  SearchParameter().apply {
    url = "MEDICATION_SORT_URL"
    addBase("Patient")
    name = "patient-index-number"
    code = "patient-index-number"
    type = Enumerations.SearchParamType.NUMBER
    expression = "Patient.extension.where(url = '$PATIENT_INDEX_NUMBER_EXTENSION_URL').value"
    description = "Search patient index number"
  }

const val PATIENT_INDEX_NUMBER_EXTENSION_URL =
  "http://example.org/fhir/StructureDefinition/patient-indexNumber"
