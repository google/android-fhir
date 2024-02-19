/*
 * Copyright 2023 Google LLC
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

package com.google.android.fhir.datacapture.extensions

import com.google.common.truth.Truth.assertThat
import kotlin.test.assertFailsWith
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.CodeType
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
          CanonicalType(structureMapUrl),
        ),
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
                        .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM),
                    ),
                ),
            )
          },
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

  @Test
  fun `validateLaunchContextExtensions should throw exception if type in type extension is not a valid resource type`() {
    val launchContextExtension =
      Extension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-launchContext")
        .apply {
          addExtension(
            "name",
            Coding("http://hl7.org/fhir/uv/sdc/CodeSystem/launchContext", "me", "Me"),
          )
          addExtension("type", CodeType("Avocado"))
        }

    val errorMessage =
      assertFailsWith<IllegalStateException> {
          validateLaunchContextExtensions(listOf(launchContextExtension))
        }
        .localizedMessage

    assertThat(errorMessage)
      .isEqualTo(
        "The extension:name and/or extension:type do not follow the format specified in $EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT",
      )
  }

  @Test
  fun `validateLaunchContextExtensions should throw exception if system in name extension is not valid`() {
    val launchContextExtension =
      Extension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-launchContext")
        .apply {
          addExtension("name", Coding("http://wrong-system", "grandma", "Grandma"))
          addExtension("type", CodeType("Patient"))
        }
    val errorMessage =
      assertFailsWith<IllegalStateException> {
          validateLaunchContextExtensions(listOf(launchContextExtension))
        }
        .localizedMessage

    assertThat(errorMessage)
      .isEqualTo(
        "The extension:name and/or extension:type do not follow the format specified in $EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT",
      )
  }

  @Test
  fun `validateLaunchContextExtensions should throw exception if the type extension has wrong url`() {
    val launchContextExtension =
      Extension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-launchContext")
        .apply {
          addExtension("name", Coding("http://wrong-system", "grandma", "Grandma"))
          addExtension("waitwhat", StringType("Patient"))
        }

    val errorMessage =
      assertFailsWith<IllegalStateException> {
          validateLaunchContextExtensions(listOf(launchContextExtension))
        }
        .localizedMessage

    assertThat(errorMessage)
      .isEqualTo(
        "The extension:type is missing or is not of type CodeType in $EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT",
      )
  }

  @Test
  fun `validateLaunchContextExtensions should throw exception if the type extension value is not CodeType`() {
    val launchContextExtension =
      Extension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-launchContext")
        .apply {
          addExtension("name", Coding("http://wrong-system", "grandma", "Grandma"))
          addExtension("type", StringType("Patient"))
        }

    val errorMessage =
      assertFailsWith<IllegalStateException> {
          validateLaunchContextExtensions(listOf(launchContextExtension))
        }
        .localizedMessage

    assertThat(errorMessage)
      .isEqualTo(
        "The extension:type is missing or is not of type CodeType in $EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT",
      )
  }

  @Test
  fun `validateLaunchContextExtensions should throw exception if the name extension has wrong url`() {
    val launchContextExtension =
      Extension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-launchContext")
        .apply {
          addExtension(
            "waitwhat",
            Coding("http://hl7.org/fhir/uv/sdc/CodeSystem/launchContext", "grandma", "Grandma"),
          )
          addExtension("type", CodeType("Patient"))
        }

    val errorMessage =
      assertFailsWith<IllegalStateException> {
          validateLaunchContextExtensions(listOf(launchContextExtension))
        }
        .localizedMessage

    assertThat(errorMessage)
      .isEqualTo(
        "The extension:name is missing or is not of type Coding in $EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT",
      )
  }

  @Test
  fun `validateLaunchContextExtensions should throw exception if the name extension value is not Coding`() {
    val launchContextExtension =
      Extension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-launchContext")
        .apply {
          addExtension("name", StringType("waitwhat"))
          addExtension("type", CodeType("Patient"))
        }

    val errorMessage =
      assertFailsWith<IllegalStateException> {
          validateLaunchContextExtensions(listOf(launchContextExtension))
        }
        .localizedMessage

    assertThat(errorMessage)
      .isEqualTo(
        "The extension:name is missing or is not of type Coding in $EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT",
      )
  }
}
