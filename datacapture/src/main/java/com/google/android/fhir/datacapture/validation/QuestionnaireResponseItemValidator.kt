package com.google.android.fhir.datacapture.validation

import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse

object QuestionnaireResponseItemValidator {

    private val validators = mutableListOf(MaxValueValidator,MinValueValidator)
    /**
     * Validates [questionnaireResponseItemBuilder] contains valid answer(s) to [questionnaireItem].
     */
    fun validate(questionnaireItem: Questionnaire.Item, questionnaireResponseItemBuilder: QuestionnaireResponse.Item.Builder): List<ValidationResult> {
        if (questionnaireResponseItemBuilder.answerBuilderList.isEmpty()) {
            return listOf(ValidationResult(true, null))
        }
        val validationResults = mutableListOf<ValidationResult>()
        validators.forEach {
            validationResults.add(it.validate(questionnaireItem, questionnaireResponseItemBuilder))
        }
        return validationResults
    }

    data class ValidationResult(val isValid: Boolean, val message: String?)
}

