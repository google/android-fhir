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

import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.TextView
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
class QuestionnaireItemCheckBoxViewHolderFactoryInstrumentedTest {
  private val parent = FrameLayout(InstrumentationRegistry.getInstrumentation().context)
  private val viewHolder = QuestionnaireItemCheckBoxViewHolderFactory.create(parent)

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
  fun shouldSetCheckBoxText() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<CheckBox>(R.id.check_box).text)
      .isEqualTo("Question?")
  }

  @Test
  fun noAnswer_shouldSetCheckBoxUnchecked() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<CheckBox>(R.id.check_box).isChecked).isFalse()
  }

  @Test
  @UiThreadTest
  fun answerTrue_shouldSetCheckBoxChecked() {
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

    assertThat(viewHolder.itemView.findViewById<CheckBox>(R.id.check_box).isChecked).isTrue()
  }

  @Test
  fun answerFalse_shouldSetCheckBoxUnchecked() {
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

    assertThat(viewHolder.itemView.findViewById<CheckBox>(R.id.check_box).isChecked).isFalse()
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
    viewHolder.itemView.findViewById<CheckBox>(R.id.check_box).performClick()

    val answer = questionnaireItemViewItem.questionnaireResponseItem.answer
    assertThat(answer.size).isEqualTo(1)
    assertThat(answer[0].valueBooleanType.value).isTrue()
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

    val checkBox = viewHolder.itemView.findViewById<CheckBox>(R.id.check_box)
    checkBox.performClick()
    checkBox.performClick()

    val answer = questionnaireItemViewItem.questionnaireResponseItem.answer
    assertThat(answer.size).isEqualTo(1)
    assertThat(answer[0].valueBooleanType.value).isFalse()
  }

  @Test
  @UiThreadTest
  fun click_shouldSetCheckBoxUnchecked() {
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
    val checkBox = viewHolder.itemView.findViewById<CheckBox>(R.id.check_box)
    checkBox.performClick()
    assertThat(checkBox.isChecked).isFalse()
  }

  @Test
  @UiThreadTest
  fun click_shouldSetCheckBoxChecked() {
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
    val checkBox = viewHolder.itemView.findViewById<CheckBox>(R.id.check_box)
    checkBox.performClick()
    assertThat(checkBox.isChecked).isTrue()
  }
}
