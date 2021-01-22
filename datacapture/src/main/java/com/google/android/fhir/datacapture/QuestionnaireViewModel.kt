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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem
import com.google.fhir.common.JsonFormat
import com.google.fhir.r4.core.Canonical
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse

class QuestionnaireViewModel(state: SavedStateHandle) : ViewModel() {
    /** The current questionnaire as questions are being answered. */
    internal var questionnaire: Questionnaire

    /** The current questionnaire response as questions are being answered. */
    internal val questionnaireResponseBuilder = QuestionnaireResponse.newBuilder()

    /** The list of [QuestionnaireItemViewItem] to be used for the [RecyclerView]. */
    internal val questionnaireItemViewItemList = mutableListOf<QuestionnaireItemViewItem>()

    init {
        val questionnaireJson: String = state[QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE]!!
        val builder = Questionnaire.newBuilder()
        questionnaire = JsonFormat.getParser().merge(questionnaireJson, builder).build()
        questionnaireResponseBuilder.questionnaire =
            Canonical.newBuilder().setValue(questionnaire.id.value).build()
        // Retain the hierarchy and order of items within the questionnaire as specified in the
        // standard. See https://www.hl7.org/fhir/questionnaireresponse.html#notes.
        questionnaire.itemList.forEach {
            questionnaireResponseBuilder.addItem(it.createQuestionnaireResponseItem())
        }
        populateQuestionnaireItemViewItemList(
            questionnaireItemViewItemList,
            questionnaire.itemList,
            questionnaireResponseBuilder.itemBuilderList
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
      questionnaireItemList: List<Questionnaire.Item>,
      questionnaireResponseItemList: List<QuestionnaireResponse.Item.Builder>
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
                questionnaireItem.itemList,
                questionnaireResponseItem.itemBuilderList
            )
        }
    }
}

/**
 * Creates a [QuestionnaireResponse.QuestionnaireResponseItemComponent] from the provided
 * [Questionnaire.QuestionnaireItemComponent].
 *
 * The hierarchy and order of child items will be retained as specified in the standard. See
 * https://www.hl7.org/fhir/questionnaireresponse.html#notes for more details.
 */
private fun Questionnaire.Item.createQuestionnaireResponseItem():
    QuestionnaireResponse.Item.Builder {
    return QuestionnaireResponse.Item.newBuilder().apply {
        linkId = com.google.fhir.r4.core.String.newBuilder()
            .setValue(this@createQuestionnaireResponseItem.linkId.value).build()
        this@createQuestionnaireResponseItem.itemList.forEach {
            this.addItem(it.createQuestionnaireResponseItem())
        }
    }
}
