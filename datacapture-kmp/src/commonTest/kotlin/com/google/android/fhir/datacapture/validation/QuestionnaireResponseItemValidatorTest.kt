/*
 * Copyright 2022-2026 Google LLC
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

import com.google.android.fhir.datacapture.fhirpath.ExpressionEvaluator
import com.google.fhir.model.r4.Boolean
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.Integer
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import com.google.fhir.model.r4.String as FhirString
import com.google.fhir.model.r4.terminologies.PublicationStatus
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class QuestionnaireResponseItemValidatorTest {

  @Test
  fun shouldReturnValidResult() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = FhirString.Builder().apply { value = "a-question" },
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
        )
        .apply {
          extension =
            mutableListOf(
              Extension.Builder(url = MIN_VALUE_EXTENSION_URL).apply {
                value =
                  Extension.Value.Integer(value = Integer.Builder().apply { value = 250 }.build())
              },
              Extension.Builder(url = MAX_VALUE_EXTENSION_URL).apply {
                value =
                  Extension.Value.Integer(value = Integer.Builder().apply { value = 300 }.build())
              },
            )
        }
        .build()
    val answer =
      QuestionnaireResponse.Item.Answer.Builder()
        .apply {
          value =
            QuestionnaireResponse.Item.Answer.Value.Integer(
              value = Integer.Builder().apply { value = 275 }.build(),
            )
        }
        .build()
    val questionnaire =
      Questionnaire.Builder(status = Enumeration(value = PublicationStatus.Active))
        .apply { this.item.add(questionnaireItem.toBuilder()) }
        .build()
    val questionnaireResponse =
      QuestionnaireResponse.Builder(
          status = Enumeration(value = QuestionnaireResponse.QuestionnaireResponseStatus.Completed)
        )
        .apply {
          this.item.add(
            QuestionnaireResponse.Item.Builder(
                linkId = FhirString.Builder().apply { value = "a-question" },
              )
              .apply { this.answer.add(answer.toBuilder()) },
          )
        }
        .build()
    val expressionEvaluator =
      ExpressionEvaluator(
        questionnaire,
        questionnaireResponse,
      )

    val validationResult =
      QuestionnaireResponseItemValidator(expressionEvaluator)
        .validate(
          questionnaire.item.first(),
          questionnaireResponse.item.first(),
        )

    assertTrue(validationResult is Valid)
  }

  @Test
  fun shouldValidateIndividualAnswersAndCombineResults() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = FhirString.Builder().apply { value = "a-question" },
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
        )
        .apply {
          extension =
            mutableListOf(
              Extension.Builder(url = MIN_VALUE_EXTENSION_URL).apply {
                value =
                  Extension.Value.Integer(value = Integer.Builder().apply { value = 100 }.build())
              },
              Extension.Builder(url = MAX_VALUE_EXTENSION_URL).apply {
                value =
                  Extension.Value.Integer(value = Integer.Builder().apply { value = 200 }.build())
              },
            )
        }
        .build()
    val answers =
      listOf(
        QuestionnaireResponse.Item.Answer.Builder()
          .apply {
            value =
              QuestionnaireResponse.Item.Answer.Value.Integer(
                value = Integer.Builder().apply { value = 50 }.build(),
              )
          }
          .build(),
        QuestionnaireResponse.Item.Answer.Builder()
          .apply {
            value =
              QuestionnaireResponse.Item.Answer.Value.Integer(
                value = Integer.Builder().apply { value = 150 }.build(),
              )
          }
          .build(),
        QuestionnaireResponse.Item.Answer.Builder()
          .apply {
            value =
              QuestionnaireResponse.Item.Answer.Value.Integer(
                value = Integer.Builder().apply { value = 250 }.build(),
              )
          }
          .build(),
      )
    val questionnaire =
      Questionnaire.Builder(status = Enumeration(PublicationStatus.Active.getCode()))
        .apply { this.item.add(questionnaireItem.toBuilder()) }
        .build()
    val questionnaireResponse =
      QuestionnaireResponse.Builder(
          status =
            Enumeration(QuestionnaireResponse.QuestionnaireResponseStatus.Completed.getCode())
        )
        .apply {
          this.item.add(
            QuestionnaireResponse.Item.Builder(
                linkId = FhirString.Builder().apply { value = "a-question" },
              )
              .apply { answers.forEach { this.answer.add(it.toBuilder()) } },
          )
        }
        .build()
    val expressionEvaluator =
      ExpressionEvaluator(
        questionnaire,
        questionnaireResponse,
      )

    val validationResult =
      QuestionnaireResponseItemValidator(expressionEvaluator)
        .validate(
          questionnaire.item.first(),
          questionnaireResponse.item.first(),
        )

    assertTrue(validationResult is Invalid)
    assertTrue(
      validationResult.getSingleStringValidationMessage().contains("Minimum value allowed is:100")
    )
    assertTrue(
      validationResult.getSingleStringValidationMessage().contains("Maximum value allowed is:200")
    )
  }

  @Test
  fun shouldValidateAllAnswers() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = FhirString.Builder().apply { value = "a-question" },
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
        )
        .apply { required = Boolean.Builder().apply { value = true } }
        .build()
    val questionnaire =
      Questionnaire.Builder(status = Enumeration(PublicationStatus.Active.getCode()))
        .apply { this.item.add(questionnaireItem.toBuilder()) }
        .build()
    val questionnaireResponse =
      QuestionnaireResponse.Builder(
          status =
            Enumeration(QuestionnaireResponse.QuestionnaireResponseStatus.Completed.getCode())
        )
        .apply {
          this.item.add(
            QuestionnaireResponse.Item.Builder(
              linkId = FhirString.Builder().apply { value = "a-question" },
            ),
          )
        }
        .build()

    val expressionEvaluator =
      ExpressionEvaluator(
        questionnaire,
        questionnaireResponse,
      )

    val validationResult =
      QuestionnaireResponseItemValidator(expressionEvaluator)
        .validate(
          questionnaire.item.first(),
          questionnaireResponse.item.first(),
        )

    assertTrue(validationResult is Invalid)
    val invalidValidationResult = validationResult
    assertTrue(
      invalidValidationResult
        .getSingleStringValidationMessage()
        .contains("Missing answer for required field.")
    )
  }
}
