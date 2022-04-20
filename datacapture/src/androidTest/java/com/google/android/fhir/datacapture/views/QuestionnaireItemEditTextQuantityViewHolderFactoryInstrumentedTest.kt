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
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemEditTextQuantityViewHolderFactoryInstrumentedTest {

  private lateinit var context: ContextThemeWrapper
  private lateinit var parent: FrameLayout
  private lateinit var viewHolder: QuestionnaireItemViewHolder

  @Before
  fun setUp() {
    context =
      ContextThemeWrapper(
        InstrumentationRegistry.getInstrumentation().targetContext,
        R.style.Theme_MaterialComponents
      )
    parent = FrameLayout(context)
    viewHolder = QuestionnaireItemEditTextQuantityViewHolderFactory.create(parent)
  }

  @Test
  @UiThreadTest
  fun shouldSetQuestionHeader() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question?")
  }

  @Test
  @UiThreadTest
  fun shouldSetInputText() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = Quantity().apply { value = BigDecimal("5") }
            }
          )
        }
      ) {}
    )

    assertThat(
        viewHolder
          .itemView
          .findViewById<TextInputEditText>(R.id.text_input_edit_text)
          .text
          .toString()
      )
      .isEqualTo("5")
  }

  @Test
  @UiThreadTest
  fun shouldSetInputTextToEmpty() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = Quantity().apply { value = BigDecimal("5") }
            }
          )
        }
      ) {}
    )
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = Quantity()
            }
          )
      ) {}
    )

    assertThat(
        viewHolder
          .itemView
          .findViewById<TextInputEditText>(R.id.text_input_edit_text)
          .text
          .toString()
      )
      .isEqualTo("")
  }

  @Test
  @UiThreadTest
  fun shouldSetQuestionnaireResponseItemAnswer() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    viewHolder.bind(questionnaireItemViewItem)
    viewHolder.itemView.findViewById<TextInputEditText>(R.id.text_input_edit_text).setText("10")

    val answer = questionnaireItemViewItem.questionnaireResponseItem.answer
    assertThat(answer.size).isEqualTo(1)
    assertThat(answer[0].valueQuantity!!.value!!.toString()).isEqualTo("10.0")
  }

  @Test
  @UiThreadTest
  fun shouldSetQuestionnaireResponseItemAnswerOneDecimalPlace() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    viewHolder.bind(questionnaireItemViewItem)
    viewHolder.itemView.findViewById<TextInputEditText>(R.id.text_input_edit_text).setText("10.1")

    val answer = questionnaireItemViewItem.questionnaireResponseItem.answer
    assertThat(answer.size).isEqualTo(1)
    assertThat(answer[0].valueQuantity!!.value.toString()).isEqualTo("10.1")
  }

  @Test
  @UiThreadTest
  fun shouldSetQuestionnaireResponseItemAnswerToEmpty() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    viewHolder.bind(questionnaireItemViewItem)
    viewHolder.itemView.findViewById<TextInputEditText>(R.id.text_input_edit_text).setText("")

    assertThat(questionnaireItemViewItem.questionnaireResponseItem.answer.size).isEqualTo(0)
  }

  @Test
  @UiThreadTest
  fun displayValidationResult_noError_shouldShowNoErrorMessage() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = Quantity(22.5)
            }
          )
        }
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.text_input_layout).error)
      .isNull()
  }

  @Test
  @UiThreadTest
  fun bind_readOnly_shouldDisableView() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { readOnly = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(
        viewHolder.itemView.findViewById<TextInputEditText>(R.id.text_input_edit_text).isEnabled
      )
      .isFalse()
  }
}
