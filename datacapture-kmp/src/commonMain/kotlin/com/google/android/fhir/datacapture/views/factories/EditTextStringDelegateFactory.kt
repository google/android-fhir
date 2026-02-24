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
import android_fhir.datacapture_kmp.generated.resources.required_text_and_new_line
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.google.fhir.model.r4.QuestionnaireResponse
import com.google.fhir.model.r4.String as FhirString

/**
 * Implementation of [EditTextViewFactoryDelegate] used in [EditTextSingleLineViewFactory] and
 * [EditTextMultiLineViewFactory].
 *
 * Any `ViewHolder` containing a `EditText` view that collects text data should use this class.
 */
internal fun createEditTextStringViewHolderDelegate(multiLine: Boolean = false) =
  EditTextViewFactoryDelegate(
    KeyboardOptions(
      keyboardType = KeyboardType.Text,
      capitalization = KeyboardCapitalization.Sentences,
      imeAction = ImeAction.Done,
    ),
    uiInputText = { it.answers.singleOrNull()?.value?.asString()?.value?.value ?: "" },
    handleInput = { inputText, questionnaireViewItem ->
      if (inputText.isEmpty()) {
        questionnaireViewItem.clearAnswer()
      } else {
        questionnaireViewItem.setAnswer(
          QuestionnaireResponse.Item.Answer(
            value = QuestionnaireResponse.Item.Answer.Value.String(FhirString(value = inputText)),
          ),
        )
      }
    },
    isMultiLine = multiLine,
    validationMessageStringRes = Res.string.required_text_and_new_line,
  )
