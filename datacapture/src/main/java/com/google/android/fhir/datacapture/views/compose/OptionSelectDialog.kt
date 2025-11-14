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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.itemAnswerOptionImage
import com.google.android.fhir.datacapture.extensions.optionExclusive
import com.google.android.fhir.datacapture.extensions.toAnnotatedString
import com.google.android.fhir.datacapture.views.factories.SelectedOptions

@Composable
internal fun OptionSelectDialog(
  title: CharSequence,
  multiSelect: Boolean,
  otherOptionsAllowed: Boolean,
  initialSelectedOptions: SelectedOptions,
  onDismiss: () -> Unit,
  onConfirm: (SelectedOptions) -> Unit,
) {
  var optionRows by remember {
    mutableStateOf(
      initialSelectedOptions.toOptionRows(
        multiSelect = multiSelect,
        otherOptionsAllowed = otherOptionsAllowed,
      ),
    )
  }

  val listState = rememberLazyListState()

  // Auto-scroll when "Other" options are shown
  LaunchedEffect(optionRows) {
    val hasOtherEditText = optionRows.any { it is OptionSelectRow.OtherEditText }
    if (hasOtherEditText && optionRows.size > 1) {
      listState.animateScrollToItem(optionRows.size - 1)
    }
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(28.dp),
      tonalElevation = 6.dp,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(
        modifier = Modifier.padding(dimensionResource(R.dimen.dialog_padding)),
      ) {
        // Title
        Text(
          text = title.toString(),
          style = MaterialTheme.typography.headlineSmall,
          modifier =
            Modifier.padding(bottom = dimensionResource(R.dimen.dialog_title_margin_bottom)),
        )

        // Options List
        LazyColumn(
          state = listState,
          modifier =
            Modifier.weight(1f, fill = false)
              .padding(top = dimensionResource(R.dimen.dialog_option_scroll_margin_top)),
        ) {
          items(items = optionRows, key = { it.key() }) { row ->
            OptionSelectRowItem(
              row = row,
              multiSelect = multiSelect,
              onSelectionChange = { newRow ->
                optionRows =
                  updateSelection(
                    currentRows = optionRows,
                    oldRow = row,
                    newRow = newRow,
                    multiSelect = multiSelect,
                  )
              },
              onDelete = { optionRows = optionRows.filterNot { it == row } },
              onTextChange = { newText ->
                optionRows =
                  optionRows.map {
                    if (it == row && it is OptionSelectRow.OtherEditText) {
                      it.copy(currentText = newText)
                    } else {
                      it
                    }
                  }
              },
              onAddAnother = {
                val insertIndex = optionRows.indexOf(row)
                optionRows =
                  optionRows.toMutableList().apply {
                    add(insertIndex, OptionSelectRow.OtherEditText.fromText(""))
                  }
              },
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
        ) {
          TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.cancel)) }
          Spacer(modifier = Modifier.width(8.dp))
          TextButton(
            onClick = {
              onConfirm(optionRows.toSelectedOptions())
              onDismiss()
            },
          ) {
            Text(stringResource(R.string.save))
          }
        }
      }
    }
  }
}

