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
import ca.uhn.fhir.context.FhirContext
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.StringType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Unit tests for {@link FhirIndexerImpl}. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class QuestionnaireViewModelTest {
    @Test
    fun questionnaireResponse_shouldCopyQuestionnaireId() {
        val questionnaire = Questionnaire()
        questionnaire.id = "a-questionnaire"
        val viewModel = QuestionnaireViewModel(questionnaire)
        assertResourceEquals(
            viewModel.questionnaireResponse,
            QuestionnaireResponse().apply { id = "a-questionnaire" }
        )
    }

    @Test
    fun questionnaireResponse_booleanQuestion_shouldCopyQuestion() {
        val questionnaire = Questionnaire()
        val item = Questionnaire.QuestionnaireItemComponent()
        item.linkId = "a-link-id"
        item.text = "Yes or no?"
        item.type = Questionnaire.QuestionnaireItemType.BOOLEAN
        questionnaire.addItem(item)
        val viewModel = QuestionnaireViewModel(questionnaire)
        assertResourceEquals(
            viewModel.questionnaireResponse,
            QuestionnaireResponse().apply {
                val item = QuestionnaireResponse.QuestionnaireResponseItemComponent()
                item.linkId = "a-link-id"
                item.text = "Yes or no?"
                addItem(item)
            }
        )
    }

    @Test
    fun questionnaireResponse_booleanQuestion_setAnswer_shouldSetAnswer() {
        val questionnaire = Questionnaire()
        val item = Questionnaire.QuestionnaireItemComponent()
        item.linkId = "a-link-id"
        item.text = "Yes or no?"
        item.type = Questionnaire.QuestionnaireItemType.BOOLEAN
        questionnaire.addItem(item)
        val viewModel = QuestionnaireViewModel(questionnaire)
        viewModel.recordAnswer("a-link-id", true)
        assertResourceEquals(
            viewModel.questionnaireResponse,
            QuestionnaireResponse().apply {
                val item = QuestionnaireResponse.QuestionnaireResponseItemComponent()
                item.linkId = "a-link-id"
                val answer = QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                answer.value = BooleanType(true)
                item.text = "Yes or no?"
                item.answer = listOf(answer)
                addItem(item)
            }
        )
    }

    @Test
    fun questionnaireResponse_stringQuestion_shouldCopyQuestion() {
        val questionnaire = Questionnaire()
        val item = Questionnaire.QuestionnaireItemComponent()
        item.linkId = "a-link-id"
        item.text = "Name?"
        item.type = Questionnaire.QuestionnaireItemType.STRING
        questionnaire.addItem(item)
        val viewModel = QuestionnaireViewModel(questionnaire)
        assertResourceEquals(
            viewModel.questionnaireResponse,
            QuestionnaireResponse().apply {
                val item = QuestionnaireResponse.QuestionnaireResponseItemComponent()
                item.linkId = "a-link-id"
                item.text = "Name?"
                addItem(item)
            }
        )
    }

    @Test
    fun questionnaireResponse_stringQuestion_setAnswer_shouldSetAnswer() {
        val questionnaire = Questionnaire()
        val item = Questionnaire.QuestionnaireItemComponent()
        item.linkId = "a-link-id"
        item.text = "Name?"
        item.type = Questionnaire.QuestionnaireItemType.STRING
        questionnaire.addItem(item)
        val viewModel = QuestionnaireViewModel(questionnaire)
        viewModel.recordAnswer("a-link-id", "John")
        assertResourceEquals(
            viewModel.questionnaireResponse,
            QuestionnaireResponse().apply {
                val item = QuestionnaireResponse.QuestionnaireResponseItemComponent()
                item.linkId = "a-link-id"
                val answer = QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                answer.value = StringType("John")
                item.text = "Name?"
                item.answer = listOf(answer)
                addItem(item)
            }
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
