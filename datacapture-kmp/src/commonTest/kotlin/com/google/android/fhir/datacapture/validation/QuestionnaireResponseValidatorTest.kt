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

import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import com.google.fhir.model.r4.String as FhirString
import com.google.fhir.model.r4.terminologies.PublicationStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class QuestionnaireResponseValidatorTest {

  @Test
  fun shouldReturnValidResults() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = FhirString.Builder().apply { value = "a-question" },
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.String),
        )
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
              )
              .apply {
                this.answer.add(
                  QuestionnaireResponse.Item.Answer.Builder().apply {
                    value =
                      QuestionnaireResponse.Item.Answer.Value.String(
                        value = FhirString(value = "some answer"),
                      )
                  },
                )
              },
          )
        }
        .build()

    val validationResults =
      QuestionnaireResponseValidator.validateQuestionnaireResponse(
        questionnaire,
        questionnaireResponse
      )

    assertTrue(validationResults.values.flatten().all { it is Valid })
  }

  @Test
  fun shouldReturnInvalidResultsForMissingRequiredField() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = FhirString.Builder().apply { value = "a-question" },
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.String),
        )
        .apply { required = com.google.fhir.model.r4.Boolean.Builder().apply { value = true } }
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

    val validationResults =
      QuestionnaireResponseValidator.validateQuestionnaireResponse(
        questionnaire,
        questionnaireResponse
      )

    assertEquals(1, validationResults.size)
    val result = validationResults["a-question"]
    assertTrue(result!![0] is Invalid)
    assertEquals(
      "Missing answer for required field.",
      (result[0] as Invalid).getSingleStringValidationMessage(),
    )
  }
}
