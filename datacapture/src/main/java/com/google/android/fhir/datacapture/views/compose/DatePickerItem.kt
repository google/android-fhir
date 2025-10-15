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

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
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
  selectableDates: () -> SelectableDates,
  parseStringToLocalDate: (String, DateFormatPattern) -> LocalDate?,
  onDateInputEntry: (DateInput) -> Unit,
) {
  val focusManager = LocalFocusManager.current
  val keyboardController = LocalSoftwareKeyboardController.current
  var dateInputState by remember(dateInput) { mutableStateOf(dateInput) }
  val dateInputDisplay by remember(dateInputState) { derivedStateOf { dateInputState.display } }

  var showDatePickerModal by remember { mutableStateOf(false) }

  LaunchedEffect(dateInputState) {
    if (dateInputState != dateInput) {
      onDateInputEntry(dateInputState)
    }
  }

  OutlinedTextField(
    value = dateInputDisplay,
    onValueChange = {
      if (
        it.length <= dateInputFormat.patternWithoutDelimiters.length &&
          it.all { char -> char.isDigit() }
      ) {
        val trimmedText = it.trim()
        val localDate =
          if (
            trimmedText.isNotBlank() &&
              trimmedText.length == dateInputFormat.patternWithoutDelimiters.length
          ) {
            parseStringToLocalDate(trimmedText, dateInputFormat.patternWithoutDelimiters)
          } else {
            null
          }

        dateInputState = DateInput(it, localDate)
      }
    },
    singleLine = true,
    label = { Text(labelText) },
    modifier =
      modifier
        .testTag(DATE_TEXT_INPUT_FIELD)
        .onFocusChanged {
          if (!it.isFocused) {
            keyboardController?.hide()
          }
        }
        .semantics { if (isError && !helperText.isNullOrBlank()) error(helperText) },
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
    keyboardOptions =
      KeyboardOptions(
        autoCorrectEnabled = false,
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Done,
      ),
    keyboardActions =
      KeyboardActions(
        onNext = { focusManager.moveFocus(FocusDirection.Down) },
      ),
    visualTransformation = DateVisualTransformation(dateInputFormat),
  )

  if (showDatePickerModal) {
    DatePickerModal(
      initialSelectedDateMillis,
      selectableDates,
      onDateSelected = { dateMillis ->
        dateMillis?.toLocalDate()?.let {
          dateInputState =
            DateInput(
              display = it.format(dateInputFormat.patternWithoutDelimiters),
              value = it,
            )
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
  selectableDates: () -> SelectableDates,
  onDateSelected: (Long?) -> Unit,
  onDismiss: () -> Unit,
) {
  val datePickerState =
    rememberDatePickerState(initialSelectedDateMillis, selectableDates = selectableDates())
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

const val DATE_TEXT_INPUT_FIELD = "date_picker_text_field"
