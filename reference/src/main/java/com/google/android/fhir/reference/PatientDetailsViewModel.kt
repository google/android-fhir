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
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.search.search
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient

/**
 * The ViewModel helper class for PatientItemRecyclerViewAdapter, that is responsible for preparing
 * data for UI.
 */
class PatientDetailsViewModel(
  application: Application,
  private val fhirEngine: FhirEngine,
  private val patientId: String
) : AndroidViewModel(application) {

  val livePatientData = liveData { emit(getPatient()) }
  val livePatientObservation = liveData { emit(getPatientObservations()) }

  private suspend fun getPatient(): PatientListViewModel.PatientItem {
    val patient = fhirEngine.load(Patient::class.java, patientId)
    return patient.toPatientItem(0)
  }

  private suspend fun getPatientObservations(): List<PatientListViewModel.ObservationItem> {
    val observations: MutableList<PatientListViewModel.ObservationItem> = mutableListOf()
    fhirEngine
      .search<Observation> { filter(Observation.SUBJECT) { value = "Patient/$patientId" } }
      .take(MAX_RESOURCE_COUNT)
      .mapIndexed { index, fhirPatient -> createObservationItem(index + 1, fhirPatient) }
      .let { observations.addAll(it) }
    return observations
  }

  /** Creates ObservationItem objects with displayable values from the Fhir Observation objects. */
  private fun createObservationItem(
    position: Int,
    observation: Observation
  ): PatientListViewModel.ObservationItem {
    // val observation: Observation = getObservationDetails(position)
    val observationCode = observation.code.text

    // Show nothing if no values available for datetime and value quantity.
    val dateTimeStr =
      if (observation.hasEffectiveDateTimeType()) observation.effectiveDateTimeType.asStringValue()
      else "No effective DateTime"
    val value =
      if (observation.hasValueQuantity()) observation.valueQuantity.value.toString()
      else "No ValueQuantity"
    val valueUnit = if (observation.hasValueQuantity()) observation.valueQuantity.unit else ""
    val valueStr = "$value $valueUnit"

    return PatientListViewModel.ObservationItem(
      position.toString(),
      observationCode,
      dateTimeStr,
      valueStr
    )
  }
}

class PatientDetailsViewModelFactory(
  private val application: Application,
  private val fhirEngine: FhirEngine,
  private val patientId: String
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    require(modelClass.isAssignableFrom(PatientDetailsViewModel::class.java)) {
      "Unknown ViewModel class"
    }
    return PatientDetailsViewModel(application, fhirEngine, patientId) as T
  }
}
