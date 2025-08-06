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
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import com.google.android.fhir.datacapture.R

@Composable
fun QuestionnaireItemEditText(questionnaireTextFieldState: QuestionnaireTextFieldState) {
  Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
    OutlinedQuestionnaireItemTextField(
      modifier = Modifier.weight(1f),
      inputTextState = questionnaireTextFieldState.inputText,
      onInputChange = questionnaireTextFieldState.onInputValueChange,
      hint = questionnaireTextFieldState.hint,
      helperText = questionnaireTextFieldState.helperText,
      isError = questionnaireTextFieldState.isError,
      isReadOnly = questionnaireTextFieldState.isReadOnly,
      keyboardOptions = questionnaireTextFieldState.keyboardOptions,
      isMultiLine = questionnaireTextFieldState.isMultiLine,
    )
    if (!questionnaireTextFieldState.unitText.isNullOrEmpty()) {
      UnitText(modifier = Modifier, unitString = questionnaireTextFieldState.unitText)
    }
  }
}

@Composable
fun OutlinedQuestionnaireItemTextField(
  modifier: Modifier,
  inputTextState: State<String>,
  onInputChange: (String) -> Unit,
  hint: AnnotatedString?,
  helperText: String?,
  isError: Boolean,
  isReadOnly: Boolean,
  keyboardOptions: KeyboardOptions,
  isMultiLine: Boolean,
) {
  val focusManager = LocalFocusManager.current
  val keyboardController = LocalSoftwareKeyboardController.current
  val inputText by remember { inputTextState }

  OutlinedTextField(
    value = inputText,
    onValueChange = { onInputChange.invoke(it) },
    minLines = if (isMultiLine) 3 else 1,
    singleLine = !isMultiLine,
    modifier =
      modifier.onFocusChanged {
        if (!it.isFocused) {
          keyboardController?.hide()
        }
      },
    label = { hint?.let { Text(hint) } },
    supportingText = { helperText?.let { Text(helperText) } },
    isError = isError,
    colors = OutlinedTextFieldDefaults.colors(),
    trailingIcon = {
      if (isError) {
        Icon(painter = painterResource(R.drawable.error_24px), contentDescription = "Error")
      }
    },
    readOnly = isReadOnly,
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
    Text(unitString, style = MaterialTheme.typography.bodyMedium)
  }
}

data class QuestionnaireTextFieldState(
  val inputText: State<String>,
  val onInputValueChange: (String) -> Unit,
  val hint: AnnotatedString?,
  val helperText: String?,
  val isError: Boolean,
  val unitText: String?,
  val isReadOnly: Boolean,
  val keyboardOptions: KeyboardOptions,
  val isMultiLine: Boolean,
//    val onFocusChangedAction: (Boolean) -> Unit
)
