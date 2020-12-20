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
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
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
                    Questionnaire.QuestionnaireItemComponent().apply
                    {
                        type = Questionnaire.QuestionnaireItemType.GROUP
                    },
                    QuestionnaireResponse.QuestionnaireResponseItemComponent()
                )
            ),
            mapper
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
                    Questionnaire.QuestionnaireItemComponent().apply
                    {
                        type = Questionnaire.QuestionnaireItemType.BOOLEAN
                    },
                    QuestionnaireResponse.QuestionnaireResponseItemComponent()
                )
            ),
            mapper
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
                    Questionnaire.QuestionnaireItemComponent().apply
                    {
                        type = Questionnaire.QuestionnaireItemType.DATE
                    },
                    QuestionnaireResponse.QuestionnaireResponseItemComponent()
                )
            ),
            mapper
        )
        assertThat(questionnaireItemAdapter.getItemViewType(0)).isEqualTo(
            QuestionnaireItemViewHolderType.DATE_PICKER.value
        )
    }

    @Test
    fun getItemViewType_stringItemType_shouldReturnEditTextViewHolderType() {
        val questionnaireItemAdapter = QuestionnaireItemAdapter(
            listOf(
                QuestionnaireItemViewItem(
                    Questionnaire.QuestionnaireItemComponent().apply
                    {
                        type = Questionnaire.QuestionnaireItemType.STRING
                    },
                    QuestionnaireResponse.QuestionnaireResponseItemComponent()
                )
            ),
            mapper
        )
        assertThat(questionnaireItemAdapter.getItemViewType(0)).isEqualTo(
            QuestionnaireItemViewHolderType.EDIT_TEXT.value
        )
    }

    // TODO: test errors thrown for unsupported types
}
