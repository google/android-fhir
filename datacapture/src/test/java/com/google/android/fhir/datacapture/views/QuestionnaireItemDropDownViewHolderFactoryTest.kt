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

import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.fhir.datacapture.DisplayItemControlType
import com.google.android.fhir.datacapture.EXTENSION_ITEM_CONTROL_SYSTEM
import com.google.android.fhir.datacapture.EXTENSION_ITEM_CONTROL_URL
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.displayString
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.material.textfield.TextInputLayout
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Reference
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class QuestionnaireItemDropDownViewHolderFactoryTest {
  private val parent =
    FrameLayout(
      RuntimeEnvironment.getApplication().apply { setTheme(R.style.Theme_Material3_DayNight) }
    )
  private val viewHolder = QuestionnaireItemDropDownViewHolderFactory.create(parent)

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
  fun shouldPopulateDropDown() {
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
        value = Coding().setCode("test-code").setDisplay("Test Code")
      }
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { addAnswerOption(answerOption) },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(
        viewHolder.itemView
          .findViewById<AutoCompleteTextView>(R.id.auto_complete)
          .adapter
          .getItem(1)
          .toString()
      )
      .isEqualTo("Test Code")
  }

  @Test
  fun `should populate dropdown with display for reference value type`() {
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
        value =
          Reference().apply {
            reference = "Patient/123"
            display = "John Doe"
          }
      }
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { addAnswerOption(answerOption) },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(
        viewHolder.itemView
          .findViewById<AutoCompleteTextView>(R.id.auto_complete)
          .adapter
          .getItem(1)
          .toString()
      )
      .isEqualTo("John Doe")
  }

  @Test
  fun `should populate dropdown with type and id for reference value type if missing display`() {
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
        value = Reference().apply { reference = "Patient/123" }
      }
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { addAnswerOption(answerOption) },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(
        viewHolder.itemView
          .findViewById<AutoCompleteTextView>(R.id.auto_complete)
          .adapter
          .getItem(1)
          .toString()
      )
      .isEqualTo("Patient/123")
  }

  @Test
  fun shouldSetDropDownOptionToCodeIfValueCodingDisplayEmpty() {
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
        value = Coding().apply { code = "test-code" }
      }
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { addAnswerOption(answerOption) },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(
        viewHolder.itemView
          .findViewById<AutoCompleteTextView>(R.id.auto_complete)
          .adapter
          .getItem(1)
          .toString()
      )
      .isEqualTo("test-code")
  }

  @Test
  fun shouldSetAutoTextViewEmptyIfAnswerNull() {
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
        value =
          Coding().apply {
            code = "test-code"
            display = "Test Code"
          }
      }
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { addAnswerOption(answerOption) },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(
        viewHolder.itemView.findViewById<AutoCompleteTextView>(R.id.auto_complete).text.toString()
      )
      .isEqualTo("")
  }

  @Test
  fun shouldAutoCompleteTextViewToDisplayIfAnswerNotNull() {
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
        value =
          Coding().apply {
            code = "test-code"
            display = "Test Code"
          }
      }
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          answerValueSet = "http://coding-value-set-url"
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = answerOption.value
            }
          )
        },
        resolveAnswerValueSet = {
          if (it == "http://coding-value-set-url") {
            listOf(answerOption)
          } else {
            emptyList()
          }
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(
        viewHolder.itemView.findViewById<AutoCompleteTextView>(R.id.auto_complete).text.toString()
      )
      .isEqualTo(answerOption.displayString)
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

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.text_input_layout).error)
      .isEqualTo("Missing answer for required field.")
  }

  @Test
  fun displayValidationResult_noError_shouldShowNoErrorMessage() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          required = true
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "display" }
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = Coding().apply { display = "display" }
            }
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.text_input_layout).error)
      .isNull()
  }

  @Test
  fun `hides error textview in the header`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.error_text_at_header).visibility)
      .isEqualTo(View.GONE)
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

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.text_input_layout).isEnabled)
      .isFalse()
  }

  @Test
  fun `shows asterisk at the end of the flyover text`() {
    val displayItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        type = Questionnaire.QuestionnaireItemType.DISPLAY
        text = "Choice"
        addExtension(
          Extension()
            .setUrl(EXTENSION_ITEM_CONTROL_URL)
            .setValue(
              CodeableConcept()
                .addCoding(
                  Coding()
                    .setCode(DisplayItemControlType.FLYOVER.extensionCode)
                    .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM)
                )
            )
        )
      }

    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "link-id"
          required = true
          item = listOf(displayItem)
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(
        viewHolder.itemView.findViewById<TextInputLayout>(R.id.text_input_layout).hint.toString()
      )
      .isEqualTo("Choice *")
  }

  @Test
  fun `does not show asterisk at the end of the flyover text if question text is not present and questionnaire item is not required`() {
    val displayItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        type = Questionnaire.QuestionnaireItemType.DISPLAY
        text = "Choice"
        addExtension(
          Extension()
            .setUrl(EXTENSION_ITEM_CONTROL_URL)
            .setValue(
              CodeableConcept()
                .addCoding(
                  Coding()
                    .setCode(DisplayItemControlType.FLYOVER.extensionCode)
                    .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM)
                )
            )
        )
      }

    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "link-id"
          item = listOf(displayItem)
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(
        viewHolder.itemView.findViewById<TextInputLayout>(R.id.text_input_layout).hint.toString()
      )
      .isEqualTo("Choice")
  }

  @Test
  fun `does not show asterisk at the end of the flyover text if question text is present and questionnaire item is not required`() {
    val displayItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        type = Questionnaire.QuestionnaireItemType.DISPLAY
        text = "Choice"
        addExtension(
          Extension()
            .setUrl(EXTENSION_ITEM_CONTROL_URL)
            .setValue(
              CodeableConcept()
                .addCoding(
                  Coding()
                    .setCode(DisplayItemControlType.FLYOVER.extensionCode)
                    .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM)
                )
            )
        )
      }

    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "link-id"
          required = true
          text = "Question text"
          item = listOf(displayItem)
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(
        viewHolder.itemView.findViewById<TextInputLayout>(R.id.text_input_layout).hint.toString()
      )
      .isEqualTo("Choice")
  }

  @Test
  fun `does not show asterisk at the end of the flyover text if questionnaire item is not required`() {
    val displayItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        type = Questionnaire.QuestionnaireItemType.DISPLAY
        text = "Choice"
        addExtension(
          Extension()
            .setUrl(EXTENSION_ITEM_CONTROL_URL)
            .setValue(
              CodeableConcept()
                .addCoding(
                  Coding()
                    .setCode(DisplayItemControlType.FLYOVER.extensionCode)
                    .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM)
                )
            )
        )
      }

    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "link-id"
          item = listOf(displayItem)
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(
        viewHolder.itemView.findViewById<TextInputLayout>(R.id.text_input_layout).hint.toString()
      )
      .isEqualTo("Choice")
  }
}
