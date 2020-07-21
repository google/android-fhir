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

package com.google.fhirengine.example.data

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.parser.IParser
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
    private val patients: MutableList<PatientItem> = ArrayList()
    // Maps a temporary patient display id to PatientItem, used to display patients both in list and
    // details.
    private val idsPatients: MutableMap<String, PatientItem> = mutableMapOf()

    private val observations: MutableList<ObservationItem> = ArrayList()
    private val observationsMap: MutableMap<String, ObservationItem> = mutableMapOf()

    // The resource bundle with Patient objects.
    private var fhirBundle: Bundle? = null

    companion object {
        val tag = "SamplePatient"
        //val foo = FhirContext.forR4().
        val fhirJsonParser: IParser = FhirContext.forR4().newJsonParser()
    }
    /**
     * Returns list of PatientItem objects based on patients from the json string.
     */
    fun getPatientItems(jsonString: String): List<PatientItem> {
        fhirBundle = fhirJsonParser.parseResource(Bundle::class.java,
            jsonString) as Bundle

        // Create a list of PatientItems from fhirPatients. The display index is 1 based.
        fhirBundle?.entry?.take(MAX_RESOURCE_COUNT)?.mapIndexed { index, entry ->
            createPatientItem(index + 1, entry.resource as Patient)
        }?.let { patients.addAll(it) }

        // Create the PatientItems Map from PatientItem List.
        idsPatients.putAll(patients.associateBy { it.id })

        return patients
    }

    fun getPatientsMap(): Map<String, PatientItem> {
        return idsPatients
    }
    fun getObservationsMap(): Map<String, ObservationItem> {
        return observationsMap
    }

    /**
     * Creates PatientItem objects with displayable values from the Fhir Patient objects.
     */
    private fun createPatientItem(position: Int, patient: Patient): PatientItem {
        val name = patient.name[0].nameAsSingleString

        // Show nothing if no values available for gender and date of birth.
        val gender = if (patient.hasGenderElement()) patient.genderElement.valueAsString else ""
        val dob = if (patient.hasBirthDateElement()) patient.birthDateElement.valueAsString else ""
        val html: String = if (patient.hasText()) patient.text.div.valueAsString else ""
        val phone: String = if (patient.hasTelecom()) patient.telecom[0].value else ""

        return PatientItem(position.toString(), name, gender, dob, html, phone)
    }

    /**
     * The Patient's details for display purposes.
     */
    data class PatientItem(val id: String, val name: String, val gender: String, val dob: String,
        val html: String, val phone: String) {
        override fun toString(): String = name
    }

    /**
     * Returns list of ObservationItem objects based on observations from the json string.
     */
    fun getObservationItems(jsonString: String): MutableList<ObservationItem> {
        fhirBundle = fhirJsonParser.parseResource(Bundle::class.java, jsonString) as Bundle
        (1..fhirBundle!!.entry.size).forEach {
            addObservationItem(createObservationItem(it))
        }
        return observations
    }

    private fun addObservationItem(item: ObservationItem) {
        observations.add(item)
        observationsMap[item.id] = item
    }

    /**
     * Creates ObservationItem objects with displayable values from the Fhir Observation objects.
     */
    private fun createObservationItem(position: Int): ObservationItem {
        val observation: Observation = getObservationDetails(position)
        val observationCode  = observation.code.text

        // Show nothing if no values available for gender and date of birth.
        val dateTimeStr = if (observation.hasEffectiveDateTimeType()) observation.effectiveDateTimeType.asStringValue() else "No effective DateTime"
        var value = if (observation.hasValueQuantity()) observation.valueQuantity.value.toString() else "No ValueQuantity"
        var valueUnit = if (observation.hasValueQuantity()) observation.valueQuantity.unit else ""
        val valueStr = "$value $valueUnit"

        return ObservationItem(
            position.toString(),
            observationCode,
            dateTimeStr,
            valueStr
        )
    }

    /**
     * Extracts observation details from the Fhir resources bundle.
     */
    private fun getObservationDetails(position: Int): Observation {
        var observation = Observation()
        if (position <= MAX_RESOURCE_COUNT) {
            observation = fhirBundle!!.entry[position - 1].resource as Observation
        } else {
            observation.code.text = "Fhir Observation $position"
        }
        return observation
    }
    /**
     * The Observation's details for display purposes.
     */
    data class ObservationItem(val id: String, val code: String, val effective: String, val value: String) {
        override fun toString(): String = code
    }
}
