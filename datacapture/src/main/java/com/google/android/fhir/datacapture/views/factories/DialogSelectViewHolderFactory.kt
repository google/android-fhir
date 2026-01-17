/*
 * Copyright 2023-2026 Google LLC
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

package com.google.android.fhir.datacapture.views.factories

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.ItemControlTypes
import com.google.android.fhir.datacapture.extensions.asStringValue
import com.google.android.fhir.datacapture.extensions.getRequiredOrOptionalText
import com.google.android.fhir.datacapture.extensions.getValidationErrorMessage
import com.google.android.fhir.datacapture.extensions.itemControl
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.extensions.localizedFlyoverAnnotatedString
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.components.Header
import com.google.android.fhir.datacapture.views.components.MediaItem
import com.google.android.fhir.datacapture.views.components.OptionDialogSelect
import com.google.android.fhir.datacapture.views.components.OptionSelectOption
import com.google.android.fhir.datacapture.views.components.SelectedOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType

internal object DialogSelectViewHolderFactory : QuestionnaireItemComposeViewHolderFactory {
  @OptIn(ExperimentalMaterial3Api::class)
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemComposeViewHolderDelegate {

      @Composable
      override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope { Dispatchers.Main }
        val readOnly =
          remember(questionnaireViewItem) { questionnaireViewItem.questionnaireItem.readOnly }
        val hintLabelText =
          remember(questionnaireViewItem) {
            questionnaireViewItem.enabledDisplayItems.localizedFlyoverAnnotatedString
          }
        val validationResultMessage =
          remember(questionnaireViewItem) {
            getValidationErrorMessage(
              context,
              questionnaireViewItem,
              questionnaireViewItem.validationResult,
            )
          }
        val hasValidationError =
          remember(validationResultMessage) { !validationResultMessage.isNullOrBlank() }
        val supportingHelperText =
          remember(questionnaireViewItem) {
            if (hasValidationError) {
              validationResultMessage
            } else {
              getRequiredOrOptionalText(questionnaireViewItem, context)
            }
          }
        var selectedOptions by
          remember(questionnaireViewItem) {
            mutableStateOf(questionnaireViewItem.extractInitialOptions(context))
          }
        val selectedOptionsString = remember(selectedOptions) { selectedOptions.selectedSummary }
        val dialogTitle =
          remember(questionnaireViewItem) {
            questionnaireViewItem.questionTextAnnotatedString
              ?: hintLabelText ?: AnnotatedString("")
          }
        val isMultiSelect =
          remember(questionnaireViewItem) { questionnaireViewItem.questionnaireItem.repeats }
        val allowOtherOptions =
          remember(questionnaireViewItem) {
            questionnaireViewItem.questionnaireItem.itemControl == ItemControlTypes.OPEN_CHOICE
          }

        Column(
          modifier =
            Modifier.fillMaxWidth()
              .padding(
                horizontal = dimensionResource(R.dimen.item_margin_horizontal),
                vertical = dimensionResource(R.dimen.item_margin_vertical),
              ),
        ) {
          Header(
            questionnaireViewItem,
          )
          questionnaireViewItem.questionnaireItem.itemMedia?.let { MediaItem(it) }

          var expanded by remember { mutableStateOf(false) }
          ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
              value = selectedOptionsString,
              onValueChange = {},
              readOnly = true,
              modifier =
                Modifier.fillMaxWidth()
                  .testTag(MULTI_SELECT_TEXT_FIELD_TAG)
                  .semantics { if (hasValidationError) error(supportingHelperText ?: "") }
                  .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, !readOnly),
              label = { hintLabelText?.let { Text(it) } },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
              supportingText = {
                if (!supportingHelperText.isNullOrBlank()) {
                  Text(supportingHelperText)
                }
              },
              isError = hasValidationError,
              enabled = !readOnly,
            )

            if (expanded) {
              OptionDialogSelect(
                context = context,
                title = dialogTitle,
                multiSelect = isMultiSelect,
                otherOptionsAllowed = allowOtherOptions,
                selectedOptions = selectedOptions,
                onDismiss = { expanded = false },
                onConfirm = { newOptions ->
                  selectedOptions = newOptions
                  coroutineScope.launch {
                    val optionAnswers =
                      newOptions.options
                        .filter { it.selected }
                        .map {
                          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                            value = it.item.value
                          }
                        }
                    val otherOptionAnswers =
                      newOptions.otherOptions.map {
                        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                          value = StringType(it)
                        }
                      }

                    val answersArray = (optionAnswers + otherOptionAnswers).toTypedArray()

                    questionnaireViewItem.setAnswer(*answersArray)
                  }
                },
              )
            }
          }
        }
      }
    }
}

private fun QuestionnaireViewItem.extractInitialOptions(context: Context): SelectedOptions {
  val options =
    enabledAnswerOptions.map { answerOption ->
      OptionSelectOption(
        item = answerOption,
        selected = isAnswerOptionSelected(answerOption),
        context = context,
      )
    }
  return SelectedOptions(
    options = options,
    otherOptions =
      answers
        // All of the Other options will be encoded as String value types
        .mapNotNull { if (it.hasValueStringType()) it.valueStringType.value else null }
        // We should also make sure that these values aren't present in the predefined options
        .filter { value -> value !in options.map { it.item.value.asStringValue() } },
  )
}

internal const val MULTI_SELECT_TEXT_FIELD_TAG = "multi_select_summary_holder"
