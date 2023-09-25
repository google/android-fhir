/*
 * Copyright 2022-2023 Google LLC
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

import android.text.Editable
import android.text.InputType
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType

/**
 * Implementation of [QuestionnaireItemEditTextViewHolderDelegate] used in
 * [EditTextSingleLineViewHolderFactory] and [EditTextMultiLineViewHolderFactory].
 *
 * Any `ViewHolder` containing a `EditText` view that collects text data should use this class.
 */
internal class EditTextStringViewHolderDelegate :
  QuestionnaireItemEditTextViewHolderDelegate(
    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES,
  ) {
  override fun handleInput(editable: Editable, questionnaireViewItem: QuestionnaireViewItem) {
    val input = getValue(editable.toString())
    if (input != null) {
      questionnaireViewItem.setAnswer(input)
    } else {
      questionnaireViewItem.clearAnswer()
    }
  }

  private fun getValue(
    text: String,
  ): QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent? {
    return text.let {
      if (it.isEmpty()) {
        null
      } else {
        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(StringType(it))
      }
    }
  }

  override fun updateUI(
    questionnaireViewItem: QuestionnaireViewItem,
    textInputEditText: TextInputEditText,
    textInputLayout: TextInputLayout,
  ) {
    val text = questionnaireViewItem.answers.singleOrNull()?.valueStringType?.value ?: ""
    if ((text != textInputEditText.text.toString())) {
      textInputEditText.setText(text)
    }
  }
}
