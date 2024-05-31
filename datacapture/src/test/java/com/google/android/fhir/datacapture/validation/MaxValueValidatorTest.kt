/*
 * Copyright 2022-2024 Google LLC
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
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.extensions.EXTENSION_CQF_CALCULATED_VALUE_URL
import com.google.common.truth.Truth.assertThat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import kotlinx.coroutines.test.runTest
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MaxValueValidatorTest {

  lateinit var context: Context

  @Before
  fun setup() {
    context = ApplicationProvider.getApplicationContext()
  }

  @Test
  fun `should return invalid result when entered value is greater than maxValue`() = runTest {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(
          Extension().apply {
            this.url = MAX_VALUE_EXTENSION_URL
            this.setValue(IntegerType(200000))
          },
        )
      }
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
        value = IntegerType(200001)
      }

    val validationResult =
      MaxValueValidator.validate(questionnaireItem, answer, context) {
        TestExpressionValueEvaluator.evaluate(questionnaireItem, it)
      }

    assertThat(validationResult.isValid).isFalse()
    assertThat(validationResult.errorMessage).isEqualTo("Maximum value allowed is:200000")
  }

  @Test
  fun `should return valid result when entered value is less than maxValue`() = runTest {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(
          Extension().apply {
            this.url = MAX_VALUE_EXTENSION_URL
            this.setValue(IntegerType(200000))
          },
        )
      }
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
        value = IntegerType(501)
      }

    val validationResult =
      MaxValueValidator.validate(questionnaireItem, answer, context) {
        TestExpressionValueEvaluator.evaluate(questionnaireItem, it)
      }

    assertThat(validationResult.isValid).isTrue()
    assertThat(validationResult.errorMessage.isNullOrBlank()).isTrue()
  }

  @Test
  fun `should return invalid result with correct max allowed value if contains only cqf-calculatedValue`() =
    runTest {
      val questionnaireItem =
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension(
            Extension().apply {
              this.url = MAX_VALUE_EXTENSION_URL
              this.setValue(
                DateType().apply {
                  addExtension(
                    Extension(
                      EXTENSION_CQF_CALCULATED_VALUE_URL,
                      Expression().apply {
                        expression = "today() - 7 'days'"
                        language = "text/fhirpath"
                      },
                    ),
                  )
                },
              )
            },
          )
        }
      val answer =
        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
          value = DateType().apply { value = Date() }
        }

      val validationResult =
        MaxValueValidator.validate(questionnaireItem, answer, context) {
          TestExpressionValueEvaluator.evaluate(questionnaireItem, it)
        }

      assertThat(validationResult.isValid).isFalse()
      assertThat(validationResult.errorMessage)
        .isEqualTo("Maximum value allowed is:${LocalDate.now().minusDays(7)}")
    }

  @Test
  fun `should return invalid result with correct max allowed value if contains both value and cqf-calculatedValue`() =
    runTest {
      val tenDaysAgo = LocalDate.now().minusDays(10)

      val questionnaireItem =
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension(
            Extension().apply {
              this.url = MAX_VALUE_EXTENSION_URL
              this.setValue(
                DateType().apply {
                  value =
                    Date.from(tenDaysAgo.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                  addExtension(
                    Extension(
                      EXTENSION_CQF_CALCULATED_VALUE_URL,
                      Expression().apply {
                        expression = "today() - 7 'days'"
                        language = "text/fhirpath"
                      },
                    ),
                  )
                },
              )
            },
          )
        }
      val answer =
        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
          value = DateType().apply { value = Date() }
        }

      val validationResult =
        MaxValueValidator.validate(questionnaireItem, answer, context) {
          TestExpressionValueEvaluator.evaluate(questionnaireItem, it)
        }

      assertThat(validationResult.isValid).isFalse()
      assertThat(validationResult.errorMessage)
        .isEqualTo("Maximum value allowed is:${LocalDate.now().minusDays(7)}")
    }

  @Test
  fun `should return valid result and removes constraint for an answer value when maxValue cqf-calculatedValue evaluates to empty`() =
    runTest {
      val questionnaireItem =
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension(
            Extension().apply {
              url = MAX_VALUE_EXTENSION_URL
              this.setValue(
                DateType().apply {
                  extension =
                    listOf(
                      Extension(
                        EXTENSION_CQF_CALCULATED_VALUE_URL,
                        Expression().apply {
                          language = "text/fhirpath"
                          expression = "yesterday()" // invalid FHIRPath expression
                        },
                      ),
                    )
                },
              )
            },
          )
        }

      val answer =
        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
          value = DateType(Date())
        }

      val validationResult =
        MaxValueValidator.validate(
          questionnaireItem,
          answer,
          InstrumentationRegistry.getInstrumentation().context,
        ) {
          TestExpressionValueEvaluator.evaluate(questionnaireItem, it)
        }

      assertThat(validationResult.isValid).isTrue()
      assertThat(validationResult.errorMessage.isNullOrBlank()).isTrue()
    }

  @Test
  fun `should return valid result and removes constraint for an answer with an empty value`() =
    runTest {
      val questionnaireItem =
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension(
            Extension().apply {
              url = MAX_VALUE_EXTENSION_URL
              this.setValue(
                DateType().apply {
                  extension =
                    listOf(
                      Extension(
                        EXTENSION_CQF_CALCULATED_VALUE_URL,
                        Expression().apply {
                          language = "text/fhirpath"
                          expression = "today()"
                        },
                      ),
                    )
                },
              )
            },
          )
        }

      val answer =
        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
          value = DateType()
        }

      val validationResult =
        MaxValueValidator.validate(
          questionnaireItem,
          answer,
          InstrumentationRegistry.getInstrumentation().context,
        ) {
          TestExpressionValueEvaluator.evaluate(questionnaireItem, it)
        }

      assertThat(validationResult.isValid).isTrue()
      assertThat(validationResult.errorMessage.isNullOrBlank()).isTrue()
    }
}
