package com.google.android.fhir.datacapture.validation

import com.google.fhir.r4.core.Extension
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse

interface QuestionnaireItemValidator {
    companion object {
        const val EXTENSION_MAX_VALUE = "http://hl7.org/fhir/StructureDefinition/maxValue"
        const val EXTENSION_MIN_VALUE = "http://hl7.org/fhir/StructureDefinition/minValue"

        /**
         * Validates the input data as per the ViewHolder and returns the result and its resultant error messages (if any)
         */
        fun validate(questionnaireItem: Questionnaire.Item, questionnaireResponseItemBuilder: QuestionnaireResponse.Item.Builder): ValidationResult {
            if (questionnaireResponseItemBuilder.answerBuilderList.isEmpty()) {
                return ValidationResult(true, emptyList())
            }
            val validationResults = mutableListOf<ValidationResult>()
            validationResults.addAll(questionnaireItem.maxValueValidator(questionnaireItem, questionnaireResponseItemBuilder))
            validationResults.addAll(questionnaireItem.minValueValidator(questionnaireItem, questionnaireResponseItemBuilder))
            validationResults.forEach {
                if (!it.isValid) {
                    return it
                }
            }
            return ValidationResult(true, emptyList())
        }


        private fun Questionnaire.Item.maxValueValidator(questionnaireItem: Questionnaire.Item, questionnaireResponseItemBuilder: QuestionnaireResponse.Item.Builder): List<ValidationResult> {
            val validationResults = mutableListOf<ValidationResult>()
            val extension = hasExtension(EXTENSION_MAX_VALUE, questionnaireItem)
                    ?: return listOf(ValidationResult(true, emptyList()))
            validationResults.add(maxValueIntegerValidator(extension, questionnaireResponseItemBuilder))
            return validationResults
        }

        private fun Questionnaire.Item.minValueValidator(questionnaireItem: Questionnaire.Item, questionnaireResponseItemBuilder: QuestionnaireResponse.Item.Builder): List<ValidationResult> {
            val validationResults = mutableListOf<ValidationResult>()
            val extension = hasExtension(EXTENSION_MIN_VALUE, questionnaireItem)
                    ?: return listOf(ValidationResult(true, emptyList()))
            validationResults.add(minValueIntegerValidator(extension, questionnaireResponseItemBuilder))
            return validationResults
        }

        private fun maxValueIntegerValidator(extension: Extension, questionnaireResponseItemBuilder: QuestionnaireResponse.Item.Builder): ValidationResult {
            val response = questionnaireResponseItemBuilder.getAnswerBuilder(0).value
            if (extension.value.hasInteger() && response.hasInteger()) {
                val answer = questionnaireResponseItemBuilder.getAnswerBuilder(0).value.integer.value
                if (answer > extension.value.integer.value) {
                    return ValidationResult(false, listOf("Error Value!"))
                }
            }
            return ValidationResult(true, emptyList())
        }

        private fun minValueIntegerValidator(extension: Extension, questionnaireResponseItemBuilder: QuestionnaireResponse.Item.Builder): ValidationResult {
            val response = questionnaireResponseItemBuilder.getAnswerBuilder(0).value
            if (extension.value.hasInteger() && response.hasInteger()) {
                val answer = questionnaireResponseItemBuilder.getAnswerBuilder(0).value.integer.value
                if (answer < extension.value.integer.value) {
                    return ValidationResult(false, listOf("Error Value!"))
                }
            }
            return ValidationResult(true, emptyList())
        }

        private fun hasExtension(extensionUrlValue: String, questionnaireItem: Questionnaire.Item): Extension? {
            val extensionIterator = questionnaireItem.extensionList.iterator()
            while (extensionIterator.hasNext()) {
                val extension = extensionIterator.next()
                if (extension.url.value == extensionUrlValue) {
                    return extension
                }
            }
            return null
        }
    }

    data class ValidationResult(val isValid: Boolean, val messages: List<String>)
}

