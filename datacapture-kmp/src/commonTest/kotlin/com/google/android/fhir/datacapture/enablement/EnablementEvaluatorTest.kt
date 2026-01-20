/*
 * Copyright 2022-2026 Google LLC
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

package com.google.android.fhir.datacapture.enablement

import com.google.android.fhir.datacapture.extensions.FhirR4Boolean
import com.google.fhir.model.r4.Attachment
import com.google.fhir.model.r4.Boolean
import com.google.fhir.model.r4.Code
import com.google.fhir.model.r4.Coding
import com.google.fhir.model.r4.Date
import com.google.fhir.model.r4.DateTime
import com.google.fhir.model.r4.Decimal
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.FhirR4Json
import com.google.fhir.model.r4.Integer
import com.google.fhir.model.r4.Quantity
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import com.google.fhir.model.r4.Reference
import com.google.fhir.model.r4.String
import com.google.fhir.model.r4.Time
import com.google.fhir.model.r4.Uri
import com.google.fhir.model.r4.terminologies.PublicationStatus
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class EnablementEvaluatorTest {
  private val json = FhirR4Json()

  @Test
  fun evaluate_noEnableWhen_shouldReturnTrue() {
    assertTrue(evaluateEnableWhen())
  }

  @Test
  fun evaluate_missingResponseItem_shouldReturnFalse() = runTest {
    val questionnaireItemBuilder =
      Questionnaire.Item.Builder(
          linkId = String.Builder().apply { value = "q1" },
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
        )
        .apply {
          enableWhen =
            mutableListOf(
              Questionnaire.Item.EnableWhen.Builder(
                  question = String.Builder().apply { value = "q2" },
                  answer =
                    Questionnaire.Item.EnableWhen.Answer.Boolean(value = Boolean(value = false)),
                  operator = Enumeration(value = Questionnaire.QuestionnaireItemOperator.Exists),
                )
                .apply {},
            )
        }
    val questionnaireBuilder =
      Questionnaire.Builder(status = Enumeration(value = PublicationStatus.Active)).apply {
        item = mutableListOf(questionnaireItemBuilder)
      }
    val questionnaireResponseItemBuilder =
      QuestionnaireResponse.Item.Builder(linkId = String.Builder().apply { value = "q1" })
    val questionnaireResponseBuilder =
      QuestionnaireResponse.Builder(
          status = Enumeration(value = QuestionnaireResponse.QuestionnaireResponseStatus.Completed),
        )
        .apply { item = mutableListOf(questionnaireResponseItemBuilder) }

    val result =
      EnablementEvaluator(questionnaireBuilder.build(), questionnaireResponseBuilder.build())
        .evaluate(questionnaireItemBuilder.build(), questionnaireResponseItemBuilder.build())
    assertFalse(result)
  }

  @Test
  fun evaluate_expectAnswerExists_answerExists_shouldReturnTrue() {
    assertTrue(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.Exists,
          expected = Boolean.Builder().apply { value = true }.build(),
          actual = listOf(Integer.Builder().apply { value = 123 }.build()),
        ),
      ),
    )
  }

  @Test
  fun evaluate_expectAnswerExists_answerDoesNotExist_shouldReturnFalse() {
    assertFalse(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.Exists,
          expected = Boolean.Builder().apply { value = true }.build(),
          actual = listOf(),
        ),
      ),
    )
  }

  @Test
  fun evaluate_expectAnswerDoesNotExist_answerExists_shouldReturnFalse() {
    assertFalse(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.Exists,
          expected = Boolean.Builder().apply { value = false }.build(),
          actual = listOf(Integer.Builder().apply { value = 123 }.build()),
        ),
      ),
    )
  }

  @Test
  @Ignore // TODO resolve after custom variables on expression is supported by kotlin-fhirpath
  fun `evaluate() should evaluate enableWhenExpression`() = runTest {
    val questionnaireJson =
      """
        {
  "resourceType": "Questionnaire",
      "item": [
        {
          "linkId": "1",
          "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.gender",
          "type": "choice",
          "text": "Gender"
        },
        {
          "extension": [
            {
              "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-enableWhenExpression",
              "valueExpression": {
                "language": "text/fhirpath",
                "expression": "%resource.repeat(item).where(linkId='1').answer.value.code ='female'"
              }
            }
          ],
          "linkId" : "2",
          "text": "Have you had mammogram before?(enableWhenExpression = only when gender is female)",
          "type": "choice",
          "answerValueSet": "http://hl7.org/fhir/ValueSet/yesnodontknow"
        }
      ]
}

            """
        .trimIndent()

    val questionnaireResponseJson =
      """
        {
    "resourceType": "QuestionnaireResponse",
    "item": [
        {
            "linkId": "1",
            "answer": [
                {
                    "valueCoding": {
                        "system": "http://hl7.org/fhir/administrative-gender",
                        "code": "female",
                        "display": "Female"
                    }
                }
            ]
        },
        {
            "linkId": "2"
        }
    ]
      }
            """
        .trimIndent()

    val questionnaire = json.decodeFromString(questionnaireJson) as Questionnaire

    val questionnaireItem: Questionnaire.Item? =
      questionnaire.item.find { item -> item.linkId.value == "2" }

    val questionnaireResponse =
      json.decodeFromString(questionnaireResponseJson) as QuestionnaireResponse

    runTest {
      assertNotNull(questionnaireItem)
      assertTrue(
        EnablementEvaluator(questionnaire, questionnaireResponse)
          .evaluate(
            questionnaireItem,
            questionnaireResponse.item[1],
          ),
      )
    }
  }

  @Test
  @Ignore // TODO resolve after custom variables on expression is supported by kotlin-fhirpath
  fun `evaluate() should evaluate false enableWhenExpression`() = runTest {
    val questionnaireJson =
      """
        {
  "resourceType": "Questionnaire",
      "item": [
        {
          "linkId": "1",
          "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.gender",
          "type": "choice",
          "text": "Gender"
        },
        {
          "extension": [
            {
              "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-enableWhenExpression",
              "valueExpression": {
                "language": "text/fhirpath",
                "expression": "%resource.repeat(item).where(linkId='1').answer.value.code ='female'"
              }
            }
          ],
          "linkId" : "2",
          "text": "Have you had mammogram before?(enableWhenExpression = only when gender is female)",
          "type": "choice",
          "answerValueSet": "http://hl7.org/fhir/ValueSet/yesnodontknow"
        }
      ]
}

            """
        .trimIndent()

    val questionnaireResponseJson =
      """
        {
    "resourceType": "QuestionnaireResponse",
    "item": [
        {
            "linkId": "1",
            "answer": [
                {
                    "valueCoding": {
                        "system": "http://hl7.org/fhir/administrative-gender",
                        "code": "male",
                        "display": "Male"
                    }
                }
            ]
        },
        {
            "linkId": "2"
        }
    ]
      }
            """
        .trimIndent()

    val questionnaire = json.decodeFromString(questionnaireJson) as Questionnaire

    val questionnaireItemComponent = questionnaire.item.find { it.linkId.value == "2" }
    val questionnaireResponse =
      json.decodeFromString(questionnaireResponseJson) as QuestionnaireResponse
    assertNotNull(questionnaireItemComponent)
    assertFalse(
      EnablementEvaluator(questionnaire, questionnaireResponse)
        .evaluate(
          questionnaireItemComponent,
          questionnaireResponse.item[1],
        ),
    )
  }

  @Test
  @Ignore // TODO resolve after custom variables on expression is supported by kotlin-fhirpath
  fun `evaluate() should evaluate enableWhenExpression with %context fhirpath supplement literal`() =
    runTest {
      val questionnaireJson =
        """
    {
      "resourceType": "Questionnaire",
          "item": [
            {
              "linkId": "1",
              "definition": "http://hl7.org/fhir/StructureDefinition/Patient#Patient.gender",
              "type": "choice",
              "text": "Gender"
            },
            {
              "extension": [
                {
                  "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-enableWhenExpression",
                  "valueExpression": {
                    "language": "text/fhirpath",
                    "expression": "%resource.repeat(item).where(linkId='1').answer.value.code = %context.linkId"
                  }
                }
              ],
              "linkId" : "female",
              "text": "Have you had mammogram before?(enableWhenExpression = only when gender is female)",
              "type": "choice",
              "answerValueSet": "http://hl7.org/fhir/ValueSet/yesnodontknow"
            }
          ]
    }
                """
          .trimIndent()

      val questionnaireResponseJson =
        """
    {
      "resourceType": "QuestionnaireResponse",
      "item": [
        {
          "linkId": "1",
          "answer": [
            {
              "valueCoding": {
                "system": "http://hl7.org/fhir/administrative-gender",
                "code": "female",
                "display": "Female"
              }
            }
          ]
        },
        {
          "linkId": "female"
        }
      ]
    }
                """
          .trimIndent()

      val questionnaire = json.decodeFromString(questionnaireJson) as Questionnaire

      val questionnaireItem: Questionnaire.Item =
        questionnaire.item.find { it.linkId.value == "female" }!!

      val questionnaireResponse =
        json.decodeFromString(questionnaireResponseJson) as QuestionnaireResponse

      assertTrue(
        EnablementEvaluator(questionnaire, questionnaireResponse)
          .evaluate(
            questionnaireItem,
            questionnaireResponse.item[1],
          ),
      )
    }

  @Test
  @Ignore // TODO resolve after custom variables on expression is supported by kotlin-fhirpath
  fun `evaluate() should evaluate enableWhenExpression with %questionnaire fhirpath supplement`() =
    runTest {
      val questionnaireJson =
        """
    {
      "resourceType": "Questionnaire",
      "subjectType": "Practitioner",
          "item": [
            {
              "extension": [
                {
                  "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-enableWhenExpression",
                  "valueExpression": {
                    "language": "text/fhirpath",
                    "expression": "%questionnaire.subjectType='Practitioner'"
                  }
                }
              ],
              "linkId" : "contribution",
              "text": "Contribution",
              "type": "choice",
              "answerValueSet": "http://hl7.org/fhir/ValueSet/yesnodontknow"
            }
          ]
    }
                """
          .trimIndent()

      val questionnaireResponseJson =
        """
    {
      "resourceType": "QuestionnaireResponse",
      "item": [
        {
          "linkId": "contribution",
          "answer": [
            {
              "valueCoding": {
                "code": "yes",
                "display": "Yes"
              }
            }
          ]
        }
      ]
    }
                """
          .trimIndent()

      val questionnaire = json.decodeFromString(questionnaireJson) as Questionnaire

      val questionnaireResponse =
        json.decodeFromString(questionnaireResponseJson) as QuestionnaireResponse

      assertTrue(
        EnablementEvaluator(questionnaire, questionnaireResponse)
          .evaluate(
            questionnaire.item[0],
            questionnaireResponse.item[0],
          ),
      )
    }

  @Test
  @Ignore // TODO resolve after custom variables on expression is supported by kotlin-fhirpath
  fun `evaluate() should evaluate enableWhenExpression with %qItem fhirpath supplement`() =
    runTest {
      val questionnaireJson =
        """
    {
      "resourceType": "Questionnaire",
      "subjectType": "Practitioner",
          "item": [
            {
              "extension": [
                {
                  "url": "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-enableWhenExpression",
                  "valueExpression": {
                    "language": "text/fhirpath",
                    "expression": "%qItem.text = 'Contribution'"
                  }
                }
              ],
              "linkId" : "contribution",
              "text": "Contribution",
              "type": "choice",
              "answerValueSet": "http://hl7.org/fhir/ValueSet/yesnodontknow"
            }
          ]
    }
                """
          .trimIndent()

      val questionnaireResponseJson =
        """
    {
      "resourceType": "QuestionnaireResponse",
      "item": [
        {
          "linkId": "contribution",
          "answer": [
            {
              "valueCoding": {
                "code": "yes",
                "display": "Yes"
              }
            }
          ]
        }
      ]
    }
                """
          .trimIndent()

      val questionnaire = json.decodeFromString(questionnaireJson) as Questionnaire

      val questionnaireResponse =
        json.decodeFromString(questionnaireResponseJson) as QuestionnaireResponse

      assertTrue(
        EnablementEvaluator(questionnaire, questionnaireResponse)
          .evaluate(
            questionnaire.item[0],
            questionnaireResponse.item[0],
          ),
      )
    }

  @Test
  fun evaluate_expectAnswerDoesNotExist_answerDoesNotExist_shouldReturnTrue() {
    assertTrue(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.Exists,
          expected = Boolean(value = false),
          actual = listOf(),
        ),
      ),
    )
  }

  @Test
  fun evaluate_expectAnswerEqualToToValue_noAnswer_shouldReturnFalse() {
    assertFalse(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EqualTo,
          expected = Integer(value = 123),
          actual = listOf(),
        ),
      ),
    )
  }

  @Test
  fun evaluate_expectAnswerEqualToToValue_someAnswerEqualToToValue_shouldReturnTrue() {
    assertTrue(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EqualTo,
          expected = Integer(value = 123),
          actual = listOf(Integer(value = 123), Integer(value = 456)),
        ),
      ),
    )
  }

  @Test
  fun evaluate_expectAnswerEqualToToValue_noAnswerEqualToToValue_shouldReturnFalse() {
    assertFalse(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EqualTo,
          expected = Integer(value = 123),
          actual = listOf(Integer(value = 456), Integer(value = 789)),
        ),
      ),
    )
  }

  @Test
  fun evaluate_expectAnswerNotEqualToValue_noAnswer_shouldReturnFalse() {
    assertFalse(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.NotEqualTo,
          expected = Integer(value = 123),
          actual = listOf(),
        ),
      ),
    )
  }

  @Test
  fun evaluate_expectAnswerNotEqualToValue_someAnswerNotEqualToValue_shouldReturnTrue() {
    assertTrue(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.NotEqualTo,
          expected = Integer(value = 123),
          actual = listOf(Integer(value = 123), Integer(value = 456)),
        ),
      ),
    )
  }

  @Test
  fun evaluate_expectAnswerNotEqualToValue_noAnswerNotEqualToValue_shouldReturnFalse() {
    assertFalse(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.NotEqualTo,
          expected = Integer(value = 123),
          actual = listOf(Integer(value = 123), Integer(value = 123)),
        ),
      ),
    )
  }

  @Test
  fun evaluate_expectAnswerGreaterThanValue_someAnswerGreaterThanValue_shouldReturnTrue() {
    assertTrue(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.GreaterThan,
          expected = Integer(value = 10),
          actual = listOf(Integer(value = 20)),
        ),
      ),
    )
  }

  @Test
  fun evaluate_expectAnswerGreaterThanValue_noAnswerGreaterThanValue_shouldReturnFalse() {
    assertFalse(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.GreaterThan,
          expected = Integer(value = 10),
          actual = listOf(Integer(value = 5)),
        ),
      ),
    )
  }

  @Test
  fun evaluate_expectAnswerGreaterThanOrEqualToToValue_someAnswerGreaterThanOrEqualToToValue_shouldReturnTrue() {
    assertTrue(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.GreaterThanOrEqualTo,
          expected = Integer(value = 10),
          actual = listOf(Integer(value = 10)),
        ),
      ),
    )
  }

  @Test
  fun evaluate_expectAnswerGreaterThanOrEqualToToValue_noAnswerGreaterThanOrEqualToToValue_shouldReturnFalse() {
    assertFalse(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.GreaterThanOrEqualTo,
          expected = Integer(value = 10),
          actual = listOf(Integer(value = 5)),
        ),
      ),
    )
  }

  @Test
  fun evaluate_expectAnswerLessThanValue_someAnswerLessThanValue_shouldReturnTrue() {
    assertTrue(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.LessThan,
          expected = Integer(value = 10),
          actual = listOf(Integer(value = 5)),
        ),
      ),
    )
  }

  @Test
  fun evaluate_expectAnswerLessThanValue_noAnswerLessThanValue_shouldReturnFalse() {
    assertFalse(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.LessThan,
          expected = Integer(value = 10),
          actual = listOf(Integer(value = 20)),
        ),
      ),
    )
  }

  @Test
  fun evaluate_expectAnswerLessThanOrEqualToToValue_someAnswerLessThanOrEqualToToValue_shouldReturnTrue() {
    assertTrue(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.LessThanOrEqualTo,
          expected = Integer(value = 10),
          actual = listOf(Integer(value = 10)),
        ),
      ),
    )
  }

  @Test
  fun evaluate_expectAnswerLessThanOrEqualToToValue_noAnswerLessThanOrEqualToToValue_shouldReturnFalse() {
    assertFalse(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.LessThanOrEqualTo,
          expected = Integer(value = 10),
          actual = listOf(Integer(value = 20)),
        ),
      ),
    )
  }

  @Test
  fun evaluate_multipleEnableWhens_behaviorAny_noneSatisfied_shouldReturnFalse() {
    assertFalse(
      evaluateEnableWhen(
        behavior = Questionnaire.EnableWhenBehavior.Any,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.Exists,
          expected = Boolean(value = true),
          actual = listOf(),
        ),
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.Exists,
          expected = Boolean(value = true),
          actual = listOf(),
        ),
      ),
    )
  }

  @Test
  fun evaluate_multipleEnableWhens_behaviorAny_someSatisfied_shouldReturnTrue() {
    assertTrue(
      evaluateEnableWhen(
        behavior = Questionnaire.EnableWhenBehavior.Any,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.Exists,
          expected = Boolean(value = false),
          actual = listOf(),
        ),
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.Exists,
          expected = Boolean(value = true),
          actual = listOf(),
        ),
      ),
    )
  }

  @Test
  fun evaluate_multipleEnableWhens_behaviorAll_someSatisfied_shouldReturnFalse() {
    assertFalse(
      evaluateEnableWhen(
        behavior = Questionnaire.EnableWhenBehavior.All,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.Exists,
          expected = Boolean(value = false),
          actual = listOf(),
        ),
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.Exists,
          expected = Boolean(value = true),
          actual = listOf(),
        ),
      ),
    )
  }

  @Test
  fun evaluate_multipleEnableWhens_behaviorAll_allSatisfied_shouldReturnTrue() {
    assertTrue(
      evaluateEnableWhen(
        behavior = Questionnaire.EnableWhenBehavior.All,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.Exists,
          expected = FhirR4Boolean(value = false),
          actual = listOf(),
        ),
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.Exists,
          expected = FhirR4Boolean(value = false),
          actual = listOf(),
        ),
      ),
    )
  }

  @Test
  fun evaluate_primitiveType_EqualTo_shouldReturnTrue() {
    assertTrue(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EqualTo,
          expected = Integer(value = 123),
          actual = listOf(Integer(value = 123)),
        ),
      ),
    )
  }

  @Test
  fun evaluate_primitiveType_EqualTo_shouldReturnFalse() {
    assertFalse(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EqualTo,
          expected = Integer(value = 123),
          actual = listOf(Integer(value = 456)),
        ),
      ),
    )
  }

  @Test
  fun evaluate_primitiveType_notEqualTo_shouldReturnTrue() {
    assertTrue(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.NotEqualTo,
          expected = Integer(value = 123),
          actual = listOf(Integer(value = 456)),
        ),
      ),
    )
  }

  @Test
  fun evaluate_primitiveType_notEqualTo_shouldReturnFalse() {
    assertFalse(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.NotEqualTo,
          expected = Integer(value = 123),
          actual = listOf(Integer(value = 123)),
        ),
      ),
    )
  }

  @Test
  fun evaluate_codingType_EqualTo_differentSystem_shouldReturnFalse() {
    assertFalse(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EqualTo,
          expected =
            Coding(
              system = Uri(value = "system"),
              code = Code(value = "code"),
              display = String(value = "display"),
            ),
          actual =
            listOf(
              Coding(
                system = Uri(value = "otherSystem"),
                code = Code(value = "code"),
                display = String(value = "display"),
              ),
            ),
        ),
      ),
    )
  }

  @Test
  fun evaluate_codingType_EqualTo_differentCode_shouldReturnFalse() {
    assertFalse(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EqualTo,
          expected =
            Coding(
              system = Uri(value = "system"),
              code = Code(value = "code"),
              display = String(value = "display"),
            ),
          actual =
            listOf(
              Coding(
                system = Uri(value = "system"),
                code = Code(value = "otherCode"),
                display = String(value = "display"),
              ),
            ),
        ),
      ),
    )
  }

  @Test
  fun evaluate_codingType_EqualTo_differentDisplay_shouldReturnTrue() {
    assertTrue(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EqualTo,
          expected =
            Coding(
              system = Uri(value = "system"),
              code = Code(value = "code"),
              display = String(value = "display"),
            ),
          actual =
            listOf(
              Coding(
                system = Uri(value = "system"),
                code = Code(value = "code"),
                display = String(value = "otherDisplay"),
              ),
            ),
        ),
      ),
    )
  }

  @Test
  fun evaluate_codingType_notEqualTo_differentSystem_shouldReturnTrue() {
    assertTrue(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.NotEqualTo,
          expected =
            Coding(
              system = Uri(value = "system"),
              code = Code(value = "code"),
              display = String(value = "display"),
            ),
          actual =
            listOf(
              Coding(
                system = Uri(value = "otherSystem"),
                code = Code(value = "code"),
                display = String(value = "display"),
              ),
            ),
        ),
      ),
    )
  }

  @Test
  fun evaluate_codingType_notEqualTo_differentCode_shouldReturnTrue() {
    assertTrue(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.NotEqualTo,
          expected =
            Coding(
              system = Uri(value = "system"),
              code = Code(value = "code"),
              display = String(value = "display"),
            ),
          actual =
            listOf(
              Coding(
                system = Uri(value = "system"),
                code = Code(value = "otherCode"),
                display = String(value = "display"),
              ),
            ),
        ),
      ),
    )
  }

  @Test
  fun evaluate_codingType_notEqualTo_differentDisplay_shouldReturnFalse() {
    assertFalse(
      evaluateEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.NotEqualTo,
          expected =
            Coding(
              system = Uri(value = "system"),
              code = Code(value = "code"),
              display = String(value = "display"),
            ),
          actual =
            listOf(
              Coding(
                system = Uri(value = "system"),
                code = Code(value = "code"),
                display = String(value = "otherDisplay"),
              ),
            ),
        ),
      ),
    )
  }

  private fun evaluateEnableWhen(
    behavior: Questionnaire.EnableWhenBehavior? = null,
    vararg enableWhen: EnableWhen,
  ): kotlin.Boolean {
    val theLinkId = "target"
    val questionnaireItemBuilder =
      Questionnaire.Item.Builder(
          linkId = String.Builder().apply { value = theLinkId },
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
        )
        .apply {
          this.enableWhen =
            enableWhen.mapIndexedTo(mutableListOf()) { index, enableWhen ->
              Questionnaire.Item.EnableWhen.Builder(
                question = String.Builder().apply { value = "$index" },
                operator = Enumeration(value = enableWhen.operator),
                answer =
                  when (enableWhen.expected) {
                    is Boolean ->
                      Questionnaire.Item.EnableWhen.Answer.Boolean(value = enableWhen.expected)
                    is Decimal ->
                      Questionnaire.Item.EnableWhen.Answer.Decimal(value = enableWhen.expected)
                    is Integer ->
                      Questionnaire.Item.EnableWhen.Answer.Integer(value = enableWhen.expected)
                    is Date ->
                      Questionnaire.Item.EnableWhen.Answer.Date(value = enableWhen.expected)
                    is DateTime ->
                      Questionnaire.Item.EnableWhen.Answer.DateTime(value = enableWhen.expected)
                    is Time ->
                      Questionnaire.Item.EnableWhen.Answer.Time(value = enableWhen.expected)
                    is String ->
                      Questionnaire.Item.EnableWhen.Answer.String(value = enableWhen.expected)
                    is Coding ->
                      Questionnaire.Item.EnableWhen.Answer.Coding(value = enableWhen.expected)
                    is Quantity ->
                      Questionnaire.Item.EnableWhen.Answer.Quantity(value = enableWhen.expected)
                    is Reference ->
                      Questionnaire.Item.EnableWhen.Answer.Reference(value = enableWhen.expected)
                    else ->
                      throw IllegalStateException("Type not supported for ${enableWhen.expected}")
                  },
              )
            }
          this.enableBehavior = Enumeration(value = behavior)
        }
    val questionnaire =
      Questionnaire.Builder(status = Enumeration(value = PublicationStatus.Active))
        .apply { item = mutableListOf(questionnaireItemBuilder) }
        .build()

    val targetResponseItem =
      QuestionnaireResponse.Item.Builder(linkId = String.Builder().apply { value = theLinkId })

    val questionnaireResponse =
      QuestionnaireResponse.Builder(
          status = Enumeration(value = QuestionnaireResponse.QuestionnaireResponseStatus.Completed),
        )
        .apply {
          this.item =
            enableWhen
              .mapIndexedTo(mutableListOf()) { index, enableWhen ->
                QuestionnaireResponse.Item.Builder(
                    linkId = String.Builder().apply { value = "$index" },
                  )
                  .apply {
                    this.answer =
                      enableWhen.actual.mapTo(mutableListOf()) {
                        QuestionnaireResponse.Item.Answer.Builder().apply {
                          value =
                            when (it) {
                              is Boolean ->
                                QuestionnaireResponse.Item.Answer.Value.Boolean(value = it)
                              is Decimal ->
                                QuestionnaireResponse.Item.Answer.Value.Decimal(value = it)
                              is Integer ->
                                QuestionnaireResponse.Item.Answer.Value.Integer(value = it)
                              is Date -> QuestionnaireResponse.Item.Answer.Value.Date(value = it)
                              is DateTime ->
                                QuestionnaireResponse.Item.Answer.Value.DateTime(value = it)
                              is Time -> QuestionnaireResponse.Item.Answer.Value.Time(value = it)
                              is String ->
                                QuestionnaireResponse.Item.Answer.Value.String(value = it)
                              is Uri -> QuestionnaireResponse.Item.Answer.Value.Uri(value = it)
                              is Attachment ->
                                QuestionnaireResponse.Item.Answer.Value.Attachment(value = it)
                              is Coding ->
                                QuestionnaireResponse.Item.Answer.Value.Coding(value = it)
                              is Quantity ->
                                QuestionnaireResponse.Item.Answer.Value.Quantity(value = it)
                              is Reference ->
                                QuestionnaireResponse.Item.Answer.Value.Reference(value = it)
                              else -> null
                            }
                        }
                      }
                  }
              }
              .apply { add(targetResponseItem) }
        }
        .build()

    val result =
      EnablementEvaluator(questionnaire, questionnaireResponse)
        .evaluate(questionnaireItemBuilder.build(), targetResponseItem.build())
    return result
  }

  private data class EnableWhen(
    val operator: Questionnaire.QuestionnaireItemOperator,
    val expected: Any,
    val actual: List<Any>,
  )
}
