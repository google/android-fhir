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
import android.widget.TextView
import com.google.android.fhir.datacapture.QuestionnaireResponseRecorder
import com.google.android.fhir.datacapture.R
import org.hl7.fhir.r4.model.Questionnaire

object QuestionnaireItemGroupViewHolderFactory : QuestionnaireItemViewHolderFactory {
    override fun create(
      parent: ViewGroup,
      questionnaireResponseRecorder: QuestionnaireResponseRecorder
    ): QuestionnaireItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.questionnaire_item_group_header_view, parent, false)
        return QuestionnaireItemGroupViewHolder(view, questionnaireResponseRecorder)
    }
}

private class QuestionnaireItemGroupViewHolder(
  itemView: View,
  questionnaireResponseRecorder: QuestionnaireResponseRecorder
) : QuestionnaireItemViewHolder(itemView, questionnaireResponseRecorder) {
    private val groupHeader = itemView.findViewById<TextView>(R.id.group_header)

    override fun bind(questionnaireItemComponent: Questionnaire.QuestionnaireItemComponent) {
        groupHeader.text = questionnaireItemComponent.text
    }
}
