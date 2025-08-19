/*
 * Copyright 2025 Google LLC
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

package com.google.android.fhir.datacapture.views.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.fhir.datacapture.R
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun EditTextFieldQuestionnaireItem(questionnaireTextFieldState: QuestionnaireTextFieldState) {
  EditTextFieldQuestionnaireItem(
    inputText = questionnaireTextFieldState.inputText,
    onInputTextChange = questionnaireTextFieldState::onInputTextChange,
    hint = questionnaireTextFieldState.hint,
    helperText = questionnaireTextFieldState.helperText,
    isError = questionnaireTextFieldState.isError,
    isReadOnly = questionnaireTextFieldState.isReadOnly,
    keyboardOptions = questionnaireTextFieldState.keyboardOptions,
    isMultiLine = questionnaireTextFieldState.isMultiLine,
    unitText = questionnaireTextFieldState.unitText,
  )
}

@Composable
fun EditTextFieldQuestionnaireItem(
  inputText: String,
  onInputTextChange: (String) -> Unit,
  hint: AnnotatedString?,
  helperText: String?,
  isError: Boolean,
  isReadOnly: Boolean,
  isMultiLine: Boolean,
  keyboardOptions: KeyboardOptions,
  unitText: String?,
) {
  Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
    OutlinedEditTextFieldItem(
      modifier = Modifier.weight(1f),
      inputText = inputText,
      onInputTextChange = onInputTextChange,
      hint = hint,
      helperText = helperText,
      isError = isError,
      isReadOnly = isReadOnly,
      keyboardOptions = keyboardOptions,
      isMultiLine = isMultiLine,
    )
    if (!unitText.isNullOrEmpty()) {
      UnitText(modifier = Modifier, unitString = unitText)
    }
  }
}

@Composable
fun OutlinedEditTextFieldItem(
  modifier: Modifier,
  inputText: String,
  onInputTextChange: (String) -> Unit,
  hint: AnnotatedString?,
  helperText: String?,
  isError: Boolean,
  isReadOnly: Boolean,
  keyboardOptions: KeyboardOptions,
  isMultiLine: Boolean,
) {
  val focusManager = LocalFocusManager.current
  val keyboardController = LocalSoftwareKeyboardController.current

  OutlinedTextField(
    value = inputText,
    onValueChange = { onInputTextChange.invoke(it) },
    minLines = if (isMultiLine) 3 else 1,
    singleLine = !isMultiLine,
    modifier =
      modifier
        .onFocusChanged {
          if (!it.isFocused) {
            keyboardController?.hide()
          }
        }
        .testTag(EDIT_TEXT_FIELD_TEST_TAG),
    label = { hint?.let { Text(it) } },
    supportingText = { helperText?.let { Text(it) } },
    isError = isError,
    colors = OutlinedTextFieldDefaults.colors(),
    trailingIcon = {
      if (isError) {
        Icon(painter = painterResource(R.drawable.error_24px), contentDescription = "Error")
      }
    },
    readOnly = isReadOnly,
    enabled = !isReadOnly,
    keyboardOptions = keyboardOptions,
    keyboardActions =
      KeyboardActions(
        onNext = { focusManager.moveFocus(FocusDirection.Down) },
      ),
  )
}

@Composable
fun UnitText(modifier: Modifier, unitString: String) {
  Box(
    modifier = modifier.padding(horizontal = dimensionResource(R.dimen.item_margin_horizontal)),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      unitString,
      style = MaterialTheme.typography.bodyMedium,
      modifier = Modifier.testTag(UNIT_TEXT_TEST_TAG),
    )
  }
}

@Composable
@Preview(showSystemUi = true)
fun PreviewQuestionnaireItemEditText() {
  EditTextFieldQuestionnaireItem(
    inputText = "Input",
    onInputTextChange = {},
    hint = null,
    helperText = null,
    isError = false,
    isReadOnly = false,
    isMultiLine = false,
    unitText = "",
    keyboardOptions = KeyboardOptions(),
  )
}

@OptIn(FlowPreview::class)
data class QuestionnaireTextFieldState(
  val hint: AnnotatedString?,
  val helperText: String?,
  val isError: Boolean,
  val unitText: String?,
  val isReadOnly: Boolean,
  val keyboardOptions: KeyboardOptions,
  val isMultiLine: Boolean,
  private val initialInputText: String,
  private val handleTextInputChange: suspend (String) -> Unit,
  private val coroutineScope: CoroutineScope,
) {
  var inputText by mutableStateOf(initialInputText)
    private set

  init {
    coroutineScope.launch {
      snapshotFlow { inputText }
        .onEach { text -> println("FIRST: $text") }
        .drop(1) // Drops the initial value emitted by snapshotFlow
        .onEach { text -> println("After drop => $text") }
        .debounce(500.milliseconds)
        .onEach { text -> println("After debounce => $text") }
        .collectLatest { handleTextInputChange(it) }
    }
  }

  fun onInputTextChange(text: String) {
    inputText = text
  }
}

const val EDIT_TEXT_FIELD_TEST_TAG = "text_input_edit_text"
const val UNIT_TEXT_TEST_TAG = "unit_text_view"
