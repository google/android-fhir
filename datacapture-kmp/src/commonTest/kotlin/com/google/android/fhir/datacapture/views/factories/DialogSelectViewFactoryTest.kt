/*
 * Copyright 2023-2026 Google LLC
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
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.text.AnnotatedString
import com.google.android.fhir.datacapture.extensions.DisplayItemControlType
import com.google.android.fhir.datacapture.extensions.EXTENSION_DIALOG_URL_ANDROID_FHIR
import com.google.android.fhir.datacapture.extensions.EXTENSION_ITEM_CONTROL_SYSTEM
import com.google.android.fhir.datacapture.extensions.EXTENSION_ITEM_CONTROL_URL
import com.google.android.fhir.datacapture.extensions.EXTENSION_OPTION_EXCLUSIVE_URL
import com.google.android.fhir.datacapture.extensions.FhirR4Boolean
import com.google.android.fhir.datacapture.extensions.FhirR4String
import com.google.android.fhir.datacapture.extensions.ItemControlTypes
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionTextConfiguration
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.ERROR_TEXT_AT_HEADER_TEST_TAG
import com.google.android.fhir.datacapture.views.compose.OPTION_CHOICE_LIST_TAG
import com.google.android.fhir.datacapture.views.compose.OPTION_CHOICE_TAG
import com.google.android.fhir.datacapture.views.compose.OTHER_OPTION_TEXT_FIELD_TAG
import com.google.android.fhir.datacapture.views.compose.QUESTION_HEADER_TAG
import com.google.fhir.model.r4.Code
import com.google.fhir.model.r4.CodeableConcept
import com.google.fhir.model.r4.Coding
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import com.google.fhir.model.r4.Uri
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainInOrder
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class DialogSelectViewFactoryTest {

  @Composable
  fun QuestionnaireDialogSelect(questionnaireViewItem: QuestionnaireViewItem) {
    QuestionnaireTheme { DialogSelectViewFactory.Content(questionnaireViewItem) }
  }

  @Test
  fun multipleChoice_selectMultiple_clickSave_shouldSaveMultipleOptions() = runComposeUiTest {
    var answerHolder: List<QuestionnaireResponse.Item.Answer>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(true, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )

    setContent { QuestionnaireDialogSelect(questionnaireViewItem) }

    // Click to open the dialog
    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    // Select options in the dialog
    onNodeWithText("Coding 1").performClick()
    onNodeWithText("Coding 3").performClick()
    onNodeWithText("Coding 5").performClick()
    onNodeWithText("Save").performClick()

    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
      .assertTextEquals("Coding 1, Coding 3, Coding 5")
      .assertIsDisplayed()

    answerHolder!!
      .mapNotNull { it.value?.asCoding()?.value?.display?.value }
      .shouldContainInOrder("Coding 1", "Coding 3", "Coding 5")
  }

  @Test
  fun multipleChoice_selectMultiple_selectExclusive_clickSave_shouldSaveOnlyExclusiveOption() =
    runComposeUiTest {
      var answerHolder: List<QuestionnaireResponse.Item.Answer>? = null
      val questionnaireViewItem =
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "dialog-select-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
            extension = listOf(Extension(url = EXTENSION_DIALOG_URL_ANDROID_FHIR)),
            repeats = FhirR4Boolean(value = true),
            answerOption =
              arrayOf("Coding 1", "Coding 2", "Coding 3").map { option ->
                Questionnaire.Item.AnswerOption(
                  value =
                    Questionnaire.Item.AnswerOption.Value.Coding(
                      value = Coding(display = FhirR4String(value = option)),
                    ),
                )
              } +
                listOf(
                  Questionnaire.Item.AnswerOption(
                    value =
                      Questionnaire.Item.AnswerOption.Value.Coding(
                        value = Coding(display = FhirR4String(value = "Coding Exclusive")),
                      ),
                    extension =
                      listOf(
                        Extension(
                          url = EXTENSION_OPTION_EXCLUSIVE_URL,
                          value = Extension.Value.Boolean(value = FhirR4Boolean(value = true)),
                        ),
                      ),
                  ),
                ),
          ),
          responseOptions(),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
        )

      setContent { QuestionnaireDialogSelect(questionnaireViewItem) }

      onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

      onNodeWithText("Coding 1").performClick()
      onNodeWithText("Coding 3").performClick()
      onNodeWithText("Coding Exclusive").performClick()
      onNodeWithText("Save").performClick()

      onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
        .assertTextEquals("Coding Exclusive")
        .assertIsDisplayed()

      answerHolder!!
        .mapNotNull { it.value?.asCoding()?.value?.display?.value }
        .shouldContainInOrder("Coding Exclusive")
    }

  @Test
  fun multipleChoice_selectExclusive_selectMultiple_clickSave_shouldSaveWithoutExclusiveOption() =
    runComposeUiTest {
      var answerHolder: List<QuestionnaireResponse.Item.Answer>? = null
      val questionnaireViewItem =
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "dialog-select-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
            extension = listOf(Extension(url = EXTENSION_DIALOG_URL_ANDROID_FHIR)),
            repeats = FhirR4Boolean(value = true),
            answerOption =
              arrayOf("Coding 1", "Coding 2", "Coding 3").map { option ->
                Questionnaire.Item.AnswerOption(
                  value =
                    Questionnaire.Item.AnswerOption.Value.Coding(
                      value = Coding(display = FhirR4String(value = option)),
                    ),
                )
              } +
                listOf(
                  Questionnaire.Item.AnswerOption(
                    value =
                      Questionnaire.Item.AnswerOption.Value.Coding(
                        value = Coding(display = FhirR4String(value = "Coding Exclusive")),
                      ),
                    extension =
                      listOf(
                        Extension(
                          url = EXTENSION_OPTION_EXCLUSIVE_URL,
                          value = Extension.Value.Boolean(value = FhirR4Boolean(value = true)),
                        ),
                      ),
                  ),
                ),
          ),
          responseOptions(),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
        )

      setContent { QuestionnaireDialogSelect(questionnaireViewItem) }

      onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

      onNodeWithText("Coding Exclusive").performClick()
      onNodeWithText("Coding 1").performClick()
      onNodeWithText("Coding 3").performClick()
      onNodeWithText("Save").performClick()

      onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
        .assertTextEquals("Coding 1, Coding 3")
        .assertIsDisplayed()

      answerHolder!!
        .mapNotNull { it.value?.asCoding()?.value?.display?.value }
        .shouldContainInOrder("Coding 1", "Coding 3")
    }

  @Test
  fun multipleChoice_multipleOptionExclusive_selectMultiple_selectExclusive1_selectExclusive2_clickSave_shouldSaveOnlyLastSelectedExclusiveOption() =
    runComposeUiTest {
      var answerHolder: List<QuestionnaireResponse.Item.Answer>? = null
      val questionnaireViewItem =
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "dialog-select-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
            extension = listOf(Extension(url = EXTENSION_DIALOG_URL_ANDROID_FHIR)),
            repeats = FhirR4Boolean(value = true),
            answerOption =
              arrayOf("Coding 1", "Coding 2", "Coding 3").map { option ->
                Questionnaire.Item.AnswerOption(
                  value =
                    Questionnaire.Item.AnswerOption.Value.Coding(
                      value = Coding(display = FhirR4String(value = option)),
                    ),
                )
              } +
                listOf(
                  Questionnaire.Item.AnswerOption(
                    value =
                      Questionnaire.Item.AnswerOption.Value.Coding(
                        value = Coding(display = FhirR4String(value = "Coding Exclusive 1")),
                      ),
                    extension =
                      listOf(
                        Extension(
                          url = EXTENSION_OPTION_EXCLUSIVE_URL,
                          value = Extension.Value.Boolean(value = FhirR4Boolean(value = true)),
                        ),
                      ),
                  ),
                  Questionnaire.Item.AnswerOption(
                    value =
                      Questionnaire.Item.AnswerOption.Value.Coding(
                        value = Coding(display = FhirR4String(value = "Coding Exclusive 2")),
                      ),
                    extension =
                      listOf(
                        Extension(
                          url = EXTENSION_OPTION_EXCLUSIVE_URL,
                          value = Extension.Value.Boolean(value = FhirR4Boolean(value = true)),
                        ),
                      ),
                  ),
                ),
          ),
          responseOptions(),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
        )

      setContent { QuestionnaireDialogSelect(questionnaireViewItem) }

      onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

      onNodeWithText("Coding 1").performClick()
      onNodeWithText("Coding 3").performClick()
      onNodeWithText("Coding Exclusive 1").performClick()
      onNodeWithText("Coding Exclusive 2").performClick()
      onNodeWithText("Save").performClick()

      onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
        .assertTextEquals("Coding Exclusive 2")
        .assertIsDisplayed()

      answerHolder!!
        .mapNotNull { it.value?.asCoding()?.value?.display?.value }
        .shouldContainInOrder("Coding Exclusive 2")
    }

  @Test
  fun multipleChoice_multipleOptionExclusive_selectExclusive1_selectExclusive2_selectMultiple_clickSave_shouldSaveWithoutAnyExclusiveOption() =
    runComposeUiTest {
      var answerHolder: List<QuestionnaireResponse.Item.Answer>? = null
      val questionnaireViewItem =
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "dialog-select-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
            extension = listOf(Extension(url = EXTENSION_DIALOG_URL_ANDROID_FHIR)),
            repeats = FhirR4Boolean(value = true),
            answerOption =
              arrayOf("Coding 1", "Coding 2", "Coding 3").map { option ->
                Questionnaire.Item.AnswerOption(
                  value =
                    Questionnaire.Item.AnswerOption.Value.Coding(
                      value = Coding(display = FhirR4String(value = option)),
                    ),
                )
              } +
                listOf(
                  Questionnaire.Item.AnswerOption(
                    value =
                      Questionnaire.Item.AnswerOption.Value.Coding(
                        value = Coding(display = FhirR4String(value = "Coding Exclusive 1")),
                      ),
                    extension =
                      listOf(
                        Extension(
                          url = EXTENSION_OPTION_EXCLUSIVE_URL,
                          value = Extension.Value.Boolean(value = FhirR4Boolean(value = true)),
                        ),
                      ),
                  ),
                  Questionnaire.Item.AnswerOption(
                    value =
                      Questionnaire.Item.AnswerOption.Value.Coding(
                        value = Coding(display = FhirR4String(value = "Coding Exclusive 2")),
                      ),
                    extension =
                      listOf(
                        Extension(
                          url = EXTENSION_OPTION_EXCLUSIVE_URL,
                          value = Extension.Value.Boolean(value = FhirR4Boolean(value = true)),
                        ),
                      ),
                  ),
                ),
          ),
          responseOptions(),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
        )

      setContent { QuestionnaireDialogSelect(questionnaireViewItem) }

      onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

      onNodeWithText("Coding Exclusive 1").performClick()
      onNodeWithText("Coding Exclusive 2").performClick()
      onNodeWithText("Coding 1").performClick()
      onNodeWithText("Coding 3").performClick()
      onNodeWithText("Save").performClick()

      onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
        .assertTextEquals("Coding 1, Coding 3")
        .assertIsDisplayed()

      answerHolder!!
        .mapNotNull { it.value?.asCoding()?.value?.display?.value }
        .shouldContainInOrder("Coding 1", "Coding 3")
    }

  @Test
  fun multipleChoice_SelectNothing_clickSave_shouldSaveNothing() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(true, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDialogSelect(questionnaireViewItem) }

    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    onNodeWithText("Save").performClick()

    // When nothing is selected, the field should be empty
    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).assertTextEquals("").assertIsDisplayed()
    questionnaireViewItem.answers.shouldBeEmpty()
  }

  @Test
  fun multipleChoice_selectMultiple_clickCancel_shouldSaveNothing() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(true, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDialogSelect(questionnaireViewItem) }

    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    onNodeWithText("Coding 3").performClick()
    onNodeWithText("Coding 1").performClick()
    onNodeWithText("Cancel").performClick()

    // When cancelled, nothing should be saved
    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).assertTextEquals("").assertIsDisplayed()
    questionnaireViewItem.answers.shouldBeEmpty()
  }

  @Test
  fun shouldSelectSingleOptionOnChangeInOptionFromDropDown() = runComposeUiTest {
    var answerHolder: List<QuestionnaireResponse.Item.Answer>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(false, "Coding 1", "Coding 2", "Coding 3"),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )

    setContent { QuestionnaireDialogSelect(questionnaireViewItem) }

    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    onNodeWithText("Coding 2").performClick()
    onNodeWithText("Coding 1").performClick()
    onNodeWithText("Save").performClick()

    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).assertTextEquals("Coding 1").assertIsDisplayed()
    answerHolder!!
      .mapNotNull { it.value?.asCoding()?.value?.display?.value }
      .shouldContainInOrder("Coding 1")
  }

  @Test
  fun singleOption_select_clickSave_shouldSaveSingleOption() = runComposeUiTest {
    var answerHolder: List<QuestionnaireResponse.Item.Answer>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(false, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )

    setContent { QuestionnaireDialogSelect(questionnaireViewItem) }

    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    onNodeWithText("Coding 2").performClick()
    onNodeWithText("Save").performClick()

    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).assertTextEquals("Coding 2")
    answerHolder!!
      .mapNotNull { it.value?.asCoding()?.value?.display?.value }
      .shouldContainInOrder("Coding 2")
  }

  @Test
  fun singleOption_selectNothing_clickSave_shouldSaveNothing() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(false, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDialogSelect(questionnaireViewItem) }

    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()
    onNodeWithText("Save").performClick()

    // When nothing is selected, the field should be empty
    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).assertTextEquals("").assertIsDisplayed()
    questionnaireViewItem.answers.shouldBeEmpty()
  }

  @Test
  fun bindView_setHintText() = runComposeUiTest {
    val hintItem =
      Questionnaire.Item(
        linkId = FhirR4String(value = "dialog-hint"),
        type = Enumeration(value = Questionnaire.QuestionnaireItemType.Display),
        text = FhirR4String(value = "Select code"),
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
                            code = Code(value = DisplayItemControlType.FLYOVER.extensionCode),
                          ),
                        ),
                    ),
                ),
            ),
          ),
      )

    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "dialog-select-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
          extension = listOf(Extension(url = EXTENSION_DIALOG_URL_ANDROID_FHIR)),
          repeats = FhirR4Boolean(value = true),
          answerOption =
            arrayOf("Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5").map { option ->
              Questionnaire.Item.AnswerOption(
                value =
                  Questionnaire.Item.AnswerOption.Value.Coding(
                    value = Coding(display = FhirR4String(value = option)),
                  ),
              )
            },
          item = listOf(hintItem),
        ),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        enabledDisplayItems = listOf(hintItem),
      )
    setContent { QuestionnaireDialogSelect(questionnaireViewItem) }

    onNodeWithText("Select code").assertIsDisplayed()
  }

  @Test
  fun singleOption_select_clickCancel_shouldSaveNothing() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(false, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDialogSelect(questionnaireViewItem) }

    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    onNodeWithText("Coding 2").performClick()
    onNodeWithText("Cancel").performClick()

    // When cancelled, nothing should be saved
    questionnaireViewItem.answers.shouldBeEmpty()
  }

  @Test
  fun selectOther_shouldScrollDownToShowAddAnotherAnswer() = runComposeUiTest {
    val questionnaireItem =
      Questionnaire.Item(
        linkId = FhirR4String(value = "dialog-select-item"),
        type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
        extension = listOf(Extension(url = EXTENSION_DIALOG_URL_ANDROID_FHIR), openChoiceType),
        repeats = FhirR4Boolean(value = true),
        answerOption =
          arrayOf(
              "Coding 1",
              "Coding 2",
              "Coding 3",
              "Coding 4",
              "Coding 5",
              "Coding 6",
              "Coding 7",
              "Coding 8",
            )
            .map { option ->
              Questionnaire.Item.AnswerOption(
                value =
                  Questionnaire.Item.AnswerOption.Value.Coding(
                    value = Coding(display = FhirR4String(value = option)),
                  ),
              )
            },
      )

    val questionnaireViewItem =
      QuestionnaireViewItem(
        questionnaireItem,
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDialogSelect(questionnaireViewItem) }

    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    // Select "Other" option
    val otherText = "Other"
    onNodeWithTag(OPTION_CHOICE_LIST_TAG).performScrollToNode(hasText(otherText))
    onNodeWithText(otherText).performClick()

    // "Add Another" button should be displayed in multi-select mode
    onNodeWithText("Add another answer").assertIsDisplayed()
  }

  @Test
  fun unselectOther_shouldHideAddAnotherAnswer() = runComposeUiTest {
    val questionnaireItem =
      Questionnaire.Item(
        linkId = FhirR4String(value = "dialog-select-item"),
        type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
        extension = listOf(Extension(url = EXTENSION_DIALOG_URL_ANDROID_FHIR), openChoiceType),
        repeats = FhirR4Boolean(value = true),
        answerOption =
          arrayOf(
              "Coding 1",
              "Coding 2",
              "Coding 3",
              "Coding 4",
              "Coding 5",
              "Coding 6",
              "Coding 7",
              "Coding 8",
            )
            .map { option ->
              Questionnaire.Item.AnswerOption(
                value =
                  Questionnaire.Item.AnswerOption.Value.Coding(
                    value = Coding(display = FhirR4String(value = option)),
                  ),
              )
            },
      )

    val questionnaireViewItem =
      QuestionnaireViewItem(
        questionnaireItem,
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDialogSelect(questionnaireViewItem) }

    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    // Select and then unselect "Other" option
    val otherText = "Other"
    onNodeWithTag(OPTION_CHOICE_LIST_TAG).performScrollToNode(hasText(otherText))
    onNodeWithText(otherText).performClick()
    onNodeWithText(otherText).performClick()

    // "Add Another" button should not be displayed when "Other" is unselected
    onNodeWithText("Add another answer").assertDoesNotExist()
  }

  @Test
  fun clickAddAnotherAnswer_shouldScrollDownToShowAddAnotherAnswer() = runComposeUiTest {
    val questionnaireItem =
      Questionnaire.Item(
        linkId = FhirR4String(value = "dialog-select-item"),
        type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
        extension = listOf(Extension(url = EXTENSION_DIALOG_URL_ANDROID_FHIR), openChoiceType),
        repeats = FhirR4Boolean(value = true),
        answerOption =
          arrayOf(
              "Coding 1",
              "Coding 2",
              "Coding 3",
              "Coding 4",
              "Coding 5",
              "Coding 6",
              "Coding 7",
              "Coding 8",
            )
            .map { option ->
              Questionnaire.Item.AnswerOption(
                value =
                  Questionnaire.Item.AnswerOption.Value.Coding(
                    value = Coding(display = FhirR4String(value = option)),
                  ),
              )
            },
      )

    val questionnaireViewItem =
      QuestionnaireViewItem(
        questionnaireItem,
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDialogSelect(questionnaireViewItem) }

    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    // Select "Other" option
    val otherText = "Other"
    onNodeWithTag(OPTION_CHOICE_LIST_TAG).performScrollToNode(hasText(otherText))
    onNodeWithText(otherText).performClick()

    // Click "Add Another" button
    val addAnotherText = "Add another answer"
    onNodeWithTag(OPTION_CHOICE_LIST_TAG).performScrollToNode(hasText(addAnotherText))
    onNodeWithText(addAnotherText).performClick()

    // "Add Another" button should still be displayed after clicking
    onNodeWithText(addAnotherText).assertIsDisplayed()
  }

  @Test
  fun selectOther_selectExclusive_shouldHideAddAnotherAnswer() = runComposeUiTest {
    val questionnaireItem =
      Questionnaire.Item(
        linkId = FhirR4String(value = "dialog-select-item"),
        type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
        extension = listOf(Extension(url = EXTENSION_DIALOG_URL_ANDROID_FHIR), openChoiceType),
        repeats = FhirR4Boolean(value = true),
        answerOption =
          arrayOf(
              "Coding 1",
              "Coding 2",
              "Coding 3",
              "Coding 4",
              "Coding 5",
              "Coding 6",
              "Coding 7",
              "Coding 8",
            )
            .map { option ->
              Questionnaire.Item.AnswerOption(
                value =
                  Questionnaire.Item.AnswerOption.Value.Coding(
                    value = Coding(display = FhirR4String(value = option)),
                  ),
              )
            } +
            listOf(
              Questionnaire.Item.AnswerOption(
                value =
                  Questionnaire.Item.AnswerOption.Value.Coding(
                    value = Coding(display = FhirR4String(value = "Coding Exclusive")),
                  ),
                extension =
                  listOf(
                    Extension(
                      url = EXTENSION_OPTION_EXCLUSIVE_URL,
                      value = Extension.Value.Boolean(value = FhirR4Boolean(value = true)),
                    ),
                  ),
              ),
            ),
      )

    val questionnaireViewItem =
      QuestionnaireViewItem(
        questionnaireItem,
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDialogSelect(questionnaireViewItem) }

    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    // Select "Other" option
    val otherText = "Other"
    onNodeWithTag(OPTION_CHOICE_LIST_TAG).performScrollToNode(hasText(otherText))
    onNodeWithText(otherText).performClick()

    // Select exclusive option
    onNodeWithText("Coding Exclusive").performClick()

    // "Add Another" button should not be displayed when exclusive option is selected
    onNodeWithText("Add another answer").assertDoesNotExist()
  }

  @Test
  fun selectOther_clickAddAnotherAnswer_selectExclusive_shouldHideAddAnotherAnswerWithEditText() =
    runComposeUiTest {
      val questionnaireItem =
        Questionnaire.Item(
          linkId = FhirR4String(value = "dialog-select-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
          extension = listOf(Extension(url = EXTENSION_DIALOG_URL_ANDROID_FHIR), openChoiceType),
          repeats = FhirR4Boolean(value = true),
          answerOption =
            arrayOf(
                "Coding 1",
                "Coding 2",
                "Coding 3",
                "Coding 4",
                "Coding 5",
                "Coding 6",
                "Coding 7",
                "Coding 8",
              )
              .map { option ->
                Questionnaire.Item.AnswerOption(
                  value =
                    Questionnaire.Item.AnswerOption.Value.Coding(
                      value = Coding(display = FhirR4String(value = option)),
                    ),
                )
              } +
              listOf(
                Questionnaire.Item.AnswerOption(
                  value =
                    Questionnaire.Item.AnswerOption.Value.Coding(
                      value = Coding(display = FhirR4String(value = "Coding Exclusive")),
                    ),
                  extension =
                    listOf(
                      Extension(
                        url = EXTENSION_OPTION_EXCLUSIVE_URL,
                        value = Extension.Value.Boolean(value = FhirR4Boolean(value = true)),
                      ),
                    ),
                ),
              ),
        )

      val questionnaireViewItem =
        QuestionnaireViewItem(
          questionnaireItem,
          responseOptions(),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        )

      setContent { QuestionnaireDialogSelect(questionnaireViewItem) }

      onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

      // Select "Other" option
      val otherText = "Other"
      onNodeWithTag(OPTION_CHOICE_LIST_TAG).performScrollToNode(hasText(otherText))
      onNodeWithText(otherText).performClick()

      // Click "Add Another" button
      onNodeWithText("Add another answer").performClick()

      // Select exclusive option
      onNodeWithText("Coding Exclusive").performClick()

      // "Add Another" button and edit text should not be displayed when exclusive option is
      // selected
      onAllNodes(hasTestTag(OTHER_OPTION_TEXT_FIELD_TAG)).assertCountEquals(0)
      onNodeWithText("Add another answer").assertDoesNotExist()
    }

  @Test
  fun shouldHideErrorTextviewInHeader() = runComposeUiTest {
    val questionnaireItem =
      Questionnaire.Item(
        linkId = FhirR4String(value = "dialog-select-item"),
        type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
        extension = listOf(Extension(url = EXTENSION_DIALOG_URL_ANDROID_FHIR), openChoiceType),
        repeats = FhirR4Boolean(value = true),
        answerOption =
          arrayOf("Coding 1").map { option ->
            Questionnaire.Item.AnswerOption(
              value =
                Questionnaire.Item.AnswerOption.Value.Coding(
                  value = Coding(display = FhirR4String(value = option)),
                ),
            )
          },
      )

    val questionnaireViewItem =
      QuestionnaireViewItem(
        questionnaireItem,
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "dialog-select-item"),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDialogSelect(questionnaireViewItem) }
    onNodeWithTag(ERROR_TEXT_AT_HEADER_TEST_TAG).assertDoesNotExist()
  }

  @Test
  fun show_asterisk() = runComposeUiTest {
    setContent {
      QuestionnaireDialogSelect(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "dialog-select-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
            extension = listOf(Extension(url = EXTENSION_DIALOG_URL_ANDROID_FHIR)),
            text = FhirR4String(value = "Question?"),
            required = FhirR4Boolean(value = true),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "dialog-select-item"),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
          questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = true),
        ),
      )
    }

    onNodeWithTag(QUESTION_HEADER_TAG).assertTextEquals("Question? *")
  }

  @Test
  fun hide_asterisk() = runComposeUiTest {
    setContent {
      QuestionnaireDialogSelect(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "dialog-select-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
            extension = listOf(Extension(url = EXTENSION_DIALOG_URL_ANDROID_FHIR)),
            text = FhirR4String(value = "Question?"),
            required = FhirR4Boolean(value = true),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "dialog-select-item"),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
          questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = false),
        ),
      )
    }

    onNodeWithTag(QUESTION_HEADER_TAG).assertTextEquals("Question?")
  }

  @Test
  fun show_requiredText() = runComposeUiTest {
    setContent {
      QuestionnaireDialogSelect(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "dialog-select-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
            extension = listOf(Extension(url = EXTENSION_DIALOG_URL_ANDROID_FHIR)),
            required = FhirR4Boolean(value = true),
            text = FhirR4String(value = "Question?"),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "dialog-select-item"),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
          questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = true),
        ),
      )
    }

    // The "Required" text should be displayed in the supporting text of the OutlinedTextField
    onNodeWithText("Required").assertIsDisplayed()
  }

  @Test
  fun hide_requiredText() = runComposeUiTest {
    setContent {
      QuestionnaireDialogSelect(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "dialog-select-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
            extension = listOf(Extension(url = EXTENSION_DIALOG_URL_ANDROID_FHIR)),
            required = FhirR4Boolean(value = true),
            text = FhirR4String(value = "Question?"),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "dialog-select-item"),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
          questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = false),
        ),
      )
    }

    // When showRequiredText is false, "Required" text should not be displayed
    onNodeWithText("Required").assertDoesNotExist()
  }

  @Test
  fun shows_optionalText() = runComposeUiTest {
    setContent {
      QuestionnaireDialogSelect(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "dialog-select-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
            extension = listOf(Extension(url = EXTENSION_DIALOG_URL_ANDROID_FHIR)),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "dialog-select-item"),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
          questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = true),
        ),
      )
    }

    // The "Optional" text should be displayed in the supporting text
    onNodeWithText("Optional").assertIsDisplayed()
  }

  @Test
  fun hide_optionalText() = runComposeUiTest {
    setContent {
      QuestionnaireDialogSelect(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "dialog-select-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
            extension = listOf(Extension(url = EXTENSION_DIALOG_URL_ANDROID_FHIR)),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "dialog-select-item"),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
          questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = false),
        ),
      )
    }

    // When showOptionalText is false, "Optional" text should not be displayed
    onNodeWithText("Optional").assertDoesNotExist()
  }

  @Test
  fun multipleChoice_doNotShowErrorInitially() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "dialog-select-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
          extension = listOf(Extension(url = EXTENSION_DIALOG_URL_ANDROID_FHIR)),
          repeats = FhirR4Boolean(value = true),
          required = FhirR4Boolean(value = true),
          answerOption =
            arrayOf("Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5").map { option ->
              Questionnaire.Item.AnswerOption(
                value =
                  Questionnaire.Item.AnswerOption.Value.Coding(
                    value = Coding(display = FhirR4String(value = option)),
                  ),
              )
            },
        ),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> },
      )

    setContent { QuestionnaireDialogSelect(questionnaireViewItem) }
    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
      .assert(
        SemanticsMatcher.keyNotDefined(
          SemanticsProperties.Error,
        ),
      )
  }

  @Test
  fun multipleChoice_unselectSelectedAnswer_showErrorWhenNoAnswerIsSelected() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "dialog-select-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
          extension = listOf(Extension(url = EXTENSION_DIALOG_URL_ANDROID_FHIR)),
          repeats = FhirR4Boolean(value = false),
          required = FhirR4Boolean(value = true),
          answerOption =
            arrayOf("Coding 1", "Coding 2").map { option ->
              Questionnaire.Item.AnswerOption(
                value =
                  Questionnaire.Item.AnswerOption.Value.Coding(
                    value = Coding(display = FhirR4String(value = option)),
                  ),
              )
            },
        ),
        responseOptions(),
        validationResult = Invalid(listOf("Missing answer for required field.")),
        answersChangedCallback = { _, _, answers, _ -> },
      )

    setContent { QuestionnaireDialogSelect(questionnaireViewItem) }

    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    onNode(hasTestTag(OPTION_CHOICE_TAG) and hasText("Coding 2")).performClick()
    onNodeWithText("Save").performClick()

    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
      .assert(
        SemanticsMatcher.expectValue(
          SemanticsProperties.EditableText,
          AnnotatedString("Coding 2"),
        ),
      )
      .assertIsDisplayed()

    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    onNode(hasTestTag(OPTION_CHOICE_TAG) and hasText("Coding 2")).performClick()
    onNodeWithText("Save").performClick()

    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
      .assert(
        SemanticsMatcher.expectValue(
          SemanticsProperties.EditableText,
          AnnotatedString(""),
        ),
      )
      .assertIsDisplayed()
    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
      .assert(
        SemanticsMatcher.expectValue(
          SemanticsProperties.Error,
          "Missing answer for required field.",
        ),
      )
  }

  @Test
  fun emptyResponseOptions_showNoneSelected() = runComposeUiTest {
    setContent {
      QuestionnaireDialogSelect(
        QuestionnaireViewItem(
          answerOptions(false, "Coding 1", "Coding 2"),
          responseOptions(),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )
    }

    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).assertTextEquals("").assertIsDisplayed()
  }

  @Test
  fun selectedResponseOptions_showSelectedOptions() = runComposeUiTest {
    setContent {
      QuestionnaireDialogSelect(
        QuestionnaireViewItem(
          answerOptions(false, "Coding 1", "Coding 2", "Coding 3"),
          responseOptions(
            "Coding 1",
            "Coding 3",
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )
    }

    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).assertTextEquals("Coding 1, Coding 3")
  }

  @Test
  fun displayValidationResult_error_shouldShowErrorMessage() = runComposeUiTest {
    setContent {
      QuestionnaireDialogSelect(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "dialog-select-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
            extension = listOf(Extension(url = EXTENSION_DIALOG_URL_ANDROID_FHIR)),
            required = FhirR4Boolean(value = true),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "dialog-select-item"),
          ),
          validationResult = Invalid(listOf("Missing answer for required field.")),
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )
    }

    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
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
      QuestionnaireDialogSelect(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "dialog-select-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
            extension = listOf(Extension(url = EXTENSION_DIALOG_URL_ANDROID_FHIR)),
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
            linkId = FhirR4String(value = "dialog-select-item"),
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

    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
      .assert(
        SemanticsMatcher.keyNotDefined(
          SemanticsProperties.Error,
        ),
      )
  }

  @Test
  fun bind_readOnly_shouldDisableView() = runComposeUiTest {
    setContent {
      QuestionnaireDialogSelect(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "dialog-select-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
            extension = listOf(Extension(url = EXTENSION_DIALOG_URL_ANDROID_FHIR)),
            readOnly = FhirR4Boolean(value = true),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "dialog-select-item"),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )
    }

    onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).assertIsNotEnabled()
  }

  private val openChoiceType =
    Extension(
      url = EXTENSION_ITEM_CONTROL_URL,
      value =
        Extension.Value.CodeableConcept(
          value =
            CodeableConcept(
              coding =
                listOf(
                  Coding(
                    code = Code(value = ItemControlTypes.OPEN_CHOICE.extensionCode),
                    system = Uri(value = EXTENSION_ITEM_CONTROL_SYSTEM),
                    display = FhirR4String(value = "Open Choice"),
                  ),
                ),
            ),
        ),
    )

  private fun answerOptions(multiSelect: Boolean, vararg options: String) =
    Questionnaire.Item(
      linkId = FhirR4String(value = "dialog-select-item"),
      type = Enumeration(value = Questionnaire.QuestionnaireItemType.Choice),
      extension = listOf(Extension(url = EXTENSION_DIALOG_URL_ANDROID_FHIR)),
      repeats = FhirR4Boolean(value = multiSelect),
      answerOption =
        options.map { option ->
          Questionnaire.Item.AnswerOption(
            value =
              Questionnaire.Item.AnswerOption.Value.Coding(
                value = Coding(display = FhirR4String(value = option)),
              ),
          )
        },
    )

  private fun responseOptions(vararg responses: String) =
    QuestionnaireResponse.Item(
      linkId = FhirR4String(value = "dialog-select-item"),
      answer =
        responses.map {
          QuestionnaireResponse.Item.Answer(
            value =
              QuestionnaireResponse.Item.Answer.Value.Coding(
                value = Coding(display = FhirR4String(value = it)),
              ),
          )
        },
    )
}
