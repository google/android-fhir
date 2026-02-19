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
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.google.android.fhir.datacapture.extensions.FhirR4Boolean
import com.google.android.fhir.datacapture.extensions.FhirR4String
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionTextConfiguration
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.components.ERROR_TEXT_AT_HEADER_TEST_TAG
import com.google.android.fhir.datacapture.views.components.QUESTION_HEADER_TAG
import com.google.android.fhir.datacapture.views.components.REQUIRED_OPTIONAL_HEADER_TEXT_TAG
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class BooleanChoiceViewFactoryTest {

  @Composable
  fun QuestionnaireBooleanChoice(questionnaireViewItem: QuestionnaireViewItem) {
    QuestionnaireTheme { BooleanChoiceViewFactory.Content(questionnaireViewItem) }
  }

  @Test
  fun bind_shouldSetQuestionHeader() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
          text = FhirR4String(value = "Question?"),
          repeats = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "boolean-choice-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireBooleanChoice(questionnaireViewItem) }

    onNodeWithTag(QUESTION_HEADER_TAG).assertTextEquals("Question?")
  }

  @Test
  fun noAnswer_shouldSetAnswerEmpty() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "boolean-choice-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    setContent { QuestionnaireBooleanChoice(questionnaireViewItem) }

    questionnaireViewItem.answers.shouldBeEmpty()
  }

  @Test
  fun noAnswer_shouldNotCheckYesOrNoRadioButton() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "boolean-choice-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireBooleanChoice(questionnaireViewItem) }

    onNodeWithTag(YES_CHOICE_RADIO_BUTTON_TAG).assertIsNotSelected()
    onNodeWithTag(NO_CHOICE_RADIO_BUTTON_TAG).assertIsNotSelected()
  }

  @Test
  fun answerTrue_shouldSetAnswerTrue() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  QuestionnaireResponse.Item.Answer.Value.Boolean(
                    value = FhirR4Boolean(value = true),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    setContent { QuestionnaireBooleanChoice(questionnaireViewItem) }
    questionnaireViewItem.answers.single().value?.asBoolean()?.value?.value.shouldBeTrue()
  }

  @Test
  fun answerTrue_shouldCheckYesRadioButton() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  QuestionnaireResponse.Item.Answer.Value.Boolean(
                    value = FhirR4Boolean(value = true),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireBooleanChoice(questionnaireViewItem) }

    onNodeWithTag(YES_CHOICE_RADIO_BUTTON_TAG).assertIsSelected()
    onNodeWithTag(NO_CHOICE_RADIO_BUTTON_TAG).assertIsNotSelected()
  }

  @Test
  fun answerFalse_shouldSetAnswerFalse() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  QuestionnaireResponse.Item.Answer.Value.Boolean(
                    value = FhirR4Boolean(value = false),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    setContent { QuestionnaireBooleanChoice(questionnaireViewItem) }

    questionnaireViewItem.answers.single().value?.asBoolean()?.value?.value.shouldBeFalse()
  }

  @Test
  fun answerFalse_shouldCheckNoRadioButton() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  QuestionnaireResponse.Item.Answer.Value.Boolean(
                    value = FhirR4Boolean(value = false),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireBooleanChoice(questionnaireViewItem) }
    onNodeWithTag(YES_CHOICE_RADIO_BUTTON_TAG).assertIsNotSelected()
    onNodeWithTag(NO_CHOICE_RADIO_BUTTON_TAG).assertIsSelected()
  }

  @Test
  fun click_shouldSetAnswerTrue() = runComposeUiTest {
    var answerHolder: List<QuestionnaireResponse.Item.Answer>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "boolean-choice-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )
    setContent { QuestionnaireBooleanChoice(questionnaireViewItem) }
    onNodeWithTag(YES_CHOICE_RADIO_BUTTON_TAG).performClick()
    waitUntil { answerHolder != null }

    answerHolder?.single()?.value?.asBoolean()?.value?.value.shouldBeTrue()
  }

  @Test
  fun click_shouldSetAnswerFalse() = runComposeUiTest {
    var answerHolder: List<QuestionnaireResponse.Item.Answer>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "boolean-choice-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )
    setContent { QuestionnaireBooleanChoice(questionnaireViewItem) }
    onNodeWithTag(NO_CHOICE_RADIO_BUTTON_TAG).performClick()
    waitUntil { answerHolder != null }

    answerHolder?.single()?.value?.asBoolean()?.value?.value.shouldBeFalse()
  }

  @Test
  fun yesSelected_clickYes_shouldClearAnswer() = runComposeUiTest {
    var answerHolder: List<QuestionnaireResponse.Item.Answer>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  QuestionnaireResponse.Item.Answer.Value.Boolean(
                    value = FhirR4Boolean(value = true),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )
    setContent { QuestionnaireBooleanChoice(questionnaireViewItem) }
    onNodeWithTag(YES_CHOICE_RADIO_BUTTON_TAG).performClick()
    waitUntil { answerHolder != null }

    answerHolder.shouldBeEmpty()
  }

  @Test
  fun yesSelected_clickYes_shouldClearRadioButtons() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  QuestionnaireResponse.Item.Answer.Value.Boolean(
                    value = FhirR4Boolean(value = true),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    setContent { QuestionnaireBooleanChoice(questionnaireViewItem) }
    onNodeWithTag(YES_CHOICE_RADIO_BUTTON_TAG).performClick()
    onNodeWithTag(YES_CHOICE_RADIO_BUTTON_TAG).assertIsNotSelected()
    onNodeWithTag(NO_CHOICE_RADIO_BUTTON_TAG).assertIsNotSelected()
  }

  @Test
  fun noSelected_clickNo_shouldClearAnswer() = runComposeUiTest {
    var answerHolder: List<QuestionnaireResponse.Item.Answer>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  QuestionnaireResponse.Item.Answer.Value.Boolean(
                    value = FhirR4Boolean(value = false),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )
    setContent { QuestionnaireBooleanChoice(questionnaireViewItem) }
    onNodeWithTag(NO_CHOICE_RADIO_BUTTON_TAG).performClick()
    waitUntil { answerHolder != null }

    answerHolder.shouldBeEmpty()
  }

  @Test
  fun noSelected_clickNo_shouldClearRadioButtons() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  QuestionnaireResponse.Item.Answer.Value.Boolean(
                    value = FhirR4Boolean(value = false),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    setContent { QuestionnaireBooleanChoice(questionnaireViewItem) }
    onNodeWithTag(NO_CHOICE_RADIO_BUTTON_TAG).performClick()

    onNodeWithTag(NO_CHOICE_RADIO_BUTTON_TAG).assertIsNotSelected()
    onNodeWithTag(YES_CHOICE_RADIO_BUTTON_TAG).assertIsNotSelected()
  }

  @Test
  fun displayValidationResult_error_shouldShowErrorMessage() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
          required = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "boolean-choice-item")),
        validationResult = Invalid(listOf("Missing answer for required field.")),
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireBooleanChoice(questionnaireViewItem) }
    onNodeWithTag(ERROR_TEXT_AT_HEADER_TEST_TAG)
      .assertTextEquals("Missing answer for required field.")
  }

  @Test
  fun displayValidationResult_noError_shouldShowNoErrorMessage() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  QuestionnaireResponse.Item.Answer.Value.Boolean(
                    value = FhirR4Boolean(value = true),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireBooleanChoice(questionnaireViewItem) }

    onNodeWithTag(ERROR_TEXT_AT_HEADER_TEST_TAG).assertDoesNotExist()
  }

  @Test
  fun bind_readOnly_shouldDisableView() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
          text = FhirR4String(value = "Question?"),
          readOnly = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  QuestionnaireResponse.Item.Answer.Value.Boolean(
                    value = FhirR4Boolean(value = true),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireBooleanChoice(questionnaireViewItem) }

    onNodeWithTag(YES_CHOICE_RADIO_BUTTON_TAG).assertIsNotEnabled()
    onNodeWithTag(NO_CHOICE_RADIO_BUTTON_TAG).assertIsNotEnabled()
  }

  @Test
  fun showAsterisk() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
          text = FhirR4String(value = "Question"),
          required = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "boolean-choice-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = true),
      )

    setContent { QuestionnaireBooleanChoice(questionnaireViewItem) }

    onNodeWithTag(QUESTION_HEADER_TAG).assertTextEquals("Question *")
  }

  @Test
  fun hideAsterisk() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
          text = FhirR4String(value = "Question"),
          required = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "boolean-choice-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = false),
      )

    setContent { QuestionnaireBooleanChoice(questionnaireViewItem) }
    onNodeWithTag(QUESTION_HEADER_TAG).assertTextEquals("Question")
  }

  @Test
  fun showsRequiredText() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
          required = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "boolean-choice-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = true),
      )

    setContent { QuestionnaireBooleanChoice(questionnaireViewItem) }

    onNodeWithTag(REQUIRED_OPTIONAL_HEADER_TEXT_TAG)
      .assertIsDisplayed()
      .assertTextEquals("Required")
  }

  @Test
  fun hideRequiredText() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
          required = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "boolean-choice-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = false),
      )

    setContent { QuestionnaireBooleanChoice(questionnaireViewItem) }

    onNodeWithTag(REQUIRED_OPTIONAL_HEADER_TEXT_TAG).assertDoesNotExist()
  }

  @Test
  fun showOptionalText() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
          text = FhirR4String(value = "Question"),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "boolean-choice-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = true),
      )

    setContent { QuestionnaireBooleanChoice(questionnaireViewItem) }
    onNodeWithTag(REQUIRED_OPTIONAL_HEADER_TEXT_TAG)
      .assertIsDisplayed()
      .assertTextEquals("Optional")
  }

  @Test
  fun hideOptionalText() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "boolean-choice-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
          text = FhirR4String(value = "Question"),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "boolean-choice-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = false),
      )

    setContent { QuestionnaireBooleanChoice(questionnaireViewItem) }
    onNodeWithTag(REQUIRED_OPTIONAL_HEADER_TEXT_TAG).assertDoesNotExist()
  }
}
