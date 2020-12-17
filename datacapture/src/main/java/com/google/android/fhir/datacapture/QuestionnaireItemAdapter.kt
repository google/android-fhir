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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.google.android.fhir.datacapture.views.QuestionnaireItemCheckBoxViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemDatePickerViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemEditTextViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemGroupViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewHolder
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent

class QuestionnaireItemAdapter(
  val questionnaireResponseRecorder: QuestionnaireResponseRecorder
) : ListAdapter<QuestionnaireItemComponent, QuestionnaireItemViewHolder>(
    QuestionDiffCallback
) {
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
            QuestionnaireItemViewHolderType.EDIT_TEXT -> QuestionnaireItemEditTextViewHolderFactory
        }
        return viewHolder.create(parent, questionnaireResponseRecorder)
    }

    override fun onBindViewHolder(holder: QuestionnaireItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * Returns the integer value of the [QuestionnaireItemViewHolderType] that will be used to
     * render the [QuestionnaireItemComponent]. This is determined by a combination of the data type
     * of the question and any additional Questionnaire Item UI Control Codes
     * (http://hl7.org/fhir/R4/valueset-questionnaire-item-control.html) used in the
     * itemControl extension (http://hl7.org/fhir/R4/extension-questionnaire-itemcontrol.html).
     */
    override fun getItemViewType(position: Int) = when (val type = currentList[position].type) {
        Questionnaire.QuestionnaireItemType.GROUP -> QuestionnaireItemViewHolderType.GROUP
        Questionnaire.QuestionnaireItemType.BOOLEAN -> QuestionnaireItemViewHolderType.CHECK_BOX
        Questionnaire.QuestionnaireItemType.DATE -> QuestionnaireItemViewHolderType.DATE_PICKER
        Questionnaire.QuestionnaireItemType.STRING -> QuestionnaireItemViewHolderType.EDIT_TEXT
        else -> throw NotImplementedError("Question type $type not supported.")
    }.value
}

private object QuestionDiffCallback : DiffUtil.ItemCallback<QuestionnaireItemComponent>() {
    override fun areItemsTheSame(
      oldItem: QuestionnaireItemComponent,
      newItem: QuestionnaireItemComponent
    ) = oldItem.linkId.contentEquals(newItem.linkId)

    override fun areContentsTheSame(
      oldItem: QuestionnaireItemComponent,
      newItem: QuestionnaireItemComponent
    ) = oldItem.equalsDeep(newItem)
}
