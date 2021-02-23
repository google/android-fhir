package com.google.android.fhir.datacapture.validation

import com.google.fhir.r4.core.Extension
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse

interface ConstraintValidator {
    /**
     * Validates the response by the user
     *
     * @param questionnaireItem
     * @param questionnaireResponseItemBuilder
     * @return
     */
    fun validate(questionnaireItem: Questionnaire.Item, questionnaireResponseItemBuilder: QuestionnaireResponse.Item.Builder): QuestionnaireItemValidator.ValidationResult

    /**
     * Returns an extension if exists by the URL
     *
     * @param extensionUrlValue
     * @param questionnaireItem
     * @return
     */
    fun Questionnaire.Item.getExtensionsByUrl(extensionUrlValue: String): List<Extension> {
        var extensions = mutableListOf<Extension>()
        val extensionIterator = this.extensionList.iterator()
        while (extensionIterator.hasNext()) {
            val extension = extensionIterator.next()
            if (extension.url.value == extensionUrlValue) {
                extensions.add(extension)
            }
        }
        return extensions
    }
}