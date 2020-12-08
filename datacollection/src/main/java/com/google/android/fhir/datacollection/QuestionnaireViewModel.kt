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

package com.google.android.fhir.datacollection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType

class QuestionnaireViewModel(val questionnaire: Questionnaire) : ViewModel() {
    private val responseItemMap =
        mutableMapOf<String, QuestionnaireResponse.QuestionnaireResponseItemComponent>()
    internal val questionnaireResponse = QuestionnaireResponse()
    init {
        questionnaireResponse.id = questionnaire.id
    }

    init {
        questionnaire.item.forEach {
            createQuestionnaireResponseItemComponent(it, questionnaireResponse.item)
        }
    }

    fun setAnswer(linkId: String, answer: Boolean) {
        responseItemMap[linkId]?.answer = listOf(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                .setValue(BooleanType(answer))
        )
    }

    fun setAnswer(linkId: String, answer: String) {
        responseItemMap[linkId]?.answer = listOf(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                .setValue(StringType(answer))
        )
    }

    private fun createQuestionnaireResponseItemComponent(
      questionnaireItemComponent: Questionnaire.QuestionnaireItemComponent,
      questionnaireResponseItemComponentList:
      MutableList<QuestionnaireResponse.QuestionnaireResponseItemComponent>
    ) {
        when (questionnaireItemComponent.type) {
            Questionnaire.QuestionnaireItemType.BOOLEAN -> {
                val questionnaireResponseItemComponent =
                    QuestionnaireResponse.QuestionnaireResponseItemComponent(
                        StringType(questionnaireItemComponent.linkId))
                questionnaireResponseItemComponent.text = questionnaireItemComponent.text
                questionnaireResponseItemComponentList.add(questionnaireResponseItemComponent)
                responseItemMap[questionnaireItemComponent.linkId] =
                    questionnaireResponseItemComponent
            }
            Questionnaire.QuestionnaireItemType.STRING -> {
                val questionnaireResponseItemComponent =
                    QuestionnaireResponse.QuestionnaireResponseItemComponent(
                        StringType(questionnaireItemComponent.linkId))
                questionnaireResponseItemComponent.text = questionnaireItemComponent.text
                questionnaireResponseItemComponentList.add(questionnaireResponseItemComponent)
                responseItemMap[questionnaireItemComponent.linkId] =
                    questionnaireResponseItemComponent
            }
            Questionnaire.QuestionnaireItemType.GROUP -> {
                val questionnaireResponseItemComponent =
                    QuestionnaireResponse.QuestionnaireResponseItemComponent(
                        StringType(questionnaireItemComponent.linkId))
                questionnaireResponseItemComponent.text = questionnaireItemComponent.text
                questionnaireResponseItemComponent.item = mutableListOf()
                questionnaireItemComponent.item.forEach {
                    createQuestionnaireResponseItemComponent(it,
                        questionnaireResponseItemComponent.item)
                }
                questionnaireResponseItemComponentList.add(questionnaireResponseItemComponent)
                responseItemMap[questionnaireItemComponent.linkId] =
                    questionnaireResponseItemComponent
            }
            else -> {
                throw IllegalArgumentException(
                    "Unsupported item type ${questionnaireItemComponent.type}"
                )
            }
        }
    }
}

class QuestionnaireViewModelFactory(
  private val questionnaire: Questionnaire
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuestionnaireViewModel::class.java)) {
            return QuestionnaireViewModel(questionnaire) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
