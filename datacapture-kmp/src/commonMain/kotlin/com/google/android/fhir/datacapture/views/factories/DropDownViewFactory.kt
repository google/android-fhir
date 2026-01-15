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
import android_fhir.datacapture_kmp.generated.resources.not_answered
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.android.fhir.datacapture.extensions.displayString
import com.google.android.fhir.datacapture.extensions.getRequiredOrOptionalText
import com.google.android.fhir.datacapture.extensions.getValidationErrorMessage
import com.google.android.fhir.datacapture.extensions.identifierString
import com.google.android.fhir.datacapture.extensions.itemAnswerOptionImage
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.extensions.localizedFlyoverAnnotatedString
import com.google.android.fhir.datacapture.extensions.toQuestionnaireResponseItemAnswer
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.AutoCompleteDropDownItem
import com.google.android.fhir.datacapture.views.compose.DropDownAnswerOption
import com.google.android.fhir.datacapture.views.compose.Header
import com.google.android.fhir.datacapture.views.compose.MediaItem
import com.google.android.fhir.datacapture.views.compose.getRequiredOrOptionalText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

internal object DropDownViewFactory : QuestionnaireItemViewFactory {
  @Composable
  override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope { Dispatchers.Main }
    val hyphen = stringResource(Res.string.hyphen)
    val notAnsweredTextString = stringResource(Res.string.not_answered)
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
      remember(questionnaireViewItem.enabledAnswerOptions) {
        questionnaireViewItem.enabledAnswerOptions.map {
          DropDownAnswerOption(
            it.value.identifierString(ifNull = notAnsweredTextString),
            it.value.displayString(ifNull = notAnsweredTextString),
            it.itemAnswerOptionImage(),
          )
        }
      }
    val validationErrorMessage =
      remember(questionnaireViewItem.validationResult) {
        getValidationErrorMessage(
          context,
          questionnaireViewItem,
          questionnaireViewItem.validationResult,
        )
          ?: ""
      }
    val showClearInput =
      remember(questionnaireViewItem.answers) { questionnaireViewItem.answers.isNotEmpty() }

    val dropDownOptions =
      remember(questionnaireItemAnswerDropDownOptions) {
        listOf(
          DropDownAnswerOption(hyphen, hyphen, null),
          *questionnaireItemAnswerDropDownOptions.toTypedArray(),
        )
      }
    val selectedAnswerIdentifier =
      remember(questionnaireViewItem.answers) {
        questionnaireViewItem.answers.singleOrNull()?.value?.identifierString(context)
      }
    val selectedOption =
      remember(dropDownOptions, selectedAnswerIdentifier) {
        questionnaireItemAnswerDropDownOptions.firstOrNull { it.ele == selectedAnswerIdentifier }
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
            it.value.identifierString(context) == answerOption?.answerId
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
