package com.google.fhirengine.example.data

import ca.uhn.fhir.context.FhirContext
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Observation

object SampleObservations {
    val OBSERVATIONS: MutableList<ObservationItem> = ArrayList()
    val OBSERVATIONS_MAP: MutableMap<String, ObservationItem> = HashMap()

    // The resource bundle with Observation objects.
    private lateinit var fhirBundle: Bundle

    private const val MAX_RESOURCE_COUNT = 20

    /**
     * Returns list of ObservationItem objects based on observations from the json string.
     */
    fun getObservationItems(jsonString: String): MutableList<ObservationItem> {
        val fhirContext = FhirContext.forR4()
        fhirBundle = fhirContext.newJsonParser().parseResource(Bundle::class.java,
                jsonString) as Bundle
        for (i in 1..fhirBundle.entry.size) {
            addObservationItem(createObservationItem(i))
        }
        return OBSERVATIONS
    }

    private fun addObservationItem(item: ObservationItem) {
        OBSERVATIONS.add(item)
        OBSERVATIONS_MAP[item.id] = item
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

        return ObservationItem(position.toString(), observationCode, dateTimeStr, valueStr)
    }

    /**
     * Extracts observation details from the Fhir resources bundle.
     */
    private fun getObservationDetails(position: Int): Observation {
        var observation = Observation()
        if (position <= MAX_RESOURCE_COUNT) {
            observation = fhirBundle.entry[position - 1].resource as Observation
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