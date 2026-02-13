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
import com.google.fhir.model.r4.Code
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import com.google.fhir.model.r4.String as FhirString
import com.google.fhir.model.r4.terminologies.PublicationStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class ConstraintItemExtensionValidatorTest {

  @Test
  fun shouldReturnValidResultIfNoConstraintExtensionIsPresent() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = FhirString.Builder().apply { value = "link-id" },
          type =
            Enumeration(
              value = Questionnaire.QuestionnaireItemType.String,
            ),
        )
        .build()
    val questionnaireResponseItem =
      QuestionnaireResponse.Item.Builder(
          linkId = FhirString.Builder().apply { value = "link-id" },
        )
        .build()

    val questionnaire =
      Questionnaire.Builder(status = Enumeration(value = PublicationStatus.Active)).build()
    val questionnaireResponse =
      QuestionnaireResponse.Builder(
          status =
            Enumeration(value = QuestionnaireResponse.QuestionnaireResponseStatus.In_Progress),
        )
        .build()

    val expressionEvaluator = ExpressionEvaluator(questionnaire, questionnaireResponse)
    val validator = ConstraintItemExtensionValidator(expressionEvaluator)

    val results = validator.validate(questionnaireItem, questionnaireResponseItem)

    assertTrue(results.isEmpty())
  }

  @Test
  fun shouldReturnValidResultIfConstraintExpressionEvaluatesToTrue() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = FhirString.Builder().apply { value = "link-id" },
          type =
            Enumeration(
              value = Questionnaire.QuestionnaireItemType.Integer,
            ),
        )
        .apply {
          extension =
            mutableListOf(
              Extension.Builder(
                  url = "http://hl7.org/fhir/StructureDefinition/questionnaire-constraint",
                )
                .apply {
                  extension =
                    mutableListOf(
                      Extension.Builder(url = "severity").apply {
                        value = Extension.Value.Code(value = Code(value = "error"))
                      },
                      Extension.Builder(url = "expression").apply {
                        value = Extension.Value.String(value = FhirString(value = "true"))
                      },
                      Extension.Builder(url = "human").apply {
                        value = Extension.Value.String(value = FhirString(value = "Error message"))
                      },
                    )
                },
            )
        }
        .build()
    val questionnaireResponseItem =
      QuestionnaireResponse.Item.Builder(
          linkId = FhirString.Builder().apply { value = "link-id" },
        )
        .build()

    val questionnaire =
      Questionnaire.Builder(status = Enumeration(value = PublicationStatus.Active)).build()
    val questionnaireResponse =
      QuestionnaireResponse.Builder(
          status =
            Enumeration(value = QuestionnaireResponse.QuestionnaireResponseStatus.In_Progress),
        )
        .build()

    val expressionEvaluator = ExpressionEvaluator(questionnaire, questionnaireResponse)
    val validator = ConstraintItemExtensionValidator(expressionEvaluator)

    val results = validator.validate(questionnaireItem, questionnaireResponseItem)

    assertEquals(1, results.size)
    assertTrue(results[0].isValid)
  }

  @Test
  fun shouldReturnInvalidResultIfConstraintExpressionEvaluatesToFalse() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = FhirString.Builder().apply { value = "link-id" },
          type =
            Enumeration(
              value = Questionnaire.QuestionnaireItemType.Integer,
            ),
        )
        .apply {
          extension =
            mutableListOf(
              Extension.Builder(
                  url = "http://hl7.org/fhir/StructureDefinition/questionnaire-constraint",
                )
                .apply {
                  extension =
                    mutableListOf(
                      Extension.Builder(url = "severity").apply {
                        value = Extension.Value.Code(value = Code(value = "error"))
                      },
                      Extension.Builder(url = "expression").apply {
                        value = Extension.Value.String(value = FhirString(value = "false"))
                      },
                      Extension.Builder(url = "human").apply {
                        value = Extension.Value.String(value = FhirString(value = "Error message"))
                      },
                    )
                },
            )
        }
        .build()
    val questionnaireResponseItem =
      QuestionnaireResponse.Item.Builder(
          linkId = FhirString.Builder().apply { value = "link-id" },
        )
        .build()

    val questionnaire =
      Questionnaire.Builder(status = Enumeration(value = PublicationStatus.Active)).build()
    val questionnaireResponse =
      QuestionnaireResponse.Builder(
          status =
            Enumeration(value = QuestionnaireResponse.QuestionnaireResponseStatus.In_Progress),
        )
        .build()

    val expressionEvaluator = ExpressionEvaluator(questionnaire, questionnaireResponse)
    val validator = ConstraintItemExtensionValidator(expressionEvaluator)

    val results = validator.validate(questionnaireItem, questionnaireResponseItem)

    assertEquals(1, results.size)
    assertFalse(results[0].isValid)
    assertEquals("Error message", results[0].errorMessage)
  }
}
