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
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.CQF_CALCULATED_EXPRESSION_URL
import com.google.common.truth.Truth.assertThat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MinValueConstraintValidatorTest {

  lateinit var context: Context

  @Before
  fun setup() {
    context = ApplicationProvider.getApplicationContext()
  }

  @Test
  fun shouldReturnInvalidResult() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(
          Extension().apply {
            url = MIN_VALUE_EXTENSION_URL
            this.setValue(IntegerType(10))
          }
        )
      }
    val answers =
      listOf(QuestionnaireResponseItemAnswerComponent().apply { value = IntegerType(9) })

    val validationResult =
      MinValueConstraintValidator.validate(
        questionnaireItem,
        answers,
        InstrumentationRegistry.getInstrumentation().context
      )

    assertThat(validationResult.isValid).isFalse()
    assertThat(validationResult.message).isEqualTo("Minimum value allowed is:10")
  }

  @Test
  fun shouldReturnValidResult() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(
          Extension().apply {
            url = MIN_VALUE_EXTENSION_URL
            this.setValue(IntegerType(500))
          }
        )
      }
    val answers =
      listOf(QuestionnaireResponseItemAnswerComponent().apply { value = IntegerType(501) })

    val validationResult =
      MinValueConstraintValidator.validate(
        questionnaireItem,
        answers,
        InstrumentationRegistry.getInstrumentation().context
      )

    assertThat(validationResult.isValid).isTrue()
    assertThat(validationResult.message.isNullOrBlank()).isTrue()
  }

  @Test
  fun shouldReturnInvalidResultWhenUsingExpressionForMinValue() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(
          Extension().apply {
            url = MIN_VALUE_EXTENSION_URL
            this.setValue(
              DateType().apply {
                extension =
                  listOf(
                    Extension(
                      CQF_CALCULATED_EXPRESSION_URL,
                      Expression().apply {
                        language = "text/fhirpath"
                        expression = "today() - 1 'days'"
                      }
                    )
                  )
              }
            )
          }
        )
      }
    val answerDate =
      DateType(
        SimpleDateFormat("yyyy-MM-dd")
          .parse(
            (DateTimeType.today()
              .apply {
                add(Calendar.YEAR, -1)
                add(Calendar.DAY_OF_MONTH, -1)
              }
              .valueAsString)
          )
      )
    val answers = listOf(QuestionnaireResponseItemAnswerComponent().apply { value = answerDate })

    val validationResult =
      MinValueConstraintValidator.validate(
        questionnaireItem,
        answers,
        InstrumentationRegistry.getInstrumentation().context
      )
    val expectedDateRange =
      (MinValueConstraintValidator.getMinValue(questionnaireItem) as? DateType)?.valueAsString
    assertThat(validationResult.isValid).isFalse()
    assertThat(validationResult.message).isEqualTo("Minimum value allowed is:$expectedDateRange")
  }

  @Test
  fun shouldReturnValidResultWhenUsingExpressionForMinValue() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(
          Extension().apply {
            url = MIN_VALUE_EXTENSION_URL
            this.setValue(
              DateType().apply {
                extension =
                  listOf(
                    Extension(
                      CQF_CALCULATED_EXPRESSION_URL,
                      Expression().apply {
                        language = "text/fhirpath"
                        expression = "today() - 1 'days'"
                      }
                    )
                  )
              }
            )
          }
        )
      }

    val answers =
      listOf(QuestionnaireResponseItemAnswerComponent().apply { value = DateType(Date()) })

    val validationResult =
      MinValueConstraintValidator.validate(
        questionnaireItem,
        answers,
        InstrumentationRegistry.getInstrumentation().context
      )

    assertThat(validationResult.isValid).isTrue()
    assertThat(validationResult.message.isNullOrBlank()).isTrue()
  }
}
