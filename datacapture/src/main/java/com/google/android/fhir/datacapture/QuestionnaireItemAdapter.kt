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

package com.google.android.fhir.datacapture

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.datacapture.views.QuestionnaireItemCheckBoxViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemDatePickerViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemEditTextDecimalViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemEditTextIntegerViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemEditTextMultiLineViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemEditTextSingleLineViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemGroupViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewHolder
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem
import com.google.fhir.r4.core.QuestionnaireItemTypeCode

internal class QuestionnaireItemAdapter(
    private val questionnaireItemViewItemList: List<QuestionnaireItemViewItem>
) : RecyclerView.Adapter<QuestionnaireItemViewHolder>() {
    /**
     * @param viewType the integer value of the [QuestionnaireItemViewHolderType] used to render the
     * [QuestionnaireItemComponent].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionnaireItemViewHolder {
        val viewHolder = when (QuestionnaireItemViewHolderType.fromInt(viewType)) {
            QuestionnaireItemViewHolderType.GROUP -> QuestionnaireItemGroupViewHolderFactory
            QuestionnaireItemViewHolderType.CHECK_BOX -> QuestionnaireItemCheckBoxViewHolderFactory
            QuestionnaireItemViewHolderType.DATE_PICKER ->
                QuestionnaireItemDatePickerViewHolderFactory
            QuestionnaireItemViewHolderType.EDIT_TEXT_SINGLE_LINE ->
                QuestionnaireItemEditTextSingleLineViewHolderFactory
            QuestionnaireItemViewHolderType.EDIT_TEXT_MULTI_LINE ->
                QuestionnaireItemEditTextMultiLineViewHolderFactory
            QuestionnaireItemViewHolderType.EDIT_TEXT_INTEGER ->
                QuestionnaireItemEditTextIntegerViewHolderFactory
            QuestionnaireItemViewHolderType.EDIT_TEXT_DECIMAL ->
                QuestionnaireItemEditTextDecimalViewHolderFactory
        }
        return viewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: QuestionnaireItemViewHolder, position: Int) {
        holder.bind(questionnaireItemViewItemList[position])
    }

    /**
     * Returns the integer value of the [QuestionnaireItemViewHolderType] that will be used to
     * render the [QuestionnaireItemComponent]. This is determined by a combination of the data type
     * of the question and any additional Questionnaire Item UI Control Codes
     * (http://hl7.org/fhir/R4/valueset-questionnaire-item-control.html) used in the
     * itemControl extension (http://hl7.org/fhir/R4/extension-questionnaire-itemcontrol.html).
     */
    override fun getItemViewType(position: Int) =
        when (val type = questionnaireItemViewItemList[position].questionnaireItem.type.value) {
            QuestionnaireItemTypeCode.Value.GROUP -> QuestionnaireItemViewHolderType.GROUP
            QuestionnaireItemTypeCode.Value.BOOLEAN -> QuestionnaireItemViewHolderType.CHECK_BOX
            QuestionnaireItemTypeCode.Value.DATE -> QuestionnaireItemViewHolderType.DATE_PICKER
            QuestionnaireItemTypeCode.Value.STRING ->
                QuestionnaireItemViewHolderType.EDIT_TEXT_SINGLE_LINE
            QuestionnaireItemTypeCode.Value.TEXT ->
                QuestionnaireItemViewHolderType.EDIT_TEXT_MULTI_LINE
            QuestionnaireItemTypeCode.Value.INTEGER ->
                QuestionnaireItemViewHolderType.EDIT_TEXT_INTEGER
            QuestionnaireItemTypeCode.Value.DECIMAL ->
                QuestionnaireItemViewHolderType.EDIT_TEXT_DECIMAL
            else -> throw NotImplementedError("Question type $type not supported.")
        }.value

    override fun getItemCount() = questionnaireItemViewItemList.size
}
