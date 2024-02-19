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
import com.google.android.fhir.datacapture.extensions.isHidden
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireResponseItemValidator {

  /** Validators for [QuestionnaireResponse.QuestionnaireResponseItemComponent]. */
  private val questionnaireResponseItemConstraintValidators =
    listOf(
      RequiredValidator,
    )

  /** Validators for [QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent]. */
  private val answerConstraintValidators =
    listOf(
      MinValueValidator,
      MaxValueValidator,
      MinLengthValidator,
      MaxLengthValidator,
      MaxDecimalPlacesValidator,
      RegexValidator,
    )

  /** Validates [answers] contains valid answer(s) to [questionnaireItem]. */
  fun validate(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    answers: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>,
    context: Context
  ): ValidationResult {
    if (questionnaireItem.isHidden) return NotValidated

    val questionnaireResponseItemConstraintValidationResult =
      questionnaireResponseItemConstraintValidators.map {
        it.validate(questionnaireItem, answers, context)
      }
    val questionnaireResponseItemAnswerConstraintValidationResult =
      answerConstraintValidators.flatMap { validator ->
        answers.map { answer -> validator.validate(questionnaireItem, answer, context) }
      }

    return if (questionnaireResponseItemConstraintValidationResult.all { it.isValid } &&
        questionnaireResponseItemAnswerConstraintValidationResult.all { it.isValid }
    ) {
      Valid
    } else {
      Invalid(
        questionnaireResponseItemConstraintValidationResult.mapNotNull { it.errorMessage } +
          questionnaireResponseItemAnswerConstraintValidationResult.mapNotNull { it.errorMessage }
      )
    }
  }
}
