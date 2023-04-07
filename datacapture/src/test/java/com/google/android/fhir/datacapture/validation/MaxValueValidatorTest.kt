/*
 * Copyright 2022 Google LLC
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
import com.google.android.fhir.datacapture.extensions.EXTENSION_CQF_CALCULATED_VALUE_URL
import com.google.common.truth.Truth.assertThat
import java.text.SimpleDateFormat
import java.time.LocalDate
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
  fun `should return invalid result when entered value is greater than maxValue`() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(
          Extension().apply {
            this.url = MAX_VALUE_EXTENSION_URL
            this.setValue(IntegerType(200000))
          }
        )
      }
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
        value = IntegerType(200001)
      }

    val validationResult = MaxValueValidator.validate(questionnaireItem, answer, context)

    assertThat(validationResult.isValid).isFalse()
    assertThat(validationResult.errorMessage).isEqualTo("Maximum value allowed is:200000")
  }

  @Test
  fun `should return valid result when entered value is less than maxValue`() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(
          Extension().apply {
            this.url = MAX_VALUE_EXTENSION_URL
            this.setValue(IntegerType(200000))
          }
        )
      }
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
        value = IntegerType(501)
      }

    val validationResult = MaxValueValidator.validate(questionnaireItem, answer, context)

    assertThat(validationResult.isValid).isTrue()
    assertThat(validationResult.errorMessage.isNullOrBlank()).isTrue()
  }

  @Test
  fun `should return maxValue date`() {
    val dateType = DateType(SimpleDateFormat("yyyy-MM-dd").parse("2023-06-01"))
    val questionItem =
      listOf(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension(
            Extension().apply {
              url = MAX_VALUE_EXTENSION_URL
              this.setValue(dateType)
            }
          )
        }
      )

    assertThat((MaxValueValidator.getMaxValue(questionItem.first()) as? DateType)?.value)
      .isEqualTo(dateType.value)
  }

  @Test
  fun `should return today's date when expression evaluates to today`() {
    val today = LocalDate.now().toString()
    val questionItem =
      listOf(
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
                        }
                      )
                    )
                }
              )
            }
          )
        }
      )

    assertThat((MaxValueValidator.getMaxValue(questionItem.first()) as? DateType)?.valueAsString)
      .isEqualTo(today)
  }

  @Test
  fun `should return future's date when expression evaluates`() {
    val fiveDaysAhead = LocalDate.now().plusDays(5).toString()
    val questionItem =
      listOf(
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
                          expression = "today() + 5 'days' "
                        }
                      )
                    )
                }
              )
            }
          )
        }
      )

    assertThat((MaxValueValidator.getMaxValue(questionItem.first()) as? DateType)?.valueAsString)
      .isEqualTo(fiveDaysAhead)
  }
}
