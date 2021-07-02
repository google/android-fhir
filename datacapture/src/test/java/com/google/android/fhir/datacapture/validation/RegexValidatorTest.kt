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

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
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
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.model.UriType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class RegexValidatorTest {

  lateinit var context: Context

  @Before
  fun initContext() {
    context = ApplicationProvider.getApplicationContext()
  }

  @Test
  fun boolean_notMatchingRegex_shouldReturnInvalidResult() {
    checkAnswerNotMatchingRegex(regex = "true", value = BooleanType(false))
  }

  @Test
  fun boolean_matchingRegex_shouldReturnValidResult() {
    checkAnswerMatchingRegex(regex = "true", value = BooleanType(true))
  }

  @Test
  fun decimal_notMatchingRegex_shouldReturnInvalidResult() {
    checkAnswerNotMatchingRegex(regex = "[0-9]+\\.[0-9]+", value = DecimalType(31234))
  }

  @Test
  fun decimal_matchingRegex_shouldReturnValidResult() {
    checkAnswerMatchingRegex(regex = "[0-9]+\\.[0-9]+", value = DecimalType(3.1415926535))
  }

  @Test
  fun int_notMatchingRegex_shouldReturnInvalidResult() {
    checkAnswerNotMatchingRegex(regex = "[0-9]+", value = IntegerType(-1))
  }

  @Test
  fun int_matchingRegex_shouldReturnValidResult() {
    checkAnswerMatchingRegex(regex = "[0-9]+", value = IntegerType(1234567890))
  }

  @Test
  fun dateType_notMatchingRegex_shouldReturnInvalidResult() {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    checkAnswerNotMatchingRegex(
      regex = "[0-9]{2}-[0-9]{2}-[0-9]{2}",
      value = DateType(dateFormat.parse("2021-06-01"))
    )
  }

  @Test
  fun date_matchingRegex_shouldReturnValidResult() {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    checkAnswerMatchingRegex(
      regex = "[0-9]{4}-[0-9]{2}-[0-9]{2}",
      value = DateType(dateFormat.parse("2021-06-01"))
    )
  }

  @Test
  fun time_matchingRegex_shouldReturnInvalidResult() {
    checkAnswerNotMatchingRegex(regex = "[0-9]{2}:[0-9]{2}", value = TimeType("18:00:59"))
  }

  @Test
  fun time_notMatchingRegex_shouldReturnValidResult() {
    checkAnswerMatchingRegex(regex = "[0-9]{2}:[0-9]{2}:[0-9]{2}", value = TimeType("18:00:59"))
  }

  @Test
  fun string_notMatchingRegex_shouldReturnInvalidResult() {
    checkAnswerNotMatchingRegex(
      regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
      value = StringType("www.hl7.org")
    )
  }

  @Test
  fun string_matchingRegex_shouldReturnValidResult() {
    checkAnswerMatchingRegex(
      regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
      value = StringType("https://www.hl7.org/")
    )
  }

  @Test
  fun uri_notMatchingRegex_shouldReturnInvalidResult() {
    checkAnswerNotMatchingRegex(
      regex = "[a-z]+",
      value = UriType(URI.create("https://www.hl7.org/"))
    )
  }

  @Test
  fun uri_matchingRegex_shouldReturnValidResult() {
    checkAnswerMatchingRegex(
      regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
      value = UriType(URI.create("https://www.hl7.org/"))
    )
  }

  @Test
  fun invalidRegex_shouldReturnValidResult() {
    checkAnswerMatchingRegex("[.*", StringType("http://www.google.com"))
  }

  @Test
  fun nonPrimitive_notMatchingRegex_shouldReturnValidResult() {
    val requirement =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(
          Extension().apply {
            url = REGEX_EXTENSION_URL
            this.setValue(StringType("[0-9]+"))
          }
        )
      }
    val response =
      QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
        addAnswer(
          QuestionnaireResponseItemAnswerComponent().apply { this.value = Quantity(1234567.89) }
        )
      }

    val validationResult = RegexValidator.validate(requirement, response, context)

    assertThat(validationResult.isValid).isTrue()
    assertThat(validationResult.message.isNullOrBlank()).isTrue()
  }

  private companion object {

    var context: Context = ApplicationProvider.getApplicationContext()

    @JvmStatic
    fun checkAnswerMatchingRegex(regex: String, value: PrimitiveType<*>) {
      val testComponent = createRegexQuestionnaireTestItem(regex, value)

      val validationResult =
        RegexValidator.validate(testComponent.requirement, testComponent.response, context)

      assertThat(validationResult.isValid).isTrue()
      assertThat(validationResult.message.isNullOrBlank()).isTrue()
    }

    @JvmStatic
    fun checkAnswerNotMatchingRegex(regex: String, value: PrimitiveType<*>) {
      val testComponent = createRegexQuestionnaireTestItem(regex, value)

      val validationResult =
        RegexValidator.validate(testComponent.requirement, testComponent.response, context)

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
