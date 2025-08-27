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

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.google.android.fhir.datacapture.views.factories.DropDownAnswerOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ExposedDropDownMenuBoxItem(
  modifier: Modifier,
  enabled: Boolean,
  selectedOption: DropDownAnswerOption? = null,
  options: List<DropDownAnswerOption>,
  onDropDownAnswerOptionSelected: (DropDownAnswerOption?) -> Unit,
) {
  var expanded by remember { mutableStateOf(false) }
  var selectedDropDownAnswerOption by
    remember(selectedOption, options) { mutableStateOf(selectedOption) }
  val selectedOptionDisplay by remember {
    derivedStateOf { selectedDropDownAnswerOption?.answerOptionString ?: "" }
  }

  LaunchedEffect(selectedDropDownAnswerOption) {
    onDropDownAnswerOptionSelected(selectedDropDownAnswerOption)
  }

  ExposedDropdownMenuBox(
    modifier = modifier,
    expanded = expanded,
    onExpandedChange = { expanded = it },
  ) {
    OutlinedTextField(
      value = selectedOptionDisplay,
      onValueChange = {},
      modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled),
      readOnly = true,
      enabled = enabled,
      minLines = 1,
      label = {},
      supportingText = {},
      trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
    )
    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      options.forEach { option ->
        DropdownMenuItem(
          text = {
            Text(option.answerOptionAnnotatedString(), style = MaterialTheme.typography.bodyLarge)
          },
          leadingIcon = {
            option.answerOptionImage?.let {
              Icon(
                it.toBitmap().asImageBitmap(),
                contentDescription = option.answerOptionString,
              )
            }
          },
          enabled = enabled,
          onClick = {
            selectedDropDownAnswerOption = option
            expanded = false
          },
          contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
        )
      }
    }
  }
}
