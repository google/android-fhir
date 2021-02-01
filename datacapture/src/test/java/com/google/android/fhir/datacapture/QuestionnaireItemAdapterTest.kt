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
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem
import com.google.common.truth.Truth.assertThat
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireItemTypeCode
import com.google.fhir.r4.core.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class QuestionnaireItemAdapterTest {
    @Test
    fun getItemViewType_groupItemType_shouldReturnGroupViewHolderType() {
        val questionnaireItemAdapter = QuestionnaireItemAdapter(
            listOf(
                QuestionnaireItemViewItem(
                    Questionnaire.Item.newBuilder()
                        .setType(
                            Questionnaire.Item.TypeCode.newBuilder()
                                .setValue(QuestionnaireItemTypeCode.Value.GROUP)
                        )
                        .build(),
                    QuestionnaireResponse.Item.newBuilder()
                )
            )
        )
        assertThat(questionnaireItemAdapter.getItemViewType(0)).isEqualTo(
            QuestionnaireItemViewHolderType.GROUP.value
        )
    }

    @Test
    fun getItemViewType_booleanItemType_shouldReturnBooleanViewHolderType() {
        val questionnaireItemAdapter = QuestionnaireItemAdapter(
            listOf(
                QuestionnaireItemViewItem(
                    Questionnaire.Item.newBuilder()
                        .setType(
                            Questionnaire.Item.TypeCode.newBuilder()
                                .setValue(QuestionnaireItemTypeCode.Value.BOOLEAN)
                        )
                        .build(),
                    QuestionnaireResponse.Item.newBuilder()
                )
            )
        )
        assertThat(questionnaireItemAdapter.getItemViewType(0)).isEqualTo(
            QuestionnaireItemViewHolderType.CHECK_BOX.value
        )
    }

    @Test
    fun getItemViewType_dateItemType_shouldReturnDatePickerViewHolderType() {
        val questionnaireItemAdapter = QuestionnaireItemAdapter(
            listOf(
                QuestionnaireItemViewItem(
                    Questionnaire.Item.newBuilder()
                        .setType(
                            Questionnaire.Item.TypeCode.newBuilder()
                                .setValue(QuestionnaireItemTypeCode.Value.DATE)
                        )
                        .build(),
                    QuestionnaireResponse.Item.newBuilder()
                )
            )
        )
        assertThat(questionnaireItemAdapter.getItemViewType(0)).isEqualTo(
            QuestionnaireItemViewHolderType.DATE_PICKER.value
        )
    }

    @Test
    fun getItemViewType_dateTimeItemType_shouldReturnDateTimePickerViewHolderType() {
        val questionnaireItemAdapter = QuestionnaireItemAdapter(
            listOf(
                QuestionnaireItemViewItem(
                    Questionnaire.Item.newBuilder()
                        .setType(
                            Questionnaire.Item.TypeCode.newBuilder()
                                .setValue(QuestionnaireItemTypeCode.Value.DATE_TIME)
                        )
                        .build(),
                    QuestionnaireResponse.Item.newBuilder()
                )
            )
        )
        assertThat(questionnaireItemAdapter.getItemViewType(0)).isEqualTo(
            QuestionnaireItemViewHolderType.DATE_TIME_PICKER.value
        )
    }

    @Test
    fun getItemViewType_stringItemType_shouldReturnEditTextViewHolderType() {
        val questionnaireItemAdapter = QuestionnaireItemAdapter(
            listOf(
                QuestionnaireItemViewItem(
                    Questionnaire.Item.newBuilder()
                        .setType(
                            Questionnaire.Item.TypeCode.newBuilder()
                                .setValue(QuestionnaireItemTypeCode.Value.STRING)
                        )
                        .build(),
                    QuestionnaireResponse.Item.newBuilder()
                )
            )
        )
        assertThat(questionnaireItemAdapter.getItemViewType(0)).isEqualTo(
            QuestionnaireItemViewHolderType.EDIT_TEXT_SINGLE_LINE.value
        )
    }

    @Test
    fun getItemViewType_textItemType_shouldReturnEditTextViewHolderType() {
        val questionnaireItemAdapter = QuestionnaireItemAdapter(
            listOf(
                QuestionnaireItemViewItem(
                    Questionnaire.Item.newBuilder()
                        .setType(
                            Questionnaire.Item.TypeCode.newBuilder()
                                .setValue(QuestionnaireItemTypeCode.Value.TEXT)
                        )
                        .build(),
                    QuestionnaireResponse.Item.newBuilder()
                )
            )
        )
        assertThat(questionnaireItemAdapter.getItemViewType(0)).isEqualTo(
            QuestionnaireItemViewHolderType.EDIT_TEXT_MULTI_LINE.value
        )
    }

    @Test
    fun getItemViewType_integerItemType_shouldReturnEditTextIntegerViewHolderType() {
        val questionnaireItemAdapter = QuestionnaireItemAdapter(
            listOf(
                QuestionnaireItemViewItem(
                    Questionnaire.Item.newBuilder()
                        .setType(
                            Questionnaire.Item.TypeCode.newBuilder()
                                .setValue(QuestionnaireItemTypeCode.Value.INTEGER)
                        )
                        .build(),
                    QuestionnaireResponse.Item.newBuilder()
                )
            )
        )
        assertThat(questionnaireItemAdapter.getItemViewType(0)).isEqualTo(
            QuestionnaireItemViewHolderType.EDIT_TEXT_INTEGER.value
        )
    }

    @Test
    fun getItemViewType_decimalItemType_shouldReturnEditTextDecimalViewHolderType() {
        val questionnaireItemAdapter = QuestionnaireItemAdapter(
            listOf(
                QuestionnaireItemViewItem(
                    Questionnaire.Item.newBuilder()
                        .setType(
                            Questionnaire.Item.TypeCode.newBuilder()
                                .setValue(QuestionnaireItemTypeCode.Value.DECIMAL)
                        )
                        .build(),
                    QuestionnaireResponse.Item.newBuilder()
                )
            )
        )
        assertThat(questionnaireItemAdapter.getItemViewType(0)).isEqualTo(
            QuestionnaireItemViewHolderType.EDIT_TEXT_DECIMAL.value
        )
    }

    @Test
    fun getItemViewType_choiceItemType_shouldReturnRadioGroupViewHolderType() {
        val questionnaireItemAdapter = QuestionnaireItemAdapter(
            listOf(
                QuestionnaireItemViewItem(
                    Questionnaire.Item.newBuilder()
                        .setType(
                            Questionnaire.Item.TypeCode.newBuilder()
                                .setValue(QuestionnaireItemTypeCode.Value.CHOICE)
                        )
                        .build(),
                    QuestionnaireResponse.Item.newBuilder()
                )
            )
        )
        assertThat(questionnaireItemAdapter.getItemViewType(0)).isEqualTo(
            QuestionnaireItemViewHolderType.RADIO_GROUP.value
        )
    }

    // TODO: test errors thrown for unsupported types
}
