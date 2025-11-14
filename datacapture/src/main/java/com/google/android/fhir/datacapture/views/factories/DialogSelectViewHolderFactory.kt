/*
 * Copyright 2023-2025 Google LLC
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
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.AnnotatedString
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.ItemControlTypes
import com.google.android.fhir.datacapture.extensions.asStringValue
import com.google.android.fhir.datacapture.extensions.displayString
import com.google.android.fhir.datacapture.extensions.getRequiredOrOptionalText
import com.google.android.fhir.datacapture.extensions.getValidationErrorMessage
import com.google.android.fhir.datacapture.extensions.itemControl
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.extensions.localizedFlyoverAnnotatedString
import com.google.android.fhir.datacapture.extensions.localizedFlyoverSpanned
import com.google.android.fhir.datacapture.extensions.toSpanned
import com.google.android.fhir.datacapture.extensions.tryUnwrapContext
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.HeaderView
import com.google.android.fhir.datacapture.views.OptionSelectDialogFragment
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.DialogSelect
import com.google.android.fhir.datacapture.views.compose.Header
import com.google.android.fhir.datacapture.views.compose.MediaItem
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType

internal object DialogSelectViewHolderFactory : QuestionnaireItemComposeViewHolderFactory {
  @OptIn(ExperimentalMaterial3Api::class)
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemComposeViewHolderDelegate {
      lateinit var questionnaireViewItem: QuestionnaireViewItem
      private lateinit var header: HeaderView
      private lateinit var summary: TextView
      private lateinit var summaryHolder: TextInputLayout
      private var selectedOptionsJob: Job? = null

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
              DialogSelect(
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

              //                OptionSelectDialog (
              //                    title = dialogTitle,
              //                    multiSelect = isMultiSelect,
              //                    otherOptionsAllowed = allowOtherOptions,
              //                    initialSelectedOptions = selectedOptions,
              //                    onDismiss = { expanded = false },
              //                    onConfirm = { newOptions ->
              //                        coroutineScope.launch {
              //                            selectedOptions = newOptions
              //                            updateAnswers(questionnaireViewItem, newOptions)
              //                        }
              //                    },
              //                )
            }
          }
        }
      }

      fun init(itemView: View) {
        header = itemView.findViewById(R.id.header)
        summary = itemView.findViewById(R.id.multi_select_summary)
        summaryHolder = itemView.findViewById(R.id.multi_select_summary_holder)
      }

      fun bind(questionnaireViewItem: QuestionnaireViewItem) {
        cleanupOldState()
        with(summaryHolder) {
          hint = questionnaireViewItem.enabledDisplayItems.localizedFlyoverSpanned
          helperText = getRequiredOrOptionalText(questionnaireViewItem, context)
        }
        val activity =
          requireNotNull(header.context.tryUnwrapContext()) {
            "Can only use dialog select in an AppCompatActivity context"
          }
        val viewModel: QuestionnaireItemDialogSelectViewModel by activity.viewModels()

        // Bind static data
        header.bind(questionnaireViewItem)

        val questionnaireItem = questionnaireViewItem.questionnaireItem
        val selectedOptions = questionnaireViewItem.extractInitialOptions(header.context)
        summary.text = selectedOptions.selectedSummary.toSpanned()
        selectedOptionsJob =
          activity.lifecycleScope.launch {
            // Listen for changes to selected options to update summary + FHIR data model
            viewModel.getSelectedOptionsFlow(questionnaireItem.linkId).collect { selectedOptions ->
              summary.text = selectedOptions.selectedSummary.toSpanned()
              updateAnswers(questionnaireViewItem, selectedOptions)
            }
          }

        // When dropdown is clicked, show dialog
        val onClick =
          View.OnClickListener {
            val fragment =
              OptionSelectDialogFragment(
                // We use the question text for the dialog title. If there is no question text, we
                // use flyover text as it is sometimes used in text fields instead of question text.
                title = questionnaireViewItem.questionText
                    ?: questionnaireItem.localizedFlyoverSpanned ?: "",
                config = questionnaireItem.buildConfig(),
                selectedOptions = selectedOptions,
              )
            fragment.arguments =
              bundleOf(
                OptionSelectDialogFragment.KEY_QUESTION_LINK_ID to questionnaireItem.linkId,
              )
            fragment.show(activity.supportFragmentManager, null)
          }

        // We need to set the click-listener on both the summary TextView, and the endIcon (the
        // small downward-facing arrow on the right side of the container), so that clicks on both
        // views will open the dialog.
        summary.setOnClickListener(onClick)
        summaryHolder.setEndIconOnClickListener(onClick)

        displayValidationResult(questionnaireViewItem.validationResult)
      }

      private fun displayValidationResult(validationResult: ValidationResult) {
        summaryHolder.error =
          getValidationErrorMessage(
            summaryHolder.context,
            questionnaireViewItem,
            validationResult,
          )
      }

      fun setReadOnly(isReadOnly: Boolean) {
        summaryHolder.isEnabled = !isReadOnly
      }

      private fun cleanupOldState() {
        selectedOptionsJob?.cancel()
      }

      private suspend fun updateAnswers(
        questionnaireViewItem: QuestionnaireViewItem,
        selectedOptions: SelectedOptions,
      ) {
        questionnaireViewItem.clearAnswer()
        var answers = arrayOf<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>()
        selectedOptions.options
          .filter { it.selected }
          .map { option ->
            answers +=
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = option.item.value
              }
          }
        selectedOptions.otherOptions.map { otherOption ->
          answers +=
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = StringType(otherOption)
            }
        }
        questionnaireViewItem.setAnswer(*answers)
      }
    }
}

internal class QuestionnaireItemDialogSelectViewModel : ViewModel() {
  private val linkIdsToSelectedOptionsFlow =
    mutableMapOf<String, MutableSharedFlow<SelectedOptions>>()

  fun getSelectedOptionsFlow(linkId: String): Flow<SelectedOptions> = selectedOptionsFlow(linkId)

  suspend fun updateSelectedOptions(linkId: String, selectedOptions: SelectedOptions) {
    selectedOptionsFlow(linkId).emit(selectedOptions)
  }

  private fun selectedOptionsFlow(linkId: String) =
    linkIdsToSelectedOptionsFlow.getOrPut(linkId) { MutableSharedFlow(replay = 0) }
}

data class SelectedOptions(
  val options: List<OptionSelectOption>,
  val otherOptions: List<String>,
) {
  val selectedSummary: String =
    (options.filter { it.selected }.map { it.displayString } + otherOptions).joinToString()
}

/** Represents selectable options in the multi-select page. */
data class OptionSelectOption(
  val item: Questionnaire.QuestionnaireItemAnswerOptionComponent,
  val selected: Boolean,
  val context: Context,
) {
  val displayString: String = item.value.displayString(context)
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

private fun Questionnaire.QuestionnaireItemComponent.buildConfig() =
  OptionSelectDialogFragment.Config(
    multiSelect = repeats,
    // Client had to specify that they want an open-choice control to use "Other" options
    otherOptionsAllowed = itemControl == ItemControlTypes.OPEN_CHOICE,
  )
