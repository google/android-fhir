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
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.TestActivity
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.material.textfield.TextInputLayout
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemMultiSelectHolderFactoryInstrumentedTest {
  @get:Rule val rule = activityScenarioRule<TestActivity>()

  @Test
  fun emptyResponseOptions_showNoneSelected() = withViewHolder { holder ->
    holder.bind(
      QuestionnaireItemViewItem(
        answerOptions("Coding 1", "Coding 2"),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )
    assertThat(holder.itemView.findViewById<TextView>(R.id.multi_select_summary).text.toString())
      .isEqualTo("")
  }

  @Test
  fun selectedResponseOptions_showSelectedOptions() = withViewHolder { holder ->
    holder.bind(
      QuestionnaireItemViewItem(
        answerOptions("Coding 1", "Coding 2", "Coding 3"),
        responseOptions("Coding 1", "Coding 3"),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )
    assertThat(holder.itemView.findViewById<TextView>(R.id.multi_select_summary).text.toString())
      .isEqualTo("Coding 1, Coding 3")
  }

  @Test
  @UiThreadTest
  fun displayValidationResult_error_shouldShowErrorMessage() = withViewHolder { viewHolder ->
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "1"
          required = true
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Invalid(listOf("Missing answer for required field.")),
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(
        viewHolder.itemView.findViewById<TextInputLayout>(R.id.multi_select_summary_holder).error
      )
      .isEqualTo("Missing answer for required field.")
  }

  @Test
  @UiThreadTest
  fun displayValidationResult_noError_shouldShowNoErrorMessage() = withViewHolder { viewHolder ->
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "1"
          required = true
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "display" }
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = Coding().apply { display = "display" }
            }
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(
        viewHolder.itemView.findViewById<TextInputLayout>(R.id.multi_select_summary_holder).error
      )
      .isNull()
  }

  @Test
  fun bind_readOnly_shouldDisableView() = withViewHolder { holder ->
    holder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "1"
          readOnly = true
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(
        holder.itemView.findViewById<TextInputLayout>(R.id.multi_select_summary_holder).isEnabled
      )
      .isFalse()
  }

  private inline fun withViewHolder(
    crossinline block: TestActivity.(QuestionnaireItemViewHolder) -> Unit
  ) {
    rule.scenario.onActivity {
      block(it, QuestionnaireItemDialogSelectViewHolderFactory.create(FrameLayout(it)))
    }
  }
}

private fun answerOptions(vararg options: String) =
  Questionnaire.QuestionnaireItemComponent().apply {
    linkId = "1"
    repeats = true
    options.forEach { option ->
      addAnswerOption(
        Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
          value = Coding().apply { display = option }
        }
      )
    }
  }

private fun responseOptions(vararg responses: String) =
  QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
    responses.forEach { response ->
      addAnswer(
        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
          value = Coding().apply { display = response }
        }
      )
    }
  }
