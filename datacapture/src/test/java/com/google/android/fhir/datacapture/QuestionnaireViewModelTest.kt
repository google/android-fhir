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

import android.os.Build
import androidx.lifecycle.SavedStateHandle
import ca.uhn.fhir.context.FhirContext
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Resource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class QuestionnaireViewModelTest {
    private lateinit var state: SavedStateHandle
    @Before
    fun setUp() {
        state = SavedStateHandle()
    }

    @Test
    fun questionnaireResponse_shouldCopyQuestionnaireId() {
        val questionnaire = Questionnaire()
        // TODO: if id = a-questionnaire, the json parser sets questionnaire.id.myCoercedValue =
        // "Questionnaire/a-questionniare" when decoding which results in the test failing
        questionnaire.id = "Questionnaire/a-questionnaire"
        val serializedQuestionniare = parser.encodeResourceToString(questionnaire)
        state.set(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE, serializedQuestionniare)
        val viewModel = QuestionnaireViewModel(state)
        assertResourceEquals(
            viewModel.questionnaireResponse,
            QuestionnaireResponse().apply { this.questionnaire = "Questionnaire/a-questionnaire" }
        )
    }

    @Test
    fun questionnaireResponse_shouldCopyQuestion() {
        val questionnaire = Questionnaire()
        val item = Questionnaire.QuestionnaireItemComponent()
        item.linkId = "a-link-id"
        item.text = "Yes or no?"
        item.type = Questionnaire.QuestionnaireItemType.BOOLEAN
        questionnaire.addItem(item)
        val serializedQuestionniare = parser.encodeResourceToString(questionnaire)
        state.set(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE, serializedQuestionniare)
        val viewModel = QuestionnaireViewModel(state)
        assertResourceEquals(
            viewModel.questionnaireResponse,
            QuestionnaireResponse().apply {
                val item = QuestionnaireResponse.QuestionnaireResponseItemComponent()
                item.linkId = "a-link-id"
                addItem(item)
            }
        )
    }

    @Test
    fun questionnaireResponse_group_shouldCopyQuestionnaireStructure() {
        val questionnaire = Questionnaire()
        val group = Questionnaire.QuestionnaireItemComponent()
        group.linkId = "a-link-id"
        group.text = "Basic questions"
        group.type = Questionnaire.QuestionnaireItemType.GROUP
        questionnaire.addItem(group)
        val item = Questionnaire.QuestionnaireItemComponent()
        item.linkId = "another-link-id"
        item.text = "Name?"
        item.type = Questionnaire.QuestionnaireItemType.STRING
        group.item.add(item)
        val serializedQuestionniare = parser.encodeResourceToString(questionnaire)
        state.set(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE, serializedQuestionniare)
        val viewModel = QuestionnaireViewModel(state)
        assertResourceEquals(
            viewModel.questionnaireResponse,
            QuestionnaireResponse().apply {
                val group = QuestionnaireResponse.QuestionnaireResponseItemComponent()
                group.linkId = "a-link-id"
                addItem(group)
                val item = QuestionnaireResponse.QuestionnaireResponseItemComponent()
                item.linkId = "another-link-id"
                group.item.add(item)
            }
        )
    }

    @Test
    fun questionnaireItemViewItemList_shouldGenerateQuestionnaireItemViewItemList() {
        val questionnaire = Questionnaire()
        val group = Questionnaire.QuestionnaireItemComponent()
        group.linkId = "a-link-id"
        group.text = "Basic questions"
        group.type = Questionnaire.QuestionnaireItemType.GROUP
        questionnaire.addItem(group)
        val item = Questionnaire.QuestionnaireItemComponent()
        item.linkId = "another-link-id"
        item.text = "Name?"
        item.type = Questionnaire.QuestionnaireItemType.STRING
        group.item.add(item)
        val serializedQuestionniare = parser.encodeResourceToString(questionnaire)
        state.set(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE, serializedQuestionniare)
        val viewModel = QuestionnaireViewModel(state)
        val questionnaireItemViewItemList = viewModel.questionnaireItemViewItemList
        assertThat(questionnaireItemViewItemList.size).isEqualTo(2)
        val firstQuestionnaireItemViewItem = questionnaireItemViewItemList[0]
        assertThat(firstQuestionnaireItemViewItem.questionnaireItemComponent.linkId).isEqualTo(
            "a-link-id"
        )
        assertThat(firstQuestionnaireItemViewItem.questionnaireItemComponent.text).isEqualTo(
            "Basic questions"
        )
        assertThat(firstQuestionnaireItemViewItem.questionnaireItemComponent.type).isEqualTo(
            Questionnaire.QuestionnaireItemType.GROUP
        )
        assertThat(firstQuestionnaireItemViewItem.questionnaireResponseItemComponent.linkId)
            .isEqualTo(
            "a-link-id"
            )
        val secondQuestionnaireItemViewItem = questionnaireItemViewItemList[1]
        assertThat(secondQuestionnaireItemViewItem.questionnaireItemComponent.linkId).isEqualTo(
            "another-link-id"
        )
        assertThat(secondQuestionnaireItemViewItem.questionnaireItemComponent.text).isEqualTo(
            "Name?"
        )
        assertThat(secondQuestionnaireItemViewItem.questionnaireItemComponent.type).isEqualTo(
            Questionnaire.QuestionnaireItemType.STRING
        )
        assertThat(secondQuestionnaireItemViewItem.questionnaireResponseItemComponent.linkId)
            .isEqualTo(
            "another-link-id"
        )
    }

    private companion object {
        val parser = FhirContext.forR4().newJsonParser()
        fun assertResourceEquals(r1: Resource, r2: Resource) {
            assertThat(parser.encodeResourceToString(r1))
                .isEqualTo(parser.encodeResourceToString(r2))
        }
    }
}
