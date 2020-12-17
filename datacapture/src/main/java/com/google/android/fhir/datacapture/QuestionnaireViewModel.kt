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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType

class QuestionnaireViewModel(
  val questionnaire: Questionnaire
) : ViewModel(), QuestionnaireResponseRecorder {
    /**
     * A map from [QuestionnaireResponse.QuestionnaireResponseItemComponent.linkId] to
     * [QuestionnaireResponse.QuestionnaireResponseItemComponent] for quick access to record
     * answers.
     */
    private val responseItemMap =
        mutableMapOf<String, QuestionnaireResponse.QuestionnaireResponseItemComponent>()

    /** The current questionnaire response as questions are being answered. */
    internal val questionnaireResponse = QuestionnaireResponse()

    init {
        questionnaireResponse.questionnaire = questionnaire.id
        // Retain the hierarchy and order of items within the questionnaire as specified in the
        // standard. See https://www.hl7.org/fhir/questionnaireresponse.html#notes.
        questionnaire.item.forEach {
            questionnaireResponse.item.add(createQuestionnaireResponseItemComponent(it))
        }
    }

    override fun recordAnswer(linkId: String, answer: Boolean) {
        responseItemMap[linkId]?.answer = listOf(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(
                BooleanType(answer)
            )
        )
    }

    override fun recordAnswer(linkId: String, answer: String) {
        responseItemMap[linkId]?.answer = listOf(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(
                StringType(answer)
            )
        )
    }

    override fun recordAnswer(linkId: String, year: Int, month: Int, dayOfMonth: Int) {
        responseItemMap[linkId]?.answer = listOf(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(
                DateType(year, month, dayOfMonth)
            )
        )
    }

    /**
     * Creates a [QuestionnaireResponse.QuestionnaireResponseItemComponent] from the provided
     * [Questionnaire.QuestionnaireItemComponent] and adds it to the [responseItemMap] to be used
     * for receiving answers.
     *
     * The hierarchy and order of child items will be retained as specified in the standard. See
     * https://www.hl7.org/fhir/questionnaireresponse.html#notes for more details.
     */
    private fun createQuestionnaireResponseItemComponent(
      questionnaireItemComponent: Questionnaire.QuestionnaireItemComponent
    ): QuestionnaireResponse.QuestionnaireResponseItemComponent {
        val questionnaireResponseItemComponent =
            QuestionnaireResponse.QuestionnaireResponseItemComponent(
                StringType(questionnaireItemComponent.linkId))
        responseItemMap[questionnaireItemComponent.linkId] = questionnaireResponseItemComponent
        questionnaireItemComponent.item.forEach {
            questionnaireResponseItemComponent.item.add(
                createQuestionnaireResponseItemComponent(it))
        }
        return questionnaireResponseItemComponent
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
