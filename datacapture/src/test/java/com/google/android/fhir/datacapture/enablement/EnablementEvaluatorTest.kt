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
import org.hl7.fhir.r4.model.Type
import org.intellij.lang.annotations.Language
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class EnablementEvaluatorTest {
  @Test
  fun evaluate_noEnableWhen_shouldReturnTrue() {
    evaluateEnableWhen().isTrue()
  }

  @Test
  fun evaluate_missingResponse_shouldReturnFalse() {
    val questionnaire =
      Questionnaire.QuestionnaireItemComponent().apply {
        type = Questionnaire.QuestionnaireItemType.BOOLEAN
        addEnableWhen(Questionnaire.QuestionnaireItemEnableWhenComponent().setQuestion("q1"))
      }
    assertThat(
        EnablementEvaluator.evaluate(
          questionnaire,
          QuestionnaireResponse.QuestionnaireResponseItemComponent(),
          QuestionnaireResponse()
        ) { _, _ -> null }
      )
      .isFalse()
  }

  @Test
  fun evaluate_expectAnswerExists_answerExists_shouldReturnTrue() {
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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

    val iParser: IParser = FhirContext.forR4().newJsonParser()

    val questionnaire =
      iParser.parseResource(Questionnaire::class.java, questionnaireJson) as Questionnaire

    var questionnaireItemComponent: Questionnaire.QuestionnaireItemComponent =
      Questionnaire.QuestionnaireItemComponent()
    questionnaire.item.forEach { item -> if (item.linkId == "2") questionnaireItemComponent = item }

    val questionnaireResponse =
      iParser.parseResource(QuestionnaireResponse::class.java, questionnaireResponseJson) as
        QuestionnaireResponse

    assertThat(
        EnablementEvaluator.evaluate(
          questionnaireItemComponent,
          questionnaireResponse.item[1],
          questionnaireResponse
        ) { _, _ -> null }
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

    val iParser: IParser = FhirContext.forR4().newJsonParser()

    val questionnaire =
      iParser.parseResource(Questionnaire::class.java, questionnaireJson) as Questionnaire

    var questionnaireItemComponent: Questionnaire.QuestionnaireItemComponent =
      Questionnaire.QuestionnaireItemComponent()
    questionnaire.item.forEach { item -> if (item.linkId == "2") questionnaireItemComponent = item }
    val questionnaireResponse =
      iParser.parseResource(QuestionnaireResponse::class.java, questionnaireResponseJson) as
        QuestionnaireResponse

    assertThat(
        EnablementEvaluator.evaluate(
          questionnaireItemComponent,
          questionnaireResponse.item[1],
          questionnaireResponse
        ) { _, _ -> null }
      )
      .isFalse()
  }

  @Test
  fun evaluate_expectAnswerDoesNotExist_answerDoesNotExist_shouldReturnTrue() {
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
    evaluateEnableWhen(
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
  private fun evaluateEnableWhen(
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
    val questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()
    return assertThat(
      EnablementEvaluator.evaluate(
        questionnaireItem,
        questionnaireResponseItem,
        QuestionnaireResponse()
      ) { origin, linkId ->
        if (origin === questionnaireResponseItem) {
          return@evaluate QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            enableWhen[linkId.toInt()].actual.forEach {
              addAnswer(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(it)
              )
            }
          }
        }
        return@evaluate null
      }
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
