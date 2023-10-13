/*
 * Copyright 2022-2023 Google LLC
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

import android.app.Application
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.datacapture.extensions.EXTENSION_LAUNCH_CONTEXT
import com.google.android.fhir.datacapture.extensions.EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT
import com.google.android.fhir.datacapture.extensions.ITEM_INITIAL_EXPRESSION_URL
import com.google.android.fhir.datacapture.views.factories.localDate
import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID
import kotlin.test.assertFailsWith
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.ContactPoint
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Immunization
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.ResourceFactory
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.codesystems.AdministrativeGender
import org.hl7.fhir.r4.terminologies.ConceptMapEngine
import org.hl7.fhir.r4.utils.StructureMapUtilities
import org.intellij.lang.annotations.Language
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class ResourceMapperTest {
  private val context = ApplicationProvider.getApplicationContext<Application>()
  private val iParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

  @Test
  fun `extract() should perform definition-based extraction`() = runBlocking {
    // https://developer.commure.com/docs/apis/sdc/examples#definition-based-extraction
    @Language("JSON")
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
                  "linkId": "PR-name-id",
                  "definition": "http://hl7.org/fhir/StructureDefinition/Resource#Resource.id",
                  "type": "string",
                  "text": "Patient Id"
                },
                {
                  "linkId": "patient-0-gender",
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
                        "text": "A control where choices are listed with a button beside them. The button can be toggled to select or de-select a given choice. Selecting one item deselects all others."
                      }
                    }
                  ],
                  "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.gender",
                  "type": "choice",
                  "text": "Gender:",
                  "answerOption": [
                    {
                      "valueCoding": {
                        "code": "female",
                        "display": "Female"
                      },
                      "initialSelected": true
                    },
                    {
                      "valueCoding": {
                        "code": "male",
                        "display": "Male"
                      }
                    },
                    {
                      "valueCoding": {
                        "code": "other",
                        "display": "Other"
                      }
                    },
                    {
                      "valueCoding": {
                        "code": "unknown",
                        "display": "Unknown"
                      }
                    }
                  ]
                },
                {
                  "linkId": "patient-0-marital-status",
                  "extension": [
                    {
                      "url": "http://hl7.org/fhir/StructureDefinition/questionnaire-itemControl",
                      "valueCodeableConcept": {
                        "coding": [
                          {
                            "system": "http://hl7.org/fhir/questionnaire-item-control",
                            "code": "check",
                            "display": "Check-box"
                          }
                        ],
                        "text": "A control where choices are listed with a button beside them. The button can be toggled to select or de-select a given choice. Selecting one item deselects all others."
                      }
                    }
                  ],
                  "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.maritalStatus",
                  "type": "choice",
                  "text": "Marital Status:",
                  "answerOption": [
                    {
                      "valueCoding": {
                        "code": "A",
                        "display": "Annulled"
                      }
                    },
                    {
                      "valueCoding": {
                        "code": "D",
                        "display": "Divorced"
                      }
                    },
                    {
                      "valueCoding": {
                        "code": "I",
                        "display": "Interlocutory"
                      }
                    },
                    {
                      "valueCoding": {
                        "code": "L",
                        "display": "Legally Separated"
                      }
                    },
                    {
                      "valueCoding": {
                        "code": "M",
                        "display": "Married"
                      },
                      "initialSelected": true
                    },
                    {
                      "valueCoding": {
                        "code": "P",
                        "display": "Polygamous"
                      }
                    },
                    {
                      "valueCoding": {
                        "code": "S",
                        "display": "Never Married"
                      }
                    },
                    {
                      "valueCoding": {
                        "code": "T",
                        "display": "Domestic Partner"
                      }
                    },
                    {
                      "valueCoding": {
                        "code": "U",
                        "display": "Unmarried"
                      }
                    },
                    {
                      "valueCoding": {
                        "code": "W",
                        "display": "Widowed"
                      }
                    },
                    {
                      "valueCoding": {
                        "code": "UNK",
                        "display": "Unknown"
                      }
                    }
                  ]
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
                  "linkId": "PR-contact-party",
                  "type": "group",
                  "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.contact",
                  "item": [
                    {
                      "linkId": "PR-contact-party-name",
                      "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.contact.name",
                      "type": "group",
                      "item": [
                        {
                          "linkId": "PR-contact-party-name-given",
                          "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.contact.name.given",
                          "type": "string",
                          "text": "First Name"
                        },
                        {
                          "linkId": "PR-contact-party-name-family",
                          "definition": "http://hl7.org/fhir/StructureDefinition/datatypes#Patient.contact.name.family",
                          "type": "string",
                          "text": "Family Name"
                        }
                      ]
                    }
                  ]
                },
                {
                  "linkId": "PR-active",
                  "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.active",
                  "type": "boolean",
                  "text": "Is Active?"
                },
                {
                  "linkId": "PR-multiple-birth",
                  "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.multipleBirth",
                  "type": "integer",
                  "text": "Multiple birth integer i.e. 1 is first-born, 2 is second-born"
                }
              ]
            }
          ]
        }
            """
        .trimIndent()

    @Language("JSON")
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
                       "linkId": "PR-name-text",
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
                  "linkId": "PR-name-id",
                  "answer": [
                    {
                      "valueString": "98238-adsfsa-23rfdsf"
                    }
                  ]
                },
                {
                  "linkId": "patient-0-gender",
                  "answer": [
                    {
                      "valueCoding": {
                        "code": "male",
                        "display": "Male"
                      }
                    }
                  ]
                },
                {
                  "linkId": "patient-0-marital-status",
                  "answer": [
                    {
                      "valueCoding": {
                        "code": "S",
                        "display": "Never Married"
                      }
                    }
                  ]
                },
                {
                  "linkId": "PR-telecom",
                  "item": [
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
                  "linkId": "PR-contact-party",
                  "item": [
                    {
                      "linkId": "PR-contact-party-name",
                      "item": [
                        {
                          "linkId": "PR-contact-party-name-given",
                          "answer": [
                            {
                              "valueString": "Brenda"
                            }
                          ]
                        },
                        {
                          "linkId": "PR-contact-party-name-family",
                          "answer": [
                            {
                              "valueString": "Penman"
                            }
                          ]
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
                },
                {
                  "linkId": "PR-multiple-birth",
                  "answer": [
                    {
                      "valueInteger": 2
                    }
                  ]
                }
              ]
            }
          ]
        }
            """
        .trimIndent()

    val uriTestQuestionnaire =
      iParser.parseResource(Questionnaire::class.java, questionnaireJson) as Questionnaire

    val uriTestQuestionnaireResponse =
      iParser.parseResource(QuestionnaireResponse::class.java, questionnaireResponseJson)
        as QuestionnaireResponse

    val patient =
      ResourceMapper.extract(uriTestQuestionnaire, uriTestQuestionnaireResponse).entry[0].resource
        as Patient

    assertThat(patient.birthDate).isEqualTo("2021-01-01".toDateFromFormatYyyyMmDd())
    assertThat(patient.active).isTrue()
    assertThat(patient.name.first().given.first().toString()).isEqualTo("John")
    assertThat(patient.name.first().family).isEqualTo("Doe")
    assertThat(patient.multipleBirthIntegerType.value).isEqualTo(2)
    assertThat(patient.contact[0].name.given.first().toString()).isEqualTo("Brenda")
    assertThat(patient.contact[0].name.family).isEqualTo("Penman")
    assertThat(patient.telecom[0].system).isNull()
    assertThat(patient.telecom[0].value).isEqualTo("+254711001122")
  }

  @Test
  fun `extract() should extract list of non primitive values`() = runBlocking {
    @Language("JSON")
    val questionnaireJson =
      """
      {
        "resourceType": "Questionnaire",
        "item": [
          {
            "linkId": "observation",
            "type": "group",
            "extension": [
              {
                "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemExtractionContext",
                "valueExpression": {
                  "expression": "Observation"
                }
              }
            ],
            "item": [
              {
                "linkId": "observation-value",
                "type": "group",
                "definition": "http://hl7.org/fhir/StructureDefinition/Observation#Observation.valueCodeableConcept",
                "item": [
                  {
                    "linkId": "observation-value-coding",
                    "type": "choice",
                    "definition": "http://hl7.org/fhir/StructureDefinition/Observation#Observation.valueCodeableConcept.coding"
                  }
                ]
              }
            ]
          }
        ]
      }
            """
        .trimIndent()

    @Language("JSON")
    val questionnaireResponseJson =
      """
        {
          "resourceType": "QuestionnaireResponse",
          "item": [
            {
              "linkId": "observation",
              "item": [
                {
                  "linkId": "observation-value",
                  "item": [
                    {
                      "linkId": "observation-value-coding",
                      "answer": [
                        {
                          "valueCoding": {
                            "system": "test-coding-system",
                            "code": "test-coding-code",
                            "display": "Test Coding Display"
                          }
                        }
                      ]
                    }
                  ]
                }
              ]
            }
          ]
        }
            """
        .trimIndent()

    val uriTestQuestionnaire =
      iParser.parseResource(Questionnaire::class.java, questionnaireJson) as Questionnaire

    val uriTestQuestionnaireResponse =
      iParser.parseResource(QuestionnaireResponse::class.java, questionnaireResponseJson)
        as QuestionnaireResponse

    val observation =
      ResourceMapper.extract(uriTestQuestionnaire, uriTestQuestionnaireResponse)
        .entry
        .single()
        .resource as Observation
    assertThat(observation.valueCodeableConcept.coding[0].code).isEqualTo("test-coding-code")
  }

  @Test
  fun `extract() should perform definition-based extraction for valueCode itemExtractionContext`() =
    runBlocking {
      // https://developer.commure.com/docs/apis/sdc/examples#definition-based-extraction
      @Language("JSON")
      val questionnaireJson =
        """
        {
          "resourceType": "Questionnaire",
          "extension": [
            {
              "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemExtractionContext",
              "valueCode":  "Patient"
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
                  "linkId": "patient-0-gender",
                  "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.gender",
                  "type": "choice",
                  "text": "Gender:"
                }
               ]
            }
          ]
        }
                """
          .trimIndent()

      @Language("JSON")
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
                       "linkId": "PR-name-text",
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
                  "linkId": "patient-0-gender",
                  "answer": [
                    {
                      "valueCoding": {
                        "code": "male",
                        "display": "Male"
                      }
                    }
                  ]
                }
                ]
            }
          ]
        }
                """
          .trimIndent()

      val iParser: IParser = FhirContext.forR4().newJsonParser()

      val uriTestQuestionnaire =
        iParser.parseResource(Questionnaire::class.java, questionnaireJson) as Questionnaire

      val uriTestQuestionnaireResponse =
        iParser.parseResource(QuestionnaireResponse::class.java, questionnaireResponseJson)
          as QuestionnaireResponse

      val patient =
        ResourceMapper.extract(uriTestQuestionnaire, uriTestQuestionnaireResponse).entry[0].resource
          as Patient

      assertThat(patient.name.first().given.first().toString()).isEqualTo("John")
      assertThat(patient.name.first().family).isEqualTo("Doe")
      assertThat(patient.gender.display).isEqualTo(AdministrativeGender.MALE.display)
    }

  @Test
  fun `extract() should extract choice value fields`() = runBlocking {
    // https://developer.commure.com/docs/apis/sdc/examples#definition-based-extraction
    @Language("JSON")
    val questionnaireJson =
      """
            {
              "resourceType": "Questionnaire",
              "item": [
                {
                  "linkId": "9",
                  "type": "group",
                  "extension": [
                    {
                      "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemExtractionContext",
                      "valueExpression": {
                        "expression": "Observation"
                      }
                    }
                  ],
                  "item": [
                    {
                      "linkId": "9.1",
                      "type": "string",
                      "definition": "https://hl7.org/fhir/R4/observation.html#Observation.valueString"
                    },
                    {
                      "linkId": "9.1.3",
                      "type": "string",
                      "definition": "http://hl7.org/fhir/StructureDefinition/Observation#Observation.code",
                      "extension": [
                        {
                          "url": "http://hl7.org/fhir/StructureDefinition/questionnaire-hidden",
                          "valueBoolean": true
                        }
                      ],
                      "initial": [
                        {
                          "valueCoding": {
                            "code": "8888",
                            "display": "dummy",
                            "system": "dummy"
                          }
                        }
                      ]
                    }
                  ]
                }
              ]
            }
            """
        .trimIndent()

    @Language("JSON")
    val questionnaireResponseJson =
      """
            {
              "resourceType": "QuestionnaireResponse",
              "item": [
                {
                  "linkId": "9",
                  "item": [
                    {
                      "linkId": "9.1",
                      "answer": [
                        {
                          "valueString": "world"
                        }
                      ]
                    },
                    {
                      "linkId": "9.1.3",
                      "answer": [
                        {
                          "valueCoding": {
                            "system": "dummy",
                            "code": "8888",
                            "display": "dummy"
                          }
                        }
                      ]
                    }
                  ]
                }
              ]
            }
            """
        .trimIndent()

    val uriTestQuestionnaire =
      iParser.parseResource(Questionnaire::class.java, questionnaireJson) as Questionnaire

    val uriTestQuestionnaireResponse =
      iParser.parseResource(QuestionnaireResponse::class.java, questionnaireResponseJson)
        as QuestionnaireResponse

    val observation =
      ResourceMapper.extract(uriTestQuestionnaire, uriTestQuestionnaireResponse)
        .entry
        .single()
        .resource as Observation

    assertThat(observation.valueStringType.value).isEqualTo("world")
  }

  @Test
  fun `populate() should correctly populate current date from fhirpath expression in QuestionnaireResponse`() =
    runBlocking {
      val questionnaire =
        Questionnaire()
          .apply {
            addExtension().apply {
              url = EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT
              extension =
                listOf(
                  Extension("name", Coding(EXTENSION_LAUNCH_CONTEXT, "mother", "Mother")),
                  Extension("type", CodeType("Patient")),
                )
            }
          }
          .addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "patient-dob"
              type = Questionnaire.QuestionnaireItemType.TEXT
              extension =
                listOf(
                  Extension(
                    ITEM_INITIAL_EXPRESSION_URL,
                    Expression().apply {
                      language = "text/fhirpath"
                      expression = "today()"
                    },
                  ),
                )
            },
          )

      val patientId = UUID.randomUUID().toString()
      val patient = Patient().apply { id = "Patient/$patientId/_history/2" }
      val questionnaireResponse = ResourceMapper.populate(questionnaire, mapOf("mother" to patient))

      assertThat((questionnaireResponse.item[0].answer[0].value as DateType).localDate)
        .isEqualTo((DateType(Date())).localDate)
    }

  @Test
  fun `extract() should perform definition-based extraction with unanswered questions`() =
    runBlocking {
      @Language("JSON")
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
                        "text": "A control where choices are listed with a button beside them. The button can be toggled to select or de-select a given choice. Selecting one item deselects all others."
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
                """
          .trimIndent()

      @Language("JSON")
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
                """
          .trimIndent()

      val uriTestQuestionnaire =
        iParser.parseResource(Questionnaire::class.java, questionnaireJson) as Questionnaire

      val uriTestQuestionnaireResponse =
        iParser.parseResource(QuestionnaireResponse::class.java, questionnaireResponseJson)
          as QuestionnaireResponse

      val patient =
        ResourceMapper.extract(uriTestQuestionnaire, uriTestQuestionnaireResponse).entry[0].resource
          as Patient

      assertThat(patient.birthDate).isEqualTo("2016-02-11".toDateFromFormatYyyyMmDd())
      assertThat(patient.active).isFalse()
      assertThat(patient.telecom.get(0).value).isNull()
      assertThat(patient.name.first().given.first().toString()).isEqualTo("Simon")
      assertThat(patient.name.first().family).isEqualTo("Crawford")
    }

  @Test
  fun `extract_updateIntegerObservationForDecimalDefinition_shouldUpdateAsDecimal() `() =
    runBlocking {
      @Language("JSON")
      val questionnaireJson =
        """
        {
          "resourceType": "Questionnaire",
          "subjectType": [
            "Encounter"
          ],
          "item": [
            {
              "text": "Pulse Oximetry",
              "linkId": "6.0.0",
              "type": "group",
              "extension": [
                {
                  "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemExtractionContext",
                  "valueExpression": {
                    "language": "application/x-fhir-query",
                    "expression": "Observation",
                    "name": "pulse"
                  }
                }
              ],
              "item": [
                {
                  "linkId": "6.2.0",
                  "type": "group",
                  "definition": "http://hl7.org/fhir/StructureDefinition/Observation#Observation.valueQuantity",
                  "item": [
                    {
                      "text": "Pulse oximetry reading",
                      "type": "integer",
                      "linkId": "6.2.1",
                      "definition": "http://hl7.org/fhir/StructureDefinition/Observation#Observation.valueQuantity.value",
                      "extension": [
                        {
                          "url": "http://hl7.org/fhir/StructureDefinition/minValue",
                          "valueInteger": 60
                        },
                        {
                          "url": "http://hl7.org/fhir/StructureDefinition/maxValue",
                          "valueInteger": 100
                        }
                      ]
                    }
                  ]
                }
              ]
            }
          ]
        }
                """
          .trimIndent()

      @Language("JSON")
      val questionnaireResponseJson =
        """
        {
          "resourceType": "QuestionnaireResponse",
          "item": [
            {
              "linkId": "6.0.0",
              "item": [
                {
                  "linkId": "6.2.0",
                  "item": [
                    {
                      "linkId": "6.2.1",
                      "answer": [
                        {
                          "valueInteger": 90
                        }
                      ]
                    }
                  ]
                }
              ]
            }
          ]
        }
                """
          .trimIndent()

      val pulseOximetryQuestionnaire =
        iParser.parseResource(Questionnaire::class.java, questionnaireJson) as Questionnaire

      val pulseOximetryQuestionnaireResponse =
        iParser.parseResource(QuestionnaireResponse::class.java, questionnaireResponseJson)
          as QuestionnaireResponse

      val observation =
        ResourceMapper.extract(pulseOximetryQuestionnaire, pulseOximetryQuestionnaireResponse)
          .entry[0]
          .resource as Observation

      assertThat(observation.valueQuantity.value).isEqualTo(BigDecimal(90))
    }

  @Test
  fun `populate() should fill QuestionnaireResponse with values when given a single Resource`() =
    runBlocking {
      @Language("JSON")
      val questionnaireJson =
        """
        {
          "resourceType": "Questionnaire",
          "id": "client-registration-sample",
          "extension": [
            {
              "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-launchContext",
              "extension": [
                {
                  "url": "name",
                  "valueCoding": {
                    "system": "http://hl7.org/fhir/uv/sdc/CodeSystem/launchContext",
                    "code": "father",
                    "display": "Patient"
                  }
                },
                {
                  "url": "type",
                  "valueCode": "Patient"
                }
              ]
            }
          ],
          "status": "active",
          "date": "2020-11-18T07:24:47.111Z",
          "subjectType": [
            "Patient"
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
                      "extension": [
                        {
                          "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression",
                          "valueExpression": {
                            "language": "text/fhirpath",
                            "expression": "%father.name.given",
                            "name": "patientName"
                          }
                        }
                      ],
                      "linkId": "PR-name-text",
                      "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.name.given",
                      "type": "string",
                      "text": "First Name"
                    },
                    {
                      "extension": [
                        {
                          "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression",
                          "valueExpression": {
                            "language": "text/fhirpath",
                            "expression": "%father.name.family",
                            "name": "patientFamily"
                          }
                        }
                      ],
                      "linkId": "PR-name-family",
                      "definition": "http://hl7.org/fhir/StructureDefinition/datatypes#HumanName.family",
                      "type": "string",
                      "text": "Family Name"
                    }
                  ]
                },
                {
                  "extension": [
                    {
                      "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression",
                      "valueExpression": {
                        "language": "text/fhirpath",
                        "expression": "%father.birthDate",
                        "name": "patientBirthDate"
                      }
                    }
                  ],
                  "linkId": "patient-0-birth-date",
                  "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.birthDate",
                  "type": "date",
                  "text": "Date of Birth"
                },
                {
                  "linkId": "patient-0-gender",
                  "extension": [
                    {
                      "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression",
                      "valueExpression": {
                        "language": "text/fhirpath",
                        "expression": "%father.gender.value",
                        "name": "patientGender"
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
                      "extension": [
                        {
                          "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression",
                          "valueExpression": {
                            "language": "text/fhirpath",
                            "expression": "%father.telecom.value",
                            "name": "patientTelecom"
                          }
                        }
                      ],
                      "linkId": "PR-telecom-value",
                      "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.telecom.value",
                      "type": "string",
                      "text": "Phone Number"
                    }
                  ]
                },
                {
                  "linkId": "PR-address",
                  "type": "group",
                  "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.address",
                  "item": [
                    {
                      "extension": [
                        {
                          "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression",
                          "valueExpression": {
                            "language": "text/fhirpath",
                            "expression": "%father.address.city",
                            "name": "patientCity"
                          }
                        }
                      ],
                      "linkId": "PR-address-city",
                      "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.address.city",
                      "type": "string",
                      "text": "City"
                    },
                    {
                      "extension": [
                        {
                          "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression",
                          "valueExpression": {
                            "language": "text/fhirpath",
                            "expression": "%father.address.country",
                            "name": "patientCity"
                          }
                        }
                      ],
                      "linkId": "PR-address-country",
                      "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.address.country",
                      "type": "string",
                      "text": "Country"
                    }
                  ]
                },
                {
                  "extension": [
                    {
                      "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression",
                      "valueExpression": {
                        "language": "text/fhirpath",
                        "expression": "%father.active",
                        "name": "patientActive"
                      }
                    }
                  ],
                  "linkId": "PR-active",
                  "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.active",
                  "type": "boolean",
                  "text": "Is Active?"
                }
              ]
            }
          ]
        }
                """
          .trimIndent()

      val uriTestQuestionnaire =
        iParser.parseResource(org.hl7.fhir.r4.model.Questionnaire::class.java, questionnaireJson)
          as Questionnaire

      val patient = createPatientResource()
      val response = ResourceMapper.populate(uriTestQuestionnaire, mapOf("father" to patient))

      val responseItem = response.item[0]
      assertThat(((responseItem.item[0].item[0].answer[0]).value as StringType).valueAsString)
        .isEqualTo("Salman")
      assertThat(((responseItem.item[0].item[1].answer[0]).value as StringType).valueAsString)
        .isEqualTo("Ali")
      assertThat(((responseItem.item[1].answer[0]).value as DateType).valueAsString)
        .isEqualTo("3896-09-17")
      assertThat(((responseItem.item[2].answer[0]).value as StringType).valueAsString)
        .isEqualTo("male")
      assertThat(((responseItem.item[3].item[1].answer[0]).value as StringType).valueAsString)
        .isEqualTo("12345")
      assertThat(((responseItem.item[4].item[0].answer[0]).value as StringType).valueAsString)
        .isEqualTo("Lahore")
      assertThat(((responseItem.item[4].item[1].answer[0]).value as StringType).valueAsString)
        .isEqualTo("Pakistan")
      assertThat(((responseItem.item[5].answer[0]).value as BooleanType).booleanValue())
        .isEqualTo(true)
    }

  @Test
  fun `populate() should fill QuestionnaireResponse with values when given multiple Resources`() =
    runBlocking {
      val questionnaire =
        Questionnaire()
          .apply {
            addExtension().apply {
              url = EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT
              extension =
                listOf(
                  Extension("name", Coding(EXTENSION_LAUNCH_CONTEXT, "father", "Father")),
                  Extension("type", CodeType("Patient")),
                )
            }
            addExtension().apply {
              url = EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT
              extension =
                listOf(
                  Extension("name", Coding(EXTENSION_LAUNCH_CONTEXT, "mother", "Mother")),
                  Extension("type", CodeType("Patient")),
                )
            }
            addExtension().apply {
              url = EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT
              extension =
                listOf(
                  Extension(
                    "name",
                    Coding(
                      EXTENSION_LAUNCH_CONTEXT,
                      "registration-encounter",
                      "Registration Encounter",
                    ),
                  ),
                  Extension("type", CodeType("Encounter")),
                )
            }
          }
          .addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "first-name-father"
              type = Questionnaire.QuestionnaireItemType.TEXT
              extension =
                listOf(
                  Extension(
                    ITEM_INITIAL_EXPRESSION_URL,
                    Expression().apply {
                      language = "text/fhirpath"
                      expression = "%father.name.given"
                    },
                  ),
                )
            },
          )
          .addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "first-name-mother"
              type = Questionnaire.QuestionnaireItemType.TEXT
              extension =
                listOf(
                  Extension(
                    ITEM_INITIAL_EXPRESSION_URL,
                    Expression().apply {
                      language = "text/fhirpath"
                      expression = "%mother.name.given"
                    },
                  ),
                )
            },
          )
          .addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "encounter-reason"
              type = Questionnaire.QuestionnaireItemType.TEXT
              extension =
                listOf(
                  Extension(
                    ITEM_INITIAL_EXPRESSION_URL,
                    Expression().apply {
                      language = "text/fhirpath"
                      expression = "%registration-encounter.reasonCode[0].text"
                    },
                  ),
                )
            },
          )

      val patientFather =
        Patient().apply {
          active = true
          gender = Enumerations.AdministrativeGender.MALE
          name = listOf(HumanName().apply { given = mutableListOf(StringType("Salman")) })
        }

      val patientMother =
        Patient().apply {
          active = true
          gender = Enumerations.AdministrativeGender.FEMALE
          name = listOf(HumanName().apply { given = mutableListOf(StringType("Fatima")) })
        }

      val encounter =
        Encounter().apply {
          addReasonCode().apply { addCoding().apply { text = "Registration Task" } }
        }

      val questionnaireResponse =
        ResourceMapper.populate(
          questionnaire,
          mapOf(
            "father" to patientFather,
            "mother" to patientMother,
            "registration-encounter" to encounter,
          ),
        )

      assertThat((questionnaireResponse.item[0].answer[0].value as StringType).valueAsString)
        .isEqualTo("Salman")
      assertThat(((questionnaireResponse.item[1].answer[0]).value as StringType).valueAsString)
        .isEqualTo("Fatima")
      assertThat(((questionnaireResponse.item[2].answer[0]).value as StringType).valueAsString)
        .isEqualTo("Registration Task")
    }

  @Test
  fun `populate() should not fill QuestionnaireResponse with values if the intended launch context extension is not declared`():
    Unit = runBlocking {
    val questionnaire =
      Questionnaire()
        .apply {
          addExtension().apply {
            url = EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT
            extension =
              listOf(
                Extension("name", Coding(EXTENSION_LAUNCH_CONTEXT, "father", "Father")),
                Extension("type", CodeType("Patient")),
              )
          }
        }
        .addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "first-name-father"
            type = Questionnaire.QuestionnaireItemType.TEXT
            extension =
              listOf(
                Extension(
                  ITEM_INITIAL_EXPRESSION_URL,
                  Expression().apply {
                    language = "text/fhirpath"
                    expression = "%father.name.given"
                  },
                ),
              )
          },
        )
        .addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "first-name-mother"
            type = Questionnaire.QuestionnaireItemType.TEXT
            extension =
              listOf(
                Extension(
                  ITEM_INITIAL_EXPRESSION_URL,
                  Expression().apply {
                    language = "text/fhirpath"
                    expression = "%mother.name.given"
                  },
                ),
              )
          },
        )

    val patientFather =
      Patient().apply {
        active = true
        gender = Enumerations.AdministrativeGender.MALE
        name = listOf(HumanName().apply { given = mutableListOf(StringType("Salman")) })
      }

    val patientMother =
      Patient().apply {
        active = true
        gender = Enumerations.AdministrativeGender.FEMALE
        name = listOf(HumanName().apply { given = mutableListOf(StringType("Fatima")) })
      }

    val questionnaireResponse =
      ResourceMapper.populate(
        questionnaire,
        mapOf("father" to patientFather, "mother" to patientMother),
      )

    assertThat((questionnaireResponse.item[0].answer[0].value as StringType).valueAsString)
      .isEqualTo("Salman")
    assertFailsWith<IndexOutOfBoundsException> {
      assertThat(((questionnaireResponse.item[1].answer[0]).value as StringType).valueAsString)
        .isEqualTo("Fatima")
    }
  }

  @Test
  fun `populate() should correctly populate IdType value in QuestionnaireResponse`() = runBlocking {
    val questionnaire =
      Questionnaire()
        .apply {
          addExtension().apply {
            url = EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT
            extension =
              listOf(
                Extension("name", Coding(EXTENSION_LAUNCH_CONTEXT, "father", "Father")),
                Extension("type", CodeType("Patient")),
              )
          }
        }
        .addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "patient-id"
            type = Questionnaire.QuestionnaireItemType.TEXT
            extension =
              listOf(
                Extension(
                  ITEM_INITIAL_EXPRESSION_URL,
                  Expression().apply {
                    language = "text/fhirpath"
                    expression = "%father.id"
                  },
                ),
              )
          },
        )

    val patientId = UUID.randomUUID().toString()
    val patient = Patient().apply { id = "Patient/$patientId" }
    val questionnaireResponse = ResourceMapper.populate(questionnaire, mapOf("father" to patient))

    assertThat((questionnaireResponse.item[0].answer[0].value as StringType).value)
      .isEqualTo(patientId)
  }

  @Test
  fun `populate() should correctly populate Reference value in QuestionnaireResponse`() =
    runBlocking {
      val questionnaire =
        Questionnaire()
          .apply {
            addExtension().apply {
              url = EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT
              extension =
                listOf(
                  Extension("name", Coding(EXTENSION_LAUNCH_CONTEXT, "father", "Father")),
                  Extension("type", CodeType("Patient")),
                )
            }
          }
          .addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "patient-id"
              type = Questionnaire.QuestionnaireItemType.REFERENCE
              extension =
                listOf(
                  Extension(
                    ITEM_INITIAL_EXPRESSION_URL,
                    Expression().apply {
                      language = "text/fhirpath"
                      expression = "%father.id"
                    },
                  ),
                )
            },
          )

      val patientId = UUID.randomUUID().toString()
      val patient = Patient().apply { id = "Patient/$patientId" }
      val questionnaireResponse = ResourceMapper.populate(questionnaire, mapOf("father" to patient))

      assertThat((questionnaireResponse.item[0].answer[0].value as Reference).reference)
        .isEqualTo(patient.idPart)
    }

  @Test
  fun `populate() should throw error when Reference value in QuestionnaireResponse but FhirExpression `() =
    runBlocking {
      val questionnaire =
        Questionnaire()
          .apply {
            addExtension().apply {
              url = EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT
              extension =
                listOf(
                  Extension("name", Coding(EXTENSION_LAUNCH_CONTEXT, "father", "Father")),
                  Extension("type", CodeType("Patient")),
                )
            }
          }
          .addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "patient-id"
              type = Questionnaire.QuestionnaireItemType.REFERENCE
              extension =
                listOf(
                  Extension(
                    ITEM_INITIAL_EXPRESSION_URL,
                    Expression().apply {
                      language = "text/fhirpath"
                      expression = "%father.gender"
                    },
                  ),
                )
            },
          )

      val patientId = UUID.randomUUID().toString()
      val patient =
        Patient().apply {
          id = "Patient/$patientId"
          gender = Enumerations.AdministrativeGender.MALE
        }

      val errorMessage =
        assertFailsWith<FHIRException> {
            ResourceMapper.populate(questionnaire, mapOf(Pair("father", patient)))
          }
          .localizedMessage
      assertThat(errorMessage).isEqualTo("Expression supplied does not evaluate to IdType.")
    }

  @Test
  fun `populate() should correctly populate Reference value in QuestionnaireResponse when expression resolves to type Resource`() =
    runBlocking {
      val questionnaire =
        Questionnaire()
          .apply {
            addExtension().apply {
              url = EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT
              extension =
                listOf(
                  Extension("name", Coding(EXTENSION_LAUNCH_CONTEXT, "patient", "Patient")),
                  Extension("type", CodeType("Patient")),
                )
            }
          }
          .addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              type = Questionnaire.QuestionnaireItemType.REFERENCE
              addExtension(
                Extension(
                  ITEM_INITIAL_EXPRESSION_URL,
                  Expression().apply {
                    language = "text/fhirpath"
                    expression = "%patient.id"
                  },
                ),
              )
            },
          )
      val patient = Patient().apply { id = UUID.randomUUID().toString() }
      val questionnaireResponse =
        ResourceMapper.populate(questionnaire, mapOf("patient" to patient))

      assertThat(questionnaireResponse.itemFirstRep.answerFirstRep.valueReference.reference)
        .isEqualTo(patient.id)
    }

  @Test
  fun `populate() should correctly populate IdType value with history in QuestionnaireResponse`() =
    runBlocking {
      val questionnaire =
        Questionnaire()
          .apply {
            addExtension().apply {
              url = EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT
              extension =
                listOf(
                  Extension("name", Coding(EXTENSION_LAUNCH_CONTEXT, "father", "Father")),
                  Extension("type", CodeType("Patient")),
                )
            }
          }
          .addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "patient-id"
              type = Questionnaire.QuestionnaireItemType.TEXT
              extension =
                listOf(
                  Extension(
                    ITEM_INITIAL_EXPRESSION_URL,
                    Expression().apply {
                      language = "text/fhirpath"
                      expression = "%father.id"
                    },
                  ),
                )
            },
          )

      val patientId = UUID.randomUUID().toString()
      val patient = Patient().apply { id = "Patient/$patientId/_history/2" }
      val questionnaireResponse = ResourceMapper.populate(questionnaire, mapOf("father" to patient))

      assertThat((questionnaireResponse.item[0].answer[0].value as StringType).value)
        .isEqualTo(patientId)
    }

  @Test
  fun `populate() should correctly populate Enumeration value in QuestionnaireResponse`() =
    runBlocking {
      val questionnaire =
        Questionnaire()
          .apply {
            addExtension().apply {
              url = EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT
              extension =
                listOf(
                  Extension("name", Coding(EXTENSION_LAUNCH_CONTEXT, "mother", "Mother")),
                  Extension("type", CodeType("Patient")),
                )
            }
          }
          .addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "patient-gender"
              type = Questionnaire.QuestionnaireItemType.CHOICE
              extension =
                listOf(
                  Extension(
                    ITEM_INITIAL_EXPRESSION_URL,
                    Expression().apply {
                      language = "text/fhirpath"
                      expression = "%mother.gender"
                    },
                  ),
                )
              answerOption =
                listOf(
                  Questionnaire.QuestionnaireItemAnswerOptionComponent(
                    Coding().apply {
                      code = AdministrativeGender.MALE.toCode()
                      display = AdministrativeGender.MALE.display
                    },
                  ),
                  Questionnaire.QuestionnaireItemAnswerOptionComponent(
                    Coding().apply {
                      code = AdministrativeGender.FEMALE.toCode()
                      display = AdministrativeGender.FEMALE.display
                    },
                  ),
                )
            },
          )

      val patient = Patient().apply { gender = Enumerations.AdministrativeGender.FEMALE }
      val questionnaireResponse = ResourceMapper.populate(questionnaire, mapOf("mother" to patient))

      assertThat((questionnaireResponse.item[0].answer[0].value as Coding).code).isEqualTo("female")
      assertThat((questionnaireResponse.item[0].answer[0].value as Coding).display)
        .isEqualTo("Female")
    }

  @Test
  fun `populate() should populate nested non-group questions`() = runBlocking {
    val questionnaire =
      Questionnaire()
        .apply {
          addExtension().apply {
            url = EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT
            extension =
              listOf(
                Extension("name", Coding(EXTENSION_LAUNCH_CONTEXT, "mother", "Mother")),
                Extension("type", CodeType("Patient")),
              )
          }
        }
        .addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "patient-gender"
            type = Questionnaire.QuestionnaireItemType.CHOICE
            extension =
              listOf(
                Extension(
                  ITEM_INITIAL_EXPRESSION_URL,
                  Expression().apply {
                    language = "text/fhirpath"
                    expression = "%mother.gender"
                  },
                ),
              )
            answerOption =
              listOf(
                Questionnaire.QuestionnaireItemAnswerOptionComponent(
                  Coding().apply {
                    code = AdministrativeGender.MALE.toCode()
                    display = AdministrativeGender.MALE.display
                  },
                ),
                Questionnaire.QuestionnaireItemAnswerOptionComponent(
                  Coding().apply {
                    code = AdministrativeGender.FEMALE.toCode()
                    display = AdministrativeGender.FEMALE.display
                  },
                ),
              )
            item =
              listOf(
                Questionnaire.QuestionnaireItemComponent().apply {
                  linkId = "patient-id"
                  type = Questionnaire.QuestionnaireItemType.TEXT
                  extension =
                    listOf(
                      Extension(
                        ITEM_INITIAL_EXPRESSION_URL,
                        Expression().apply {
                          language = "text/fhirpath"
                          expression = "%mother.id"
                        },
                      ),
                    )
                },
              )
          },
        )

    val patientId = UUID.randomUUID().toString()
    val patient =
      Patient().apply {
        gender = Enumerations.AdministrativeGender.FEMALE
        id = "Patient/$patientId/_history/2"
      }
    val questionnaireResponse = ResourceMapper.populate(questionnaire, mapOf("mother" to patient))

    assertThat((questionnaireResponse.item[0].answer[0].value as Coding).code).isEqualTo("female")
    assertThat((questionnaireResponse.item[0].answer[0].value as Coding).display)
      .isEqualTo(AdministrativeGender.FEMALE.display)
    assertThat(
        (questionnaireResponse.item[0].answer[0].item[0].answer[0].value as StringType).value,
      )
      .isEqualTo(patientId)
  }

  private fun createPatientResource(): Patient {
    return Patient().apply {
      active = true
      birthDate = Date(1996, 8, 17)
      gender = Enumerations.AdministrativeGender.MALE
      address =
        listOf(
          Address().apply {
            city = "Lahore"
            country = "Pakistan"
          },
        )
      name =
        listOf(
          HumanName().apply {
            given = mutableListOf(StringType("Salman"))
            family = "Ali"
          },
        )
      telecom = listOf(ContactPoint().apply { value = "12345" })
    }
  }

  @Test
  fun `extract() should perform StructureMap-based extraction`() = runBlocking {
    @Language("JSON")
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
              "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-targetStructureMap",
              "valueCanonical": "https://fhir.labs.smartregister.org/StructureMap/336"
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
                  "item": [
                    {
                      "linkId": "PR-name-given",
                      "type": "string",
                      "text": "First Name"
                    },
                    {
                      "linkId": "PR-name-family",
                      "type": "string",
                      "text": "Family Name"
                    }
                  ]
                },
                {
                  "linkId": "patient-0-birth-date",
                  "type": "date",
                  "text": "Date of Birth"
                },
                {
                  "linkId": "patient-0-gender",
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
                        "text": "A control where choices are listed with a button beside them. The button can be toggled to select or de-select a given choice. Selecting one item deselects all others."
                      }
                    }
                  ],
                  "type": "string",
                  "text": "Gender"
                },
                {
                  "linkId": "PR-telecom",
                  "type": "group",
                  "item": [
                    {
                      "linkId": "PR-telecom-system",
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
                      "type": "string",
                      "text": "Phone Number"
                    }
                  ]
                },
                {
                  "linkId": "PR-active",
                  "type": "boolean",
                  "text": "Is Active?"
                }
              ]
            }
          ]
        }
            """
        .trimIndent()

    @Language("JSON")
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
                      "valueDate": "2016-02-11"
                    }
                  ]
                },
                {
                  "linkId": "patient-0-gender",
                  "answer": [
                    {
                      "valueCoding": {
                        "code": "male",
                        "display": "Male"
                      }
                    }
                  ]
                },
                {
                  "linkId": "patient-0-marital-status",
                  "answer": [
                    {
                      "valueCoding": {
                        "code": "S",
                        "display": "Never Married"
                      }
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
                  "linkId": "PR-contact-party",
                  "item": [
                    {
                      "linkId": "PR-contact-party-name",
                      "item": [
                        {
                          "linkId": "PR-contact-party-name-given",
                          "answer": [
                            {
                              "valueString": "Brenda"
                            }
                          ]
                        },
                        {
                          "linkId": "PR-contact-party-name-family",
                          "answer": [
                            {
                              "valueString": "Penman"
                            }
                          ]
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
                },
                {
                  "linkId": "PR-multiple-birth",
                  "answer": [
                    {
                      "valueInteger": 2
                    }
                  ]
                }
              ]
            }
          ]
        }
            """
        .trimIndent()

    val mapping =
      """map "http://hl7.org/fhir/StructureMap/PatientRegistration" = 'PatientRegistration'

        uses "http://hl7.org/fhir/StructureDefinition/QuestionnaireReponse" as source
        uses "http://hl7.org/fhir/StructureDefinition/Bundle" as target
        
        group PatientRegistration(source src : QuestionnaireResponse, target bundle: Bundle) {
            src -> bundle.id = uuid() "rule_c";
            src -> bundle.type = 'collection' "rule_b";
            src -> bundle.entry as entry, entry.resource = create('Patient') as patient then
                ExtractPatient(src, patient) "rule_z";
        }
        
        group ExtractPatient(source src : QuestionnaireResponse, target tgt : Patient) {
             src.item as item where(linkId = 'PR') then {
                 item.item as inner_item where (linkId = 'patient-0-birth-date') then {
                     inner_item.answer first as ans then { 
                         ans.value as val -> tgt.birthDate = val "rule_a";
                     } ;
                 } ;
                 
                 item.item as nameItem where(linkId = 'PR-name') -> tgt.name = create('HumanName') as patientName then {  
                    src -> patientName.family = evaluate(nameItem, ${"$"}this.item.where(linkId = 'PR-name-family').answer.value) "rule_d";
                    src -> patientName.given = evaluate(nameItem, ${"$"}this.item.where(linkId = 'PR-name-given').answer.value) "rule_e";
                 };
             };
        }"""

    val uriTestQuestionnaire =
      iParser.parseResource(Questionnaire::class.java, questionnaireJson) as Questionnaire

    val uriTestQuestionnaireResponse =
      iParser.parseResource(QuestionnaireResponse::class.java, questionnaireResponseJson)
        as QuestionnaireResponse

    val bundle =
      ResourceMapper.extract(
        uriTestQuestionnaire,
        uriTestQuestionnaireResponse,
        StructureMapExtractionContext(context = context) { _, worker ->
          StructureMapUtilities(worker).parse(mapping, "")
        },
      )

    val patient = bundle.entry.get(0).resource as Patient
    assertThat(patient.birthDate).isEqualTo("2016-02-11".toDateFromFormatYyyyMmDd())
    assertThat(patient.active).isFalse()
    assertThat(patient.name.first().given.first().toString()).isEqualTo("John")
    assertThat(patient.name.first().family).isEqualTo("Doe")
  }

  @Test
  fun `extract() should use custom TransformSupportServices to generate unsupported nested resource types`() =
    runBlocking {
      @Language("JSON")
      val questionnaireJson =
        """
        {
          "resourceType": "Questionnaire",
          "id": "immunization-sample",
          "status": "active",
          "subjectType": [
            "Immunization"
          ],
          "extension": [
            {
              "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-targetStructureMap",
              "valueCanonical": "https://fhir.labs.smartregister.org/StructureMap/336"
            }
          ],
          "item": [
            {
              "linkId": "immunization-name",
              "type": "text"
            }
          ]
        }
                """
          .trimIndent()

      @Language("JSON")
      val questionnaireResponseJson =
        """
        {
          "resourceType": "QuestionnaireResponse",
          "questionnaire": "client-registration-sample",
          "item": [
            {
              "linkId": "immunization-name",
              "answer": [
                  {
                    "answerString": "Oxford AstraZeneca"
                  }
              ]
            }
          ]
        }
                """
          .trimIndent()

      val mapping =
        """map "http://hl7.org/fhir/StructureMap/ImmunizationReg" = 'ImmunizationReg'

        uses "http://hl7.org/fhir/StructureDefinition/QuestionnaireReponse" as source
        uses "http://hl7.org/fhir/StructureDefinition/Bundle" as target
        
        group ImmunizationReg(source src : QuestionnaireResponse, target bundle: Bundle) {
            src -> bundle.id = uuid() "rule_c";
            src -> bundle.type = 'collection' "rule_b";
            src -> bundle.entry as entry, entry.resource = create('Immunization') as immunization then
                ExtractImmunization(src, immunization) "rule_z";
        }
        
        group ExtractImmunization(source src : QuestionnaireResponse, target tgt : Immunization) {
             src -> tgt.reaction = create('Immunization_Reaction') "rule_z1";
        }"""

      val uriTestQuestionnaire =
        iParser.parseResource(Questionnaire::class.java, questionnaireJson) as Questionnaire

      val uriTestQuestionnaireResponse =
        iParser.parseResource(QuestionnaireResponse::class.java, questionnaireResponseJson)
          as QuestionnaireResponse

      val transformSupportServices = TransformSupportServices(mutableListOf())

      val bundle =
        ResourceMapper.extract(
          uriTestQuestionnaire,
          uriTestQuestionnaireResponse,
          StructureMapExtractionContext(context, transformSupportServices) { _, worker ->
            StructureMapUtilities(worker).parse(mapping, "")
          },
        )

      assertThat(bundle.entry.get(0).resource).isInstanceOf(Immunization::class.java)
      assertThat((bundle.entry.get(0).resource as Immunization).reaction[0])
        .isInstanceOf(Immunization.ImmunizationReactionComponent::class.java)
    }

  @Test
  fun extract_choiceType_updateObservationFields() = runBlocking {
    @Language("JSON")
    val questionnaire =
      """
        {
          "title": "Screener",
          "status": "active",
          "version": "0.0.1",
          "publisher": "Fred Hersch (fredhersch@google.com)",
          "resourceType": "Questionnaire",
          "subjectType": [
            "Encounter"
          ],
          "extension": [
            {
              "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemExtractionContext",
              "valueExpression": {
                "language": "application/x-fhir-query",
                "expression": "Encounter",
                "name": "encounter"
              }
            }
          ],
          "item": [
            {
              "text": "Temperature",
              "type": "group",
              "linkId": "5.0.0",
              "code": [
                {
                  "code": "8310-5",
                  "display": "Temperature",
                  "system": "http://loinc.org"
                }
              ],
              "extension": [
                {
                  "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemExtractionContext",
                  "valueExpression": {
                    "language": "application/x-fhir-query",
                    "expression": "Observation",
                    "name": "temperature"
                  }
                },
                {
                  "url": "http://hl7.org/fhir/StructureDefinition/questionnaire-itemControl",
                  "valueCodeableConcept": {
                    "coding": [
                      {
                        "system": "http://hl7.org/fhir/questionnaire-item-control",
                        "code": "page",
                        "display": "Page"
                      }
                    ],
                    "text": "Page"
                  }
                }
              ],
              "item": [
                {
                  "text": "Add instructions for capturing temperature",
                  "type": "display",
                  "linkId": "5.0.1"
                },
                {
                  "type": "group",
                  "definition": "http://hl7.org/fhir/StructureDefinition/Observation#Observation.valueQuantity",
                  "item": [
                    {
                      "definition": "http://hl7.org/fhir/StructureDefinition/Observation#Observation.valueQuantity.value",
                      "text": "Record temperature",
                      "type": "decimal",
                      "linkId": "5.1.0",
                      "extension": [
                        {
                          "url": "http://hl7.org/fhir/StructureDefinition/minValue",
                          "valueDecimal": 35.0
                        },
                        {
                          "url": "http://hl7.org/fhir/StructureDefinition/maxValue",
                          "valueDecimal": 45.0
                        }
                      ]
                    },
                    {
                      "text": "Unit",
                      "type": "choice",
                      "linkId": "5.2.0",
                      "required": true,
                      "definition": "http://hl7.org/fhir/StructureDefinition/Observation#Observation.valueQuantity.code",
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
                      "answerOption": [
                        {
                          "valueCoding": {
                            "code": "F",
                            "display": "F"
                          }
                        },
                        {
                          "valueCoding": {
                            "code": "C",
                            "display": "C"
                          }
                        }
                      ]
                    }
                  ]
                }
              ]
            }
          ]
        }
            """
        .trimIndent()

    @Language("JSON")
    val response =
      """
        {
          "resourceType": "QuestionnaireResponse",
          "item": [
            {
              "linkId": "5.0.0",
              "item": [
                {
                  "linkId": "5.0.1"
                },
                {
                  "item": [
                    {
                      "linkId": "5.1.0",
                      "answer": [
                        {
                          "valueDecimal": 36
                        }
                      ]
                    },
                    {
                      "linkId": "5.2.0",
                      "answer": [
                        {
                          "valueCoding": {
                            "code": "F",
                            "display": "F"
                          }
                        }
                      ]
                    }
                  ]
                }
              ]
            }
          ]
        }
            """
        .trimIndent()
    val temperatureQuestionnaire =
      iParser.parseResource(Questionnaire::class.java, questionnaire) as Questionnaire
    val temperatureQuestionnaireResponse =
      iParser.parseResource(QuestionnaireResponse::class.java, response) as QuestionnaireResponse
    val bundle = ResourceMapper.extract(temperatureQuestionnaire, temperatureQuestionnaireResponse)
    val observation = bundle.entry[0].resource as Observation

    assertThat(observation.valueQuantity.value).isEqualTo(BigDecimal(36))
    assertThat(observation.valueQuantity.code).isEqualTo("F")
  }

  @Test
  fun extract_questionnaireItemDisabled() = runBlocking {
    // https://developer.commure.com/docs/apis/sdc/examples#definition-based-extraction
    @Language("JSON")
    val questionnaireJson =
      """
        {
          "resourceType": "Questionnaire",
          "subjectType": [
            "Patient"
          ],
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
                      "text": "First Name",
                       "enableWhen": [
                        {
                          "question": "question-id",
                          "operator": "=",
                          "answerString": "ok"
                        }
                      ]
                    },
                    {
                      "linkId": "PR-name-family",
                      "definition": "http://hl7.org/fhir/StructureDefinition/datatypes#Patient.name.family",
                      "type": "string",
                      "text": "Family Name"
                    }
                  ]
                }
              ]
            }
          ]
        }
            """
        .trimIndent()

    @Language("JSON")
    val questionnaireResponseJson =
      """
        {
          "resourceType": "QuestionnaireResponse",
          "item": [
            {
              "linkId": "PR",
              "item": [
                {
                  "linkId": "PR-name",
                  "item": [
                    {
                      "linkId": "PR-name-family",
                      "answer": [
                        {
                          "valueString": "Doe"
                        }
                      ]
                    }
                  ]
                }
              ]
            }
          ]
        }
            """
        .trimIndent()
    val questionnaire =
      iParser.parseResource(Questionnaire::class.java, questionnaireJson) as Questionnaire
    val response =
      iParser.parseResource(QuestionnaireResponse::class.java, questionnaireResponseJson)
        as QuestionnaireResponse
    val patient = ResourceMapper.extract(questionnaire, response).entry[0].resource as Patient

    assertThat(patient.name.first().given).isEmpty() // disabled questionnaire item
    assertThat(patient.name.first().family).isEqualTo("Doe")
  }

  @Test
  fun extract_questionnaireInheritedFieldsMapping() {
    // https://developer.commure.com/docs/apis/sdc/examples#definition-based-extraction
    @Language("JSON")
    val questionnaireJson =
      """
        {
          "resourceType": "Questionnaire",
          "subjectType": [ "Patient" ],
          "extension": [
            {
              "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemExtractionContext",
              "valueExpression": {
                "language": "application/x-fhir-query",
                "expression": "Patient"
              }
            }
          ],
          "item": [
            {
              "linkId": "a",
              "type": "group",
              "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.extension",
              "item": [
                {
                  "linkId": "deep-nesting-item",
                  "type": "group",
                  "item": [
                    {
                      "linkId": "a-url",
                      "type": "string",
                      "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.extension.url",
                      "initial": [
                        {
                          "valueString": "http://fhir/StructureDefinition/us-core-ethnicity"
                        }
                      ]
                    },
                    {
                      "linkId": "a-value",
                      "type": "choice",
                      "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.extension.value",
                      "answerOption": [
                        {
                          "valueCoding": {
                            "code": "option 1",
                            "display": "Option 1"
                          }
                        }
                      ],
                      "initial": [
                        {
                          "valueCoding": {
                            "code": "option 1",
                            "display": "Option 1"
                          }
                        }
                      ]
                    }
                  ]
                }
              ]
            },
            {
              "linkId": "b",
              "type": "group",
              "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.extension",
              "item": [
                {
                  "linkId": "b-url",
                  "type": "string",
                  "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.extension.url",
                  "initial": [
                    {
                      "valueString": "http://fhir/StructureDefinition/current-occupation"
                    }
                  ]
                },
                {
                  "linkId": "b-value",
                  "type": "choice",
                  "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.extension.value",
                  "answerOption": [
                    {
                      "valueCoding": {
                        "code": "option I",
                        "display": "Option I"
                      }
                    }
                  ],
                  "initial": [
                    {
                      "valueCoding": {
                        "code": "option I",
                        "display": "Option I"
                      }
                    }
                  ]
                }
              ]
            }
          ]
        }
            """
        .trimIndent()

    @Language("JSON")
    val questionnaireResponseJson =
      """
        {
          "resourceType": "QuestionnaireResponse",
          "questionnaire": "Questionnaire/questionnaire-registration-extension",
          "item": [
            {
              "linkId": "a",
              "item": [
                {
                  "linkId": "deep-nesting-item",
                  "item": [
                    {
                      "linkId": "a-url",
                      "answer": [
                        {
                          "valueString": "http://fhir/StructureDefinition/us-core-ethnicity"
                        }
                      ]
                    },
                    {
                      "linkId": "a-value",
                      "answer": [
                        {
                          "valueCoding": {
                            "code": "option 1",
                            "display": "Option 1"
                          }
                        }
                      ]
                    }
                  ]
                }
              ]
            },
            {
              "linkId": "b",
              "item": [
                {
                  "linkId": "b-url",
                  "answer": [
                    {
                      "valueString": "http://fhir/StructureDefinition/current-occupation"
                    }
                  ]
                },
                {
                  "linkId": "b-value",
                  "answer": [
                    {
                      "valueCoding": {
                        "code": "option i",
                        "display": "Option I"
                      }
                    }
                  ]
                }
              ]
            }
          ]
        }
            """
        .trimIndent()
    val questionnaire =
      iParser.parseResource(Questionnaire::class.java, questionnaireJson) as Questionnaire
    val response =
      iParser.parseResource(QuestionnaireResponse::class.java, questionnaireResponseJson)
        as QuestionnaireResponse
    val patient: Patient
    runBlocking {
      patient = ResourceMapper.extract(questionnaire, response).entry[0].resource as Patient
    }

    assertThat(patient.extension).hasSize(2)

    val extension1 = patient.extension[0]

    assertThat(extension1.url).isEqualTo("http://fhir/StructureDefinition/us-core-ethnicity")
    assertThat(extension1.value).isInstanceOf(Coding::class.java)
    assertThat((extension1.value as Coding).code).isEqualTo("option 1")
    assertThat((extension1.value as Coding).display).isEqualTo("Option 1")

    val extension2 = patient.extension[1]

    assertThat(extension2.url).isEqualTo("http://fhir/StructureDefinition/current-occupation")
    assertThat(extension2.value).isInstanceOf(Coding::class.java)
    assertThat((extension2.value as Coding).code).isEqualTo("option i")
    assertThat((extension2.value as Coding).display).isEqualTo("Option I")
  }

  @Test
  fun `populate() should fail with IllegalStateException when QuestionnaireItem has both initial value and initialExpression`():
    Unit = runBlocking {
    val questionnaire =
      Questionnaire()
        .apply {
          addExtension().apply {
            url = EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT
            extension =
              listOf(
                Extension("name", Coding(EXTENSION_LAUNCH_CONTEXT, "father", "Father")),
                Extension("type", CodeType("Patient")),
              )
          }
        }
        .addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "patient-gender"
            type = Questionnaire.QuestionnaireItemType.CHOICE
            extension =
              listOf(
                Extension(
                  ITEM_INITIAL_EXPRESSION_URL,
                  Expression().apply {
                    language = "text/fhirpath"
                    expression = "%father.gender"
                  },
                ),
              )
            initial = listOf(Questionnaire.QuestionnaireItemInitialComponent(StringType("female")))
          },
        )

    val patient = Patient().apply { gender = Enumerations.AdministrativeGender.MALE }
    val errorMessage =
      assertFailsWith<IllegalStateException> {
          ResourceMapper.populate(questionnaire, mapOf("father" to patient))
        }
        .localizedMessage
    assertThat(errorMessage)
      .isEqualTo(
        "QuestionnaireItem item is not allowed to have both initial.value and initial expression. See rule at http://build.fhir.org/ig/HL7/sdc/expressions.html#initialExpression.",
      )
  }

  @Test
  fun `extract() definition based extraction should extract multiple values of a list field in a group`():
    Unit = runBlocking {
    @Language("JSON")
    val questionnaire =
      """
        {
          "resourceType": "Questionnaire",
          "subjectType": [
          "Patient"
        ],
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
                  "linkId": "PR-name-middle",
                  "definition": "http://hl7.org/fhir/StructureDefinition/datatypes#Patient.name.given",
                  "type": "string",
                  "text": "Middle Name"
                }
              ]
            }
          ]
        }
            """
        .trimIndent()

    @Language("JSON")
    val response =
      """
        {
          "resourceType": "QuestionnaireResponse",
          "item": [
            {
              "linkId": "PR-name",
              "item": [
                {
                  "linkId": "PR-name-text",
                  "answer": [
                        {
                          "valueString": "TestName-First"
                        }
                      ]
                },{
                  "linkId": "PR-name-middle",
                  "answer": [
                        {
                          "valueString": "TestName-Middle"
                        }
                      ]
                }
              ]
            }
          ]
        }
            """
        .trimIndent()
    val questionnaireObj =
      iParser.parseResource(Questionnaire::class.java, questionnaire) as Questionnaire
    val temperatureQuestionnaireResponse =
      iParser.parseResource(QuestionnaireResponse::class.java, response) as QuestionnaireResponse
    val bundle = ResourceMapper.extract(questionnaireObj, temperatureQuestionnaireResponse)
    val patient = bundle.entry.single().resource as Patient

    assertThat(patient).isNotNull()
    assertThat(patient.name.first().given.map { it.value })
      .containsExactly("TestName-First", "TestName-Middle")
  }

  private fun String.toDateFromFormatYyyyMmDd(): Date? = SimpleDateFormat("yyyy-MM-dd").parse(this)

  class TransformSupportServices(private val outputs: MutableList<Base>) :
    StructureMapUtilities.ITransformerServices {
    override fun log(message: String) {}

    fun getContext(): org.hl7.fhir.r4.context.SimpleWorkerContext {
      return org.hl7.fhir.r4.context.SimpleWorkerContext()
    }

    @Throws(FHIRException::class)
    override fun createType(appInfo: Any, name: String): Base {
      return when (name) {
        "Immunization_Reaction" -> Immunization.ImmunizationReactionComponent()
        else -> ResourceFactory.createResourceOrType(name)
      }
    }

    override fun createResource(appInfo: Any, res: Base, atRootofTransform: Boolean): Base {
      if (atRootofTransform) outputs.add(res)
      return res
    }

    @Throws(FHIRException::class)
    override fun translate(appInfo: Any, source: Coding, conceptMapUrl: String): Coding {
      val cme = ConceptMapEngine(getContext())
      return cme.translate(source, conceptMapUrl)
    }

    @Throws(FHIRException::class)
    override fun resolveReference(appContext: Any, url: String): Base {
      throw FHIRException("resolveReference is not supported yet")
    }

    @Throws(FHIRException::class)
    override fun performSearch(appContext: Any, url: String): List<Base> {
      throw FHIRException("performSearch is not supported yet")
    }
  }
}
