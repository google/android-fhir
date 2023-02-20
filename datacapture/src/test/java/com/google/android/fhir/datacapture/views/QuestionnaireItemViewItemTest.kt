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

package com.google.android.fhir.datacapture.views

import android.app.Application
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.common.truth.Truth.assertThat
import kotlin.test.assertFailsWith
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class QuestionnaireItemViewItemTest {
  private val context = ApplicationProvider.getApplicationContext<Application>()

  @Test
  fun `addAnswer() should throw exception if question does not allow repeated answers`() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { linkId = "a-question" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(BooleanType(true))
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

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
  fun `addAnswer() should add answer to QuestionnaireResponseItem`() {
    var answers = listOf<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>()
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
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result, _ -> answers = result },
      )

    questionnaireItemViewItem.addAnswer(
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(BooleanType(true))
    )

    assertThat(answers).hasSize(2)
  }

  @Test
  fun `removeAnswer() should throw exception if question does not allow repeated answers`() {
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
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

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
  fun `removeAnswer() should remove answer from QuestionnaireResponseItem`() {
    var answers = listOf<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>()
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
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result, _ -> answers = result },
      )

    questionnaireItemViewItem.removeAnswer(
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(BooleanType(false))
    )

    assertThat(answers).hasSize(1)
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
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

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
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    assertThat(
        questionnaireItemViewItem.isAnswerOptionSelected(
          Questionnaire.QuestionnaireItemAnswerOptionComponent()
            .setValue(Coding("sample-system", "sample-code2", "Sample Code2"))
        )
      )
      .isFalse()
  }

  @Test
  fun `hasTheSameItem() should return false if questionnaire items are different`() {
    val questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()

    assertThat(
        QuestionnaireItemViewItem(
            Questionnaire.QuestionnaireItemComponent(),
            questionnaireResponseItem,
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> }
          )
          .hasTheSameItem(
            QuestionnaireItemViewItem(
              Questionnaire.QuestionnaireItemComponent(),
              questionnaireResponseItem,
              validationResult = NotValidated,
              answersChangedCallback = { _, _, _, _ -> }
            )
          )
      )
      .isFalse()
  }

  @Test
  fun `hasTheSameItem() should return false if questionnaire response items are different`() {
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent()

    assertThat(
        QuestionnaireItemViewItem(
            questionnaireItem,
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> }
          )
          .hasTheSameItem(
            QuestionnaireItemViewItem(
              questionnaireItem,
              QuestionnaireResponse.QuestionnaireResponseItemComponent(),
              validationResult = NotValidated,
              answersChangedCallback = { _, _, _, _ -> }
            )
          )
      )
      .isFalse()
  }

  @Test
  fun `hasTheSameItem() should return true if questionnaire items and questionnaire response items are the same`() {
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent()
    val questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()

    assertThat(
        QuestionnaireItemViewItem(
            questionnaireItem,
            questionnaireResponseItem,
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> }
          )
          .hasTheSameItem(
            QuestionnaireItemViewItem(
              questionnaireItem,
              questionnaireResponseItem,
              validationResult = NotValidated,
              answersChangedCallback = { _, _, _, _ -> }
            )
          )
      )
      .isTrue()
  }

  @Test
  fun `hasTheSameAnswer() should return false for different answer list sizes`() {
    assertThat(
        QuestionnaireItemViewItem(
            Questionnaire.QuestionnaireItemComponent(),
            QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
              addAnswer(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  value = BooleanType(true)
                }
              )
            },
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> }
          )
          .hasTheSameAnswer(
            QuestionnaireItemViewItem(
              Questionnaire.QuestionnaireItemComponent(),
              QuestionnaireResponse.QuestionnaireResponseItemComponent(),
              validationResult = NotValidated,
              answersChangedCallback = { _, _, _, _ -> }
            )
          )
      )
      .isFalse()
  }

  @Test
  fun `hasTheSameAnswer() should return true for two empty answer lists`() {
    assertThat(
        QuestionnaireItemViewItem(
            Questionnaire.QuestionnaireItemComponent(),
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> }
          )
          .hasTheSameAnswer(
            QuestionnaireItemViewItem(
              Questionnaire.QuestionnaireItemComponent(),
              QuestionnaireResponse.QuestionnaireResponseItemComponent(),
              validationResult = NotValidated,
              answersChangedCallback = { _, _, _, _ -> }
            )
          )
      )
      .isTrue()
  }

  @Test
  fun `hasTheSameAnswer() should return false for different answers`() {
    assertThat(
        QuestionnaireItemViewItem(
            Questionnaire.QuestionnaireItemComponent(),
            QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
              addAnswer(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  value = BooleanType(true)
                }
              )
            },
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> }
          )
          .hasTheSameAnswer(
            QuestionnaireItemViewItem(
              Questionnaire.QuestionnaireItemComponent(),
              QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                addAnswer(
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                    value = BooleanType(false)
                  }
                )
              },
              validationResult = NotValidated,
              answersChangedCallback = { _, _, _, _ -> }
            )
          )
      )
      .isFalse()
  }

  @Test
  fun `hasTheSameAnswer() should return false for null and non-null answers`() {
    assertThat(
        QuestionnaireItemViewItem(
            Questionnaire.QuestionnaireItemComponent(),
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> }
          )
          .apply { setAnswer(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()) }
          .hasTheSameAnswer(
            QuestionnaireItemViewItem(
              Questionnaire.QuestionnaireItemComponent(),
              QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                addAnswer(
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                    value = BooleanType(false)
                  }
                )
              },
              validationResult = NotValidated,
              answersChangedCallback = { _, _, _, _ -> }
            )
          )
      )
      .isFalse()
  }

  @Test
  fun `hasTheSameAnswer() should return false for non-null and null answers`() {
    assertThat(
        QuestionnaireItemViewItem(
            Questionnaire.QuestionnaireItemComponent(),
            QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
              addAnswer(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  value = BooleanType(true)
                }
              )
            },
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> }
          )
          .hasTheSameAnswer(
            QuestionnaireItemViewItem(
                Questionnaire.QuestionnaireItemComponent(),
                QuestionnaireResponse.QuestionnaireResponseItemComponent(),
                validationResult = NotValidated,
                answersChangedCallback = { _, _, _, _ -> }
              )
              .apply { setAnswer(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()) }
          )
      )
      .isFalse()
  }

  @Test
  fun `hasTheSameAnswer() should return true for the same answers`() {
    assertThat(
        QuestionnaireItemViewItem(
            Questionnaire.QuestionnaireItemComponent(),
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> }
          )
          .apply {
            setAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(true)
              }
            )
          }
          .hasTheSameAnswer(
            QuestionnaireItemViewItem(
                Questionnaire.QuestionnaireItemComponent(),
                QuestionnaireResponse.QuestionnaireResponseItemComponent(),
                validationResult = NotValidated,
                answersChangedCallback = { _, _, _, _ -> }
              )
              .apply {
                setAnswer(
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                    value = BooleanType(true)
                  }
                )
              }
          )
      )
      .isTrue()
  }

  @Test
  fun `hasTheSameValidationResult() should return true for null validation results`() {
    assertThat(
        QuestionnaireItemViewItem(
            Questionnaire.QuestionnaireItemComponent(),
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> }
          )
          .hasTheSameValidationResult(
            QuestionnaireItemViewItem(
              Questionnaire.QuestionnaireItemComponent(),
              QuestionnaireResponse.QuestionnaireResponseItemComponent(),
              validationResult = NotValidated,
              answersChangedCallback = { _, _, _, _ -> }
            )
          )
      )
      .isTrue()
  }

  @Test
  fun `hasTheSameValidationResult() should return true for null and valid validation results`() {
    assertThat(
        QuestionnaireItemViewItem(
            Questionnaire.QuestionnaireItemComponent(),
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> }
          )
          .hasTheSameValidationResult(
            QuestionnaireItemViewItem(
              Questionnaire.QuestionnaireItemComponent(),
              QuestionnaireResponse.QuestionnaireResponseItemComponent(),
              validationResult = Valid,
              answersChangedCallback = { _, _, _, _ -> }
            )
          )
      )
      .isTrue()
  }

  @Test
  fun `hasTheSameValidationResult() should return false for valid and null validation results`() {
    assertThat(
        QuestionnaireItemViewItem(
            Questionnaire.QuestionnaireItemComponent(),
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = Valid,
            answersChangedCallback = { _, _, _, _ -> }
          )
          .hasTheSameValidationResult(
            QuestionnaireItemViewItem(
              Questionnaire.QuestionnaireItemComponent(),
              QuestionnaireResponse.QuestionnaireResponseItemComponent(),
              validationResult = NotValidated,
              answersChangedCallback = { _, _, _, _ -> }
            )
          )
      )
      .isTrue()
  }

  @Test
  fun `hasTheSameValidationResult() should return true for two valid validation results`() {
    assertThat(
        QuestionnaireItemViewItem(
            Questionnaire.QuestionnaireItemComponent(),
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = Valid,
            answersChangedCallback = { _, _, _, _ -> }
          )
          .hasTheSameValidationResult(
            QuestionnaireItemViewItem(
              Questionnaire.QuestionnaireItemComponent(),
              QuestionnaireResponse.QuestionnaireResponseItemComponent(),
              validationResult = Valid,
              answersChangedCallback = { _, _, _, _ -> }
            )
          )
      )
      .isTrue()
  }

  @Test
  fun `hasTheSameValidationResult() should return false for different validation results`() {
    assertThat(
        QuestionnaireItemViewItem(
            Questionnaire.QuestionnaireItemComponent(),
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = Valid,
            answersChangedCallback = { _, _, _, _ -> }
          )
          .hasTheSameValidationResult(
            QuestionnaireItemViewItem(
              Questionnaire.QuestionnaireItemComponent(),
              QuestionnaireResponse.QuestionnaireResponseItemComponent(),
              validationResult = Invalid(listOf("error")),
              answersChangedCallback = { _, _, _, _ -> }
            )
          )
      )
      .isFalse()
  }

  @Test
  fun `hasTheSameValidationResult() should return false for validation results with different messages`() {
    assertThat(
        QuestionnaireItemViewItem(
            Questionnaire.QuestionnaireItemComponent(),
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = Invalid(listOf("error 1")),
            answersChangedCallback = { _, _, _, _ -> }
          )
          .hasTheSameValidationResult(
            QuestionnaireItemViewItem(
              Questionnaire.QuestionnaireItemComponent(),
              QuestionnaireResponse.QuestionnaireResponseItemComponent(),
              validationResult = Invalid(listOf("error 2")),
              answersChangedCallback = { _, _, _, _ -> }
            )
          )
      )
      .isFalse()
  }

  @Test
  fun `hasTheSameValidationResult() should return true for same validation results`() {
    assertThat(
        QuestionnaireItemViewItem(
            Questionnaire.QuestionnaireItemComponent(),
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = Invalid(listOf("error")),
            answersChangedCallback = { _, _, _, _ -> }
          )
          .hasTheSameValidationResult(
            QuestionnaireItemViewItem(
              Questionnaire.QuestionnaireItemComponent(),
              QuestionnaireResponse.QuestionnaireResponseItemComponent(),
              validationResult = Invalid(listOf("error")),
              answersChangedCallback = { _, _, _, _ -> }
            )
          )
      )
      .isTrue()
  }

  @Test
  fun `answerString() should return not answered with no answer`() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Valid,
        answersChangedCallback = { _, _, _, _ -> },
      )
    assertThat(questionnaireItemViewItem.answerString(context))
      .isEqualTo(context.getString(R.string.not_answered))
  }

  @Test
  fun `answerString() should return answer value with single answer`() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(StringType("Answer"))
          ),
        validationResult = Valid,
        answersChangedCallback = { _, _, _, _ -> },
      )
    assertThat(questionnaireItemViewItem.answerString(context)).isEqualTo("Answer")
  }

  @Test
  fun `answerString() should return comma separated answer value with multiple answers`() {
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
          ),
        validationResult = Valid,
        answersChangedCallback = { _, _, _, _ -> },
      )
    assertThat(questionnaireItemViewItem.answerString(context)).isEqualTo("Answer1, Answer2")
  }

  @Test
  fun `update partial answer`() {
    var answers = listOf<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>()
    var partialAnswer: Any? = null
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { linkId = "a-question" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result, partialValue ->
          answers = result
          partialAnswer = partialValue
        },
      )

    questionnaireItemViewItem.updatePartialAnswer("02/02")

    assertThat(partialAnswer).isEqualTo("02/02")
    assertThat(answers).isEmpty()
  }

  @Test
  fun `no partial answer for addAnswer`() {
    var answers = listOf<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>()
    var partialAnswer: Any? = null
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          linkId = "a-question"
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result, partialValue ->
          answers = result
          partialAnswer = partialValue
        },
      )

    questionnaireItemViewItem.addAnswer(
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(DateType(2023, 1, 2))
    )

    assertThat(partialAnswer).isNull()
    assertThat(answers).hasSize(1)
  }

  @Test
  fun `no partial answer for removeAnswer`() {
    var partialAnswer: Any? = null
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          linkId = "a-question"
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result, partialValue -> partialAnswer = partialValue },
      )

    questionnaireItemViewItem.removeAnswer(
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(DateType(2023, 1, 2))
    )

    assertThat(partialAnswer).isNull()
  }

  @Test
  fun `no partial answer for setAnswer`() {
    var partialAnswer: Any? = null
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          linkId = "a-question"
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result, partialValue -> partialAnswer = partialValue },
      )

    questionnaireItemViewItem.setAnswer(
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(DateType(2023, 1, 2))
    )

    assertThat(partialAnswer).isNull()
  }
}
