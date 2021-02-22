package com.google.android.fhir.datacapture.validation

import com.google.fhir.r4.core.Extension
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse

object QuestionnaireItemValidator {

    private val validators = mutableListOf<ValidationInterface>(MaxValueValidator,MinValueValidator)
    /**
     * Validates [questionnaireResponseItemBuilder] contains valid answer(s) to [questionnaireItem].
     */
    fun validate(questionnaireItem: Questionnaire.Item, questionnaireResponseItemBuilder: QuestionnaireResponse.Item.Builder): List<ValidationResult> {
        if (questionnaireResponseItemBuilder.answerBuilderList.isEmpty()) {
            return listOf(ValidationResult(true, emptyList()))
        }
        val validationResults = mutableListOf<ValidationResult>()
        validators.forEach {
            validationResults.addAll(it.validate(questionnaireItem, questionnaireResponseItemBuilder))
        }
        return validationResults
    }

    data class ValidationResult(val isValid: Boolean, val messages: List<String>)
}

