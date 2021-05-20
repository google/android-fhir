/*
 * Copyright 2020 Google LLC
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

import android.os.Build
import com.google.common.truth.Truth.assertThat
import java.net.URI
import java.text.SimpleDateFormat
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.PrimitiveType
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.model.UriType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class PrimitiveTypeAnswerMinLengthValidatorTest {
  @Test
  fun boolean_answerBelowMinLength_shouldReturnInvalidResult() {
    checkAnswerBelowMinLength(minLength = 10, value = BooleanType(false))
  }

  @Test
  fun boolean_answerExceededMinLength_shouldReturnValidResult() {
    checkAnswerExceededMinLength(minLength = 5, value = BooleanType(false))
  }

  @Test
  fun decimal_answerBelowMinLength_shouldReturnInvalidResult() {
    checkAnswerBelowMinLength(minLength = 15, value = DecimalType(3.1415926535))
  }

  @Test
  fun decimal_answerExceededMinLength_shouldReturnValidResult() {
    checkAnswerExceededMinLength(minLength = 10, value = DecimalType(3.1415926535))
  }

  @Test
  fun int_answerBelowMinLength_shouldReturnInvalidResult() {
    checkAnswerBelowMinLength(minLength = 5, value = IntegerType(123))
  }

  @Test
  fun int_answerExceededMinLength_shouldReturnValidResult() {
    checkAnswerExceededMinLength(minLength = 10, value = IntegerType(1234567890))
  }

  @Test
  fun dateType_answerBelowMinLength_shouldReturnInvalidResult() {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    checkAnswerBelowMinLength(minLength = 11, value = DateType(dateFormat.parse("2021-06-01")))
  }

  @Test
  fun date_answerExceededMinLength_shouldReturnValidResult() {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    checkAnswerExceededMinLength(minLength = 10, value = DateType(dateFormat.parse("2021-06-01")))
  }

  @Test
  fun time_answerBelowMinLength_shouldReturnInvalidResult() {
    checkAnswerBelowMinLength(minLength = 10, value = TimeType("18:00:59"))
  }

  @Test
  fun time_answerExceededMinLength_shouldReturnValidResult() {
    checkAnswerExceededMinLength(minLength = 5, value = TimeType("18:00:59"))
  }

  @Test
  fun string_answerBelowMinLength_shouldReturnInvalidResult() {
    checkAnswerBelowMinLength(minLength = 12, value = StringType("Hello World"))
  }

  @Test
  fun string_answerExceededMinLength_shouldReturnValidResult() {
    checkAnswerExceededMinLength(minLength = 5, value = StringType("Hello World"))
  }

  @Test
  fun uri_answerBelowMinLength_shouldReturnInvalidResult() {
    checkAnswerBelowMinLength(minLength = 21, value = UriType(URI.create("https://www.hl7.org/")))
  }

  @Test
  fun uri_answerExceededMinLength_shouldReturnValidResult() {
    checkAnswerExceededMinLength(minLength = 5, value = UriType(URI.create("https://www.hl7.org/")))
  }

  @Test
  fun nonPrimitiveBelowMinLength_shouldReturnValidResult() {
    val requirement =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(
          Extension().apply {
            url = MIN_LENGTH_EXTENSION_URL
            this.setValue(IntegerType(100))
          }
        )
      }
    val response =
      QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
        addAnswer(
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
            this.value = Quantity(1234567.89)
          }
        )
      }

    val validationResult = PrimitiveTypeAnswerMaxLengthValidator.validate(requirement, response)

    assertThat(validationResult.isValid).isTrue()
    assertThat(validationResult.message.isNullOrBlank()).isTrue()
  }

  private fun checkAnswerExceededMinLength(minLength: Int, value: PrimitiveType<*>) {
    val testComponent = createMaxLengthQuestionnaireTestItem(minLength, value)

    val validationResult =
      PrimitiveTypeAnswerMinLengthValidator.validate(
        testComponent.requirement,
        testComponent.response
      )

    assertThat(validationResult.isValid).isTrue()
    assertThat(validationResult.message.isNullOrBlank()).isTrue()
  }

  private fun checkAnswerBelowMinLength(minLength: Int, value: PrimitiveType<*>) {
    val testComponent = createMaxLengthQuestionnaireTestItem(minLength, value)

    val validationResult =
      PrimitiveTypeAnswerMinLengthValidator.validate(
        testComponent.requirement,
        testComponent.response
      )

    assertThat(validationResult.isValid).isFalse()
    assertThat(validationResult.message)
      .isEqualTo("The minimum number of characters that are permitted in the answer is: $minLength")
  }

  private fun createMaxLengthQuestionnaireTestItem(
    minLength: Int,
    value: PrimitiveType<*>
  ): QuestionnaireTestItem {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(
          Extension().apply {
            url = MIN_LENGTH_EXTENSION_URL
            this.setValue(IntegerType(minLength))
          }
        )
      }
    val questionnaireResponseItem =
      QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
        addAnswer(
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
            this.value = value
          }
        )
      }
    return QuestionnaireTestItem(questionnaireItem, questionnaireResponseItem)
  }
}
