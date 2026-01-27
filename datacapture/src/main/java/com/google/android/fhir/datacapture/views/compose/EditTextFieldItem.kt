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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.fhir.datacapture.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

@Composable
internal fun EditTextFieldItem(modifier: Modifier, textFieldState: EditTextFieldState) {
  OutlinedEditTextFieldItem(
    modifier = modifier,
    inputText = textFieldState.inputText,
    onInputTextChange = textFieldState::onInputTextChange,
    hint = textFieldState.hint,
    helperText = textFieldState.helperText,
    isError = textFieldState.isError,
    isReadOnly = textFieldState.isReadOnly,
    keyboardOptions = textFieldState.keyboardOptions,
    isMultiLine = textFieldState.isMultiLine,
  )
}

@Composable
internal fun OutlinedEditTextFieldItem(
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
  var textFieldValue by remember {
    mutableStateOf(TextFieldValue(text = inputText, selection = TextRange(inputText.length)))
  }
  var isFocused by remember { mutableStateOf(false) }

  // Update the local state when the initial inputText changes and the field is not focused
  LaunchedEffect(inputText) {
    if (!isFocused && textFieldValue.text != inputText) {
      textFieldValue =
        textFieldValue.copy(text = inputText, selection = TextRange(inputText.length))
    }
  }

  OutlinedTextField(
    value = textFieldValue,
    onValueChange = {
      textFieldValue = it
      onInputTextChange(it.text)
    },
    minLines = if (isMultiLine) 3 else 1,
    singleLine = !isMultiLine,
    modifier =
      modifier
        .onFocusChanged {
          isFocused = it.isFocused
          if (!it.isFocused) {
            keyboardController?.hide()
            // Sync with external state on focus loss
            if (textFieldValue.text != inputText) {
              onInputTextChange(textFieldValue.text)
            }
          }
        }
        .testTag(EDIT_TEXT_FIELD_TEST_TAG),
    label = { hint?.let { Text(it) } },
    supportingText = { helperText?.let { Text(it) } },
    isError = isError,
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
internal fun UnitText(unitString: String) {
  Box(
    modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.item_margin_horizontal)),
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
internal fun PreviewQuestionnaireItemEditText() {
  OutlinedEditTextFieldItem(
    inputText = "Input",
    onInputTextChange = {},
    hint = null,
    helperText = null,
    isError = false,
    isReadOnly = false,
    isMultiLine = false,
    modifier = Modifier,
    keyboardOptions = KeyboardOptions(),
  )
}

@OptIn(FlowPreview::class)
internal data class EditTextFieldState(
  val hint: AnnotatedString?,
  val helperText: String?,
  val isError: Boolean,
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
        .drop(1) // Drops the initial value emitted by snapshotFlow
        .debounce(HANDLE_INPUT_DEBOUNCE_TIME)
        .collectLatest { handleTextInputChange(it) }
    }
  }

  fun onInputTextChange(text: String) {
    inputText = text
  }
}

const val EDIT_TEXT_FIELD_TEST_TAG = "text_input_edit_text"
const val UNIT_TEXT_TEST_TAG = "unit_text_view"
const val HANDLE_INPUT_DEBOUNCE_TIME = 500L
