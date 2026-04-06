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

import com.google.android.fhir.datacapture.extensions.EXTENSION_CQF_CALCULATED_VALUE_URL
import com.google.android.fhir.datacapture.extensions.FhirR4DateType
import com.google.android.fhir.datacapture.extensions.FhirR4String
import com.google.android.fhir.datacapture.fhirpath.FhirPathService
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.Expression
import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.FhirDate
import com.google.fhir.model.r4.Integer
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import com.google.fhir.model.r4.QuestionnaireResponse.QuestionnaireResponseStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalTime::class)
class MaxValueValidatorTest {

  @Test
  fun shouldReturnValidResultIfMaxValueExtensionIsNotPresent() = runTest {
    val questionnaireItem =
      Questionnaire.Item.Builder(
          linkId = com.google.fhir.model.r4.String.Builder().apply { value = "link-id" },
          type =
            Enumeration(
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
            Enumeration(
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
            Enumeration(
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
            Enumeration(
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

  @Test
  fun shouldReturnInvalidResultWithCorrectMaxAllowedValueIfContainsOnlyCqfCalculatedValue() =
    runTest {
      val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

      val questionnaireItem =
        Questionnaire.Item(
          linkId = FhirR4String(value = "test-item"),
          type =
            Enumeration(
              value = Questionnaire.QuestionnaireItemType.Date,
            ),
          extension =
            listOf(
              Extension(
                url = MAX_VALUE_EXTENSION_URL,
                value =
                  Extension.Value.Date(
                    value =
                      FhirR4DateType(
                        extension =
                          listOf(
                            Extension(
                              url = EXTENSION_CQF_CALCULATED_VALUE_URL,
                              value =
                                Extension.Value.Expression(
                                  value =
                                    Expression(
                                      language =
                                        Enumeration(
                                          value = Expression.ExpressionLanguage.Text_Fhirpath,
                                        ),
                                      expression = FhirR4String(value = "today() - 7 days"),
                                    ),
                                ),
                            ),
                          ),
                      ),
                  ),
              ),
            ),
        )

      val answer =
        QuestionnaireResponse.Item.Answer(
          value =
            QuestionnaireResponse.Item.Answer.Value.Date(
              value =
                FhirR4DateType(
                  value =
                    FhirDate.Date(
                      today,
                    ),
                ),
            ),
        )

      val validationResult =
        MaxValueValidator.validate(questionnaireItem, answer) {
          FhirPathService.evaluate(
              it.expression?.value!!,
              QuestionnaireResponse(
                status = Enumeration(value = QuestionnaireResponseStatus.In_Progress),
              ),
            )
            .singleOrNull()
        }

      assertFalse(validationResult.isValid)
      assertEquals(
        "Maximum value allowed is:${today.minus(DatePeriod(days = 7))} ",
        validationResult.errorMessage,
      )
    }

  @Test
  fun shouldReturnInvalidResultWithCorrectMaxAllowedValueIfContainsBothValueAndCqfCalculatedValue() =
    runTest {
      val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
      val tenDaysAgo = today.minus(DatePeriod(days = 10))

      val questionnaireItem =
        Questionnaire.Item(
          linkId = FhirR4String(value = "test-item"),
          type =
            Enumeration(
              value = Questionnaire.QuestionnaireItemType.Date,
            ),
          extension =
            listOf(
              Extension(
                url = MAX_VALUE_EXTENSION_URL,
                value =
                  Extension.Value.Date(
                    value =
                      FhirR4DateType(
                        value = FhirDate.Date(tenDaysAgo),
                        extension =
                          listOf(
                            Extension(
                              url = EXTENSION_CQF_CALCULATED_VALUE_URL,
                              value =
                                Extension.Value.Expression(
                                  value =
                                    Expression(
                                      language =
                                        Enumeration(
                                          value = Expression.ExpressionLanguage.Text_Fhirpath,
                                        ),
                                      expression = FhirR4String(value = "today() - 7 days"),
                                    ),
                                ),
                            ),
                          ),
                      ),
                  ),
              ),
            ),
        )

      val answer =
        QuestionnaireResponse.Item.Answer(
          value =
            QuestionnaireResponse.Item.Answer.Value.Date(
              value =
                FhirR4DateType(
                  value =
                    FhirDate.Date(
                      today,
                    ),
                ),
            ),
        )

      val validationResult =
        MaxValueValidator.validate(questionnaireItem, answer) {
          FhirPathService.evaluate(
              it.expression?.value!!,
              QuestionnaireResponse(
                status = Enumeration(value = QuestionnaireResponseStatus.In_Progress),
              ),
            )
            .singleOrNull()
        }

      assertFalse(validationResult.isValid)
      assertEquals(
        "Maximum value allowed is:${today.minus(DatePeriod(days = 7))} ",
        validationResult.errorMessage,
      )
    }

  @Test
  fun shouldReturnValidResultAndRemovesConstraintForAnAnswerValueWhenMaxValueCqfCalculatedValueEvaluatesToEmpty() =
    runTest {
      val questionnaireItem =
        Questionnaire.Item(
          linkId = FhirR4String(value = "test-item"),
          type =
            Enumeration(
              value = Questionnaire.QuestionnaireItemType.Date,
            ),
          extension =
            listOf(
              Extension(
                url = MAX_VALUE_EXTENSION_URL,
                value =
                  Extension.Value.Date(
                    value =
                      FhirR4DateType(
                        extension =
                          listOf(
                            Extension(
                              url = EXTENSION_CQF_CALCULATED_VALUE_URL,
                              value =
                                Extension.Value.Expression(
                                  value =
                                    Expression(
                                      language =
                                        Enumeration(
                                          value = Expression.ExpressionLanguage.Text_Fhirpath,
                                        ),
                                      expression =
                                        FhirR4String(
                                          value = "yesterday()",
                                        ), // invalid FHIRPath expression
                                    ),
                                ),
                            ),
                          ),
                      ),
                  ),
              ),
            ),
        )

      val answer =
        QuestionnaireResponse.Item.Answer(
          value =
            QuestionnaireResponse.Item.Answer.Value.Date(
              value =
                FhirR4DateType(
                  value =
                    FhirDate.Date(
                      Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                    ),
                ),
            ),
        )

      val validationResult =
        MaxValueValidator.validate(
          questionnaireItem,
          answer,
        ) {
          FhirPathService.evaluate(
              it.expression?.value!!,
              QuestionnaireResponse(
                status = Enumeration(value = QuestionnaireResponseStatus.In_Progress),
              ),
            )
            .singleOrNull()
        }

      assertTrue(validationResult.isValid)
      assertTrue(validationResult.errorMessage.isNullOrBlank())
    }

  @Test
  fun shouldReturnValidResultAndRemovesConstraintForAnAnswerWithAnEmptyValue() = runTest {
    val questionnaireItem =
      Questionnaire.Item(
        linkId = FhirR4String(value = "test-item"),
        type =
          Enumeration(
            value = Questionnaire.QuestionnaireItemType.Date,
          ),
        extension =
          listOf(
            Extension(
              url = MAX_VALUE_EXTENSION_URL,
              value =
                Extension.Value.Date(
                  value =
                    FhirR4DateType(
                      extension =
                        listOf(
                          Extension(
                            url = EXTENSION_CQF_CALCULATED_VALUE_URL,
                            value =
                              Extension.Value.Expression(
                                value =
                                  Expression(
                                    language =
                                      Enumeration(
                                        value = Expression.ExpressionLanguage.Text_Fhirpath,
                                      ),
                                    expression = FhirR4String(value = "today()"),
                                  ),
                              ),
                          ),
                        ),
                    ),
                ),
            ),
          ),
      )

    val answer =
      QuestionnaireResponse.Item.Answer(
        value =
          QuestionnaireResponse.Item.Answer.Value.Date(
            value =
              FhirR4DateType(
                value =
                  FhirDate.Date(
                    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                  ),
              ),
          ),
      )

    val validationResult =
      MaxValueValidator.validate(
        questionnaireItem,
        answer,
      ) {
        FhirPathService.evaluate(
            it.expression?.value!!,
            QuestionnaireResponse(
              status = Enumeration(value = QuestionnaireResponseStatus.In_Progress),
            ),
          )
          .singleOrNull()
      }

    assertTrue(validationResult.isValid)
    assertTrue(validationResult.errorMessage.isNullOrBlank())
  }
}
