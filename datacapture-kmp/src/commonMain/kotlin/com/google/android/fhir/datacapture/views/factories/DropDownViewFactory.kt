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
import android_fhir.datacapture_kmp.generated.resources.hyphen
import android_fhir.datacapture_kmp.generated.resources.required_text_and_new_line
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.google.android.fhir.datacapture.extensions.FhirR4String
import com.google.android.fhir.datacapture.extensions.elementValue
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.extensions.localizedFlyoverAnnotatedString
import com.google.android.fhir.datacapture.extensions.toQuestionnaireResponseItemAnswer
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.components.AutoCompleteDropDownItem
import com.google.android.fhir.datacapture.views.components.DropDownAnswerOption
import com.google.android.fhir.datacapture.views.components.Header
import com.google.android.fhir.datacapture.views.components.MediaItem
import com.google.android.fhir.datacapture.views.components.getRequiredOrOptionalText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

internal object DropDownViewFactory : QuestionnaireItemViewFactory {
  @Composable
  override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
    val coroutineScope = rememberCoroutineScope { Dispatchers.Main }
    val hyphen = stringResource(Res.string.hyphen)
    val isQuestionnaireItemReadOnly =
      remember(questionnaireViewItem.questionnaireItem) {
        questionnaireViewItem.questionnaireItem.readOnly?.value ?: false
      }
    val flyOverText =
      remember(questionnaireViewItem.enabledDisplayItems) {
        questionnaireViewItem.enabledDisplayItems.localizedFlyoverAnnotatedString
      }
    val requiredOptionalText = getRequiredOrOptionalText(questionnaireViewItem)
    val questionnaireItemAnswerDropDownOptions =
      questionnaireViewItem.enabledAnswerOptions.map { DropDownAnswerOption.of(it) }

    val requiredTextAndNewLineString = stringResource(Res.string.required_text_and_new_line)

    val validationErrorMessage =
      remember(questionnaireViewItem.validationResult) {
        when (val validationResult = questionnaireViewItem.validationResult) {
          is Invalid -> {
            if (
              questionnaireViewItem.questionnaireItem.required?.value == true &&
                questionnaireViewItem.questionViewTextConfiguration.showRequiredText
            ) {
              "$requiredTextAndNewLineString${validationResult.singleStringValidationMessage}"
            } else {
              validationResult.singleStringValidationMessage
            }
          }
          else -> ""
        }
      }
    val showClearInput =
      remember(questionnaireViewItem.answers.toString()) {
        questionnaireViewItem.answers.isNotEmpty()
      }

    val dropDownOptions =
      remember(questionnaireItemAnswerDropDownOptions) {
        listOf(
          DropDownAnswerOption(elementValue = FhirR4String(value = hyphen), hyphen, null),
          *questionnaireItemAnswerDropDownOptions.toTypedArray(),
        )
      }
    val selectedAnswerIdentifier =
      remember(questionnaireViewItem.answers.toString()) {
        questionnaireViewItem.answers.singleOrNull()?.elementValue
      }
    val selectedOption =
      remember(dropDownOptions, selectedAnswerIdentifier) {
        questionnaireItemAnswerDropDownOptions.firstOrNull {
          it.elementValue == selectedAnswerIdentifier
        }
      }

    Column(
      modifier =
        Modifier.fillMaxWidth()
          .padding(
            horizontal = QuestionnaireTheme.dimensions.itemMarginHorizontal,
            vertical = QuestionnaireTheme.dimensions.itemMarginVertical,
          ),
    ) {
      Header(questionnaireViewItem)
      questionnaireViewItem.questionnaireItem.itemMedia?.let { MediaItem(it) }

      AutoCompleteDropDownItem(
        modifier = Modifier.fillMaxWidth(),
        enabled = !isQuestionnaireItemReadOnly,
        labelText = flyOverText,
        supportingText = validationErrorMessage.ifBlank { requiredOptionalText },
        isError = validationErrorMessage.isNotBlank(),
        showClearIcon = showClearInput,
        selectedOption = selectedOption,
        options = dropDownOptions,
      ) { answerOption ->
        val selectedAnswer =
          questionnaireViewItem.enabledAnswerOptions.firstOrNull {
            it.elementValue == answerOption?.elementValue
          }

        coroutineScope.launch {
          if (selectedAnswer != null) {
            questionnaireViewItem.setAnswer(
              selectedAnswer.toQuestionnaireResponseItemAnswer(),
            )
          } else {
            questionnaireViewItem.clearAnswer()
          }
        }
      }
    }
  }
}
