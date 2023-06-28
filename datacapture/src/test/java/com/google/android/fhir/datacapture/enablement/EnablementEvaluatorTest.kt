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

package com.google.android.fhir.datacapture.enablement

import android.os.Build
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.parser.IParser
import com.google.common.truth.BooleanSubject
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
import org.hl7.fhir.r4.model.Type
import org.intellij.lang.annotations.Language
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class EnablementEvaluatorTest {
  val iParser: IParser = FhirContext.forR4Cached().newJsonParser()

  @Test
  fun evaluate_noEnableWhen_shouldReturnTrue() {
    assertEnableWhen().isTrue()
  }

  @Test
  fun evaluate_missingResponseItem_shouldReturnFalse() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        linkId = "q1"
        type = Questionnaire.QuestionnaireItemType.BOOLEAN
        addEnableWhen(Questionnaire.QuestionnaireItemEnableWhenComponent().setQuestion("q2"))
      }
    val questionnaireResponseItem =
      QuestionnaireResponse.QuestionnaireResponseItemComponent().apply { linkId = "q1" }
    val questionnaireResponse = QuestionnaireResponse().apply { addItem(questionnaireResponseItem) }
    assertThat(
        EnablementEvaluator(questionnaireResponse)
          .evaluate(questionnaireItem, questionnaireResponseItem)
      )
      .isFalse()
  }

  @Test
  fun evaluate_expectAnswerExists_answerExists_shouldReturnTrue() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EXISTS,
          expected = BooleanType(true),
          actual = listOf(IntegerType(123))
        )
      )
      .isTrue()
  }

  @Test
  fun evaluate_expectAnswerExists_answerDoesNotExist_shouldReturnFalse() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EXISTS,
          expected = BooleanType(true),
          actual = listOf()
        )
      )
      .isFalse()
  }

  @Test
  fun evaluate_expectAnswerDoesNotExist_answerExists_shouldReturnFalse() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EXISTS,
          expected = BooleanType(false),
          actual = listOf(IntegerType(123))
        )
      )
      .isFalse()
  }

  @Test
  fun `evaluate() should evaluate enableWhenExpression`() = runBlocking {
    @Language("JSON")
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
      """.trimIndent()

    val questionnaire =
      iParser.parseResource(Questionnaire::class.java, questionnaireJson) as Questionnaire

    var questionnaireItem: Questionnaire.QuestionnaireItemComponent =
      Questionnaire.QuestionnaireItemComponent()
    questionnaire.item.forEach { item -> if (item.linkId == "2") questionnaireItem = item }

    val questionnaireResponse =
      iParser.parseResource(QuestionnaireResponse::class.java, questionnaireResponseJson)
        as QuestionnaireResponse

    assertThat(
        EnablementEvaluator(questionnaireResponse)
          .evaluate(
            questionnaireItem,
            questionnaireResponse.item[1],
          )
      )
      .isTrue()
  }

  @Test
  fun `evaluate() should evaluate false enableWhenExpression`() = runBlocking {
    @Language("JSON")
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
      """.trimIndent()

    val questionnaire =
      iParser.parseResource(Questionnaire::class.java, questionnaireJson) as Questionnaire

    var questionnaireItemComponent: Questionnaire.QuestionnaireItemComponent =
      Questionnaire.QuestionnaireItemComponent()
    questionnaire.item.forEach { item -> if (item.linkId == "2") questionnaireItemComponent = item }
    val questionnaireResponse =
      iParser.parseResource(QuestionnaireResponse::class.java, questionnaireResponseJson)
        as QuestionnaireResponse

    assertThat(
        EnablementEvaluator(questionnaireResponse)
          .evaluate(
            questionnaireItemComponent,
            questionnaireResponse.item[1],
          )
      )
      .isFalse()
  }

  @Test
  fun `evaluate() should evaluate enableWhenExpression with context fhirpath supplement literal`() =
    runBlocking {
      @Language("JSON")
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
        """.trimIndent()

      val questionnaire =
        iParser.parseResource(Questionnaire::class.java, questionnaireJson) as Questionnaire

      val questionnaireItem: Questionnaire.QuestionnaireItemComponent =
        questionnaire.item.find { it.linkId == "female" }!!

      val questionnaireResponse =
        iParser.parseResource(QuestionnaireResponse::class.java, questionnaireResponseJson)
          as QuestionnaireResponse

      assertThat(
          EnablementEvaluator(questionnaireResponse)
            .evaluate(
              questionnaireItem,
              questionnaireResponse.item[1],
            )
        )
        .isTrue()
    }

  @Test
  fun evaluate_expectAnswerDoesNotExist_answerDoesNotExist_shouldReturnTrue() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EXISTS,
          expected = BooleanType(false),
          actual = listOf()
        )
      )
      .isTrue()
  }

  @Test
  fun evaluate_expectAnswerEqualToValue_noAnswer_shouldReturnFalse() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EQUAL,
          expected = IntegerType(123),
          actual = listOf()
        )
      )
      .isFalse()
  }

  @Test
  fun evaluate_expectAnswerEqualToValue_someAnswerEqualToValue_shouldReturnTrue() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EQUAL,
          expected = IntegerType(123),
          actual = listOf(IntegerType(123), IntegerType(456))
        )
      )
      .isTrue()
  }

  @Test
  fun evaluate_expectAnswerEqualToValue_noAnswerEqualToValue_shouldReturnFalse() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EQUAL,
          expected = IntegerType(123),
          actual = listOf(IntegerType(456), IntegerType(789))
        )
      )
      .isFalse()
  }

  @Test
  fun evaluate_expectAnswerNotEqualToValue_noAnswer_shouldReturnFalse() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.NOT_EQUAL,
          expected = IntegerType(123),
          actual = listOf()
        )
      )
      .isFalse()
  }

  @Test
  fun evaluate_expectAnswerNotEqualToValue_someAnswerNotEqualToValue_shouldReturnTrue() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.NOT_EQUAL,
          expected = IntegerType(123),
          actual = listOf(IntegerType(123), IntegerType(456))
        )
      )
      .isTrue()
  }

  @Test
  fun evaluate_expectAnswerNotEqualToValue_noAnswerNotEqualToValue_shouldReturnFalse() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.NOT_EQUAL,
          expected = IntegerType(123),
          actual = listOf(IntegerType(123), IntegerType(123))
        )
      )
      .isFalse()
  }

  @Test
  fun evaluate_expectAnswerGreaterThanValue_someAnswerGreaterThanValue_shouldReturnTrue() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.GREATER_THAN,
          expected = IntegerType(10),
          actual = listOf(IntegerType(20))
        )
      )
      .isTrue()
  }

  @Test
  fun evaluate_expectAnswerGreaterThanValue_noAnswerGreaterThanValue_shouldReturnFalse() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.GREATER_THAN,
          expected = IntegerType(10),
          actual = listOf(IntegerType(5))
        )
      )
      .isFalse()
  }

  @Test
  fun evaluate_expectAnswerGreaterThanOrEqualToValue_someAnswerGreaterThanOrEqualToValue_shouldReturnTrue() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.GREATER_OR_EQUAL,
          expected = IntegerType(10),
          actual = listOf(IntegerType(10))
        )
      )
      .isTrue()
  }

  @Test
  fun evaluate_expectAnswerGreaterThanOrEqualToValue_noAnswerGreaterThanOrEqualToValue_shouldReturnFalse() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.GREATER_OR_EQUAL,
          expected = IntegerType(10),
          actual = listOf(IntegerType(5))
        )
      )
      .isFalse()
  }

  @Test
  fun evaluate_expectAnswerLessThanValue_someAnswerLessThanValue_shouldReturnTrue() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.LESS_THAN,
          expected = IntegerType(10),
          actual = listOf(IntegerType(5))
        )
      )
      .isTrue()
  }

  @Test
  fun evaluate_expectAnswerLessThanValue_noAnswerLessThanValue_shouldReturnFalse() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.LESS_THAN,
          expected = IntegerType(10),
          actual = listOf(IntegerType(20))
        )
      )
      .isFalse()
  }

  @Test
  fun evaluate_expectAnswerLessThanOrEqualToValue_someAnswerLessThanOrEqualToValue_shouldReturnTrue() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.LESS_OR_EQUAL,
          expected = IntegerType(10),
          actual = listOf(IntegerType(10))
        )
      )
      .isTrue()
  }

  @Test
  fun evaluate_expectAnswerLessThanOrEqualToValue_noAnswerLessThanOrEqualToValue_shouldReturnFalse() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.LESS_OR_EQUAL,
          expected = IntegerType(10),
          actual = listOf(IntegerType(20))
        )
      )
      .isFalse()
  }

  @Test
  fun evaluate_multipleEnableWhens_behaviorAny_noneSatisfied_shouldReturnFalse() {
    assertEnableWhen(
        behavior = Questionnaire.EnableWhenBehavior.ANY,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EXISTS,
          expected = BooleanType(true),
          actual = listOf()
        ),
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EXISTS,
          expected = BooleanType(true),
          actual = listOf()
        )
      )
      .isFalse()
  }

  @Test
  fun evaluate_multipleEnableWhens_behaviorAny_someSatisfied_shouldReturnTrue() {
    assertEnableWhen(
        behavior = Questionnaire.EnableWhenBehavior.ANY,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EXISTS,
          expected = BooleanType(false),
          actual = listOf()
        ),
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EXISTS,
          expected = BooleanType(true),
          actual = listOf()
        )
      )
      .isTrue()
  }

  @Test
  fun evaluate_multipleEnableWhens_behaviorAll_someSatisfied_shouldReturnFalse() {
    assertEnableWhen(
        behavior = Questionnaire.EnableWhenBehavior.ALL,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EXISTS,
          expected = BooleanType(false),
          actual = listOf()
        ),
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EXISTS,
          expected = BooleanType(true),
          actual = listOf()
        )
      )
      .isFalse()
  }

  @Test
  fun evaluate_multipleEnableWhens_behaviorAll_allSatisfied_shouldReturnTrue() {
    assertEnableWhen(
        behavior = Questionnaire.EnableWhenBehavior.ALL,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EXISTS,
          expected = BooleanType(false),
          actual = listOf()
        ),
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EXISTS,
          expected = BooleanType(false),
          actual = listOf()
        )
      )
      .isTrue()
  }

  @Test
  fun evaluate_primitiveType_equal_shouldReturnTrue() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EQUAL,
          expected = IntegerType(123),
          actual = listOf(IntegerType(123))
        )
      )
      .isTrue()
  }

  @Test
  fun evaluate_primitiveType_equal_shouldReturnFalse() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EQUAL,
          expected = IntegerType(123),
          actual = listOf(IntegerType(456))
        )
      )
      .isFalse()
  }

  @Test
  fun evaluate_primitiveType_notEqual_shouldReturnTrue() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.NOT_EQUAL,
          expected = IntegerType(123),
          actual = listOf(IntegerType(456))
        )
      )
      .isTrue()
  }

  @Test
  fun evaluate_primitiveType_notEqual_shouldReturnFalse() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.NOT_EQUAL,
          expected = IntegerType(123),
          actual = listOf(IntegerType(123))
        )
      )
      .isFalse()
  }

  @Test
  fun evaluate_codingType_equal_differentSystem_shouldReturnFalse() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EQUAL,
          expected = Coding("system", "code", "display"),
          actual = listOf(Coding("otherSystem", "code", "display"))
        )
      )
      .isFalse()
  }

  @Test
  fun evaluate_codingType_equal_differentCode_shouldReturnFalse() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EQUAL,
          expected = Coding("system", "code", "display"),
          actual = listOf(Coding("system", "otherCode", "display"))
        )
      )
      .isFalse()
  }

  @Test
  fun evaluate_codingType_equal_differentDisplay_shouldReturnTrue() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.EQUAL,
          expected = Coding("system", "code", "display"),
          actual = listOf(Coding("system", "code", "otherDisplay"))
        )
      )
      .isTrue()
  }

  @Test
  fun evaluate_codingType_notEqual_differentSystem_shouldReturnTrue() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.NOT_EQUAL,
          expected = Coding("system", "code", "display"),
          actual = listOf(Coding("otherSystem", "code", "display"))
        )
      )
      .isTrue()
  }

  @Test
  fun evaluate_codingType_notEqual_differentCode_shouldReturnTrue() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.NOT_EQUAL,
          expected = Coding("system", "code", "display"),
          actual = listOf(Coding("system", "otherCode", "display"))
        )
      )
      .isTrue()
  }

  @Test
  fun evaluate_codingType_notEqual_differentDisplay_shouldReturnFalse() {
    assertEnableWhen(
        behavior = null,
        EnableWhen(
          operator = Questionnaire.QuestionnaireItemOperator.NOT_EQUAL,
          expected = Coding("system", "code", "display"),
          actual = listOf(Coding("system", "code", "otherDisplay"))
        )
      )
      .isFalse()
  }

  /**
   * Evaluates multiple `enableWhen` constraints according to the `behavior` (any or all).
   *
   * See https://www.hl7.org/fhir/valueset-questionnaire-enable-behavior.html.
   */
  private fun assertEnableWhen(
    behavior: Questionnaire.EnableWhenBehavior? = null,
    vararg enableWhen: EnableWhen
  ): BooleanSubject {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        enableWhen.forEachIndexed { index, enableWhen ->
          addEnableWhen(
            Questionnaire.QuestionnaireItemEnableWhenComponent()
              .setQuestion("$index") // use the index as linkId
              .setOperator(enableWhen.operator)
              .setAnswer(enableWhen.expected)
          )
        }
        behavior?.let { enableBehavior = it }
        type = Questionnaire.QuestionnaireItemType.BOOLEAN
      }
    val questionnaireResponse =
      QuestionnaireResponse().apply {
        enableWhen.forEachIndexed { index, enableWhen ->
          addItem(
            QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
              linkId = "$index"
              enableWhen.actual.forEach {
                addAnswer(QuestionnaireResponseItemAnswerComponent().apply { value = it })
              }
            }
          )
        }
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply { linkId = "target" }
        )
      }
    return assertThat(
      EnablementEvaluator(questionnaireResponse)
        .evaluate(questionnaireItem, questionnaireResponse.item.last())
    )
  }

  /**
   * Encapsulates the `enableWhen` constraint (`operator` and `expected` answer) and the `actual`
   * answers used for evaluation. In the test cases, the actual answers will be provided when the
   * evaluator retrieves answers to the question that matches the `linkId` of the `enableWhen`
   * constraint.
   */
  private data class EnableWhen(
    val operator: Questionnaire.QuestionnaireItemOperator,
    val expected: Type,
    val actual: List<Type>
  )
}
