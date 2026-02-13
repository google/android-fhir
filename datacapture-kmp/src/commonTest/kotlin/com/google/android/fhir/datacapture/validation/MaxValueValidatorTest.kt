/*
 * Copyright 2023-2026 Google LLC
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

import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.Integer
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class MaxValueValidatorTest {

  @Test
  fun shouldReturnValidResultIfMaxValueExtensionIsNotPresent() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = com.google.fhir.model.r4.String.Builder().apply { value = "link-id" },
          type =
            com.google.fhir.model.r4.Enumeration(
              value = Questionnaire.QuestionnaireItemType.Integer,
            ),
        )
        .build()
    val answer =
      QuestionnaireResponse.Item.Answer.Builder()
        .apply {
          value =
            QuestionnaireResponse.Item.Answer.Value.Integer(
              value = Integer(value = 10),
            )
        }
        .build()

    val result = MaxValueValidator.validate(questionnaireItem, answer) { null }

    assertTrue(result.isValid)
  }

  @Test
  fun shouldReturnValidResultIfAnswerValueIsLessThanMaxValue() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = com.google.fhir.model.r4.String.Builder().apply { value = "link-id" },
          type =
            com.google.fhir.model.r4.Enumeration(
              value = Questionnaire.QuestionnaireItemType.Integer,
            ),
        )
        .apply {
          extension =
            mutableListOf(
              Extension.Builder(url = MAX_VALUE_EXTENSION_URL).apply {
                value =
                  Extension.Value.Integer(value = Integer.Builder().apply { value = 15 }.build())
              },
            )
        }
        .build()
    val answer =
      QuestionnaireResponse.Item.Answer.Builder()
        .apply {
          value =
            QuestionnaireResponse.Item.Answer.Value.Integer(
              value = Integer(value = 10),
            )
        }
        .build()

    val result = MaxValueValidator.validate(questionnaireItem, answer) { null }

    assertTrue(result.isValid)
  }

  @Test
  fun shouldReturnValidResultIfAnswerValueIsEqualToMaxValue() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = com.google.fhir.model.r4.String.Builder().apply { value = "link-id" },
          type =
            com.google.fhir.model.r4.Enumeration(
              value = Questionnaire.QuestionnaireItemType.Integer,
            ),
        )
        .apply {
          extension =
            mutableListOf(
              Extension.Builder(url = MAX_VALUE_EXTENSION_URL).apply {
                value =
                  Extension.Value.Integer(value = Integer.Builder().apply { value = 15 }.build())
              },
            )
        }
        .build()
    val answer =
      QuestionnaireResponse.Item.Answer.Builder()
        .apply {
          value =
            QuestionnaireResponse.Item.Answer.Value.Integer(
              value = Integer(value = 15),
            )
        }
        .build()

    val result = MaxValueValidator.validate(questionnaireItem, answer) { null }

    assertTrue(result.isValid)
  }

  @Test
  fun shouldReturnInvalidResultIfAnswerValueIsGreaterThanMaxValue() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = com.google.fhir.model.r4.String.Builder().apply { value = "link-id" },
          type =
            com.google.fhir.model.r4.Enumeration(
              value = Questionnaire.QuestionnaireItemType.Integer,
            ),
        )
        .apply {
          extension =
            mutableListOf(
              Extension.Builder(url = MAX_VALUE_EXTENSION_URL).apply {
                value =
                  Extension.Value.Integer(value = Integer.Builder().apply { value = 15 }.build())
              },
            )
        }
        .build()
    val answer =
      QuestionnaireResponse.Item.Answer.Builder()
        .apply {
          value =
            QuestionnaireResponse.Item.Answer.Value.Integer(
              value = Integer(value = 20),
            )
        }
        .build()

    val result = MaxValueValidator.validate(questionnaireItem, answer) { null }

    assertFalse(result.isValid)
  }
}
