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

package com.google.android.fhir.datacapture.views.factories

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.get
import androidx.core.view.isEmpty
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.displayString
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.HeaderView
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object AutoCompleteViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.edit_text_auto_complete_view) {

  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var header: HeaderView
      private lateinit var autoCompleteTextView: MaterialAutoCompleteTextView
      private lateinit var chipContainer: ChipGroup
      private lateinit var textInputLayout: TextInputLayout
      private val canHaveMultipleAnswers
        get() = questionnaireViewItem.questionnaireItem.repeats
      override lateinit var questionnaireViewItem: QuestionnaireViewItem
      private lateinit var errorTextView: TextView

      override fun init(itemView: View) {
        header = itemView.findViewById(R.id.header)
        autoCompleteTextView = itemView.findViewById(R.id.autoCompleteTextView)
        chipContainer = itemView.findViewById(R.id.chipContainer)
        textInputLayout = itemView.findViewById(R.id.text_input_layout)
        errorTextView = itemView.findViewById(R.id.error)
        autoCompleteTextView.onItemClickListener =
          AdapterView.OnItemClickListener { _, _, position, _ ->
            val answer =
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value =
                  questionnaireViewItem.answerOption
                    .first {
                      it.value.displayString(header.context) ==
                        autoCompleteTextView.adapter.getItem(position) as String
                    }
                    .valueCoding
              }

            onAnswerSelected(answer)
            autoCompleteTextView.setText("")
          }
      }

      override fun bind(questionnaireViewItem: QuestionnaireViewItem) {
        header.bind(questionnaireViewItem)
        header.showRequiredOrOptionalTextInHeaderView(questionnaireViewItem)
        val answerOptionString =
          questionnaireViewItem.answerOption.map { it.value.displayString(header.context) }
        val adapter = ArrayAdapter(header.context, R.layout.drop_down_list_item, answerOptionString)
        autoCompleteTextView.setAdapter(adapter)
        // Remove chips if any from the last bindView call on this VH.
        chipContainer.removeAllViews()
        presetValuesIfAny()

        displayValidationResult(questionnaireViewItem.validationResult)
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        for (i in 0 until chipContainer.childCount) {
          val view = chipContainer.getChildAt(i)
          view.isEnabled = !isReadOnly
          if (view is Chip && isReadOnly) {
            view.setOnCloseIconClickListener(null)
          }
        }
        textInputLayout.isEnabled = !isReadOnly
      }

      private fun presetValuesIfAny() {
        questionnaireViewItem.answers.map { answer -> addNewChipIfNotPresent(answer) }
      }

      private fun onAnswerSelected(
        answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
      ) {
        if (canHaveMultipleAnswers) {
          handleSelectionWhenQuestionCanHaveMultipleAnswers(answer)
        } else {
          handleSelectionWhenQuestionCanHaveSingleAnswer(answer)
        }
      }

      /**
       * Adds a new chip if it not already present in [chipContainer].It returns [true] if a new
       * Chip is added and [false] if the Chip is already present for the selected answer. The later
       * will happen if the user selects an already selected answer.
       */
      private fun addNewChipIfNotPresent(
        answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
      ): Boolean {
        if (chipIsAlreadyPresent(answer)) return false

        val chip = Chip(chipContainer.context, null, R.attr.questionnaireChipStyle)
        chip.id = View.generateViewId()
        chip.text = answer.valueCoding.display
        chip.isCloseIconVisible = true
        chip.isClickable = true
        chip.isCheckable = false
        chip.tag = answer
        chip.setOnCloseIconClickListener {
          chipContainer.removeView(chip)
          onChipRemoved(chip)
        }

        chipContainer.addView(chip)
        return true
      }

      private fun chipIsAlreadyPresent(
        answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
      ): Boolean {
        return chipContainer.children.any { chip ->
          (chip.tag as QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent)
            .value.equalsDeep(answer.value)
        }
      }

      private fun handleSelectionWhenQuestionCanHaveSingleAnswer(
        answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
      ) {
        if (chipContainer.isEmpty()) {
          addNewChipIfNotPresent(answer)
        } else {
          (chipContainer[0] as Chip).apply {
            text = answer.valueCoding.display
            tag = answer
          }
        }
        questionnaireViewItem.setAnswer(answer)
      }

      private fun handleSelectionWhenQuestionCanHaveMultipleAnswers(
        answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
      ) {
        val answerNotPresent =
          questionnaireViewItem.answers.none { it.value.equalsDeep(answer.value) }

        if (answerNotPresent) {
          addNewChipIfNotPresent(answer)
          questionnaireViewItem.addAnswer(answer)
        }
      }

      private fun onChipRemoved(chip: Chip) {
        if (canHaveMultipleAnswers) {
          (chip.tag as QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent).let {
            questionnaireViewItem.removeAnswer(it)
          }
        } else {
          questionnaireViewItem.clearAnswer()
        }
      }

      private fun displayValidationResult(validationResult: ValidationResult) {
        // https://github.com/material-components/material-components-android/issues/1435
        // Because of the above issue, we use separate error textview. But we still use
        // textInputLayout to show the error icon and the box color.
        when (validationResult) {
          is NotValidated,
          Valid -> {
            errorTextView.visibility = View.GONE
            textInputLayout.error = null
          }
          is Invalid -> {
            errorTextView.text = validationResult.getSingleStringValidationMessage()
            errorTextView.visibility = View.VISIBLE
            textInputLayout.error = " " // non empty text
          }
        }
      }
    }
}
