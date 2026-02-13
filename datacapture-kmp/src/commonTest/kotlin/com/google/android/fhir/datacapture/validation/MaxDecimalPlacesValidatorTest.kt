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

import com.google.fhir.model.r4.Decimal
import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.Integer
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class MaxDecimalPlacesValidatorTest {

  @Test
  fun shouldReturnValidResultIfMaxDecimalPlacesExtensionIsNotPresent() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = com.google.fhir.model.r4.String.Builder().apply { value = "link-id" },
          type =
            com.google.fhir.model.r4.Enumeration(
              value = Questionnaire.QuestionnaireItemType.Decimal,
            ),
        )
        .build()
    val answer =
      QuestionnaireResponse.Item.Answer.Builder()
        .apply {
          value =
            QuestionnaireResponse.Item.Answer.Value.Decimal(
              value = Decimal(value = "1.2345".toBigDecimal()),
            )
        }
        .build()

    val result = MaxDecimalPlacesValidator.validate(questionnaireItem, answer) { null }

    assertTrue(result.isValid)
  }

  @Test
  fun shouldReturnValidResultIfDecimalPlacesIsLessThanMaxDecimalPlaces() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = com.google.fhir.model.r4.String.Builder().apply { value = "link-id" },
          type =
            com.google.fhir.model.r4.Enumeration(
              value = Questionnaire.QuestionnaireItemType.Decimal,
            ),
        )
        .apply {
          extension =
            mutableListOf(
              Extension.Builder(url = "http://hl7.org/fhir/StructureDefinition/maxDecimalPlaces")
                .apply {
                  value =
                    Extension.Value.Integer(value = Integer.Builder().apply { value = 3 }.build())
                },
            )
        }
        .build()
    val answer =
      QuestionnaireResponse.Item.Answer.Builder()
        .apply {
          value =
            QuestionnaireResponse.Item.Answer.Value.Decimal(
              value = Decimal(value = "1.23".toBigDecimal()),
            )
        }
        .build()

    val result = MaxDecimalPlacesValidator.validate(questionnaireItem, answer) { null }

    assertTrue(result.isValid)
  }

  @Test
  fun shouldReturnValidResultIfDecimalPlacesIsEqualToMaxDecimalPlaces() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = com.google.fhir.model.r4.String.Builder().apply { value = "link-id" },
          type =
            com.google.fhir.model.r4.Enumeration(
              value = Questionnaire.QuestionnaireItemType.Decimal,
            ),
        )
        .apply {
          extension =
            mutableListOf(
              Extension.Builder(url = "http://hl7.org/fhir/StructureDefinition/maxDecimalPlaces")
                .apply {
                  value =
                    Extension.Value.Integer(value = Integer.Builder().apply { value = 3 }.build())
                },
            )
        }
        .build()
    val answer =
      QuestionnaireResponse.Item.Answer.Builder()
        .apply {
          value =
            QuestionnaireResponse.Item.Answer.Value.Decimal(
              value = Decimal(value = "1.234".toBigDecimal()),
            )
        }
        .build()

    val result = MaxDecimalPlacesValidator.validate(questionnaireItem, answer) { null }

    assertTrue(result.isValid)
  }

  @Test
  fun shouldReturnInvalidResultIfDecimalPlacesIsGreaterThanMaxDecimalPlaces() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = com.google.fhir.model.r4.String.Builder().apply { value = "link-id" },
          type =
            com.google.fhir.model.r4.Enumeration(
              value = Questionnaire.QuestionnaireItemType.Decimal,
            ),
        )
        .apply {
          extension =
            mutableListOf(
              Extension.Builder(url = "http://hl7.org/fhir/StructureDefinition/maxDecimalPlaces")
                .apply {
                  value =
                    Extension.Value.Integer(value = Integer.Builder().apply { value = 3 }.build())
                },
            )
        }
        .build()
    val answer =
      QuestionnaireResponse.Item.Answer.Builder()
        .apply {
          value =
            QuestionnaireResponse.Item.Answer.Value.Decimal(
              value = Decimal(value = "1.2345".toBigDecimal()),
            )
        }
        .build()

    val result = MaxDecimalPlacesValidator.validate(questionnaireItem, answer) { null }

    assertFalse(result.isValid)
  }
}
