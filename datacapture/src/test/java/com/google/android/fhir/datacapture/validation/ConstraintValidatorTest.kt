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
class ConstraintValidatorTest {

  @Test
  fun questionnaireResponseItemValidator_validate_shouldReturnTrueIfAnswerBuilderListIsEmpty() {
    val questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent()
    val extension = Extension()
    extension.url = "http://hl7.org/fhir/StructureDefinition/maxValue"
    questionnaireItem.apply { addExtension(extension) }
    val validate =
      QuestionnaireResponseItemValidator.validate(questionnaireItem, questionnaireResponseItem)
    assertThat(validate.isEmpty()).isFalse()
    assertThat(validate[0].isValid).isTrue()
  }

  @Test
  fun questionnaireResponseItemValidator_validate_shouldReturnTrueIfThereIsNoExtension() {
    val questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent()
    questionnaireResponseItem.addAnswer(QuestionnaireResponseItemAnswerComponent())
    val validate =
      QuestionnaireResponseItemValidator.validate(questionnaireItem, questionnaireResponseItem)
    assertThat(validate.isEmpty()).isFalse()
    assertThat(validate[0].isValid).isTrue()
  }

  @Test
  fun questionnaireResponseItemValidator_validate_shouldReturnFalseIfAnswerIsGreaterThanMaxValue() {
    val maxValue = 200000
    val answerValue = 200001
    val extensionUrl = "http://hl7.org/fhir/StructureDefinition/maxValue"
    val validationMessage = "Maximum value allowed is:$maxValue"

    val questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent()
    val extension = Extension()
    val questionnaireResponseItemAnswerComponent = QuestionnaireResponseItemAnswerComponent()
    questionnaireResponseItemAnswerComponent.value = IntegerType(answerValue)
    questionnaireResponseItem.addAnswer(questionnaireResponseItemAnswerComponent)
    extension.url = extensionUrl
    extension.setValue(IntegerType(maxValue))
    questionnaireItem.apply { addExtension(extension) }

    val validate =
      QuestionnaireResponseItemValidator.validate(questionnaireItem, questionnaireResponseItem)

    assertThat(validate.isEmpty()).isFalse()
    assertThat(validate[0].isValid).isFalse()
    assertThat(validate[0].message.equals(validationMessage)).isTrue()
  }

  @Test
  fun questionnaireResponseItemValidator_validate_shouldReturnTrueIfAnswerIsLessThanMaxValue() {
    val maxValue = 200000
    val answerValue = 199999
    val extensionUrl = "http://hl7.org/fhir/StructureDefinition/maxValue"

    val questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent()
    val extension = Extension()
    val questionnaireResponseItemAnswerComponent = QuestionnaireResponseItemAnswerComponent()
    questionnaireResponseItemAnswerComponent.value = IntegerType(answerValue)
    questionnaireResponseItem.addAnswer(questionnaireResponseItemAnswerComponent)
    extension.url = extensionUrl
    extension.setValue(IntegerType(maxValue))
    questionnaireItem.apply { addExtension(extension) }

    val validate =
      QuestionnaireResponseItemValidator.validate(questionnaireItem, questionnaireResponseItem)

    assertThat(validate.isEmpty()).isFalse()
    assertThat(validate[0].isValid).isTrue()
    assertThat(validate[0].message == null).isTrue()
  }

  @Test
  fun questionnaireResponseItemValidator_validate_shouldReturnFalseIfAnswerIsLessThanMinValue() {
    val minValue = 250
    val answerValue = 249
    val extensionUrl = "http://hl7.org/fhir/StructureDefinition/minValue"
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

    val validate =
      QuestionnaireResponseItemValidator.validate(questionnaireItem, questionnaireResponseItem)

    assertThat(validate.isEmpty()).isFalse()
    assertThat(validate[1].isValid).isFalse()
    assertThat(validate[1].message.equals(validationMessage)).isTrue()
  }

  @Test
  fun questionnaireResponseItemValidator_validate_shouldReturnTrueIfAnswerIsGreaterThanMinValue() {
    val minValue = 250
    val answerValue = 2251
    val extensionUrl = "http://hl7.org/fhir/StructureDefinition/minValue"

    val questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent()
    val extension = Extension()
    val questionnaireResponseItemAnswerComponent = QuestionnaireResponseItemAnswerComponent()
    questionnaireResponseItemAnswerComponent.value = IntegerType(answerValue)
    questionnaireResponseItem.addAnswer(questionnaireResponseItemAnswerComponent)
    extension.url = extensionUrl
    extension.setValue(IntegerType(minValue))
    questionnaireItem.apply { addExtension(extension) }

    val validate =
      QuestionnaireResponseItemValidator.validate(questionnaireItem, questionnaireResponseItem)

    assertThat(validate.isEmpty()).isFalse()
    assertThat(validate[1].isValid).isTrue()
    assertThat(validate[1].message == null).isTrue()
  }
}
