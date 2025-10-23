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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.views.factories.DropDownAnswerOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MultiAutoCompleteTextItem(
  modifier: Modifier,
  enabled: Boolean,
  labelText: AnnotatedString? = null,
  supportingText: String? = null,
  isError: Boolean = false,
  selectedOptions: List<DropDownAnswerOption> = emptyList(),
  options: List<DropDownAnswerOption>,
  onNewOptionSelected: (DropDownAnswerOption) -> Unit,
  onOptionDeselected: (DropDownAnswerOption) -> Unit,
) {
  var expanded by remember { mutableStateOf(false) }
  var autoCompleteText by remember(options) { mutableStateOf(TextFieldValue("")) }
  val filteredOptions =
    remember(options, autoCompleteText) {
      options.filter { it.answerOptionString.contains(autoCompleteText.text, true) }
    }

  ExposedDropdownMenuBox(
    modifier = modifier,
    expanded = expanded,
    onExpandedChange = { expanded = it },
  ) {
    OutlinedTextField(
      value = autoCompleteText,
      onValueChange = {
        autoCompleteText = it
        if (!expanded && autoCompleteText.text.isNotBlank()) expanded = true
      },
      modifier =
        Modifier.fillMaxWidth()
          .testTag(MULTI_AUTO_COMPLETE_TEXT_FIELD_TAG)
          .semantics { if (isError) error(supportingText ?: "") }
          .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, enabled),
      enabled = enabled,
      isError = isError,
      label = { labelText?.let { Text(it) } },
      supportingText = { supportingText?.let { Text(it) } },
    )

    if (filteredOptions.isNotEmpty()) {
      ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        filteredOptions.forEach { option ->
          DropDownAnswerMenuItem(enabled, option) {
            expanded = false
            autoCompleteText = TextFieldValue("") // Reset autoComplete text to empty
            onNewOptionSelected(option)
          }
        }
      }
    }

    FlowRow(
      modifier =
        Modifier.padding(dimensionResource(R.dimen.auto_complete_chip_margin))
          .padding(bottom = dimensionResource(R.dimen.auto_complete_chip_margin_bottom)),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      selectedOptions.forEach {
        InputChip(
          selected = false,
          modifier = Modifier.testTag(MULTI_AUTO_COMPLETE_INPUT_CHIP_TAG),
          enabled = enabled,
          onClick = { onOptionDeselected(it) },
          label = { Text(it.answerOptionAnnotatedString()) },
          trailingIcon = {
            Icon(painterResource(R.drawable.ic_clear), contentDescription = "Close")
          },
        )
      }
    }
  }
}

const val MULTI_AUTO_COMPLETE_TEXT_FIELD_TAG = "multi_auto_complete_text_field"
const val MULTI_AUTO_COMPLETE_INPUT_CHIP_TAG = "multi_auto_complete_input_chip"
