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
import java.text.SimpleDateFormat
import java.util.Date
import org.hl7.fhir.r4.model.Patient
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
          "id": "client-registration-sample",
          "status": "active",
          "date": "2020-11-18T07:24:47.111Z",
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
              "linkId": "PR",
              "type": "group",
              "item": [
                {
                  "linkId": "PR-name",
                  "type": "group",
                  "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.name",
                  "item": [
                    {
                      "linkId": "PR-name-text",
                      "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.name.given",
                      "type": "string",
                      "text": "First Name"
                    },
                    {
                      "linkId": "PR-name-family",
                      "definition": "http://hl7.org/fhir/StructureDefinition/datatypes#Patient.name.family",
                      "type": "string",
                      "text": "Family Name"
                    }
                  ]
                },
                {
                  "linkId": "patient-0-birth-date",
                  "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.birthDate",
                  "type": "date",
                  "text": "Date of Birth"
                },
                {
                  "linkId": "patient-0-gender",
                  "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.gender",
                  "type": "string",
                  "text": "Gender"
                },
                {
                  "linkId": "PR-telecom",
                  "type": "group",
                  "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.telecom",
                  "item": [
                    {
                      "linkId": "PR-telecom-system",
                      "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.telecom.system",
                      "type": "string",
                      "text": "system",
                      "initial": [
                        {
                          "valueString": "phone"
                        }
                      ],
                      "enableWhen": [
                        {
                          "question": "patient-0-gender",
                          "operator": "=",
                          "answerString": "ok"
                        }
                      ]
                    },
                    {
                      "linkId": "PR-telecom-value",
                      "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.telecom.value",
                      "type": "string",
                      "text": "Phone Number"
                    }
                  ]
                },
                {
                  "linkId": "PR-active",
                  "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.active",
                  "type": "boolean",
                  "text": "Is Active?"
                }
              ]
            }
          ]
        }
        """.trimIndent()

    val questionnaireResponseJson =
      """
        {
          "resourceType": "QuestionnaireResponse",
          "questionnaire": "client-registration-sample",
          "item": [
            {
              "linkId": "PR",
              "item": [
                {
                  "linkId": "PR-name",
                  "item": [
                    {
                      "linkId": "PR-name-given",
                      "answer": [
                        {
                          "valueString": "John"
                        }
                      ]
                    },
                    {
                      "linkId": "PR-name-family",
                      "answer": [
                        {
                          "valueString": "Doe"
                        }
                      ]
                    }
                  ]
                },
                {
                  "linkId": "patient-0-birth-date",
                  "answer": [
                    {
                      "valueDate": "2021-01-01"
                    }
                  ]
                },
                {
                  "linkId": "patient-0-gender",
                  "answer": [
                    {
                      "valueString": "male"
                    }
                  ]
                },
                {
                  "linkId": "PR-telecom",
                  "item": [
                    {
                      "linkId": "PR-telecom-system"
                    },
                    {
                      "linkId": "PR-telecom-value",
                      "answer": [
                        {
                          "valueString": "+254711001122"
                        }
                      ]
                    }
                  ]
                },
                {
                  "linkId": "PR-active",
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

    val iParser: IParser = FhirContext.forR4().newJsonParser()

    val uriTestQuestionnaire =
      iParser.parseResource(org.hl7.fhir.r4.model.Questionnaire::class.java, questionnaireJson) as
        org.hl7.fhir.r4.model.Questionnaire

    val uriTestQuestionnaireResponse =
      iParser.parseResource(
        org.hl7.fhir.r4.model.QuestionnaireResponse::class.java,
        questionnaireResponseJson
      ) as
        org.hl7.fhir.r4.model.QuestionnaireResponse

    val patient =
      ResourceMapper.extract(uriTestQuestionnaire, uriTestQuestionnaireResponse) as Patient

    assertThat(patient.birthDate).isEqualTo("2021-01-01".toDateFromFormatYyyyMmDd())
    assertThat(patient.active).isTrue()
    assertThat(patient.name.first().given.first().toString()).isEqualTo("John")
    assertThat(patient.name.first().family).isEqualTo("Doe")
  }

  @Test
  fun `extract() should allow extracting with unanswered questions`() {
    val questionnaireJson =
      """
        {
          "resourceType": "Questionnaire",
          "id": "client-registration-sample",
          "status": "active",
          "date": "2020-11-18T07:24:47.111Z",
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
              "linkId": "PR",
              "type": "group",
              "item": [
                {
                  "linkId": "PR-name",
                  "type": "group",
                  "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.name",
                  "extension": [
                    {
                      "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemContext",
                      "valueExpression": {
                        "language": "application/x-fhir-query",
                        "expression": "HumanName",
                        "name": "humanName"
                      }
                    }
                  ],
                  "item": [
                    {
                      "linkId": "PR-name-text",
                      "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.name.given",
                      "type": "string",
                      "text": "First Name"
                    },
                    {
                      "linkId": "PR-name-family",
                      "definition": "http://hl7.org/fhir/StructureDefinition/datatypes#Patient.name.family",
                      "type": "string",
                      "text": "Family Name"
                    }
                  ]
                },
                {
                  "linkId": "patient-0-birth-date",
                  "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.birthDate",
                  "type": "date",
                  "text": "Date of Birth"
                },
                {
                  "linkId": "patient-0-gender",
                  "extension": [
                    {
                      "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemContext",
                      "valueExpression": {
                        "language": "application/x-fhir-query",
                        "expression": "Enumerations$""" +
        """AdministrativeGender",
                        "name": "administrativeGender"
                      }
                    }
                  ],
                  "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.gender",
                  "type": "string",
                  "text": "Gender"
                },
                {
                  "linkId": "PR-telecom",
                  "type": "group",
                  "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.telecom",
                  "extension": [
                    {
                      "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemContext",
                      "valueExpression": {
                        "language": "application/x-fhir-query",
                        "expression": "ContactPoint",
                        "name": "contactPoint"
                      }
                    }
                  ],
                  "item": [
                    {
                      "linkId": "PR-telecom-system",
                      "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.telecom.system",
                      "extension": [
                        {
                          "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemContext",
                          "valueExpression": {
                            "language": "application/x-fhir-query",
                            "expression": "ContactPoint$""" +
        """ContactPointSystem",
                            "name": "contactPointSystem"
                          }
                        }
                      ],
                      "type": "string",
                      "text": "system",
                      "initial": [
                        {
                          "valueString": "phone"
                        }
                      ],
                      "enableWhen": [
                        {
                          "question": "patient-0-gender",
                          "operator": "=",
                          "answerString": "ok"
                        }
                      ]
                    },
                    {
                      "linkId": "PR-telecom-value",
                      "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.telecom.value",
                      "type": "string",
                      "text": "Phone Number"
                    }
                  ]
                },
                {
                  "linkId": "PR-active",
                  "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.active",
                  "type": "boolean",
                  "text": "Is Active?"
                }
              ]
            }
          ]
        }
        """.trimIndent()

    val questionnaireResponseJson =
      """
        {
          "resourceType": "QuestionnaireResponse",
          "questionnaire": "Questionnaire/client-registration-sample",
          "item": [
            {
              "linkId": "PR",
              "item": [
                {
                  "linkId": "PR-name",
                  "item": [
                    {
                      "linkId": "PR-name-text",
                      "answer": [
                        {
                          "valueString": "Simon"
                        }
                      ]
                    },
                    {
                      "linkId": "PR-name-family",
                      "answer": [
                        {
                          "valueString": "Crawford"
                        }
                      ]
                    }
                  ]
                },
                {
                  "linkId": "patient-0-birth-date",
                  "answer": [
                    {
                      "valueDate": "2016-02-11"
                    }
                  ]
                },
                {
                  "linkId": "patient-0-gender",
                  "answer": [
                    {
                      "valueString": "female"
                    }
                  ]
                },
                {
                  "linkId": "PR-telecom",
                  "item": [
                    {
                      "linkId": "PR-telecom-system",
                      "answer": [
                        {
                          "valueString": "phone"
                        }
                      ]
                    },
                    {
                      "linkId": "PR-telecom-value"
                    }
                  ]
                },
                {
                  "linkId": "PR-address",
                  "item": [
                    {
                      "linkId": "PR-address-city",
                      "answer": [
                        {
                          "valueString": "Nairobi"
                        }
                      ]
                    },
                    {
                      "linkId": "PR-address-country",
                      "answer": [
                        {
                          "valueString": "Kenya"
                        }
                      ]
                    }
                  ]
                },
                {
                  "linkId": "PR-active"
                }
              ]
            }
          ]
        }
        """.trimIndent()

    val iParser: IParser = FhirContext.forR4().newJsonParser()

    val uriTestQuestionnaire =
      iParser.parseResource(org.hl7.fhir.r4.model.Questionnaire::class.java, questionnaireJson) as
        org.hl7.fhir.r4.model.Questionnaire

    val uriTestQuestionnaireResponse =
      iParser.parseResource(
        org.hl7.fhir.r4.model.QuestionnaireResponse::class.java,
        questionnaireResponseJson
      ) as
        org.hl7.fhir.r4.model.QuestionnaireResponse

    val patient =
      ResourceMapper.extract(uriTestQuestionnaire, uriTestQuestionnaireResponse) as Patient
    assertThat(patient.birthDate).isEqualTo("2016-02-11".toDateFromFormatYyyyMmDd())
    assertThat(patient.active).isFalse()
    assertThat(patient.telecom.get(0).value).isNull()
    assertThat(patient.name.first().given.first().toString()).isEqualTo("Simon")
    assertThat(patient.name.first().family).isEqualTo("Crawford")
  }

  private fun String.toDateFromFormatYyyyMmDd(): Date? {
    return SimpleDateFormat("yyyy-MM-dd").parse(this)
  }
}
