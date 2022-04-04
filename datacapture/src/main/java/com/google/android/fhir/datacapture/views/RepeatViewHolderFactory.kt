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

import android.view.ViewGroup
import com.google.android.fhir.datacapture.QuestionnaireItemViewHolderType
import com.google.android.fhir.datacapture.contrib.views.QuestionnaireItemPhoneNumberViewHolderFactory

internal object RepeatViewHolderFactory {

  fun create(parent: ViewGroup, viewType: Int): QuestionnaireItemViewHolder {
    val viewHolderFactory =
      when (QuestionnaireItemViewHolderType.fromInt(viewType)) {
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
        else -> throw NotImplementedError("Question type $viewType not supported.")
      }

    // Add the - button
    if (parent.tag == "first") {
      // Add the + button
    }

    return viewHolderFactory.create(parent)
  }
}
