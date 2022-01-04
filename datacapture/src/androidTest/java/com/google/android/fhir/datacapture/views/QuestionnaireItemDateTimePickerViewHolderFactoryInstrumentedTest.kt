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

import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.isVisible
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.android.material.textfield.TextInputLayout
import com.google.common.truth.Truth.assertThat
import java.util.Date
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemDateTimePickerViewHolderFactoryInstrumentedTest {
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
    assertThat(parent).isNotNull()
    viewHolder = QuestionnaireItemDateTimePickerViewHolderFactory.create(parent)
  }

  @Test
  fun shouldShowPrefixText() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { prefix = "Prefix?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix_text_view).isVisible).isTrue()
    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix_text_view).text)
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

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix_text_view).isVisible)
      .isFalse()
  }

  @Test
  fun shouldSetTextInputLayoutHint() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question_text_view).text)
      .isEqualTo("Question?")
  }

  @Test
  @UiThreadTest
  fun shouldSetEmptyDateTimeInput() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.date_input_edit_text).text.toString()
      )
      .isEqualTo("")
    assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.time_input_edit_text).text.toString()
      )
      .isEqualTo("")
  }

  @Test
  @UiThreadTest
  fun shouldSetDateTimeInput() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0)))
          )
      ) {}
    )

    assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.date_input_edit_text).text.toString()
      )
      .isEqualTo("2020-02-05")
    assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.time_input_edit_text).text.toString()
      )
      .isEqualTo("01:30:00")
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

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.date_input_layout).error)
      .isEqualTo("Missing answer for required field.")
    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.time_input_layout).error)
      .isEqualTo("Missing answer for required field.")
  }

  @Test
  @UiThreadTest
  fun displayValidationResult_noError_shouldShowNoErrorMessage() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/minValue"
            setValue((DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0))))
          }
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/maxValue"
            setValue((DateTimeType(Date(2025 - 1900, 1, 5, 1, 30, 0))))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = (DateTimeType(Date(2023 - 1900, 1, 5, 1, 30, 0)))
            }
          )
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.date_input_layout).error)
      .isNull()
    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.time_input_layout).error)
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

    assertThat(viewHolder.itemView.findViewById<EditText>(R.id.date_input_edit_text).isEnabled)
      .isFalse()
    assertThat(viewHolder.itemView.findViewById<EditText>(R.id.time_input_edit_text).isEnabled)
      .isFalse()
  }
}
