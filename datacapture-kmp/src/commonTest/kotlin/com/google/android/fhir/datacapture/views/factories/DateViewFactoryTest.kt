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

import android_fhir.datacapture_kmp.generated.resources.Res
import android_fhir.datacapture_kmp.generated.resources.select_date
import androidx.compose.runtime.Composable
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.text.intl.Locale
import com.google.android.fhir.datacapture.extensions.DateAnswerValue
import com.google.android.fhir.datacapture.extensions.FhirR4Boolean
import com.google.android.fhir.datacapture.extensions.FhirR4DateType
import com.google.android.fhir.datacapture.extensions.FhirR4String
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionTextConfiguration
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.DATE_TEXT_INPUT_FIELD
import com.google.android.fhir.datacapture.views.compose.ERROR_TEXT_AT_HEADER_TEST_TAG
import com.google.android.fhir.datacapture.views.compose.QUESTION_HEADER_TAG
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.FhirDate
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import kotlin.test.Test
import org.jetbrains.compose.resources.getString

@OptIn(ExperimentalTestApi::class)
class DateViewFactoryTest {

  @Composable
  fun QuestionnaireDateView(questionnaireViewItem: QuestionnaireViewItem) {
    QuestionnaireTheme { DateViewFactory.Content(questionnaireViewItem) }
  }

  @Test
  fun shouldSetQuestionHeader() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    setContent { QuestionnaireDateView(questionnaireViewItem) }

