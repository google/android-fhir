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

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.toLocalizedString
import java.time.LocalTime

@Composable
internal fun TimePickerItem(
  modifier: Modifier = Modifier,
  timeSelectedDisplay: String?,
  initialStartTime: LocalTime,
  enabled: Boolean,
  hint: String,
  supportingHelperText: String?,
  isError: Boolean,
  onTimeChanged: (LocalTime) -> Unit,
) {
  val context = LocalContext.current
  val focusManager = LocalFocusManager.current
  val keyboardController = LocalSoftwareKeyboardController.current
  var selectedTimeTextDisplay by
    remember(timeSelectedDisplay) { mutableStateOf(timeSelectedDisplay ?: "") }
  var showTimePickerModal by remember { mutableStateOf(false) }
  var timePickerDialogType by remember { mutableStateOf<TimeInputMode>(TimeInputMode.CLOCK) }

  OutlinedTextField(
    value = selectedTimeTextDisplay,
    onValueChange = {},
    singleLine = true,
    label = { Text(hint) },
    modifier =
      modifier
        .testTag(TIME_PICKER_INPUT_FIELD)
        .onFocusChanged {
          if (!it.isFocused) {
            keyboardController?.hide()
          }
        }
        .semantics { if (isError) error(supportingHelperText ?: "") },
    supportingText = { supportingHelperText?.let { Text(it) } },
    isError = isError,
    trailingIcon = {
      IconButton(
        onClick = {
          timePickerDialogType = TimeInputMode.CLOCK
          showTimePickerModal = true
        },
        enabled = enabled,
      ) {
        Icon(
          painterResource(R.drawable.gm_schedule_24),
          contentDescription = stringResource(R.string.select_time),
        )
      }
    },
    readOnly = true,
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
    interactionSource =
      remember { MutableInteractionSource() }
        .also { interactionSource ->
          LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect {
              if (it is PressInteraction.Release) {
                timePickerDialogType = TimeInputMode.KEYBOARD
                showTimePickerModal = true
              }
            }
          }
        },
  )

  if (showTimePickerModal) {
    TimePickerDialog(
      type = timePickerDialogType,
      initialSelectedHour = initialStartTime.hour,
      initialSelectedMinute = initialStartTime.minute,
      onDismiss = { showTimePickerModal = false },
    ) { hour, min,
      ->
      val localTime = LocalTime.of(hour, min)
      selectedTimeTextDisplay = localTime.toLocalizedString(context)
      onTimeChanged(localTime)
    }
  }
}

@Composable
@Preview
fun PreviewTimePickerItem() {
  val context = LocalContext.current
  TimePickerItem(
    Modifier,
    null,
    LocalTime.now(),
    true,
    stringResource(R.string.time),
    null,
    false,
  ) {}
}

const val TIME_PICKER_INPUT_FIELD = "time_picker_text_field"
