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
      /**
       * This view is a container that contains the selected answers as Chip(View) and the EditText
       * that is used to enter the answer query. Current logic in this class expects the EditText to
       * be always there in the FlexboxLayout as its last child. Any new answer Chip is added as a
       * Child before the EditText
       */
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
          AdapterView.OnItemClickListener { _, _, position, _ ->
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
            /** Check [chipContainer] on how children are laid out. */
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
        /**
         * Remove chips as this FlexBox might contain chips from the last time bindView was called
         * for this VH. Check [chipContainer] on how children are laid out.
         */
        val textBox = chipContainer.getChildAt(chipContainer.childCount - 1)
        chipContainer.removeAllViews()
        chipContainer.addView(textBox)
        presetValuesIfAny()
      }

      private fun presetValuesIfAny() {
        questionnaireItemViewItem.questionnaireResponseItem.answer?.let {
          it.map { answer -> addNewChipIfNotPresent(answer) }
        }
      }

      private fun onAnswerSelected(
        answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
      ) {
        if (canHaveMultipleAnswers) {
          val answerNotPresent =
            questionnaireItemViewItem.questionnaireResponseItem.answer?.none {
              it.value.equalsDeep(answer.value)
            }
              ?: false
          if (answerNotPresent) {
            addNewChipIfNotPresent(answer)
            questionnaireItemViewItem.addAnswer(answer)
          }
        } else {
          replaceChip(answer)
          questionnaireItemViewItem.singleAnswerOrNull = answer
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

      private fun chipIsAlreadyPresent(
        answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
      ): Boolean {
        return chipContainer.children.any { view ->
          (view is Chip) &&
            (view.getTag(R.id.flexboxLayout) as
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent)
              .value.equalsDeep(answer.value)
        }
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
