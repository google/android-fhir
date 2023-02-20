/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir.datacapture.views

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.google.android.fhir.datacapture.ItemControlTypes
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.common.datatype.asStringValue
import com.google.android.fhir.datacapture.common.datatype.displayString
import com.google.android.fhir.datacapture.itemControl
import com.google.android.fhir.datacapture.localizedFlyoverSpanned
import com.google.android.fhir.datacapture.localizedTextSpanned
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType

internal object QuestionnaireItemDialogSelectViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_option_select_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    @SuppressLint("StaticFieldLeak")
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var holder: DialogSelectViewHolder
      override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem
      private var selectedOptionsJob: Job? = null

      override fun init(itemView: View) {
        holder = DialogSelectViewHolder(itemView)
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        cleanupOldState()
        holder.summaryHolder.hint =
          questionnaireItemViewItem.questionnaireItem.localizedFlyoverSpanned
        val activity =
          requireNotNull(holder.header.context.tryUnwrapContext()) {
            "Can only use dialog select in an AppCompatActivity context"
          }
        val viewModel: QuestionnaireItemDialogSelectViewModel by activity.viewModels()

        val item = questionnaireItemViewItem.questionnaireItem

        // Bind static data
        holder.header.bind(item)

        selectedOptionsJob =
          activity.lifecycleScope.launch {
            // Set the initial selected options state from the FHIR data model
            viewModel.updateSelectedOptions(
              item.linkId,
              questionnaireItemViewItem.extractInitialOptions(holder.header.context)
            )

            // Listen for changes to selected options to update summary + FHIR data model
            viewModel.getSelectedOptionsFlow(item.linkId).collect { selectedOptions ->
              holder.summary.text = selectedOptions.selectedSummary
              updateAnswers(selectedOptions)
            }
          }

        // When dropdown is clicked, show dialog
        val onClick =
          View.OnClickListener {
            val fragment =
              OptionSelectDialogFragment(
                title = item.localizedTextSpanned ?: "",
                config = item.buildConfig(),
              )
            fragment.arguments =
              bundleOf(
                OptionSelectDialogFragment.KEY_QUESTION_LINK_ID to item.linkId,
              )
            fragment.show(activity.supportFragmentManager, null)
          }

        // We need to set the click-listener on both the summary TextView, and the endIcon (the
        // small downward-facing arrow on the right side of the container), so that clicks on both
        // views will open the dialog.
        holder.summary.setOnClickListener(onClick)
        holder.summaryHolder.setEndIconOnClickListener(onClick)

        displayValidationResult(questionnaireItemViewItem.validationResult)
      }

      private fun displayValidationResult(validationResult: ValidationResult) {
        holder.summaryHolder.error =
          when (validationResult) {
            is NotValidated,
            Valid -> null
            is Invalid -> validationResult.getSingleStringValidationMessage()
          }
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        holder.summaryHolder.isEnabled = !isReadOnly
      }

      private fun cleanupOldState() {
        selectedOptionsJob?.cancel()
      }

      private fun updateAnswers(selectedOptions: SelectedOptions) {
        questionnaireItemViewItem.clearAnswer()
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
        questionnaireItemViewItem.setAnswer(*answers)
      }
    }

  private class DialogSelectViewHolder(itemView: View) {
    val header: QuestionnaireItemHeaderView = itemView.findViewById(R.id.header)
    val summary: TextView = itemView.findViewById(R.id.multi_select_summary)
    val summaryHolder: TextInputLayout = itemView.findViewById(R.id.multi_select_summary_holder)
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
    linkIdsToSelectedOptionsFlow.getOrPut(linkId) { MutableSharedFlow(replay = 1) }
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

private fun QuestionnaireItemViewItem.extractInitialOptions(context: Context): SelectedOptions {
  val options =
    answerOption.map { answerOption ->
      OptionSelectOption(
        item = answerOption,
        selected = isAnswerOptionSelected(answerOption),
        context = context
      )
    }
  return SelectedOptions(
    options = options,
    otherOptions =
      answers
        // All of the Other options will be encoded as String value types
        .mapNotNull { if (it.hasValueStringType()) it.valueStringType.value else null }
        // We should also make sure that these values aren't present in the predefined options
        .filter { value -> value !in options.map { it.item.value.asStringValue() } }
  )
}

private fun Questionnaire.QuestionnaireItemComponent.buildConfig() =
  OptionSelectDialogFragment.Config(
    multiSelect = repeats,
    // Client had to specify that they want an open-choice control to use "Other" options
    otherOptionsAllowed = itemControl == ItemControlTypes.OPEN_CHOICE,
  )
