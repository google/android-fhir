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
import android.widget.RadioButton
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.EXTENSION_DISPLAY_CATEGORY_SYSTEM
import com.google.android.fhir.datacapture.extensions.EXTENSION_DISPLAY_CATEGORY_URL
import com.google.android.fhir.datacapture.extensions.INSTRUCTIONS
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionTextConfiguration
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class BooleanChoiceViewHolderFactoryTest {
  private val parent =
    FrameLayout(
      RuntimeEnvironment.getApplication().apply { setTheme(R.style.Theme_Material3_DayNight) }
    )
  private val viewHolder = BooleanChoiceViewHolderFactory.create(parent)

  @Test
  fun bind_shouldSetQuestionHeader() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          text = "Question?"
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question?")
  }

  @Test
  fun noAnswer_shouldSetAnswerEmpty() {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    viewHolder.bind(questionnaireViewItem)

    assertThat(questionnaireViewItem.answers).isEmpty()
  }

  @Test
  fun noAnswer_shouldNotCheckYesOrNoRadioButton() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<RadioButton>(R.id.yes_radio_button).isChecked)
      .isFalse()
    assertThat(viewHolder.itemView.findViewById<RadioButton>(R.id.no_radio_button).isChecked)
      .isFalse()
  }

  @Test
  fun answerTrue_shouldSetAnswerTrue() {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = BooleanType(true)
            }
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    viewHolder.bind(questionnaireViewItem)

    assertThat(questionnaireViewItem.answers.single().valueBooleanType.value).isTrue()
  }

  @Test
  fun answerTrue_shouldCheckYesRadioButton() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = BooleanType(true)
            }
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<RadioButton>(R.id.yes_radio_button).isChecked)
      .isTrue()
    assertThat(viewHolder.itemView.findViewById<RadioButton>(R.id.no_radio_button).isChecked)
      .isFalse()
  }

  @Test
  fun answerFalse_shouldSetAnswerFalse() {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = BooleanType(false)
            }
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    viewHolder.bind(questionnaireViewItem)

    assertThat(questionnaireViewItem.answers.single().valueBooleanType.value).isFalse()
  }

  @Test
  fun answerFalse_shouldCheckNoRadioButton() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = BooleanType(false)
            }
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<RadioButton>(R.id.yes_radio_button).isChecked)
      .isFalse()
    assertThat(viewHolder.itemView.findViewById<RadioButton>(R.id.no_radio_button).isChecked)
      .isTrue()
  }

  @Test
  fun click_shouldSetAnswerTrue() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )
    viewHolder.bind(questionnaireViewItem)
    viewHolder.itemView.findViewById<RadioButton>(R.id.yes_radio_button).performClick()

    assertThat(answerHolder!!.single().valueBooleanType.value).isTrue()
  }

  @Test
  fun click_shouldSetAnswerFalse() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )
    viewHolder.bind(questionnaireViewItem)
    viewHolder.itemView.findViewById<RadioButton>(R.id.no_radio_button).performClick()

    assertThat(answerHolder!!.single().valueBooleanType.value).isFalse()
  }

  @Test
  fun yesSelected_clickYes_shouldClearAnswer() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = BooleanType(true)
            }
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )
    viewHolder.bind(questionnaireViewItem)
    viewHolder.itemView.findViewById<RadioButton>(R.id.yes_radio_button).performClick()

    assertThat(answerHolder).isEmpty()
  }

  @Test
  fun yesSelected_clickYes_shouldClearRadioButtons() {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = BooleanType(true)
            }
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    viewHolder.bind(questionnaireViewItem)
    viewHolder.itemView.findViewById<RadioButton>(R.id.yes_radio_button).performClick()

    assertThat(viewHolder.itemView.findViewById<RadioButton>(R.id.yes_radio_button).isChecked)
      .isFalse()
    assertThat(viewHolder.itemView.findViewById<RadioButton>(R.id.no_radio_button).isChecked)
      .isFalse()
  }

  @Test
  fun noSelected_clickNo_shouldClearAnswer() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = BooleanType(false)
            }
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )
    viewHolder.bind(questionnaireViewItem)
    viewHolder.itemView.findViewById<RadioButton>(R.id.no_radio_button).performClick()

    assertThat(answerHolder).isEmpty()
  }

  @Test
  fun noSelected_clickNo_shouldClearRadioButtons() {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = BooleanType(false)
            }
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    viewHolder.bind(questionnaireViewItem)
    viewHolder.itemView.findViewById<RadioButton>(R.id.no_radio_button).performClick()

    assertThat(viewHolder.itemView.findViewById<RadioButton>(R.id.yes_radio_button).isChecked)
      .isFalse()
    assertThat(viewHolder.itemView.findViewById<RadioButton>(R.id.no_radio_button).isChecked)
      .isFalse()
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

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.error_text_at_header).text)
      .isEqualTo("Missing answer for required field.")
  }

  @Test
  fun displayValidationResult_noError_shouldShowNoErrorMessage() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = BooleanType(true)
            }
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.error_text_at_header).text)
      .isEqualTo("")
  }

  @Test
  fun bind_readOnly_shouldDisableView() {
    viewHolder.bind(
      QuestionnaireViewItem(
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
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )
    assertThat((viewHolder.itemView.findViewById<RadioButton>(R.id.yes_radio_button).isEnabled))
      .isFalse()
    assertThat((viewHolder.itemView.findViewById<RadioButton>(R.id.no_radio_button).isEnabled))
      .isFalse()
  }

  @Test
  fun `show asterisk`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          text = "Question"
          required = true
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = true)
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question *")
  }

  @Test
  fun `hide asterisk`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          text = "Question"
          required = true
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = false)
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question")
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
        viewHolder.itemView.findViewById<TextView>(R.id.required_optional_text).text.toString()
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

    assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.required_optional_text).text.toString()
      )
      .isEmpty()
    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.required_optional_text).visibility)
      .isEqualTo(View.GONE)
  }

  @Test
  fun `show optional text`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = true)
      )
    )

    assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.required_optional_text).text.toString()
      )
      .isEqualTo("Optional")
  }

  @Test
  fun `hide optional text`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = false)
      )
    )

    assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.required_optional_text).text.toString()
      )
      .isEmpty()
    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.required_optional_text).visibility)
      .isEqualTo(View.GONE)
  }

  private val displayCategoryExtensionWithInstructionsCode =
    Extension().apply {
      url = EXTENSION_DISPLAY_CATEGORY_URL
      setValue(
        CodeableConcept().apply {
          coding =
            listOf(
              Coding().apply {
                code = INSTRUCTIONS
                system = EXTENSION_DISPLAY_CATEGORY_SYSTEM
              }
            )
        }
      )
    }
}
