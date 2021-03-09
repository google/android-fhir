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

package com.google.android.fhir.datacapture.mapping

import android.os.Build
import com.google.common.truth.Truth.assertThat
import com.google.fhir.common.JsonFormat
import com.google.fhir.r4.core.Date
import com.google.fhir.r4.core.Patient
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse
import java.time.LocalDate
import java.time.ZoneId
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class ResourceMapperTest {
  @Test
  fun extract() {
    // https://developer.commure.com/docs/apis/sdc/examples#definition-based-extraction
    val questionnaireJson =
      """
            {
              "resourceType": "Questionnaire",
              "subjectType": [
                "Patient"
              ],
              "extension": [
                {
                  "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemContext",
                  "valueExpression": {
                    "language": "application/x-fhir-query",
                    "expression": "Patient",
                    "name": "patient"
                  }
                }
              ],
              "item": [
                {
                  "linkId": "patient-0",
                  "type": "group",
                  "item": [
                    {
                      "linkId": "patient-0-birth-date",
                      "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.birthDate",
                      "type": "date"
                    },
                    {
                      "linkId": "patient-0-active",
                      "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.active",
                      "type": "boolean"
                    }
                  ]
                }
              ]
            }
        """.trimIndent()
    val questionnaireBuilder = Questionnaire.newBuilder()
    JsonFormat.getParser().merge(questionnaireJson, questionnaireBuilder)

    val questionnaireResponseJson =
      """
            {
              "resourceType": "QuestionnaireResponse",
              "item": [
                {
                  "linkId": "patient-0",
                  "item": [
                    {
                      "linkId": "patient-0-birth-date",
                      "answer": [
                        {
                          "valueDate": "2021-01-01"
                        }
                      ]
                    },
                    {
                      "linkId": "patient-0-active",
                      "answer": [
                        {
                          "valueBoolean": true
                        }
                      ]
                    }
                  ]
                }
              ]
            }
        """.trimIndent()
    val questionnaireResponseBuilder = QuestionnaireResponse.newBuilder()
    JsonFormat.getParser().merge(questionnaireResponseJson, questionnaireResponseBuilder)

    val patient =
      ResourceMapper.extract(questionnaireBuilder.build(), questionnaireResponseBuilder.build()) as
        Patient

    assertThat(patient.birthDate)
      .isEqualTo(
        Date.newBuilder()
          .setValueUs(
            LocalDate.of(2021, 1, 1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000000
          )
          .setTimezone(ZoneId.systemDefault().id)
          .setPrecision(Date.Precision.DAY)
          .build()
      )
    assertThat(patient.active.value).isTrue()
  }
}
