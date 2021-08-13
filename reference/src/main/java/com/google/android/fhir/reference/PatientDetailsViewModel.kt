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
import android.content.res.Resources
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.search.search
import org.hl7.fhir.r4.model.Condition
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
  val livePatientCondition = liveData { emit(getPatientConditions()) }

  private suspend fun getPatient(): PatientListViewModel.PatientItem {
    val patient = fhirEngine.load(Patient::class.java, patientId)
    return patient.toPatientItem(0)
  }

  private suspend fun getPatientObservations(): List<PatientListViewModel.ObservationItem> {
    val observations: MutableList<PatientListViewModel.ObservationItem> = mutableListOf()
    fhirEngine
      .search<Observation> { filter(Observation.SUBJECT) { value = "Patient/$patientId" } }
      .take(MAX_RESOURCE_COUNT)
      .mapIndexed { index, fhirPatient ->
        createObservationItem(index + 1, fhirPatient, getApplication<Application>().resources)
      }
      .let { observations.addAll(it) }
    return observations
  }

  private suspend fun getPatientConditions(): List<PatientListViewModel.ConditionItem> {
    val observations: MutableList<PatientListViewModel.ConditionItem> = mutableListOf()
    fhirEngine
      .search<Condition> { filter(Condition.SUBJECT) { value = "Patient/$patientId" } }
      .take(MAX_RESOURCE_COUNT)
      .mapIndexed { index, fhirPatient ->
        createConditionItem(index + 1, fhirPatient, getApplication<Application>().resources)
      }
      .let { observations.addAll(it) }
    return observations
  }

  companion object {
    /**
     * Creates ObservationItem objects with displayable values from the Fhir Observation objects.
     */
    private fun createObservationItem(
      position: Int,
      observation: Observation,
      resources: Resources
    ): PatientListViewModel.ObservationItem {
      val observationCode = observation.code.text ?: observation.code.codingFirstRep.display

      // Show nothing if no values available for datetime and value quantity.
      val dateTimeStr =
        if (observation.hasEffectiveDateTimeType())
          observation.effectiveDateTimeType.asStringValue()
        else resources.getText(R.string.message_no_datetime).toString()
      val value =
        if (observation.hasValueQuantity()) observation.valueQuantity.value.toString()
        else resources.getText(R.string.message_no_value_quantity).toString()
      val valueUnit =
        if (observation.hasValueQuantity())
          observation.valueQuantity.unit ?: observation.valueQuantity.code
        else ""
      val valueString = "$value $valueUnit"

      return PatientListViewModel.ObservationItem(
        position.toString(),
        observationCode,
        dateTimeStr,
        valueString
      )
    }

    /** Creates ConditionItem objects with displayable values from the Fhir Condition objects. */
    private fun createConditionItem(
      position: Int,
      condition: Condition,
      resources: Resources
    ): PatientListViewModel.ConditionItem {
      // val observation: Observation = getObservationDetails(position)
      val observationCode = condition.code.text ?: condition.code.codingFirstRep.display ?: ""

      // Show nothing if no values available for datetime and value quantity.
      val dateTimeString =
        if (condition.hasOnsetDateTimeType()) condition.onsetDateTimeType.asStringValue()
        else resources.getText(R.string.message_no_datetime).toString()
      val value =
        if (condition.hasVerificationStatus()) condition.verificationStatus.codingFirstRep.code
        else resources.getText(R.string.message_no_value_quantity).toString()

      return PatientListViewModel.ConditionItem(
        position.toString(),
        observationCode,
        dateTimeString,
        value
      )
    }
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
