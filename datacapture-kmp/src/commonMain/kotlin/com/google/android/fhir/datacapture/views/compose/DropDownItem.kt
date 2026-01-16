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
import android_fhir.datacapture_kmp.generated.resources.ic_clear
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.google.android.fhir.datacapture.extensions.displayString
import com.google.android.fhir.datacapture.extensions.elementValue
import com.google.android.fhir.datacapture.extensions.itemAnswerOptionImage
import com.google.android.fhir.datacapture.extensions.toAnnotatedString
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.fhir.model.r4.Element
import com.google.fhir.model.r4.Questionnaire
import org.jetbrains.compose.resources.painterResource

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
      derivedStateOf { selectedDropDownAnswerOption?.displayString ?: "" }
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
        selectedDropDownAnswerOption?.iconImage?.let {
          {
            Icon(
              it,
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
      Text(
        answerOption.displayAnnotatedString(),
        style = QuestionnaireTheme.textStyles.dropDownText,
      )
    },
    leadingIcon =
      answerOption.iconImage?.let {
        {
          Icon(
            it,
            contentDescription = answerOption.displayString,
          )
        }
      },
    enabled = enabled,
    onClick = { onSelected() },
    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AutoCompleteDropDownItem(
  modifier: Modifier,
  enabled: Boolean,
  labelText: AnnotatedString? = null,
  supportingText: String? = null,
  isError: Boolean = false,
  showClearIcon: Boolean = false,
  readOnly: Boolean = showClearIcon,
  selectedOption: DropDownAnswerOption? = null,
  options: List<DropDownAnswerOption>,
  onDropDownAnswerOptionSelected: (DropDownAnswerOption?) -> Unit,
) {
  val focusManager = LocalFocusManager.current
  var expanded by remember { mutableStateOf(false) }
  var selectedDropDownAnswerOption by
    remember(selectedOption, options) { mutableStateOf(selectedOption) }

  // A flag to track when the user is in the process of filtering the options.
  var inFilterMode: Boolean by remember(options) { mutableStateOf(false) }

  var selectedOptionDisplay by
    remember(selectedDropDownAnswerOption) {
      // When an option is selected, the filter is no longer active.
      inFilterMode = false
      val stringValue = selectedDropDownAnswerOption?.displayString ?: ""
      mutableStateOf(TextFieldValue(stringValue, selection = TextRange(stringValue.length)))
    }

  // The options are filtered only when the user is in filter mode.
  val filteredOptions =
    remember(options, selectedOptionDisplay, inFilterMode) {
      if (inFilterMode) {
        options.filter { it.displayString.contains(selectedOptionDisplay.text, true) }
      } else {
        // When not in filter mode, all options are displayed.
        options
      }
    }

  LaunchedEffect(selectedDropDownAnswerOption) {
    if (selectedDropDownAnswerOption != null) {
      focusManager.clearFocus()
    }

    if (selectedDropDownAnswerOption != selectedOption) {
      onDropDownAnswerOptionSelected(selectedDropDownAnswerOption)
    }
  }

  ExposedDropdownMenuBox(
    modifier = modifier,
    expanded = expanded,
    onExpandedChange = { expanded = it },
  ) {
    OutlinedTextField(
      value = selectedOptionDisplay,
      onValueChange = {
        inFilterMode = true
        selectedOptionDisplay = it
        if (!expanded) expanded = true
      },
      modifier =
        Modifier.fillMaxWidth()
          .testTag(DROP_DOWN_TEXT_FIELD_TAG)
          .semantics { if (isError) error(supportingText ?: "") }
          .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, enabled),
      readOnly = readOnly,
      enabled = enabled,
      minLines = 1,
      isError = isError,
      label = { labelText?.let { Text(it) } },
      supportingText = { supportingText?.let { Text(it) } },
      leadingIcon =
        selectedDropDownAnswerOption?.iconImage?.let {
          {
            Icon(
              it,
              contentDescription = selectedDropDownAnswerOption!!.displayString,
              modifier = Modifier.testTag(DROP_DOWN_TEXT_FIELD_LEADING_ICON_TAG),
            )
          }
        },
      trailingIcon = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          if (showClearIcon) {
            IconButton(
              onClick = { selectedDropDownAnswerOption = null },
              modifier = Modifier.testTag(CLEAR_TEXT_ICON_BUTTON_TAG),
            ) {
              Icon(painterResource(Res.drawable.ic_clear), contentDescription = "clear")
            }
          }
          ExposedDropdownMenuDefaults.TrailingIcon(
            expanded = expanded,
            modifier =
              Modifier.menuAnchor(
                ExposedDropdownMenuAnchorType.SecondaryEditable,
                enabled,
              ),
          )
        }
      },
    )

    if (filteredOptions.isNotEmpty()) {
      ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        filteredOptions.forEach { option ->
          DropDownAnswerMenuItem(enabled, option) {
            selectedDropDownAnswerOption = option
            expanded = false
          }
        }
      }
    }
  }
}

internal data class DropDownAnswerOption(
  val elementValue: Element,
  val displayString: String,
  val iconImage: ImageBitmap? = null,
) {
  override fun toString(): String = this.displayString

  fun displayAnnotatedString() = displayString.toAnnotatedString()

  companion object {
    fun of(answerOption: Questionnaire.Item.AnswerOption): DropDownAnswerOption =
      DropDownAnswerOption(
        answerOption.elementValue,
        answerOption.value.displayString(),
        answerOption.itemAnswerOptionImage(),
      )
  }
}

const val CLEAR_TEXT_ICON_BUTTON_TAG = "clear_field_text"
const val DROP_DOWN_TEXT_FIELD_TAG = "drop_down_text_field"
const val DROP_DOWN_TEXT_FIELD_LEADING_ICON_TAG = "drop_down_text_field_leading_icon"
const val DROP_DOWN_ANSWER_MENU_ITEM_TAG = "drop_down_answer_list_menu_item"
