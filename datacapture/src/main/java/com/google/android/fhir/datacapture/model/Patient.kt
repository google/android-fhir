package com.google.android.fhir.datacapture.model

import org.hl7.fhir.r4.model.Enumeration
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.StringType

class Patient : org.hl7.fhir.r4.model.Patient() {
    fun setGenderElement(value: StringType?): Patient {
        if (value == null) gender = null else {
            val administrativeGender = Enumerations.AdministrativeGender.fromCode(value.toString())
            if (gender == null) gender = Enumeration(Enumerations.AdministrativeGenderEnumFactory())
            gender.setValue(administrativeGender)
        }
        return this
    }
}