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

/**
 * A validator to check if the answer (a decimal value) exceeds the maximum number of permitted
 * decimal places.
 *
 * <p>Only decimal types permitted in questionnaires response are subjected to this validation. See
 * https://www.hl7.org/fhir/extension-maxdecimalplaces.html
 */
import android.content.Context
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object DecimalTypeMaxDecimalValidator : ConstraintValidator {
  override fun validate(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent,
    context: Context
  ): ConstraintValidator.ConstraintValidationResult {

    val answer =
      questionnaireResponseItem.answer.singleOrNull()
        ?: return ConstraintValidator.ConstraintValidationResult(true, null)

    val maxDecimalValue =
      (questionnaireItem.getExtensionByUrl(MAX_DECIMAL_URL)?.value as? IntegerType)?.value
        ?: return ConstraintValidator.ConstraintValidationResult(true, null)

    if (answer.hasValueDecimalType() &&
        answer.valueDecimalType.valueAsString.substringAfter(".").length > maxDecimalValue
    ) {
      return ConstraintValidator.ConstraintValidationResult(
        false,
        "The maximum number of decimal places that are permitted in the answer is: $maxDecimalValue"
      )
    }
    return ConstraintValidator.ConstraintValidationResult(true, null)
  }
  private const val MAX_DECIMAL_URL = "http://hl7.org/fhir/StructureDefinition/maxDecimalPlaces"
}
