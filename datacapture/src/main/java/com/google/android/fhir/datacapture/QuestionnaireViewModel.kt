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

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemComponent
import org.hl7.fhir.r4.model.StringType

class QuestionnaireViewModel(state: SavedStateHandle) : ViewModel() {
    /** The current questionnaire as questions are being answered. */
    internal var questionnaire: Questionnaire

    /** The current questionnaire response as questions are being answered. */
    internal val questionnaireResponse = QuestionnaireResponse()

    /** The list of [QuestionnaireItemViewItem] to be used for the [RecyclerView]. */
    internal val questionnaireItemViewItemList = mutableListOf<QuestionnaireItemViewItem>()

    init {

        questionnaire = state.get<Questionnaire>("questionnaire")!!
        questionnaireResponse.questionnaire = questionnaire.id
        // Retain the hierarchy and order of items within the questionnaire as specified in the
        // standard. See https://www.hl7.org/fhir/questionnaireresponse.html#notes.
        questionnaire.item.forEach {
            questionnaireResponse.item.add(it.createQuestionnaireResponseItem())
        }
        populateQuestionnaireItemViewItemList(
            questionnaireItemViewItemList,
            questionnaire.item,
            questionnaireResponse.item
        )
    }

    /**
     * Traverse (DFS) through the list of questionnaire items and the list of questionnaire response
     * items and populate [questionnaireItemViewItemList] with matching pairs of questionnaire item
     * and questionnaire response item.
     *
     * The traverse is carried out in the two lists in tandem. The two lists should be structurally
     * identical.
     */
    private fun populateQuestionnaireItemViewItemList(
      questionnaireItemViewItemList: MutableList<QuestionnaireItemViewItem>,
      questionnaireItemList: List<QuestionnaireItemComponent>,
      questionnaireResponseItemList: List<QuestionnaireResponseItemComponent>
    ) {
        val questionnaireItemListIterator = questionnaireItemList.iterator()
        val questionnaireResponseItemListIterator = questionnaireResponseItemList.iterator()
        while (questionnaireItemListIterator.hasNext() &&
            questionnaireResponseItemListIterator.hasNext()) {
            val questionnaireItem = questionnaireItemListIterator.next()
            val questionnaireResponseItem = questionnaireResponseItemListIterator.next()
            questionnaireItemViewItemList.add(
                QuestionnaireItemViewItem(questionnaireItem, questionnaireResponseItem)
            )
            populateQuestionnaireItemViewItemList(
                questionnaireItemViewItemList,
                questionnaireItem.item,
                questionnaireResponseItem.item
            )
        }
    }
}

class QuestionnaireViewModelFactory(
  owner: SavedStateRegistryOwner,
  defaultArgs: Bundle?
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel?> create(
      key: String,
      modelClass: Class<T>,
      handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(QuestionnaireViewModel::class.java)) {
            return QuestionnaireViewModel(handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * Creates a [QuestionnaireResponse.QuestionnaireResponseItemComponent] from the provided
 * [Questionnaire.QuestionnaireItemComponent].
 *
 * The hierarchy and order of child items will be retained as specified in the standard. See
 * https://www.hl7.org/fhir/questionnaireresponse.html#notes for more details.
 */
private fun QuestionnaireItemComponent.createQuestionnaireResponseItem():
    QuestionnaireResponse.QuestionnaireResponseItemComponent {
    return QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType(linkId)).apply {
        this@createQuestionnaireResponseItem.item.forEach {
            this.item.add(it.createQuestionnaireResponseItem())
        }
    }
}
