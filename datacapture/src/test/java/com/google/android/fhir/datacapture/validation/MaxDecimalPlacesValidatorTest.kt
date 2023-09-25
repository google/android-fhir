/*
 * Copyright 2022-2023 Google LLC
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
class MaxDecimalPlacesValidatorTest {
  lateinit var context: Context

  @Before
  fun setup() {
    context = ApplicationProvider.getApplicationContext()
  }

  @Test
  fun validate_noExtension_shouldReturnValidResult() {
    val validationResult =
      MaxDecimalPlacesValidator.validate(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
          .setValue(DecimalType("1.00")),
        context,
      )

    assertThat(validationResult.isValid).isTrue()
    assertThat(validationResult.errorMessage.isNullOrBlank()).isTrue()
  }

  @Test
  fun validate_validAnswer_shouldReturnValidResult() {
    val validationResult =
      MaxDecimalPlacesValidator.validate(
        Questionnaire.QuestionnaireItemComponent().apply {
          this.addExtension(Extension(MAX_DECIMAL_URL, IntegerType(2)))
        },
        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
          .setValue(DecimalType("1.00")),
        context,
      )

    assertThat(validationResult.isValid).isTrue()
    assertThat(validationResult.errorMessage.isNullOrBlank()).isTrue()
  }

  @Test
  fun validate_tooManyDecimalPlaces_shouldReturnInvalidResult() {
    val validationResult =
      MaxDecimalPlacesValidator.validate(
        Questionnaire.QuestionnaireItemComponent().apply {
          this.addExtension(Extension(MAX_DECIMAL_URL, IntegerType(2)))
        },
        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
          .setValue(DecimalType("1.000")),
        context,
      )

    assertThat(validationResult.isValid).isFalse()
    assertThat(validationResult.errorMessage)
      .isEqualTo("The maximum number of decimal places that are permitted in the answer is: 2")
  }

  companion object {
    private const val MAX_DECIMAL_URL = "http://hl7.org/fhir/StructureDefinition/maxDecimalPlaces"
  }
}
