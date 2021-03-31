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
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class QuestionnaireResponseItemValidatorTest {

  @Test
  fun shouldReturnValidResult() {
    val extensionUrlMaxValue = "http://hl7.org/fhir/StructureDefinition/maxValue"
    val extensionUrlMinValue = "http://hl7.org/fhir/StructureDefinition/minValue"
    val extensionMaxValue = Extension()
    val extensionMinValue = Extension()
    val minValue = 250
    val maxValue = 200000
    val answerValue = 251
    extensionMaxValue.url = extensionUrlMaxValue
    extensionMaxValue.setValue(IntegerType(maxValue))
    extensionMinValue.url = extensionUrlMinValue
    extensionMinValue.setValue(IntegerType(minValue))
    val extensions =
      mutableListOf<Extension>().apply {
        add(extensionMaxValue)
        add(extensionMinValue)
      }
    val questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent()
    val questionnaireResponseItemAnswerComponent =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
    questionnaireResponseItemAnswerComponent.value = IntegerType(answerValue)
    questionnaireResponseItem.addAnswer(questionnaireResponseItemAnswerComponent)
    questionnaireItem.apply { extensions.forEach { addExtension(it) } }
    val validateAggregationFromChildValidators =
      QuestionnaireResponseItemValidator.validate(questionnaireItem, questionnaireResponseItem)
    assertThat(validateAggregationFromChildValidators.isValid).isTrue()
    assertThat(validateAggregationFromChildValidators.validationMessages.isEmpty()).isTrue()
  }

  @Test
  fun shouldReturnInvalidResultWithMessages() {
    val extensionUrlMaxValue = "http://hl7.org/fhir/StructureDefinition/maxValue"
    val extensionUrlMinValue = "http://hl7.org/fhir/StructureDefinition/minValue"
    val extensionMaxValue = Extension()
    val extensionMinValue = Extension()
    val minValue = 200000
    val maxValue = 250
    val answerValue = 10000
    extensionMaxValue.url = extensionUrlMaxValue
    extensionMaxValue.setValue(IntegerType(maxValue))
    extensionMinValue.url = extensionUrlMinValue
    extensionMinValue.setValue(IntegerType(minValue))
    val extensions =
      mutableListOf<Extension>().apply {
        add(extensionMaxValue)
        add(extensionMinValue)
      }
    val questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent()
    val questionnaireResponseItemAnswerComponent =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
    questionnaireResponseItemAnswerComponent.value = IntegerType(answerValue)
    questionnaireResponseItem.addAnswer(questionnaireResponseItemAnswerComponent)
    questionnaireItem.apply { extensions.forEach { addExtension(it) } }
    val validateAggregationFromChildValidators =
      QuestionnaireResponseItemValidator.validate(questionnaireItem, questionnaireResponseItem)
    assertThat(validateAggregationFromChildValidators.isValid).isFalse()
    assertThat(validateAggregationFromChildValidators.validationMessages.size == 2).isTrue()
  }
}
