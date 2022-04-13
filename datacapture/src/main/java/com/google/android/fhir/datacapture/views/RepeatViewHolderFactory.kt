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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.google.android.fhir.datacapture.QuestionnaireItemAdapter
import com.google.android.fhir.datacapture.QuestionnaireItemViewHolderType
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.contrib.views.QuestionnaireItemPhoneNumberViewHolderFactory
import com.google.android.fhir.datacapture.localizedTextSpanned
import com.google.android.fhir.datacapture.validation.ValidationResult

object RepeatViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_repeats) {
  lateinit var itemAdapter: QuestionnaireItemAdapter
  var newViewType: Int = 0
  var showRemoveViewButton = false

  fun create(
    parent: View,
    viewType: Int,
    questionnaireItemAdapter: QuestionnaireItemAdapter
  ): QuestionnaireItemViewHolder {
    newViewType = viewType + 1000
    itemAdapter = questionnaireItemAdapter

    val viewHolderFactory2 =
      when (QuestionnaireItemViewHolderType.fromInt(newViewType)) {
        QuestionnaireItemViewHolderType.GROUP -> QuestionnaireItemGroupViewHolderFactory
        QuestionnaireItemViewHolderType.BOOLEAN_TYPE_PICKER ->
          QuestionnaireItemBooleanTypePickerViewHolderFactory
        QuestionnaireItemViewHolderType.DATE_PICKER -> QuestionnaireItemDatePickerViewHolderFactory
        QuestionnaireItemViewHolderType.DATE_TIME_PICKER ->
          QuestionnaireItemDateTimePickerViewHolderFactory
        QuestionnaireItemViewHolderType.EDIT_TEXT_SINGLE_LINE ->
          QuestionnaireItemEditTextSingleLineViewHolderFactory
        QuestionnaireItemViewHolderType.EDIT_TEXT_MULTI_LINE ->
          QuestionnaireItemEditTextMultiLineViewHolderFactory
        QuestionnaireItemViewHolderType.EDIT_TEXT_INTEGER ->
          QuestionnaireItemEditTextIntegerViewHolderFactory
        QuestionnaireItemViewHolderType.EDIT_TEXT_DECIMAL ->
          QuestionnaireItemEditTextDecimalViewHolderFactory
        QuestionnaireItemViewHolderType.RADIO_GROUP -> QuestionnaireItemRadioGroupViewHolderFactory
        QuestionnaireItemViewHolderType.DROP_DOWN -> QuestionnaireItemDropDownViewHolderFactory
        QuestionnaireItemViewHolderType.DISPLAY -> QuestionnaireItemDisplayViewHolderFactory
        QuestionnaireItemViewHolderType.QUANTITY ->
          QuestionnaireItemEditTextQuantityViewHolderFactory
        QuestionnaireItemViewHolderType.CHECK_BOX_GROUP ->
          QuestionnaireItemCheckBoxGroupViewHolderFactory
        QuestionnaireItemViewHolderType.AUTO_COMPLETE ->
          QuestionnaireItemAutoCompleteViewHolderFactory
        QuestionnaireItemViewHolderType.DIALOG_SELECT ->
          QuestionnaireItemDialogSelectViewHolderFactory
        QuestionnaireItemViewHolderType.SLIDER -> QuestionnaireItemSliderViewHolderFactory
        QuestionnaireItemViewHolderType.PHONE_NUMBER ->
          QuestionnaireItemPhoneNumberViewHolderFactory
      }

    val layout = LayoutInflater.from(parent.context).inflate(resId, parent as ViewGroup, false)

    val viewGroup = layout.findViewById<LinearLayout>(R.id.repeat_layout)

    viewGroup.addView(
      ViewProvider.getView(parent, viewHolderFactory2.resId),
      viewGroup.childCount - 1
    )

    val delegate = getQuestionnaireItemViewHolderDelegate()

    viewHolderFactory2.create(viewGroup, delegate)

    return viewHolderFactory2.create(viewGroup, delegate)
  }

