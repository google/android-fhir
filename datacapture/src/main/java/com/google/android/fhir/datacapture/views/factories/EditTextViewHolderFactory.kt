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
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.View.FOCUS_DOWN
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.getRequiredOrOptionalText
import com.google.android.fhir.datacapture.extensions.localizedFlyoverAnnotatedString
import com.google.android.fhir.datacapture.extensions.localizedFlyoverSpanned
import com.google.android.fhir.datacapture.extensions.tryUnwrapContext
import com.google.android.fhir.datacapture.extensions.unit
import com.google.android.fhir.datacapture.views.HeaderView
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.QuestionnaireItemEditText
import com.google.android.fhir.datacapture.views.compose.QuestionnaireTextFieldState
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

internal abstract class EditTextViewHolderFactory(@LayoutRes override val resId: Int) :
  QuestionnaireItemViewHolderFactory(resId) {
  abstract override fun getQuestionnaireItemViewHolderDelegate():
    QuestionnaireItemEditTextViewHolderDelegate
}

class QuestionnaireItemEditTextViewHolderDelegate(
  private val rawInputType: Int,
  private val uiInputText: (QuestionnaireViewItem) -> String?,
  private val uiValidationMessage: (QuestionnaireViewItem, Context) -> String?,
  private val handleInput: suspend (String, QuestionnaireViewItem) -> Unit,
  private val isMultiLine: Boolean = false,
) : QuestionnaireItemViewHolderDelegate {
  override lateinit var questionnaireViewItem: QuestionnaireViewItem

  private lateinit var context: AppCompatActivity
  private lateinit var header: HeaderView

  private lateinit var composeView: ComposeView
  private val editTextMutableState: MutableState<String> by lazy {
    val text = uiInputText(questionnaireViewItem) ?: ""
    mutableStateOf(text)
  }

  override fun init(itemView: View) {
    context = itemView.context.tryUnwrapContext()!!
    header = itemView.findViewById(R.id.header)

    composeView = itemView.findViewById(R.id.text_input_view)
  }

  @OptIn(FlowPreview::class)
  override fun bind(questionnaireViewItem: QuestionnaireViewItem) {
    context.lifecycleScope.launch {
      snapshotFlow { editTextMutableState.value }
        .debounce(500.milliseconds)
        .filter { it != uiInputText(questionnaireViewItem) }
        .collectLatest { handleInput(it, questionnaireViewItem) }
    }

    val validationUiMessage = uiValidationMessage(questionnaireViewItem, context)
    val keyboardOptions =
      when (rawInputType) {
        InputType.TYPE_CLASS_PHONE ->
          KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done)
        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL ->
          KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done)
        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED ->
          KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES ->
          KeyboardOptions(
            keyboardType = KeyboardType.Text,
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Done,
          )
        else -> KeyboardOptions.Default
      }

    val composeViewQuestionnaireState =
      QuestionnaireTextFieldState(
        inputText = editTextMutableState,
        onInputValueChange = { editTextMutableState.value = it },
        hint = questionnaireViewItem.enabledDisplayItems.localizedFlyoverAnnotatedString,
        helperText =
          if (!validationUiMessage.isNullOrBlank()) {
            validationUiMessage
          } else {
            getRequiredOrOptionalText(questionnaireViewItem, context)
          },
        isError = !validationUiMessage.isNullOrBlank(),
        unitText = questionnaireViewItem.questionnaireItem.unit?.code,
        isReadOnly = questionnaireViewItem.questionnaireItem.readOnly,
        keyboardOptions = keyboardOptions,
        isMultiLine = isMultiLine,
      )

    composeView.setContent {
      Mdc3Theme { QuestionnaireItemEditText(composeViewQuestionnaireState) }
    }

    header.bind(questionnaireViewItem)
  }

  override fun setReadOnly(isReadOnly: Boolean) {}
}
