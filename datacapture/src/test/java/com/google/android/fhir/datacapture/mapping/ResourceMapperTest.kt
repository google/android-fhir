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
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.ContactPoint
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType
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
    assertThat(patient.multipleBirthIntegerType.value).isEqualTo(2)
    assertThat(patient.contact[0].name.given.first().toString()).isEqualTo("Brenda")
    assertThat(patient.contact[0].name.family).isEqualTo("Penman")
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

  @Test
  fun populateResourceAnswers() {
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
              "extension": [
                {
                  "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression",
                  "valueExpression": {
                    "language": "text/fhirpath",
                    "expression": "Patient.name",
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
                    "expression": "Patient.name",
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
                "expression": "Patient.birthDate",
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
                "expression": "Patient.gender",
                "name": "patientGender"
              }
            }
          ],
          "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.gender",
          "initial": [
            {
              "valueString": "female"
            }
          ],
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
              "extension": [
                {
                  "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression",
                  "valueExpression": {
                    "language": "text/fhirpath",
                    "expression": "Patient.telecom",
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
          "extension": [
            {
              "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemContext",
              "valueExpression": {
                "language": "application/x-fhir-query",
                "expression": "Address",
                "name": "address"
              }
            }
          ],
          "item": [
            {
              "extension": [
                {
                  "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression",
                  "valueExpression": {
                    "language": "text/fhirpath",
                    "expression": "Patient.address",
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
                    "expression": "Patient.address",
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
                "expression": "Patient.active",
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
      """.trimIndent()

    val iParser: IParser = FhirContext.forR4().newJsonParser()

    val uriTestQuestionnaire =
      iParser.parseResource(org.hl7.fhir.r4.model.Questionnaire::class.java, questionnaireJson) as
        org.hl7.fhir.r4.model.Questionnaire

    val patient = createPatientResource()
    val response: QuestionnaireResponse =
      ResourceMapper.populate(uriTestQuestionnaire, patient)

    assertThat(((response.item[0].item[0].item[0].answer[0]).value as StringType).valueAsString).isEqualTo("Salman")
    assertThat(((response.item[0].item[0].item[1].answer[0]).value as StringType).valueAsString).isEqualTo("Ali")
    assertThat(((response.item[0].item[1].answer[0]).value as DateType).valueAsString).isEqualTo("3896-09-17")
    assertThat(((response.item[0].item[2].answer[0]).value as StringType).valueAsString).isEqualTo("male")
    assertThat(((response.item[0].item[3].item[1].answer[0]).value as StringType).valueAsString).isEqualTo("12345")
    assertThat(((response.item[0].item[4].item[0].answer[0]).value as StringType).valueAsString).isEqualTo("Lahore")
    assertThat(((response.item[0].item[4].item[1].answer[0]).value as StringType).valueAsString).isEqualTo("Pakistan")
    assertThat(((response.item[0].item[5].answer[0]).value as BooleanType).booleanValue()).isEqualTo(true)
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
          }
        )
      name =
        listOf(
          HumanName().apply {
            given = mutableListOf(StringType("Salman"))
            family = "Ali"
          }
        )
      telecom = listOf(ContactPoint().apply { value = "12345" })
    }
  }
}
