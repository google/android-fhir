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

import com.google.fhir.model.r4.Boolean
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class RequiredValidatorTest {

  @Test
  fun shouldReturnValidResultIfItemIsNotRequired() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = com.google.fhir.model.r4.String.Builder().apply { value = "link-id" },
          type =
            com.google.fhir.model.r4.Enumeration(
              value = Questionnaire.QuestionnaireItemType.String,
            ),
        )
        .apply { required = Boolean.Builder().apply { value = false } }
        .build()
    val questionnaireResponseItem =
      QuestionnaireResponse.Item.Builder(
          linkId = com.google.fhir.model.r4.String.Builder().apply { value = "link-id" },
        )
        .build()

    val result = RequiredValidator.validate(questionnaireItem, questionnaireResponseItem)

    assertTrue(result[0].isValid)
  }

  @Test
  fun shouldReturnValidResultIfItemIsRequiredAndHasAnswer() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = com.google.fhir.model.r4.String.Builder().apply { value = "link-id" },
          type =
            com.google.fhir.model.r4.Enumeration(
              value = Questionnaire.QuestionnaireItemType.String,
            ),
        )
        .apply { required = Boolean.Builder().apply { value = true } }
        .build()
    val questionnaireResponseItem =
      QuestionnaireResponse.Item.Builder(
          linkId = com.google.fhir.model.r4.String.Builder().apply { value = "link-id" },
        )
        .apply {
          answer =
            mutableListOf(
              QuestionnaireResponse.Item.Answer.Builder().apply {
                value =
                  QuestionnaireResponse.Item.Answer.Value.String(
                    value = com.google.fhir.model.r4.String(value = "some answer"),
                  )
              },
            )
        }
        .build()

    val result = RequiredValidator.validate(questionnaireItem, questionnaireResponseItem)

    assertTrue(result[0].isValid)
  }

  @Test
  fun shouldReturnInvalidResultIfItemIsRequiredAndHasNoAnswer() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = com.google.fhir.model.r4.String.Builder().apply { value = "link-id" },
          type =
            com.google.fhir.model.r4.Enumeration(
              value = Questionnaire.QuestionnaireItemType.String,
            ),
        )
        .apply { required = Boolean.Builder().apply { value = true } }
        .build()
    val questionnaireResponseItem =
      QuestionnaireResponse.Item.Builder(
          linkId = com.google.fhir.model.r4.String.Builder().apply { value = "link-id" },
        )
        .build()

    val result = RequiredValidator.validate(questionnaireItem, questionnaireResponseItem)

    assertFalse(result[0].isValid)
  }
}
