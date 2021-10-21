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

package com.google.android.fhir.datacapture

import android.os.Build
import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Reference
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MoreTypesTest {

  @Test
  fun equals_differentTypes_shouldReturnFalse() {
    assertThat(equals(BooleanType(true), DecimalType(1.1))).isFalse()
  }

  @Test
  fun equals_sameObject_shouldReturnTrue() {
    val value = BooleanType(true)
    assertThat(equals(value, value)).isTrue()
  }

  @Test
  fun equals_samePrimitiveValue_shouldReturnTrue() {
    assertThat(equals(DecimalType(1.1), DecimalType(1.1))).isTrue()
  }

  @Test
  fun equals_differentPrimitiveValues_shouldReturnFalse() {
    assertThat(equals(DecimalType(1.1), DecimalType(1.2))).isFalse()
  }

  @Test
  fun equals_coding_differentSystems_shouldReturnFalse() {
    assertThat(
        equals(Coding("system", "code", "display"), Coding("otherSystem", "code", "display"))
      )
      .isFalse()
  }

  @Test
  fun equals_coding_differentCodes_shouldReturnFalse() {
    assertThat(
        equals(Coding("system", "code", "display"), Coding("system", "otherCode", "display"))
      )
      .isFalse()
  }

  @Test
  fun equals_coding_differentDisplays_shouldReturnTrue() {
    assertThat(
        equals(Coding("system", "code", "display"), Coding("system", "code", "otherDisplay"))
      )
      .isTrue()
  }

  @Test
  fun equals_attachment_shouldThrowException() {
    val exception =
      assertThrows(NotImplementedError::class.java) { equals(Attachment(), Attachment()) }

    assertThat(exception.message)
      .isEqualTo("Comparison for type ${Attachment::class.java} not supported.")
  }

  @Test
  fun equals_quantity_shouldThrowException() {
    val exception = assertThrows(NotImplementedError::class.java) { equals(Quantity(), Quantity()) }

    assertThat(exception.message)
      .isEqualTo("Comparison for type ${Quantity::class.java} not supported.")
  }

  @Test
  fun equals_reference_shouldThrowException() {
    val exception =
      assertThrows(NotImplementedError::class.java) { equals(Reference(), Reference()) }

    assertThat(exception.message)
      .isEqualTo("Comparison for type ${Reference::class.java} not supported.")
  }

  @Test
  fun greaterThan_noAnswerGreaterThanEnableAnswer_shouldReturnTrue() {
    val enableWhen =
      Questionnaire.QuestionnaireItemEnableWhenComponent().apply {
        answer = IntegerType().apply { value = 50 }
      }
    val answer = IntegerType().apply { value = 40 }

    assertThat(enableWhen.greaterThan(answer)).isTrue()
  }

  @Test
  fun greaterThan_answerGreaterThanEnableAnswer_shouldReturnFalse() {
    val enableWhen =
      Questionnaire.QuestionnaireItemEnableWhenComponent().apply {
        answer = IntegerType().apply { value = 50 }
      }
    val answer = IntegerType().apply { value = 100 }

    assertThat(enableWhen.greaterThan(answer)).isFalse()
  }

  @Test
  fun greaterThan_answerEqualEnableAnswer_shouldReturnTrue() {
    val enableWhen =
      Questionnaire.QuestionnaireItemEnableWhenComponent().apply {
        answer = IntegerType().apply { value = 50 }
      }
    val answer = IntegerType().apply { value = 50 }

    assertThat(enableWhen.greaterThan(answer)).isTrue()
  }

  @Test
  fun greaterThan_nonPrimitiveValues_shouldReturnFalse() {
    val enableWhen =
      Questionnaire.QuestionnaireItemEnableWhenComponent().apply { answer = Coding() }
    val answer = Coding()

    assertThat(enableWhen.greaterThan(answer)).isFalse()
  }

  @Test
  fun greaterThan_noAnswerQuantityGreaterThanEnableAnswerQuantity_shouldReturnTrue() {
    val enableWhen =
      Questionnaire.QuestionnaireItemEnableWhenComponent().apply {
        answer =
          answerQuantity.apply {
            value = BigDecimal(10)
            code = "h"
          }
        operator = Questionnaire.QuestionnaireItemOperator.GREATER_THAN
      }
    val answer =
      Quantity().apply {
        value = BigDecimal(20)
        code = "min"
      }

    assertThat(enableWhen.greaterThan(answer)).isTrue()
  }

  @Test
  fun greaterThan_differentQuantityUnits_shouldReturnFalse() {
    val enableWhen =
      Questionnaire.QuestionnaireItemEnableWhenComponent().apply {
        answer =
          answerQuantity.apply {
            value = BigDecimal(10)
            code = "h"
          }
        operator = Questionnaire.QuestionnaireItemOperator.GREATER_THAN
      }
    val answer =
      Quantity().apply {
        value = BigDecimal(10)
        code = "kg"
      }

    assertThat(enableWhen.greaterThan(answer)).isFalse()
  }

  @Test
  fun greaterOrEqual_noAnswerGreaterThanEnableAnswer_shouldReturnTrue() {
    val enableWhen =
      Questionnaire.QuestionnaireItemEnableWhenComponent().apply {
        answer = IntegerType().apply { value = 50 }
      }
    val answer = IntegerType().apply { value = 40 }

    assertThat(enableWhen.greaterOrEqual(answer)).isTrue()
  }

  @Test
  fun greaterOrEqual_answerGreaterThanEnableAnswer_shouldReturnFalse() {
    val enableWhen =
      Questionnaire.QuestionnaireItemEnableWhenComponent().apply {
        answer = IntegerType().apply { value = 50 }
      }
    val answer = IntegerType().apply { value = 100 }

    assertThat(enableWhen.greaterOrEqual(answer)).isFalse()
  }

  @Test
  fun greaterOrEqual_answerEqualEnableAnswer_shouldReturnFalse() {
    val enableWhen =
      Questionnaire.QuestionnaireItemEnableWhenComponent().apply {
        answer = IntegerType().apply { value = 50 }
      }
    val answer = IntegerType().apply { value = 50 }

    assertThat(enableWhen.greaterOrEqual(answer)).isFalse()
  }

  @Test
  fun greaterOrEqual_nonPrimitive_shouldReturnFalse() {
    val enableWhen =
      Questionnaire.QuestionnaireItemEnableWhenComponent().apply { answer = Coding() }
    val answer = Coding()

    assertThat(enableWhen.greaterOrEqual(answer)).isFalse()
  }

  @Test
  fun greaterOrEqual_AnswerQuantityGreaterThanEnableAnswerQuantity_shouldReturnFalse() {
    val enableWhen =
      Questionnaire.QuestionnaireItemEnableWhenComponent().apply {
        answer =
          answerQuantity.apply {
            value = BigDecimal(20)
            code = "h"
          }
        operator = Questionnaire.QuestionnaireItemOperator.GREATER_THAN
      }
    val answer =
      Quantity().apply {
        value = BigDecimal(30)
        code = "h"
      }

    assertThat(enableWhen.greaterOrEqual(answer)).isFalse()
  }

  @Test
  fun greaterOrEqual_differentQuantityUnits_shouldReturnFalse() {
    val enableWhen =
      Questionnaire.QuestionnaireItemEnableWhenComponent().apply {
        answer =
          answerQuantity.apply {
            value = BigDecimal(20)
            code = "h"
          }
      }
    val answer =
      Quantity().apply {
        value = BigDecimal(10)
        code = "kg"
      }

    assertThat(enableWhen.greaterOrEqual(answer)).isFalse()
  }

  @Test
  fun lessThan_noAnswerLessThanEnableAnswer_shouldReturnTrue() {
    val enableWhen =
      Questionnaire.QuestionnaireItemEnableWhenComponent().apply {
        answer = IntegerType().apply { value = 40 }
      }
    val answer = IntegerType().apply { value = 50 }

    assertThat(enableWhen.lessThan(answer)).isTrue()
  }

  @Test
  fun lessThan_answerLessThanEnableAnswer_shouldReturnFalse() {
    val enableWhen =
      Questionnaire.QuestionnaireItemEnableWhenComponent().apply {
        answer = IntegerType().apply { value = 50 }
      }
    val answer = IntegerType().apply { value = 10 }

    assertThat(enableWhen.lessThan(answer)).isFalse()
  }

  @Test
  fun lessThan_answerEqualsEnableAnswer_shouldReturnTrue() {
    val enableWhen =
      Questionnaire.QuestionnaireItemEnableWhenComponent().apply {
        answer = IntegerType().apply { value = 50 }
      }
    val answer = IntegerType().apply { value = 50 }

    assertThat(enableWhen.lessThan(answer)).isTrue()
  }

  @Test
  fun lessThan_nonPrimitive_shouldReturnFalse() {
    val enableWhen =
      Questionnaire.QuestionnaireItemEnableWhenComponent().apply { answer = Coding() }
    val answer = Coding()

    assertThat(enableWhen.lessThan(answer)).isFalse()
  }

  @Test
  fun lessThan_noAnswerQuantityLessThanEnableAnswerQuantity_shouldReturnTrue() {
    val enableWhen =
      Questionnaire.QuestionnaireItemEnableWhenComponent().apply {
        answer =
          answerQuantity.apply {
            value = BigDecimal(20)
            code = "h"
          }
      }
    val answer =
      Quantity().apply {
        value = BigDecimal(30)
        code = "h"
      }

    assertThat(enableWhen.lessThan(answer)).isTrue()
  }

  @Test
  fun lessThan_differentQuantityUnits_shouldReturnFalse() {
    val enableWhen =
      Questionnaire.QuestionnaireItemEnableWhenComponent().apply {
        answer =
          answerQuantity.apply {
            value = BigDecimal(20)
            code = "h"
          }
      }
    val answer =
      Quantity().apply {
        value = BigDecimal(30)
        code = "kg"
      }

    assertThat(enableWhen.lessThan(answer)).isFalse()
  }

  @Test
  fun lessOrEqual_noAnswerLessOrEqualEnableAnswer_shouldReturnTrue() {
    val enableWhen =
      Questionnaire.QuestionnaireItemEnableWhenComponent().apply {
        answer = IntegerType().apply { value = 40 }
      }
    val answer = IntegerType().apply { value = 50 }

    assertThat(enableWhen.lessThan(answer)).isTrue()
  }

  @Test
  fun lessOrEqual_answerLessOrEqualEnableAnswer_shouldReturnFalse() {
    val enableWhen =
      Questionnaire.QuestionnaireItemEnableWhenComponent().apply {
        answer = IntegerType().apply { value = 50 }
      }
    val answer = IntegerType().apply { value = 10 }

    assertThat(enableWhen.lessOrEqual(answer)).isFalse()
  }

  @Test
  fun lessOrEqual_answerEqualEnableAnswer_shouldReturnFalse() {
    val enableWhen =
      Questionnaire.QuestionnaireItemEnableWhenComponent().apply {
        answer = IntegerType().apply { value = 50 }
      }
    val answer = IntegerType().apply { value = 50 }

    assertThat(enableWhen.lessOrEqual(answer)).isFalse()
  }

  @Test
  fun lessOrEqual_nonPrimitive_shouldReturnFalse() {
    val enableWhen =
      Questionnaire.QuestionnaireItemEnableWhenComponent().apply { answer = Coding() }
    val answer = Coding()

    assertThat(enableWhen.lessOrEqual(answer)).isFalse()
  }

  @Test
  fun lessOrEqual_noAnswerQuantityLessThanOrEqualEnableAnswerQuantity_shouldReturnTrue() {
    val enableWhen =
      Questionnaire.QuestionnaireItemEnableWhenComponent().apply {
        answer =
          answerQuantity.apply {
            value = BigDecimal(20)
            code = "h"
          }
      }
    val answer =
      Quantity().apply {
        value = BigDecimal(30)
        code = "h"
      }

    assertThat(enableWhen.lessOrEqual(answer)).isTrue()
  }

  @Test
  fun lessOrEqual_differentQuantityUnits_shouldReturnFalse() {
    val enableWhen =
      Questionnaire.QuestionnaireItemEnableWhenComponent().apply {
        answer =
          answerQuantity.apply {
            value = BigDecimal(20)
            code = "h"
          }
      }
    val answer =
      Quantity().apply {
        value = BigDecimal(30)
        code = "kg"
      }

    assertThat(enableWhen.lessOrEqual(answer)).isFalse()
  }
}
