/*
 * Copyright 2023 Google LLC
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

package com.google.android.fhir.datacapture.views.factories

import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.views.QuestionTextConfiguration
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class QuantityViewHolderFactoryTest {
  private val parent =
    FrameLayout(
      RuntimeEnvironment.getApplication().apply {
        setTheme(com.google.android.material.R.style.Theme_Material3_DayNight)
      },
    )
  private val viewHolder = QuantityViewHolderFactory.create(parent)

  @Test
  fun `should set question text`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question?")
  }

  @Test
  fun `should set input decimal value`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = Quantity().apply { value = BigDecimal("5") }
            },
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    assertThat(
        viewHolder.itemView
          .findViewById<TextInputEditText>(R.id.text_input_edit_text)
          .text
          .toString(),
      )
      .isEqualTo("5")
  }

  @Test
  fun `should clear input decimal value`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = Quantity().apply { value = BigDecimal("5") }
            },
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    assertThat(
        viewHolder.itemView
          .findViewById<TextInputEditText>(R.id.text_input_edit_text)
          .text
          .toString(),
      )
      .isEqualTo("")
  }

  @Test
  fun `should set unit value`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = Quantity().apply { unit = "kg" }
            },
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    assertThat(
        viewHolder.itemView
          .findViewById<AutoCompleteTextView>(R.id.unit_auto_complete)
          .text
          .toString(),
      )
      .isEqualTo("kg")
  }

  @Test
  fun `should clear unit value`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = Quantity().apply { unit = "kg" }
            },
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    assertThat(
        viewHolder.itemView
          .findViewById<AutoCompleteTextView>(R.id.unit_auto_complete)
          .text
          .toString(),
      )
      .isEqualTo("")
  }

  @Test
  fun `should display error message in validation result`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Invalid(listOf("Missing answer for required field.")),
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.text_input_layout).error)
      .isEqualTo("Missing answer for required field.")
  }

  @Test
  fun `should display no error message when validation result is valid`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = Quantity(22.5)
            },
          )
        },
        validationResult = Valid,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.text_input_layout).error)
      .isNull()
  }

  @Test
  fun `should disable text input in read-only mode`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { readOnly = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    assertThat(
        viewHolder.itemView.findViewById<TextInputEditText>(R.id.text_input_edit_text).isEnabled,
      )
      .isFalse()
  }

  @Test
  fun `should disable unit input in read-only mode`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { readOnly = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    assertThat(
        viewHolder.itemView.findViewById<AutoCompleteTextView>(R.id.unit_auto_complete).isEnabled,
      )
      .isFalse()
  }

  @Test
  fun `should always hide error textview in the header`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.error_text_at_header).visibility)
      .isEqualTo(View.GONE)
  }

  @Test
  fun `should show asterisk`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          text = "Question?"
          required = true
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = true),
      ),
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question? *")
  }

  @Test
  fun `should hide asterisk`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          text = "Question?"
          required = true
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = false),
      ),
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question?")
  }

  @Test
  fun `should show required text`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = true),
      ),
    )

    assertThat(
        viewHolder.itemView
          .findViewById<TextInputLayout>(R.id.text_input_layout)
          .helperText
          .toString(),
      )
      .isEqualTo("Required")
  }

  @Test
  fun `should hide required text`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = false),
      ),
    )

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.text_input_layout).helperText)
      .isNull()
  }

  @Test
  fun `should show optional text`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = true),
      ),
    )

    assertThat(
        viewHolder.itemView
          .findViewById<TextInputLayout>(R.id.text_input_layout)
          .helperText
          .toString(),
      )
      .isEqualTo("Optional")
  }

  @Test
  fun `should hide optional text`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = false),
      ),
    )

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.text_input_layout).helperText)
      .isNull()
  }
}
