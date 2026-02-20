/*
 * Copyright 2025-2026 Google LLC
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

import android_fhir.datacapture_kmp.generated.resources.Res
import android_fhir.datacapture_kmp.generated.resources.gm_calendar_today_24
import android_fhir.datacapture_kmp.generated.resources.select_date
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.google.android.fhir.datacapture.extensions.toLocalDate
import com.google.android.fhir.datacapture.getLocalDateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

internal const val DATE_TEXT_INPUT_FIELD = "date_picker_text_field"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DateFieldItem(
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
  val focusManager = LocalFocusManager.current
  val keyboardController = LocalSoftwareKeyboardController.current
  val coroutineScope = rememberCoroutineScope { Dispatchers.Main }
  val localDateTimeFormatter = getLocalDateTimeFormatter()

  var dateInputDisplay by
    remember(dateInput) {
      mutableStateOf(
        TextFieldValue(
          text = dateInput.display,
          selection = TextRange(dateInputFormat.pattern.length),
        ),
      )
    }

  var showDatePickerModal by remember { mutableStateOf(false) }
  var typingJob by remember { mutableStateOf<Job?>(null) }
  val postDelayedNewDateInput: (DateInput, Long) -> Unit =
    remember(dateInput) {
      { newDateInput, delayInMillis ->
        typingJob?.cancel() // Cancel previous debounce
        typingJob =
          coroutineScope.launch {
            delay(delayInMillis) // Debounce delay
            if (newDateInput != dateInput) {
              onDateInputEntry(newDateInput)
            }
          }
      }
    }

  OutlinedTextField(
    value = dateInputDisplay,
    onValueChange = { textFieldValue ->
      if (dateInputFormat.isTextValid(textFieldValue.text)) {
        textFieldValue.text.let {
          val isDeletion = it.length < dateInputDisplay.text.length
          val formattedText =
            if (!dateInputFormat.delimiterExistsInPattern || isDeletion) {
              it
            } else {
              StringBuilder(it)
                .apply {
                  dateInputFormat.delimiterIndex.forEach { index ->
                    if (this.length > index && get(index) != dateInputFormat.delimiter) {
                      insert(index, dateInputFormat.delimiter)
                    }
                  }
                }
                .toString()
            }
          val localDate =
            if (formattedText.length == dateInputFormat.pattern.length) {
              parseStringToLocalDate(formattedText, dateInputFormat.pattern)
            } else {
              null
            }
          dateInputDisplay =
            dateInputDisplay.copy(
              text = formattedText,
              selection = TextRange(dateInputFormat.pattern.length),
            )
          postDelayedNewDateInput(
            DateInput(formattedText, localDate),
            HANDLE_INPUT_DEBOUNCE_TIME,
          )
        }
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
        .semantics { if (isError) error(helperText ?: "") },
    supportingText = { helperText?.let { Text(it) } },
    isError = isError,
    trailingIcon = {
      IconButton(onClick = { showDatePickerModal = true }, enabled = enabled) {
        Icon(
          painterResource(Res.drawable.gm_calendar_today_24),
          contentDescription = stringResource(Res.string.select_date),
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
  )

  if (selectableDates != null && showDatePickerModal) {
    DatePickerModal(
      initialSelectedDateMillis,
      selectableDates,
      onDateSelected = { dateMillis ->
        dateMillis?.toLocalDate()?.let {
          val dateDisplay = localDateTimeFormatter.format(it, dateInputFormat.pattern)
          dateInputDisplay =
            dateInputDisplay.copy(
              text = dateDisplay,
              selection = TextRange(dateInputFormat.pattern.length),
            )
          val newDateInput =
            DateInput(
              display = dateDisplay,
              value = it,
            )
          postDelayedNewDateInput(newDateInput, 0L)
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

internal typealias DateFormatPattern = String

internal data class DateInput(val display: String, val value: LocalDate?)

internal data class DateInputFormat(val pattern: String, val delimiter: Char) {
  val delimiterIndex = pattern.indices.filter { pattern[it] == delimiter }
  val delimiterExistsInPattern = delimiterIndex.isNotEmpty()

  fun isTextValid(text: String): Boolean =
    (text.length <= pattern.length &&
        text
          .filterIndexed { index, ch ->
            ch.isDigit() || (ch == delimiter && index in delimiterIndex)
          }
          .isNotEmpty())
      .or(text.isEmpty())
}
