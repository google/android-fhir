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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.fhir.datacapture.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
  type: TimeInputMode,
  initialSelectedHour: Int = 0,
  initialSelectedMinute: Int = 0,
  onDismiss: () -> Unit,
  onConfirm: (Int, Int) -> Unit,
) {
  val timePickerState =
    rememberTimePickerState(
      initialHour = initialSelectedHour,
      initialMinute = initialSelectedMinute,
    )
  var inputType by remember(type) { mutableStateOf(type) }

  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      TextButton(
        onClick = {
          onConfirm(timePickerState.hour, timePickerState.minute)
          onDismiss()
        },
      ) {
        Text("OK")
      }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    text = {
      Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        if (inputType == TimeInputMode.CLOCK) {
          TimePicker(state = timePickerState)
        } else {
          TimeInput(state = timePickerState)
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Start,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          IconButton(
            onClick = {
              inputType =
                if (inputType == TimeInputMode.CLOCK) {
                  TimeInputMode.KEYBOARD
                } else {
                  TimeInputMode.CLOCK
                }
            },
          ) {
            val iconRes =
              if (inputType == TimeInputMode.CLOCK) {
                R.drawable.ic_keyboard
              } else {
                R.drawable.ic_access_time
              }
            Icon(
              painterResource(iconRes),
              contentDescription =
                if (inputType == TimeInputMode.CLOCK) {
                  "Switch to text input"
                } else {
                  "Switch to clock input"
                },
            )
          }
        }
      }
    },
    title = { Text(stringResource(R.string.select_time)) },
  )
}

sealed interface TimeInputMode {
  object KEYBOARD : TimeInputMode

  object CLOCK : TimeInputMode
}

@Preview
@Composable
fun TimePickerDialogPreview() {
  TimePickerDialog(onDismiss = {}, type = TimeInputMode.KEYBOARD, onConfirm = { _, _ -> })
}
