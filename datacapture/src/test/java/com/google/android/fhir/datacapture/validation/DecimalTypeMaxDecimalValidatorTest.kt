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
import com.google.common.truth.Truth
import org.hl7.fhir.r4.model.DecimalType
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
class DecimalTypeMaxDecimalValidatorTest {

  lateinit var context: Context

  @Before
  fun initContext() {
    context = ApplicationProvider.getApplicationContext()
  }

  @Test
  fun noAnswer_shouldReturnValidResult() {
    val validationResult =
      DecimalTypeMaxDecimalValidator.validate(
        Questionnaire.QuestionnaireItemComponent().apply {
          this.addExtension(Extension(MAX_DECIMAL_URL, IntegerType(1)))
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        context
      )

    Truth.assertThat(validationResult.isValid).isTrue()
    Truth.assertThat(validationResult.message.isNullOrBlank()).isTrue()
  }

  @Test
  fun validAnswer_shouldReturnValidResult() {
    val validationResult =
      DecimalTypeMaxDecimalValidator.validate(
        Questionnaire.QuestionnaireItemComponent().apply {
          this.addExtension(Extension(MAX_DECIMAL_URL, IntegerType(2)))
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DecimalType("1.00"))
          ),
        context
      )

    Truth.assertThat(validationResult.isValid).isTrue()
    Truth.assertThat(validationResult.message.isNullOrBlank()).isTrue()
  }
  @Test
  fun noExtension_shouldReturnValidResult() {
    val validationResult =
      DecimalTypeMaxDecimalValidator.validate(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DecimalType("1.00"))
          ),
        context
      )

    Truth.assertThat(validationResult.isValid).isTrue()
    Truth.assertThat(validationResult.message.isNullOrBlank()).isTrue()
  }

  @Test
  fun invalidAnswer_shouldReturnInvalidResult() {
    val validationResult =
      DecimalTypeMaxDecimalValidator.validate(
        Questionnaire.QuestionnaireItemComponent().apply {
          this.addExtension(Extension(MAX_DECIMAL_URL, IntegerType(2)))
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DecimalType("1.000"))
          ),
        context
      )

    Truth.assertThat(validationResult.isValid).isFalse()
    Truth.assertThat(validationResult.message)
      .isEqualTo("The maximum number of decimal places that are permitted in the answer is: 2")
  }
}

private const val MAX_DECIMAL_URL = "http://hl7.org/fhir/StructureDefinition/maxDecimalPlaces"
