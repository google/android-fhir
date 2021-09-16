/*
 * Copyright 2020 Google LLC
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

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.fragment.app.FragmentResultListener
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.displayString
import com.google.android.fhir.datacapture.localizedPrefix
import com.google.android.fhir.datacapture.localizedText
import com.google.android.material.textfield.TextInputLayout
import org.hl7.fhir.r4.model.QuestionnaireResponse

/**
 * A dropdown-style widget that, when clicked, opens a dialog to select options.
 *
 * The dialog allows the user to select multiple options, and there is no "Other" option presented.
 */
internal object QuestionnaireItemMultiSelectViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_option_select_view) {
  override fun getQuestionnaireItemViewHolderDelegate(): QuestionnaireItemViewHolderDelegate =
    OptionsSelectViewHolderDelegate(
      config =
        OptionsDialogConfig(
          mode = OptionsDialogConfig.Mode.MULTI_SELECT,
        )
    )
}

/**
 * A dropdown-style widget that, when clicked, opens a dialog to select options.
 *
 * The dialog allows the user to select a single option, including an "Other" option with a
 * free-form text box.
 */
internal object QuestionnaireItemSingleSelectViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_option_select_view) {
  override fun getQuestionnaireItemViewHolderDelegate(): QuestionnaireItemViewHolderDelegate =
    OptionsSelectViewHolderDelegate(
      config =
        OptionsDialogConfig(
          mode = OptionsDialogConfig.Mode.SINGLE_SELECT,
        )
    )
}

private class OptionsSelectViewHolderDelegate(private val config: OptionsDialogConfig) :
  QuestionnaireItemViewHolderDelegate {
  private lateinit var holder: SelectViewHolder

  override fun init(itemView: View) {
    holder = SelectViewHolder(itemView)
  }

  override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
    val activity =
      requireNotNull(holder.question.context.tryUnwrapContext()) {
        "Can only use option select widget in an AppCompatActivity context"
      }

    val item = questionnaireItemViewItem.questionnaireItem

    holder.prefix.text = item.localizedPrefix
    holder.prefix.visibility = if (item.localizedPrefix.isNullOrEmpty()) GONE else VISIBLE

    holder.question.text = item.localizedText

    holder.summary.text = questionnaireItemViewItem.extractOptions().summaryText()
    val onClick =
      View.OnClickListener {
        activity.supportFragmentManager.setFragmentResultListener(
          OptionSelectDialogFragment.RESULT_REQUEST_KEY,
          activity,
          getFragmentResultListener(questionnaireItemViewItem),
        )
        val fragment =
          OptionSelectDialogFragment(
            title = item.localizedText ?: "",
            options = questionnaireItemViewItem.extractOptions(),
            config = config,
          )
        fragment.show(
          activity.supportFragmentManager,
          OptionSelectDialogFragment.RESULT_REQUEST_KEY
        )
      }
    // We need to set the click-listener on both the summary TextView, and the endIcon (the
    // small downward-facing arrow on the right side of the container), so that clicks on both
    // views will open the dialog.
    holder.summary.setOnClickListener(onClick)
    holder.summaryHolder.setEndIconOnClickListener(onClick)
  }

  private fun getFragmentResultListener(
    questionnaireItemViewItem: QuestionnaireItemViewItem
  ): FragmentResultListener = FragmentResultListener { _, result ->
    val (item, response, answersChangedCallback) = questionnaireItemViewItem

    val selectedIndices =
      result.getIntArray(OptionSelectDialogFragment.RESULT_BUNDLE_KEY_SELECTED_INDICES)
        ?: return@FragmentResultListener

    val allAnswers = item.answerOption
    val selectedAnswers = selectedIndices.map { selectedIndex -> allAnswers[selectedIndex] }

    response.answer.clear()
    response.answer.addAll(
      selectedAnswers.map { answer ->
        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
          value = answer.value
        }
      }
    )
    holder.summary.text = questionnaireItemViewItem.extractOptions().summaryText()
    answersChangedCallback()
  }

  private class SelectViewHolder(itemView: View) {
    val prefix: TextView = itemView.findViewById(R.id.prefix)
    val question: TextView = itemView.findViewById(R.id.question)
    val summary: TextView = itemView.findViewById(R.id.option_select_summary)
    val summaryHolder: TextInputLayout = itemView.findViewById(R.id.option_select_summary_holder)
  }
}

/** Settings that can be used to configure the options dialog. */
data class OptionsDialogConfig(
  val mode: Mode,
) {
  enum class Mode {
    SINGLE_SELECT,
    MULTI_SELECT
  }
}

/** Represents selectable options in the option-select page. */
data class SelectableOption(val name: String, val selected: Boolean)

/** Summary of the selected options, used for displaying in the immutable summary TextView. */
private fun List<SelectableOption>.summaryText(): String {
  return filter { it.selected }.joinToString { it.name }
}

private fun QuestionnaireItemViewItem.extractOptions(): List<SelectableOption> {
  return questionnaireItem.answerOption.map { answerOption ->
    SelectableOption(
      name = answerOption.displayString,
      selected = isAnswerOptionSelected(answerOption),
    )
  }
}
