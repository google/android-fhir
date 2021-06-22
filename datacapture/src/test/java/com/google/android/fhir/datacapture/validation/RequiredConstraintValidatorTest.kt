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
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class RequiredConstraintValidatorTest {

  @Test
  fun shouldReturnValidResult() {
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent().apply { required = true }
    val questionnaireResponseItem =
      QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
        addAnswer(
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
            value = IntegerType(9)
          }
        )
      }

    val validationResult =
      RequiredConstraintValidator.validate(
        questionnaireItem,
        questionnaireResponseItem,
        InstrumentationRegistry.getInstrumentation().context
      )
    assertThat(validationResult.isValid).isTrue()
  }

  @Test
  fun shouldReturnInvalidResult() {
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent().apply { required = true }
    val questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()

    val validationResult =
      RequiredConstraintValidator.validate(
        questionnaireItem,
        questionnaireResponseItem,
        InstrumentationRegistry.getInstrumentation().context
      )
    assertThat(validationResult.isValid).isFalse()
    assertThat(validationResult.message).isEqualTo("Missing answer for required field.")
  }
}
