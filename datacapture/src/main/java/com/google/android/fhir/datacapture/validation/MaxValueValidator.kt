package com.google.android.fhir.datacapture.validation

import com.google.fhir.r4.core.Extension
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse

object MaxValueValidator : ValidationInterface {

    private const val EXTENSION_MAX_VALUE = "http://hl7.org/fhir/StructureDefinition/maxValue"

    override fun validate(questionnaireItem: Questionnaire.Item, questionnaireResponseItemBuilder: QuestionnaireResponse.Item.Builder): List<QuestionnaireItemValidator.ValidationResult> {
        val validationResults = mutableListOf<QuestionnaireItemValidator.ValidationResult>()
        val extension = hasExtension(EXTENSION_MAX_VALUE, questionnaireItem)
                ?: return listOf(QuestionnaireItemValidator.ValidationResult(true, emptyList()))
        validationResults.add(maxValueIntegerValidator(extension, questionnaireResponseItemBuilder))
        return validationResults
    }

    private fun maxValueIntegerValidator(extension: Extension, questionnaireResponseItemBuilder: QuestionnaireResponse.Item.Builder): QuestionnaireItemValidator.ValidationResult {
        val response = questionnaireResponseItemBuilder.getAnswerBuilder(0).value
        if (extension.value.hasInteger() && response.hasInteger()) {
            val answer = questionnaireResponseItemBuilder.getAnswerBuilder(0).value.integer.value
            if (answer > extension.value.integer.value) {
                return QuestionnaireItemValidator.ValidationResult(false, validationMessageGenerator(extension))
            }
        }
        return QuestionnaireItemValidator.ValidationResult(true, emptyList())
    }

    override fun validationMessageGenerator(extension: Extension): List<String> {
        return listOf("Maximum value allowed is:" + extension.value.integer.value)
    }
}