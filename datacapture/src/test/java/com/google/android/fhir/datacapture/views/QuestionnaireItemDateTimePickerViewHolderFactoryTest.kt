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
import java.util.Date
import java.util.Locale
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class QuestionnaireItemDateTimePickerViewHolderFactoryTest {
  private val parent =
    FrameLayout(
      RuntimeEnvironment.getApplication().apply { setTheme(R.style.Theme_Material3_DayNight) }
    )
  private val viewHolder = QuestionnaireItemDateTimePickerViewHolderFactory.create(parent)

  @Before
  fun setUp() {
    Locale.setDefault(Locale.US)
    org.robolectric.shadows.ShadowSettings.set24HourTimeFormat(false)
  }

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
  fun shouldSetEmptyDateTimeInput() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.dateInputView.text.toString()).isEqualTo("")
    assertThat(viewHolder.timeInputView.text.toString()).isEqualTo("")
  }

  @Test
  fun shouldSetDateTimeInput() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0)))
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.dateInputView.text.toString()).isEqualTo("2/5/20")
    assertThat(viewHolder.timeInputView.text.toString()).isEqualTo("1:30 AM")
  }

  @Test
  fun `parse date text input in US locale`() {
    var answers: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val itemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0)))
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result -> answers = result },
      )
    viewHolder.bind(itemViewItem)

    viewHolder.dateInputView.text = "11/19/2020"

    val answer = answers!!.single().value as DateTimeType
    assertThat(answer.day).isEqualTo(19)
    assertThat(answer.month).isEqualTo(10)
    assertThat(answer.year).isEqualTo(2020)
  }

  @Test
  fun `parse date text input in Japan locale`() {
    Locale.setDefault(Locale.JAPAN)
    var answers: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val itemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0)))
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result -> answers = result },
      )
    viewHolder.bind(itemViewItem)

    viewHolder.dateInputView.text = "2020/11/19"

    val answer = answers!!.single().value as DateTimeType
    assertThat(answer.day).isEqualTo(19)
    assertThat(answer.month).isEqualTo(10)
    assertThat(answer.year).isEqualTo(2020)
  }

  @Test
  fun `if date input is invalid then clear the answer`() {
    var answers: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val itemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0)))
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result -> answers = result },
      )
    viewHolder.bind(itemViewItem)
    viewHolder.dateInputView.text = "2020/11/"

    assertThat(answers!!).isEmpty()
  }

  @Test
  fun `if date input is invalid then do not enable time text input layout`() {
    val itemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )

    viewHolder.bind(itemViewItem)
    viewHolder.dateInputView.text = "11/19/"

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.time_input_layout).isEnabled)
      .isFalse()
  }

  @Test
  fun `if date input is valid then enable time text input layout`() {
    val itemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )

    viewHolder.bind(itemViewItem)
    viewHolder.dateInputView.text = "11/19/2020"

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.time_input_layout).isEnabled)
      .isTrue()
  }

  @Test
  fun displayValidationResult_error_shouldShowErrorMessage() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Invalid(listOf("Missing answer for required field.")),
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.date_input_layout).error)
      .isEqualTo("Missing answer for required field.")
  }

  @Test
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
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.date_input_layout).error)
      .isNull()
    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.time_input_layout).error)
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
    assertThat(viewHolder.timeInputView.isEnabled).isFalse()
  }

  @Test
  fun `bind multiple times with separate QuestionnaireItemViewItem should show proper date and time`() {

    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0)))
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.dateInputView.text.toString()).isEqualTo("2/5/20")
    assertThat(viewHolder.timeInputView.text.toString()).isEqualTo("1:30 AM")

    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2021 - 1900, 1, 5, 2, 30, 0)))
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.dateInputView.text.toString()).isEqualTo("2/5/21")
    assertThat(viewHolder.timeInputView.text.toString()).isEqualTo("2:30 AM")

    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.dateInputView.text.toString()).isEmpty()
    assertThat(viewHolder.timeInputView.text.toString()).isEmpty()
  }

  private val QuestionnaireItemViewHolder.dateInputView: TextView
    get() {
      return itemView.findViewById(R.id.date_input_edit_text)
    }

  private val QuestionnaireItemViewHolder.timeInputView: TextView
    get() {
      return itemView.findViewById(R.id.time_input_edit_text)
    }
}
