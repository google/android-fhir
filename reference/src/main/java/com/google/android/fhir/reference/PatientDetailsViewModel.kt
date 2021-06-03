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
import com.google.android.fhir.reference.data.SamplePatients
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

  private val samplePatients = SamplePatients()

  val livePatientData = liveData { emit(getPatient()) }
  val livePatientObservation = liveData { emit(getPatientObservations()) }

  private suspend fun getPatient(): PatientListViewModel.PatientItem? {
    val patient = fhirEngine.load(Patient::class.java, patientId)
    return samplePatients.getPatientItems(listOf(patient))?.first()
  }

  private suspend fun getPatientObservations(): List<PatientListViewModel.ObservationItem> {
    val observations =
      fhirEngine.search<Observation> {
        filter(Observation.SUBJECT) { value = "Patient/$patientId" }
      }
    return samplePatients.getObservationItems(observations)
  }
}

class PatientDetailsViewModelFactory(
  private val application: Application,
  private val fhirEngine: FhirEngine,
  private val patientId: String
) : ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(PatientDetailsViewModel::class.java)) {
      return PatientDetailsViewModel(application, fhirEngine, patientId) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
