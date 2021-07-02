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
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class QuestionnaireResponseItemValidatorTest {

  lateinit var context: Context

  @Before
  fun initContext() {
    context = ApplicationProvider.getApplicationContext()
  }

  @Test
  fun shouldReturnValidResult() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(
          Extension().apply {
            url = MIN_VALUE_EXTENSION_URL
            this.setValue(IntegerType(250))
          }
        )
        addExtension(
          Extension().apply {
            url = MAX_VALUE_EXTENSION_URL
            this.setValue(IntegerType(300))
          }
        )
        addExtension(
          Extension().apply {
            url = REGEX_EXTENSION_URL
            this.setValue(StringType("[0-9]+"))
          }
        )
      }
    val questionnaireResponseItem =
      QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
        addAnswer(
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
            value = IntegerType(275)
          }
        )
      }

    val validateAggregationFromChildValidators =
      QuestionnaireResponseItemValidator.validate(
        questionnaireItem,
        questionnaireResponseItem,
        context
      )

    assertThat(validateAggregationFromChildValidators.isValid).isTrue()
    assertThat(validateAggregationFromChildValidators.validationMessages).isEmpty()
  }

  @Test
  fun exceededMaxMinValue_shouldReturnInvalidResultWithMessages() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        linkId = "a-question"
        addExtension(
          Extension().apply {
            url = MIN_VALUE_EXTENSION_URL
            this.setValue(IntegerType(600))
          }
        )
        addExtension(
          Extension().apply {
            url = MAX_VALUE_EXTENSION_URL
            this.setValue(IntegerType(500))
          }
        )
      }
    val questionnaireResponseItem =
      QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
        linkId = "a-question"
        addAnswer(
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
            value = IntegerType(550)
          }
        )
      }

    val validateAggregationFromChildValidators =
      QuestionnaireResponseItemValidator.validate(
        questionnaireItem,
        questionnaireResponseItem,
        context
      )

    assertThat(validateAggregationFromChildValidators.isValid).isFalse()
    assertThat(validateAggregationFromChildValidators.validationMessages.size).isEqualTo(2)
    assertThat(validateAggregationFromChildValidators.validationMessages[0])
      .isEqualTo("Maximum value allowed is:500")
    assertThat(validateAggregationFromChildValidators.validationMessages[1])
      .isEqualTo("Minimum value allowed is:600")
  }

  @Test
  fun exceededMaxMinLength_shouldReturnInvalidResultWithMessages() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        maxLength = 10
        addExtension(
          Extension().apply {
            url = MIN_LENGTH_EXTENSION_URL
            this.setValue(IntegerType(20))
          }
        )
      }
    val questionnaireResponseItem =
      QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
        addAnswer(
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
            value = StringType("Length: 15chars")
          }
        )
      }

    val validateAggregationFromChildValidators =
      QuestionnaireResponseItemValidator.validate(
        questionnaireItem,
        questionnaireResponseItem,
        context
      )

    assertThat(validateAggregationFromChildValidators.isValid).isFalse()
    assertThat(validateAggregationFromChildValidators.validationMessages.size).isEqualTo(2)
    assertThat(validateAggregationFromChildValidators.validationMessages[0])
      .isEqualTo("The maximum number of characters that are permitted in the answer is: 10")
    assertThat(validateAggregationFromChildValidators.validationMessages[1])
      .isEqualTo("The minimum number of characters that are permitted in the answer is: 20")
  }

  @Test
  fun notMatchingRegex_shouldReturnInvalidResultWithMessages() {
    val regex = "[0-9]+\\.[0-9]+"
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
        addAnswer(
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
            value = IntegerType("3141516")
          }
        )
      }

    val validateAggregationFromChildValidators =
      QuestionnaireResponseItemValidator.validate(
        questionnaireItem,
        questionnaireResponseItem,
        context
      )

    assertThat(validateAggregationFromChildValidators.isValid).isFalse()
    assertThat(validateAggregationFromChildValidators.validationMessages.size).isEqualTo(1)
    assertThat(validateAggregationFromChildValidators.validationMessages[0])
      .isEqualTo("The answer doesn't match regular expression: $regex")
  }
}