  private fun getQuestionnaireDelegate(): QuestionnaireItemViewHolderDelegate {
    // TODO pass viewType as a parameter
    return when (QuestionnaireItemViewHolderType.fromInt(newViewType)) {
      QuestionnaireItemViewHolderType.GROUP ->
        QuestionnaireItemGroupViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
      QuestionnaireItemViewHolderType.BOOLEAN_TYPE_PICKER ->
        QuestionnaireItemBooleanTypePickerViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
      QuestionnaireItemViewHolderType.DATE_PICKER ->
        QuestionnaireItemDatePickerViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
      QuestionnaireItemViewHolderType.DATE_TIME_PICKER ->
        QuestionnaireItemDateTimePickerViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
      QuestionnaireItemViewHolderType.EDIT_TEXT_SINGLE_LINE ->
        QuestionnaireItemEditTextSingleLineViewHolderFactory
          .getQuestionnaireItemViewHolderDelegate()
      QuestionnaireItemViewHolderType.EDIT_TEXT_MULTI_LINE ->
        QuestionnaireItemEditTextMultiLineViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
      QuestionnaireItemViewHolderType.EDIT_TEXT_INTEGER ->
        QuestionnaireItemEditTextIntegerViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
      QuestionnaireItemViewHolderType.EDIT_TEXT_DECIMAL ->
        QuestionnaireItemEditTextDecimalViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
      QuestionnaireItemViewHolderType.RADIO_GROUP ->
        QuestionnaireItemRadioGroupViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
      QuestionnaireItemViewHolderType.DROP_DOWN ->
        QuestionnaireItemDropDownViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
      QuestionnaireItemViewHolderType.DISPLAY ->
        QuestionnaireItemDisplayViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
      QuestionnaireItemViewHolderType.QUANTITY ->
        QuestionnaireItemEditTextQuantityViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
      QuestionnaireItemViewHolderType.CHECK_BOX_GROUP ->
        QuestionnaireItemCheckBoxGroupViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
      QuestionnaireItemViewHolderType.AUTO_COMPLETE ->
        QuestionnaireItemAutoCompleteViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
      QuestionnaireItemViewHolderType.DIALOG_SELECT ->
        QuestionnaireItemDialogSelectViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
      QuestionnaireItemViewHolderType.SLIDER ->
        QuestionnaireItemSliderViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
      QuestionnaireItemViewHolderType.PHONE_NUMBER ->
        QuestionnaireItemPhoneNumberViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
    }
  }

  override fun getQuestionnaireItemViewHolderDelegate(): QuestionnaireItemViewHolderDelegate =
    object : QuestionnaireItemViewHolderDelegate {

      var childDelegate: QuestionnaireItemViewHolderDelegate = getQuestionnaireDelegate()
      var addButton: Button? = null
      var removeButton: Button? = null
      override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

      override fun init(itemView: View) {
        childDelegate.init(itemView)
        addButton = itemView.findViewById(R.id.btn_add_view)
        removeButton = itemView.findViewById(R.id.btn_remove_view)
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem, position: Int) {
        this.questionnaireItemViewItem = questionnaireItemViewItem
        childDelegate.questionnaireItemViewItem = questionnaireItemViewItem
        if (itemAdapter.currentList
            .filter {
              it.questionnaireItem.linkId == questionnaireItemViewItem.questionnaireItem.linkId
            }
            .size > 1
        ) {
          removeButton!!.visibility = View.VISIBLE
          addButton!!.visibility = View.GONE
        } else {
          removeButton!!.visibility = View.GONE
          addButton!!.visibility = View.VISIBLE
        }
        addButton!!.text = "Add ${questionnaireItemViewItem.questionnaireItem.localizedTextSpanned}"
        addButton!!.setOnClickListener {
          removeButton!!.visibility = View.VISIBLE
          itemAdapter.addItem(questionnaireItemViewItem, position)
        }
        removeButton!!.setOnClickListener {
          itemAdapter.removeItem(position)
          removeButton!!.visibility = View.GONE
        }

        childDelegate.bind(questionnaireItemViewItem, position)
      }

      override fun displayValidationResult(validationResult: ValidationResult) {

        childDelegate.displayValidationResult(validationResult)
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        childDelegate.setReadOnly(isReadOnly)
      }
    }
}
