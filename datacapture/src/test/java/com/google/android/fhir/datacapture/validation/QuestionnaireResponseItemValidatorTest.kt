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
  fun setup() {
    context = ApplicationProvider.getApplicationContext()
  }

  @Test
  fun `should return valid result`() {
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
    val answers =
      listOf(
        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
          value = IntegerType(275)
        }
      )

    val validationResult =
      QuestionnaireResponseItemValidator.validate(questionnaireItem, answers, context)

    assertThat(validationResult).isEqualTo(Valid)
  }

  @Test
  fun `should validate individual answers and combine results`() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        linkId = "a-question"
        type = Questionnaire.QuestionnaireItemType.INTEGER
        addExtension(
          Extension().apply {
            url = MIN_VALUE_EXTENSION_URL
            this.setValue(IntegerType(100))
          }
        )
        addExtension(
          Extension().apply {
            url = MAX_VALUE_EXTENSION_URL
            this.setValue(IntegerType(200))
          }
        )
      }
    val answers =
      listOf(
        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
          value = IntegerType(50)
        },
        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
          value = IntegerType(150)
        },
        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
          value = IntegerType(250)
        },
      )

    val validationResult =
      QuestionnaireResponseItemValidator.validate(questionnaireItem, answers, context)

    assertThat(validationResult).isInstanceOf(Invalid::class.java)
    val invalidValidationResult = validationResult as Invalid
    assertThat(invalidValidationResult.getSingleStringValidationMessage())
      .isEqualTo("Minimum value allowed is:100\nMaximum value allowed is:200")
  }

  @Test
  fun `should validate all answers`() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        type = Questionnaire.QuestionnaireItemType.INTEGER
        required = true
      }
    val answers = listOf<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>()

    val validationResult =
      QuestionnaireResponseItemValidator.validate(questionnaireItem, answers, context)

    assertThat(validationResult).isInstanceOf(Invalid::class.java)
    val invalidValidationResult = validationResult as Invalid
    assertThat(invalidValidationResult.getSingleStringValidationMessage())
      .isEqualTo("Missing answer for required field.")
  }
}
