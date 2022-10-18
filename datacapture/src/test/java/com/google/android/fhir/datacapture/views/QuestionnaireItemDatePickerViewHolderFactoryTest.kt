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

import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.material.textfield.TextInputLayout
import com.google.common.truth.Truth.assertThat
import java.util.Locale
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class QuestionnaireItemDatePickerViewHolderFactoryTest {
  private val context =
    RuntimeEnvironment.getApplication().apply { setTheme(R.style.Theme_Material3_DayNight) }
  private val parent = FrameLayout(context)
  private val viewHolder = QuestionnaireItemDatePickerViewHolderFactory.create(parent)

  @Test
  fun shouldSetQuestionHeader() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question?")
  }

  @Test
  fun shouldSetEmptyDateInput() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.dateInputView.text.toString()).isEqualTo("")
  }

  @Test
  fun `should set text field empty when date field is initialized but answer date value is null`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(DateType())
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.text_input_edit_text).text.toString()
      )
      .isEqualTo("")
  }

  @Test
  fun shouldSetDateInput_localeUs() {
    setLocale(Locale.US)
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateType(2020, 10, 19))
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )
    assertThat(viewHolder.dateInputView.text.toString()).isEqualTo("11/19/20")
  }

  @Test
  fun shouldSetDateInput_localeJp() {
    setLocale(Locale.JAPAN)
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateType(2020, 10, 19))
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )
    assertThat(viewHolder.dateInputView.text.toString()).isEqualTo("2020/11/19")
  }

  @Test
  fun shouldSetDateInput_localeEn() {
    setLocale(Locale.ENGLISH)
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateType(2020, 10, 19))
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )
    assertThat(viewHolder.dateInputView.text.toString()).isEqualTo("11/19/20")
  }

  @Test
  fun `parse date text input in US locale`() {
    setLocale(Locale.US)
    var answers: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val item =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result -> answers = result },
      )

    viewHolder.bind(item)
    viewHolder.dateInputView.text = "11/19/2020"

    val answer = answers!!.single().value as DateType

    assertThat(answer.day).isEqualTo(19)
    assertThat(answer.month).isEqualTo(10)
    assertThat(answer.year).isEqualTo(2020)
  }

  @Test
  fun `parse date text input in Japan locale`() {
    setLocale(Locale.JAPAN)
    var answers: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val item =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result -> answers = result },
      )
    viewHolder.bind(item)
    viewHolder.dateInputView.text = "2020/11/19"
    val answer = answers!!.single()?.value as DateType

    assertThat(answer.day).isEqualTo(19)
    assertThat(answer.month).isEqualTo(10)
    assertThat(answer.year).isEqualTo(2020)
  }

  @Test
  fun `clear the answer if date input is invalid`() {
    setLocale(Locale.US)
    var answers: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val questionnaireItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateType(2020, 10, 19))
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result -> answers = result },
      )
    viewHolder.bind(questionnaireItem)

    viewHolder.dateInputView.text = "11/19/"
    assertThat(answers!!).isEmpty()
  }

  @Test
  fun displayValidationResult_error_shouldShowErrorMessage() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          required = true
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/minValue"
            setValue(DateType(2020, 0, 1))
          }
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/maxValue"
            setValue(DateType(2025, 0, 1))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateType(2026, 0, 1))
          )
        },
        validationResult = Invalid(listOf("Maximum value allowed is:2025-01-01")),
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.text_input_layout).error)
      .isEqualTo("Maximum value allowed is:2025-01-01")
  }

  @Test
  fun displayValidationResult_noError_shouldShowNoErrorMessage() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/minValue"
            setValue(DateType(2020, 0, 1))
          }
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/maxValue"
            setValue(DateType(2025, 0, 1))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateType(2023, 0, 1))
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.text_input_layout).error)
      .isNull()
  }

  @Test
  fun bind_readOnly_shouldDisableView() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { readOnly = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.dateInputView.isEnabled).isFalse()
  }

  @Test
  fun `bind multiple times with different QuestionnaireItemViewItem should show proper date`() {
    setLocale(Locale.US)

    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateType(2020, 10, 19))
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )
    assertThat(viewHolder.dateInputView.text.toString()).isEqualTo("11/19/20")

    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateType(2021, 10, 19))
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )
    assertThat(viewHolder.dateInputView.text.toString()).isEqualTo("11/19/21")

    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )
    assertThat(viewHolder.dateInputView.text.toString()).isEmpty()
  }

  private fun setLocale(locale: Locale) {
    Locale.setDefault(locale)
    context.resources.configuration.setLocale(locale)
  }

  private val QuestionnaireItemViewHolder.dateInputView: TextView
    get() {
      return itemView.findViewById(R.id.text_input_edit_text)
    }
}
