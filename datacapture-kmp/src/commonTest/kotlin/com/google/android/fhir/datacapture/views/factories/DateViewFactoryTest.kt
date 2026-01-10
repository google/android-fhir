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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsActions
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
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.runComposeUiTest
import com.google.android.fhir.datacapture.extensions.DateAnswerValue
import com.google.android.fhir.datacapture.extensions.EXTENSION_ENTRY_FORMAT_URL
import com.google.android.fhir.datacapture.extensions.FhirR4Boolean
import com.google.android.fhir.datacapture.extensions.FhirR4DateType
import com.google.android.fhir.datacapture.extensions.FhirR4String
import com.google.android.fhir.datacapture.extensions.toAnnotatedString
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
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
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
  @Ignore
  fun shouldSetDateInput_localeUs() =
    runComposeUiTest {
      //    setLocale(Locale.US)
      //    val questionnaireViewItem =
      //      QuestionnaireViewItem(
      //        Questionnaire.Item(
      //          linkId = FhirR4String(value = "date-item"),
      //          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
      //          text = FhirR4String(value = "Question?"),
      //        ),
      //        QuestionnaireResponse.Item(
      //          linkId = FhirR4String(value = "date-item"),
      //          answer =
      //            listOf(
      //              QuestionnaireResponse.Item.Answer(
      //                value =
      //                  DateAnswerValue(
      //                    value = FhirR4DateType(value = FhirDate.fromString("2020-10-19")),
      //                  ),
      //              ),
      //            ),
      //        ),
      //        validationResult = NotValidated,
      //        answersChangedCallback = { _, _, _, _ -> },
      //      )
      //
      //    setContent { QuestionnaireDateView(questionnaireViewItem) }
      //
      //    onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree =
      // true).assertTextEquals("11/19/2020")
    }

  @Test
  fun showDateFormatLabelInLowerCase() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
          extension =
            listOf(
              Extension(
                url = EXTENSION_ENTRY_FORMAT_URL,
                value = Extension.Value.String(value = FhirR4String(value = "mm/dd/yyyy")),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDateView(questionnaireViewItem) }

    onNodeWithTag(DATE_TEXT_INPUT_FIELD).assertTextEquals("mm/dd/yyyy", includeEditableText = false)
  }

  @Test
  @Ignore
  fun shouldSetDateInput_localeJp() =
    runComposeUiTest {
      //    setLocale(Locale.JAPAN)
      //      val questionnaireViewItem = QuestionnaireViewItem(
      //          Questionnaire.Item(
      //              linkId = FhirR4String(value = "date-item"),
      //              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
      //              text = FhirR4String(value = "Question?")
      //          ),
      //          QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item"),
      //              answer = listOf(
      //                  QuestionnaireResponse.Item.Answer(
      //                      value = DateAnswerValue(value = FhirR4DateType(value =
      // FhirDate.Date(LocalDate(2020, 10, 19))))
      //                  )
      //              )),
      //          validationResult = NotValidated,
      //
      //          answersChangedCallback = { _, _, _, _ -> },
      //      )
      //
      //    setContent { QuestionnaireDateView(questionnaireViewItem) }
      //    onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
      //      .assertTextEquals("2020/11/19")
    }

  @Test
  @Ignore
  fun shouldSetDateInput_localeEn() =
    runComposeUiTest {
      //    setLocale(Locale.ENGLISH)
      //      val questionnaireViewItem = QuestionnaireViewItem(
      //          Questionnaire.Item(
      //              linkId = FhirR4String(value = "date-item"),
      //              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
      //              text = FhirR4String(value = "Question?")
      //          ),
      //          QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item"),
      //                            answer = listOf(
      //                  QuestionnaireResponse.Item.Answer(
      //                      value = DateAnswerValue(value = FhirR4DateType(value =
      // FhirDate.Date(LocalDate(2020, 10, 19))))
      //                  )
      //              )),
      //          validationResult = NotValidated,
      //          answersChangedCallback = { _, _, _, _ -> },
      //      )
      //
      //      setContent { QuestionnaireDateView(questionnaireViewItem) }
      //
      //    onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
      //      .assertTextEquals("11/19/2020")
    }

  @Test
  fun shouldSetDateInput() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
          text = FhirR4String(value = "Question?"),
          extension =
            listOf(
              Extension(
                url = EXTENSION_ENTRY_FORMAT_URL,
                value = Extension.Value.String(value = FhirR4String(value = "dd/MM/yyyy")),
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
                    value = FhirR4DateType(value = FhirDate.Date(LocalDate(2020, 11, 19))),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDateView(questionnaireViewItem) }

    onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("19/11/2020")
  }

  @Test
  @Ignore
  fun parseDateTextInputInUsLocale() =
    runComposeUiTest {
      //    setLocale(Locale.US)
      //    var answers: List<QuestionnaireResponse.Item.Answer>? = null
      //    val item =
      //      QuestionnaireViewItem(
      //        Questionnaire.Item(
      //          linkId = FhirR4String(value = "date-item"),
      //          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
      //        ),
      //        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
      //        validationResult = NotValidated,
      //        answersChangedCallback = { _, _, result, _ -> answers = result },
      //      )
      //
      //    setContent { QuestionnaireDateView(item) }
      //    onNodeWithTag(DATE_TEXT_INPUT_FIELD).performTextInput("11/19/2020")
      //    waitUntil { answers != null }
      //
      //    val answer = (answers!!.single().value?.asDate()?.value?.value as? FhirDate.Date)?.date
      //
      //      answer?.day.shouldBe(19)
      //      answer?.month.shouldBe(10)
      //      answer?.year.shouldBe(2020)
    }

  @Test
  @Ignore
  fun parseDateTextInputInJapanLocale() =
    runComposeUiTest {
      //    setLocale(Locale.JAPAN)
      //    var answers: List<QuestionnaireResponse.Item.Answer>? = null
      //    val item =
      //      QuestionnaireViewItem(
      //        Questionnaire.Item(
      //          linkId = FhirR4String(value = "date-item"),
      //          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
      //        ),
      //        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
      //        validationResult = NotValidated,
      //        answersChangedCallback = { _, _, result, _ -> answers = result },
      //      )
      //      setContent { QuestionnaireDateView(item) }
      //    onNodeWithTag(DATE_TEXT_INPUT_FIELD).performTextInput("2020/11/19")
      //    waitUntil { answers != null }
      //    val answer = (answers!!.single().value?.asDate()?.value?.value as? FhirDate.Date)?.date
      //
      //      answer?.day.shouldBe(19)
      //      answer?.month.shouldBe(10)
      //      answer?.year.shouldBe(2020)
    }

  @Test
  fun parseDateTextInput() = runComposeUiTest {
    var answers: List<QuestionnaireResponse.Item.Answer>? = null
    val item =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
          extension =
            listOf(
              Extension(
                url = EXTENSION_ENTRY_FORMAT_URL,
                value = Extension.Value.String(value = FhirR4String(value = "MM/dd/yyyy")),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result, _ -> answers = result },
      )

    setContent { QuestionnaireDateView(item) }
    onNodeWithTag(DATE_TEXT_INPUT_FIELD).performTextInput("11/19/2020")
    waitUntil { answers != null }

    val answer = (answers!!.single().value?.asDate()?.value?.value as? FhirDate.Date)?.date

    answer.shouldNotBeNull()
    answer.day.shouldBe(19)
    answer.month.number.shouldBe(11)
    answer.year.shouldBe(2020)
  }

  @Test
  fun clearTheAnswerIfDateInputIsInvalid() = runComposeUiTest {
    var answers: List<QuestionnaireResponse.Item.Answer>? = null
    val questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
          extension =
            listOf(
              Extension(
                url = EXTENSION_ENTRY_FORMAT_URL,
                value = Extension.Value.String(value = FhirR4String(value = "MM/dd/yyyy")),
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
                    value = FhirR4DateType(value = FhirDate.Date(LocalDate(2020, 11, 19))),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result, _ -> answers = result },
      )
    setContent { QuestionnaireDateView(questionnaireItem) }
    onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("11/19/2020")
    val dateTextInput = "11/19"
    onNodeWithTag(DATE_TEXT_INPUT_FIELD).performSemanticsAction(
      SemanticsActions.SetText,
    ) {
      it(dateTextInput.toAnnotatedString())
    }
    waitUntil { answers != null }
    answers.shouldBeEmpty()
  }

  @Test
  fun doNotClearTheTextFieldInputForInvalidDate() = runComposeUiTest {
    val questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
          extension =
            listOf(
              Extension(
                url = EXTENSION_ENTRY_FORMAT_URL,
                value = Extension.Value.String(value = FhirR4String(value = "MM/dd/yyyy")),
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
                    value = FhirR4DateType(value = FhirDate.Date(LocalDate(2020, 11, 19))),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    setContent { QuestionnaireDateView(questionnaireItem) }
    onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("11/19/2020")
    onNodeWithTag(DATE_TEXT_INPUT_FIELD).performSemanticsAction(
      SemanticsActions.SetText,
    ) {
      it("11/19".toAnnotatedString())
    }
    onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("11/19")
  }

  @Test
  fun clearQuestionnaireResponseAnswerOnDraftAnswerUpdate() = runComposeUiTest {
    var answers: List<QuestionnaireResponse.Item.Answer>? =
      listOf(QuestionnaireResponse.Item.Answer())
    val questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
          extension =
            listOf(
              Extension(
                url = EXTENSION_ENTRY_FORMAT_URL,
                value = Extension.Value.String(value = FhirR4String(value = "MM/dd/yyyy")),
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
                    value = FhirR4DateType(value = FhirDate.Date(LocalDate(2020, 10, 19))),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answersForCallback, _ -> answers = answersForCallback },
      )

    setContent { QuestionnaireDateView(questionnaireItem) }
    questionnaireItem.setDraftAnswer("02/07")
    waitUntil { answers != null }
    answers.shouldBeEmpty()
  }

  @Test
  fun clearDraftValueOnAnValidAnswerUpdate() = runComposeUiTest {
    val answer =
      QuestionnaireResponse.Item.Answer(
        value =
          DateAnswerValue(value = FhirR4DateType(value = FhirDate.Date(LocalDate(2026, 1, 1)))),
      )
    var partialValue: String? = "02/07"
    val questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
          extension =
            listOf(
              Extension(
                url = EXTENSION_ENTRY_FORMAT_URL,
                value = Extension.Value.String(value = FhirR4String(value = "MM/dd/yyyy")),
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
                    value = FhirR4DateType(value = FhirDate.Date(LocalDate(2020, 11, 19))),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, partialAnswer ->
          partialValue = partialAnswer as? String
        },
      )

    setContent { QuestionnaireDateView(questionnaireItem) }
    questionnaireItem.setAnswer(answer)
    partialValue.shouldBeNull()
  }

  @Test
  fun displayPartialAnswerInTheTextFieldOfRecycledItems() = runComposeUiTest {
    var questionnaireItem by
      mutableStateOf(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "date-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
            extension =
              listOf(
                Extension(
                  url = EXTENSION_ENTRY_FORMAT_URL,
                  value = Extension.Value.String(value = FhirR4String(value = "MM/dd/yyyy")),
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
                      value = FhirR4DateType(value = FhirDate.Date(LocalDate(2020, 11, 19))),
                    ),
                ),
              ),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )

    setContent { QuestionnaireDateView(questionnaireItem) }
    onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("11/19/2020")

    questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
          extension =
            listOf(
              Extension(
                url = EXTENSION_ENTRY_FORMAT_URL,
                value = Extension.Value.String(value = FhirR4String(value = "MM/dd/yyyy")),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "02/07",
      )

    onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("02/07")
  }

  @Test
  fun displayAnAnswerInTheTextFieldOfPartiallyAnsweredRecycledItem() = runComposeUiTest {
    var questionnaireItem by
      mutableStateOf(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "date-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
            extension =
              listOf(
                Extension(
                  url = EXTENSION_ENTRY_FORMAT_URL,
                  value = Extension.Value.String(value = FhirR4String(value = "MM/dd/yyyy")),
                ),
              ),
          ),
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
          draftAnswer = "02/07",
        ),
      )

    setContent { QuestionnaireDateView(questionnaireItem) }
    onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("02/07")

    questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
          extension =
            listOf(
              Extension(
                url = EXTENSION_ENTRY_FORMAT_URL,
                value = Extension.Value.String(value = FhirR4String(value = "MM/dd/yyyy")),
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
                    value = FhirR4DateType(value = FhirDate.Date(LocalDate(2020, 11, 19))),
                  ),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("11/19/2020")
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
                    value = FhirR4DateType(value = FhirDate.fromString("2020-01-01")),
                  ),
              ),
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/maxValue",
                value =
                  Extension.Value.Date(
                    value = FhirR4DateType(value = FhirDate.fromString("2025-01-01")),
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
                    value = FhirR4DateType(value = FhirDate.fromString("2026-01-01")),
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
          extension =
            listOf(
              Extension(
                url = EXTENSION_ENTRY_FORMAT_URL,
                value = Extension.Value.String(value = FhirR4String(value = "MM/dd/yyyy")),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "11/19/202",
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
                    value = FhirR4DateType(value = FhirDate.fromString("2020-01-01")),
                  ),
              ),
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/maxValue",
                value =
                  Extension.Value.Date(
                    value = FhirR4DateType(value = FhirDate.fromString("2025-01-01")),
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
                    value = FhirR4DateType(value = FhirDate.fromString("2023-01-01")),
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
          readOnly = FhirR4Boolean(value = true),
        ),
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
      var questionnaireViewItem by
        mutableStateOf(
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "date-item"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
              text = FhirR4String(value = "Question?"),
              extension =
                listOf(
                  Extension(
                    url = EXTENSION_ENTRY_FORMAT_URL,
                    value = Extension.Value.String(value = FhirR4String(value = "MM/dd/yyyy")),
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
                        value = FhirR4DateType(value = FhirDate.Date(LocalDate(2020, 11, 19))),
                      ),
                  ),
                ),
            ),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
        )

      setContent { QuestionnaireDateView(questionnaireViewItem) }
      onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("11/19/2020")

      questionnaireViewItem =
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "date-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
            text = FhirR4String(value = "Question?"),
            extension =
              listOf(
                Extension(
                  url = EXTENSION_ENTRY_FORMAT_URL,
                  value = Extension.Value.String(value = FhirR4String(value = "MM/dd/yyyy")),
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
                      value = FhirR4DateType(value = FhirDate.Date(LocalDate(2021, 11, 19))),
                    ),
                ),
              ),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        )

      onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("11/19/2021")

      questionnaireViewItem =
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "date-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
            text = FhirR4String(value = "Question?"),
            extension =
              listOf(
                Extension(
                  url = EXTENSION_ENTRY_FORMAT_URL,
                  value = Extension.Value.String(value = FhirR4String(value = "MM/dd/yyyy")),
                ),
              ),
          ),
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        )

      onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("")
    }

  @Test
  fun shouldUseDateFormatInTheEntryFormatExtension() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
          extension =
            listOf(
              Extension(
                url = EXTENSION_ENTRY_FORMAT_URL,
                value = Extension.Value.String(value = FhirR4String(value = "yyyy-MM-dd")),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDateView(questionnaireViewItem) }
    onNodeWithTag(DATE_TEXT_INPUT_FIELD).assertTextEquals("yyyy-mm-dd", includeEditableText = false)
  }

  @Test
  @Ignore
  fun shouldSetLocalDateInputFormatWhenEntryFormatExtensionHasIncorrectFormatStringInQuestionnaire() =
    runComposeUiTest {
      //      setLocale(Locale.US)
      //        val questionnaireViewItem = QuestionnaireViewItem(
      //            Questionnaire.Item(
      //                linkId = FhirR4String(value = "date-item"),
      //                type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
      //                extension = listOf(
      //                    Extension(url = EXTENSION_ENTRY_FORMAT_URL, value =
      // Extension.Value.String(value = FhirR4String(value = "yMyd")))
      //                )
      //            ),
      //            QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
      //            validationResult = NotValidated,
      //            answersChangedCallback = { _, _, _, _ -> },
      //        )
      //
      //        setContent { QuestionnaireDateView(questionnaireViewItem) }
      //      onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      //        .assertTextEquals("mm/dd/yyyy", includeEditableText = false)
    }

  @Test
  fun shouldUseDateFormatInTheEntryFormatExtensionThoughDateSeparatorIsMissing() =
    runComposeUiTest {
      val questionnaireViewItem =
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "date-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
            extension =
              listOf(
                Extension(
                  url = EXTENSION_ENTRY_FORMAT_URL,
                  value = Extension.Value.String(value = FhirR4String(value = "yyyyMMdd")),
                ),
              ),
          ),
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        )

      setContent { QuestionnaireDateView(questionnaireViewItem) }
      onNodeWithTag(DATE_TEXT_INPUT_FIELD).assertTextEquals("yyyymmdd", includeEditableText = false)
    }

  @Test
  fun shouldUseDateFormatInTheEntryFormatAfterConvertingItToShortFormatStyle() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
          extension =
            listOf(
              Extension(
                url = EXTENSION_ENTRY_FORMAT_URL,
                value = Extension.Value.String(value = FhirR4String(value = "yyyy MMMM dd")),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireDateView(questionnaireViewItem) }
    onNodeWithTag(DATE_TEXT_INPUT_FIELD).assertTextEquals("yyyy mm dd", includeEditableText = false)
  }

  @Test
  @Ignore
  fun shouldSetLocalDateInputFormatWhenEntryFormatExtensionHasEmptyStringInQuestionnaire() =
    runComposeUiTest {
      //      setLocale(Locale.US)
      //        val questionnaireViewItem = QuestionnaireViewItem(
      //            Questionnaire.Item(
      //                linkId = FhirR4String(value = "date-item"),
      //                type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
      //                extension = listOf(
      //                    Extension(url = EXTENSION_ENTRY_FORMAT_URL, value =
      // Extension.Value.String(value = FhirR4String(value = "")))
      //                )
      //            ),
      //            QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
      //            validationResult = NotValidated,
      //            answersChangedCallback = { _, _, _, _ -> },
      //        )
      //
      //        setContent { QuestionnaireDateView(questionnaireViewItem) }
      //      onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      //        .assertTextEquals("mm/dd/yyyy", includeEditableText = false)
    }

  @Test
  @Ignore
  fun shouldSetLocalDateInputFormatWhenNoEntryFormatExtensionInQuestionnaire() =
    runComposeUiTest {
      //    setLocale(Locale.US)
      //      val questionnaireViewItem = QuestionnaireViewItem(
      //          Questionnaire.Item(
      //              linkId = FhirR4String(value = "date-item"),
      //              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
      //          ),
      //          QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
      //          validationResult = NotValidated,
      //          answersChangedCallback = { _, _, _, _ -> },
      //      )
      //
      //      setContent { QuestionnaireDateView(questionnaireViewItem) }
      //    onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      //      .assertTextEquals("mm/dd/yyyy", includeEditableText = false)
    }

  @Test
  fun clearUiTextClearsQuestionnaireResponseAnswerAndDraftAnswer() = runComposeUiTest {
    var questionnaireResponseAnswers: List<QuestionnaireResponse.Item.Answer>? = null
    var draftAnswer: String? = null

    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "date-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Date),
          extension =
            listOf(
              Extension(
                url = EXTENSION_ENTRY_FORMAT_URL,
                value = Extension.Value.String(value = FhirR4String(value = "MM/dd/yyyy")),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "date-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, draft ->
          questionnaireResponseAnswers = answers
          draftAnswer = draft as? String
        },
      )

    setContent { QuestionnaireDateView(questionnaireViewItem) }

    onNodeWithTag(DATE_TEXT_INPUT_FIELD).performTextInput("11/19/2020")
    waitUntil { questionnaireResponseAnswers != null }
    val dateAnswer =
      (questionnaireResponseAnswers!!.single().value?.asDate()?.value?.value as? FhirDate.Date)
        ?.date
    dateAnswer.shouldBe(LocalDate(2020, 11, 19))

    questionnaireResponseAnswers = null
    onNodeWithTag(DATE_TEXT_INPUT_FIELD).performTextReplacement("11/19")
    waitUntil { questionnaireResponseAnswers != null }
    draftAnswer.shouldNotBeNull()
    draftAnswer.shouldBe("11/19")

    questionnaireResponseAnswers = null
    onNodeWithTag(DATE_TEXT_INPUT_FIELD).performTextClearance()
    waitUntil { questionnaireResponseAnswers != null }
    questionnaireResponseAnswers.shouldBeEmpty()
    assertTrue { draftAnswer.isNullOrEmpty() }
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

    onNodeWithTag(DATE_TEXT_INPUT_FIELD).assertTextContains("Required", substring = true)
  }

  @Test
  fun hideRequiredText() = runComposeUiTest {
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

  //  private fun setLocale(locale: Locale) {
  //    Locale.setDefault(locale)
  //    parent.context.resources.configuration.setLocale(locale)
  //  }
}
