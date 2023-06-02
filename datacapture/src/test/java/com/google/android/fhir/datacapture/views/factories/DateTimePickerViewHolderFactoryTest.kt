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

package com.google.android.fhir.datacapture.views.factories

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionTextConfiguration
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
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
class DateTimePickerViewHolderFactoryTest {
  private val parent =
    FrameLayout(
      RuntimeEnvironment.getApplication().apply { setTheme(R.style.Theme_Material3_DayNight) }
    )
  private val viewHolder = DateTimePickerViewHolderFactory.create(parent)

  @Before
  fun setUp() {
    Locale.setDefault(Locale.US)
    org.robolectric.shadows.ShadowSettings.set24HourTimeFormat(false)
  }

  @Test
  fun shouldSetQuestionHeader() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question?")
  }

  @Test
  fun shouldSetEmptyDateTimeInput() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.dateInputView.text.toString()).isEqualTo("")
    assertThat(viewHolder.timeInputView.text.toString()).isEqualTo("")
  }

  @Test
  fun `show dateFormat label in lowerCase`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )
    assertThat(viewHolder.dateInputView.hint.toString()).isEqualTo("mm/dd/yyyy")
  }

  @Test
  fun shouldSetDateTimeInput() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0)))
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.dateInputView.text.toString()).isEqualTo("02/05/2020")
    assertThat(viewHolder.timeInputView.text.toString()).isEqualTo("1:30 AM")
  }

  @Test
  fun `parse date text input in US locale`() {
    var draftAnswer: Any? = null
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0)))
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, result -> draftAnswer = result },
      )
    viewHolder.bind(itemViewItem)

    viewHolder.dateInputView.text = "11/19/2020"
    assertThat(draftAnswer as String).isEqualTo("11/19/2020")
  }

  @Test
  fun `parse date text input in Japan locale`() {
    Locale.setDefault(Locale.JAPAN)
    var draftAnswer: Any? = null
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0)))
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, result -> draftAnswer = result },
      )
    viewHolder.bind(itemViewItem)

    viewHolder.dateInputView.text = "2020/11/19"

    assertThat(draftAnswer as String).isEqualTo("2020/11/19")
  }

  @Test
  fun `if date input is invalid then clear the answer`() {
    var answers: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0)))
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result, _ -> answers = result },
      )
    viewHolder.bind(itemViewItem)
    viewHolder.dateInputView.text = "2020/11/"

    assertThat(answers!!).isEmpty()
  }

  @Test
  fun `do not clear the textField input on invalid date`() {
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0)))
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    viewHolder.bind(itemViewItem)
    viewHolder.dateInputView.text = "2020/11/"

    assertThat(viewHolder.dateInputView.text.toString()).isEqualTo("2020/11/")
  }

  @Test
  fun `clear questionnaire response answer on draft answer update`() {
    var answers: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? =
      listOf(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent())
    val questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0)))
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answersForCallback, _ -> answers = answersForCallback },
      )

    viewHolder.bind(questionnaireItem)
    questionnaireItem.setDraftAnswer("02/07")

    assertThat(answers!!).isEmpty()
  }

  @Test
  fun `clear draft answer on an valid answer update`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(DateTimeType(Date(2020 - 1900, 2, 6, 2, 30, 0)))
    var draft: String? = "02/07"
    val questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0)))
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, draftAnswer -> draft = draftAnswer as? String },
      )

    viewHolder.bind(questionnaireItem)
    questionnaireItem.setAnswer(answer)

    assertThat(draft).isNull()
  }

  @Test
  fun `display draft answer in the text field of recycled items`() {
    var questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0)))
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    viewHolder.bind(questionnaireItem)
    assertThat(viewHolder.dateInputView.text.toString()).isEqualTo("02/05/2020")

    questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "02/07"
      )

    viewHolder.bind(questionnaireItem)
    assertThat(viewHolder.dateInputView.text.toString()).isEqualTo("02/07")
  }

  @Test
  fun `display an answer in the text field of partially answered recycled item`() {
    var questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "02/07"
      )

    viewHolder.bind(questionnaireItem)
    assertThat(viewHolder.dateInputView.text.toString()).isEqualTo("02/07")

    questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0)))
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    viewHolder.bind(questionnaireItem)
    assertThat(viewHolder.dateInputView.text.toString()).isEqualTo("02/05/2020")
  }

  @Test
  fun `if draft answer input is invalid then do not enable time text input layout`() {
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "11/19/"
      )

    viewHolder.bind(itemViewItem)

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.time_input_layout).isEnabled)
      .isFalse()
  }

  @Test
  fun `if the draft answer input is empty, do not enable the time text input layout`() {
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = ""
      )

    viewHolder.bind(itemViewItem)

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.time_input_layout).isEnabled)
      .isFalse()
  }

  @Test
  fun `if there is no answer or draft answer, do not enable the time text input layout`() {
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = null
      )

    viewHolder.bind(itemViewItem)

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.time_input_layout).isEnabled)
      .isFalse()
  }

  @Test
  fun `if date draft answer is valid then enable time text input layout`() {
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "11/19/2020"
      )

    viewHolder.bind(itemViewItem)

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.time_input_layout).isEnabled)
      .isTrue()
  }

  @Test
  fun displayValidationResult_error_shouldShowErrorMessage() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Invalid(listOf("Missing answer for required field.")),
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.date_input_layout).error)
      .isEqualTo("Missing answer for required field.")
  }

  @Test
  fun displayValidationResult_noError_shouldShowNoErrorMessage() {
    viewHolder.bind(
      QuestionnaireViewItem(
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
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.date_input_layout).error)
      .isNull()
    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.time_input_layout).error)
      .isNull()
  }

  @Test
  fun `if the draft answer is invalid, display the error message`() {
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "11/19/202"
      )

    viewHolder.bind(itemViewItem)

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.date_input_layout).error)
      .isEqualTo("Date format needs to be mm/dd/yyyy (e.g. 01/31/2023)")
  }

  @Test
  fun `show dateFormat in lowerCase in the error message`() {
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "11/19/202"
      )

    viewHolder.bind(itemViewItem)

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.date_input_layout).error)
      .isEqualTo("Date format needs to be mm/dd/yyyy (e.g. 01/31/2023)")
  }

  @Test
  fun `hides error textview in the header`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.error_text_at_header).visibility)
      .isEqualTo(View.GONE)
  }

  @Test
  fun bind_readOnly_shouldDisableView() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { readOnly = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.dateInputView.isEnabled).isFalse()
    assertThat(viewHolder.timeInputView.isEnabled).isFalse()
  }

  @Test
  fun `bind multiple times with separate QuestionnaireItemViewItem should show proper date and time`() {

    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0)))
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.dateInputView.text.toString()).isEqualTo("02/05/2020")
    assertThat(viewHolder.timeInputView.text.toString()).isEqualTo("1:30 AM")

    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2021 - 1900, 1, 5, 2, 30, 0)))
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.dateInputView.text.toString()).isEqualTo("02/05/2021")
    assertThat(viewHolder.timeInputView.text.toString()).isEqualTo("2:30 AM")

    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.dateInputView.text.toString()).isEmpty()
    assertThat(viewHolder.timeInputView.text.toString()).isEmpty()
  }

  @Test
  fun `shows asterisk`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          text = "Question?"
          required = true
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = true)
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question? *")
  }

  @Test
  fun `hide asterisk`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          text = "Question?"
          required = true
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = false)
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question?")
  }

  @Test
  fun `shows required text`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = true)
      )
    )

    assertThat(
        viewHolder.itemView
          .findViewById<TextInputLayout>(R.id.date_input_layout)
          .helperText.toString()
      )
      .isEqualTo("Required")
  }

  @Test
  fun `hide required text`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = false)
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.date_input_layout).helperText)
      .isNull()
  }

  @Test
  fun `shows optional text`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = true)
      )
    )

    assertThat(
        viewHolder.itemView
          .findViewById<TextInputLayout>(R.id.date_input_layout)
          .helperText.toString()
      )
      .isEqualTo("Optional")
  }

  @Test
  fun `hide optional text`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = false)
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.date_input_layout).helperText)
      .isNull()
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
