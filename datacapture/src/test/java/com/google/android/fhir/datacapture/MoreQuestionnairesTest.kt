/*
 * Copyright 2022 Google LLC
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

import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.StringType
import org.junit.Test

class MoreQuestionnairesTest {

  @Test
  fun `targetStructureMap() should return null when Questionnaire lacks extension`() {
    assertThat(Questionnaire().targetStructureMap).isEqualTo(null)
  }

  @Test
  fun `targetStructureMap() should return StructureMap url`() {
    val structureMapUrl = "https://fhir.labs.smartregister.org/StructureMap/383"

    val questionnaire = Questionnaire()
    questionnaire.extension =
      listOf(
        Extension(
          "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-targetStructureMap",
          CanonicalType(structureMapUrl)
        )
      )

    assertThat(questionnaire.targetStructureMap).isEqualTo(structureMapUrl)
  }

  @Test
  fun `isPaginated should return true`() {
    val questionnaire =
      Questionnaire().apply {
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            addExtension(
              Extension()
                .setUrl(EXTENSION_ITEM_CONTROL_URL)
                .setValue(
                  CodeableConcept()
                    .addCoding(
                      Coding()
                        .setCode(DisplayItemControlType.PAGE.extensionCode)
                        .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM)
                    )
                )
            )
          }
        )
      }

    assertThat(questionnaire.isPaginated).isTrue()
  }

  @Test
  fun `isPaginated should return false`() {
    val questionnaire =
      Questionnaire().apply { addItem(Questionnaire.QuestionnaireItemComponent()) }

    assertThat(questionnaire.isPaginated).isFalse()
  }

  @Test
  fun `entryMode should return prior-edit EntryMode`() {
    val questionnaire = Questionnaire()
    questionnaire.extension = listOf(Extension(EXTENSION_ENTRY_MODE_URL, StringType("prior-edit")))
    assertThat(questionnaire.entryMode).isEqualTo(EntryMode.PRIOR_EDIT)
  }

  @Test
  fun `entryMode should return sequential EntryMode`() {
    val questionnaire = Questionnaire()
    questionnaire.extension = listOf(Extension(EXTENSION_ENTRY_MODE_URL, StringType("sequential")))
    assertThat(questionnaire.entryMode).isEqualTo(EntryMode.SEQUENTIAL)
  }

  @Test
  fun `entryMode should return random EntryMode`() {
    val questionnaire = Questionnaire()
    questionnaire.extension = listOf(Extension(EXTENSION_ENTRY_MODE_URL, StringType("random")))
    assertThat(questionnaire.entryMode).isEqualTo(EntryMode.RANDOM)
  }

  @Test
  fun `entryMode should return null if no EntryMode is defined`() {
    val questionnaire = Questionnaire()
    assertThat(questionnaire.entryMode).isNull()
  }
}
