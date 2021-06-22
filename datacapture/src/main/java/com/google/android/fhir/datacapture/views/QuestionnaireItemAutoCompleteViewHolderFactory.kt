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

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.get
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.displayString
import com.google.android.fhir.datacapture.localizedPrefix
import com.google.android.fhir.datacapture.localizedText
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireItemAutoCompleteViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_edit_text_auto_complete_view) {

  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var textInputLayout: TextInputLayout
      private lateinit var prefixTextView: TextView
      private lateinit var groupHeader: TextView
      private lateinit var autoCompleteTextView: AppCompatAutoCompleteTextView
      private lateinit var chipContainer: FlexboxLayout
      private lateinit var editText: TextInputEditText
      private lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

      private val canHaveMultipleAnswers
        get() = questionnaireItemViewItem.questionnaireItem.repeats

      override fun init(itemView: View) {
        prefixTextView = itemView.findViewById(R.id.prefix)
        groupHeader = itemView.findViewById(R.id.group_header)
        autoCompleteTextView = itemView.findViewById(R.id.autoCompleteTextView)
        chipContainer = itemView.findViewById(R.id.flexboxLayout)
        textInputLayout = itemView.findViewById(R.id.textInputLayout)
        editText = itemView.findViewById(R.id.textInputEditText)

        autoCompleteTextView.dropDownAnchor = textInputLayout.editText!!.id
        autoCompleteTextView.onItemClickListener =
          AdapterView.OnItemClickListener { _, _, position, id ->
            val answer =
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value =
                  questionnaireItemViewItem.questionnaireItem.answerOption
                    .first {
                      it.displayString == autoCompleteTextView.adapter.getItem(position) as String
                    }
                    .valueCoding
              }

            onAnswerSelected(answer)
            autoCompleteTextView.setText("")
            editText.setText("")
          }

        editText.setOnKeyListener { _, _, event ->
          if (event != null &&
              event.action == KeyEvent.ACTION_DOWN &&
              event.keyCode == KeyEvent.KEYCODE_DEL
          ) {
            if (editText.length() == 0 && chipContainer.childCount > 1) {
              val chip = chipContainer.getChildAt(chipContainer.childCount - 2) as Chip
              chipContainer.removeView(chip)
              onChipRemoved(chip)
            }
          }
          false
        }

        chipContainer.background = textInputLayout.editText!!.background
        editText.onFocusChangeListener =
          View.OnFocusChangeListener { _, hasFocus ->
            updateContainerBorder(hasFocus)
            if (!hasFocus) {
              autoCompleteTextView.setText("")
              editText.setText("")
            }
          }

        editText.addTextChangedListener(
          object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
              if (!autoCompleteTextView.isPopupShowing) {
                autoCompleteTextView.showDropDown()
              }
              autoCompleteTextView.setText(s.toString(), true)
            }
          }
        )
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        this.questionnaireItemViewItem = questionnaireItemViewItem
        if (!questionnaireItemViewItem.questionnaireItem.prefix.isNullOrEmpty()) {
          prefixTextView.visibility = View.VISIBLE
          prefixTextView.text = questionnaireItemViewItem.questionnaireItem.localizedPrefix
        } else {
          prefixTextView.visibility = View.GONE
        }
        groupHeader.text = questionnaireItemViewItem.questionnaireItem.localizedText
        groupHeader.visibility =
          if (groupHeader.text.isEmpty()) {
            View.GONE
          } else {
            View.VISIBLE
          }

        val answerOptionString =
          questionnaireItemViewItem.questionnaireItem.answerOption.map { it.displayString }
        val adapter =
          ArrayAdapter(
            chipContainer.context,
            android.R.layout.simple_dropdown_item_1line,
            answerOptionString
          )
        autoCompleteTextView.setAdapter(adapter)
        // Remove chips as this FlexBox might contain chips from some previous item where it was
        // used
        // in the recyclerview
        val textBox = chipContainer.getChildAt(chipContainer.childCount - 1)
        chipContainer.removeAllViews()
        chipContainer.addView(textBox)
        presetValuesIfAny()
      }

      private fun presetValuesIfAny() {
        questionnaireItemViewItem.questionnaireResponseItem.answer?.forEach {
          if (canHaveMultipleAnswers) {
            addNewChipIfNotPresent(it)
          } else {
            replaceChip(it)
          }
        }
      }

      private fun onAnswerSelected(
        answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
      ) {
        if (canHaveMultipleAnswers) {
          questionnaireItemViewItem.takeIf { addNewChipIfNotPresent(answer) }?.addAnswer(answer)
        } else {
          replaceChip(answer)
          questionnaireItemViewItem.singleAnswerOrNull = answer
        }
      }

      private fun addNewChipIfNotPresent(
        answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
      ): Boolean {

        if (chipIsAlreadyPresent(answer.valueCoding.display)) return false

        val chip = Chip(chipContainer.context)
        chip.text = answer.valueCoding.display
        chip.isCloseIconVisible = true
        chip.isClickable = true
        chip.isCheckable = false
        chip.setTag(R.id.flexboxLayout, answer)

        chipContainer.addView(chip, chipContainer.childCount - 1)
        chip.setOnCloseIconClickListener {
          chipContainer.removeView(chip)
          onChipRemoved(chip)
        }
        (chip.layoutParams as ViewGroup.MarginLayoutParams).marginEnd =
          chipContainer.context.resources.getDimension(R.dimen.auto_complete_item_gap).toInt()
        return true
      }

      private fun chipIsAlreadyPresent(selection: String): Boolean {
        return chipContainer.children.filter { view -> view is Chip }.find { chip ->
          (chip as Chip).text == selection
        } != null
      }

      private fun replaceChip(
        answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
      ) {
        if (chipContainer.childCount == 1) {
          addNewChipIfNotPresent(answer)
        } else {
          (chipContainer[0] as Chip).apply {
            text = answer.valueCoding.display
            setTag(R.id.flexboxLayout, answer)
          }
        }
      }

      private fun onChipRemoved(chip: Chip) {
        if (canHaveMultipleAnswers) {
          val answer =
            chip.getTag(R.id.flexboxLayout) as
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
          questionnaireItemViewItem.removeAnswer(answer)
        } else {
          questionnaireItemViewItem.singleAnswerOrNull = null
        }
      }

      private fun updateContainerBorder(hasFocus: Boolean) {
        (chipContainer.background as MaterialShapeDrawable).apply {
          setStroke(
            if (hasFocus) {
              textInputLayout.boxStrokeWidthFocused.toFloat()
            } else {
              textInputLayout.boxStrokeWidth.toFloat()
            },
            if (hasFocus) {
              textInputLayout.boxStrokeColor
            } else {
              ContextCompat.getColor(
                textInputLayout.context,
                com.google.android.material.R.color.mtrl_textinput_default_box_stroke_color
              )
            }
          )
        }
      }
    }
}
