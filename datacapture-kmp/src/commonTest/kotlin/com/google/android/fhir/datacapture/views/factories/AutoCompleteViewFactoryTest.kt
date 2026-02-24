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
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasTextExactly
import androidx.compose.ui.test.isPopup
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.runComposeUiTest
import com.google.android.fhir.datacapture.extensions.EXTENSION_ITEM_CONTROL_SYSTEM
import com.google.android.fhir.datacapture.extensions.EXTENSION_ITEM_CONTROL_URL
import com.google.android.fhir.datacapture.extensions.FhirR4Boolean
import com.google.android.fhir.datacapture.extensions.FhirR4String
import com.google.android.fhir.datacapture.extensions.toQuestionnaireResponseItemAnswer
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.views.QuestionTextConfiguration
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.components.DROP_DOWN_ANSWER_MENU_ITEM_TAG
import com.google.android.fhir.datacapture.views.components.ERROR_TEXT_AT_HEADER_TEST_TAG
import com.google.android.fhir.datacapture.views.components.MULTI_AUTO_COMPLETE_INPUT_CHIP_TAG
import com.google.android.fhir.datacapture.views.components.MULTI_AUTO_COMPLETE_TEXT_FIELD_TAG
import com.google.android.fhir.datacapture.views.components.QUESTION_HEADER_TAG
import com.google.android.fhir.datacapture.views.components.REQUIRED_OPTIONAL_HEADER_TEXT_TAG
import com.google.fhir.model.r4.Canonical
import com.google.fhir.model.r4.Code
import com.google.fhir.model.r4.CodeableConcept
import com.google.fhir.model.r4.Coding
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import com.google.fhir.model.r4.Uri
import io.kotest.matchers.collections.shouldContainExactly
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class AutoCompleteViewFactoryTest {

  @Composable
  fun QuestionnaireAutoCompleteView(questionnaireViewItem: QuestionnaireViewItem) {
    QuestionnaireTheme { AutoCompleteViewFactory.Content(questionnaireViewItem) }
  }

  @Test
  fun shouldSetQuestionHeader() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "autocomplete-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
          extension =
            listOf(
              Extension(
                url = EXTENSION_ITEM_CONTROL_URL,
                value =
                  Extension.Value.CodeableConcept(
                    value =
                      CodeableConcept(
                        coding =
                          listOf(
                            Coding(
                              system = Uri(value = EXTENSION_ITEM_CONTROL_SYSTEM),
                              code = Code(value = "autocomplete"),
                            ),
                          ),
                      ),
                  ),
              ),
            ),
          text = FhirR4String(value = "Question"),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "autocomplete-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireAutoCompleteView(questionnaireViewItem) }

    onNodeWithTag(QUESTION_HEADER_TAG).assertTextEquals("Question")
  }

  @Test
  fun shouldHaveSingleAnswerChip() = runComposeUiTest {
    val questionnaireItem =
      Questionnaire.Item(
        linkId = FhirR4String(value = "autocomplete-item"),
        type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
        extension =
          listOf(
            Extension(
              url = EXTENSION_ITEM_CONTROL_URL,
              value =
                Extension.Value.CodeableConcept(
                  value =
                    CodeableConcept(
                      coding =
                        listOf(
                          Coding(
                            system = Uri(value = EXTENSION_ITEM_CONTROL_SYSTEM),
                            code = Code(value = "autocomplete"),
                          ),
                        ),
                    ),
                ),
            ),
          ),
        repeats = FhirR4Boolean(value = false),
        answerOption =
          listOf(
            Questionnaire.Item.AnswerOption(
              value =
                Questionnaire.Item.AnswerOption.Value.Coding(
                  value =
                    Coding(
                      code = Code(value = "test1-code"),
                      display = FhirR4String(value = "Test1 Code"),
                    ),
                ),
            ),
            Questionnaire.Item.AnswerOption(
              value =
                Questionnaire.Item.AnswerOption.Value.Coding(
                  value =
                    Coding(
                      code = Code(value = "test2-code"),
                      display = FhirR4String(value = "Test2 Code"),
                    ),
                ),
            ),
          ),
      )

    setContent {
      QuestionnaireAutoCompleteView(
        QuestionnaireViewItem(
          questionnaireItem,
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "autocomplete-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value =
                    QuestionnaireResponse.Item.Answer.Value.Coding(
                      value =
                        Coding(
                          code = Code(value = "test1-code"),
                          display = FhirR4String(value = "Test1 Code"),
                        ),
                    ),
                ),
              ),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )
    }

    onAllNodes(hasTestTag(MULTI_AUTO_COMPLETE_INPUT_CHIP_TAG)).assertCountEquals(1)
  }

  @Test
  fun shouldHaveTwoAnswerChipWithExternalValueSet() = runComposeUiTest {
    val answers =
      listOf(
        Questionnaire.Item.AnswerOption(
          value =
            Questionnaire.Item.AnswerOption.Value.Coding(
              value =
                Coding(
                  code = Code(value = "test1-code"),
                  display = FhirR4String(value = "Test1 Code"),
                ),
            ),
        ),
        Questionnaire.Item.AnswerOption(
          value =
            Questionnaire.Item.AnswerOption.Value.Coding(
              value =
                Coding(
                  code = Code(value = "test2-code"),
                  display = FhirR4String(value = "Test2 Code"),
                ),
            ),
        ),
      )

    val fakeAnswerValueSetResolver = { uri: String ->
      if (uri == "http://answwer-value-set-url") {
        answers
      } else {
        emptyList()
      }
    }

    val questionnaireItem =
      Questionnaire.Item(
        linkId = FhirR4String(value = "autocomplete-item"),
        type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
        extension =
          listOf(
            Extension(
              url = EXTENSION_ITEM_CONTROL_URL,
              value =
                Extension.Value.CodeableConcept(
                  value =
                    CodeableConcept(
                      coding =
                        listOf(
                          Coding(
                            system = Uri(value = EXTENSION_ITEM_CONTROL_SYSTEM),
                            code = Code(value = "autocomplete"),
                          ),
                        ),
                    ),
                ),
            ),
          ),
        repeats = FhirR4Boolean(value = true),
        answerValueSet = Canonical(value = "http://answwer-value-set-url"),
      )

    setContent {
      QuestionnaireAutoCompleteView(
        QuestionnaireViewItem(
          questionnaireItem,
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "autocomplete-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value =
                    QuestionnaireResponse.Item.Answer.Value.Coding(
                      value =
                        Coding(
                          code = Code(value = "test1-code"),
                          display = FhirR4String(value = "Test1 Code"),
                        ),
                    ),
                ),
                QuestionnaireResponse.Item.Answer(
                  value =
                    QuestionnaireResponse.Item.Answer.Value.Coding(
                      value =
                        Coding(
                          code = Code(value = "test2-code"),
                          display = FhirR4String(value = "Test2 Code"),
                        ),
                    ),
                ),
              ),
          ),
          enabledAnswerOptions =
            fakeAnswerValueSetResolver.invoke(questionnaireItem.answerValueSet?.value!!),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )
    }

    onAllNodes(hasTestTag(MULTI_AUTO_COMPLETE_INPUT_CHIP_TAG)).assertCountEquals(2)
  }

  @Test
  fun shouldHaveTwoAnswerChipWithAnswerOptionsHavingSameDisplayStringDifferentId() =
    runComposeUiTest {
      val answers =
        listOf(
          Questionnaire.Item.AnswerOption(
            value =
              Questionnaire.Item.AnswerOption.Value.Coding(
                value =
                  Coding(
                    code = Code(value = "test1-code"),
                    display = FhirR4String(value = "Test Code"),
                    id = "test1-code",
                  ),
              ),
          ),
          Questionnaire.Item.AnswerOption(
            value =
              Questionnaire.Item.AnswerOption.Value.Coding(
                value =
                  Coding(
                    system = Uri(value = "http://answers/test-codes"),
                    version = FhirR4String(value = "1.0"),
                    code = Code(value = "test2-code"),
                    display = FhirR4String(value = "Test Code"),
                  ),
              ),
          ),
        )

      val fakeAnswerValueSetResolver = { uri: String ->
        if (uri == "http://answwer-value-set-url") {
          answers
        } else {
          emptyList()
        }
      }
      val questionnaireItem =
        Questionnaire.Item(
          linkId = FhirR4String(value = "autocomplete-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
          extension =
            listOf(
              Extension(
                url = EXTENSION_ITEM_CONTROL_URL,
                value =
                  Extension.Value.CodeableConcept(
                    value =
                      CodeableConcept(
                        coding =
                          listOf(
                            Coding(
                              system = Uri(value = EXTENSION_ITEM_CONTROL_SYSTEM),
                              code = Code(value = "autocomplete"),
                            ),
                          ),
                      ),
                  ),
              ),
            ),
          repeats = FhirR4Boolean(value = true),
          answerValueSet = Canonical(value = "http://answwer-value-set-url"),
        )

      setContent {
        QuestionnaireAutoCompleteView(
          QuestionnaireViewItem(
            questionnaireItem,
            QuestionnaireResponse.Item(
              linkId = FhirR4String(value = "autocomplete-item"),
              answer =
                listOf(
                  QuestionnaireResponse.Item.Answer(
                    value =
                      QuestionnaireResponse.Item.Answer.Value.Coding(
                        value =
                          Coding(
                            code = Code(value = "test1-code"),
                            display = FhirR4String(value = "Test Code"),
                            id = "test1-code",
                          ),
                      ),
                  ),
                  QuestionnaireResponse.Item.Answer(
                    value =
                      QuestionnaireResponse.Item.Answer.Value.Coding(
                        value =
                          Coding(
                            system = Uri(value = "http://answers/test-codes"),
                            version = FhirR4String(value = "1.0"),
                            code = Code(value = "test2-code"),
                            display = FhirR4String(value = "Test Code"),
                          ),
                      ),
                  ),
                ),
            ),
            enabledAnswerOptions =
              fakeAnswerValueSetResolver.invoke(questionnaireItem.answerValueSet?.value!!),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
        )
      }

      onAllNodes(hasTestTag(MULTI_AUTO_COMPLETE_INPUT_CHIP_TAG)).assertCountEquals(2)
    }

  @Test
  fun shouldHaveSingleAnswerChipWithContainedAnswerValueSet() = runComposeUiTest {
    val answers =
      listOf(
        Questionnaire.Item.AnswerOption(
          value =
            Questionnaire.Item.AnswerOption.Value.Coding(
              value =
                Coding(
                  code = Code(value = "test1-code"),
                  display = FhirR4String(value = "Test1 Code"),
                ),
            ),
        ),
        Questionnaire.Item.AnswerOption(
          value =
            Questionnaire.Item.AnswerOption.Value.Coding(
              value =
                Coding(
                  code = Code(value = "test2-code"),
                  display = FhirR4String(value = "Test2 Code"),
                ),
            ),
        ),
      )

    val fakeAnswerValueSetResolver = { uri: String ->
      if (uri == "#ContainedValueSet") {
        answers
      } else {
        emptyList()
      }
    }
    val questionnaireItem =
      Questionnaire.Item(
        linkId = FhirR4String(value = "autocomplete-item"),
        type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
        extension =
          listOf(
            Extension(
              url = EXTENSION_ITEM_CONTROL_URL,
              value =
                Extension.Value.CodeableConcept(
                  value =
                    CodeableConcept(
                      coding =
                        listOf(
                          Coding(
                            system = Uri(value = EXTENSION_ITEM_CONTROL_SYSTEM),
                            code = Code(value = "autocomplete"),
                          ),
                        ),
                    ),
                ),
            ),
          ),
        repeats = FhirR4Boolean(value = false),
        answerValueSet = Canonical(value = "#ContainedValueSet"),
      )

    setContent {
      QuestionnaireAutoCompleteView(
        QuestionnaireViewItem(
          questionnaireItem,
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "autocomplete-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value =
                    QuestionnaireResponse.Item.Answer.Value.Coding(
                      value =
                        Coding(
                          code = Code(value = "test1-code"),
                          display = FhirR4String(value = "Test1 Code"),
                        ),
                    ),
                ),
              ),
          ),
          enabledAnswerOptions =
            fakeAnswerValueSetResolver.invoke(questionnaireItem.answerValueSet?.value!!),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )
    }

    onAllNodes(hasTestTag(MULTI_AUTO_COMPLETE_INPUT_CHIP_TAG)).assertCountEquals(1)
  }

  @Test
  fun noDisplayString_shouldShowCode() = runComposeUiTest {
    val answers =
      listOf(
        Questionnaire.Item.AnswerOption(
          value =
            Questionnaire.Item.AnswerOption.Value.Coding(
              value = Coding(code = Code(value = "test1-code")),
            ),
          initialSelected = FhirR4Boolean(value = true),
        ),
      )

    setContent {
      QuestionnaireAutoCompleteView(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "autocomplete-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
            extension =
              listOf(
                Extension(
                  url = EXTENSION_ITEM_CONTROL_URL,
                  value =
                    Extension.Value.CodeableConcept(
                      value =
                        CodeableConcept(
                          coding =
                            listOf(
                              Coding(
                                system = Uri(value = EXTENSION_ITEM_CONTROL_SYSTEM),
                                code = Code(value = "autocomplete"),
                              ),
                            ),
                        ),
                    ),
                ),
              ),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "autocomplete-item"),
            answer =
              answers.map { itemAnswerOption ->
                itemAnswerOption.toQuestionnaireResponseItemAnswer()
              },
          ),
          enabledAnswerOptions = answers,
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )
    }

    onAllNodes(hasTestTag(MULTI_AUTO_COMPLETE_INPUT_CHIP_TAG))
      .onFirst()
      .assertTextEquals("test1-code")
  }

  @Test
  fun shouldReturnFilteredDropDownMenuItems() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(false, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireAutoCompleteView(questionnaireViewItem) }

    onNodeWithTag(MULTI_AUTO_COMPLETE_TEXT_FIELD_TAG).performTextReplacement("Coding 1")
    onAllNodes(hasTestTag(DROP_DOWN_ANSWER_MENU_ITEM_TAG)).assertCountEquals(1)
  }

  @Test
  fun shouldAddDropDownValueSelectedForMultipleAnswersAutoCompleteTextView() = runComposeUiTest {
    var answerHolder: List<QuestionnaireResponse.Item.Answer>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(true, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions("Coding 1", "Coding 5"),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )
    setContent { QuestionnaireAutoCompleteView(questionnaireViewItem) }

    onNodeWithTag(MULTI_AUTO_COMPLETE_TEXT_FIELD_TAG).performTextReplacement("Coding 3")

    onNode(
        hasTestTag(DROP_DOWN_ANSWER_MENU_ITEM_TAG) and
          hasTextExactly("Coding 3") and
          hasAnyAncestor(isPopup()),
      )
      .assertIsDisplayed()
      .performClick()

    onNodeWithTag(MULTI_AUTO_COMPLETE_TEXT_FIELD_TAG).assertTextEquals("")

    waitUntil { answerHolder != null }

    answerHolder!!
      .map { itemAnswer -> itemAnswer.value?.asCoding()?.value?.display?.value }
      .shouldContainExactly("Coding 1", "Coding 5", "Coding 3")
  }

  @Test
  fun shouldSetCorrectNumberOfChipsForSelectedAnswers() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(true, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions("Coding 1", "Coding 5"),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    setContent { QuestionnaireAutoCompleteView(questionnaireViewItem) }
    onAllNodes(hasTestTag(MULTI_AUTO_COMPLETE_INPUT_CHIP_TAG)).assertCountEquals(2)
  }

  @Test
  fun displayValidationResult_error_shouldShowErrorMessage() = runComposeUiTest {
    setContent {
      QuestionnaireAutoCompleteView(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "autocomplete-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
            extension =
              listOf(
                Extension(
                  url = EXTENSION_ITEM_CONTROL_URL,
                  value =
                    Extension.Value.CodeableConcept(
                      value =
                        CodeableConcept(
                          coding =
                            listOf(
                              Coding(
                                system = Uri(value = EXTENSION_ITEM_CONTROL_SYSTEM),
                                code = Code(value = "autocomplete"),
                              ),
                            ),
                        ),
                    ),
                ),
              ),
            required = FhirR4Boolean(value = true),
          ),
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "autocomplete-item")),
          validationResult = Invalid(listOf("Missing answer for required field.")),
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )
    }

    onNodeWithTag(MULTI_AUTO_COMPLETE_TEXT_FIELD_TAG)
      .assert(
        SemanticsMatcher.expectValue(
          SemanticsProperties.Error,
          "Missing answer for required field.",
        ),
      )
  }

  @Test
  fun displayValidationResult_noError_shouldShowNoErrorMessage() = runComposeUiTest {
    setContent {
      QuestionnaireAutoCompleteView(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "autocomplete-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
            extension =
              listOf(
                Extension(
                  url = EXTENSION_ITEM_CONTROL_URL,
                  value =
                    Extension.Value.CodeableConcept(
                      value =
                        CodeableConcept(
                          coding =
                            listOf(
                              Coding(
                                system = Uri(value = EXTENSION_ITEM_CONTROL_SYSTEM),
                                code = Code(value = "autocomplete"),
                              ),
                            ),
                        ),
                    ),
                ),
              ),
            answerOption =
              listOf(
                Questionnaire.Item.AnswerOption(
                  value =
                    Questionnaire.Item.AnswerOption.Value.Coding(
                      value = Coding(display = FhirR4String(value = "display")),
                    ),
                ),
              ),
            required = FhirR4Boolean(value = true),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "autocomplete-item"),
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
          validationResult = Valid,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )
    }

    onNodeWithTag(MULTI_AUTO_COMPLETE_TEXT_FIELD_TAG)
      .assert(
        SemanticsMatcher.keyNotDefined(
          SemanticsProperties.Error,
        ),
      )
  }

  @Test
  fun hidesErrorTextviewInTheHeader() = runComposeUiTest {
    setContent {
      QuestionnaireAutoCompleteView(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "autocomplete-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
            extension =
              listOf(
                Extension(
                  url = EXTENSION_ITEM_CONTROL_URL,
                  value =
                    Extension.Value.CodeableConcept(
                      value =
                        CodeableConcept(
                          coding =
                            listOf(
                              Coding(
                                system = Uri(value = EXTENSION_ITEM_CONTROL_SYSTEM),
                                code = Code(value = "autocomplete"),
                              ),
                            ),
                        ),
                    ),
                ),
              ),
          ),
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "autocomplete-item")),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )
    }

    onNodeWithTag(ERROR_TEXT_AT_HEADER_TEST_TAG).assertDoesNotExist()
  }

  @Test
  fun showAsterisk() = runComposeUiTest {
    setContent {
      QuestionnaireAutoCompleteView(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "autocomplete-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
            extension =
              listOf(
                Extension(
                  url = EXTENSION_ITEM_CONTROL_URL,
                  value =
                    Extension.Value.CodeableConcept(
                      value =
                        CodeableConcept(
                          coding =
                            listOf(
                              Coding(
                                system = Uri(value = EXTENSION_ITEM_CONTROL_SYSTEM),
                                code = Code(value = "autocomplete"),
                              ),
                            ),
                        ),
                    ),
                ),
              ),
            text = FhirR4String(value = "Question"),
            required = FhirR4Boolean(value = true),
          ),
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "autocomplete-item")),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
          questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = true),
        ),
      )
    }

    onNodeWithTag(QUESTION_HEADER_TAG).assertTextEquals("Question *")
  }

  @Test
  fun hideAsterisk() = runComposeUiTest {
    setContent {
      QuestionnaireAutoCompleteView(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "autocomplete-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
            extension =
              listOf(
                Extension(
                  url = EXTENSION_ITEM_CONTROL_URL,
                  value =
                    Extension.Value.CodeableConcept(
                      value =
                        CodeableConcept(
                          coding =
                            listOf(
                              Coding(
                                system = Uri(value = EXTENSION_ITEM_CONTROL_SYSTEM),
                                code = Code(value = "autocomplete"),
                              ),
                            ),
                        ),
                    ),
                ),
              ),
            text = FhirR4String(value = "Question"),
            required = FhirR4Boolean(value = true),
          ),
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "autocomplete-item")),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
          questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = false),
        ),
      )
    }

    onNodeWithTag(QUESTION_HEADER_TAG).assertTextEquals("Question")
  }

  @Test
  fun showsRequiredText() = runComposeUiTest {
    setContent {
      QuestionnaireAutoCompleteView(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "autocomplete-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
            extension =
              listOf(
                Extension(
                  url = EXTENSION_ITEM_CONTROL_URL,
                  value =
                    Extension.Value.CodeableConcept(
                      value =
                        CodeableConcept(
                          coding =
                            listOf(
                              Coding(
                                system = Uri(value = EXTENSION_ITEM_CONTROL_SYSTEM),
                                code = Code(value = "autocomplete"),
                              ),
                            ),
                        ),
                    ),
                ),
              ),
            required = FhirR4Boolean(value = true),
          ),
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "autocomplete-item")),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
          questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = true),
        ),
      )
    }

    onNodeWithTag(REQUIRED_OPTIONAL_HEADER_TEXT_TAG)
      .assertIsDisplayed()
      .assertTextEquals("Required")
  }

  @Test
  fun hideRequiredText() = runComposeUiTest {
    setContent {
      QuestionnaireAutoCompleteView(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "autocomplete-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
            extension =
              listOf(
                Extension(
                  url = EXTENSION_ITEM_CONTROL_URL,
                  value =
                    Extension.Value.CodeableConcept(
                      value =
                        CodeableConcept(
                          coding =
                            listOf(
                              Coding(
                                system = Uri(value = EXTENSION_ITEM_CONTROL_SYSTEM),
                                code = Code(value = "autocomplete"),
                              ),
                            ),
                        ),
                    ),
                ),
              ),
            required = FhirR4Boolean(value = true),
          ),
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "autocomplete-item")),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
          questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = false),
        ),
      )
    }

    onNodeWithTag(REQUIRED_OPTIONAL_HEADER_TEXT_TAG).assertDoesNotExist()
  }

  @Test
  fun showsOptionalText() = runComposeUiTest {
    setContent {
      QuestionnaireAutoCompleteView(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "autocomplete-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
            extension =
              listOf(
                Extension(
                  url = EXTENSION_ITEM_CONTROL_URL,
                  value =
                    Extension.Value.CodeableConcept(
                      value =
                        CodeableConcept(
                          coding =
                            listOf(
                              Coding(
                                system = Uri(value = EXTENSION_ITEM_CONTROL_SYSTEM),
                                code = Code(value = "autocomplete"),
                              ),
                            ),
                        ),
                    ),
                ),
              ),
            text = FhirR4String(value = "Question"),
          ),
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "autocomplete-item")),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
          questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = true),
        ),
      )
    }

    onNodeWithTag(REQUIRED_OPTIONAL_HEADER_TEXT_TAG)
      .assertIsDisplayed()
      .assertTextEquals("Optional")
  }

  @Test
  fun hideOptionalText() = runComposeUiTest {
    setContent {
      QuestionnaireAutoCompleteView(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "autocomplete-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
            extension =
              listOf(
                Extension(
                  url = EXTENSION_ITEM_CONTROL_URL,
                  value =
                    Extension.Value.CodeableConcept(
                      value =
                        CodeableConcept(
                          coding =
                            listOf(
                              Coding(
                                system = Uri(value = EXTENSION_ITEM_CONTROL_SYSTEM),
                                code = Code(value = "autocomplete"),
                              ),
                            ),
                        ),
                    ),
                ),
              ),
            text = FhirR4String(value = "Question"),
          ),
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "autocomplete-item")),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
          questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = false),
        ),
      )
    }

    onNodeWithTag(REQUIRED_OPTIONAL_HEADER_TEXT_TAG).assertDoesNotExist()
  }

  private fun answerOptions(repeats: Boolean, vararg options: String) =
    Questionnaire.Item(
      linkId = FhirR4String(value = "autocomplete-item"),
      type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
      extension =
        listOf(
          Extension(
            url = EXTENSION_ITEM_CONTROL_URL,
            value =
              Extension.Value.CodeableConcept(
                value =
                  CodeableConcept(
                    coding =
                      listOf(
                        Coding(
                          system = Uri(value = EXTENSION_ITEM_CONTROL_SYSTEM),
                          code = Code(value = "autocomplete"),
                        ),
                      ),
                  ),
              ),
          ),
        ),
      answerOption =
        options.map { option ->
          Questionnaire.Item.AnswerOption(
            value =
              Questionnaire.Item.AnswerOption.Value.Coding(
                value =
                  Coding(
                    code = Code(value = option.replace(" ", "_")),
                    display = FhirR4String(value = option),
                  ),
              ),
          )
        },
      repeats = FhirR4Boolean(value = repeats),
    )

  private fun responseOptions(vararg options: String) =
    QuestionnaireResponse.Item(
      linkId = FhirR4String(value = "autocomplete-item"),
      answer =
        options.map { option ->
          QuestionnaireResponse.Item.Answer(
            value =
              QuestionnaireResponse.Item.Answer.Value.Coding(
                value =
                  Coding(
                    code = Code(value = option.replace(" ", "_")),
                    display = FhirR4String(value = option),
                  ),
              ),
          )
        },
    )
}
