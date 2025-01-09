/*
 * Copyright 2022-2025 Google LLC
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.get
import androidx.core.view.isEmpty
import androidx.lifecycle.lifecycleScope
import com.google.android.fhir.datacapture.CustomCallback
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.displayString
import com.google.android.fhir.datacapture.extensions.identifierString
import com.google.android.fhir.datacapture.extensions.tryUnwrapContext
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
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object AutoCompleteViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.edit_text_auto_complete_view) {

  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var context: AppCompatActivity
      private lateinit var header: HeaderView
      private lateinit var autoCompleteTextView: MaterialAutoCompleteTextView
      private lateinit var chipContainer: ChipGroup
      private lateinit var textInputLayout: TextInputLayout
      private lateinit var adapter: ArrayAdapter<AutoCompleteViewAnswerOption>

      private val canHaveMultipleAnswers
        get() = questionnaireViewItem.questionnaireItem.repeats

      private val callback: CustomCallback<*>?
        get() = questionnaireViewItem.callback

      override lateinit var questionnaireViewItem: QuestionnaireViewItem
      private lateinit var errorTextView: TextView

      override fun init(itemView: View) {
        context = itemView.context.tryUnwrapContext()!!
        header = itemView.findViewById(R.id.header)
        autoCompleteTextView = itemView.findViewById(R.id.autoCompleteTextView)
        chipContainer = itemView.findViewById(R.id.chipContainer)
        textInputLayout = itemView.findViewById(R.id.text_input_layout)
        errorTextView = itemView.findViewById(R.id.error)
        autoCompleteTextView.onItemClickListener = onClickListener()
      }

      override fun bind(questionnaireViewItem: QuestionnaireViewItem) {
        header.bind(questionnaireViewItem)
        header.showRequiredOrOptionalTextInHeaderView(questionnaireViewItem)
        val suggestions = mutableListOf<AutoCompleteViewAnswerOption>()
        val answerOptionValues =
          questionnaireViewItem.enabledAnswerOptions.map {
            AutoCompleteViewAnswerOption(
              answerId = it.value.identifierString(header.context),
              answerDisplay = it.value.displayString(header.context),
            )
          }
        suggestions.addAll(answerOptionValues)
        adapter =
          AutoCompleteArrayAdapter(
            context = header.context,
            resource = R.layout.drop_down_list_item,
            textViewResourceId = R.id.answer_option_textview,
            objects = answerOptionValues,
            callback = callback,
            answerValueSet = questionnaireViewItem.questionnaireItem.answerValueSet,
          )
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
        answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent,
      ) {
        if (canHaveMultipleAnswers) {
          handleSelectionWhenQuestionCanHaveMultipleAnswers(answer)
        } else {
          handleSelectionWhenQuestionCanHaveSingleAnswer(answer)
        }
      }

      private fun onClickListener() =
        AdapterView.OnItemClickListener { _, _, position, _ ->
          val answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
          if (questionnaireViewItem.enabledAnswerOptions.isEmpty()) {
            val answerValue =
              autoCompleteTextView.adapter.getItem(position) as AutoCompleteViewAnswerOption
            val answerCoding =
              Coding().apply {
                code = answerValue.answerId
                display = answerValue.answerDisplay
              }
            answer =
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = answerCoding
              }
          } else {
            answer =
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value =
                  questionnaireViewItem.enabledAnswerOptions
                    .first {
                      it.value.identifierString(header.context) ==
                        (autoCompleteTextView.adapter.getItem(position)
                            as AutoCompleteViewAnswerOption)
                          .answerId
                    }
                    .valueCoding
              }
          }
          onAnswerSelected(answer)
          autoCompleteTextView.setText("")
        }

      /**
       * Adds a new chip if it not already present in [chipContainer].It returns [true] if a new
       * Chip is added and [false] if the Chip is already present for the selected answer. The later
       * will happen if the user selects an already selected answer.
       */
      private fun addNewChipIfNotPresent(
        answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent,
      ): Boolean {
        if (chipIsAlreadyPresent(answer)) return false

        val chip = Chip(chipContainer.context, null, R.attr.questionnaireChipStyle)
        chip.id = View.generateViewId()
        chip.text = answer.valueCoding.displayOrCode
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
        answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent,
      ): Boolean {
        return chipContainer.children.any { chip ->
          (chip.tag as QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent)
            .value
            .equalsDeep(answer.value)
        }
      }

      private fun handleSelectionWhenQuestionCanHaveSingleAnswer(
        answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent,
      ) {
        if (chipContainer.isEmpty()) {
          addNewChipIfNotPresent(answer)
        } else {
          (chipContainer[0] as Chip).apply {
            text = answer.valueCoding.displayOrCode
            tag = answer
          }
        }
        context.lifecycleScope.launch { questionnaireViewItem.setAnswer(answer) }
      }

      private fun handleSelectionWhenQuestionCanHaveMultipleAnswers(
        answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent,
      ) {
        val answerNotPresent =
          questionnaireViewItem.answers.none { it.value.equalsDeep(answer.value) }

        if (answerNotPresent) {
          addNewChipIfNotPresent(answer)
          context.lifecycleScope.launch { questionnaireViewItem.addAnswer(answer) }
        }
      }

      private fun onChipRemoved(chip: Chip) {
        context.lifecycleScope.launch {
          if (canHaveMultipleAnswers) {
            (chip.tag as QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent).let {
              questionnaireViewItem.removeAnswer(it)
            }
          } else {
            questionnaireViewItem.clearAnswer()
          }
        }
      }

      private fun displayValidationResult(validationResult: ValidationResult) {
        // https://github.com/material-components/material-components-android/issues/1435
        // Because of the above issue, we use separate error textview. But we still use
        // textInputLayout to show the error icon and the box color.
        when (validationResult) {
          is NotValidated,
          Valid, -> {
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

      private val Coding.displayOrCode: String
        get() =
          if (display.isNullOrBlank()) {
            code
          } else {
            display
          }
    }
}

/**
 * An answer option that would show up as a dropdown item in an [AutoCompleteViewHolderFactory]
 * textview
 */
data class AutoCompleteViewAnswerOption(val answerId: String, val answerDisplay: String) {
  override fun toString(): String {
    return this.answerDisplay
  }
}

internal class AutoCompleteArrayAdapter(
  context: Context,
  val resource: Int,
  val textViewResourceId: Int,
  private val objects: List<AutoCompleteViewAnswerOption>,
  private val callback: CustomCallback<*>? = null,
  private val answerValueSet: String? = null,
) : ArrayAdapter<AutoCompleteViewAnswerOption>(context, resource, textViewResourceId, objects) {

  private var items = listOf<AutoCompleteViewAnswerOption>()

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
    val item = getItem(position)
    view.findViewById<TextView>(textViewResourceId).text = item.toString()
    return view
  }

  override fun getCount(): Int = items.size

  fun updateData(newData: List<AutoCompleteViewAnswerOption>) {
    items = newData
    notifyDataSetChanged()
  }

  override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
    return getView(position, convertView, parent)
  }

  override fun getItem(position: Int): AutoCompleteViewAnswerOption? = items.getOrNull(position)

  @Suppress("UNCHECKED_CAST")
  override fun getFilter(): Filter {
    return object : Filter() {
      override fun performFiltering(constraint: CharSequence?): FilterResults {
        val query = (constraint?.toString() ?: "").trim()
        val filteredResults: List<AutoCompleteViewAnswerOption> =
          if (callback != null && answerValueSet != null && objects.isEmpty()) {
            (callback as? CustomCallback<AutoCompleteViewAnswerOption>)?.invoke(
              query,
              answerValueSet,
            )
              ?: emptyList()
          } else {
            objects.filter { it.answerDisplay.contains(query, ignoreCase = true) }
          }
        return FilterResults().apply {
          values = filteredResults
          count = filteredResults.size
        }
      }

      override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        @Suppress("UNCHECKED_CAST")
        val data = results?.values as? List<AutoCompleteViewAnswerOption> ?: emptyList()
        updateData(data)
      }
    }
  }
}
