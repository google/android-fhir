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
import com.google.common.truth.Truth
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

  @Before
  fun setUp() {
    mContext = InstrumentationRegistry.getInstrumentation().context
  }

  @Test
  fun extract_resourceExtension_presentInConformingProfile_shouldPerformDefinitionBasedExtraction():
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
             "definition": "http://fhir.org/guides/who/anc-cds/StructureDefinition/anc-patient#Patient.birthTime",
             "text": "Time of birth",
             "type": "dateTime"
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
                  "valueDateTime": "2022-02-07T13:28:17-05:00"
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
        loadProfile = LoadProfile(mContext),
      )
    val patient = bundle.entry.single().resource as Patient

    Truth.assertThat(patient).isNotNull()
    val dateTimeType =
      patient
        .getExtensionByUrl(
          "http://fhir.org/guides/who/anc-cds/StructureDefinition/anc-patient#Patient.birthTime"
        )
        .value as DateTimeType
    val expectedDateTimeType = DateTimeType("2022-02-07T13:28:17-05:00")
    Truth.assertThat(dateTimeType.year).isEqualTo(expectedDateTimeType.year)
    Truth.assertThat(dateTimeType.month).isEqualTo(expectedDateTimeType.month)
    Truth.assertThat(dateTimeType.day).isEqualTo(expectedDateTimeType.day)
    Truth.assertThat(dateTimeType.hour).isEqualTo(expectedDateTimeType.hour)
    Truth.assertThat(dateTimeType.minute).isEqualTo(expectedDateTimeType.minute)
    Truth.assertThat(dateTimeType.second).isEqualTo(expectedDateTimeType.second)
    Truth.assertThat(dateTimeType.timeZone).isEqualTo(expectedDateTimeType.timeZone)
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
             "definition": "http://fhir.org/guides/who/anc-cds/StructureDefinition/anc-patient#Patient.reminder",
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
        loadProfile = LoadProfile(mContext),
      )
    val patient = bundle.entry.single().resource as Patient

    Truth.assertThat(patient).isNotNull()
    Truth.assertThat(
        patient
          .getExtensionByUrl(
            "http://fhir.org/guides/who/anc-cds/StructureDefinition/anc-patient#Patient.reminder"
          )
          ?.valueAsPrimitive
          ?.value
      )
      .isEqualTo(null)
  }

  @After fun tearDown() {}

  private class LoadProfile(private val context: Context) : LoadProfileCallback {
    override fun loadProfile(url: CanonicalType): StructureDefinition {
      val structureDefinition =
        when (url.toString()) {
          "http://fhir.org/guides/who/core/StructureDefinition/who-patient" -> {
            getStructureDefinition("structure_definition_who_patient.json", context)
          }
          else -> {
            // getStructureDefinition("structure_definition_fhir_patient.json", context)
            getStructureDefinition("structure_definition_who_patient.json", context)
          }
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
