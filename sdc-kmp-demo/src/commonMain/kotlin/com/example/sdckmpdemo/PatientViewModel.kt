/*
 * Copyright 2025 Google LLC
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

import android_fhir.sdc_kmp_demo.generated.resources.Res
import com.google.fhir.model.r4.FhirR4Json
import com.google.fhir.model.r4.Patient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray

private val json = Json { prettyPrint = true }

class PatientViewModel {
  private val _patients = MutableStateFlow<List<Patient>>(emptyList())
  val patients: StateFlow<List<Patient>> = _patients.asStateFlow()

  init {
    CoroutineScope(Dispatchers.Main).launch {
      val jsonString = Res.readBytes("files/list.json").decodeToString()
      val jsonArray = json.parseToJsonElement(jsonString) as JsonArray
      val patients =
        jsonArray.map { patientJson ->
          FhirR4Json().decodeFromString(json.encodeToString(patientJson)) as Patient
        }
      _patients.update { patients }
    }
  }
}
