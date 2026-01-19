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

package com.google.android.fhir.datacapture.views.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import com.google.android.fhir.datacapture.extensions.toAnnotatedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DropDownItem(
  modifier: Modifier,
  enabled: Boolean,
  labelText: AnnotatedString? = null,
  supportingText: String? = null,
  isError: Boolean = false,
  selectedOption: DropDownAnswerOption? = null,
  options: List<DropDownAnswerOption>,
  onDropDownAnswerOptionSelected: (DropDownAnswerOption?) -> Unit,
) {
  var expanded by remember { mutableStateOf(false) }
  var selectedDropDownAnswerOption by
    remember(selectedOption, options) { mutableStateOf(selectedOption) }
  val selectedOptionDisplay by
    remember(selectedDropDownAnswerOption) {
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
      modifier =
        Modifier.fillMaxWidth()
          .testTag(DROP_DOWN_TEXT_FIELD_TAG)
          .semantics { if (isError) error(supportingText ?: "") }
          .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled),
      readOnly = true,
      enabled = enabled,
      minLines = 1,
      isError = isError,
      label = { labelText?.let { Text(it) } },
      supportingText = { supportingText?.let { Text(it) } },
      leadingIcon =
        selectedDropDownAnswerOption?.answerOptionImage?.let {
          {
            Icon(
              bitmap = it,
              contentDescription = selectedOptionDisplay,
              modifier = Modifier.testTag(DROP_DOWN_TEXT_FIELD_LEADING_ICON_TAG),
            )
          }
        },
      trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
    )
    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      options.forEach { option ->
        DropDownAnswerMenuItem(enabled, option) {
          selectedDropDownAnswerOption = option
          expanded = false
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DropDownAnswerMenuItem(
  enabled: Boolean,
  answerOption: DropDownAnswerOption,
  onSelected: () -> Unit,
) {
  DropdownMenuItem(
    modifier = Modifier.testTag(DROP_DOWN_ANSWER_MENU_ITEM_TAG),
    text = {
      Text(answerOption.answerOptionAnnotatedString(), style = MaterialTheme.typography.bodyLarge)
    },
    leadingIcon =
      answerOption.answerOptionImage?.let {
        {
          Icon(
            bitmap = it,
            contentDescription = answerOption.answerOptionString,
          )
        }
      },
    enabled = enabled,
    onClick = { onSelected() },
    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
  )
}

internal data class DropDownAnswerOption(
  val answerId: String,
  val answerOptionString: String,
  val answerOptionImage: ImageBitmap? = null,
) {
  override fun toString(): String = this.answerOptionString

  fun answerOptionAnnotatedString() = answerOptionString.toAnnotatedString()
}

const val DROP_DOWN_TEXT_FIELD_TAG = "drop_down_text_field"
const val DROP_DOWN_TEXT_FIELD_LEADING_ICON_TAG = "drop_down_text_field_leading_icon"
const val DROP_DOWN_ANSWER_MENU_ITEM_TAG = "drop_down_answer_list_menu_item"
