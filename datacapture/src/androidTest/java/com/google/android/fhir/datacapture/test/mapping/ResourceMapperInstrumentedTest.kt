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

package com.google.android.fhir.datacapture.test.mapping

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.datacapture.mapping.ProfileLoader
import com.google.android.fhir.datacapture.mapping.ResourceMapper
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StructureDefinition
import org.intellij.lang.annotations.Language
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResourceMapperInstrumentedTest {
  private lateinit var mContext: Context

  @Language("JSON")
  private val questionnaireJson =
    """
       {
         "resourceType": "Questionnaire",
         "extension": [
           {
             "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemExtractionContext",
             "valueExpression": {
               "language": "application/x-fhir-query",
               "expression": "Patient",
               "name": "patient"
             }
           }
         ],
         "item": [
           {
             "linkId": "1",
             "definition": "http://fhir.org/guides/who/core/StructureDefinition/who-patient#Patient.birthTime",
             "text": "Time of birth",
             "type": "dateTime"
           }
         ]
       }
        """
      .trimIndent()

  @Language("JSON")
  private val questionnaireResponseJson =
    """
        {
          "resourceType": "QuestionnaireResponse",
          "item": [
            {
              "linkId": "1",
              "answer": [
                {
                  "valueDateTime": "2022-02-07T13:28:17-05:00"
                }
              ]
            }
          ]
        }
        """
      .trimIndent()
  val iParser: IParser = FhirContext.forR4().newJsonParser()
  private val questionnaireWithBirthTimeExt: Questionnaire =
    iParser.parseResource(Questionnaire::class.java, questionnaireJson) as Questionnaire
  private val questionnaireResponseWithBirthTimeExt: QuestionnaireResponse =
    iParser.parseResource(QuestionnaireResponse::class.java, questionnaireResponseJson)
      as QuestionnaireResponse

  @Before
  fun setUp() {
    mContext = InstrumentationRegistry.getInstrumentation().context
  }

  @Test
  fun extract_resourceExtension_presentInConformingProfile_shouldPerformDefinitionBasedExtraction():
    Unit = runBlocking {
    @Language("JSON")
    val bundle =
      ResourceMapper.extract(
        questionnaire = questionnaireWithBirthTimeExt,
        questionnaireResponse = questionnaireResponseWithBirthTimeExt,
        structureMapExtractionContext = null,
        profileLoader = ProfileLoaderImplWithExt(mContext),
      )
    val patient = bundle.entry.single().resource as Patient

    assertThat(patient).isNotNull()
    val dateTimeType =
      patient
        .getExtensionByUrl(
          "http://fhir.org/guides/who/core/StructureDefinition/who-patient#Patient.birthTime",
        )
        .value as DateTimeType
    val expectedDateTimeType = DateTimeType("2022-02-07T13:28:17-05:00")
    assertThat(dateTimeType.value).isEqualTo(expectedDateTimeType.value)
  }

  @Test
  fun extract_resourceExtension_notPresentInConformingProfile_shouldNotPerformDefinitionBasedExtraction():
    Unit = runBlocking {
    val bundle =
      ResourceMapper.extract(
        questionnaire = questionnaireWithBirthTimeExt,
        questionnaireResponse = questionnaireResponseWithBirthTimeExt,
        structureMapExtractionContext = null,
        profileLoader = ProfileLoaderImplWithoutBirthTimeExt(mContext),
      )
    val patient = bundle.entry.single().resource as Patient

    assertThat(patient).isNotNull()
    assertThat(
        patient.getExtensionByUrl(
          "http://fhir.org/guides/who/core/StructureDefinition/who-patient#Patient.birthTime",
        ),
      )
      .isEqualTo(null)
  }

  @Test
  fun extract_resourceExtension_loadProfileProvidedNull_shouldNotExtractExtensionField(): Unit =
    runBlocking {
      val bundle =
        ResourceMapper.extract(
          questionnaire = questionnaireWithBirthTimeExt,
          questionnaireResponse = questionnaireResponseWithBirthTimeExt,
          structureMapExtractionContext = null,
          profileLoader = ProfileLoaderImplWithExt(mContext),
        )
      val patient = bundle.entry.single().resource as Patient

      assertThat(patient).isNotNull()
      assertThat(
          patient.getExtensionByUrl(
            "http://fhir.org/guides/who/anc-cds/StructureDefinition/anc-patient#Patient.birthTime",
          ),
        )
        .isEqualTo(null)
    }

  @After fun tearDown() {}

  private class ProfileLoaderImplWithExt(private val context: Context) : ProfileLoader {
    override fun loadProfile(url: CanonicalType): StructureDefinition? {
      val structureDefinition =
        when (url.valueAsString) {
          "http://fhir.org/guides/who/core/StructureDefinition/who-patient" -> {
            // This JSON file is available at
            // https://build.fhir.org/ig/WorldHealthOrganization/smart-anc/branches/master/StructureDefinition-who-patient.profile.json.html
            // in WHO IG can be downloaded as raw JSON file
            getStructureDefinition("structure_definition_who_patient.json", context)
          }
          else -> null
        }
      return structureDefinition
    }
  }

  private class ProfileLoaderImplWithoutBirthTimeExt(private val context: Context) : ProfileLoader {
    override fun loadProfile(url: CanonicalType): StructureDefinition? {
      val structureDefinition =
        when (url.valueAsString) {
          "http://fhir.org/guides/who/core/StructureDefinition/who-patient" -> {
            getStructureDefinition(
              "structure_definition_who_patient_without_birthtime.json",
              context,
            )
          }
          else -> null
        }
      return structureDefinition
    }
  }
}

private fun getStructureDefinition(
  StructureDefinitionUri: String,
  context: Context,
): StructureDefinition {
  val structureDefinitionJson =
    context.assets.open(StructureDefinitionUri).bufferedReader().use { it.readText() }
  return FhirContext.forCached(FhirVersionEnum.R4)
    .newJsonParser()
    .parseResource(structureDefinitionJson) as StructureDefinition
}
