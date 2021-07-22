/*
 * Copyright 2020 Google LLC
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

package com.google.android.fhir.reference.data

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.rest.gclient.StringClientParam
import com.google.android.fhir.reference.api.GcpFhirService
import com.google.android.fhir.reference.api.HapiFhirService
import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.sync.ResourceSyncParams
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType

class DataSourceFactory {
  companion object {
    private const val DEFAULT_SOURCE = "hapi"
    fun createSource(type: String = DEFAULT_SOURCE): DataSource {
      return when (type) {
        "gcp" ->
          GcpFhirResourceDataSource(GcpFhirService.create(FhirContext.forR4().newJsonParser()))
        "hapi" ->
          HapiFhirResourceDataSource(HapiFhirService.create(FhirContext.forR4().newJsonParser()))
        else -> throw IllegalArgumentException("Unknown data source type : $type ")
      }
    }

    fun createSyncData(type: String = DEFAULT_SOURCE): ResourceSyncParams {
      return when (type) {
        "gcp" -> mapOf(ResourceType.Patient to mapOf("address-country" to "US"))
        "hapi" -> mapOf(ResourceType.Patient to mapOf("address-city" to "NAIROBI"))
        else -> throw IllegalArgumentException("Unknown data source type : $type ")
      }
    }

    fun countFilterData(type: String = DEFAULT_SOURCE): Pair<StringClientParam, String> {
      return when (type) {
        "gcp" -> Patient.ADDRESS_COUNTRY to "US"
        "hapi" -> Patient.ADDRESS_CITY to "NAIROBI"
        else -> throw IllegalArgumentException("Unknown data source type : $type ")
      }
    }
  }
}
