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
import com.google.android.fhir.datacapture.enablement.EnablementEvaluator
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem
import com.google.fhir.common.JsonFormat
import com.google.fhir.r4.core.Canonical
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireItemTypeCode
import com.google.fhir.r4.core.QuestionnaireResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

internal class QuestionnaireViewModel(state: SavedStateHandle) : ViewModel() {
    /** The current questionnaire as questions are being answered. */
    private val questionnaire: Questionnaire
    init {
        val questionnaireJson: String = state[QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE]!!
        val builder = Questionnaire.newBuilder()
        questionnaire = JsonFormat.getParser().merge(questionnaireJson, builder).build()
    }

    /** The current questionnaire response as questions are being answered. */
    private lateinit var questionnaireResponseBuilder: QuestionnaireResponse.Builder

    init {
        val questionnaireJsonResponseString: String? =
            state[QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE_RESPONSE]
        if (questionnaireJsonResponseString != null) {
            questionnaireResponseBuilder = QuestionnaireResponse.newBuilder()
            val questionnaireResponse =
                JsonFormat.getParser()
                    .merge(questionnaireJsonResponseString, questionnaireResponseBuilder)
                    .build()
            validateQuestionniareResponseItems(
                questionnaire.itemList, questionnaireResponse.itemList
            )
            questionnaireResponseBuilder = questionnaireResponse.toBuilder()
        } else {
            questionnaireResponseBuilder = QuestionnaireResponse.newBuilder()
            questionnaireResponseBuilder.questionnaire =
                Canonical.newBuilder().setValue(questionnaire.id.value).build()
            // Retain the hierarchy and order of items within the questionnaire as specified in the
            // standard. See https://www.hl7.org/fhir/questionnaireresponse.html#notes.
            questionnaire.itemList.forEach {
                questionnaireResponseBuilder.addItem(it.createQuestionnaireResponseItem())
            }
        }
    }

    /** Map from link IDs to questionnaire response items. */
    private val linkIdToQuestionnaireResponseItemMap =
        createLinkIdToQuestionnaireResponseItemMap(
            questionnaireResponseBuilder.itemBuilderList
        )

    /** Tracks modifications in order to update the UI. */
    private val modificationCount = MutableStateFlow(0)

    /** Callback function to update the UI. */
    private val questionnaireResponseItemChangedCallback = { modificationCount.value += 1 }

    internal val questionnaireItemViewItemList
        get() = getQuestionnaireItemViewItemList(
            questionnaire.itemList,
            questionnaireResponseBuilder.itemBuilderList
        )

    /** [QuestionnaireItemViewItem]s to be displayed in the UI. */
    internal val questionnaireItemViewItemListFlow: Flow<List<QuestionnaireItemViewItem>> =
        modificationCount.map { questionnaireItemViewItemList }

    /** The current [QuestionnaireResponse] captured by the UI. */
    fun getQuestionnaireResponse(): QuestionnaireResponse = questionnaireResponseBuilder.build()

    private fun createLinkIdToQuestionnaireResponseItemMap(
        questionnaireResponseItemList: List<QuestionnaireResponse.Item.Builder>
    ): Map<String, QuestionnaireResponse.Item.Builder> {
        val linkIdToQuestionnaireResponseItemMap = questionnaireResponseItemList.map {
            it.linkId.value to it
        }.toMap().toMutableMap()
        for (item in questionnaireResponseItemList) {
            linkIdToQuestionnaireResponseItemMap.putAll(
                createLinkIdToQuestionnaireResponseItemMap(item.itemBuilderList)
            )
        }
        return linkIdToQuestionnaireResponseItemMap
    }

    /**
     * Traverse (DFS) through the list of questionnaire items , the list of questionnaire response
     * items and the list of items in the questionnaire response answer list and populate
     * [questionnaireItemViewItemList] with matching pairs of questionnaire item
     * and questionnaire response item.
     *
     * The traverse is carried out in the two lists in tandem. The two lists should be structurally
     * identical.
     */
    private fun getQuestionnaireItemViewItemList(
        questionnaireItemList: List<Questionnaire.Item>,
        questionnaireResponseItemList: List<QuestionnaireResponse.Item.Builder>
    ): List<QuestionnaireItemViewItem> {
        val questionnaireItemViewItemList = mutableListOf<QuestionnaireItemViewItem>()
        val questionnaireItemListIterator = questionnaireItemList.iterator()
        val questionnaireResponseItemListIterator = questionnaireResponseItemList.iterator()
        while (
            questionnaireItemListIterator.hasNext() &&
            questionnaireResponseItemListIterator.hasNext()
        ) {
            val questionnaireItem = questionnaireItemListIterator.next()
            val questionnaireResponseItem = questionnaireResponseItemListIterator.next()

            val enabled = EnablementEvaluator.evaluate(questionnaireItem) {
                (linkIdToQuestionnaireResponseItemMap[it] ?: return@evaluate null).build()
            }
            if (enabled) {
                questionnaireItemViewItemList.add(
                    QuestionnaireItemViewItem(
                        questionnaireItem,
                        questionnaireResponseItem,
                        questionnaireResponseItemChangedCallback
                    )
                )
                questionnaireItemViewItemList.addAll(
                    getQuestionnaireItemViewItemList(
                        questionnaireItem.itemList,
                        questionnaireResponseItem.itemBuilderList
                    )
                )
                if (!questionnaireItem.type.value.equals(QuestionnaireItemTypeCode.Value.GROUP)) {
                    questionnaireResponseItem.answerBuilderList?.forEach {
                        if (it.itemCount> 0) {
                            questionnaireItemViewItemList.addAll(
                                getQuestionnaireItemViewItemList(
                                    questionnaireItem.itemList,
                                    it.itemBuilderList
                                )
                            )
                        }
                    }
                }
            }
        }
        return questionnaireItemViewItemList
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

/**
 * Traverse (DFS) through the list of questionnaire items and the list of questionnaire response
 * items and check if the linkid of the matching pairs of questionnaire item and questionnaire
 * response item are equal.
 * The traverse is carried out in the two lists in tandem. The two lists should be structurally
 * identical.
 */
private fun validateQuestionniareResponseItems(
    questionnaireItemList: List<Questionnaire.Item>,
    questionnaireResponseItemList: List<QuestionnaireResponse.Item>
) {
    val questionnaireItemListIterator = questionnaireItemList.iterator()
    val questionnaireResponseItemListIterator = questionnaireResponseItemList.iterator()
    while (
        questionnaireItemListIterator.hasNext() &&
        questionnaireResponseItemListIterator.hasNext()
    ) {
        // TODO: Validate type and item nesting within answers for repeated answers https://github.com/google/android-fhir/issues/286
        val questionnaireItem = questionnaireItemListIterator.next()
        val questionnaireResponseItem = questionnaireResponseItemListIterator.next()
        if (!questionnaireItem.linkId.equals(questionnaireResponseItem.linkId))
            throw IllegalArgumentException("linkId mismatch")
        if (questionnaireItem.type.value.equals(QuestionnaireItemTypeCode.Value.GROUP)) {
            validateQuestionniareResponseItems(
                questionnaireItem.itemList,
                questionnaireResponseItem.itemList
            )
        } else {
            validateQuestionniareResponseItems(
                questionnaireItem.itemList,
                questionnaireResponseItem.answerList.first().itemList
            )
        }
    }
    if (
        questionnaireItemListIterator.hasNext() xor
        questionnaireResponseItemListIterator.hasNext()
    )
        throw IllegalArgumentException("Structure mismatch")
}
