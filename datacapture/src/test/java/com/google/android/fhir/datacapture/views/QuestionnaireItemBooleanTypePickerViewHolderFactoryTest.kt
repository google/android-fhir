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

import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class QuestionnaireItemBooleanTypePickerViewHolderFactoryTest {
  private val parent =
    FrameLayout(
      RuntimeEnvironment.getApplication().apply { setTheme(R.style.Theme_MaterialComponents) }
    )
  private val viewHolder = QuestionnaireItemBooleanTypePickerViewHolderFactory.create(parent)

  @Test
  fun bind_shouldSetQuestionHeader() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          text = "Question?"
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question?")
  }

  @Test
  fun noAnswer_shouldSetAnswerEmpty() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    viewHolder.bind(questionnaireItemViewItem)

    assertThat(questionnaireItemViewItem.questionnaireResponseItem.answer).isEmpty()
  }

  @Test
  fun noAnswer_shouldNotCheckYesOrNoRadioButton() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<RadioButton>(R.id.yes_radio_button).isChecked)
      .isFalse()
    assertThat(viewHolder.itemView.findViewById<RadioButton>(R.id.no_radio_button).isChecked)
      .isFalse()
  }

  @Test
  fun answerTrue_shouldSetAnswerTrue() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = BooleanType(true)
            }
          )
      ) {}
    viewHolder.bind(questionnaireItemViewItem)

    assertThat(
        questionnaireItemViewItem.questionnaireResponseItem.answer.single().valueBooleanType.value
      )
      .isTrue()
  }

  @Test
  fun answerTrue_shouldCheckYesRadioButton() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = BooleanType(true)
            }
          )
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<RadioButton>(R.id.yes_radio_button).isChecked)
      .isTrue()
    assertThat(viewHolder.itemView.findViewById<RadioButton>(R.id.no_radio_button).isChecked)
      .isFalse()
  }

  @Test
  fun answerFalse_shouldSetAnswerFalse() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = BooleanType(false)
            }
          )
      ) {}
    viewHolder.bind(questionnaireItemViewItem)

    assertThat(
        questionnaireItemViewItem.questionnaireResponseItem.answer.single().valueBooleanType.value
      )
      .isFalse()
  }

  @Test
  fun answerFalse_shouldCheckNoRadioButton() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = BooleanType(false)
            }
          )
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<RadioButton>(R.id.yes_radio_button).isChecked)
      .isFalse()
    assertThat(viewHolder.itemView.findViewById<RadioButton>(R.id.no_radio_button).isChecked)
      .isTrue()
  }

  @Test
  fun click_shouldSetAnswerTrue() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    viewHolder.bind(questionnaireItemViewItem)
    viewHolder.itemView.findViewById<RadioButton>(R.id.yes_radio_button).performClick()

    assertThat(
        questionnaireItemViewItem.questionnaireResponseItem.answer.single().valueBooleanType.value
      )
      .isTrue()
  }

  @Test
  fun click_shouldSetAnswerFalse() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    viewHolder.bind(questionnaireItemViewItem)
    viewHolder.itemView.findViewById<RadioButton>(R.id.no_radio_button).performClick()

    assertThat(
        questionnaireItemViewItem.questionnaireResponseItem.answer.single().valueBooleanType.value
      )
      .isFalse()
  }

  @Test
  fun yesSelected_clickYes_shouldClearAnswer() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = BooleanType(true)
            }
          )
        }
      ) {}
    viewHolder.bind(questionnaireItemViewItem)
    viewHolder.itemView.findViewById<RadioButton>(R.id.yes_radio_button).performClick()

    assertThat(questionnaireItemViewItem.questionnaireResponseItem.answer).isEmpty()
  }

  @Test
  fun yesSelected_clickYes_shouldClearRadioButtons() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = BooleanType(true)
            }
          )
        }
      ) {}
    viewHolder.bind(questionnaireItemViewItem)
    viewHolder.itemView.findViewById<RadioButton>(R.id.yes_radio_button).performClick()

    assertThat(viewHolder.itemView.findViewById<RadioButton>(R.id.yes_radio_button).isChecked)
      .isFalse()
    assertThat(viewHolder.itemView.findViewById<RadioButton>(R.id.no_radio_button).isChecked)
      .isFalse()
  }

  @Test
  fun noSelected_clickNo_shouldClearAnswer() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = BooleanType(false)
            }
          )
        }
      ) {}
    viewHolder.bind(questionnaireItemViewItem)
    viewHolder.itemView.findViewById<RadioButton>(R.id.no_radio_button).performClick()

    assertThat(questionnaireItemViewItem.questionnaireResponseItem.answer).isEmpty()
  }

  @Test
  fun noSelected_clickNo_shouldClearRadioButtons() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = BooleanType(false)
            }
          )
        }
      ) {}
    viewHolder.bind(questionnaireItemViewItem)
    viewHolder.itemView.findViewById<RadioButton>(R.id.no_radio_button).performClick()

    assertThat(viewHolder.itemView.findViewById<RadioButton>(R.id.yes_radio_button).isChecked)
      .isFalse()
    assertThat(viewHolder.itemView.findViewById<RadioButton>(R.id.no_radio_button).isChecked)
      .isFalse()
  }

  @Test
  fun displayValidationResult_error_shouldShowErrorMessage() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        modified = true
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.error).text)
      .isEqualTo("Missing answer for required field.")
  }

  @Test
  fun displayValidationResult_noError_shouldShowNoErrorMessage() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = BooleanType(true)
            }
          )
        }
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.error).text).isEqualTo("")
  }

  @Test
  fun bind_readOnly_shouldDisableView() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          text = "Question?"
          readOnly = true
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = BooleanType(true)
            }
          )
        }
      ) {}
    )

    assertThat(
        (viewHolder.itemView.findViewById<RadioGroup>(R.id.radio_group).getChildAt(0) as
            RadioButton)
          .isEnabled
      )
      .isFalse()
  }
}
