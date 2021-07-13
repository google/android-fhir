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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.search.Order
import com.google.android.fhir.search.StringFilterModifier
import com.google.android.fhir.search.count
import com.google.android.fhir.search.search
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Patient

/**
 * The ViewModel helper class for PatientItemRecyclerViewAdapter, that is responsible for preparing
 * data for UI.
 */
class PatientListViewModel(application: Application, private val fhirEngine: FhirEngine) :
  AndroidViewModel(application) {

  val liveSearchedPatients = MutableLiveData<List<PatientItem>>()
  val patientCount = liveData { emit(count()) }

  init {
    fetchAndPost { getSearchResults() }
  }

  fun searchPatientsByName(nameQuery: String) {
    fetchAndPost { getSearchResults(nameQuery) }
  }

  private fun fetchAndPost(search: suspend () -> List<PatientItem>) {
    viewModelScope.launch { liveSearchedPatients.value = search() }
  }

  private suspend fun count(): Long {
    return fhirEngine.count<Patient> {
      filter(Patient.ADDRESS_CITY) {
        modifier = StringFilterModifier.MATCHES_EXACTLY
        value = "NAIROBI"
      }
    }
  }

  private suspend fun getSearchResults(nameQuery: String = ""): List<PatientItem> {
    val patients: MutableList<PatientItem> = mutableListOf()
    fhirEngine
      .search<Patient> {
        if (nameQuery.isNotEmpty())
          filter(Patient.NAME) {
            modifier = StringFilterModifier.CONTAINS
            value = nameQuery
          }
        sort(Patient.GIVEN, Order.ASCENDING)
        count = 100
        from = 0
      }
      .take(MAX_RESOURCE_COUNT)
      .mapIndexed { index, fhirPatient -> fhirPatient.toPatientItem(index + 1) }
      .let { patients.addAll(it) }
    return patients
  }

  /** The Patient's details for display purposes. */
  data class PatientItem(
    val id: String,
    val resourceId: String,
    val name: String,
    val gender: String,
    val dob: String,
    val phone: String,
    val city: String,
    val country: String,
    val isActive: Boolean,
    val html: String,
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

  class PatientListViewModelFactory(
    private val application: Application,
    private val fhirEngine: FhirEngine
  ) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(PatientListViewModel::class.java)) {
        return PatientListViewModel(application, fhirEngine) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}

internal fun Patient.toPatientItem(position: Int): PatientListViewModel.PatientItem {
  // Show nothing if no values available for gender and date of birth.
  val patientId = if (hasIdElement()) idElement.idPart else ""
  val name = if (hasName()) name[0].nameAsSingleString else ""
  val gender = if (hasGenderElement()) genderElement.valueAsString else ""
  val dob = if (hasBirthDateElement()) birthDateElement.valueAsString else ""
  val phone = if (hasTelecom()) telecom[0].value else ""
  val city = if (hasAddress()) address[0].city else ""
  val country = if (hasAddress()) address[0].country else ""
  val isActive = active
  val html: String = if (hasText()) text.div.valueAsString else ""

  return PatientListViewModel.PatientItem(
    id = position.toString(),
    resourceId = patientId,
    name = name,
    gender = gender,
    dob = dob,
    phone = phone,
    city = city,
    country = country,
    isActive = isActive,
    html = html
  )
}
