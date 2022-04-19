/*
 * Copyright 2021 Google LLC
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
import com.google.android.fhir.datacapture.utilities.localizedString
import com.google.common.truth.Truth.assertThat
import java.time.LocalDate
import java.util.Date
import kotlin.test.assertFailsWith
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.model.UriType
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
        questionnaireItemViewItem.isAnswerOptionSelected(
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
        questionnaireItemViewItem.isAnswerOptionSelected(
          Questionnaire.QuestionnaireItemAnswerOptionComponent()
            .setValue(Coding("sample-system", "sample-code2", "Sample Code2"))
        )
      )
      .isFalse()
  }

  @Test
  fun answerString_noAnswer_shouldReturnNotAnswered() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    assertThat(questionnaireItemViewItem.answerString)
      .isEqualTo(QuestionnaireItemViewItem.NOT_ANSWERED)
  }

  @Test
  fun answerString_singleAnswer_shouldReturnSingleAnswer() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(StringType("Answer"))
          )
      ) {}
    assertThat(questionnaireItemViewItem.answerString).isEqualTo("Answer")
  }

  @Test
  fun answerString_multipleAnswer_shouldReturnAnswerSeparatedWithCommas() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(StringType("Answer1"))
          )
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(StringType("Answer2"))
          )
      ) {}
    assertThat(questionnaireItemViewItem.answerString).isEqualTo("Answer1, Answer2")
  }

  @Test
  fun answerString_booleanTypeTrueAnswer_shouldReturnAnswerAsYes() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(BooleanType(true))
          )
      ) {}
    assertThat(questionnaireItemViewItem.answerString).isEqualTo("Yes")
  }

  @Test
  fun answerString_booleanTypeFalseAnswer_shouldReturnAnswerAsNo() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(BooleanType(false))
          )
      ) {}
    assertThat(questionnaireItemViewItem.answerString).isEqualTo("No")
  }

  @Test
  fun answerString_booleanTypeNullAnswer_shouldReturnAnswerAsNotAnswered() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(BooleanType())
          )
      ) {}
    assertThat(questionnaireItemViewItem.answerString)
      .isEqualTo(QuestionnaireItemViewItem.NOT_ANSWERED)
  }

  @Test
  fun answerString_stringTypeAnswer_shouldReturnAnswer() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(StringType("StringAnswer"))
          )
      ) {}
    assertThat(questionnaireItemViewItem.answerString).isEqualTo("StringAnswer")
  }

  @Test
  fun answerString_integerTypeAnswer_shouldReturnAnswer() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(IntegerType(12))
          )
      ) {}
    assertThat(questionnaireItemViewItem.answerString).isEqualTo("12")
  }

  @Test
  fun answerString_decimalTypeAnswer_shouldReturnAnswer() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DecimalType(12.5612))
          )
      ) {}
    assertThat(questionnaireItemViewItem.answerString).isEqualTo("12.5612")
  }

  @Test
  fun answerString_dateTypeAnswer_shouldReturnAnswer() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateType(Date()))
          )
      ) {}
    assertThat(questionnaireItemViewItem.answerString).isEqualTo(LocalDate.now().localizedString)
  }

  @Test
  fun answerString_dateTimeTypeAnswer_shouldReturnAnswer() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date()))
          )
      ) {}
  }

  @Test
  fun answerString_timeTypeAnswer_shouldReturnAnswer() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(TimeType("03:00"))
          )
      ) {}
    assertThat(questionnaireItemViewItem.answerString).isEqualTo("03:00")
  }

  @Test
  fun answerString_quantityTypeAnswer_shouldReturnAnswer() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(Quantity(59.125))
          )
      ) {}
    assertThat(questionnaireItemViewItem.answerString).isEqualTo("59.125")
  }

  @Test
  fun answerString_uriTypeAnswer_shouldReturnAnswer() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(UriType("./a"))
          )
      ) {}
    assertThat(questionnaireItemViewItem.answerString).isEqualTo("./a")
  }

  @Test
  fun answerString_attachmentTypeAnswer_shouldReturnAnswer() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(Attachment().setUrl("http://photos.com/a.png"))
          )
      ) {}
    assertThat(questionnaireItemViewItem.answerString).isEqualTo("http://photos.com/a.png")
  }

  @Test
  fun answerString_codingTypeAnswer_shouldReturnAnswer() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(Coding("http:/a.b", "a", "A"))
          )
      ) {}
    assertThat(questionnaireItemViewItem.answerString).isEqualTo("A")
  }
}
