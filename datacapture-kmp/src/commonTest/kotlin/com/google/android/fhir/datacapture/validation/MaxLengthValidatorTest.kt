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

import com.google.fhir.model.r4.Integer
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class MaxLengthValidatorTest {

  @Test
  fun shouldReturnValidResultIfMaxLengthIsNull() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = com.google.fhir.model.r4.String.Builder().apply { value = "link-id" },
          type =
            com.google.fhir.model.r4.Enumeration(
              value = Questionnaire.QuestionnaireItemType.String,
            ),
        )
        .build()
    val answer =
      QuestionnaireResponse.Item.Answer.Builder()
        .apply {
          value =
            QuestionnaireResponse.Item.Answer.Value.String(
              value = com.google.fhir.model.r4.String(value = "some answer"),
            )
        }
        .build()

    val result = MaxLengthValidator.validate(questionnaireItem, answer) { null }

    assertTrue(result.isValid)
  }

  @Test
  fun shouldReturnValidResultIfAnswerLengthIsLessThanMaxLength() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = com.google.fhir.model.r4.String.Builder().apply { value = "link-id" },
          type =
            com.google.fhir.model.r4.Enumeration(
              value = Questionnaire.QuestionnaireItemType.String,
            ),
        )
        .apply { maxLength = Integer.Builder().apply { value = 10 } }
        .build()
    val answer =
      QuestionnaireResponse.Item.Answer.Builder()
        .apply {
          value =
            QuestionnaireResponse.Item.Answer.Value.String(
              value = com.google.fhir.model.r4.String(value = "short"),
            )
        }
        .build()

    val result = MaxLengthValidator.validate(questionnaireItem, answer) { null }

    assertTrue(result.isValid)
  }

  @Test
  fun shouldReturnValidResultIfAnswerLengthIsEqualToMaxLength() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = com.google.fhir.model.r4.String.Builder().apply { value = "link-id" },
          type =
            com.google.fhir.model.r4.Enumeration(
              value = Questionnaire.QuestionnaireItemType.String,
            ),
        )
        .apply { maxLength = Integer.Builder().apply { value = 10 } }
        .build()
    val answer =
      QuestionnaireResponse.Item.Answer.Builder()
        .apply {
          value =
            QuestionnaireResponse.Item.Answer.Value.String(
              value = com.google.fhir.model.r4.String(value = "1234567890"),
            )
        }
        .build()

    val result = MaxLengthValidator.validate(questionnaireItem, answer) { null }

    assertTrue(result.isValid)
  }

  @Test
  fun shouldReturnInvalidResultIfAnswerLengthIsGreaterThanMaxLength() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = com.google.fhir.model.r4.String.Builder().apply { value = "link-id" },
          type =
            com.google.fhir.model.r4.Enumeration(
              value = Questionnaire.QuestionnaireItemType.String,
            ),
        )
        .apply { maxLength = Integer.Builder().apply { value = 10 } }
        .build()
    val answer =
      QuestionnaireResponse.Item.Answer.Builder()
        .apply {
          value =
            QuestionnaireResponse.Item.Answer.Value.String(
              value = com.google.fhir.model.r4.String(value = "very long answer"),
            )
        }
        .build()

    val result = MaxLengthValidator.validate(questionnaireItem, answer) { null }

    assertFalse(result.isValid)
  }
}
