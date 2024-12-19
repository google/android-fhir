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
import android.icu.text.SimpleDateFormat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.datacapture.extensions.logicalId
import com.google.android.fhir.delete
import com.google.android.fhir.demo.helpers.PatientCreationHelper
import java.util.Locale
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType

class CrudOperationViewModel(application: Application) : AndroidViewModel(application) {
  private val fhirEngine: FhirEngine = FhirEngineProvider.getInstance(application)
  private val _patientUiState = MutableSharedFlow<PatientUiState>(replay = 0)
  var currentPatientLogicalId: String? = null
    private set

  val patientUiState: SharedFlow<PatientUiState> = _patientUiState.asSharedFlow()

  fun readPatientById(patientLogicalId: String) {
    viewModelScope.launch {
      val patient = fhirEngine.get(ResourceType.Patient, patientLogicalId) as Patient
      _patientUiState.emit(patient.toPatientUiState(OperationType.READ))
    }
  }

  fun createPatient(
    patientId: String,
    firstName: String,
    lastName: String? = null,
    birthDate: String? = null,
    gender: Enumerations.AdministrativeGender = Enumerations.AdministrativeGender.OTHER,
    isActive: Boolean = false,
  ) {
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
        currentPatientLogicalId = patientId
        _patientUiState.emit(patient.toPatientUiState(OperationType.CREATE))
      }
    }
  }

  fun updatePatient(
    patientLogicalId: String,
    firstName: String,
    lastName: String? = null,
    birthDate: String? = null,
    gender: Enumerations.AdministrativeGender = Enumerations.AdministrativeGender.OTHER,
    isActive: Boolean = false,
  ) {
    viewModelScope.launch {
      val patient =
        PatientCreationHelper.createPatient(
          patientId = patientLogicalId,
          firstName = firstName,
          lastName = lastName,
          birthDate = birthDate,
          gender = gender,
          isActive = isActive,
        )
      fhirEngine.update(patient)
      _patientUiState.emit(patient.toPatientUiState(OperationType.UPDATE))
    }
  }

  fun deletePatient(patientLogicalId: String) {
    viewModelScope.launch {
      fhirEngine.delete<Patient>(patientLogicalId)
      currentPatientLogicalId = null
      _patientUiState.emit(Patient().toPatientUiState(OperationType.DELETE))
    }
  }
}

data class PatientUiState(
  val patientId: String,
  val firstName: String,
  val lastName: String? = null,
  val birthDate: String? = null,
  val gender: Enumerations.AdministrativeGender = Enumerations.AdministrativeGender.OTHER,
  val isActive: Boolean = true,
  val operationType: OperationType,
)

fun Patient.toPatientUiState(operationType: OperationType): PatientUiState {
  val patientId = this.logicalId
  val firstName = this.name?.firstOrNull()?.given?.firstOrNull()?.value ?: ""
  val lastName = this.name?.firstOrNull()?.family
  val birthDate =
    if (hasBirthDateElement()) {
      SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(this.birthDate)
    } else {
      null
    }
  val gender = this.gender ?: Enumerations.AdministrativeGender.OTHER
  val isActive = this.active
  return PatientUiState(
    patientId,
    firstName,
    lastName,
    birthDate,
    gender = gender,
    isActive = isActive,
    operationType = operationType,
  )
}

enum class OperationType {
  CREATE,
  READ,
  UPDATE,
  DELETE,
}
