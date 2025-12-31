/*
 * Copyright 2025 Google LLC
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

import android_fhir.datacapture_kmp.generated.resources.Res
import android_fhir.datacapture_kmp.generated.resources.decimal_format_validation_error_msg
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.runComposeUiTest
import com.google.android.fhir.datacapture.extensions.DecimalAnswerValue
import com.google.android.fhir.datacapture.extensions.FhirR4String
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.EDIT_TEXT_FIELD_TEST_TAG
import com.google.fhir.model.r4.Decimal
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class EditTextViewFactoryDelegateTest {

  private var programmaticUpdateCounter = 0

  val editTextFactory: EditTextViewFactoryDelegate
    get() {
      return EditTextViewFactoryDelegate(
        keyboardOptions = KeyboardOptions.Default,
        uiInputText = {
          programmaticUpdateCounter += 1
          "$programmaticUpdateCounter"
        },
        validationMessageStringRes = Res.string.decimal_format_validation_error_msg,
        handleInput = { _, _ -> },
      )
    }

  @Test
  fun bindingWhenViewIsInFocusDoesNotProgrammaticallyUpdateEditTextButUpdatesValidationUi() =
    runComposeUiTest {
      var questionnaireViewItem by
        mutableStateOf(
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "edit-text-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Decimal),
            ),
            QuestionnaireResponse.Item(
              linkId = FhirR4String(value = "edit-text-item"),
              answer =
                listOf(
                  QuestionnaireResponse.Item.Answer(
                    value = DecimalAnswerValue(value = Decimal(value = 1.toBigDecimal())),
                  ),
                ),
            ),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
        )

      setContent { QuestionnaireTheme { editTextFactory.Content(questionnaireViewItem) } }

      questionnaireViewItem =
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "edit-text-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Decimal),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "edit-text-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value = DecimalAnswerValue(value = Decimal(value = 1.1.toBigDecimal())),
                ),
              ),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        )

      onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG)
        .assertTextEquals("2") // Value of [programmaticUpdateCounter] in the [testViewHolder]
      onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).requestFocus()

      questionnaireViewItem =
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "edit-text-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Decimal),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "edit-text-item"),
            answer = emptyList(),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
          draftAnswer = "1.1.",
        )

      onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG, useUnmergedTree = true)
        .assertTextEquals("2") // Since the view is in focus the value will not be updated

      onNodeWithContentDescription("Error").assertIsDisplayed()
      onNodeWithText("Invalid number").assertIsDisplayed()
    }
}
