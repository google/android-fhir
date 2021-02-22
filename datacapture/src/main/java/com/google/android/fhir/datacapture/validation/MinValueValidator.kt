package com.google.android.fhir.datacapture.validation

import com.google.fhir.r4.core.Extension
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse

object MinValueValidator : ValidationInterface {

    private const val EXTENSION_MIN_VALUE = "http://hl7.org/fhir/StructureDefinition/minValue"

    override fun validate(questionnaireItem: Questionnaire.Item, questionnaireResponseItemBuilder: QuestionnaireResponse.Item.Builder): List<QuestionnaireItemValidator.ValidationResult> {
        val validationResults = mutableListOf<QuestionnaireItemValidator.ValidationResult>()
        val extension = hasExtension(EXTENSION_MIN_VALUE, questionnaireItem)
                ?: return listOf(QuestionnaireItemValidator.ValidationResult(true, emptyList()))
        validationResults.add(minValueIntegerValidator(extension, questionnaireResponseItemBuilder))
        return validationResults
    }

    private fun minValueIntegerValidator(extension: Extension, questionnaireResponseItemBuilder: QuestionnaireResponse.Item.Builder): QuestionnaireItemValidator.ValidationResult {
        val response = questionnaireResponseItemBuilder.getAnswerBuilder(0).value
        if (extension.value.hasInteger() && response.hasInteger()) {
            val answer = questionnaireResponseItemBuilder.getAnswerBuilder(0).value.integer.value
            if (answer < extension.value.integer.value) {
                return QuestionnaireItemValidator.ValidationResult(false, validationMessageGenerator(extension))
            }
        }
        return QuestionnaireItemValidator.ValidationResult(true, emptyList())
    }

    override fun validationMessageGenerator(extension: Extension): List<String> {
        return listOf("Minimum value allowed is:" + extension.value.integer.value)
    }
}