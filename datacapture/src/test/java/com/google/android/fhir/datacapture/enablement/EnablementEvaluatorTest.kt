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

package com.google.android.fhir.datacapture.enablement

import android.os.Build
import com.google.common.truth.BooleanSubject
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Type
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
  fun evaluate_missingQuestion_shouldReturnTrue() {
    assertThat(
        EnablementEvaluator.evaluate(
          Questionnaire.QuestionnaireItemComponent().apply {
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addEnableWhen(Questionnaire.QuestionnaireItemEnableWhenComponent().setQuestion("q1"))
          }
        ) { QuestionnaireItemWithResponse(null, null) }
      )
      .isTrue()
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
    return assertThat(
      EnablementEvaluator.evaluate(
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
      ) { linkId ->
        QuestionnaireItemWithResponse(
          Questionnaire.QuestionnaireItemComponent(),
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            enableWhen[linkId.toInt()].actual.forEach {
              addAnswer(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(it)
              )
            }
          }
        )
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
