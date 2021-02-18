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
import com.google.common.truth.Truth.assertThat
import com.google.fhir.common.JsonFormat
import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.Canonical
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireItemTypeCode
import com.google.fhir.r4.core.QuestionnaireResponse
import com.google.fhir.r4.core.String
import com.google.fhir.shaded.protobuf.Message
import kotlin.test.assertFailsWith
import kotlinx.coroutines.runBlocking
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
        val questionnaire = Questionnaire.newBuilder().apply {
            // TODO: if id = a-questionnaire, the json parser sets questionnaire.id.myCoercedValue =
            // "Questionnaire/a-questionniare" when decoding which results in the test failing
            id = Id.newBuilder().setValue("a-questionnaire").build()
        }.build()
        val serializedQuestionniare = printer.print(questionnaire)
        state.set(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE, serializedQuestionniare)
        val viewModel = QuestionnaireViewModel(state)
        assertResourceEquals(
            viewModel.getQuestionnaireResponse(),
            QuestionnaireResponse.newBuilder().apply {
                this.questionnaire = Canonical.newBuilder().setValue("a-questionnaire").build()
            }.build()
        )
    }

    @Test
    fun questionnaireResponse_shouldCopyQuestion() {
        val questionnaire = Questionnaire.newBuilder().apply {
            id = Id.newBuilder().setValue("a-questionnaire").build()
            addItem(Questionnaire.Item.newBuilder().apply {
                linkId = String.newBuilder().setValue("a-link-id").build()
                text = String.newBuilder().setValue("Yes or no?").build()
                type = Questionnaire.Item.TypeCode.newBuilder()
                    .setValue(QuestionnaireItemTypeCode.Value.BOOLEAN).build()
            }.build())
        }.build()
        val serializedQuestionniare = printer.print(questionnaire)
        state.set(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE, serializedQuestionniare)
        val viewModel = QuestionnaireViewModel(state)
        assertResourceEquals(
            viewModel.getQuestionnaireResponse(),
            QuestionnaireResponse.newBuilder().apply {
                this.questionnaire = Canonical.newBuilder().setValue("a-questionnaire").build()
                addItem(QuestionnaireResponse.Item.newBuilder().apply {
                    linkId = String.newBuilder().setValue("a-link-id").build()
                }.build())
            }.build()
        )
    }

    @Test
    fun questionnaireResponse_group_shouldCopyQuestionnaireStructure() {
        val questionnaire = Questionnaire.newBuilder().apply {
            id = Id.newBuilder().setValue("a-questionnaire").build()
            addItem(Questionnaire.Item.newBuilder().apply {
                linkId = String.newBuilder().setValue("a-link-id").build()
                text = String.newBuilder().setValue("Basic questions").build()
                type = Questionnaire.Item.TypeCode.newBuilder()
                    .setValue(QuestionnaireItemTypeCode.Value.GROUP).build()
                addItem(Questionnaire.Item.newBuilder().apply {
                    linkId = String.newBuilder().setValue("another-link-id").build()
                    text = String.newBuilder().setValue("Name?").build()
                    type = Questionnaire.Item.TypeCode.newBuilder()
                        .setValue(QuestionnaireItemTypeCode.Value.STRING).build()
                })
            })
        }.build()
        val serializedQuestionniare = printer.print(questionnaire)
        state.set(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE, serializedQuestionniare)
        val viewModel = QuestionnaireViewModel(state)
        assertResourceEquals(
            viewModel.getQuestionnaireResponse(),
            QuestionnaireResponse.newBuilder().apply {
                this.questionnaire = Canonical.newBuilder().setValue("a-questionnaire").build()
                addItem(QuestionnaireResponse.Item.newBuilder().apply {
                    linkId = String.newBuilder().setValue("a-link-id").build()
                    addItem(QuestionnaireResponse.Item.newBuilder().apply {
                        linkId = String.newBuilder().setValue("another-link-id").build()
                    })
                })
            }.build()
        )
    }

    @Test
    fun questionnaireItemViewItemList_shouldGenerateQuestionnaireItemViewItemList() = runBlocking {
        val questionnaire = Questionnaire.newBuilder().apply {
            id = Id.newBuilder().setValue("a-questionnaire").build()
            addItem(Questionnaire.Item.newBuilder().apply {
                linkId = String.newBuilder().setValue("a-link-id").build()
                text = String.newBuilder().setValue("Basic questions").build()
                type = Questionnaire.Item.TypeCode.newBuilder()
                    .setValue(QuestionnaireItemTypeCode.Value.GROUP).build()
                addItem(Questionnaire.Item.newBuilder().apply {
                    linkId = String.newBuilder().setValue("another-link-id").build()
                    text = String.newBuilder().setValue("Name?").build()
                    type = Questionnaire.Item.TypeCode.newBuilder()
                        .setValue(QuestionnaireItemTypeCode.Value.STRING).build()
                })
            })
        }.build()
        val serializedQuestionnaire = printer.print(questionnaire)
        state.set(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE, serializedQuestionnaire)
        val viewModel = QuestionnaireViewModel(state)
        var questionnaireItemViewItemList = viewModel.questionnaireItemViewItemList
        questionnaireItemViewItemList[0].questionnaireResponseItemChangedCallback()
        assertThat(questionnaireItemViewItemList.size).isEqualTo(2)
        val firstQuestionnaireItemViewItem = questionnaireItemViewItemList[0]
        val firstQuestionnaireItem = firstQuestionnaireItemViewItem.questionnaireItem
        assertThat(firstQuestionnaireItem.linkId.value).isEqualTo("a-link-id")
        assertThat(firstQuestionnaireItem.text.value).isEqualTo("Basic questions")
        assertThat(firstQuestionnaireItem.type.value)
            .isEqualTo(QuestionnaireItemTypeCode.Value.GROUP)
        assertThat(firstQuestionnaireItemViewItem.questionnaireResponseItemBuilder.linkId.value)
            .isEqualTo("a-link-id")
        val secondQuestionnaireItemViewItem = questionnaireItemViewItemList[1]
        val secondQuestionnaireItem = secondQuestionnaireItemViewItem.questionnaireItem
        assertThat(secondQuestionnaireItem.linkId.value).isEqualTo("another-link-id")
        assertThat(secondQuestionnaireItem.text.value).isEqualTo("Name?")
        assertThat(secondQuestionnaireItem.type.value)
            .isEqualTo(QuestionnaireItemTypeCode.Value.STRING)
        assertThat(secondQuestionnaireItemViewItem.questionnaireResponseItemBuilder.linkId.value)
            .isEqualTo("another-link-id")
    }

    @Test
    fun validateQuestionnaireResponse_shouldReturnTrue() {
        val questionnaire = Questionnaire.newBuilder().apply {
            id = Id.newBuilder().setValue("a-questionnaire").build()
            addItem(Questionnaire.Item.newBuilder().apply {
                linkId = String.newBuilder().setValue("a-link-id").build()
                text = String.newBuilder().setValue("Basic question").build()
                type = Questionnaire.Item.TypeCode.newBuilder()
                    .setValue(QuestionnaireItemTypeCode.Value.BOOLEAN).build()
            })
        }.build()
        val questionnaireResponse = QuestionnaireResponse.newBuilder().apply {
            id = Id.newBuilder().setValue("a-questionnaire-response").build()
            addItem(QuestionnaireResponse.Item.newBuilder().apply {
                linkId = String.newBuilder().setValue("a-link-id").build()
                addAnswer(
                    QuestionnaireResponse.Item.Answer.newBuilder()
                        .setValue(
                            QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                                .setBoolean(
                                    Boolean.newBuilder().setValue(true)
                                )
                        )
                )
            })
        }.build()

        assertThat(
            validateQuestionnaireResponse(questionnaire.itemList, questionnaireResponse.itemList)
        ).isTrue()
    }

    @Test
    fun validateQuestionnaireResponse_shouldReturnThrowError_wrongLinkId() {
        val questionnaire = Questionnaire.newBuilder().apply {
            id = Id.newBuilder().setValue("a-questionnaire").build()
            addItem(Questionnaire.Item.newBuilder().apply {
                linkId = String.newBuilder().setValue("a-link-id").build()
                text = String.newBuilder().setValue("Basic question").build()
                type = Questionnaire.Item.TypeCode.newBuilder()
                    .setValue(QuestionnaireItemTypeCode.Value.BOOLEAN).build()
            })
        }.build()
        val questionnaireResponse = QuestionnaireResponse.newBuilder().apply {
            id = Id.newBuilder().setValue("a-questionnaire-response").build()
            addItem(QuestionnaireResponse.Item.newBuilder().apply {
                linkId = String.newBuilder().setValue("a-different-link-id").build()
                addAnswer(
                    QuestionnaireResponse.Item.Answer.newBuilder()
                        .setValue(
                            QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                                .setBoolean(
                                    Boolean.newBuilder().setValue(true)
                                )
                        )
                )
            })
        }.build()

        val errorMessage = assertFailsWith<IllegalArgumentException> {
            validateQuestionnaireResponse(questionnaire.itemList, questionnaireResponse.itemList)
        }.localizedMessage

        assertThat(errorMessage).isEqualTo("linkId mismatch")
    }

    @Test
    fun validateQuestionnaireResponse_shouldReturnThrowError_wrongStructure_moreItemsInQuestionnaireResponse() { // ktlint-disable max-line-length
        val questionnaire = Questionnaire.newBuilder().apply {
            id = Id.newBuilder().setValue("a-questionnaire").build()
            addItem(Questionnaire.Item.newBuilder().apply {
                linkId = String.newBuilder().setValue("a-link-id").build()
                text = String.newBuilder().setValue("Basic question").build()
                type = Questionnaire.Item.TypeCode.newBuilder()
                    .setValue(QuestionnaireItemTypeCode.Value.BOOLEAN).build()
            })
        }.build()
        val questionnaireResponse = QuestionnaireResponse.newBuilder().apply {
            id = Id.newBuilder().setValue("a-questionnaire-response").build()
            addItem(QuestionnaireResponse.Item.newBuilder().apply {
                linkId = String.newBuilder().setValue("a-link-id").build()
                addAnswer(
                    QuestionnaireResponse.Item.Answer.newBuilder()
                        .setValue(
                            QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                                .setBoolean(
                                    Boolean.newBuilder().setValue(true)
                                )
                        )
                )
            })
            addItem(QuestionnaireResponse.Item.newBuilder().apply {
                linkId = String.newBuilder().setValue("a-different-link-id").build()
                addAnswer(
                    QuestionnaireResponse.Item.Answer.newBuilder()
                        .setValue(
                            QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                                .setBoolean(
                                    Boolean.newBuilder().setValue(true)
                                )
                        )
                )
            })
        }.build()

        val errorMessage = assertFailsWith<IllegalArgumentException> {
            validateQuestionnaireResponse(questionnaire.itemList, questionnaireResponse.itemList)
        }.localizedMessage

        assertThat(errorMessage).isEqualTo("Structure mismatch")
    }

    @Test
    fun validateQuestionnaireResponse_shouldReturnThrowError_wrongStructure_lessItemsInQuestionnaireResponse() { // ktlint-disable max-line-length
        val questionnaire = Questionnaire.newBuilder().apply {
            id = Id.newBuilder().setValue("a-questionnaire").build()
            addItem(Questionnaire.Item.newBuilder().apply {
                linkId = String.newBuilder().setValue("a-link-id").build()
                text = String.newBuilder().setValue("Basic question 1").build()
                type = Questionnaire.Item.TypeCode.newBuilder()
                    .setValue(QuestionnaireItemTypeCode.Value.BOOLEAN).build()
            })
            addItem(Questionnaire.Item.newBuilder().apply {
                linkId = String.newBuilder().setValue("another-link-id").build()
                text = String.newBuilder().setValue("Basic question 2").build()
                type = Questionnaire.Item.TypeCode.newBuilder()
                    .setValue(QuestionnaireItemTypeCode.Value.BOOLEAN).build()
            })
        }.build()
        val questionnaireResponse = QuestionnaireResponse.newBuilder().apply {
            id = Id.newBuilder().setValue("a-questionnaire-response").build()
            addItem(QuestionnaireResponse.Item.newBuilder().apply {
                linkId = String.newBuilder().setValue("a-link-id").build()
                addAnswer(
                    QuestionnaireResponse.Item.Answer.newBuilder()
                        .setValue(
                            QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                                .setBoolean(
                                    Boolean.newBuilder().setValue(true)
                                )
                        )
                )
            })
        }.build()

        val errorMessage = assertFailsWith<IllegalArgumentException> {
            validateQuestionnaireResponse(questionnaire.itemList, questionnaireResponse.itemList)
        }.localizedMessage

        assertThat(errorMessage).isEqualTo("Structure mismatch")
    }

    private companion object {
        val printer: JsonFormat.Printer = JsonFormat.getPrinter()
        fun assertResourceEquals(r1: Message, r2: Message) {
            assertThat(printer.print(r1)).isEqualTo(printer.print(r2))
        }
    }
}
