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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import com.google.android.fhir.datacapture.R
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType

object QuestionnaireItemEditTextViewHolderFactory : QuestionnaireItemViewHolderFactory {
    override fun create(parent: ViewGroup): QuestionnaireItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.questionnaire_item_edit_text_view, parent, false)
        return QuestionnaireItemEditTextViewHolder(view)
    }
}

class QuestionnaireItemEditTextViewHolder(
  itemView: View
) : QuestionnaireItemViewHolder(itemView) {
    private val textView = itemView.findViewById<TextView>(R.id.text)
    private val editText = itemView.findViewById<EditText>(R.id.input)
    init {
        editText.doAfterTextChanged { editable: Editable? ->
            questionnaireItemViewItem.questionnaireResponseItemComponent.answer = listOf(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                    value = StringType(editable.toString())
                }
            )
        }
    }

    private lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

    override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        this.questionnaireItemViewItem = questionnaireItemViewItem
        textView.text = questionnaireItemViewItem.questionnaireItemComponent.text
    }
}
