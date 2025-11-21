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

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.text.input.then
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.format
import com.google.android.fhir.datacapture.extensions.toLocalDate
import java.time.LocalDate
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DatePickerItem(
  modifier: Modifier = Modifier,
  initialSelectedDateMillis: Long?,
  dateInput: DateInput,
  labelText: String,
  helperText: String?,
  isError: Boolean,
  enabled: Boolean,
  dateInputFormat: DateInputFormat,
  selectableDates: SelectableDates?,
  parseStringToLocalDate: (String, DateFormatPattern) -> LocalDate?,
  onDateInputEntry: (DateInput) -> Unit,
) {
  val keyboardController = LocalSoftwareKeyboardController.current
  var dateInputState by remember(dateInput) { mutableStateOf(dateInput) }
  val textFieldState = rememberTextFieldState(dateInputState.display)
  var isFocused by remember { mutableStateOf(false) }

  val firstDelimiterIndex =
    remember(dateInputFormat) {
      dateInputFormat.patternWithDelimiters.indexOf(dateInputFormat.delimiter).takeIf { it >= 0 }
    }
  val secondDelimiterIndex =
    remember(dateInputFormat) {
      dateInputFormat.patternWithDelimiters.lastIndexOf(dateInputFormat.delimiter).takeIf {
        it >= 0
      }
    }
  val dateFormatLength =
    remember(dateInputFormat) { dateInputFormat.patternWithoutDelimiters.length }

  var showDatePickerModal by remember { mutableStateOf(false) }

  // Sync external dateInput changes to textFieldState
  LaunchedEffect(dateInput) {
    if (!isFocused && dateInput.display != textFieldState.text.toString()) {
      textFieldState.setTextAndPlaceCursorAtEnd(dateInput.display)
    }
  }

  // Monitor textFieldState changes and update dateInputState
  LaunchedEffect(textFieldState) {
    snapshotFlow { textFieldState.text.toString() }
      .collectLatest {
        val trimmedText = it.trim()
        val localDate =
          if (trimmedText.isNotBlank() && trimmedText.length == dateFormatLength) {
            parseStringToLocalDate(trimmedText, dateInputFormat.patternWithoutDelimiters)
          } else {
            null
          }
        val newDateInput = DateInput(trimmedText, localDate)
        if (dateInputState != newDateInput) {
          dateInputState = newDateInput
          onDateInputEntry(newDateInput)
        }
      }
  }

  OutlinedTextField(
    state = textFieldState,
    lineLimits = TextFieldLineLimits.SingleLine,
    label = { Text(labelText) },
    modifier =
      modifier
        .testTag(DATE_TEXT_INPUT_FIELD)
        .onFocusChanged {
          isFocused = it.isFocused
          if (!it.isFocused) {
            keyboardController?.hide()
            //              Sync external dateInput changes to textFieldState
            if (dateInput.display != textFieldState.text.toString()) {
              textFieldState.setTextAndPlaceCursorAtEnd(dateInput.display)
            }
          }
        }
        .semantics { if (isError) error(helperText ?: "") },
    supportingText = { helperText?.let { Text(it) } },
    isError = isError,
    trailingIcon = {
      IconButton(onClick = { showDatePickerModal = true }, enabled = enabled) {
        Icon(
          painterResource(R.drawable.gm_calendar_today_24),
          contentDescription = stringResource(R.string.select_date),
        )
      }
    },
    enabled = enabled,
    inputTransformation =
      InputTransformation.maxLength(dateFormatLength).then {
        if (asCharSequence().any { !Character.isDigit(it) }) revertAllChanges()
      },
    keyboardOptions =
      KeyboardOptions(
        autoCorrectEnabled = false,
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Done,
      ),
    outputTransformation =
      OutputTransformation {
        firstDelimiterIndex?.let {
          if (length >= firstDelimiterIndex) {
            insert(firstDelimiterIndex, dateInputFormat.delimiter.toString())
          }
        }
        secondDelimiterIndex?.let {
          if (length >= secondDelimiterIndex) {
            insert(secondDelimiterIndex, dateInputFormat.delimiter.toString())
          }
        }
      },
  )

  if (selectableDates != null && showDatePickerModal) {
    DatePickerModal(
      initialSelectedDateMillis,
      selectableDates,
      onDateSelected = { dateMillis ->
        dateMillis?.toLocalDate()?.let {
          val formattedDate = it.format(dateInputFormat.patternWithoutDelimiters)
          textFieldState.setTextAndPlaceCursorAtEnd(formattedDate)
        }
      },
    ) {
      showDatePickerModal = false
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DatePickerModal(
  initialSelectedDateMillis: Long?,
  selectableDates: SelectableDates,
  onDateSelected: (Long?) -> Unit,
  onDismiss: () -> Unit,
) {
  val datePickerState =
    rememberDatePickerState(initialSelectedDateMillis, selectableDates = selectableDates)
  val datePickerSelectedDateMillis =
    remember(initialSelectedDateMillis) { initialSelectedDateMillis }
  val confirmEnabled by remember { derivedStateOf { datePickerState.selectedDateMillis != null } }

  LaunchedEffect(datePickerSelectedDateMillis) {
    if (datePickerSelectedDateMillis != datePickerState.selectedDateMillis) {
      datePickerState.selectedDateMillis = datePickerSelectedDateMillis
    }
  }

  DatePickerDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      TextButton(
        onClick = {
          onDateSelected(datePickerState.selectedDateMillis)
          onDismiss()
        },
        enabled = confirmEnabled,
      ) {
        Text("OK")
      }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
  ) {
    DatePicker(state = datePickerState)
  }
}

typealias DateFormatPattern = String

data class DateInput(val display: String, val value: LocalDate?)

data class DateInputFormat(val patternWithDelimiters: String, val delimiter: Char) {
  val patternWithoutDelimiters: String = patternWithDelimiters.replace(delimiter.toString(), "")
}

const val DATE_TEXT_INPUT_FIELD = "date_picker_text_field"
