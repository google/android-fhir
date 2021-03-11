package com.google.android.fhir.datacapture.validation

import com.google.fhir.r4.core.Extension
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse

object MinValueValidator : ConstraintValidator {

    private const val MIN_VALUE_EXTENSION_URL = "http://hl7.org/fhir/StructureDefinition/minValue"

    override fun validate(
            questionnaireItem: Questionnaire.Item,
            questionnaireResponseItemBuilder: QuestionnaireResponse.Item.Builder
    ): QuestionnaireResponseItemValidator.ValidationResult {

        val extension = questionnaireItem.getExtensionsByUrl(MIN_VALUE_EXTENSION_URL).firstOrNull()
                ?: return QuestionnaireResponseItemValidator.ValidationResult(true, null)
        return minValueIntegerValidator(extension, questionnaireResponseItemBuilder)
    }

    private fun minValueIntegerValidator(extension: Extension, questionnaireResponseItemBuilder: QuestionnaireResponse.Item.Builder): QuestionnaireResponseItemValidator.ValidationResult {
        val response = questionnaireResponseItemBuilder.getAnswerBuilder(0).value
        when {
            extension.value.hasInteger() && response.hasInteger() -> {
                val answer = questionnaireResponseItemBuilder.getAnswerBuilder(0).value.integer.value
                if (answer < extension.value.integer.value) {
                    return QuestionnaireResponseItemValidator.ValidationResult(false, validationMessageGenerator(extension))
                }
            }
        }
        return QuestionnaireResponseItemValidator.ValidationResult(true, null)
    }

    private fun validationMessageGenerator(extension: Extension): String {
        return "Minimum value allowed is:" + extension.value.integer.value
    }
}