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

import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.isVisible
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemBooleanTypePickerViewHolderFactoryInstrumentedTest {
  private val parent =
    FrameLayout(
      ContextThemeWrapper(
        InstrumentationRegistry.getInstrumentation().targetContext,
        R.style.Theme_MaterialComponents
      )
    )
  private val viewHolder = QuestionnaireItemBooleanTypePickerViewHolderFactory.create(parent)

  @Test
  fun shouldShowPrefixText() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { prefix = "Prefix?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix).isVisible).isTrue()
    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix).text).isEqualTo("Prefix?")
  }

  @Test
  fun shouldHidePrefixText() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { prefix = "" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix).isVisible).isFalse()
  }

  @Test
  fun bind_shouldSetQuestionText() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          text = "Question?"
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question_text_view).text)
      .isEqualTo("Question?")
  }

  @Test
  @UiThreadTest
  fun noAnswer_shouldSetAnswerEmpty() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    viewHolder.bind(questionnaireItemViewItem)

    assertThat(questionnaireItemViewItem.questionnaireResponseItem.answer.isEmpty())
  }

  @Test
  @UiThreadTest
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
  @UiThreadTest
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
  @UiThreadTest
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
  @UiThreadTest
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
  @UiThreadTest
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
  @UiThreadTest
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
  @UiThreadTest
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
  @UiThreadTest
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

    assertThat(questionnaireItemViewItem.questionnaireResponseItem.answer.isEmpty())
  }

  @Test
  @UiThreadTest
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
  @UiThreadTest
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

    assertThat(questionnaireItemViewItem.questionnaireResponseItem.answer.isEmpty())
  }

  @Test
  @UiThreadTest
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
  @UiThreadTest
  fun displayValidationResult_error_shouldShowErrorMessage() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.error_text_view).text)
      .isEqualTo("Missing answer for required field.")
  }

  @Test
  @UiThreadTest
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

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.error_text_view).text.isEmpty())
      .isTrue()
  }

  @Test
  @UiThreadTest
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
        (viewHolder.itemView.findViewById<RadioGroup>(R.id.radio_group_main).getChildAt(0) as
            RadioButton)
          .isEnabled
      )
      .isFalse()
  }
}
