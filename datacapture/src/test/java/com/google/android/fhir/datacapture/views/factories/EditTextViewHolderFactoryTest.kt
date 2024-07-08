/*
 * Copyright 2024 Google LLC
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

import android.text.Editable
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.fhir.datacapture.extensions.getValidationErrorMessage
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.material.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.common.truth.Truth
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
import org.hl7.fhir.r4.model.StringType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EditTextViewHolderFactoryTest {

  private val parent =
    FrameLayout(
      Robolectric.buildActivity(AppCompatActivity::class.java).create().get().apply {
        setTheme(R.style.Theme_Material3_DayNight)
      },
    )
  private val testViewHolder =
    object :
        EditTextViewHolderFactory(
          com.google.android.fhir.datacapture.R.layout.edit_text_single_line_view,
        ) {
        override fun getQuestionnaireItemViewHolderDelegate() =
          object : QuestionnaireItemEditTextViewHolderDelegate(DECIMAL_INPUT_TYPE) {

            private var programmaticUpdateCounter = 0

            override suspend fun handleInput(
              editable: Editable,
              questionnaireViewItem: QuestionnaireViewItem,
            ) {}

            override fun updateInputTextUI(
              questionnaireViewItem: QuestionnaireViewItem,
              textInputEditText: TextInputEditText,
            ) {
              programmaticUpdateCounter += 1
              textInputEditText.setText("$programmaticUpdateCounter")
            }

            override fun updateValidationTextUI(
              questionnaireViewItem: QuestionnaireViewItem,
              textInputLayout: TextInputLayout,
            ) {
              textInputLayout.error =
                getValidationErrorMessage(
                  textInputLayout.context,
                  questionnaireViewItem,
                  questionnaireViewItem.validationResult,
                )
              // Update error message if draft answer present
              if (questionnaireViewItem.draftAnswer != null) {
                textInputLayout.error =
                  textInputLayout.context.getString(
                    com.google.android.fhir.datacapture.R.string
                      .decimal_format_validation_error_msg,
                  )
              }
            }
          }
      }
      .create(parent)

  @Test
  fun `binding when view is in focus does not programmatically update edit text but updates validation ui`() {
    testViewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          answer =
            listOf(
              QuestionnaireResponseItemAnswerComponent().apply { value = StringType("1") },
            )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    testViewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          answer =
            listOf(
              QuestionnaireResponseItemAnswerComponent().apply { value = StringType("1.1") },
            )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    Truth.assertThat(
        testViewHolder.itemView
          .findViewById<TextInputEditText>(
            com.google.android.fhir.datacapture.R.id.text_input_edit_text,
          )
          .text
          .toString(),
      )
      .isEqualTo("2") // Value of [programmaticUpdateCounter] in the [testViewHolder]

    testViewHolder.itemView
      .findViewById<TextInputEditText>(
        com.google.android.fhir.datacapture.R.id.text_input_edit_text,
      )
      .requestFocus()

    testViewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply { answer = emptyList() },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "1.1.",
      ),
    )

    Truth.assertThat(
        testViewHolder.itemView
          .findViewById<TextInputEditText>(
            com.google.android.fhir.datacapture.R.id.text_input_edit_text,
          )
          .text
          .toString(),
      )
      .isEqualTo("2") // Since the view is in focus the value will not be updated

    Truth.assertThat(
        testViewHolder.itemView
          .findViewById<TextInputLayout>(com.google.android.fhir.datacapture.R.id.text_input_layout)
          .error
          .toString(),
      )
      .isEqualTo(
        testViewHolder.itemView
          .findViewById<TextInputLayout>(com.google.android.fhir.datacapture.R.id.text_input_layout)
          .context
          .getString(
            com.google.android.fhir.datacapture.R.string.decimal_format_validation_error_msg,
          ),
      )
  }
}
