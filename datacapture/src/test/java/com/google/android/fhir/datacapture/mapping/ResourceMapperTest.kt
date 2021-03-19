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
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.parser.IParser
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
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
    val questionnaire = parser.parseResource(questionnaireJson) as Questionnaire

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
    val questionnaireResponse =
      parser.parseResource(questionnaireResponseJson) as QuestionnaireResponse

    val patient = ResourceMapper.extract(questionnaire, questionnaireResponse) as Patient

    val birthDate = patient.birthDateElement
    assertThat(birthDate.year).isEqualTo(2021)
    assertThat(birthDate.month).isEqualTo(0)
    assertThat(birthDate.day).isEqualTo(1)
    assertThat(patient.active).isTrue()
  }

  companion object {
    val parser: IParser = FhirContext.forR4().newJsonParser()
  }
}
