package com.google.android.fhir.datacapture.validation

import com.google.fhir.r4.core.Extension
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse

class QuestionnaireItemIntegerValidator : QuestionnaireItemValidator {
    companion object {}

    override fun validate(questionnaireItem: Questionnaire.Item, questionnaireResponseItemBuilder: QuestionnaireResponse.Item.Builder): ValidationResult {
        if (questionnaireResponseItemBuilder.answerBuilderList.isNotEmpty()) {

            if (!questionnaireResponseItemBuilder.getAnswerBuilder(0).value.hasInteger()) {
                val messages = listOf("Error Value!")
                questionnaireResponseItemBuilder.clearAnswer()
                return ValidationResult(false, messages)
            }

            val answer = questionnaireResponseItemBuilder.getAnswerBuilder(0).value.integer.value
            val extensionIterator = questionnaireItem.extensionList.iterator()
            while (extensionIterator.hasNext()) {
                val extension = extensionIterator.next()
                when (extension.url.value) {
                    QuestionnaireItemValidator.EXTENSION_MAX_VALUE -> {
                        if (validateMaxValue(extension, answer)) {
                            val messages = listOf("Error Value!")
                            questionnaireResponseItemBuilder.clearAnswer()
                            return ValidationResult(false, messages)
                        }
                    }

                    QuestionnaireItemValidator.EXTENSION_MIN_VALUE -> {
                        if (validateMinValue(extension, answer)) {
                            val messages = listOf("Error Value!")
                            questionnaireResponseItemBuilder.clearAnswer()
                            return ValidationResult(false, messages)
                        }
                    }
                }
            }
        }
        return ValidationResult(true, emptyList())
    }

    fun validateMaxValue(extension: Extension, inputValue: Int): Boolean {
        if (extension.value.hasInteger()) {
            if (inputValue > extension.value.integer.value) {
                return true
            }
        }
        return false
    }

    fun validateMinValue(extension: Extension, inputValue: Int): Boolean {
        if (extension.value.hasInteger()) {
            if (inputValue < extension.value.integer.value) {
                return true
            }
        }
        return false
    }
}