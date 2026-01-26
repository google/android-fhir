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
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import com.google.fhir.model.r4.String as FhirString
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class RegexValidatorTest {

  @Test
  fun shouldReturnValidResultIfRegexExtensionIsNotPresent() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = FhirString.Builder().apply { value = "link-id" },
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
              value = FhirString(value = "some answer"),
            )
        }
        .build()

    val result = RegexValidator.validate(questionnaireItem, answer) { null }

    assertTrue(result.isValid)
  }

  @Test
  fun shouldReturnValidResultIfAnswerMatchesRegex() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = FhirString.Builder().apply { value = "link-id" },
          type =
            com.google.fhir.model.r4.Enumeration(
              value = Questionnaire.QuestionnaireItemType.String,
            ),
        )
        .apply {
          extension =
            mutableListOf(
              Extension.Builder(url = REGEX_EXTENSION_URL).apply {
                value =
                  Extension.Value.String(
                    value = FhirString.Builder().apply { value = "^[0-9]+$" }.build(),
                  )
              },
            )
        }
        .build()
    val answer =
      QuestionnaireResponse.Item.Answer.Builder()
        .apply {
          value =
            QuestionnaireResponse.Item.Answer.Value.String(
              value = FhirString(value = "12345"),
            )
        }
        .build()

    val result = RegexValidator.validate(questionnaireItem, answer) { null }

    assertTrue(result.isValid)
  }

  @Test
  fun shouldReturnInvalidResultIfAnswerDoesNotMatchRegex() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = FhirString.Builder().apply { value = "link-id" },
          type =
            com.google.fhir.model.r4.Enumeration(
              value = Questionnaire.QuestionnaireItemType.String,
            ),
        )
        .apply {
          extension =
            mutableListOf(
              Extension.Builder(url = REGEX_EXTENSION_URL).apply {
                value =
                  Extension.Value.String(
                    value = FhirString.Builder().apply { value = "^[0-9]+$" }.build(),
                  )
              },
            )
        }
        .build()
    val answer =
      QuestionnaireResponse.Item.Answer.Builder()
        .apply {
          value =
            QuestionnaireResponse.Item.Answer.Value.String(
              value = FhirString(value = "123a45"),
            )
        }
        .build()

    val result = RegexValidator.validate(questionnaireItem, answer) { null }

    assertFalse(result.isValid)
  }
}
