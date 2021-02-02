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

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.getDisplayString
import com.google.android.fhir.datacapture.getResponseAnswerValueX
import com.google.android.material.textfield.TextInputLayout
import com.google.fhir.r4.core.QuestionnaireResponse

object QuestionnaireItemDropDownViewHolderFactory : QuestionnaireItemViewHolderFactory(
    R.layout.questionnaire_item_drop_down_view
) {
    override fun getQuestionnaireItemViewHolderDelegate() =
        object : QuestionnaireItemViewHolderDelegate {
            private lateinit var textInputLayout: TextInputLayout
            private lateinit var autoCompleteTextView: AutoCompleteTextView
            private lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem
            private lateinit var context: Context

            override fun init(itemView: View) {
                textInputLayout = itemView.findViewById(R.id.dropdown_menu)
                autoCompleteTextView = itemView.findViewById(R.id.exposed_dropdown_menu)
                context = itemView.context
            }

            override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
                this.questionnaireItemViewItem = questionnaireItemViewItem
                textInputLayout.hint = questionnaireItemViewItem.questionnaireItem.text.value
                val answerOptionString = arrayListOf<String>()
                this.questionnaireItemViewItem.questionnaireItem.answerOptionList.forEach {
                    answerOptionString.add(it.getDisplayString())
                }
                val adapter = ArrayAdapter(
                    context,
                    R.layout.questionnaire_item_drop_down_list,
                    answerOptionString
                )
                autoCompleteTextView.setText(
                    questionnaireItemViewItem.singleAnswerOrNull?.value?.coding?.display?.value?:"")
                autoCompleteTextView.setAdapter(adapter)
                autoCompleteTextView.onItemClickListener =
                    object : AdapterView.OnItemClickListener {
                        override fun onItemClick(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            questionnaireItemViewItem.singleAnswerOrNull =
                                QuestionnaireResponse.Item.Answer.newBuilder()
                                    .setValue(
                                        questionnaireItemViewItem
                                            .questionnaireItem
                                            .answerOptionList[position].getResponseAnswerValueX()
                                    )
                        }
                    }
            }
        }
}
