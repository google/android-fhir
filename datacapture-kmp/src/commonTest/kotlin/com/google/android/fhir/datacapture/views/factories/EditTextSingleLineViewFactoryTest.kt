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
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionTextConfiguration
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.EDIT_TEXT_FIELD_TEST_TAG
import com.google.android.fhir.datacapture.views.compose.ERROR_TEXT_AT_HEADER_TEST_TAG
import com.google.android.fhir.datacapture.views.compose.QUESTION_HEADER_TAG
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.Integer
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class EditTextSingleLineViewFactoryTest {

  @Composable
  fun QuestionnaireEditTextSingleLineView(questionnaireViewItem: QuestionnaireViewItem) {
    QuestionnaireTheme { EditTextSingleLineViewFactory.Content(questionnaireViewItem) }
  }

  @Test
  fun shouldSetQuestionnaireHeader() = runComposeUiTest {
    setContent {
      QuestionnaireEditTextSingleLineView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "string-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.String),
              text = FhirR4String(value = "Question?"),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "string-item")),
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
      QuestionnaireEditTextSingleLineView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "string-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.String),
            ),
            QuestionnaireResponse.Item(
              linkId = FhirR4String(value = "string-item"),
              answer =
                listOf(
                  QuestionnaireResponse.Item.Answer(
                    value =
                      QuestionnaireResponse.Item.Answer.Value.String(
                        value = FhirR4String(value = "Answer"),
                      ),
                  ),
                ),
            ),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
      )
    }

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("Answer")
  }

  @Test
  fun shouldSetInputTextToEmpty() = runComposeUiTest {
    var questionnaireViewItem by
      mutableStateOf(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "string-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.String),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "string-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value =
                    QuestionnaireResponse.Item.Answer.Value.String(
                      value = FhirR4String(value = "Answer"),
                    ),
                ),
              ),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )

    setContent { QuestionnaireEditTextSingleLineView(questionnaireViewItem) }

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("Answer")

    questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "string-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.String),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "string-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("")
  }

  @Test
  fun shouldSetQuestionnaireResponseItemAnswer() = runComposeUiTest {
    var answers: List<QuestionnaireResponse.Item.Answer>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "string-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.String),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "string-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result, _ -> answers = result },
      )

    setContent { QuestionnaireEditTextSingleLineView(questionnaireViewItem) }
    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextReplacement("Answer")
    waitUntil { answers != null }

    answers!!.single().value!!.asString()!!.value.value!!.shouldBe("Answer")
  }

  @Test
  fun shouldSetQuestionnaireResponseItemAnswerToEmpty() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "string-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.String),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "string-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    setContent { QuestionnaireEditTextSingleLineView(questionnaireViewItem) }
    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextReplacement("")
    waitForIdle()

    questionnaireViewItem.answers.shouldBeEmpty()
  }

  @Test
  fun displayValidationResult_noError_shouldShowNoErrorMessage() = runComposeUiTest {
    setContent {
      QuestionnaireEditTextSingleLineView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "string-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.String),
              extension =
                listOf(
                  Extension(
                    url = "http://hl7.org/fhir/StructureDefinition/minLength",
                    value = Extension.Value.Integer(value = Integer(value = 10)),
                  ),
                ),
            ),
            QuestionnaireResponse.Item(
              linkId = FhirR4String(value = "string-item"),
              answer =
                listOf(
                  QuestionnaireResponse.Item.Answer(
                    value =
                      QuestionnaireResponse.Item.Answer.Value.String(
                        value = FhirR4String(value = "hello there"),
                      ),
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
  fun displayValidationResult_error_shouldShowErrorMessage() = runComposeUiTest {
    setContent {
      QuestionnaireEditTextSingleLineView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "string-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.String),
              extension =
                listOf(
                  Extension(
                    url = "http://hl7.org/fhir/StructureDefinition/minLength",
                    value = Extension.Value.Integer(value = Integer(value = 10)),
                  ),
                ),
            ),
            QuestionnaireResponse.Item(
              linkId = FhirR4String(value = "string-item"),
              answer =
                listOf(
                  QuestionnaireResponse.Item.Answer(
                    value =
                      QuestionnaireResponse.Item.Answer.Value.String(
                        value = FhirR4String(value = "hello"),
                      ),
                  ),
                ),
            ),
            validationResult =
              Invalid(
                listOf("The minimum number of characters that are permitted in the answer is: 10"),
              ),
            answersChangedCallback = { _, _, _, _ -> },
          ),
      )
    }

    onNodeWithContentDescription("Error").assertIsDisplayed()
    onNodeWithText("The minimum number of characters that are permitted in the answer is: 10")
      .assertIsDisplayed()
  }

  @Test
  fun hidesErrorTextviewInTheHeader() = runComposeUiTest {
    setContent {
      QuestionnaireEditTextSingleLineView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "string-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.String),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "string-item")),
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
      QuestionnaireEditTextSingleLineView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "string-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.String),
              readOnly = FhirR4Boolean(value = true),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "string-item")),
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
      QuestionnaireEditTextSingleLineView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "string-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.String),
              text = FhirR4String(value = "Question?"),
              required = FhirR4Boolean(value = true),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "string-item")),
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
      QuestionnaireEditTextSingleLineView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "string-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.String),
              text = FhirR4String(value = "Question?"),
              required = FhirR4Boolean(value = true),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "string-item")),
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
      QuestionnaireEditTextSingleLineView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "string-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.String),
              required = FhirR4Boolean(value = true),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "string-item")),
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
      QuestionnaireEditTextSingleLineView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "string-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.String),
              required = FhirR4Boolean(value = true),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "string-item")),
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
      QuestionnaireEditTextSingleLineView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "string-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.String),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "string-item")),
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
      QuestionnaireEditTextSingleLineView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "string-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.String),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "string-item")),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
            questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = false),
          ),
      )
    }

    onNodeWithText("Optional").assertDoesNotExist()
  }

  @Test
  fun displaysCorrectTextOnQuestionnaireViewItemAnswerUpdate() = runComposeUiTest {
    var questionnaireViewItem by
      mutableStateOf(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "string-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.String),
            text = FhirR4String(value = "First Name"),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "string-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value =
                    QuestionnaireResponse.Item.Answer.Value.String(
                      value = FhirR4String(value = "Jane"),
                    ),
                ),
              ),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )

    setContent { QuestionnaireEditTextSingleLineView(questionnaireViewItem) }
    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("Jane")

    questionnaireViewItem =
      questionnaireViewItem.copy(
        questionnaireResponseItem =
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "string-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value =
                    QuestionnaireResponse.Item.Answer.Value.String(
                      value = FhirR4String(value = "Janette"),
                    ),
                ),
              ),
          ),
      )

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("Janette")
  }
}
