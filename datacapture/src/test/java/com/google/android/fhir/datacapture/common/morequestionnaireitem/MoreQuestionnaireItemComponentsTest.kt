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

package com.google.android.fhir.datacapture.common.morequestionnaireitem

import android.os.Build
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.datacapture.createQuestionnaireResponseItem
import com.google.common.truth.Truth
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.IntegerType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MoreQuestionnaireItemComponentsTest {

  @Test
  fun createQuestionnaireResponse() {
    val questionnaireJson =
      """
        {
    "meta": {
      "profile": [
        "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire|2.7"
      ],
      "tag": [
        {
          "code": "lformsVersion: 25.1.3"
        }
      ]
    },
    "status": "draft",
    "resourceType": "Questionnaire",
    "item": [
      {
        "type": "choice",
        "code": [],
        "extension": [
          {
            "url": "http://hl7.org/fhir/StructureDefinition/questionnaire-itemControl",
            "valueCodeableConcept": {
              "coding": [
                {
                  "system": "http://hl7.org/fhir/questionnaire-item-control",
                  "code": "radio-button",
                  "display": "Radio Button"
                }
              ],
              "text": "Radio Button"
            }
          }
        ],
        "required": true,
        "linkId": "1",
        "text": "Please select your gender",
        "prefix": "Q1:",
        "initial": [
          {
            "valueCoding": {
              "code": "male",
              "display": "Male",
              "system": "http://hl7.org/fhir/gender-identity"
            }
          }
        ],
        "answerOption": [
          {
            "valueCoding": {
              "code": "male",
              "display": "Male",
              "system": "http://hl7.org/fhir/gender-identity"
            }
          },
          {
            "valueCoding": {
              "code": "female",
              "display": "Female",
              "system": "http://hl7.org/fhir/gender-identity"
            }
          },
          {
            "valueCoding": {
              "code": "transgender-female",
              "display": "Transgender Female",
              "system": "http://hl7.org/fhir/gender-identity"
            }
          },
          {
            "valueCoding": {
              "code": "transgender-male",
              "display": "Transgender Male",
              "system": "http://hl7.org/fhir/gender-identity"
            }
          }
        ]
      },
      {
        "type": "choice",
        "code": [],
        "extension": [
          {
            "url": "http://hl7.org/fhir/StructureDefinition/questionnaire-itemControl",
            "valueCodeableConcept": {
              "coding": [
                {
                  "system": "http://hl7.org/fhir/questionnaire-item-control",
                  "code": "drop-down",
                  "display": "Drop down"
                }
              ],
              "text": "Drop down"
            }
          }
        ],
        "required": true,
        "linkId": "3",
        "text": "Please select your nationality",
        "prefix": "Q3:",
        "initial": [
          {
            "valueCoding": {
              "code": "south-africa",
              "display": "South Africa"
            }
          }
        ],
        "answerOption": [
          {
            "valueCoding": {
              "code": "south-africa",
              "display": "South Africa"
            }
          },
          {
            "valueCoding": {
              "code": "zimbabwe",
              "display": "Zimbabwe"
            }
          },
          {
            "valueCoding": {
              "code": "mozambique",
              "display": "Mozambique"
            }
          },
          {
            "valueCoding": {
              "code": "malawi",
              "display": "Malawi"
            }
          },
          {
            "valueCoding": {
              "code": "other",
              "display": "Other"
            }
          }
        ]
      },
      {
        "type": "group",
        "code": [],
        "required": false,
        "linkId": "11",
        "text": "Please select Gender and Age of all sexual  partners with whom you had sex in last 3 months.",
        "prefix": "Q11:",
        "item": [
          {
            "type": "group",
            "code": [],
            "required": false,
            "repeats": true,
            "linkId": "11.1",
            "text": "Male",
            "item": [
              {
                "type": "integer",
                "code": [],
                "required": false,
                "linkId": "11.1.1",
                "text": "0-14",
                "initial": [
                  {
                    "valueInteger": 0
                  }
                ]
              },
              {
                "type": "integer",
                "code": [],
                "required": false,
                "linkId": "11.1.2",
                "text": "15-19",
                "initial": [
                  {
                    "valueInteger": 0
                  }
                ]
              },
              {
                "type": "integer",
                "code": [],
                "required": false,
                "linkId": "11.1.3",
                "text": "20-24",
                "initial": [
                  {
                    "valueInteger": 0
                  }
                ]
              },
              {
                "type": "integer",
                "code": [],
                "required": false,
                "linkId": "11.1.4",
                "text": "25-29",
                "initial": [
                  {
                    "valueInteger": 0
                  }
                ]
              },
              {
                "type": "integer",
                "code": [],
                "required": false,
                "linkId": "11.1.5",
                "text": "30-34",
                "initial": [
                  {
                    "valueInteger": 0
                  }
                ]
              },
              {
                "type": "integer",
                "code": [],
                "required": false,
                "linkId": "11.1.6",
                "text": "35-39",
                "initial": [
                  {
                    "valueInteger": 0
                  }
                ]
              },
              {
                "type": "integer",
                "code": [],
                "required": false,
                "linkId": "11.1.7",
                "text": "40-44",
                "initial": [
                  {
                    "valueInteger": 0
                  }
                ]
              },
              {
                "type": "integer",
                "code": [],
                "required": false,
                "linkId": "11.1.8",
                "text": "45-49",
                "initial": [
                  {
                    "valueInteger": 0
                  }
                ]
              },
              {
                "type": "integer",
                "code": [],
                "required": false,
                "linkId": "11.1.9",
                "text": "50-54",
                "initial": [
                  {
                    "valueInteger": 0
                  }
                ]
              },
              {
                "type": "integer",
                "code": [],
                "required": false,
                "linkId": "11.1.10",
                "text": "55-59",
                "initial": [
                  {
                    "valueInteger": 0
                  }
                ]
              },
              {
                "type": "integer",
                "code": [],
                "required": false,
                "linkId": "11.1.11",
                "text": "60-64",
                "initial": [
                  {
                    "valueInteger": 0
                  }
                ]
              }
            ]
          },
          {
            "type": "group",
            "code": [],
            "required": false,
            "repeats": true,
            "linkId": "11.2",
            "text": "Female",
            "item": [
              {
                "type": "integer",
                "code": [],
                "required": false,
                "linkId": "11.2.1",
                "text": "0-14",
                "initial": [
                  {
                    "valueInteger": 0
                  }
                ]
              },
              {
                "type": "integer",
                "code": [],
                "required": false,
                "linkId": "11.2.2",
                "text": "15-19",
                "initial": [
                  {
                    "valueInteger": 0
                  }
                ]
              },
              {
                "type": "integer",
                "code": [],
                "required": false,
                "linkId": "11.2.3",
                "text": "20-24",
                "initial": [
                  {
                    "valueInteger": 0
                  }
                ]
              },
              {
                "type": "integer",
                "code": [],
                "required": false,
                "linkId": "11.2.4",
                "text": "25-29",
                "initial": [
                  {
                    "valueInteger": 0
                  }
                ]
              },
              {
                "type": "integer",
                "code": [],
                "required": false,
                "linkId": "11.2.5",
                "text": "30-34",
                "initial": [
                  {
                    "valueInteger": 0
                  }
                ]
              },
              {
                "type": "integer",
                "code": [],
                "required": false,
                "linkId": "11.2.6",
                "text": "35-39",
                "initial": [
                  {
                    "valueInteger": 0
                  }
                ]
              },
              {
                "type": "integer",
                "code": [],
                "required": false,
                "linkId": "11.2.7",
                "text": "40-44",
                "initial": [
                  {
                    "valueInteger": 0
                  }
                ]
              },
              {
                "type": "integer",
                "code": [],
                "required": false,
                "linkId": "11.2.8",
                "text": "45-49",
                "initial": [
                  {
                    "valueInteger": 0
                  }
                ]
              },
              {
                "type": "integer",
                "code": [],
                "required": false,
                "linkId": "11.2.9",
                "text": "50-54",
                "initial": [
                  {
                    "valueInteger": 0
                  }
                ]
              },
              {
                "type": "integer",
                "code": [],
                "required": false,
                "linkId": "11.2.10",
                "text": "55-59",
                "initial": [
                  {
                    "valueInteger": 0
                  }
                ]
              },
              {
                "type": "integer",
                "code": [],
                "required": false,
                "linkId": "11.2.11",
                "text": "60-64",
                "initial": [
                  {
                    "valueInteger": 0
                  }
                ]
              }
            ]
          }
        ]
      },
      {
        "type": "boolean",
        "required": true,
        "linkId": "30",
        "initial": [
          {
            "valueBoolean": true
          }
        ],
        "text": "In last 1 year, did you experience persistent “Shortness of Breath” or “Chest Pain”?",
        "prefix": "Q30:"
      }
    ]
  }
      """.trimIndent()

    val iParser: IParser = FhirContext.forR4().newJsonParser()

    val questionnaire =
      iParser.parseResource(org.hl7.fhir.r4.model.Questionnaire::class.java, questionnaireJson) as
        org.hl7.fhir.r4.model.Questionnaire

    val questionnaireResponse = questionnaire.item.map { it.createQuestionnaireResponseItem() }

    Truth.assertThat((questionnaireResponse[0].answer[0].value as Coding).code).isEqualTo("male")
    Truth.assertThat((questionnaireResponse[1].answer[0].value as Coding).code)
      .isEqualTo("south-africa")
    Truth.assertThat(
        (questionnaireResponse[2].item[0].item[0].answer[0].value as IntegerType).value
      )
      .isEqualTo(0)
    Truth.assertThat((questionnaireResponse[3].answer[0].value as BooleanType).booleanValue())
      .isEqualTo(true)
  }
}
