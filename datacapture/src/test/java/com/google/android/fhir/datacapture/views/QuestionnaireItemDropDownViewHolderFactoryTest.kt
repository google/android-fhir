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

import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.displayString
import com.google.android.material.textfield.TextInputLayout
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class QuestionnaireItemDropDownViewHolderFactoryTest {
  private val parent =
    FrameLayout(
      RuntimeEnvironment.getApplication().apply { setTheme(R.style.Theme_MaterialComponents) }
    )
  private val viewHolder = QuestionnaireItemDropDownViewHolderFactory().create(parent)

  @Test
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
  fun shouldPopulateDropDown() {
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
        value = Coding().setCode("test-code").setDisplay("Test Code")
      }
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { addAnswerOption(answerOption) },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(
        viewHolder
          .itemView
          .findViewById<AutoCompleteTextView>(R.id.auto_complete)
          .adapter
          .getItem(1)
          .toString()
      )
      .isEqualTo("Test Code")
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
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(
        viewHolder
          .itemView
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
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
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
        }
      ) {}
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
        modified = true
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.text_input_layout).error)
      .isEqualTo("Missing answer for required field.")
  }

  @Test
  fun displayValidationResult_shouldShowNoErrorMessageAtStart() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        modified = false
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.text_input_layout).error)
      .isNull()
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
          )
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.text_input_layout).error)
      .isNull()
  }

  @Test
  fun bind_readOnly_shouldDisableView() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { readOnly = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.text_input_layout).isEnabled)
      .isFalse()
  }
}
