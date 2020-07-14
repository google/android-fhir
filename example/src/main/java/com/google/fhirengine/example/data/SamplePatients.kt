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
import org.hl7.fhir.r4.model.Bundle
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

    // The resource bundle with Patient objects.
    private var fhirBundle: Bundle? = null

    companion object {
        val fhirJsonParser = FhirContext.forR4().newJsonParser()
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

    /**
     * Creates PatientItem objects with displayable values from the Fhir Patient objects.
     */
    private fun createPatientItem(position: Int, patient: Patient): PatientItem {
        val name = patient.name[0].nameAsSingleString

        // Show nothing if no values available for gender and date of birth.
        val gender = if (patient.hasGenderElement()) patient.genderElement.valueAsString else ""
        val dob = if (patient.hasBirthDateElement()) patient.birthDateElement.valueAsString else ""

        return PatientItem(position.toString(), name, gender, dob)
    }

    /**
     * The Patient's details for display purposes.
     */
    data class PatientItem(val id: String, val name: String, val gender: String, val dob: String) {
        override fun toString(): String = name
    }
}
