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

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.itemAnswerOptionImage
import com.google.android.fhir.datacapture.extensions.optionExclusive
import com.google.android.fhir.datacapture.extensions.toAnnotatedString
import com.google.android.fhir.datacapture.views.factories.OptionSelectOption
import com.google.android.fhir.datacapture.views.factories.SelectedOptions
import java.util.concurrent.atomic.AtomicInteger

@Composable
fun DialogSelect(
  context: Context,
  title: AnnotatedString,
  multiSelect: Boolean,
  otherOptionsAllowed:
    Boolean, // Client had to specify that they want an open-choice control to use "Other" options
  selectedOptions: SelectedOptions,
  onDismiss: () -> Unit,
  onConfirm: (SelectedOptions) -> Unit,
) {
  var choiceOptions by
    remember(multiSelect, selectedOptions.options) {
      mutableStateOf(selectedOptions.options.map { OptionSelectRow.Option(it) })
    }
  var otherOptionRowSelected by
    remember(otherOptionsAllowed, selectedOptions.otherOptions) {
      mutableStateOf(otherOptionsAllowed && selectedOptions.otherOptions.isNotEmpty())
    }
  val otherOptionEditTexts =
    remember(otherOptionsAllowed, selectedOptions.otherOptions) {
      val list = selectedOptions.otherOptions.map { OptionSelectRow.OtherEditText.fromText(it) }
      mutableStateListOf(*list.toTypedArray())
    }
  val showAddAnother =
    remember(otherOptionRowSelected, multiSelect) { otherOptionRowSelected && multiSelect }
  val listState = rememberLazyListState()
  remember(multiSelect, otherOptionsAllowed, selectedOptions) {
    val rows = selectedOptions.toOptionRows(multiSelect, otherOptionsAllowed).toTypedArray()
    mutableStateListOf(*rows)
  }

  LaunchedEffect(otherOptionRowSelected, otherOptionEditTexts.size) {
    if (otherOptionRowSelected) {
      val listSize =
        choiceOptions.size +
          if (otherOptionsAllowed) {
            1
          } else {
            0
          } +
          otherOptionEditTexts.size +
          if (showAddAnother) 1 else 0
      listState.animateScrollToItem(listSize - 1)
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
  ) {
    Surface(
      shape = MaterialTheme.shapes.extraLarge,
      tonalElevation = 6.dp,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(modifier = Modifier.padding(dimensionResource(R.dimen.dialog_padding))) {
        Text(
          text = title,
          style = MaterialTheme.typography.headlineSmall,
          modifier = Modifier.padding(dimensionResource(R.dimen.dialog_title_margin_bottom)),
        )

        LazyColumn(
          state = listState,
          modifier =
            Modifier.fillMaxWidth()
              .weight(1f, fill = false)
              .padding(top = dimensionResource(R.dimen.dialog_option_scroll_margin_top)),
          verticalArrangement =
            Arrangement.spacedBy(dimensionResource(R.dimen.option_item_margin_vertical)),
          horizontalAlignment = Alignment.CenterHorizontally,
          contentPadding =
            PaddingValues(horizontal = dimensionResource(R.dimen.option_item_margin_horizontal)),
        ) {
          itemsIndexed(
            choiceOptions,
            key = { _, row -> row.key() },
            contentType = { _, row -> row::class.simpleName },
          ) { index, optionSelectRow ->
            val label = optionSelectRow.option.displayString.toAnnotatedString()
            val image = optionSelectRow.option.item.itemAnswerOptionImage(context)
            OptionChoice(
              modifier = Modifier.fillMaxWidth(),
              isMultiSelect = multiSelect,
              label = label,
              selected = optionSelectRow.option.selected,
              image = image,
            ) { selected ->
              choiceOptions =
                choiceOptions.mapIndexed { ind, option ->
                  when {
                    ind == index || option == optionSelectRow -> {
                      optionSelectRow.copy(
                        option = optionSelectRow.option.copy(selected = selected),
                      )
                    }
                    selected &&
                      multiSelect &&
                      (optionSelectRow.option.item.optionExclusive ||
                        option.option.item.optionExclusive) -> {
                      // if the selected answer option has optionExclusive extension, then deselect
                      // other
                      // answer options.
                      // or if the selected answer option does not have optionExclusive extension,
                      // then
                      // deselect optionExclusive answer option.
                      option.copy(option = option.option.copy(selected = false))
                    }
                    !multiSelect -> {
                      // In single-select mode, we need to disable all of the other rows
                      option.copy(option = option.option.copy(selected = false))
                    }
                    else -> {
                      option
                    }
                  }
                }

              if (selected && (!multiSelect || optionSelectRow.option.item.optionExclusive)) {
                otherOptionRowSelected = false
                otherOptionEditTexts.clear()
              }
            }
          }

          if (otherOptionsAllowed) {
            item {
              val label = AnnotatedString(stringResource(R.string.open_choice_other))
              OptionChoice(
                modifier = Modifier.fillMaxWidth(),
                isMultiSelect = multiSelect,
                label = label,
                selected = otherOptionRowSelected,
              ) { selected ->
                otherOptionRowSelected = selected
                when {
                  selected && !multiSelect -> {
                    choiceOptions =
                      choiceOptions.map { it.copy(option = it.option.copy(selected = false)) }
                  }
                  selected -> {
                    choiceOptions =
                      choiceOptions.map {
                        if (it.option.item.optionExclusive) {
                          it.copy(option = it.option.copy(selected = false))
                        } else {
                          it
                        }
                      }
                  }
                }

                if (selected && otherOptionEditTexts.isEmpty()) {
                  otherOptionEditTexts.add(OptionSelectRow.OtherEditText.fromText(""))
                }
              }
            }
          }

          if (otherOptionRowSelected) {
            itemsIndexed(
              otherOptionEditTexts,
              key = { _, option -> option.key() },
              contentType = { _, _ -> OptionSelectRow.OtherEditText },
            ) { index, option ->
              Row(
                modifier =
                  Modifier.testTag(OTHER_OPTION_TEXT_FIELD_TAG)
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                OutlinedTextField(
                  value = option.currentText,
                  onValueChange = { newText ->
                    otherOptionEditTexts.removeAt(index)
                    otherOptionEditTexts.add(index, option.copy(currentText = newText))
                  },
                  modifier = Modifier.weight(1f),
                  placeholder = { Text(stringResource(R.string.open_choice_other_hint)) },
                )

                if (multiSelect) {
                  IconButton(
                    onClick = { otherOptionEditTexts.removeAt(index) },
                  ) {
                    Icon(
                      painterResource(R.drawable.delete_24px),
                      contentDescription = stringResource(R.string.delete),
                      tint = MaterialTheme.colorScheme.primary,
                    )
                  }
                }
              }
            }
          }

          if (showAddAnother) {
            item {
              Button(
                onClick = { otherOptionEditTexts.add(OptionSelectRow.OtherEditText.fromText("")) },
              ) {
                Text(stringResource(R.string.open_choice_other_add_another))
              }
            }
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          TextButton(
            modifier =
              Modifier.padding(dimensionResource(R.dimen.dialog_confirmation_button_padding)),
            onClick = onDismiss,
          ) {
            Text(
              stringResource(android.R.string.cancel),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
          TextButton(
            modifier =
              Modifier.padding(dimensionResource(R.dimen.dialog_confirmation_button_padding)),
            onClick = {
              onConfirm(
                SelectedOptions(
                  options = choiceOptions.map { it.option },
                  otherOptions =
                    otherOptionEditTexts.map { it.currentText }.filterNot { it.isBlank() },
                ),
              )
              onDismiss()
            },
          ) {
            Text(
              stringResource(R.string.save),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
        }
      }
    }
  }
}

@Composable
internal fun OptionChoice(
  modifier: Modifier,
  isMultiSelect: Boolean,
  label: AnnotatedString,
  selected: Boolean,
  image: Drawable? = null,
  onSelectionChange: (Boolean) -> Unit,
) {
  val testTagModifier = modifier.testTag(OPTION_CHOICE_TAG)
  if (isMultiSelect) {
    ChoiceCheckbox(
      label = label,
      checked = selected,
      enabled = true,
      image = image,
      onCheckedChange = { checked -> onSelectionChange(checked) },
      modifier = testTagModifier,
    )
  } else {
    ChoiceRadioButton(
      label = label,
      selected = selected,
      enabled = true,
      image = image,
      onClick = { onSelectionChange(!selected) },
      modifier = testTagModifier,
    )
  }
}

/** Sealed class representing different types of rows in the option select dialog. */
internal sealed class OptionSelectRow {
  /** A predefined option. */
  data class Option(val option: OptionSelectOption) : OptionSelectRow()

  /** "Other" option. Only shown if otherOptionsAllowed is true. */
  data class OtherRow(val selected: Boolean) : OptionSelectRow()

  /** Text boxes for user to enter "Other" options in. Only shown when [OtherRow] is selected. */
  data class OtherEditText(val id: Int, val startingText: String, var currentText: String) :
    OptionSelectRow() {
    companion object {
      fun fromText(text: String) =
        OtherEditText(
          id = idProvider.getAndIncrement(),
          startingText = text,
          currentText = text,
        )

      private val idProvider = AtomicInteger(0)
    }
  }

  /** "Add Another" other field button. Only used in multi-select when [OtherRow] is selected. */
  object OtherAddAnother : OptionSelectRow()

  fun key(): Any =
    when (this) {
      is Option -> "option_${option.displayString}"
      is OtherRow -> "other_row"
      is OtherEditText -> "other_edit_$id"
      OtherAddAnother -> "add_another"
    }
}

/** Converts the initial SelectedOptions state into rows to display * */
internal fun SelectedOptions.toOptionRows(
  multiSelect: Boolean,
  otherOptionsAllowed: Boolean,
): List<OptionSelectRow> = buildList {
  addAll(options.map { OptionSelectRow.Option(it) })
  if (!otherOptionsAllowed) return@buildList

  // Show "Other" option and select if other options list is not empty
  add(OptionSelectRow.OtherRow(selected = otherOptions.isNotEmpty()))

  if (otherOptions.isNotEmpty()) {
    check(multiSelect || otherOptions.size == 1) {
      "Multiple 'Other' options selected in single-select mode: $otherOptions"
    }
    addAll(otherOptions.map { OptionSelectRow.OtherEditText.fromText(it) })
  }

  sanitizeOtherOptionRows(multiSelectEnabled = multiSelect)
}

internal fun List<OptionSelectRow>.sanitizeOtherOptionRows(
  multiSelectEnabled: Boolean,
): List<OptionSelectRow> {
  // Now that we've set the selected states properly, we need to make sure that the "Other" rows
  // are showing in their correct state
  val isOtherRowSelected = this.any { it is OptionSelectRow.OtherRow && it.selected }
  return when {
    isOtherRowSelected && multiSelectEnabled && this.last() !is OptionSelectRow.OtherAddAnother -> {
      // In multi-select with Other enabled, we need the last row to be an AddAnother button
      this + OptionSelectRow.OtherAddAnother
    }
    isOtherRowSelected &&
      !multiSelectEnabled &&
      this.last() !is OptionSelectRow.OtherAddAnother -> {
      // In single-select with Other enabled, the last row should just be an OtherEditText
      this + OptionSelectRow.OtherEditText.fromText("")
    }
    !isOtherRowSelected -> {
      // We should not show the "Other" edit-texts or Add Another buttons, so return a sub-list with
      // those items dropped
      this.dropLastWhile {
        it is OptionSelectRow.OtherEditText || it is OptionSelectRow.OtherAddAnother
      }
    }
    else -> this
  }
}

/** Converts rows back to SelectedOptions for saving. */
internal fun List<OptionSelectRow>.toSelectedOptions(): SelectedOptions {
  return SelectedOptions(
    options = filterIsInstance<OptionSelectRow.Option>().map { it.option },
    otherOptions =
      filterIsInstance<OptionSelectRow.OtherEditText>()
        .filter { it.currentText.isNotBlank() }
        .map { it.currentText },
  )
}

internal const val OPTION_CHOICE_TAG = "dialog_select_option_choice"
internal const val OTHER_OPTION_TEXT_FIELD_TAG = "other_option_edit_text_field"
