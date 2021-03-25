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
import androidx.core.view.isVisible
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemRadioGroupViewHolderFactoryInstrumentedTest {
  private val parent = FrameLayout(InstrumentationRegistry.getInstrumentation().context)
  private val viewHolder = QuestionnaireItemRadioGroupViewHolderFactory.create(parent)

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
  fun bind_shouldSetHeaderText() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.radio_header).text)
      .isEqualTo("Question?")
  }

  @Test
  fun bind_shouldCreateRadioButtons() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding 1" }
            }
          )
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding 2" }
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    val radioGroup = viewHolder.itemView.findViewById<RadioGroup>(R.id.radio_group)
    assertThat(radioGroup.childCount).isEqualTo(2)
    val radioButton1 = radioGroup.getChildAt(0) as RadioButton
    assertThat(radioButton1.text).isEqualTo("Coding 1")
    val radioButton2 = radioGroup.getChildAt(1) as RadioButton
    assertThat(radioButton2.text).isEqualTo("Coding 2")
  }

  @Test
  fun bind_noAnswer_shouldLeaveRadioButtonsUnchecked() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding 1" }
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    val radioButton =
      viewHolder.itemView.findViewById<RadioGroup>(R.id.radio_group).getChildAt(0) as RadioButton
    assertThat(radioButton.isChecked).isFalse()
  }

  @Test
  @UiThreadTest
  fun bind_answer_shouldCheckRadioButton() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding 1" }
            }
          )
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding 2" }
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = Coding().apply { display = "Coding 1" }
            }
          )
        }
      ) {}
    )

    assertThat(
        (viewHolder.itemView.findViewById<RadioGroup>(R.id.radio_group).getChildAt(0) as
            RadioButton)
          .isChecked
      )
      .isTrue()
    assertThat(
        (viewHolder.itemView.findViewById<RadioGroup>(R.id.radio_group).getChildAt(1) as
            RadioButton)
          .isChecked
      )
      .isFalse()
  }

  @Test
  @UiThreadTest
  fun click_shouldSetQuestionnaireResponseItemAnswer() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding 1" }
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    viewHolder.bind(questionnaireItemViewItem)
    viewHolder.itemView.findViewById<RadioGroup>(R.id.radio_group).getChildAt(0).performClick()

    val answer = questionnaireItemViewItem.questionnaireResponseItem.answer
    assertThat(answer.size).isEqualTo(1)
    assertThat(answer[0].valueCoding.display).isEqualTo("Coding 1")
  }

  @Test
  @UiThreadTest
  fun click_shouldCheckRadioButton() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding 1" }
            }
          )
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding 2" }
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    viewHolder.bind(questionnaireItemViewItem)
    viewHolder.itemView.findViewById<RadioGroup>(R.id.radio_group).getChildAt(0).performClick()

    assertThat(
        (viewHolder.itemView.findViewById<RadioGroup>(R.id.radio_group).getChildAt(0) as
            RadioButton)
          .isChecked
      )
      .isTrue()
    assertThat(
        (viewHolder.itemView.findViewById<RadioGroup>(R.id.radio_group).getChildAt(1) as
            RadioButton)
          .isChecked
      )
      .isFalse()
  }
}
