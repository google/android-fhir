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
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.common.truth.Truth
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemBooleanTypePickerViewHolderFactoryInstrumentedTest {
  private val parent = FrameLayout(InstrumentationRegistry.getInstrumentation().context)
  private val viewHolder = QuestionnaireItemBooleanTypePickerViewHolderFactory.create(parent)

  @Test
  fun shouldShowPrefixText() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { prefix = "Prefix?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    Truth.assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix).isVisible).isTrue()
    Truth.assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix).text)
      .isEqualTo("Prefix?")
  }

  @Test
  fun shouldHidePrefixText() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { prefix = "" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    Truth.assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix).isVisible).isFalse()
  }

  @Test
  fun bind_shouldSetHeaderText() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          text = "Question?"
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    Truth.assertThat(viewHolder.itemView.findViewById<TextView>(R.id.bool_header).text)
      .isEqualTo("Question?")
  }

  @Test
  fun bind_shouldCreateRadioButtonsForBooleanType() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    Truth.assertThat(viewHolder.itemView.findViewById<TextView>(R.id.bool_header).text)
      .isEqualTo("Question?")
    Truth.assertThat(
        viewHolder.itemView.findViewById<RadioButton>(R.id.boolean_type_yes).isClickable
      )
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
    viewHolder.itemView.findViewById<RadioButton>(R.id.boolean_type_yes).performClick()

    val answer = questionnaireItemViewItem.questionnaireResponseItem.answer
    Truth.assertThat(answer[0].valueBooleanType.value).isTrue()
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
    viewHolder.itemView.findViewById<RadioButton>(R.id.boolean_type_no).performClick()

    val answer = questionnaireItemViewItem.questionnaireResponseItem.answer
    Truth.assertThat(answer[0].valueBooleanType.value).isFalse()
  }

  @Test
  @UiThreadTest
  fun doubleClickYes_shouldClearAnswer() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    viewHolder.bind(questionnaireItemViewItem)
    viewHolder.itemView.findViewById<RadioButton>(R.id.boolean_type_yes).performClick()
    viewHolder.itemView.findViewById<RadioButton>(R.id.boolean_type_yes).performClick()

    val answer = questionnaireItemViewItem.questionnaireResponseItem.answer
    Truth.assertThat(answer.isEmpty())
  }

  @Test
  @UiThreadTest
  fun doubleClickNo_shouldClearAnswer() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    viewHolder.bind(questionnaireItemViewItem)
    viewHolder.itemView.findViewById<RadioButton>(R.id.boolean_type_no).performClick()
    viewHolder.itemView.findViewById<RadioButton>(R.id.boolean_type_no).performClick()

    val answer = questionnaireItemViewItem.questionnaireResponseItem.answer
    Truth.assertThat(answer.isEmpty())
  }
}
