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

package com.google.android.fhir.datacapture.views

import android.os.Build
import com.google.common.truth.Truth.assertThat
import kotlin.test.assertFailsWith
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class QuestionnaireItemViewItemTest {
  @Test
  fun singleAnswerOrNull_noAnswer_shouldReturnNull() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    assertThat(questionnaireItemViewItem.singleAnswerOrNull).isNull()
  }

  @Test
  fun singleAnswerOrNull_singleAnswer_shouldReturnSingleAnswer() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(BooleanType(true))
          )
      ) {}
    assertThat(questionnaireItemViewItem.singleAnswerOrNull!!.valueBooleanType.value).isTrue()
  }

  @Test
  fun singleAnswerOrNull_multipleAnswers_shouldReturnNull() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(BooleanType(true))
          )
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(BooleanType(true))
          )
        }
      ) {}
    assertThat(questionnaireItemViewItem.singleAnswerOrNull).isNull()
  }

  @Test
  fun addAnswer_questionnaireItemDoesNotRepeat_shouldThrowIllegalArgument() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { linkId = "a-question" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(BooleanType(true))
          )
        }
      ) {}

    val errorMessage =
      assertFailsWith<IllegalStateException> {
          questionnaireItemViewItem.addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(BooleanType(true))
          )
        }
        .localizedMessage

    assertThat(errorMessage)
      .isEqualTo("Questionnaire item with linkId a-question does not allow repeated answers")
  }

  @Test
  fun addAnswer_questionnaireItemRepeats_shouldAddQuestionnaireResponseItemAnswerComponent() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          linkId = "a-question"
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(BooleanType(true))
          )
        }
      ) {}

    questionnaireItemViewItem.addAnswer(
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(BooleanType(true))
    )

    assertThat(questionnaireItemViewItem.questionnaireResponseItem.answer).hasSize(2)
  }

  @Test
  fun removeAnswer_questionnaireItemDoesNotRepeat_shouldThrowIllegalArgument() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { linkId = "a-question" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(BooleanType(true))
          )
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(BooleanType(true))
          )
        }
      ) {}

    val errorMessage =
      assertFailsWith<IllegalStateException> {
          questionnaireItemViewItem.removeAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(BooleanType(true))
          )
        }
        .localizedMessage

    assertThat(errorMessage)
      .isEqualTo("Questionnaire item with linkId a-question does not allow repeated answers")
  }

  @Test
  fun removeAnswer_questionnaireItemRepeats_shouldRemoveQuestionnaireResponseItemAnswerComponent() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          linkId = "a-question"
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(BooleanType(true))
          )
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(BooleanType(false))
          )
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(BooleanType(false))
          )
        }
      ) {}

    questionnaireItemViewItem.removeAnswer(
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(BooleanType(false))
    )

    assertThat(questionnaireItemViewItem.questionnaireResponseItem.answer.size).isEqualTo(1)
  }

  @Test
  fun hasAnswerOption_questionnaireItemRepeats_shouldReturnTrue() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          linkId = "a-question"
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent()
              .setValue(Coding("sample-system", "sample-code1", "Sample Code1"))
          )
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent()
              .setValue(Coding("sample-system", "sample-code2", "Sample Code2"))
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(Coding("sample-system", "sample-code2", "Sample Code2"))
          )
        }
      ) {}

    assertThat(
        questionnaireItemViewItem.hasAnswerOption(
          Questionnaire.QuestionnaireItemAnswerOptionComponent()
            .setValue(Coding("sample-system", "sample-code2", "Sample Code2"))
        )
      )
      .isTrue()
  }

  @Test
  fun hasAnswerOption_questionnaireItemRepeats_shouldReturnFalse() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          linkId = "a-question"
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent()
              .setValue(Coding("sample-system", "sample-code1", "Sample Code1"))
          )
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent()
              .setValue(Coding("sample-system", "sample-code2", "Sample Code2"))
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}

    assertThat(
        questionnaireItemViewItem.hasAnswerOption(
          Questionnaire.QuestionnaireItemAnswerOptionComponent()
            .setValue(Coding("sample-system", "sample-code2", "Sample Code2"))
        )
      )
      .isFalse()
  }
}
