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
import androidx.test.core.app.ApplicationProvider
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.datacapture.utilities.NpmPackageProvider
import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.context.SimpleWorkerContext
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.ContactPoint
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.StructureMap
import org.hl7.fhir.r4.utils.StructureMapUtilities
import org.intellij.lang.annotations.Language
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], shadows = [ShadowNpmPackageProvider::class])
class ResourceMapperTest {
  @Test
  fun `extract() should perform definition-based extraction`() {
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
      """.trimIndent()

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
      iParser.parseResource(Questionnaire::class.java, questionnaireJson) as Questionnaire

    val uriTestQuestionnaireResponse =
      iParser.parseResource(QuestionnaireResponse::class.java, questionnaireResponseJson) as
        QuestionnaireResponse

    val patient: Patient

    runBlocking {
      patient =
        ResourceMapper.extract(uriTestQuestionnaire, uriTestQuestionnaireResponse).entry[0]
          .resource as
          Patient
    }

    assertThat(patient.birthDate).isEqualTo("2021-01-01".toDateFromFormatYyyyMmDd())
    assertThat(patient.active).isTrue()
    assertThat(patient.name.first().given.first().toString()).isEqualTo("John")
    assertThat(patient.name.first().family).isEqualTo("Doe")
    assertThat(patient.multipleBirthIntegerType.value).isEqualTo(2)
    assertThat(patient.contact[0].name.given.first().toString()).isEqualTo("Brenda")
    assertThat(patient.contact[0].name.family).isEqualTo("Penman")
  }

  @Test
  fun `extract() should perform definition-based extraction with unanswered questions`() {
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
      """.trimIndent()

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
      """.trimIndent()

    val iParser: IParser = FhirContext.forR4().newJsonParser()

    val uriTestQuestionnaire =
      iParser.parseResource(Questionnaire::class.java, questionnaireJson) as Questionnaire

    val uriTestQuestionnaireResponse =
      iParser.parseResource(QuestionnaireResponse::class.java, questionnaireResponseJson) as
        QuestionnaireResponse

    val patient: Patient

    runBlocking {
      patient =
        ResourceMapper.extract(uriTestQuestionnaire, uriTestQuestionnaireResponse).entry[0]
          .resource as
          Patient
    }
    assertThat(patient.birthDate).isEqualTo("2016-02-11".toDateFromFormatYyyyMmDd())
    assertThat(patient.active).isFalse()
    assertThat(patient.telecom.get(0).value).isNull()
    assertThat(patient.name.first().given.first().toString()).isEqualTo("Simon")
    assertThat(patient.name.first().family).isEqualTo("Crawford")
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
                    "expression": "Patient.name.given",
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
                    "expression": "Patient.name.family",
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
                "expression": "Patient.gender.value",
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
                    "expression": "Patient.telecom.value",
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
                    "expression": "Patient.address.city",
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
                    "expression": "Patient.address.country",
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
        Questionnaire

    val patient = createPatientResource()
    val response: QuestionnaireResponse
    runBlocking { response = ResourceMapper.populate(uriTestQuestionnaire, patient) }

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

  @Test
  fun `extract() should perform StructureMap-based extraction`() {
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
      """.trimIndent()

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
      """.trimIndent()

    val contextR4: SimpleWorkerContext

    runBlocking {
      contextR4 =
        SimpleWorkerContext.fromPackage(
          NpmPackageProvider.loadNpmPackage(ApplicationProvider.getApplicationContext())
        )
    }
    contextR4.isCanRunWithoutTerminology = true
    val structureMapUtilities = StructureMapUtilities(contextR4)
    val map: StructureMap =
      structureMapUtilities.parse(
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
        }""",
        "FHIRMapperTutorial"
      )

    val iParser: IParser = FhirContext.forR4().newJsonParser()

    val uriTestQuestionnaire =
      iParser.parseResource(Questionnaire::class.java, questionnaireJson) as Questionnaire

    val uriTestQuestionnaireResponse =
      iParser.parseResource(QuestionnaireResponse::class.java, questionnaireResponseJson) as
        QuestionnaireResponse

    val bundle: Bundle

    runBlocking {
      bundle =
        ResourceMapper.extract(
          uriTestQuestionnaire,
          uriTestQuestionnaireResponse,
          { map },
          ApplicationProvider.getApplicationContext()
        )
    }

    val patient = bundle.entry.get(0).resource as Patient
    assertThat(patient.birthDate).isEqualTo("2016-02-11".toDateFromFormatYyyyMmDd())
    assertThat(patient.active).isFalse()
    assertThat(patient.name.first().given.first().toString()).isEqualTo("John")
    assertThat(patient.name.first().family).isEqualTo("Doe")
  }

  @Test
  fun extract_choiceType_updateObservationFields() {
    @Language("JSON")
    val questionnaire =
      """{
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
      "definition": "http://hl7.org/fhir/StructureDefinition/Observation",
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
          "extension": [
            {
              "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemExtractionContext",
              "valueExpression": {
                "language": "application/x-fhir-query",
                "expression": "Quantity",
                "name": "quantity"
              }
            }
          ],
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
      """.trimIndent()
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
      """.trimIndent()
    val iParser: IParser = FhirContext.forR4().newJsonParser()
    val temperatureQuestionnaire =
      iParser.parseResource(Questionnaire::class.java, questionnaire) as Questionnaire
    val temperatureQuestionnaireResponse =
      iParser.parseResource(QuestionnaireResponse::class.java, response) as QuestionnaireResponse
    val bundle: Bundle
    runBlocking {
      bundle = ResourceMapper.extract(temperatureQuestionnaire, temperatureQuestionnaireResponse)
    }
    val observation = bundle.entry[0].resource as Observation

    assertThat(observation.valueQuantity.value).isEqualTo(BigDecimal(36))
    assertThat(observation.valueQuantity.code).isEqualTo("F")
  }

  private fun String.toDateFromFormatYyyyMmDd(): Date? = SimpleDateFormat("yyyy-MM-dd").parse(this)
}
