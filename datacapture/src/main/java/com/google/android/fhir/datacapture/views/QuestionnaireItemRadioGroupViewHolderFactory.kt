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

import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.displayString
import com.google.android.fhir.datacapture.responseAnswerValueX
import com.google.fhir.r4.core.QuestionnaireResponse

object QuestionnaireItemRadioGroupViewHolderFactory : QuestionnaireItemViewHolderFactory(
    R.layout.questionnaire_item_radio_group_view
) {
    override fun getQuestionnaireItemViewHolderDelegate() =
        object : QuestionnaireItemViewHolderDelegate {
            private lateinit var radioHeader: TextView
            private lateinit var radioGroup: RadioGroup
            private lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

            override fun init(itemView: View) {
                radioGroup = itemView.findViewById(R.id.radio_group)
                radioHeader = itemView.findViewById(R.id.radio_header)
            }

            override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
                this.questionnaireItemViewItem = questionnaireItemViewItem
                val questionnaireItem = questionnaireItemViewItem.questionnaireItem
                val questionnaireResponseItemBuilder =
                    questionnaireItemViewItem.questionnaireResponseItemBuilder
                val answer =
                    questionnaireResponseItemBuilder.answerList.singleOrNull()?.value?.coding
                radioHeader.text = questionnaireItem.text.value
                radioGroup.removeAllViews()

                // TODO: support other answer types besides coding
                questionnaireItem.answerOptionList.forEach {
                    radioGroup.addView(RadioButton(radioGroup.context).apply {
                        text = it.displayString
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        if (questionnaireItemViewItem.singleAnswerOrNull
                                ?.value?.coding?.equals(answer) == true) {
                            this.isChecked = true
                        }
                        setOnCheckedChangeListener { buttonView, isChecked ->
                            if (isChecked) {
                                questionnaireResponseItemBuilder.clearAnswer().addAnswer(
                                    QuestionnaireResponse.Item.Answer.newBuilder().apply {
                                        value = it.responseAnswerValueX
                                    }
                                )
                            }
                        }
                    })
                }
            }
        }
}
