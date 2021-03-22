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
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class EnablementEvaluatorTest {
  @Test
  fun evaluate_noEnableWhen_shouldReturnTrue() {
    assertThat(
        EnablementEvaluator.evaluate(Questionnaire.QuestionnaireItemComponent()) {
          QuestionnaireItemWithResponse(null, null)
        }
      )
      .isTrue()
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
  fun evaluate_expectsAnswer_answerExists_shouldReturnTrue() {
    assertThat(
        EnablementEvaluator.evaluate(
          Questionnaire.QuestionnaireItemComponent()
            .addEnableWhen(
              Questionnaire.QuestionnaireItemEnableWhenComponent()
                .setQuestion("q1")
                .setOperator(Questionnaire.QuestionnaireItemOperator.EXISTS)
                .setAnswer(BooleanType(true))
            )
            .setType(Questionnaire.QuestionnaireItemType.BOOLEAN)
        ) {
          if (it == "q1") {
            QuestionnaireItemWithResponse(
              Questionnaire.QuestionnaireItemComponent(),
              QuestionnaireResponse.QuestionnaireResponseItemComponent()
                .addAnswer(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent())
            )
          } else {
            QuestionnaireItemWithResponse(null, null)
          }
        }
      )
      .isTrue()
  }

  @Test
  fun evaluate_expectsAnswer_answerDoesNotExist_shouldReturnFalse() {
    assertThat(
        EnablementEvaluator.evaluate(
          Questionnaire.QuestionnaireItemComponent()
            .addEnableWhen(
              Questionnaire.QuestionnaireItemEnableWhenComponent()
                .setQuestion("q1")
                .setOperator(Questionnaire.QuestionnaireItemOperator.EXISTS)
                .setAnswer(BooleanType(true))
            )
            .setType(Questionnaire.QuestionnaireItemType.BOOLEAN)
        ) {
          if (it == "q1") {
            QuestionnaireItemWithResponse(
              Questionnaire.QuestionnaireItemComponent(),
              QuestionnaireResponse.QuestionnaireResponseItemComponent()
            )
          } else {
            QuestionnaireItemWithResponse(null, null)
          }
        }
      )
      .isFalse()
  }

  @Test
  fun evaluate_expectsNoAnswer_answerExists_shouldReturnFalse() {
    assertThat(
        EnablementEvaluator.evaluate(
          Questionnaire.QuestionnaireItemComponent()
            .addEnableWhen(
              Questionnaire.QuestionnaireItemEnableWhenComponent()
                .setQuestion("q1")
                .setOperator(Questionnaire.QuestionnaireItemOperator.EXISTS)
                .setAnswer(BooleanType(false))
            )
            .setType(Questionnaire.QuestionnaireItemType.BOOLEAN)
        ) {
          if (it == "q1") {
            QuestionnaireItemWithResponse(
              Questionnaire.QuestionnaireItemComponent(),
              QuestionnaireResponse.QuestionnaireResponseItemComponent()
                .addAnswer(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent())
            )
          } else {
            QuestionnaireItemWithResponse(null, null)
          }
        }
      )
      .isFalse()
  }

  @Test
  fun evaluate_expectsNoAnswer_answerDoesNotExist_shouldReturnTrue() {
    assertThat(
        EnablementEvaluator.evaluate(
          Questionnaire.QuestionnaireItemComponent()
            .addEnableWhen(
              Questionnaire.QuestionnaireItemEnableWhenComponent()
                .setQuestion("q1")
                .setOperator(Questionnaire.QuestionnaireItemOperator.EXISTS)
                .setAnswer(BooleanType(false))
            )
            .setType(Questionnaire.QuestionnaireItemType.BOOLEAN)
        ) {
          if (it == "q1") {
            QuestionnaireItemWithResponse(
              Questionnaire.QuestionnaireItemComponent(),
              QuestionnaireResponse.QuestionnaireResponseItemComponent()
            )
          } else {
            QuestionnaireItemWithResponse(null, null)
          }
        }
      )
      .isTrue()
  }

  @Test
  fun evaluate_anyEnableWhens_noneSatisfied_shouldReturnFalse() {
    assertThat(
        EnablementEvaluator.evaluate(
          Questionnaire.QuestionnaireItemComponent()
            .addEnableWhen(
              Questionnaire.QuestionnaireItemEnableWhenComponent()
                .setQuestion("q1")
                .setOperator(Questionnaire.QuestionnaireItemOperator.EXISTS)
                .setAnswer(BooleanType(true))
            )
            .addEnableWhen(
              Questionnaire.QuestionnaireItemEnableWhenComponent()
                .setQuestion("q2")
                .setOperator(Questionnaire.QuestionnaireItemOperator.EXISTS)
                .setAnswer(BooleanType(true))
            )
            .setEnableBehavior(Questionnaire.EnableWhenBehavior.ANY)
            .setType(Questionnaire.QuestionnaireItemType.BOOLEAN)
        ) {
          when (it) {
            "q1" ->
              QuestionnaireItemWithResponse(
                Questionnaire.QuestionnaireItemComponent(),
                QuestionnaireResponse.QuestionnaireResponseItemComponent()
              )
            "q2" ->
              QuestionnaireItemWithResponse(
                Questionnaire.QuestionnaireItemComponent(),
                QuestionnaireResponse.QuestionnaireResponseItemComponent()
              )
            else -> QuestionnaireItemWithResponse(null, null)
          }
        }
      )
      .isFalse()
  }

  @Test
  fun evaluate_anyEnableWhens_oneSatisfied_shouldReturnTrue() {
    assertThat(
        EnablementEvaluator.evaluate(
          Questionnaire.QuestionnaireItemComponent()
            .addEnableWhen(
              Questionnaire.QuestionnaireItemEnableWhenComponent()
                .setQuestion("q1")
                .setOperator(Questionnaire.QuestionnaireItemOperator.EXISTS)
                .setAnswer(BooleanType(false))
            )
            .addEnableWhen(
              Questionnaire.QuestionnaireItemEnableWhenComponent()
                .setQuestion("q2")
                .setOperator(Questionnaire.QuestionnaireItemOperator.EXISTS)
                .setAnswer(BooleanType(true))
            )
            .setEnableBehavior(Questionnaire.EnableWhenBehavior.ANY)
            .setType(Questionnaire.QuestionnaireItemType.BOOLEAN)
        ) {
          when (it) {
            "q1" ->
              QuestionnaireItemWithResponse(
                Questionnaire.QuestionnaireItemComponent(),
                QuestionnaireResponse.QuestionnaireResponseItemComponent()
              )
            "q2" ->
              QuestionnaireItemWithResponse(
                Questionnaire.QuestionnaireItemComponent(),
                QuestionnaireResponse.QuestionnaireResponseItemComponent()
              )
            else -> QuestionnaireItemWithResponse(null, null)
          }
        }
      )
      .isTrue()
  }

  @Test
  fun evaluate_allEnableWhens_someSatisfied_shouldReturnFalse() {
    assertThat(
        EnablementEvaluator.evaluate(
          Questionnaire.QuestionnaireItemComponent()
            .addEnableWhen(
              Questionnaire.QuestionnaireItemEnableWhenComponent()
                .setQuestion("q1")
                .setOperator(Questionnaire.QuestionnaireItemOperator.EXISTS)
                .setAnswer(BooleanType(false))
            )
            .addEnableWhen(
              Questionnaire.QuestionnaireItemEnableWhenComponent()
                .setQuestion("q2")
                .setOperator(Questionnaire.QuestionnaireItemOperator.EXISTS)
                .setAnswer(BooleanType(true))
            )
            .setEnableBehavior(Questionnaire.EnableWhenBehavior.ALL)
            .setType(Questionnaire.QuestionnaireItemType.BOOLEAN)
        ) {
          when (it) {
            "q1" ->
              QuestionnaireItemWithResponse(
                Questionnaire.QuestionnaireItemComponent(),
                QuestionnaireResponse.QuestionnaireResponseItemComponent()
              )
            "q2" ->
              QuestionnaireItemWithResponse(
                Questionnaire.QuestionnaireItemComponent(),
                QuestionnaireResponse.QuestionnaireResponseItemComponent()
              )
            else -> QuestionnaireItemWithResponse(null, null)
          }
        }
      )
      .isFalse()
  }

  @Test
  fun evaluate_allEnableWhens_allSatisfied_shouldReturnTrue() {
    assertThat(
        EnablementEvaluator.evaluate(
          Questionnaire.QuestionnaireItemComponent()
            .addEnableWhen(
              Questionnaire.QuestionnaireItemEnableWhenComponent()
                .setQuestion("q1")
                .setOperator(Questionnaire.QuestionnaireItemOperator.EXISTS)
                .setAnswer(BooleanType(false))
            )
            .addEnableWhen(
              Questionnaire.QuestionnaireItemEnableWhenComponent()
                .setQuestion("q2")
                .setOperator(Questionnaire.QuestionnaireItemOperator.EXISTS)
                .setAnswer(BooleanType(false))
            )
            .setEnableBehavior(Questionnaire.EnableWhenBehavior.ALL)
            .setType(Questionnaire.QuestionnaireItemType.BOOLEAN)
        ) {
          when (it) {
            "q1" ->
              QuestionnaireItemWithResponse(
                Questionnaire.QuestionnaireItemComponent(),
                QuestionnaireResponse.QuestionnaireResponseItemComponent()
              )
            "q2" ->
              QuestionnaireItemWithResponse(
                Questionnaire.QuestionnaireItemComponent(),
                QuestionnaireResponse.QuestionnaireResponseItemComponent()
              )
            else -> QuestionnaireItemWithResponse(null, null)
          }
        }
      )
      .isTrue()
  }

  @Test
  fun evaluate_expectsAnswer_answerEqual_shouldReturnTrue() {
    assertThat(
        EnablementEvaluator.evaluate(
          Questionnaire.QuestionnaireItemComponent()
            .addEnableWhen(
              Questionnaire.QuestionnaireItemEnableWhenComponent()
                .setQuestion("q1")
                .setOperator(Questionnaire.QuestionnaireItemOperator.EQUAL)
                .setAnswer(BooleanType(true))
            )
            .setType(Questionnaire.QuestionnaireItemType.BOOLEAN)
        ) {
          if (it == "q1") {
            QuestionnaireItemWithResponse(
              Questionnaire.QuestionnaireItemComponent()
                .setType(Questionnaire.QuestionnaireItemType.BOOLEAN),
              QuestionnaireResponse.QuestionnaireResponseItemComponent()
                .addAnswer(
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                    .setValue(BooleanType(true))
                )
            )
          } else {
            QuestionnaireItemWithResponse(null, null)
          }
        }
      )
      .isTrue()
  }

  @Test
  fun evaluate_expectsAnswer_answerDoesNotEqual_shouldReturnFalse() {
    assertThat(
        EnablementEvaluator.evaluate(
          Questionnaire.QuestionnaireItemComponent()
            .addEnableWhen(
              Questionnaire.QuestionnaireItemEnableWhenComponent()
                .setQuestion("q1")
                .setOperator(Questionnaire.QuestionnaireItemOperator.EQUAL)
                .setAnswer(BooleanType(true))
            )
            .setType(Questionnaire.QuestionnaireItemType.BOOLEAN)
        ) {
          if (it == "q1") {
            QuestionnaireItemWithResponse(
              Questionnaire.QuestionnaireItemComponent()
                .setType(Questionnaire.QuestionnaireItemType.BOOLEAN),
              QuestionnaireResponse.QuestionnaireResponseItemComponent()
                .addAnswer(
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                    .setValue(BooleanType(false))
                )
            )
          } else {
            QuestionnaireItemWithResponse(null, null)
          }
        }
      )
      .isFalse()
  }

  @Test
  fun evaluate_expectsAnswer_answerEqualOne_shouldReturnTrue() {
    assertThat(
        EnablementEvaluator.evaluate(
          Questionnaire.QuestionnaireItemComponent().apply {
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addEnableWhen(
              Questionnaire.QuestionnaireItemEnableWhenComponent()
                .setQuestion("q1")
                .setOperator(Questionnaire.QuestionnaireItemOperator.EQUAL)
                .setAnswer(BooleanType(true))
            )
          }
        ) {
          if (it == "q1") {
            QuestionnaireItemWithResponse(
              Questionnaire.QuestionnaireItemComponent()
                .setType(Questionnaire.QuestionnaireItemType.BOOLEAN),
              QuestionnaireResponse.QuestionnaireResponseItemComponent()
                .addAnswer(
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                    .setValue(BooleanType(true))
                )
                .addAnswer(
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                    .setValue(BooleanType(false))
                )
            )
          } else {
            QuestionnaireItemWithResponse(null, null)
          }
        }
      )
      .isTrue()
  }

  @Test
  fun evaluate_expectsAnswer_answerNotEqual_shouldReturnTrue() {
    assertThat(
        EnablementEvaluator.evaluate(
          Questionnaire.QuestionnaireItemComponent()
            .addEnableWhen(
              Questionnaire.QuestionnaireItemEnableWhenComponent()
                .setQuestion("q1")
                .setOperator(Questionnaire.QuestionnaireItemOperator.NOT_EQUAL)
                .setAnswer(BooleanType(true))
            )
            .setType(Questionnaire.QuestionnaireItemType.BOOLEAN)
        ) {
          if (it == "q1") {
            QuestionnaireItemWithResponse(
              Questionnaire.QuestionnaireItemComponent()
                .setType(Questionnaire.QuestionnaireItemType.BOOLEAN),
              QuestionnaireResponse.QuestionnaireResponseItemComponent()
                .addAnswer(
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                    .setValue(BooleanType(false))
                )
            )
          } else {
            QuestionnaireItemWithResponse(null, null)
          }
        }
      )
      .isTrue()
  }

  @Test
  fun evaluate_expectsAnswer_answerDoesNotNotEqual_shouldReturnFalse() {
    assertThat(
        EnablementEvaluator.evaluate(
          Questionnaire.QuestionnaireItemComponent()
            .addEnableWhen(
              Questionnaire.QuestionnaireItemEnableWhenComponent()
                .setQuestion("q1")
                .setOperator(Questionnaire.QuestionnaireItemOperator.NOT_EQUAL)
                .setAnswer(BooleanType(true))
            )
            .setType(Questionnaire.QuestionnaireItemType.BOOLEAN)
        ) {
          if (it == "q1") {
            QuestionnaireItemWithResponse(
              Questionnaire.QuestionnaireItemComponent()
                .setType(Questionnaire.QuestionnaireItemType.BOOLEAN),
              QuestionnaireResponse.QuestionnaireResponseItemComponent()
                .addAnswer(
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                    .setValue(BooleanType(true))
                )
            )
          } else {
            QuestionnaireItemWithResponse(null, null)
          }
        }
      )
      .isFalse()
  }

  @Test
  fun evaluate_expectsAnswer_answerNotEqualOne_shouldReturnTrue() {
    assertThat(
        EnablementEvaluator.evaluate(
          Questionnaire.QuestionnaireItemComponent()
            .addEnableWhen(
              Questionnaire.QuestionnaireItemEnableWhenComponent()
                .setQuestion("q1")
                .setOperator(Questionnaire.QuestionnaireItemOperator.NOT_EQUAL)
                .setAnswer(BooleanType(true))
            )
            .setType(Questionnaire.QuestionnaireItemType.BOOLEAN)
        ) {
          if (it == "q1") {
            QuestionnaireItemWithResponse(
              Questionnaire.QuestionnaireItemComponent()
                .setType(Questionnaire.QuestionnaireItemType.BOOLEAN),
              QuestionnaireResponse.QuestionnaireResponseItemComponent()
                .addAnswer(
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                    .setValue(BooleanType(true))
                )
                .addAnswer(
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                    .setValue(BooleanType(false))
                )
            )
          } else {
            QuestionnaireItemWithResponse(null, null)
          }
        }
      )
      .isTrue()
  }
}
