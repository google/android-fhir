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
import com.google.android.fhir.logicalId
import com.google.android.fhir.search.search
import java.util.Locale
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

  val livePatientData = liveData { emit(getPatientDetailDataModel()) }

  private suspend fun getPatient(): PatientListViewModel.PatientItem {
    val patient = fhirEngine.load(Patient::class.java, patientId)
    return patient.toPatientItem(0)
  }

  private suspend fun getPatientObservations(): List<PatientListViewModel.ObservationItem> {
    val observations: MutableList<PatientListViewModel.ObservationItem> = mutableListOf()
    fhirEngine
      .search<Observation> { filter(Observation.SUBJECT) { value = "Patient/$patientId" } }
      .take(MAX_RESOURCE_COUNT)
      .map { createObservationItem(it, getApplication<Application>().resources) }
      .let { observations.addAll(it) }
    return observations
  }

  private suspend fun getPatientConditions(): List<PatientListViewModel.ConditionItem> {
    val conditions: MutableList<PatientListViewModel.ConditionItem> = mutableListOf()
    fhirEngine
      .search<Condition> { filter(Condition.SUBJECT) { value = "Patient/$patientId" } }
      .take(MAX_RESOURCE_COUNT)
      .map { createConditionItem(it, getApplication<Application>().resources) }
      .let { conditions.addAll(it) }
    return conditions
  }

  private suspend fun getPatientDetailDataModel(): List<PatientDetailData> {
    val data = mutableListOf<PatientDetailData>()
    val patient = getPatient()

    val observations = getPatientObservations()
    val conditions = getPatientConditions()

    patient.let {
      data.add(PatientDetailOverview(it, firstInGroup = true))
      data.add(
        PatientDetailProperty(
          PatientProperty(getString(R.string.patient_property_mobile), it.phone)
        )
      )
      data.add(
        PatientDetailProperty(
          PatientProperty(getString(R.string.patient_property_id), it.resourceId)
        )
      )
      data.add(
        PatientDetailProperty(
          PatientProperty(
            getString(R.string.patient_property_address),
            "${it.city}, ${it.country} "
          )
        )
      )
      data.add(
        PatientDetailProperty(PatientProperty(getString(R.string.patient_property_dob), it.dob))
      )
      data.add(
        PatientDetailProperty(
          PatientProperty(
            getString(R.string.patient_property_gender),
            it.gender.capitalize(Locale.ROOT)
          ),
          lastInGroup = true
        )
      )
    }

    if (observations.isNotEmpty()) {
      data.add(PatientDetailHeader(getString(R.string.header_observation)))

      val observationDataModel =
        observations.mapIndexed { index, observationItem ->
          PatientDetailObservation(
            observationItem,
            firstInGroup = index == 0,
            lastInGroup = index == observations.size - 1
          )
        }
      data.addAll(observationDataModel)
    }

    if (conditions.isNotEmpty()) {
      data.add(PatientDetailHeader(getString(R.string.header_conditions)))
      val conditionDataModel =
        conditions.mapIndexed { index, conditionItem ->
          PatientDetailCondition(
            conditionItem,
            firstInGroup = index == 0,
            lastInGroup = index == conditions.size - 1
          )
        }
      data.addAll(conditionDataModel)
    }

    return data
  }

  private fun getString(resId: Int) = getApplication<Application>().resources.getString(resId)

  companion object {
    /**
     * Creates ObservationItem objects with displayable values from the Fhir Observation objects.
     */
    private fun createObservationItem(
      observation: Observation,
      resources: Resources
    ): PatientListViewModel.ObservationItem {
      val observationCode = observation.code.text ?: observation.code.codingFirstRep.display

      // Show nothing if no values available for datetime and value quantity.
      val dateTimeString =
        if (observation.hasEffectiveDateTimeType()) {
          observation.effectiveDateTimeType.asStringValue()
        } else {
          resources.getText(R.string.message_no_datetime).toString()
        }
      val value =
        if (observation.hasValueQuantity()) {
          observation.valueQuantity.value.toString()
        } else {
          resources.getText(R.string.message_no_value_quantity).toString()
        }
      val valueUnit =
        if (observation.hasValueQuantity()) {
          observation.valueQuantity.unit ?: observation.valueQuantity.code
        } else {
          ""
        }
      val valueString = "$value $valueUnit"

      return PatientListViewModel.ObservationItem(
        observation.logicalId,
        observationCode,
        dateTimeString,
        valueString
      )
    }

    /** Creates ConditionItem objects with displayable values from the Fhir Condition objects. */
    private fun createConditionItem(
      condition: Condition,
      resources: Resources
    ): PatientListViewModel.ConditionItem {
      val observationCode = condition.code.text ?: condition.code.codingFirstRep.display ?: ""

      // Show nothing if no values available for datetime and value quantity.
      val dateTimeString =
        if (condition.hasOnsetDateTimeType()) {
          condition.onsetDateTimeType.asStringValue()
        } else {
          resources.getText(R.string.message_no_datetime).toString()
        }
      val value =
        if (condition.hasVerificationStatus()) {
          condition.verificationStatus.codingFirstRep.code
        } else {
          resources.getText(R.string.message_no_value_quantity).toString()
        }

      return PatientListViewModel.ConditionItem(
        condition.logicalId,
        observationCode,
        dateTimeString,
        value
      )
    }
  }
}

interface PatientDetailData {
  val firstInGroup: Boolean
  val lastInGroup: Boolean
}

data class PatientDetailHeader(
  val header: String,
  override val firstInGroup: Boolean = false,
  override val lastInGroup: Boolean = false
) : PatientDetailData

data class PatientDetailProperty(
  val patientProperty: PatientProperty,
  override val firstInGroup: Boolean = false,
  override val lastInGroup: Boolean = false
) : PatientDetailData

data class PatientDetailOverview(
  val patient: PatientListViewModel.PatientItem,
  override val firstInGroup: Boolean = false,
  override val lastInGroup: Boolean = false
) : PatientDetailData

data class PatientDetailObservation(
  val observation: PatientListViewModel.ObservationItem,
  override val firstInGroup: Boolean = false,
  override val lastInGroup: Boolean = false
) : PatientDetailData

data class PatientDetailCondition(
  val condition: PatientListViewModel.ConditionItem,
  override val firstInGroup: Boolean = false,
  override val lastInGroup: Boolean = false
) : PatientDetailData

data class PatientProperty(val header: String, val value: String)

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
