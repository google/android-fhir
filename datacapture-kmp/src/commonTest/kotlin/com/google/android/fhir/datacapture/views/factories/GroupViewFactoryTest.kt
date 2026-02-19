/*
 * Copyright 2025-2026 Google LLC
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

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import com.google.android.fhir.datacapture.extensions.FhirR4Boolean
import com.google.android.fhir.datacapture.extensions.FhirR4String
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.components.ERROR_TEXT_TAG
import com.google.android.fhir.datacapture.views.components.HEADER_TAG
import com.google.android.fhir.datacapture.views.components.HINT_HEADER_TAG
import com.google.android.fhir.datacapture.views.components.QUESTION_HEADER_TAG
import com.google.fhir.model.r4.Coding
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class GroupViewFactoryTest {

  @Composable
  fun QuestionnaireGroupView(questionnaireViewItem: QuestionnaireViewItem) {
    QuestionnaireTheme { GroupViewFactory.Content(questionnaireViewItem) }
  }

  @Test
  fun shouldSetQuestionHeader() = runComposeUiTest {
    setContent {
      QuestionnaireGroupView(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "group-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Group),
            text = FhirR4String(value = "Group header"),
          ),
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "group-item")),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )
    }

    onNodeWithTag(QUESTION_HEADER_TAG).assertTextEquals("Group header")
  }

  @Test
  fun displayValidationResult_error_shouldShowErrorMessage() = runComposeUiTest {
    setContent {
      QuestionnaireGroupView(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "group-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Group),
            required = FhirR4Boolean(value = true),
          ),
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "group-item")),
          validationResult = Invalid(listOf("Missing answer for required field.")),
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )
    }

    onNodeWithTag(ERROR_TEXT_TAG).assertTextEquals("Missing answer for required field.")
  }

  @Test
  fun displayValidationResult_noError_shouldShowNoErrorMessage() = runComposeUiTest {
    setContent {
      QuestionnaireGroupView(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "group-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Group),
            required = FhirR4Boolean(value = true),
            answerOption =
              listOf(
                Questionnaire.Item.AnswerOption(
                  value =
                    Questionnaire.Item.AnswerOption.Value.Coding(
                      value = Coding(display = FhirR4String(value = "display")),
                    ),
                ),
              ),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "group-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value =
                    QuestionnaireResponse.Item.Answer.Value.Coding(
                      value = Coding(display = FhirR4String(value = "display")),
                    ),
                ),
              ),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )
    }

    onNodeWithTag(ERROR_TEXT_TAG).assertDoesNotExist()
  }

  @Test
  fun hintText_nestedDisplayItem_shouldNotShowHintText() = runComposeUiTest {
    setContent {
      QuestionnaireGroupView(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "group-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Group),
            item =
              listOf(
                Questionnaire.Item(
                  linkId = FhirR4String(value = "nested-display-question"),
                  type = Enumeration(value = Questionnaire.QuestionnaireItemType.Display),
                  text = FhirR4String(value = "text"),
                ),
              ),
          ),
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "group-item")),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )
    }

    onNodeWithTag(HINT_HEADER_TAG).assertDoesNotExist()
  }

  @Test
  fun shouldHaveHeaderViewVisible() = runComposeUiTest {
    setContent {
      QuestionnaireGroupView(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "group-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Group),
            text = FhirR4String(value = "Group header"),
          ),
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "group-item")),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )
    }

    onNodeWithTag(HEADER_TAG).assertIsDisplayed()
  }

  @Test
  fun shouldSetHeaderViewVisibilityAsGone() = runComposeUiTest {
    setContent {
      QuestionnaireGroupView(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "group-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Group),
          ),
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "group-item")),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )
    }

    onNodeWithTag(HEADER_TAG).assertDoesNotExist()
  }
}
