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

  /** Scenario 1 - answerValue is greater than maxValue */
  @Test
  fun maxValueValidator_validate_shouldValidateScenarioWhereAnswerValueIsGreaterThanMaxValue() {
    val extensionUrl = "http://hl7.org/fhir/StructureDefinition/maxValue"
    val maxValue = 200000
    val answerValue = 200001
    val validationMessage = "Maximum value allowed is:$maxValue"
    val maxValueValidatorScenarioOne =
      maxValueValidatorScenarioTester(answerValue, maxValue, extensionUrl)
    Truth.assertThat(maxValueValidatorScenarioOne.isValid).isFalse()
    Truth.assertThat(maxValueValidatorScenarioOne.message.equals(validationMessage)).isTrue()
  }

  /** Scenario 2 - answerValue is greater than maxValue */
  @Test
  fun maxValueValidator_validate_shouldValidateScenarioWhereAnswerValueIsSmallerThanMaxValue() {
    val maxValue = 200000
    val answerValue = 199999
    val extensionUrl = "http://hl7.org/fhir/StructureDefinition/maxValue"
    val maxValueValidatorScenarioTwo =
      maxValueValidatorScenarioTester(answerValue, maxValue, extensionUrl)
    Truth.assertThat(maxValueValidatorScenarioTwo.isValid).isTrue()
    Truth.assertThat(maxValueValidatorScenarioTwo.message.isNullOrBlank()).isTrue()
  }

  private fun maxValueValidatorScenarioTester(
    answerValue: Int,
    maxValue: Int,
    extensionUrl: String
  ): ConstraintValidator.ConstraintValidationResult {
    val questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent()
    val extension = Extension()
    val questionnaireResponseItemAnswerComponent =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
    questionnaireResponseItemAnswerComponent.value = IntegerType(answerValue)
    questionnaireResponseItem.addAnswer(questionnaireResponseItemAnswerComponent)
    extension.url = extensionUrl
    extension.setValue(IntegerType(maxValue))
    questionnaireItem.apply { addExtension(extension) }
    return MaxValueConstraintValidator.validate(questionnaireItem, questionnaireResponseItem)
  }
}