    onNodeWithTag(QUESTION_HEADER_TAG).assertTextEquals("Question?")
  }

  @Test
  fun shouldSetEmptyDateInput() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDateView(questionnaireViewItem) }

    onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("")
  }

  @Test
  fun shouldSetTextFieldEmptyWhenDateFieldIsInitializedButAnswerDateValueIsNull() =
    runComposeUiTest {
      val questionnaireViewItem =
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "date-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
            text = FhirR4String(value = "Question?"),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "date-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value = DateAnswerValue(value = FhirR4DateType(value = null)),
                ),
              ),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        )
      setContent { QuestionnaireDateView(questionnaireViewItem) }
      onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("")
    }

  @Test
  fun shouldSetDateInput_localeUs() = runComposeUiTest {
    setLocale(Locale.US)
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "date-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  DateAnswerValue(
                    value = FhirR4DateType(value = FhirDate.fromString("2020-10-19")),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDateView(questionnaireViewItem) }

    onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("11/19/2020")
  }

  @Test
  fun showDateFormatLabelInLowerCase() = runComposeUiTest {
    setLocale(Locale.US)
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    onNodeWithTag(DATE_TEXT_INPUT_FIELD).assertTextEquals("mm/dd/yyyy", includeEditableText = false)
  }

  @Test
  fun shouldSetDateInput_localeJp() = runComposeUiTest {
    setLocale(Locale.JAPAN)
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.Item(
            linkId = FhirR4String(value = "date-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
          )
          .apply { text = "Question?" },
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item"))
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateType(2020, 10, 19)),
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
      .assertTextEquals("2020/11/19")
  }

  @Test
  fun shouldSetDateInput_localeEn() = runComposeUiTest {
    setLocale(Locale.ENGLISH)
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.Item(
            linkId = FhirR4String(value = "date-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
          )
          .apply { text = "Question?" },
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item"))
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateType(2020, 10, 19)),
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
      .assertTextEquals("11/19/2020")
  }

  @Test
  fun parseDateTextInputInUsLocale() = runComposeUiTest {
    setLocale(Locale.US)
    var answers: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val item =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result, _ -> answers = result },
      )

    viewHolder.bind(item)
    onNodeWithTag(DATE_TEXT_INPUT_FIELD).performTextInput("11/19/2020")
    waitUntil { answers != null }

    val answer = answers!!.single().value as DateType

    assertThat(answer.day).isEqualTo(19)
    assertThat(answer.month).isEqualTo(10)
    assertThat(answer.year).isEqualTo(2020)
  }

  @Test
  fun parseDateTextInputInJapanLocale() = runComposeUiTest {
    setLocale(Locale.JAPAN)
    var answers: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val item =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result, _ -> answers = result },
      )
    viewHolder.bind(item)
    onNodeWithTag(DATE_TEXT_INPUT_FIELD).performTextInput("2020/11/19")
    waitUntil { answers != null }
    val answer = answers!!.single().value as DateType

    assertThat(answer.day).isEqualTo(19)
    assertThat(answer.month).isEqualTo(10)
    assertThat(answer.year).isEqualTo(2020)
  }

  @Test
  fun clearTheAnswerIfDateInputIsInvalid() = runComposeUiTest {
    setLocale(Locale.US)
    var answers: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item"))
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateType(2020, 10, 19)),
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result, _ -> answers = result },
      )
    viewHolder.bind(questionnaireItem)
    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
      .assertTextEquals("11/19/2020")
    val dateTextInput = "1119" // transforms to 11/19 in the datePicker widget
    onNodeWithTag(DATE_TEXT_INPUT_FIELD).performSemanticsAction(
      SemanticsActions.SetText,
    ) {
      it(dateTextInput.toAnnotatedString())
    }
    waitUntil { answers != null }

    assertThat(answers!!).isEmpty()
  }

  @Test
  fun doNotClearTheTextFieldInputForInvalidDate() = runComposeUiTest {
    setLocale(Locale.US)
    val questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item"))
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateType(2020, 10, 19)),
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    viewHolder.bind(questionnaireItem)
    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
      .assertTextEquals("11/19/2020")
    onNodeWithTag(DATE_TEXT_INPUT_FIELD).performSemanticsAction(
      SemanticsActions.SetText,
    ) {
      it("11/19".toAnnotatedString())
    }
    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
      .assertTextEquals("11/19")
  }

  @Test
  fun clearQuestionnaireResponseAnswerOnDraftAnswerUpdate() = runComposeUiTest {
    var answers: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? =
      listOf(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent())
    setLocale(Locale.US)
    val questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item"))
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateType(2020, 10, 19)),
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answersForCallback, _ -> answers = answersForCallback },
      )

    viewHolder.bind(questionnaireItem)
    runBlocking { questionnaireItem.setDraftAnswer("02/07") }
    assertThat(answers!!).isEmpty()
  }

  @Test
  fun clearDraftValueOnAnValidAnswerUpdate() = runComposeUiTest {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(DateType(2026, 0, 1))
    var partialValue: String? = "02/07"
    setLocale(Locale.US)
    val questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item"))
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateType(2020, 10, 19)),
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, partialAnswer ->
          partialValue = partialAnswer as? String
        },
      )

    viewHolder.bind(questionnaireItem)
    runBlocking { questionnaireItem.setAnswer(answer) }
    assertThat(partialValue).isNull()
  }

  @Test
  fun displayPartialAnswerInTheTextFieldOfRecycledItems() = runComposeUiTest {
    setLocale(Locale.US)
    var questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item"))
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateType(2020, 10, 19)),
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    viewHolder.bind(questionnaireItem)
    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
      .assertTextEquals("11/19/2020")

    questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "02/07",
      )

    viewHolder.bind(questionnaireItem)
    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
      .assertTextEquals("02/07")
  }

  @Test
  fun displayAnAnswerInTheTextFieldOfPartiallyAnsweredRecycledItem() = runComposeUiTest {
    setLocale(Locale.US)
    var questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "02/07",
      )

    viewHolder.bind(questionnaireItem)
    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
      .assertTextEquals("02/07")

    questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item"))
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateType(2020, 10, 19)),
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    viewHolder.bind(questionnaireItem)
    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
      .assertTextEquals("11/19/2020")
  }

  @Test
  fun displayValidationResult_error_shouldShowErrorMessage() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
          required = FhirR4Boolean(value = true),
          extension =
            listOf(
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/minValue",
                value =
                  Extension.Value.Date(
                    value = FhirR4DateType(value = FhirDate.fromString("2020-00-01")),
                  ),
              ),
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/maxValue",
                value =
                  Extension.Value.Date(
                    value = FhirR4DateType(value = FhirDate.fromString("2025-00-01")),
                  ),
              ),
            ),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "date-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  DateAnswerValue(
                    value = FhirR4DateType(value = FhirDate.fromString("2026-00-01")),
                  ),
              ),
            ),
        ),
        validationResult = Invalid(listOf("Maximum value allowed is:2025-01-01")),
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDateView(questionnaireViewItem) }

    onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .assert(
        SemanticsMatcher.expectValue(
          SemanticsProperties.Error,
          "Maximum value allowed is:2025-01-01",
        ),
      )
  }

  @Test
  fun showDateFormatInLowercaseInTheErrorMessage() = runComposeUiTest {
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "1119202",
      )

    setContent { QuestionnaireDateView(itemViewItem) }

    onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .assert(
        SemanticsMatcher.expectValue(
          SemanticsProperties.Error,
          "Date format needs to be mm/dd/yyyy (e.g. 01/31/2023)",
        ),
      )
  }

  @Test
  fun displayValidationResult_noError_shouldShowNoErrorMessage() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
          extension =
            listOf(
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/minValue",
                value =
                  Extension.Value.Date(
                    value = FhirR4DateType(value = FhirDate.fromString("2020-00-01")),
                  ),
              ),
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/maxValue",
                value =
                  Extension.Value.Date(
                    value = FhirR4DateType(value = FhirDate.fromString("2025-00-01")),
                  ),
              ),
            ),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "date-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  DateAnswerValue(
                    value = FhirR4DateType(value = FhirDate.fromString("2023-00-01")),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDateView(questionnaireViewItem) }

    onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Error))
  }

  @Test
  fun hidesErrorTextviewInTheHeader() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDateView(questionnaireViewItem) }

    onNodeWithTag(ERROR_TEXT_AT_HEADER_TEST_TAG).assertDoesNotExist()
  }

  @Test
  fun bind_readOnly_shouldDisableView() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
            linkId = FhirR4String(value = "date-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
          )
          .apply { readOnly = true },
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDateView(questionnaireViewItem) }

    onNodeWithTag(DATE_TEXT_INPUT_FIELD).assertIsNotEnabled()
    onNodeWithContentDescription(getString(Res.string.select_date)).assertIsNotEnabled()
  }

  @Test
  fun bindMultipleTimesWithDifferentQuestionnaireItemViewItemShouldShowProperDate() =
    runComposeUiTest {
      setLocale(Locale.US)

      viewHolder.bind(
        QuestionnaireViewItem(
          Questionnaire.Item(
              linkId = FhirR4String(value = "date-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
            )
            .apply { text = "Question?" },
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item"))
            .addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                .setValue(DateType(2020, 10, 19)),
            ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )
      composeTestRule
        .onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
        .assertTextEquals("11/19/2020")

      viewHolder.bind(
        QuestionnaireViewItem(
          Questionnaire.Item(
              linkId = FhirR4String(value = "date-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
            )
            .apply { text = "Question?" },
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item"))
            .addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                .setValue(DateType(2021, 10, 19)),
            ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )
      composeTestRule
        .onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
        .assertTextEquals("11/19/2021")

      viewHolder.bind(
        QuestionnaireViewItem(
          Questionnaire.Item(
              linkId = FhirR4String(value = "date-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
            )
            .apply { text = "Question?" },
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )
      composeTestRule
        .onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
        .assertTextEquals("")
    }

  @Test
  fun shouldUseDateFormatInTheEntryFormatExtension() = runComposeUiTest {
    setLocale(Locale.US)
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.Item(
            linkId = FhirR4String(value = "date-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
          )
          .apply { addExtension(EXTENSION_ENTRY_FORMAT_URL, StringType("yyyy-MM-dd")) },
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .assertTextEquals("yyyy-mm-dd", includeEditableText = false)
  }

  @Test
  fun shouldSetLocalDateInputFormatWhenEntryFormatExtensionHasIncorrectFormatStringInQuestionnaire() =
    runComposeUiTest {
      setLocale(Locale.US)
      viewHolder.bind(
        QuestionnaireViewItem(
          Questionnaire.Item(
              linkId = FhirR4String(value = "date-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
            )
            .apply { addExtension(EXTENSION_ENTRY_FORMAT_URL, StringType("yMyd")) },
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )
      composeTestRule
        .onNodeWithTag(DATE_TEXT_INPUT_FIELD)
        .assertTextEquals("mm/dd/yyyy", includeEditableText = false)
    }

  @Test
  fun shouldUseDateFormatInTheEntryFormatExtensionThoughDateSeparatorIsMissing() =
    runComposeUiTest {
      setLocale(Locale.US)
      viewHolder.bind(
        QuestionnaireViewItem(
          Questionnaire.Item(
              linkId = FhirR4String(value = "date-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
            )
            .apply { addExtension(EXTENSION_ENTRY_FORMAT_URL, StringType("yyyyMMdd")) },
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )
      composeTestRule
        .onNodeWithTag(DATE_TEXT_INPUT_FIELD)
        .assertTextEquals("yyyymmdd", includeEditableText = false)
    }

  @Test
  fun shouldUseDateFormatInTheEntryFormatAfterConvertingItToShortFormatStyle() = runComposeUiTest {
    setLocale(Locale.US)
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.Item(
            linkId = FhirR4String(value = "date-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
          )
          .apply { addExtension(EXTENSION_ENTRY_FORMAT_URL, StringType("yyyy MMMM dd")) },
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .assertTextEquals("yyyy mm dd", includeEditableText = false)
  }

  @Test
  fun shouldSetLocalDateInputFormatWhenEntryFormatExtensionHasEmptyStringInQuestionnaire() =
    runComposeUiTest {
      setLocale(Locale.US)
      viewHolder.bind(
        QuestionnaireViewItem(
          Questionnaire.Item(
              linkId = FhirR4String(value = "date-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
            )
            .apply { addExtension(EXTENSION_ENTRY_FORMAT_URL, StringType("")) },
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )
      composeTestRule
        .onNodeWithTag(DATE_TEXT_INPUT_FIELD)
        .assertTextEquals("mm/dd/yyyy", includeEditableText = false)
    }

  @Test
  fun shouldSetLocalDateInputFormatWhenNoEntryFormatExtensionInQuestionnaire() = runComposeUiTest {
    setLocale(Locale.US)
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .assertTextEquals("mm/dd/yyyy", includeEditableText = false)
  }

  @Test
  fun showAsterisk() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
          text = FhirR4String(value = "Question?"),
          required = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = true),
      )
    setContent { QuestionnaireDateView(questionnaireViewItem) }

    onNodeWithTag(QUESTION_HEADER_TAG).assertTextEquals("Question? *")
  }

  @Test
  fun hideAsterisk() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
          text = FhirR4String(value = "Question?"),
          required = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = false),
      )

    setContent { QuestionnaireDateView(questionnaireViewItem) }

    onNodeWithTag(QUESTION_HEADER_TAG).assertTextEquals("Question?")
  }

  @Test
  fun showRequiredText() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
          required = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = true),
      )

    setContent { QuestionnaireDateView(questionnaireViewItem) }

    onNodeWithTag(DATE_TEXT_INPUT_FIELD).assertTextContains("Required")
  }

  @Test
  fun hideRequiredText() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
            linkId = FhirR4String(value = "date-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
          )
          .apply { required = true },
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = false),
      )

    setContent { QuestionnaireDateView(questionnaireViewItem) }

    onNodeWithText("Required").assertDoesNotExist()
  }

  @Test
  fun showsOptionalText() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = true),
      )

    setContent { QuestionnaireDateView(questionnaireViewItem) }

    onNodeWithTag(DATE_TEXT_INPUT_FIELD).assertTextContains("Optional")
  }

  @Test
  fun hideOptionalText() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = false),
      )

    setContent { QuestionnaireDateView(questionnaireViewItem) }
    onNodeWithText("Optional").assertDoesNotExist()
  }

  private fun setLocale(locale: Locale) {
    Locale.setDefault(locale)
    parent.context.resources.configuration.setLocale(locale)
  }
}
