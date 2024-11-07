/*
 * Copyright 2024 Google LLC
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

package com.google.android.fhir.demo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.delete
import com.google.android.fhir.demo.helpers.PatientCreationHelper
import com.google.android.fhir.demo.helpers.PatientUiState
import com.google.android.fhir.demo.helpers.toPatientUiState
import java.util.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType

class CrudOperationViewModel(application: Application) : AndroidViewModel(application) {
  private val fhirEngine: FhirEngine = FhirEngineProvider.getInstance(application)

  private val _patientUiState = MutableStateFlow<PatientUiState?>(null)
  val patientUiState: StateFlow<PatientUiState?> = _patientUiState.asStateFlow()

  fun getPatientById(patientId: String) {
    viewModelScope.launch {
      val patient = fhirEngine.get(ResourceType.Patient, patientId) as Patient
      _patientUiState.value = patient.toPatientUiState(isReadOperation = true)
    }
  }

  fun createPatient(
    patientId: String,
    firstName: String,
    lastName: String = "Unknown",
    birthDate: String = "1980-01-01",
    gender: Enumerations.AdministrativeGender = Enumerations.AdministrativeGender.OTHER,
    isActive: Boolean = true,
  ) {
    // Launch coroutine to create patient
    viewModelScope.launch {
      val patient =
        PatientCreationHelper.createPatient(
          patientId = patientId,
          firstName = firstName,
          lastName = lastName,
          birthDate = birthDate,
          gender = gender,
          isActive = isActive,
        )
      fhirEngine.create(patient).firstOrNull()?.let {
        _patientUiState.value = patient.toPatientUiState()
      }
    }
  }

  fun updatePatient(
    patientId: String,
    firstName: String,
    lastName: String = "Unknown",
    birthDate: String = "1980-01-01",
    gender: Enumerations.AdministrativeGender = Enumerations.AdministrativeGender.OTHER,
    isActive: Boolean = true,
  ) {
    // Launch coroutine to create patient
    viewModelScope.launch {
      val patient =
        PatientCreationHelper.createPatient(
          patientId = patientId,
          firstName = firstName,
          lastName = lastName,
          birthDate = birthDate,
          gender = gender,
          isActive = isActive,
        )
      fhirEngine.update(patient)
      _patientUiState.value = patient.toPatientUiState()
    }
  }

  fun deletePatient(patientLogicalId: String) {
    viewModelScope.launch {
      fhirEngine.delete<Patient>(patientLogicalId)
      _patientUiState.value = Patient().toPatientUiState()
    }
  }

  fun getPatientId(): String {
    return UUID.randomUUID().toString()
  }

  //  fun updatePatient(patientUpdateData: PatientUpdateData) {
  //    // Launch coroutine to update patient
  //    viewModelScope.launch {
  //      val patient = PatientCreationHelper.createPatient(
  //        patientId = patientUpdateData.patientId,
  //        firstName = patientUpdateData.firstName,
  //        lastName = patientUpdateData.lastName,
  //        birthDate = patientUpdateData.birthDate,
  //        gender = patientUpdateData.gender,
  //        isActive = patientUpdateData.isActive,
  //      )
  //      fhirEngine.update(patient)
  //      _patientUiState.value = patient.toPatientUiState()
  //    }
  //  }

  //  fun deletePatient() {
  //    // Launch coroutine to delete patient
  //  }
}

// data class PatientUpdateData(
//  val patientId: String,
//  val firstName: String,
//  val lastName: String,
//  val birthDate: String,
//  val gender: Enumerations.AdministrativeGender,
//  val isActive: Boolean
// )
