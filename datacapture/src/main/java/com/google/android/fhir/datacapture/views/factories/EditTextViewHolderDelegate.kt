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

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.getRequiredOrOptionalText
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.extensions.localizedFlyoverAnnotatedString
import com.google.android.fhir.datacapture.extensions.unit
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.EditTextFieldItem
import com.google.android.fhir.datacapture.views.compose.EditTextFieldState
import com.google.android.fhir.datacapture.views.compose.Header
import com.google.android.fhir.datacapture.views.compose.MediaItem
import com.google.android.fhir.datacapture.views.compose.UnitText
import kotlinx.coroutines.Dispatchers

class EditTextViewHolderDelegate(
  private val keyboardOptions: KeyboardOptions,
  private val uiInputText: (QuestionnaireViewItem) -> String?,
  private val uiValidationMessage: (QuestionnaireViewItem, Context) -> String?,
  private val handleInput: suspend (String, QuestionnaireViewItem) -> Unit,
  private val isMultiLine: Boolean = false,
) : QuestionnaireItemComposeViewHolderDelegate {

  @Composable
  override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
    val text = uiInputText(questionnaireViewItem) ?: ""
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope({ Dispatchers.Main })

    val validationUiMessage = uiValidationMessage(questionnaireViewItem, context)
    val composeViewQuestionnaireState =
      remember(questionnaireViewItem) {
        EditTextFieldState(
          initialInputText = text,
          handleTextInputChange = { handleInput(it, questionnaireViewItem) },
          coroutineScope = coroutineScope,
          hint = questionnaireViewItem.enabledDisplayItems.localizedFlyoverAnnotatedString,
          helperText = validationUiMessage.takeIf { !it.isNullOrBlank() }
              ?: getRequiredOrOptionalText(questionnaireViewItem, context),
          isError = !validationUiMessage.isNullOrBlank(),
          isReadOnly = questionnaireViewItem.questionnaireItem.readOnly,
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
          horizontal = dimensionResource(R.dimen.item_margin_horizontal),
          vertical = dimensionResource(R.dimen.item_margin_vertical),
        ),
    ) {
      Header(questionnaireViewItem)

      questionnaireItem.itemMedia?.let { MediaItem(it) }

      Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        EditTextFieldItem(modifier = Modifier.weight(1f), composeViewQuestionnaireState)

        if (!unit.isNullOrBlank()) {
          UnitText(unitString = unit)
        }
      }
    }
  }
}
