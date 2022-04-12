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

import android.view.View
import android.widget.Button
import com.google.android.fhir.datacapture.QuestionnaireItemViewHolderType
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.contrib.views.QuestionnaireItemPhoneNumberViewHolderFactory
import com.google.android.fhir.datacapture.localizedPrefixSpanned
import com.google.android.fhir.datacapture.validation.ValidationResult

object RepeatViewHolderFactory : QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_repeats) {



  private fun getQuestionnaireDelegate(): QuestionnaireItemViewHolderDelegate {
      //TODO pass viewType as a parameter
    return when (QuestionnaireItemViewHolderType.fromInt(newViewType)) {
        QuestionnaireItemViewHolderType.GROUP -> QuestionnaireItemGroupViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
        QuestionnaireItemViewHolderType.BOOLEAN_TYPE_PICKER ->
            QuestionnaireItemBooleanTypePickerViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
        QuestionnaireItemViewHolderType.DATE_PICKER ->
            QuestionnaireItemDatePickerViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
        QuestionnaireItemViewHolderType.DATE_TIME_PICKER ->
            QuestionnaireItemDateTimePickerViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
        QuestionnaireItemViewHolderType.EDIT_TEXT_SINGLE_LINE ->
            QuestionnaireItemEditTextSingleLineViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
        QuestionnaireItemViewHolderType.EDIT_TEXT_MULTI_LINE ->
            QuestionnaireItemEditTextMultiLineViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
        QuestionnaireItemViewHolderType.EDIT_TEXT_INTEGER ->
            QuestionnaireItemEditTextIntegerViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
        QuestionnaireItemViewHolderType.EDIT_TEXT_DECIMAL ->
            QuestionnaireItemEditTextDecimalViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
        QuestionnaireItemViewHolderType.RADIO_GROUP ->
            QuestionnaireItemRadioGroupViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
        QuestionnaireItemViewHolderType.DROP_DOWN -> QuestionnaireItemDropDownViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
        QuestionnaireItemViewHolderType.DISPLAY -> QuestionnaireItemDisplayViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
        QuestionnaireItemViewHolderType.QUANTITY ->
            QuestionnaireItemEditTextQuantityViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
        QuestionnaireItemViewHolderType.CHECK_BOX_GROUP ->
            QuestionnaireItemCheckBoxGroupViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
        QuestionnaireItemViewHolderType.AUTO_COMPLETE ->
            QuestionnaireItemAutoCompleteViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
        QuestionnaireItemViewHolderType.DIALOG_SELECT ->
            QuestionnaireItemDialogSelectViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
        QuestionnaireItemViewHolderType.SLIDER -> QuestionnaireItemSliderViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
        QuestionnaireItemViewHolderType.PHONE_NUMBER ->
            QuestionnaireItemPhoneNumberViewHolderFactory.getQuestionnaireItemViewHolderDelegate()
    }
  }

    override fun getQuestionnaireItemViewHolderDelegate(): QuestionnaireItemViewHolderDelegate = object : QuestionnaireItemViewHolderDelegate {

            var childDelegate : QuestionnaireItemViewHolderDelegate = getQuestionnaireDelegate()
            var addButton : Button? = null
            var removeButton: Button? = null
            override var questionnaireItemViewItem: QuestionnaireItemViewItem
                get() {
                    return  childDelegate.questionnaireItemViewItem
                }
                set(value) {}


        override fun init(itemView: View) {
                childDelegate.init(itemView)
                addButton = itemView.findViewById(R.id.btn_add_view)
            }

            override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem, position: Int) {
                addButton!!.text = questionnaireItemViewItem.questionnaireItem.localizedPrefixSpanned
                addButton!!.setOnClickListener {
                    itemAdapter.addItem(questionnaireItemViewItem, position)
                }
                // When add is clicked, add copy the item with the same id as the next

//                removeButton?.setOnClickListener()

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
