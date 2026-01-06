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

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.isEditable
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.runComposeUiTest
import com.google.android.fhir.datacapture.extensions.DateTimeAnswerValue
import com.google.android.fhir.datacapture.extensions.FhirR4Boolean
import com.google.android.fhir.datacapture.extensions.FhirR4String
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionTextConfiguration
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.DATE_TEXT_INPUT_FIELD
import com.google.android.fhir.datacapture.views.compose.ERROR_TEXT_AT_HEADER_TEST_TAG
import com.google.android.fhir.datacapture.views.compose.QUESTION_HEADER_TAG
import com.google.android.fhir.datacapture.views.compose.TIME_PICKER_INPUT_FIELD
import com.google.fhir.model.r4.DateTime
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.FhirDateTime
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.UtcOffset

@OptIn(ExperimentalTestApi::class)
class DateTimeViewFactoryTest {
  @Composable
  fun QuestionnaireDateTimeView(questionnaireViewItem: QuestionnaireViewItem) {
    QuestionnaireTheme { DateTimeViewFactory.Content(questionnaireViewItem) }
  }

  @Test
  fun shouldSetQuestionHeader() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "datetime-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDateTimeView(questionnaireViewItem) }

    onNodeWithTag(QUESTION_HEADER_TAG).assertTextEquals("Question?")
  }

  @Test
  fun shouldSetEmptyDateTimeInput() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "datetime-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDateTimeView(questionnaireViewItem) }

    onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("")
    onNodeWithTag(TIME_PICKER_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("")
  }

  @Test
  fun showDateFormatLabelInLowerCase() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "datetime-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDateTimeView(questionnaireViewItem) }
    onNodeWithTag(DATE_TEXT_INPUT_FIELD).assertTextEquals("mm/dd/yyyy", includeEditableText = false)
  }

  @Test
  fun shouldSetDateTimeInput() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "datetime-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  DateTimeAnswerValue(
                    value =
                      DateTime(
                        value =
                          FhirDateTime.DateTime(
                            dateTime = LocalDateTime(2020, 1, 5, 1, 30),
                            utcOffset = UtcOffset.ZERO,
                          ),
                      ),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDateTimeView(questionnaireViewItem) }

    onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("02/05/2020")
    val is24Hour = DateFormat.is24HourFormat(viewHolder.itemView.context)
    val expectedTime = if (is24Hour) "01:30" else "1:30 AM"
    onNodeWithTag(TIME_PICKER_INPUT_FIELD, useUnmergedTree = true).assertTextEquals(expectedTime)
  }

  @Test
  fun parseDateTextInputInUSLocale() = runComposeUiTest {
    var answer: QuestionnaireResponse.Item.Answer? = null
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "datetime-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  DateTimeAnswerValue(
                    value =
                      DateTime(
                        value =
                          FhirDateTime.DateTime(
                            dateTime = LocalDateTime(2020, 1, 5, 1, 30),
                            utcOffset = UtcOffset.ZERO,
                          ),
                      ),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result, _ -> answer = result.singleOrNull() },
      )
    setContent { QuestionnaireDateTimeView(itemViewItem) }

    onNodeWithTag(DATE_TEXT_INPUT_FIELD).performTextReplacement("11/19/2020")
    waitUntil { answer != null }

    val dateTime = (answer!!.value?.asDateTime()?.value?.value as? FhirDateTime.DateTime)?.dateTime
    dateTime.shouldNotBeNull()
    dateTime.day.shouldBe(19)
    dateTime.month.shouldBe(10)
    dateTime.year.shouldBe(2020)
  }

  @Test
  fun parseDateTextInputInJapanLocale() = runComposeUiTest {
    Locale.setDefault(Locale.JAPAN)
    var answer: QuestionnaireResponse.Item.Answer? = null
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "datetime-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  DateTimeAnswerValue(
                    value =
                      DateTime(
                        value =
                          FhirDateTime.DateTime(
                            dateTime = LocalDateTime(2020, 1, 5, 1, 30),
                            utcOffset = UtcOffset.ZERO,
                          ),
                      ),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result, _ -> answer = result.singleOrNull() },
      )
    setContent { QuestionnaireDateTimeView(itemViewItem) }

    onNodeWithTag(DATE_TEXT_INPUT_FIELD).performTextReplacement("2020/11/19")
    waitUntil { answer != null }

    val dateTime = (answer!!.value?.asDateTime()?.value?.value as? FhirDateTime.DateTime)?.dateTime
    dateTime.shouldNotBeNull()
    dateTime.day.shouldBe(19)
    dateTime.month.shouldBe(10)
    dateTime.year.shouldBe(2020)
  }

  @Test
  fun ifDateInputIsInvalidThenClearTheAnswer() = runComposeUiTest {
    Locale.setDefault(Locale.JAPAN)
    var answers: List<QuestionnaireResponse.Item.Answer>? = null
    var draftAnswer: Any? = null
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "datetime-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  DateTimeAnswerValue(
                    value =
                      DateTime(
                        value =
                          FhirDateTime.DateTime(
                            dateTime = LocalDateTime(2020, 1, 5, 1, 30),
                            utcOffset = UtcOffset.ZERO,
                          ),
                      ),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result, draft ->
          answers = result
          draftAnswer = draft
        },
      )
    setContent { QuestionnaireDateTimeView(itemViewItem) }

    onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .performTextReplacement("202011") // transforms to 2020/11 for Locale.JAPAN
    waitUntil { answers != null }

    answers!!.shouldBeEmpty()
    (draftAnswer as String).shouldBe("202011")
  }

  @Test
  fun doNotClearTheTextFieldInputOnInvalidDate() = runComposeUiTest {
    Locale.setDefault(Locale.JAPAN)
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "datetime-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  DateTimeAnswerValue(
                    value =
                      DateTime(
                        value =
                          FhirDateTime.DateTime(
                            dateTime = LocalDateTime(2020, 1, 5, 1, 30),
                            utcOffset = UtcOffset.ZERO,
                          ),
                      ),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    setContent { QuestionnaireDateTimeView(itemViewItem) }
    onNodeWithTag(DATE_TEXT_INPUT_FIELD).performTextReplacement("2020/11")

    onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("2020/11")
  }

  @Test
  fun clearQuestionnaireResponseAnswerOnDraftAnswerUpdate() = runComposeUiTest {
    var answers: List<QuestionnaireResponse.Item.Answer>? =
      listOf(QuestionnaireResponse.Item.Answer())
    val questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "datetime-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  DateTimeAnswerValue(
                    value =
                      DateTime(
                        value =
                          FhirDateTime.DateTime(
                            dateTime = LocalDateTime(2020, 1, 5, 1, 30),
                            utcOffset = UtcOffset.ZERO,
                          ),
                      ),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answersForCallback, _ -> answers = answersForCallback },
      )

    setContent { QuestionnaireDateTimeView(questionnaireItem) }
    questionnaireItem.setDraftAnswer("0207") // would transform to 02/07/ for default locale

    answers!!.shouldBeEmpty()
  }

  @Test
  fun clearDraftAnswerOnAnValidAnswerUpdate() = runComposeUiTest {
    val answer =
      QuestionnaireResponse.Item.Answer(
        value =
          DateTimeAnswerValue(
            value =
              DateTime(
                value =
                  FhirDateTime.DateTime(
                    dateTime = LocalDateTime(2020, 2, 6, 2, 30),
                    utcOffset = UtcOffset.ZERO,
                  ),
              ),
          ),
      )
    var draft: String? = "02/07"
    val questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "datetime-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  DateTimeAnswerValue(
                    value =
                      DateTime(
                        value =
                          FhirDateTime.DateTime(
                            dateTime = LocalDateTime(2020, 1, 5, 1, 30),
                            utcOffset = UtcOffset.ZERO,
                          ),
                      ),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, draftAnswer -> draft = draftAnswer as? String },
      )

    setContent { QuestionnaireDateTimeView(questionnaireItem) }
    questionnaireItem.setAnswer(answer)
    draft.shouldBeNull()
  }

  @Test
  fun displayDraftAnswerInTheTextFieldOfRecycledItems() = runComposeUiTest {
    var questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "datetime-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  DateTimeAnswerValue(
                    value =
                      DateTime(
                        value =
                          FhirDateTime.DateTime(
                            dateTime = LocalDateTime(2020, 1, 5, 1, 30),
                            utcOffset = UtcOffset.ZERO,
                          ),
                      ),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDateTimeView(questionnaireItem) }
    onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("02/05/2020")

    questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "datetime-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "02/07",
      )

    setContent { QuestionnaireDateTimeView(questionnaireItem) }
    onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("02/07")
  }

  @Test
  fun displayAnAnswerInTheTextFieldOfPartiallyAnsweredRecycledItem() = runComposeUiTest {
    var questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "datetime-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "02/07",
      )

    setContent { QuestionnaireDateTimeView(questionnaireItem) }
    onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("02/07")

    questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "datetime-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  DateTimeAnswerValue(
                    value =
                      DateTime(
                        value =
                          FhirDateTime.DateTime(
                            dateTime = LocalDateTime(2020, 1, 5, 1, 30),
                            utcOffset = UtcOffset.ZERO,
                          ),
                      ),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDateTimeView(questionnaireItem) }
    onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("02/05/2020")
  }

  @Test
  fun ifDraftAnswerInputIsInvalidThenDoNotEnableTimeTextInputLayout() = runComposeUiTest {
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "datetime-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "1119", // would transform to 11/19/ for default locale
      )

    setContent { QuestionnaireDateTimeView(itemViewItem) }
    onNodeWithTag(TIME_PICKER_INPUT_FIELD).assertIsNotEnabled()
  }

  @Test
  fun ifTheDraftAnswerInputIsEmptyDoNotEnableTheTimeTextInputLayout() = runComposeUiTest {
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "datetime-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "",
      )

    setContent { QuestionnaireDateTimeView(itemViewItem) }
    onNodeWithTag(TIME_PICKER_INPUT_FIELD).assertIsNotEnabled()
  }

  @Test
  fun ifThereIsNoAnswerOrDraftAnswerDoNotEnableTheTimeTextInputLayout() = runComposeUiTest {
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "datetime-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = null,
      )

    setContent { QuestionnaireDateTimeView(itemViewItem) }
    onNodeWithTag(TIME_PICKER_INPUT_FIELD).assertIsNotEnabled()
  }

  @Test
  fun ifDateDraftAnswerIsValidThenEnableTimeTextInputLayout() = runComposeUiTest {
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "datetime-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "11192020", // transforms to 11/19/2020 for default locale
      )

    setContent { QuestionnaireDateTimeView(itemViewItem) }
    onNodeWithTag(TIME_PICKER_INPUT_FIELD).assertIsNotEnabled()
  }

  @Test
  fun displayValidationResult_error_shouldShowErrorMessage() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
          required = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "datetime-item")),
        validationResult = Invalid(listOf("Missing answer for required field.")),
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDateTimeView(questionnaireViewItem) }

    onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .assert(
        SemanticsMatcher.expectValue(
          SemanticsProperties.Error,
          "Missing answer for required field.",
        ),
      )
  }

  @Test
  fun displayValidationResult_noError_shouldShowNoErrorMessage() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
          extension =
            listOf(
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/minValue",
                value =
                  Extension.Value.DateTime(
                    value =
                      DateTime(
                        value =
                          FhirDateTime.DateTime(
                            dateTime = LocalDateTime(2020, 1, 5, 1, 30),
                            utcOffset = UtcOffset.ZERO,
                          ),
                      ),
                  ),
              ),
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/maxValue",
                value =
                  Extension.Value.DateTime(
                    value =
                      DateTime(
                        value =
                          FhirDateTime.DateTime(
                            dateTime = LocalDateTime(2025, 1, 5, 1, 30),
                            utcOffset = UtcOffset.ZERO,
                          ),
                      ),
                  ),
              ),
            ),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "datetime-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  DateTimeAnswerValue(
                    value =
                      DateTime(
                        value =
                          FhirDateTime.DateTime(
                            dateTime = LocalDateTime(2023, 1, 5, 1, 30),
                            utcOffset = UtcOffset.ZERO,
                          ),
                      ),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDateTimeView(questionnaireViewItem) }

    onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Error))
    onNodeWithTag(TIME_PICKER_INPUT_FIELD)
      .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Error))
  }

  @Test
  fun ifTheDraftAnswerIsInvalidDisplayTheErrorMessage() = runComposeUiTest {
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "datetime-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "1119202", // transforms to 11/19/202
      )

    setContent { QuestionnaireDateTimeView(itemViewItem) }

    onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .assert(
        SemanticsMatcher.expectValue(
          SemanticsProperties.Error,
          "Date format needs to be mm/dd/yyyy (e.g. 01/31/2023)",
        ),
      )
  }

  @Test
  fun showDateFormatInLowerCaseInTheErrorMessage() = runComposeUiTest {
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "datetime-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "1119202", // transforms to 11/19/202
      )

    setContent { QuestionnaireDateTimeView(itemViewItem) }
    onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .assert(
        SemanticsMatcher.expectValue(
          SemanticsProperties.Error,
          "Date format needs to be mm/dd/yyyy (e.g. 01/31/2023)",
        ),
      )
  }

  @Test
  fun hidesErrorTextviewInTheHeader() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "datetime-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDateTimeView(questionnaireViewItem) }

    onNodeWithTag(ERROR_TEXT_AT_HEADER_TEST_TAG).assertIsNotDisplayed().assertDoesNotExist()
  }

  @Test
  fun bind_readOnly_shouldDisableView() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
            linkId = FhirR4String(value = "datetime-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
          )
          .apply { readOnly = true },
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "datetime-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDateTimeView(questionnaireViewItem) }

    onNodeWithTag(DATE_TEXT_INPUT_FIELD).assertIsNotEnabled()
    onNodeWithTag(TIME_PICKER_INPUT_FIELD).assertIsNotEnabled()
  }

  @Test
  fun bindMultipleTimesWithSeparateQuestionnaireItemViewItemShouldShowProperDateAndTime() =
    runComposeUiTest {
      var questionnaireViewItem by
        mutableStateOf(
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "datetime-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
              text = FhirR4String(value = "Question?"),
            ),
            QuestionnaireResponse.Item(
              linkId = FhirR4String(value = "datetime-item"),
              answer =
                listOf(
                  QuestionnaireResponse.Item.Answer(
                    value =
                      DateTimeAnswerValue(
                        value =
                          DateTime(
                            value =
                              FhirDateTime.DateTime(
                                dateTime = LocalDateTime(2020, 1, 5, 1, 30),
                                utcOffset = UtcOffset.ZERO,
                              ),
                          ),
                      ),
                  ),
                ),
            ),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
        )

      setContent { QuestionnaireDateTimeView(questionnaireViewItem) }

      onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("02/05/2020")
      onNodeWithTag(TIME_PICKER_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("1:30 AM")

      questionnaireViewItem =
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "datetime-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
            text = FhirR4String(value = "Question?"),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "datetime-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value =
                    DateTimeAnswerValue(
                      value =
                        DateTime(
                          value =
                            FhirDateTime.DateTime(
                              dateTime = LocalDateTime(2021, 1, 5, 2, 30),
                              utcOffset = UtcOffset.ZERO,
                            ),
                        ),
                    ),
                ),
              ),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        )

      onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("02/05/2021")
      onNodeWithTag(TIME_PICKER_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("2:30 AM")

      questionnaireViewItem =
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "datetime-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
            text = FhirR4String(value = "Question?"),
          ),
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "datetime-item")),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        )

      onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("")
      onNodeWithTag(TIME_PICKER_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("")
    }

  @Test
  fun showsTimePickerInInputMode() = runComposeUiTest {
    val questionnaireItemView =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "datetime-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDateTimeView(questionnaireItemView) }
    onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .onChildren()
      .filterToOne(
        SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button),
      )
      .performClick()
    onNodeWithText("OK").performClick()
    onNodeWithTag(TIME_PICKER_INPUT_FIELD).performClick()

    onNode(
        hasContentDescription("Switch to clock input", substring = true) and
          SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button),
      )
      .assertIsDisplayed()
    onNode(hasContentDescription("for hour", substring = true) and isEditable()).assertIsDisplayed()
    onNode(hasContentDescription("for minutes", substring = true) and isEditable()).assertExists()
  }

  @Test
  fun showsTimePickerInClockMode() = runComposeUiTest {
    val questionnaireItemView =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "datetime-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDateTimeView(questionnaireItemView) }
    onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .onChildren()
      .filterToOne(
        SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button),
      )
      .performClick()
    onNodeWithText("OK").performClick()
    onNodeWithTag(TIME_PICKER_INPUT_FIELD)
      .onChildren()
      .filterToOne(
        SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button),
      )
      .performClick()

    onNode(
        hasContentDescription("Switch to text input", substring = true) and
          SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button),
      )
      .assertIsDisplayed()
    onNode(
        hasAnyChild(hasContentDescription("12 o'clock", substring = true)) and
          SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup),
      )
      .assertIsDisplayed()
  }

  @Test
  fun showsAsterisk() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
          text = FhirR4String(value = "Question?"),
          required = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "datetime-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = true),
      )

    setContent { QuestionnaireDateTimeView(questionnaireViewItem) }

    onNodeWithTag(QUESTION_HEADER_TAG).assertTextEquals("Question? *")
  }

  @Test
  fun hideAsterisk() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
          text = FhirR4String(value = "Question?"),
          required = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "datetime-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = false),
      )

    setContent { QuestionnaireDateTimeView(questionnaireViewItem) }

    onNodeWithTag(QUESTION_HEADER_TAG).assertTextEquals("Question?")
  }

  @Test
  fun showsRequiredText() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
          required = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "datetime-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = true),
      )

    setContent { QuestionnaireDateTimeView(questionnaireViewItem) }
    onNodeWithTag(DATE_TEXT_INPUT_FIELD).assertTextContains("Required")
  }

  @Test
  fun hideRequiredText() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
          required = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "datetime-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = false),
      )

    setContent { QuestionnaireDateTimeView(questionnaireViewItem) }

    onNodeWithText("Required").assertIsNotDisplayed().assertDoesNotExist()
  }

  @Test
  fun showsOptionalText() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "datetime-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = true),
      )

    setContent { QuestionnaireDateTimeView(questionnaireViewItem) }

    onNodeWithTag(DATE_TEXT_INPUT_FIELD).assertTextContains("Optional")
  }

  @Test
  fun hideOptionalText() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "datetime-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.DateTime),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "datetime-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = false),
      )

    setContent { QuestionnaireDateTimeView(questionnaireViewItem) }
    onNodeWithText("Optional").assertIsNotDisplayed().assertDoesNotExist()
  }
}
