/*
 * Copyright 2022-2025 Google LLC
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
import android_fhir.datacapture_kmp.generated.resources.required
import android_fhir.datacapture_kmp.generated.resources.required_text_and_new_line
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.extensions.localizedFlyoverAnnotatedString
import com.google.android.fhir.datacapture.extensions.unit
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.EditTextFieldItem
import com.google.android.fhir.datacapture.views.compose.EditTextFieldState
import com.google.android.fhir.datacapture.views.compose.Header
import com.google.android.fhir.datacapture.views.compose.MediaItem
import com.google.android.fhir.datacapture.views.compose.UnitText
import com.google.android.fhir.datacapture.views.compose.getRequiredOrOptionalText
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

class EditTextViewFactoryDelegate(
  private val keyboardOptions: KeyboardOptions,
  private val uiInputText: (QuestionnaireViewItem) -> String?,
  private val handleInput: suspend (String, QuestionnaireViewItem) -> Unit,
  private val isMultiLine: Boolean = false,
  private val validationMessageStringRes: StringResource,
  private val validationMessageStringResArgs: Array<Any> = emptyArray(),
) : QuestionnaireItemViewFactory {

  @Composable
  override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
    val text = uiInputText(questionnaireViewItem) ?: ""
    val coroutineScope = rememberCoroutineScope()
    val requiredOptionalText = getRequiredOrOptionalText(questionnaireViewItem)
    val validationMessage = getValidationErrorMessage(questionnaireViewItem)
    val composeViewQuestionnaireState =
      remember(questionnaireViewItem) {
        EditTextFieldState(
          initialInputText = text,
          handleTextInputChange = { handleInput(it, questionnaireViewItem) },
          coroutineScope = coroutineScope,
          hint = questionnaireViewItem.enabledDisplayItems.localizedFlyoverAnnotatedString,
          helperText = validationMessage.takeIf { !it.isNullOrBlank() } ?: requiredOptionalText,
          isError = !validationMessage.isNullOrBlank(),
          isReadOnly = questionnaireViewItem.questionnaireItem.readOnly?.value == true,
          keyboardOptions = keyboardOptions,
          isMultiLine = isMultiLine,
        )
      }
    val unit =
      remember(questionnaireViewItem) { questionnaireViewItem.questionnaireItem.unit?.code }

    val questionnaireItem = questionnaireViewItem.questionnaireItem

    Column(
      modifier =
        Modifier.padding(
          horizontal = 16.dp,
          vertical = 16.dp,
        ),
    ) {
      Header(questionnaireViewItem)

      questionnaireItem.itemMedia?.let { MediaItem(it) }

      Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        EditTextFieldItem(modifier = Modifier.weight(1f), composeViewQuestionnaireState)

        if (!unit?.value.isNullOrBlank()) {
          UnitText(unitString = unit.value!!)
        }
      }
    }
  }

  /**
   * Returns the validation error message. If Questionnaire.Item.required is true, the error message
   * starts with `Required` text and the rest of the error message is placed on the next line.
   */
  @Composable
  private fun getValidationErrorMessage(questionnaireViewItem: QuestionnaireViewItem): String? {
    if (questionnaireViewItem.draftAnswer != null) {
      return stringResource(validationMessageStringRes, *validationMessageStringResArgs)
    }
    return when (questionnaireViewItem.validationResult) {
      is NotValidated,
      Valid, -> null
      is Invalid -> {
        val validationMessage =
          questionnaireViewItem.validationResult.getSingleStringValidationMessage()
        if (
          questionnaireViewItem.questionnaireItem.required?.value == true &&
            questionnaireViewItem.questionViewTextConfiguration.showRequiredText
        ) {
          stringResource(Res.string.required_text_and_new_line) + validationMessage
        } else {
          validationMessage
        }
      }
    }
  }
}
