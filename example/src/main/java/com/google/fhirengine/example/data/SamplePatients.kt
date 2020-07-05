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

/**
 * Helper class for loading a list of sample Fhir Patient Resource objects.
 *
 * Parses and loads Patient data from the passed JSON String into items that could be used by
 * PatientListViewModel.
 */
object SamplePatients {
    private val PATIENTS: MutableList<PatientItem> = ArrayList()
    val PATIENTS_MAP: MutableMap<String, PatientItem> = HashMap()

    // The resource bundle with Patient objects.
    private lateinit var fhirBundle: Bundle

    private const val MAX_RESOURCE_COUNT = 20

    /**
     * Returns list of PatientItem objects based on patients from the json string.
     */
    fun getPatientItems(jsonString: String): MutableList<PatientItem> {
        val fhirContext = FhirContext.forR4()
        fhirBundle = fhirContext.newJsonParser().parseResource(Bundle::class.java,
                jsonString) as Bundle
        for (i in 1..fhirBundle.entry.size) {
            addPatientItem(createPatientItem(i))
        }
        return PATIENTS
    }

    private fun addPatientItem(item: PatientItem) {
        PATIENTS.add(item)
        PATIENTS_MAP[item.id] = item
    }

    /**
     * Creates PatientItem objects with displayable values from the Fhir Patient objects.
     */
    private fun createPatientItem(position: Int): PatientItem {
        val patient: Patient = getPatientDetails(position)
        val name = patient.name[0].nameAsSingleString

        // Show nothing if no values available for gender and date of birth.
        val gender = if (patient.hasGenderElement()) patient.genderElement.valueAsString else ""
        val dob = if (patient.hasBirthDateElement()) patient.birthDateElement.valueAsString else ""
        val html: String = if (patient.hasText()) patient.text.div.valueAsString else ""

        return PatientItem(position.toString(), name, gender, dob, html)
    }

    /**
     * Extracts patient details from the Fhir resources bundle.
     */
    private fun getPatientDetails(position: Int): Patient {
        var patient = Patient()
        if (position <= MAX_RESOURCE_COUNT) {
            patient = fhirBundle.entry[position - 1].resource as Patient
        } else {
            patient.addName().family = "Fhir Patient $position"
        }
        return patient
    }

    /**
     * The Patient's details for display purposes.
     */
    data class PatientItem(val id: String, val name: String, val gender: String, val dob: String, val html: String) {
        override fun toString(): String = name
    }
}
