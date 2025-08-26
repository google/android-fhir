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

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.google.android.fhir.datacapture.extensions.getValidationErrorMessage
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType

/**
 * Implementation of [EditTextViewHolderDelegate] used in [EditTextSingleLineViewHolderFactory] and
 * [EditTextMultiLineViewHolderFactory].
 *
 * Any `ViewHolder` containing a `EditText` view that collects text data should use this class.
 */
internal fun EditTextStringViewHolderDelegate(multiLine: Boolean = false) =
  EditTextViewHolderDelegate(
    KeyboardOptions(
      keyboardType = KeyboardType.Text,
      capitalization = KeyboardCapitalization.Sentences,
      imeAction = ImeAction.Done,
    ),
    uiInputText = { it.answers.singleOrNull()?.valueStringType?.value ?: "" },
    uiValidationMessage = { questionnaireViewItem, context ->
      getValidationErrorMessage(
        context,
        questionnaireViewItem,
        questionnaireViewItem.validationResult,
      )
    },
    handleInput = { inputText, questionnaireViewItem ->
      val input =
        inputText.let {
          if (it.isEmpty()) {
            null
          } else {
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(StringType(it))
          }
        }
      if (input != null) {
        questionnaireViewItem.setAnswer(input)
      } else {
        questionnaireViewItem.clearAnswer()
      }
    },
    isMultiLine = multiLine,
  )
