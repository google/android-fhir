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

package com.google.android.fhir.datacapture.test.views

import android.text.format.DateFormat
import android.widget.FrameLayout
import android.widget.TextView
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.test.TestActivity
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionTextConfiguration
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.DATE_TEXT_INPUT_FIELD
import com.google.android.fhir.datacapture.views.compose.ERROR_TEXT_AT_HEADER_TEST_TAG
import com.google.android.fhir.datacapture.views.compose.TIME_PICKER_INPUT_FIELD
import com.google.android.fhir.datacapture.views.factories.DateTimePickerViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolder
import com.google.common.truth.Truth.assertThat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DateTimePickerViewHolderFactoryTest {
  @get:Rule
  val activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

  @get:Rule val composeTestRule = createEmptyComposeRule()

  private lateinit var viewHolder: QuestionnaireItemViewHolder

  @Before
  fun setUp() {
    Locale.setDefault(Locale.US)
    activityScenarioRule.scenario.onActivity { activity ->
      viewHolder = DateTimePickerViewHolderFactory.create(FrameLayout(activity))
      activity.setContentView(viewHolder.itemView)
    }

    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
  }

  @Test
  fun shouldSetQuestionHeader() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    // Synchronize
    composeTestRule.waitForIdle()

    assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString(),
      )
      .isEqualTo("Question?")
  }

  @Test
  fun shouldSetEmptyDateTimeInput() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
      .assertTextEquals("")
    composeTestRule
      .onNodeWithTag(TIME_PICKER_INPUT_FIELD, useUnmergedTree = true)
      .assertTextEquals("")
  }

  @Test
  fun showDateFormatLabelInLowerCase() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .assertTextEquals("mm/dd/yyyy", includeEditableText = false)
  }

  @Test
  fun shouldSetDateTimeInput() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0))),
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
      .assertTextEquals("02/05/2020")
    val is24Hour = DateFormat.is24HourFormat(viewHolder.itemView.context)
    val expectedTime = if (is24Hour) "01:30" else "1:30 AM"
    composeTestRule
      .onNodeWithTag(TIME_PICKER_INPUT_FIELD, useUnmergedTree = true)
      .assertTextEquals(expectedTime)
  }

  @Test
  fun parseDateTextInputInUSLocale() {
    var answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent? = null
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0))),
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result, _ -> answer = result.singleOrNull() },
      )
    viewHolder.bind(itemViewItem)

    composeTestRule.onNodeWithTag(DATE_TEXT_INPUT_FIELD).performTextReplacement("11/19/2020")
    composeTestRule.waitUntil { answer != null }

    val dateTime = answer!!.value as DateTimeType
    assertThat(dateTime.day).isEqualTo(19)
    assertThat(dateTime.month).isEqualTo(10)
    assertThat(dateTime.year).isEqualTo(2020)
  }

  @Test
  fun parseDateTextInputInJapanLocale() {
    Locale.setDefault(Locale.JAPAN)
    var answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent? = null
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0))),
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result, _ -> answer = result.singleOrNull() },
      )
    viewHolder.bind(itemViewItem)

    composeTestRule.onNodeWithTag(DATE_TEXT_INPUT_FIELD).performTextReplacement("2020/11/19")
    composeTestRule.waitUntil { answer != null }

    val dateTime = answer!!.value as DateTimeType
    assertThat(dateTime.day).isEqualTo(19)
    assertThat(dateTime.month).isEqualTo(10)
    assertThat(dateTime.year).isEqualTo(2020)
  }

  @Test
  fun ifDateInputIsInvalidThenClearTheAnswer() {
    Locale.setDefault(Locale.JAPAN)
    var answers: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    var draftAnswer: Any? = null
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0))),
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result, draft ->
          answers = result
          draftAnswer = draft
        },
      )
    viewHolder.bind(itemViewItem)

    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .performTextReplacement("202011") // transforms to 2020/11 for Locale.JAPAN
    composeTestRule.waitUntil { answers != null }

    assertThat(answers!!).isEmpty()
    assertThat(draftAnswer as String).isEqualTo("202011")
  }

  @Test
  fun doNotClearTheTextFieldInputOnInvalidDate() {
    Locale.setDefault(Locale.JAPAN)
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0))),
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    viewHolder.bind(itemViewItem)
    composeTestRule.onNodeWithTag(DATE_TEXT_INPUT_FIELD).performTextReplacement("2020/11")

    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
      .assertTextEquals("2020/11")
  }

  @Test
  fun clearQuestionnaireResponseAnswerOnDraftAnswerUpdate() {
    var answers: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? =
      listOf(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent())
    val questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0))),
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answersForCallback, _ -> answers = answersForCallback },
      )

    viewHolder.bind(questionnaireItem)
    runBlocking {
      questionnaireItem.setDraftAnswer("0207")
    } // would transform to 02/07/ for default locale
    assertThat(answers!!).isEmpty()
  }

  @Test
  fun clearDraftAnswerOnAnValidAnswerUpdate() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(DateTimeType(Date(2020 - 1900, 2, 6, 2, 30, 0)))
    var draft: String? = "02/07"
    val questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0))),
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, draftAnswer -> draft = draftAnswer as? String },
      )

    viewHolder.bind(questionnaireItem)
    runBlocking { questionnaireItem.setAnswer(answer) }
    assertThat(draft).isNull()
  }

  @Test
  fun displayDraftAnswerInTheTextFieldOfRecycledItems() {
    var questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0))),
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    viewHolder.bind(questionnaireItem)
    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
      .assertTextEquals("02/05/2020")

    questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
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
  fun displayAnAnswerInTheTextFieldOfPartiallyAnsweredRecycledItem() {
    var questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
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
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0))),
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    viewHolder.bind(questionnaireItem)
    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
      .assertTextEquals("02/05/2020")
  }

  @Test
  fun ifDraftAnswerInputIsInvalidThenDoNotEnableTimeTextInputLayout() {
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "1119", // would transform to 11/19/ for default locale
      )

    viewHolder.bind(itemViewItem)
    composeTestRule.onNodeWithTag(TIME_PICKER_INPUT_FIELD).assertIsNotEnabled()
  }

  @Test
  fun ifTheDraftAnswerInputIsEmptyDoNotEnableTheTimeTextInputLayout() {
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "",
      )

    viewHolder.bind(itemViewItem)
    composeTestRule.onNodeWithTag(TIME_PICKER_INPUT_FIELD).assertIsNotEnabled()
  }

  @Test
  fun ifThereIsNoAnswerOrDraftAnswerDoNotEnableTheTimeTextInputLayout() {
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = null,
      )

    viewHolder.bind(itemViewItem)
    composeTestRule.onNodeWithTag(TIME_PICKER_INPUT_FIELD).assertIsNotEnabled()
  }

  @Test
  fun ifDateDraftAnswerIsValidThenEnableTimeTextInputLayout() {
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "11192020", // transforms to 11/19/2020 for default locale
      )

    viewHolder.bind(itemViewItem)
    composeTestRule.onNodeWithTag(TIME_PICKER_INPUT_FIELD).assertIsNotEnabled()
  }

  @Test
  fun displayValidationResult_error_shouldShowErrorMessage() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Invalid(listOf("Missing answer for required field.")),
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .assert(
        SemanticsMatcher.expectValue(
          SemanticsProperties.Error,
          "Missing answer for required field.",
        ),
      )
  }

  @Test
  fun displayValidationResult_noError_shouldShowNoErrorMessage() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/minValue"
            setValue((DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0))))
          }
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/maxValue"
            setValue((DateTimeType(Date(2025 - 1900, 1, 5, 1, 30, 0))))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = (DateTimeType(Date(2023 - 1900, 1, 5, 1, 30, 0)))
            },
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Error))
    composeTestRule
      .onNodeWithTag(TIME_PICKER_INPUT_FIELD)
      .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Error))
  }

  @Test
  fun ifTheDraftAnswerIsInvalidDisplayTheErrorMessage() {
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "1119202", // transforms to 11/19/202
      )

    viewHolder.bind(itemViewItem)

    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .assert(
        SemanticsMatcher.expectValue(
          SemanticsProperties.Error,
          "Date format needs to be mm/dd/yyyy (e.g. 01/31/2023)",
        ),
      )
  }

  @Test
  fun showDateFormatInLowerCaseInTheErrorMessage() {
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "1119202", // transforms to 11/19/202
      )

    viewHolder.bind(itemViewItem)
    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .assert(
        SemanticsMatcher.expectValue(
          SemanticsProperties.Error,
          "Date format needs to be mm/dd/yyyy (e.g. 01/31/2023)",
        ),
      )
  }

  @Test
  fun hidesErrorTextviewInTheHeader() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule
      .onNodeWithTag(ERROR_TEXT_AT_HEADER_TEST_TAG)
      .assertIsNotDisplayed()
      .assertDoesNotExist()
  }

  @Test
  fun bind_readOnly_shouldDisableView() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { readOnly = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule.onNodeWithTag(DATE_TEXT_INPUT_FIELD).assertIsNotEnabled()
    composeTestRule.onNodeWithTag(TIME_PICKER_INPUT_FIELD).assertIsNotEnabled()
  }

  @Test
  fun bindMultipleTimesWithSeparateQuestionnaireItemViewItemShouldShowProperDateAndTime() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2020 - 1900, 1, 5, 1, 30, 0))),
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
      .assertTextEquals("02/05/2020")
    composeTestRule
      .onNodeWithTag(TIME_PICKER_INPUT_FIELD, useUnmergedTree = true)
      .assertTextEquals("1:30 AM")

    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateTimeType(Date(2021 - 1900, 1, 5, 2, 30, 0))),
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
      .assertTextEquals("02/05/2021")
    composeTestRule
      .onNodeWithTag(TIME_PICKER_INPUT_FIELD, useUnmergedTree = true)
      .assertTextEquals("2:30 AM")

    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
      .assertTextEquals("")
    composeTestRule
      .onNodeWithTag(TIME_PICKER_INPUT_FIELD, useUnmergedTree = true)
      .assertTextEquals("")
  }

  @Test
  fun showsAsterisk() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          text = "Question?"
          required = true
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = true),
      ),
    )
    // Synchronize
    composeTestRule.waitForIdle()

    assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString(),
      )
      .isEqualTo("Question? *")
  }

  @Test
  fun hideAsterisk() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          text = "Question?"
          required = true
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = false),
      ),
    )

    // Synchronize
    composeTestRule.waitForIdle()

    assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString(),
      )
      .isEqualTo("Question?")
  }

  @Test
  fun showsRequiredText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = true),
      ),
    )
    composeTestRule.onNodeWithTag(DATE_TEXT_INPUT_FIELD).assertTextContains("Required")
  }

  @Test
  fun hideRequiredText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = false),
      ),
    )

    composeTestRule.onNodeWithText("Required").assertIsNotDisplayed().assertDoesNotExist()
  }

  @Test
  fun showsOptionalText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = true),
      ),
    )

    composeTestRule.onNodeWithTag(DATE_TEXT_INPUT_FIELD).assertTextContains("Optional")
  }

  @Test
  fun hideOptionalText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = false),
      ),
    )

    composeTestRule.onNodeWithText("Optional").assertIsNotDisplayed().assertDoesNotExist()
  }
}
