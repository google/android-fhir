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
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.model.UriType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class RegexValidatorTest {

  @Test
  fun boolean_notMatchRegex_shouldReturnInvalidResult() {
    checkAnswerNotMatchRegex(regex = "true", value = BooleanType(false))
  }

  @Test
  fun boolean_matchRegex_shouldReturnValidResult() {
    checkAnswerMatchRegex(regex = "true", value = BooleanType(true))
  }

  @Test
  fun decimal_notMatchRegex_shouldReturnInvalidResult() {
    checkAnswerNotMatchRegex(regex = "[0-9]+\\.[0-9]+", value = DecimalType(31234))
  }

  @Test
  fun decimal_matchRegex_shouldReturnValidResult() {
    checkAnswerMatchRegex(regex = "[0-9]+\\.[0-9]+", value = DecimalType(3.1415926535))
  }

  @Test
  fun int_notMatchRegex_shouldReturnInvalidResult() {
    checkAnswerNotMatchRegex(regex = "[0-9]+", value = IntegerType(-1))
  }

  @Test
  fun int_matchRegex_shouldReturnValidResult() {
    checkAnswerMatchRegex(regex = "[0-9]+", value = IntegerType(1234567890))
  }

  @Test
  fun dateType_notMatchRegex_shouldReturnInvalidResult() {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    checkAnswerNotMatchRegex(
      regex = "[0-9]{2}-[0-9]{2}-[0-9]{2}",
      value = DateType(dateFormat.parse("2021-06-01"))
    )
  }

  @Test
  fun date_matchRegex_shouldReturnValidResult() {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    checkAnswerMatchRegex(
      regex = "[0-9]{4}-[0-9]{2}-[0-9]{2}",
      value = DateType(dateFormat.parse("2021-06-01"))
    )
  }

  @Test
  fun time_matchRegex_shouldReturnInvalidResult() {
    checkAnswerNotMatchRegex(regex = "[0-9]{2}:[0-9]{2}", value = TimeType("18:00:59"))
  }

  @Test
  fun time_notMatchRegex_shouldReturnValidResult() {
    checkAnswerMatchRegex(regex = "[0-9]{2}:[0-9]{2}:[0-9]{2}", value = TimeType("18:00:59"))
  }

  @Test
  fun string_notMatch_shouldReturnInvalidResult() {
    checkAnswerNotMatchRegex(
      regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
      value = StringType("www.hl7.org")
    )
  }

  @Test
  fun string_matchRegex_shouldReturnValidResult() {
    checkAnswerMatchRegex(
      regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
      value = StringType("https://www.hl7.org/")
    )
  }

  @Test
  fun uri_notMatch_shouldReturnInvalidResult() {
    checkAnswerNotMatchRegex(regex = "[a-z]+", value = UriType(URI.create("https://www.hl7.org/")))
  }

  @Test
  fun uri_answerOverMinLength_shouldReturnValidResult() {
    checkAnswerMatchRegex(
      regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
      value = UriType(URI.create("https://www.hl7.org/"))
    )
  }

  @Test
  fun invalidRegex_shouldReturnValidResult() {
    checkAnswerMatchRegex("[.*", StringType("http://www.google.com"))
  }

  //  @Test
  //  fun nonPrimitiveUnderMinLength_shouldReturnValidResult() {
  //    val requirement =
  //      Questionnaire.QuestionnaireItemComponent().apply {
  //        addExtension(
  //          Extension().apply {
  //            url = MIN_LENGTH_EXTENSION_URL
  //            this.setValue(IntegerType(100))
  //          }
  //        )
  //      }
  //    val response =
  //      QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
  //        addAnswer(
  //          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
  //            this.value = Quantity(1234567.89)
  //          }
  //        )
  //      }
  //
  //    val validationResult = PrimitiveTypeAnswerMaxLengthValidator.validate(requirement, response)
  //
  //    assertThat(validationResult.isValid).isTrue()
  //    assertThat(validationResult.message.isNullOrBlank()).isTrue()
  //  }

  private companion object {

    @JvmStatic
    fun checkAnswerMatchRegex(regex: String, value: PrimitiveType<*>) {
      val testComponent = createRegexQuestionnaireTestItem(regex, value)

      val validationResult =
        RegexValidator.validate(testComponent.requirement, testComponent.response)

      assertThat(validationResult.isValid).isTrue()
      assertThat(validationResult.message.isNullOrBlank()).isTrue()
    }

    @JvmStatic
    fun checkAnswerNotMatchRegex(regex: String, value: PrimitiveType<*>) {
      val testComponent = createRegexQuestionnaireTestItem(regex, value)

      val validationResult =
        RegexValidator.validate(testComponent.requirement, testComponent.response)

      assertThat(validationResult.isValid).isFalse()
      assertThat(validationResult.message)
        .isEqualTo("The answer doesn't match regular expression: $regex")
    }

    @JvmStatic
    fun createRegexQuestionnaireTestItem(
      regex: String,
      value: PrimitiveType<*>
    ): QuestionnaireTestItem {
      val questionnaireItem =
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension(
            Extension().apply {
              url = REGEX_EXTENSION_URL
              this.setValue(StringType(regex))
            }
          )
        }
      val questionnaireResponseItem =
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(QuestionnaireResponseItemAnswerComponent().apply { this.value = value })
        }
      return QuestionnaireTestItem(questionnaireItem, questionnaireResponseItem)
    }
  }
}
