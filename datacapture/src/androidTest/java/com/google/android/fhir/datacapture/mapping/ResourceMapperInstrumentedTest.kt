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

package com.google.android.fhir.datacapture.mapping

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.parser.IParser
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
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResourceMapperInstrumentedTest {
  private lateinit var mContext: Context
  private lateinit var questionnaire: Questionnaire
  private lateinit var questionnaireResponse: QuestionnaireResponse

  @Before
  fun setUp() {
    mContext = InstrumentationRegistry.getInstrumentation().context
    val questionnaireJson =
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
      """.trimIndent()

    @Language("JSON")
    val questionnaireResponseJson =
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
      """.trimIndent()

    val iParser: IParser = FhirContext.forR4().newJsonParser()
    questionnaire =
      iParser.parseResource(Questionnaire::class.java, questionnaireJson) as Questionnaire
    questionnaireResponse =
      iParser.parseResource(QuestionnaireResponse::class.java, questionnaireResponseJson)
        as QuestionnaireResponse
  }

  @Test
  fun extract_resourceExtension_presentInConformingProfile_shouldPerformDefinitionBasedExtraction():
    Unit = runBlocking {
    @Language("JSON")
    val bundle =
      ResourceMapper.extract(
        questionnaire = questionnaire,
        questionnaireResponse = questionnaireResponse,
        structureMapExtractionContext = null,
        profileLoader = ProfileLoaderImpl(mContext),
      )
    val patient = bundle.entry.single().resource as Patient

    assertThat(patient).isNotNull()
    val dateTimeType =
      patient
        .getExtensionByUrl(
          "http://fhir.org/guides/who/core/StructureDefinition/who-patient#Patient.birthTime"
        )
        .value as DateTimeType
    val expectedDateTimeType = DateTimeType("2022-02-07T13:28:17-05:00")
    assertThat(dateTimeType.year).isEqualTo(expectedDateTimeType.year)
    assertThat(dateTimeType.month).isEqualTo(expectedDateTimeType.month)
    assertThat(dateTimeType.day).isEqualTo(expectedDateTimeType.day)
    assertThat(dateTimeType.hour).isEqualTo(expectedDateTimeType.hour)
    assertThat(dateTimeType.minute).isEqualTo(expectedDateTimeType.minute)
    assertThat(dateTimeType.second).isEqualTo(expectedDateTimeType.second)
    assertThat(dateTimeType.timeZone).isEqualTo(expectedDateTimeType.timeZone)
  }

  @Test
  fun extract_resourceExtension_notPresentInConformingProfile_shouldNotPerformDefinitionBasedExtraction():
    Unit = runBlocking {
    @Language("JSON")
    val questionnaire =
      """
       {
         "resourceType": "Questionnaire",
         "meta": {
           "profile": [
             "http://fhir.org/guides/who/core/StructureDefinition/who-patient"
           ]
         },
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
             "definition": "http://fhir.org/guides/who/core/StructureDefinition/who-patient#Patient.reminder",
             "text": "Woman wants to receive reminders during pregnancy",
             "type": "boolean"
           }
         ]
       }
      """.trimIndent()

    @Language("JSON")
    val response =
      """
        {
          "resourceType": "QuestionnaireResponse",
          "item": [
            {
              "linkId": "1",
              "answer": [
                {
                  "valueBoolean": true
                }
              ]
            }
          ]
        }
      """.trimIndent()

    val iParser: IParser = FhirContext.forR4().newJsonParser()
    val questionnaireObj =
      iParser.parseResource(Questionnaire::class.java, questionnaire) as Questionnaire
    val temperatureQuestionnaireResponse =
      iParser.parseResource(QuestionnaireResponse::class.java, response) as QuestionnaireResponse
    val bundle =
      ResourceMapper.extract(
        questionnaire = questionnaireObj,
        questionnaireResponse = temperatureQuestionnaireResponse,
        structureMapExtractionContext = null,
        profileLoader = ProfileLoaderImpl(mContext),
      )
    val patient = bundle.entry.single().resource as Patient

    assertThat(patient).isNotNull()
    assertThat(
        patient
          .getExtensionByUrl(
            "http://fhir.org/guides/who/core/StructureDefinition/who-patient#Patient.reminder"
          )
          ?.valueAsPrimitive
          ?.value
      )
      .isEqualTo(null)
  }

  @Test
  fun extract_resourceExtension_loadProfileImplementationNotProvided_shouldThrowIllegalArgumentException():
    Unit = runBlocking {
    val exception =
      Assert.assertThrows(IllegalArgumentException::class.java) {
        runBlocking {
          ResourceMapper.extract(
            questionnaire = questionnaire,
            questionnaireResponse = questionnaireResponse,
            structureMapExtractionContext = null
          )
        }
      }
    assertThat(exception.message)
      .isEqualTo(
        "ProfileLoader implementation required to load StructureDefinition that this resource claims to conform to"
      )
  }

  @Test
  fun extract_resourceExtension_loadProfileProvidedNull_shouldNotExtractExtensionField(): Unit =
    runBlocking {
      val bundle =
        ResourceMapper.extract(
          questionnaire = questionnaire,
          questionnaireResponse = questionnaireResponse,
          structureMapExtractionContext = null,
          profileLoader = ProfileLoaderImpl(mContext),
        )
      val patient = bundle.entry.single().resource as Patient

      assertThat(patient).isNotNull()
      assertThat(
          patient.getExtensionByUrl(
            "http://fhir.org/guides/who/anc-cds/StructureDefinition/anc-patient#Patient.birthTime"
          )
        )
        .isEqualTo(null)
    }

  @After fun tearDown() {}

  private class ProfileLoaderImpl(private val context: Context) : ProfileLoader {
    override fun loadProfile(url: CanonicalType): StructureDefinition? {
      val structureDefinition =
        when (url.valueAsString) {
          "http://fhir.org/guides/who/core/StructureDefinition/who-patient" -> {
            /**
             * ThIs json file is available at
             * https://build.fhir.org/ig/WorldHealthOrganization/smart-anc/branches/master/StructureDefinition-who-patient.profile.json.html
             * in WHO IG can be downloaded as raw json file
             */
            getStructureDefinition("structure_definition_who_patient.json", context)
          }
          else -> null
        }
      return structureDefinition
    }

    private fun getStructureDefinition(
      StructureDefinitionUri: String,
      context: Context
    ): StructureDefinition {
      val structureDefinitionJson =
        context.assets.open(StructureDefinitionUri).bufferedReader().use { it.readText() }
      return FhirContext.forCached(FhirVersionEnum.R4)
        .newJsonParser()
        .parseResource(structureDefinitionJson) as StructureDefinition
    }
  }
}
