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

package com.google.android.fhir.reference

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.reference.data.SamplePatients
import com.google.android.fhir.search.Order
import com.google.android.fhir.search.search
import org.hl7.fhir.r4.model.Patient

/**
 * The ViewModel helper class for PatientItemRecyclerViewAdapter, that is responsible for preparing
 * data for UI.
 */
class PatientListViewModel(application: Application, private val fhirEngine: FhirEngine) :
  AndroidViewModel(application) {

  private val samplePatients = SamplePatients()

  val liveSearchedPatients = liveData { emit(getSearchResults()) }

  private suspend fun getSearchResults(): List<PatientItem> {
    val searchResults: List<Patient> =
      fhirEngine.search {
        filter(Patient.ADDRESS_CITY) {
          prefix = ParamPrefixEnum.EQUAL
          value = "NAIROBI"
        }
        filter(Patient.ACTIVE) { value = true }
        sort(Patient.GIVEN, Order.ASCENDING)
        count = 100
        from = 0
      }
    return samplePatients.getPatientItems(searchResults)
  }

  /** The Patient's details for display purposes. */
  data class PatientItem(
    val id: String,
    val name: String,
    val gender: String,
    val dob: String,
    val html: String,
    val phone: String
  ) {
    override fun toString(): String = name
  }

  /** The Observation's details for display purposes. */
  data class ObservationItem(
    val id: String,
    val code: String,
    val effective: String,
    val value: String
  ) {
    override fun toString(): String = code
  }
}

class PatientListViewModelFactory(
  private val application: Application,
  private val fhirEngine: FhirEngine
) : ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(PatientListViewModel::class.java)) {
      return PatientListViewModel(application, fhirEngine) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
