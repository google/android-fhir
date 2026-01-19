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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.runComposeUiTest
import com.google.android.fhir.datacapture.extensions.FhirR4Boolean
import com.google.android.fhir.datacapture.extensions.FhirR4String
import com.google.android.fhir.datacapture.extensions.IntegerAnswerValue
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionTextConfiguration
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.components.EDIT_TEXT_FIELD_TEST_TAG
import com.google.android.fhir.datacapture.views.components.ERROR_TEXT_AT_HEADER_TEST_TAG
import com.google.android.fhir.datacapture.views.components.QUESTION_HEADER_TAG
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.Integer
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import io.kotest.matchers.shouldBe
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class EditTextIntegerViewFactoryTest {

  @Composable
  fun QuestionnaireEditTextIntegerView(questionnaireViewItem: QuestionnaireViewItem) {
    QuestionnaireTheme { EditTextIntegerViewFactory.Content(questionnaireViewItem) }
  }

  @Test
  fun shouldSetQuestionnaireHeader() = runComposeUiTest {
    setContent {
      QuestionnaireEditTextIntegerView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "integer-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
              text = FhirR4String(value = "Question?"),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "integer-item")),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
      )
    }

    onNodeWithTag(QUESTION_HEADER_TAG).assertTextEquals("Question?")
  }

  @Test
  fun shouldSetInputText() = runComposeUiTest {
    setContent {
      QuestionnaireEditTextIntegerView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "integer-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
            ),
            QuestionnaireResponse.Item(
              linkId = FhirR4String(value = "integer-item"),
              answer =
                listOf(
                  QuestionnaireResponse.Item.Answer(
                    value = IntegerAnswerValue(value = Integer(value = 5)),
                  ),
                ),
            ),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
      )
    }

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("5")
  }

  @Test
  fun shouldSetInputTextToEmpty() = runComposeUiTest {
    var questionnaireViewItem by
      mutableStateOf(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "integer-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "integer-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value = IntegerAnswerValue(value = Integer(value = 5)),
                ),
              ),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )

    setContent { QuestionnaireEditTextIntegerView(questionnaireViewItem) }

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("5")

    questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "integer-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "integer-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("")
  }

  @Test
  fun shouldSetQuestionnaireResponseItemAnswerIfTextIsValid() = runComposeUiTest {
    var answers: List<QuestionnaireResponse.Item.Answer>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "integer-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "integer-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result, _ -> answers = result },
      )

    setContent { QuestionnaireEditTextIntegerView(questionnaireViewItem) }
    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextReplacement("13")
    waitUntil { answers != null }

    answers!!.single().value!!.asInteger()!!.value.value.shouldBe(13)
  }

  @Test
  fun shouldSetQuestionnaireResponseItemAnswerToEmpty() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "integer-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "integer-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    setContent { QuestionnaireEditTextIntegerView(questionnaireViewItem) }
    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextReplacement("")
    waitForIdle()

    questionnaireViewItem.answers.isEmpty()
  }

  @Test
  fun shouldSetDraftAnswerIfTextIsInvalid() = runComposeUiTest {
    var draftAnswer: Any? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "integer-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "integer-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, result -> draftAnswer = result },
      )
    setContent { QuestionnaireEditTextIntegerView(questionnaireViewItem) }
    // The character in 1O2 is the letter O, not the number 0
    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextReplacement("1O2")
    waitUntil { draftAnswer != null }
    (draftAnswer as String).shouldBe("1O2")
  }

  @Test
  fun displayValidationResultShouldShowNoErrorMessage() = runComposeUiTest {
    setContent {
      QuestionnaireEditTextIntegerView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "integer-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
              extension =
                listOf(
                  Extension(
                    url = "http://hl7.org/fhir/StructureDefinition/minValue",
                    value = Extension.Value.Integer(value = Integer(value = 2)),
                  ),
                  Extension(
                    url = "http://hl7.org/fhir/StructureDefinition/maxValue",
                    value = Extension.Value.Integer(value = Integer(value = 4)),
                  ),
                ),
            ),
            QuestionnaireResponse.Item(
              linkId = FhirR4String(value = "integer-item"),
              answer =
                listOf(
                  QuestionnaireResponse.Item.Answer(
                    value = IntegerAnswerValue(value = Integer(value = 3)),
                  ),
                ),
            ),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
      )
    }

    onNodeWithContentDescription("Error").assertDoesNotExist()
  }

  @Test
  fun displayValidationResultShouldShowErrorMessage() = runComposeUiTest {
    setContent {
      QuestionnaireEditTextIntegerView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "integer-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
              extension =
                listOf(
                  Extension(
                    url = "http://hl7.org/fhir/StructureDefinition/minValue",
                    value = Extension.Value.Integer(value = Integer(value = 2)),
                  ),
                  Extension(
                    url = "http://hl7.org/fhir/StructureDefinition/maxValue",
                    value = Extension.Value.Integer(value = Integer(value = 4)),
                  ),
                ),
            ),
            QuestionnaireResponse.Item(
              linkId = FhirR4String(value = "integer-item"),
              answer =
                listOf(
                  QuestionnaireResponse.Item.Answer(
                    value = IntegerAnswerValue(value = Integer(value = 1)),
                  ),
                ),
            ),
            validationResult = Invalid(listOf("Minimum value allowed is:2")),
            answersChangedCallback = { _, _, _, _ -> },
          ),
      )
    }

    onNodeWithContentDescription("Error").assertIsDisplayed()
    onNodeWithText("Minimum value allowed is:2").assertIsDisplayed()
  }

  @Test
  fun hidesErrorTextviewInTheHeader() = runComposeUiTest {
    setContent {
      QuestionnaireEditTextIntegerView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "integer-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "integer-item")),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
      )
    }

    onNodeWithTag(ERROR_TEXT_AT_HEADER_TEST_TAG).assertDoesNotExist()
  }

  @Test
  fun bindReadOnlyShouldDisableView() = runComposeUiTest {
    setContent {
      QuestionnaireEditTextIntegerView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "integer-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
              readOnly = FhirR4Boolean(value = true),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "integer-item")),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
      )
    }

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertIsNotEnabled()
  }

  @Test
  fun showAsterisk() = runComposeUiTest {
    setContent {
      QuestionnaireEditTextIntegerView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "integer-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
              text = FhirR4String(value = "Question?"),
              required = FhirR4Boolean(value = true),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "integer-item")),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
            questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = true),
          ),
      )
    }

    onNodeWithTag(QUESTION_HEADER_TAG).assertTextEquals("Question? *")
  }

  @Test
  fun hideAsterisk() = runComposeUiTest {
    setContent {
      QuestionnaireEditTextIntegerView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "integer-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
              text = FhirR4String(value = "Question?"),
              required = FhirR4Boolean(value = true),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "integer-item")),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
            questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = false),
          ),
      )
    }

    onNodeWithTag(QUESTION_HEADER_TAG).assertTextEquals("Question?")
  }

  @Test
  fun showsRequiredText() = runComposeUiTest {
    setContent {
      QuestionnaireEditTextIntegerView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "integer-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
              required = FhirR4Boolean(value = true),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "integer-item")),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
            questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = true),
          ),
      )
    }

    onNodeWithText("Required", substring = true).assertIsDisplayed()
  }

  @Test
  fun hideRequiredText() = runComposeUiTest {
    setContent {
      QuestionnaireEditTextIntegerView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "integer-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
              required = FhirR4Boolean(value = true),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "integer-item")),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
            questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = false),
          ),
      )
    }

    onNodeWithText("Required").assertDoesNotExist()
  }

  @Test
  fun showOptionalText() = runComposeUiTest {
    setContent {
      QuestionnaireEditTextIntegerView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "integer-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "integer-item")),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
            questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = true),
          ),
      )
    }

    onNodeWithText("Optional", substring = true).assertIsDisplayed()
  }

  @Test
  fun hideOptionalText() = runComposeUiTest {
    setContent {
      QuestionnaireEditTextIntegerView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "integer-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "integer-item")),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
            questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = false),
          ),
      )
    }

    onNodeWithText("Optional").assertDoesNotExist()
  }

  @Test
  fun bindAgainShouldRemovePreviousText() = runComposeUiTest {
    var questionnaireViewItem by
      mutableStateOf(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "integer-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
          ),
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "integer-item")),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
          draftAnswer = "9999999999",
        ),
      )

    setContent { QuestionnaireEditTextIntegerView(questionnaireViewItem) }

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG, useUnmergedTree = true).assertTextEquals("9999999999")

    questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "integer-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "integer-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG, useUnmergedTree = true).assertTextEquals("")
  }

  @Test
  fun displaysCorrectTextOnQuestionnaireViewItemAnswerUpdate() = runComposeUiTest {
    var questionnaireViewItem by
      mutableStateOf(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "integer-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
            text = FhirR4String(value = "Age"),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "integer-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value = IntegerAnswerValue(value = Integer(value = 12)),
                ),
              ),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )

    setContent { QuestionnaireEditTextIntegerView(questionnaireViewItem) }
    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("12")

    questionnaireViewItem =
      questionnaireViewItem.copy(
        questionnaireResponseItem =
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "integer-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value = IntegerAnswerValue(value = Integer(value = 120)),
                ),
              ),
          ),
      )

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("120")
  }
}
