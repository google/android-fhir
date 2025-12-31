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
import com.google.android.fhir.datacapture.extensions.DecimalAnswerValue
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
import com.google.android.fhir.datacapture.views.compose.UNIT_TEXT_TEST_TAG
import com.google.fhir.model.r4.Code
import com.google.fhir.model.r4.Coding
import com.google.fhir.model.r4.Decimal
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class EditTextDecimalViewFactoryTest {
  @Composable
  fun QuestionnaireEditTextDecimalView(questionnaireViewItem: QuestionnaireViewItem) {
    QuestionnaireTheme { EditTextDecimalViewFactory.Content(questionnaireViewItem) }
  }

  @Test
  fun shouldSetQuestionnaireHeader() = runComposeUiTest {
    setContent {
      QuestionnaireEditTextDecimalView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "decimal-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Decimal),
              text = FhirR4String(value = "Question?"),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "decimal-item")),
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
      QuestionnaireEditTextDecimalView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "decimal-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Decimal),
            ),
            QuestionnaireResponse.Item(
              linkId = FhirR4String(value = "decimal-item"),
              answer =
                listOf(
                  QuestionnaireResponse.Item.Answer(
                    value = DecimalAnswerValue(value = Decimal(value = "1.1".toBigDecimal())),
                  ),
                ),
            ),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
      )
    }

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("1.1")
  }

  @Test
  fun shouldSetInputTextToEmpty() = runComposeUiTest {
    var questionnaireViewItem by
      mutableStateOf(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "decimal-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Decimal),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "decimal-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value = DecimalAnswerValue(value = Decimal(value = "1.1".toBigDecimal())),
                ),
              ),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )

    setContent { QuestionnaireEditTextDecimalView(questionnaireViewItem) }

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("1.1")

    questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "decimal-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Decimal),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "decimal-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("")
  }

  @Test
  fun shouldSetUnitText() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "decimal-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Decimal),
          extension =
            listOf(
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/questionnaire-unit",
                value =
                  Extension.Value.Coding(
                    value = Coding(code = Code(value = "kg")),
                  ),
              ),
            ),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "decimal-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value = DecimalAnswerValue(value = Decimal(value = "1.1".toBigDecimal())),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent {
      QuestionnaireEditTextDecimalView(
        questionnaireViewItem,
      )
    }

    onNodeWithTag(UNIT_TEXT_TEST_TAG).assertIsDisplayed().assertTextEquals("kg")
  }

  @Test
  fun shouldClearUnitText() = runComposeUiTest {
    var questionnaireViewItem by
      mutableStateOf(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "decimal-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Decimal),
            extension =
              listOf(
                Extension(
                  url = "http://hl7.org/fhir/StructureDefinition/questionnaire-unit",
                  value =
                    Extension.Value.Coding(
                      value = Coding(code = Code("kg")),
                    ),
                ),
              ),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "decimal-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value = DecimalAnswerValue(value = Decimal(value = "1.1".toBigDecimal())),
                ),
              ),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )

    setContent { QuestionnaireEditTextDecimalView(questionnaireViewItem) }
    questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "decimal-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Decimal),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "decimal-item"),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("")
    onNodeWithTag(UNIT_TEXT_TEST_TAG).assertDoesNotExist()
  }

  @Test
  fun shouldSetQuestionnaireResponseItemAnswerIfTextIsValid() = runComposeUiTest {
    var answers: List<QuestionnaireResponse.Item.Answer>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "decimal-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Decimal),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "decimal-item"),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result, _ -> answers = result },
      )

    setContent { QuestionnaireEditTextDecimalView(questionnaireViewItem) }
    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextReplacement("1.1")
    waitUntil { answers != null }

    answers!!.single().value?.asDecimal()?.value?.value.shouldBe(1.1.toBigDecimal())
  }

  @Test
  fun shouldSetQuestionnaireResponseItemAnswerToEmpty() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "decimal-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Decimal),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "decimal-item"),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    setContent { QuestionnaireEditTextDecimalView(questionnaireViewItem) }
    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextReplacement("")
    waitForIdle()

    questionnaireViewItem.answers.shouldBeEmpty()
  }

  @Test
  fun shouldSetDraftAnswerIfTextIsInvalid() = runComposeUiTest {
    var draftAnswer: Any? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "decimal-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Decimal),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "decimal-item"),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, result -> draftAnswer = result },
      )
    setContent { QuestionnaireEditTextDecimalView(questionnaireViewItem) }
    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextReplacement("1.1.1.1")
    waitUntil { draftAnswer != null }

    (draftAnswer as String).shouldBe("1.1.1.1")
  }

  @Test
  fun displayValidationResultShouldShowNoErrorMessage() = runComposeUiTest {
    setContent {
      QuestionnaireEditTextDecimalView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "decimal-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Decimal),
              extension =
                listOf(
                  Extension(
                    url = "http://hl7.org/fhir/StructureDefinition/minValue",
                    value = Extension.Value.Decimal(value = Decimal(value = "2.2".toBigDecimal())),
                  ),
                  Extension(
                    url = "http://hl7.org/fhir/StructureDefinition/maxValue",
                    value = Extension.Value.Decimal(value = Decimal(value = "4.4".toBigDecimal())),
                  ),
                ),
            ),
            QuestionnaireResponse.Item(
              linkId = FhirR4String(value = "decimal-item"),
              answer =
                listOf(
                  QuestionnaireResponse.Item.Answer(
                    value = DecimalAnswerValue(value = Decimal(value = "3.3".toBigDecimal())),
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
      QuestionnaireEditTextDecimalView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "decimal-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Decimal),
              extension =
                listOf(
                  Extension(
                    url = "http://hl7.org/fhir/StructureDefinition/minValue",
                    value = Extension.Value.Decimal(value = Decimal(value = "2.1".toBigDecimal())),
                  ),
                  Extension(
                    url = "http://hl7.org/fhir/StructureDefinition/maxValue",
                    value = Extension.Value.Decimal(value = Decimal(value = "4.2".toBigDecimal())),
                  ),
                ),
            ),
            QuestionnaireResponse.Item(
              linkId = FhirR4String(value = "decimal-item"),
              answer =
                listOf(
                  QuestionnaireResponse.Item.Answer(
                    value = DecimalAnswerValue(value = Decimal(value = "1.1".toBigDecimal())),
                  ),
                ),
            ),
            validationResult = Invalid(listOf("Minimum value allowed is:2.1")),
            answersChangedCallback = { _, _, _, _ -> },
          ),
      )
    }

    onNodeWithContentDescription("Error").assertIsDisplayed()
    onNodeWithText("Minimum value allowed is:2.1").assertIsDisplayed()
  }

  @Test
  fun hidesErrorTextviewInTheHeader() = runComposeUiTest {
    setContent {
      QuestionnaireEditTextDecimalView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "decimal-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Decimal),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "decimal-item")),
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
      QuestionnaireEditTextDecimalView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "decimal-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Decimal),
              readOnly = FhirR4Boolean(value = true),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "decimal-item")),
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
      QuestionnaireEditTextDecimalView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "decimal-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Decimal),
              text = FhirR4String(value = "Question?"),
              required = FhirR4Boolean(value = true),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "decimal-item")),
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
      QuestionnaireEditTextDecimalView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "decimal-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Decimal),
              text = FhirR4String(value = "Question?"),
              required = FhirR4Boolean(value = true),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "decimal-item")),
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
      QuestionnaireEditTextDecimalView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "decimal-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Decimal),
              required = FhirR4Boolean(value = true),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "decimal-item")),
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
      QuestionnaireEditTextDecimalView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "decimal-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Decimal),
              required = FhirR4Boolean(value = true),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "decimal-item")),
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
      QuestionnaireEditTextDecimalView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "decimal-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Decimal),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "decimal-item")),
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
      QuestionnaireEditTextDecimalView(
        questionnaireViewItem =
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "decimal-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Decimal),
            ),
            QuestionnaireResponse.Item(linkId = FhirR4String(value = "decimal-item")),
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
            linkId = FhirR4String(value = "decimal-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Decimal),
          ),
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "decimal-item")),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
          draftAnswer = "1.1.1.1",
        ),
      )

    setContent { QuestionnaireEditTextDecimalView(questionnaireViewItem) }

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG, useUnmergedTree = true).assertTextEquals("1.1.1.1")

    questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "decimal-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Decimal),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "decimal-item")),
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
            linkId = FhirR4String(value = "decimal-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Decimal),
            text = FhirR4String(value = "Weight"),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "decimal-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value = DecimalAnswerValue(value = Decimal(value = "124.5".toBigDecimal())),
                ),
              ),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )

    setContent { QuestionnaireEditTextDecimalView(questionnaireViewItem) }
    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("124.5")

    questionnaireViewItem =
      questionnaireViewItem.copy(
        questionnaireResponseItem =
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "decimal-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value = DecimalAnswerValue(value = Decimal(value = "124.578".toBigDecimal())),
                ),
              ),
          ),
      )
    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("124.578")
  }
}
