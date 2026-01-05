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
import android_fhir.datacapture_kmp.generated.resources.gm_schedule_24
import android_fhir.datacapture_kmp.generated.resources.select_time
import android_fhir.datacapture_kmp.generated.resources.time
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.google.android.fhir.datacapture.extensions.toLocalizedString
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TimeFieldItem(
  modifier: Modifier = Modifier,
  timeSelectedDisplay: String?,
  initialStartTime: LocalTime,
  enabled: Boolean,
  hint: String,
  supportingHelperText: String?,
  isError: Boolean,
  onTimeChanged: (LocalTime) -> Unit,
) {
  val focusManager = LocalFocusManager.current
  val keyboardController = LocalSoftwareKeyboardController.current
  var selectedTimeTextDisplay by
    remember(timeSelectedDisplay) { mutableStateOf(timeSelectedDisplay ?: "") }
  var timePickerDialogType by remember { mutableStateOf<TimeInputMode>(TimeInputMode.CLOCK) }
  var expanded by remember { mutableStateOf(false) }

  ExposedDropdownMenuBox(
    modifier = modifier,
    expanded = expanded,
    onExpandedChange = {
      if (it) {
        timePickerDialogType = TimeInputMode.KEYBOARD
      }
      expanded = it
    },
  ) {
    OutlinedTextField(
      value = selectedTimeTextDisplay,
      onValueChange = {},
      singleLine = true,
      label = { Text(hint) },
      modifier =
        Modifier.fillMaxWidth()
          .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled)
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
            expanded = true
          },
          enabled = enabled,
        ) {
          Icon(
            painterResource(Res.drawable.gm_schedule_24),
            contentDescription = stringResource(Res.string.select_time),
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
    )

    if (expanded) {
      TimePickerDialog(
        type = timePickerDialogType,
        initialSelectedHour = initialStartTime.hour,
        initialSelectedMinute = initialStartTime.minute,
        onDismiss = { expanded = false },
      ) { hour, min,
        ->
        val localTime = LocalTime(hour, min)
        selectedTimeTextDisplay = localTime.toLocalizedString()
        onTimeChanged(localTime)
      }
    }
  }
}

@Composable
@Preview
fun PreviewTimePickerItem() {
  TimeFieldItem(
    Modifier,
    null,
    LocalTime(11, 38),
    true,
    stringResource(Res.string.time),
    null,
    false,
  ) {}
}

const val TIME_PICKER_INPUT_FIELD = "time_picker_text_field"
