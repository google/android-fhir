package com.google.android.fhir.datacapture.validation

import com.google.fhir.r4.core.Extension
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse

interface ValidationInterface {

    fun validate(questionnaireItem: Questionnaire.Item, questionnaireResponseItemBuilder: QuestionnaireResponse.Item.Builder): List<QuestionnaireItemValidator.ValidationResult>

    fun validationMessageGenerator(extension: Extension): List<String>

    fun hasExtension(extensionUrlValue: String, questionnaireItem: Questionnaire.Item): Extension? {
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