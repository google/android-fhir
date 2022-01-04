/*
 * Copyright 2021 Google LLC
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
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.google.android.fhir.datacapture.ItemControlTypes
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.common.datatype.asStringValue
import com.google.android.fhir.datacapture.displayString
import com.google.android.fhir.datacapture.itemControl
import com.google.android.fhir.datacapture.localizedPrefix
import com.google.android.fhir.datacapture.localizedText
import com.google.android.fhir.datacapture.utilities.toSpanned
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.validation.getSingleStringValidationMessage
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
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

      override fun init(itemView: View) {
        holder = DialogSelectViewHolder(itemView)
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        val activity =
          requireNotNull(holder.question.context.tryUnwrapContext()) {
            "Can only use dialog select in an AppCompatActivity context"
          }
        val viewModel: QuestionnaireItemDialogSelectViewModel by activity.viewModels()

        val (item, response) = questionnaireItemViewItem

        // Bind static data
        holder.prefix.text = item.localizedPrefix
        holder.prefix.visibility = if (item.localizedPrefix.isNullOrEmpty()) GONE else VISIBLE
        holder.question.text = item.localizedText?.toSpanned()

        activity.lifecycleScope.launch {
          // Set the initial selected options state from the FHIR data model
          viewModel.updateSelectedOptions(
            item.linkId,
            questionnaireItemViewItem.extractInitialOptions()
          )

          // Listen for changes to selected options to update summary + FHIR data model
          viewModel.getSelectedOptionsFlow(item.linkId).collect { selectedOptions ->
            holder.summary.text = selectedOptions.selectedSummary
            response.updateAnswers(selectedOptions)
            onAnswerChanged(holder.summaryHolder.context)
          }
        }

        // When dropdown is clicked, show dialog
        val onClick =
          View.OnClickListener {
            val fragment =
              OptionSelectDialogFragment(
                title = item.localizedText ?: "",
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
      }

      override fun displayValidationResult(validationResult: ValidationResult) {
        holder.summaryHolder.error =
          validationResult.getSingleStringValidationMessage().takeIf { it.isNotEmpty() }
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        holder.summaryHolder.isEnabled = !isReadOnly
      }
    }

  private class DialogSelectViewHolder(itemView: View) {
    val prefix: TextView = itemView.findViewById(R.id.prefix_text_view)
    val question: TextView = itemView.findViewById(R.id.question_text_view)
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
) {
  val displayString: String = item.displayString
}

private fun QuestionnaireItemViewItem.extractInitialOptions(): SelectedOptions {
  val options =
    answerOption.map { answerOption ->
      OptionSelectOption(item = answerOption, selected = isAnswerOptionSelected(answerOption))
    }
  return SelectedOptions(
    options = options,
    otherOptions =
      questionnaireResponseItem.answer
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

private fun QuestionnaireResponse.QuestionnaireResponseItemComponent.updateAnswers(
  selectedOptions: SelectedOptions
) {
  answer.clear()
  answer.addAll(
    selectedOptions.options.filter { it.selected }.map { option ->
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
        value = option.item.value
      }
    }
  )
  answer.addAll(
    selectedOptions.otherOptions.map { otherOption ->
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
        value = StringType(otherOption)
      }
    }
  )
}
