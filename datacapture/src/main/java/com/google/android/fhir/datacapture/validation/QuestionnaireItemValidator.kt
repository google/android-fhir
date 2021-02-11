package com.google.android.fhir.datacapture.validation

import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse

interface QuestionnaireItemValidator {

    /**
     * Validates the input data as per the ViewHolder and returns the result and its resultant error messages (if any)
     */
    fun validate(questionnaireItem: Questionnaire.Item, questionnaireResponseItemBuilder: QuestionnaireResponse.Item.Builder): ValidationResult

    companion object {
        const val EXTENSION_MAX_VALUE = "http://hl7.org/fhir/StructureDefinition/maxValue"
        const val EXTENSION_MIN_VALUE = "http://hl7.org/fhir/StructureDefinition/minValue"
    }
}

data class ValidationResult(val pass: Boolean, val errorMsgs: List<String>)

