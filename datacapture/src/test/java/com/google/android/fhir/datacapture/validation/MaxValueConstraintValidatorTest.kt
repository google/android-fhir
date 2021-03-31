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
import com.google.common.truth.Truth
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MaxValueConstraintValidatorTest {

  // Scenario 1 - answerValue is greater than maxValue
  @Test
  fun shouldReturnInvalidResult() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(
          Extension().apply {
            this.url = "http://hl7.org/fhir/StructureDefinition/maxValue"
            this.setValue(IntegerType(200000))
          }
        )
      }
    val questionnaireResponseItem =
      QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
        addAnswer(
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
            value = IntegerType(200001)
          }
        )
      }
    val validationResult =
      MaxValueConstraintValidator.validate(questionnaireItem, questionnaireResponseItem)
    Truth.assertThat(validationResult.isValid).isFalse()
    Truth.assertThat(validationResult.message.equals("Maximum value allowed is:200000"))
      .isTrue()
  }

  // Scenario 2 - answerValue is less than maxValue
  @Test
  fun shouldReturnValidResult() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(
          Extension().apply {
            this.url = "http://hl7.org/fhir/StructureDefinition/maxValue"
            this.setValue(IntegerType(200000))
          }
        )
      }
    val questionnaireResponseItem =
      QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
        addAnswer(
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
            value = IntegerType(501)
          }
        )
      }
    val validationResult =
      MaxValueConstraintValidator.validate(questionnaireItem, questionnaireResponseItem)
    Truth.assertThat(validationResult.isValid).isTrue()
    Truth.assertThat(validationResult.message.isNullOrBlank()).isTrue()
  }
}
