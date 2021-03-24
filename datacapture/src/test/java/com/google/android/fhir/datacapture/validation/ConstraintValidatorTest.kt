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
    fun maxValueValidator_validate_shouldValidateScenarios() {
        val extensionUrl = "http://hl7.org/fhir/StructureDefinition/maxValue"

        /**
         * Scenario 1 - answerValue is greater than maxValue
         */
        var maxValue = 200000
        var answerValue = 200001
        val validationMessage = "Maximum value allowed is:$maxValue"
        val maxValueValidatorScenarioOne =
            maxValueValidatorScenarioTester(answerValue, maxValue, extensionUrl)
        assertThat(maxValueValidatorScenarioOne.isValid).isFalse()
        assertThat(maxValueValidatorScenarioOne.message.equals(validationMessage)).isTrue()

        /**
         * Scenario 2 - answerValue is greater than maxValue
         */
        maxValue = 200000
        answerValue = 199999
        val maxValueValidatorScenarioTwo =
            maxValueValidatorScenarioTester(answerValue, maxValue, extensionUrl)
        assertThat(maxValueValidatorScenarioTwo.isValid).isTrue()
        assertThat(maxValueValidatorScenarioTwo.message.isNullOrBlank()).isTrue()
    }

    private fun maxValueValidatorScenarioTester(
        answerValue: Int,
        maxValue: Int,
        extensionUrl: String
    ): ConstraintValidator.ConstraintValidationResult {
        val questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()
        val questionnaireItem = Questionnaire.QuestionnaireItemComponent()
        val extension = Extension()
        val questionnaireResponseItemAnswerComponent = QuestionnaireResponseItemAnswerComponent()
        questionnaireResponseItemAnswerComponent.value = IntegerType(answerValue)
        questionnaireResponseItem.addAnswer(questionnaireResponseItemAnswerComponent)
        extension.url = extensionUrl
        extension.setValue(IntegerType(maxValue))
        questionnaireItem.apply { addExtension(extension) }
        return MaxValueValidator.validate(questionnaireItem, questionnaireResponseItem)
    }

    @Test
    fun minValueValidator_validate_shouldValidateScenarios() {
        val extensionUrl = "http://hl7.org/fhir/StructureDefinition/minValue"

        /**
         * Scenario 1 - answerValue is less than minValue
         */
        var minValue = 10
        var answerValue = 9
        val validationMessage = "Minimum value allowed is:$minValue"
        val minValueValidatorScenarioOne =
            minValueValidatorScenarioTester(answerValue, minValue, extensionUrl)
        assertThat(minValueValidatorScenarioOne.isValid).isFalse()
        assertThat(minValueValidatorScenarioOne.message.equals(validationMessage)).isTrue()

        /**
         * Scenario 2 - answerValue is greater than maxValue
         */
        minValue = 500
        answerValue = 501
        val minValueValidatorScenarioTwo =
            minValueValidatorScenarioTester(answerValue, minValue, extensionUrl)
        assertThat(minValueValidatorScenarioTwo.isValid).isTrue()
        assertThat(minValueValidatorScenarioTwo.message.isNullOrBlank()).isTrue()
    }

    private fun minValueValidatorScenarioTester(
        answerValue: Int,
        minValue: Int,
        extensionUrl: String
    ): ConstraintValidator.ConstraintValidationResult {
        val questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()
        val questionnaireItem = Questionnaire.QuestionnaireItemComponent()
        val extension = Extension()
        val questionnaireResponseItemAnswerComponent = QuestionnaireResponseItemAnswerComponent()
        questionnaireResponseItemAnswerComponent.value = IntegerType(answerValue)
        questionnaireResponseItem.addAnswer(questionnaireResponseItemAnswerComponent)
        extension.url = extensionUrl
        extension.setValue(IntegerType(minValue))
        questionnaireItem.apply { addExtension(extension) }
        return MinValueValidator.validate(questionnaireItem, questionnaireResponseItem)
    }

}
