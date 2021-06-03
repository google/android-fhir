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

package com.google.android.fhir.reference.data

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.reference.PatientListViewModel
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient

private const val MAX_RESOURCE_COUNT = 20

/**
 * Helper class for loading a list of sample Fhir Patient Resource objects.
 *
 * Parses and loads Patient data from the passed JSON String into items that could be used by
 * PatientListViewModel.
 */
class SamplePatients {
  // The resource bundle with Patient objects.
  private var fhirBundle: Bundle? = null

  companion object {
    val tag = "SamplePatient"

    val fhirJsonParser: IParser = FhirContext.forR4().newJsonParser()
  }
  /** Returns list of PatientItem objects based on patients from the json string. */
  fun getPatientItems(jsonString: String): List<PatientListViewModel.PatientItem> {
    val patients: MutableList<PatientListViewModel.PatientItem> = mutableListOf()

    fhirBundle = fhirJsonParser.parseResource(Bundle::class.java, jsonString) as Bundle

    // Create a list of PatientItems from fhirPatients. The display index is 1 based.
    fhirBundle
      ?.entry
      ?.take(MAX_RESOURCE_COUNT)
      ?.mapIndexed { index, entry -> createPatientItem(index + 1, entry.resource as Patient) }
      ?.let { patients.addAll(it) }

    return patients
  }

  fun getPatientItems(fhirPatients: List<Patient>): List<PatientListViewModel.PatientItem> {
    val patients: MutableList<PatientListViewModel.PatientItem> = mutableListOf()

    // Create a list of PatientItems from fhirPatients. The display index is 1 based.
    fhirPatients
      .take(MAX_RESOURCE_COUNT)
      ?.mapIndexed { index, fhirPatient -> createPatientItem(index + 1, fhirPatient) }
      ?.let { patients.addAll(it) }

    // Return a cloned List
    return patients
  }

  /** Creates PatientItem objects with displayable values from the Fhir Patient objects. */
  private fun createPatientItem(position: Int, patient: Patient): PatientListViewModel.PatientItem {
    val name = patient.name[0].nameAsSingleString

    // Show nothing if no values available for gender and date of birth.
    val gender = if (patient.hasGenderElement()) patient.genderElement.valueAsString else ""
    val dob = if (patient.hasBirthDateElement()) patient.birthDateElement.valueAsString else ""
    val html: String = if (patient.hasText()) patient.text.div.valueAsString else ""
    val phone: String = if (patient.hasTelecom()) patient.telecom[0].value else ""

    return PatientListViewModel.PatientItem(
      position.toString(),
      name,
      gender,
      dob,
      html,
      phone,
      patient.idElement.idPart
    )
  }

  /** Returns list of ObservationItem objects based on observations from the json string. */
  fun getObservationItems(jsonString: String): MutableList<PatientListViewModel.ObservationItem> {
    val observations = ArrayList<PatientListViewModel.ObservationItem>()

    fhirBundle = fhirJsonParser.parseResource(Bundle::class.java, jsonString) as Bundle

    // Create a list of ObservationItems from fhirObservations. The display index is 1 based.
    fhirBundle
      ?.entry
      ?.take(MAX_RESOURCE_COUNT)
      ?.mapIndexed { index, entry ->
        createObservationItem(index + 1, entry.resource as Observation)
      }
      ?.let { observations.addAll(it) }

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
