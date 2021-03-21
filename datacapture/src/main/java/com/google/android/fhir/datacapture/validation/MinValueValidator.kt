/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.fhir.datacapture.validation

import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

object MinValueValidator : ConstraintValidator {

    private const val MIN_VALUE_EXTENSION_URL = "http://hl7.org/fhir/StructureDefinition/minValue"

    override fun validate(
        questionnaireItem: Questionnaire.QuestionnaireItemComponent,
        questionnaireResponseItemBuilder: QuestionnaireResponse.QuestionnaireResponseItemComponent
    ): QuestionnaireResponseItemValidator.ValidationResult {
        return if (questionnaireItem.hasExtension(MIN_VALUE_EXTENSION_URL))
            minValueIntegerValidator(
                questionnaireItem.getExtensionByUrl(MIN_VALUE_EXTENSION_URL),
                questionnaireResponseItemBuilder
            )
        else QuestionnaireResponseItemValidator.ValidationResult(true, null)
    }

    private fun minValueIntegerValidator(
        extension: Extension,
        questionnaireResponseItemBuilder: QuestionnaireResponse.QuestionnaireResponseItemComponent
    ): QuestionnaireResponseItemValidator.ValidationResult {
        val answer = questionnaireResponseItemBuilder.answer[0]
        when {
            extension.value.fhirType().equals("integer") && answer.hasValueIntegerType() -> {
                val answeredValue = answer.valueIntegerType.value
                if (answeredValue < extension.value.primitiveValue().toInt()) {
                    return QuestionnaireResponseItemValidator.ValidationResult(
                        false,
                        validationMessageGenerator(extension)
                    )
                }
            }
        }
        return QuestionnaireResponseItemValidator.ValidationResult(true, null)
    }

    private fun validationMessageGenerator(extension: Extension): String {
        return "Minimum value allowed is:" + extension.value.primitiveValue()
    }
}