@Composable
private fun OptionSelectRowItem(
  row: OptionSelectRow,
  multiSelect: Boolean,
  onSelectionChange: (OptionSelectRow) -> Unit,
  onDelete: () -> Unit,
  onTextChange: (String) -> Unit,
  onAddAnother: () -> Unit,
) {
  val context = LocalContext.current

  when (row) {
    is OptionSelectRow.Option -> {
      val label = row.option.displayString.toAnnotatedString()
      val image = row.option.item.itemAnswerOptionImage(context)

      if (multiSelect) {
        ChoiceCheckbox(
          label = label,
          checked = row.option.selected,
          enabled = true,
          image = image,
          onCheckedChange = { checked ->
            onSelectionChange(row.copy(option = row.option.copy(selected = checked)))
          },
          modifier =
            Modifier.padding(vertical = dimensionResource(R.dimen.option_item_margin_vertical)),
        )
      } else {
        ChoiceRadioButton(
          label = label,
          selected = row.option.selected,
          enabled = true,
          image = image,
          onClick = { onSelectionChange(row.copy(option = row.option.copy(selected = true))) },
          modifier =
            Modifier.padding(vertical = dimensionResource(R.dimen.option_item_margin_vertical)),
        )
      }
    }
    is OptionSelectRow.OtherRow -> {
      val label = AnnotatedString(stringResource(R.string.open_choice_other))

      if (multiSelect) {
        ChoiceCheckbox(
          label = label,
          checked = row.selected,
          enabled = true,
          onCheckedChange = { checked -> onSelectionChange(row.copy(selected = checked)) },
          modifier =
            Modifier.padding(vertical = dimensionResource(R.dimen.option_item_margin_vertical)),
        )
      } else {
        ChoiceRadioButton(
          label = label,
          selected = row.selected,
          enabled = true,
          onClick = { onSelectionChange(row.copy(selected = true)) },
          modifier =
            Modifier.padding(vertical = dimensionResource(R.dimen.option_item_margin_vertical)),
        )
      }
    }
    is OptionSelectRow.OtherEditText -> {
      Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        OutlinedTextField(
          value = row.currentText,
          onValueChange = onTextChange,
          modifier = Modifier.weight(1f),
          placeholder = { Text(stringResource(R.string.open_choice_other_hint)) },
        )

        if (multiSelect) {
          IconButton(onClick = onDelete) {
            Icon(
              painterResource(R.drawable.ic_delete),
              contentDescription = stringResource(R.string.delete),
            )
          }
        }
      }
    }
    OptionSelectRow.OtherAddAnother -> {
      TextButton(onClick = onAddAnother, modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(R.string.open_choice_other_add_another))
      }
    }
  }
}

/**
 * Updates the selection state when a row is changed.
 *
 * Handles single-select deselection, exclusive options, and other state management.
 */
private fun updateSelection(
  currentRows: List<OptionSelectRow>,
  oldRow: OptionSelectRow,
  newRow: OptionSelectRow,
  multiSelect: Boolean,
): List<OptionSelectRow> {
  val rows = currentRows.toMutableList()
  val index = rows.indexOf(oldRow)

  if (index < 0) return currentRows

  rows[index] = newRow

  val selectedItem = newRow

  // Apply selection logic
  rows.forEachIndexed { i, row ->
    if (i != index) {
      // This is some other row
      if (multiSelect) {
        // In multi-select mode,
        if (
          selectedItem is OptionSelectRow.Option &&
            selectedItem.option.selected &&
            ((selectedItem.option.item.optionExclusive) ||
              (row is OptionSelectRow.Option && row.option.item.optionExclusive))
        ) {
          // if the selected answer option has optionExclusive extension, then deselect other
          // answer options.
          // or if the selected answer option does not have optionExclusive extension, then
          // deselect optionExclusive answer option.
          rows[i] = row.withSelectedState(selected = false) ?: row
        }
      } else {
        // In single-select mode, we need to disable all of the other rows
        if (selectedItem.isSelected()) {
          rows[i] = row.withSelectedState(selected = false) ?: row
        }
      }
    }
  }

  return rows.sanitizeOtherOptionRows(multiSelectEnabled = multiSelect)
}

private fun OptionSelectRow.withSelectedState(selected: Boolean): OptionSelectRow? =
  when (this) {
    is OptionSelectRow.Option -> copy(option = option.copy(selected = selected))
    is OptionSelectRow.OtherRow -> copy(selected = selected)
    OptionSelectRow.OtherAddAnother,
    is OptionSelectRow.OtherEditText, -> null
  }

private fun OptionSelectRow.isSelected(): Boolean =
  when (this) {
    is OptionSelectRow.Option -> option.selected
    is OptionSelectRow.OtherRow -> selected
    else -> false
  }
