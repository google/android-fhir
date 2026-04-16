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

import android_fhir.sdc_kmp_demo.generated.resources.Res
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.search.search
import com.google.fhir.model.r4.FhirR4Json
import com.google.fhir.model.r4.Patient
import com.google.fhir.model.r4.terminologies.ResourceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray

private val json = Json { prettyPrint = true }

class PatientViewModel : ViewModel() {
  private val fhirEngine = FhirEngineProvider.getInstance()

  private val _patients = MutableStateFlow<List<Patient>>(emptyList())
  val patients: StateFlow<List<Patient>> = _patients.asStateFlow()

  init {
    viewModelScope.launch {
//      seedIfEmpty()
      refreshPatients()
    }
  }

  private suspend fun seedIfEmpty() {
    val count =
      fhirEngine.count(
        com.google.android.fhir.search.Search(ResourceType.Patient),
      )
    if (count > 0L) return

    val jsonString = Res.readBytes("files/list.json").decodeToString()
    val jsonArray = json.parseToJsonElement(jsonString) as JsonArray
    val fhirJson = FhirR4Json()
    val patients =
      jsonArray.map { patientJson ->
        fhirJson.decodeFromString(json.encodeToString(patientJson)) as Patient
      }
    fhirEngine.create(*patients.toTypedArray())
  }

  fun refreshPatients() {
    viewModelScope.launch {
      val results = fhirEngine.search<Patient> {}
      _patients.value = results.map { it.resource }
    }
  }

  fun createPatient(patient: Patient) {
    viewModelScope.launch {
      fhirEngine.create(patient)
      refreshPatients()
    }
  }

  fun updatePatient(patient: Patient) {
    viewModelScope.launch {
      fhirEngine.update(patient)
      refreshPatients()
    }
  }

  fun deletePatient(id: String) {
    viewModelScope.launch {
      fhirEngine.delete(ResourceType.Patient, id)
      refreshPatients()
    }
  }
}
