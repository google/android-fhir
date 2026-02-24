/*
 * Copyright 2022-2026 Google LLC
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

package com.google.android.fhir.datacapture.views.factories

import android_fhir.datacapture_kmp.generated.resources.Res
import android_fhir.datacapture_kmp.generated.resources.min_value_less_than_max_value_validation_error_msg
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.google.android.fhir.datacapture.extensions.FhirR4Integer
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.extensions.sliderStepValue
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.components.ErrorText
import com.google.android.fhir.datacapture.views.components.Header
import com.google.android.fhir.datacapture.views.components.MediaItem
import com.google.android.fhir.datacapture.views.components.SliderItem
import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.QuestionnaireResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

internal object SliderViewFactory : QuestionnaireItemViewFactory {
  private const val SLIDER_DEFAULT_STEP_SIZE = 1
  private const val SLIDER_DEFAULT_VALUE_FROM = 0.0F
  private const val SLIDER_DEFAULT_VALUE_TO = 100.0F

  @Composable
  override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
    val validationMessage =
      remember(questionnaireViewItem) {
        displayValidationResult(questionnaireViewItem.validationResult)
      }
    val readOnly =
      remember(questionnaireViewItem) {
        questionnaireViewItem.questionnaireItem.readOnly?.value ?: false
      }
    val answer = remember(questionnaireViewItem) { questionnaireViewItem.answers.singleOrNull() }
    val minValue =
      remember(answer) {
        getFloatValue(questionnaireViewItem.minAnswerValue, ifNull = SLIDER_DEFAULT_VALUE_FROM)
      }
    val maxValue =
      remember(answer) {
        getFloatValue(questionnaireViewItem.maxAnswerValue, ifNull = SLIDER_DEFAULT_VALUE_TO)
      }

    check(minValue < maxValue) {
      stringResource(
        Res.string.min_value_less_than_max_value_validation_error_msg,
        minValue,
        maxValue,
      )
    }
    val stepSize =
      remember(questionnaireViewItem) {
        questionnaireViewItem.questionnaireItem.sliderStepValue ?: SLIDER_DEFAULT_STEP_SIZE
      }
    val steps =
      remember(stepSize, minValue, maxValue) { (maxValue - minValue).div(stepSize).toInt() - 1 }
    val questionnaireViewItemAnswerValue =
      remember(answer) { answer?.value?.asInteger()?.value?.value?.toFloat() ?: minValue }
    val coroutineScope = rememberCoroutineScope { Dispatchers.Main }

    Column(
      modifier =
        Modifier.fillMaxWidth()
          .padding(
            horizontal = QuestionnaireTheme.dimensions.itemMarginHorizontal,
            vertical = QuestionnaireTheme.dimensions.itemMarginVertical,
          ),
    ) {
      Header(questionnaireViewItem, showRequiredOrOptionalText = true)
      questionnaireViewItem.questionnaireItem.itemMedia?.let { MediaItem(it) }
      SliderItem(
        position = questionnaireViewItemAnswerValue,
        steps = steps,
        valueRange = minValue..maxValue,
        enabled = !readOnly,
      ) {
        coroutineScope.launch {
          questionnaireViewItem.setAnswer(
            QuestionnaireResponse.Item.Answer(
              value =
                QuestionnaireResponse.Item.Answer.Value.Integer(
                  FhirR4Integer(value = it.toInt()),
                ),
            ),
          )
        }
      }
      validationMessage?.let { ErrorText(it) }
    }
  }

  private fun displayValidationResult(validationResult: ValidationResult) =
    when (validationResult) {
      is NotValidated,
      Valid, -> null
      is Invalid -> validationResult.singleStringValidationMessage
    }

  private fun getFloatValue(extensionValue: Extension.Value?, ifNull: Float) =
    extensionValue?.asInteger()?.value?.value?.toFloat() ?: ifNull
}
