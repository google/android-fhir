/*
 * Copyright 2022 Google LLC
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

import android.content.Context
import com.google.android.fhir.datacapture.isHidden
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireResponseItemValidator {

  private val validators =
    mutableListOf(
      RequiredConstraintValidator,
      MaxValueConstraintValidator,
      MinValueConstraintValidator,
      PrimitiveTypeAnswerMaxLengthValidator,
      PrimitiveTypeAnswerMinLengthValidator,
      RegexValidator,
      DecimalTypeMaxDecimalValidator
    )

  /** Validates [answers] contains valid answer(s) to [questionnaireItem]. */
  fun validate(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    answers: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>,
    context: Context
  ): ValidationResult {
    if (questionnaireItem.isHidden) return NotValidated

    val validationResults = validators.map { it.validate(questionnaireItem, answers, context) }

    return if (validationResults.all { it.isValid }) {
      Valid
    } else {
      Invalid(validationResults.mapNotNull { it.message })
    }
  }
}
