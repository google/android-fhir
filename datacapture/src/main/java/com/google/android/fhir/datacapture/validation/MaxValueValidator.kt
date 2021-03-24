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

object MaxValueValidator : ConstraintValidator {

  private const val MAX_VALUE_EXTENSION_URL = "http://hl7.org/fhir/StructureDefinition/maxValue"

    override fun validate(
        questionnaireItem: Questionnaire.QuestionnaireItemComponent,
        questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent
    ): ConstraintValidator.ConstraintValidationResult {
        val extension = questionnaireItem.getExtensionByUrl(MAX_VALUE_EXTENSION_URL)
        if (questionnaireItem.hasExtension(MAX_VALUE_EXTENSION_URL))
            if (ValueConstraintValidator.valueConstraintValidator(
                    extension,
                    questionnaireResponseItem, ">"
                )
            ) {
                return ConstraintValidator.ConstraintValidationResult(
                    false,
                    validationMessageGenerator(extension)
                )
            }
        return ConstraintValidator.ConstraintValidationResult(true, null)
    }

    private fun validationMessageGenerator(extension: Extension): String {
        return "Maximum value allowed is:" + extension.value.primitiveValue()
    }
}
