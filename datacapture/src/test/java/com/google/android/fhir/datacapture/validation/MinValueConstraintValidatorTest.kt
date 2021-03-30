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
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MinValueConstraintValidatorTest {

  /** Scenario 1 - answerValue is less than minValue */
  @Test
  fun minValueValidator_validate_shouldValidateScenarioWhereAnswerValueIsLessThanMinValue() {
    val extensionUrl = "http://hl7.org/fhir/StructureDefinition/minValue"
    val minValue = 10
    val answerValue = 9
    val validationMessage = "Minimum value allowed is:$minValue"
    val questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent()
    val extension = Extension()
    val questionnaireResponseItemAnswerComponent = QuestionnaireResponseItemAnswerComponent()
    questionnaireResponseItemAnswerComponent.value = IntegerType(answerValue)
    questionnaireResponseItem.addAnswer(questionnaireResponseItemAnswerComponent)
    extension.url = extensionUrl
    extension.setValue(IntegerType(minValue))
    questionnaireItem.apply { addExtension(extension) }
    val minValueValidatorScenarioOne = MinValueConstraintValidator.validate(questionnaireItem, questionnaireResponseItem)
    assertThat(minValueValidatorScenarioOne.isValid).isFalse()
    assertThat(minValueValidatorScenarioOne.message.equals(validationMessage)).isTrue()
  }

  /** Scenario 2 - answerValue is greater than maxValue */
  @Test
  fun minValueValidator_validate_shouldValidateScenarioWhereAnswerValueIsGreaterThanMinValue() {
    val extensionUrl = "http://hl7.org/fhir/StructureDefinition/minValue"
    val minValue = 500
    val answerValue = 501
    val questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent()
    val extension = Extension()
    val questionnaireResponseItemAnswerComponent = QuestionnaireResponseItemAnswerComponent()
    questionnaireResponseItemAnswerComponent.value = IntegerType(answerValue)
    questionnaireResponseItem.addAnswer(questionnaireResponseItemAnswerComponent)
    extension.url = extensionUrl
    extension.setValue(IntegerType(minValue))
    questionnaireItem.apply { addExtension(extension) }
    val minValueValidatorScenarioTwo = MinValueConstraintValidator.validate(questionnaireItem, questionnaireResponseItem)
    assertThat(minValueValidatorScenarioTwo.isValid).isTrue()
    assertThat(minValueValidatorScenarioTwo.message.isNullOrBlank()).isTrue()
  }
}
