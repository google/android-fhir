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

import android_fhir.datacapture_kmp.generated.resources.Res
import android_fhir.datacapture_kmp.generated.resources.required_text_and_new_line
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import com.google.android.fhir.datacapture.extensions.FhirR4String
import com.google.android.fhir.datacapture.extensions.ItemControlTypes
import com.google.android.fhir.datacapture.extensions.itemControl
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.extensions.localizedFlyoverAnnotatedString
import com.google.android.fhir.datacapture.extensions.toQuestionnaireResponseItemAnswer
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.Header
import com.google.android.fhir.datacapture.views.compose.MediaItem
import com.google.android.fhir.datacapture.views.compose.OptionDialogSelect
import com.google.android.fhir.datacapture.views.compose.OptionSelectOption
import com.google.android.fhir.datacapture.views.compose.SelectedOptions
import com.google.android.fhir.datacapture.views.compose.getRequiredOrOptionalText
import com.google.android.fhir.datacapture.views.isAnswerOptionSelected
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

internal object DialogSelectViewFactory : QuestionnaireItemViewFactory {
  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
    val coroutineScope = rememberCoroutineScope { Dispatchers.Main }
    val readOnly =
      remember(questionnaireViewItem) {
        questionnaireViewItem.questionnaireItem.readOnly?.value ?: false
      }
    val hintLabelText =
      remember(questionnaireViewItem) {
        questionnaireViewItem.enabledDisplayItems.localizedFlyoverAnnotatedString
      }
    val requiredTextAndNewLineString = stringResource(Res.string.required_text_and_new_line)

    val validationResultMessage =
      remember(questionnaireViewItem.validationResult) {
        questionnaireViewItem.validationResult.getSingleStringValidationMessage()?.let {
          if (
            questionnaireViewItem.questionnaireItem.required?.value == true &&
              questionnaireViewItem.questionViewTextConfiguration.showRequiredText
          ) {
            "$requiredTextAndNewLineString$it"
          } else {
            it
          }
        }
      }

    val hasValidationError =
      remember(validationResultMessage) { !validationResultMessage.isNullOrBlank() }
    val requiredOrOptionalTextString = getRequiredOrOptionalText(questionnaireViewItem)
    val supportingHelperText =
      remember(questionnaireViewItem) {
        if (hasValidationError) {
          validationResultMessage
        } else {
          requiredOrOptionalTextString
        }
      }
    var selectedOptions by
      remember(questionnaireViewItem) {
        mutableStateOf(questionnaireViewItem.extractInitialOptions())
      }
    val selectedOptionsString = remember(selectedOptions) { selectedOptions.selectedSummary }
    val dialogTitle =
      remember(questionnaireViewItem) {
        questionnaireViewItem.questionText ?: hintLabelText ?: AnnotatedString("")
      }
    val isMultiSelect =
      remember(questionnaireViewItem) {
        questionnaireViewItem.questionnaireItem.repeats?.value ?: false
      }
    val allowOtherOptions =
      remember(questionnaireViewItem) {
        questionnaireViewItem.questionnaireItem.itemControl == ItemControlTypes.OPEN_CHOICE
      }

    Column(
      modifier =
        Modifier.fillMaxWidth()
          .padding(
            horizontal = QuestionnaireTheme.dimensions.itemMarginHorizontal,
            vertical = QuestionnaireTheme.dimensions.itemMarginVertical,
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
                    .map { it.item.toQuestionnaireResponseItemAnswer() }
                val otherOptionAnswers =
                  newOptions.otherOptions.map {
                    QuestionnaireResponse.Item.Answer(
                      value =
                        QuestionnaireResponse.Item.Answer.Value.String(
                          value = FhirR4String(value = it),
                        ),
                    )
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

private fun QuestionnaireViewItem.extractInitialOptions(): SelectedOptions {
  val options =
    enabledAnswerOptions.map { answerOption ->
      OptionSelectOption(
        item = answerOption,
        selected = isAnswerOptionSelected(answerOption),
      )
    }

  val optionsStringValues =
    options.mapNotNull {
      (it.item.value as? Questionnaire.Item.AnswerOption.Value.String)?.value?.value
    }
  val otherOptions =
    answers
      // All of the Other options will be encoded as String value types
      .mapNotNull { it.value?.asString()?.value?.value }
      // We should also make sure that these values aren't present in the predefined options
      .filter { value -> value !in optionsStringValues }

  return SelectedOptions(
    options = options,
    otherOptions = otherOptions,
  )
}

internal const val MULTI_SELECT_TEXT_FIELD_TAG = "multi_select_summary_holder"
