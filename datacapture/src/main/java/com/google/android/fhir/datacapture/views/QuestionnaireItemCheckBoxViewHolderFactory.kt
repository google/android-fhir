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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.google.android.fhir.datacapture.QuestionnaireViewModel
import com.google.android.fhir.datacapture.R
import org.hl7.fhir.r4.model.Questionnaire

object QuestionnaireItemCheckBoxViewHolderFactory : QuestionnaireItemViewHolderFactory {
    override fun create(parent: ViewGroup, viewModel: QuestionnaireViewModel):
        QuestionnaireItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.questionnaire_item_check_box_view, parent, false)
        return QuestionnaireItemCheckBoxViewHolder(view, viewModel)
    }
}

private class QuestionnaireItemCheckBoxViewHolder(
  itemView: View,
  viewModel: QuestionnaireViewModel
) :
    QuestionnaireItemViewHolder(itemView, viewModel) {
    private val checkBox = itemView.findViewById<CheckBox>(R.id.check_box)

    override fun bind(questionnaireItemComponent: Questionnaire.QuestionnaireItemComponent) {
        checkBox.text = questionnaireItemComponent.text
        checkBox.setOnClickListener {
            viewModel.recordAnswer(questionnaireItemComponent.linkId, checkBox.isChecked)
        }
    }
}
