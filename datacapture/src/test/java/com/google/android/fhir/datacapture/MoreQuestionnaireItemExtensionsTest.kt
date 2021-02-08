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
import com.google.common.truth.Truth.assertThat
import com.google.fhir.r4.core.Code
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.Coding
import com.google.fhir.r4.core.Extension
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireItemTypeCode
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.Uri
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MoreQuestionnaireItemExtensionsTest {

    @Test
    fun itemControl_shouldReturnItemControlCode() {

        val questionnaireItem = Questionnaire.Item.newBuilder()
            .setType(
                Questionnaire.Item.TypeCode.newBuilder()
                    .setValue(QuestionnaireItemTypeCode.Value.CHOICE)
            )
            .addExtension(
                Extension.newBuilder()
                    .setUrl(
                        Uri.newBuilder()
                            .setValue(EXTENSION_ITEM_CONTROL_URL)
                    )
                    .setValue(
                        Extension.ValueX.newBuilder()
                            .setCodeableConcept(
                                CodeableConcept.newBuilder()
                                    .addCoding(
                                        Coding.newBuilder()
                                            .setCode(
                                                Code.newBuilder()
                                                    .setValue(ITEM_CONTROL_DROP_DOWN)
                                            )
                                            .setDisplay(
                                                String.newBuilder()
                                                    .setValue("Drop Down")
                                            )
                                            .setSystem(
                                                Uri.newBuilder()
                                                    .setValue(EXTENSION_ITEM_CONTROL_SYSTEM)
                                            )
                                    )
                            )

                    )
            )
            .build()

        assertThat(questionnaireItem.itemControl).isEqualTo(ITEM_CONTROL_DROP_DOWN)
    }

    @Test
    fun itemControl_wrongExtensionUrl_shouldReturnNull() {

        val questionnaireItem = Questionnaire.Item.newBuilder()
            .setType(
                Questionnaire.Item.TypeCode.newBuilder()
                    .setValue(QuestionnaireItemTypeCode.Value.CHOICE)
            )
            .addExtension(
                Extension.newBuilder()
                    .setUrl(
                        Uri.newBuilder()
                            .setValue("null-test")
                    )
                    .setValue(
                        Extension.ValueX.newBuilder()
                            .setCodeableConcept(
                                CodeableConcept.newBuilder()
                                    .addCoding(
                                        Coding.newBuilder()
                                            .setCode(
                                                Code.newBuilder()
                                                    .setValue(ITEM_CONTROL_DROP_DOWN)
                                            )
                                            .setDisplay(
                                                String.newBuilder()
                                                    .setValue("Drop Down")
                                            )
                                            .setSystem(
                                                Uri.newBuilder()
                                                    .setValue(EXTENSION_ITEM_CONTROL_SYSTEM)
                                            )
                                    )
                            )

                    )
            )
            .build()

        assertThat(questionnaireItem.itemControl).isNull()
    }

    @Test
    fun itemControl_wrongExtensionCoding_shouldReturnNull() {

        val questionnaireItem = Questionnaire.Item.newBuilder()
            .setType(
                Questionnaire.Item.TypeCode.newBuilder()
                    .setValue(QuestionnaireItemTypeCode.Value.CHOICE)
            )
            .addExtension(
                Extension.newBuilder()
                    .setUrl(
                        Uri.newBuilder()
                            .setValue(EXTENSION_ITEM_CONTROL_URL)
                    )
                    .setValue(
                        Extension.ValueX.newBuilder()
                            .setCodeableConcept(
                                CodeableConcept.newBuilder()
                                    .addCoding(
                                        Coding.newBuilder()
                                            .setCode(
                                                Code.newBuilder()
                                                    .setValue("null-test")
                                            )
                                            .setDisplay(
                                                String.newBuilder()
                                                    .setValue("Drop Down")
                                            )
                                            .setSystem(
                                                Uri.newBuilder()
                                                    .setValue(EXTENSION_ITEM_CONTROL_SYSTEM)
                                            )
                                    )
                            )

                    )
            )
            .build()

        assertThat(questionnaireItem.itemControl).isNull()
    }
}
