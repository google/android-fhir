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
import android.view.View
import androidx.core.widget.doAfterTextChanged
import com.google.android.fhir.datacapture.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.fhir.r4.core.QuestionnaireResponse

abstract class QuestionnaireItemEditTextViewHolderFactory : QuestionnaireItemViewHolderFactory(
    R.layout.questionnaire_item_edit_text_view
) {
    abstract override fun getQuestionnaireItemViewHolderDelegate():
        QuestionnaireItemEditTextViewHolderDelegate
}

abstract class QuestionnaireItemEditTextViewHolderDelegate : QuestionnaireItemViewHolderDelegate {
    private lateinit var textInputLayout: TextInputLayout
    private lateinit var textInputEditText: TextInputEditText
    private lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

    override fun init(itemView: View) {
        textInputLayout = itemView.findViewById(R.id.textInputLayout)
        textInputEditText = itemView.findViewById(R.id.textInputEditText)
        textInputEditText.setRawInputType(getRawInputType())
        textInputEditText.doAfterTextChanged { editable: Editable? ->
            questionnaireItemViewItem.singleAnswerOrNull = getValue(editable.toString())
        }
    }

    override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        this.questionnaireItemViewItem = questionnaireItemViewItem
        textInputLayout.hint = questionnaireItemViewItem.questionnaireItem.text.value
        textInputEditText.setText(getText(questionnaireItemViewItem.singleAnswerOrNull))
    }

    /** Returns the answer that should be recorded given the text input by the user. */
    abstract fun getValue(text: String): QuestionnaireResponse.Item.Answer.Builder?

    /**
     * Returns the text that should be displayed in the [TextInputEditText] from the existing
     * answer to the question (may be input by the user or previously recorded).
     */
    abstract fun getText(answer: QuestionnaireResponse.Item.Answer.Builder?): String

    /**  Returns the raw input type of the [Editable] widget. */
    abstract fun getRawInputType(): Int
}
