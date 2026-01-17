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

package com.google.android.fhir.datacapture.test.views

import android.widget.FrameLayout
import android.widget.TextView
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.EXTENSION_ENTRY_FORMAT_URL
import com.google.android.fhir.datacapture.extensions.toAnnotatedString
import com.google.android.fhir.datacapture.test.TestActivity
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionTextConfiguration
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.components.DATE_TEXT_INPUT_FIELD
import com.google.android.fhir.datacapture.views.components.ERROR_TEXT_AT_HEADER_TEST_TAG
import com.google.android.fhir.datacapture.views.factories.DatePickerViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolder
import com.google.common.truth.Truth.assertThat
import java.util.Locale
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatePickerViewHolderFactoryTest {
  @get:Rule
  val activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

  @get:Rule val composeTestRule = createEmptyComposeRule()

  private lateinit var viewHolder: QuestionnaireItemViewHolder
  private lateinit var parent: FrameLayout

  @Before
  fun setUp() {
    activityScenarioRule.scenario.onActivity { activity ->
      parent = FrameLayout(activity)
      viewHolder = DatePickerViewHolderFactory.create(parent)
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

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question?")
  }

  @Test
  fun shouldSetEmptyDateInput() {
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
  }

  @Test
  fun shouldSetTextFieldEmptyWhenDateFieldIsInitializedButAnswerDateValueIsNull() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(DateType()),
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
      .assertTextEquals("")
  }

  @Test
  fun shouldSetDateInput_localeUs() {
    setLocale(Locale.US)
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
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
  fun showDateFormatLabelInLowerCase() {
    setLocale(Locale.US)
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
  fun shouldSetDateInput_localeJp() {
    setLocale(Locale.JAPAN)
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
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
  fun shouldSetDateInput_localeEn() {
    setLocale(Locale.ENGLISH)
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
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
  fun parseDateTextInputInUsLocale() {
    setLocale(Locale.US)
    var answers: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val item =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result, _ -> answers = result },
      )

    viewHolder.bind(item)
    composeTestRule.onNodeWithTag(DATE_TEXT_INPUT_FIELD).performTextInput("11/19/2020")
    composeTestRule.waitUntil { answers != null }

    val answer = answers!!.single().value as DateType

    assertThat(answer.day).isEqualTo(19)
    assertThat(answer.month).isEqualTo(10)
    assertThat(answer.year).isEqualTo(2020)
  }

  @Test
  fun parseDateTextInputInJapanLocale() {
    setLocale(Locale.JAPAN)
    var answers: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val item =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result, _ -> answers = result },
      )
    viewHolder.bind(item)
    composeTestRule.onNodeWithTag(DATE_TEXT_INPUT_FIELD).performTextInput("2020/11/19")
    composeTestRule.waitUntil { answers != null }
    val answer = answers!!.single().value as DateType

    assertThat(answer.day).isEqualTo(19)
    assertThat(answer.month).isEqualTo(10)
    assertThat(answer.year).isEqualTo(2020)
  }

  @Test
  fun clearTheAnswerIfDateInputIsInvalid() {
    setLocale(Locale.US)
    var answers: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
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
    composeTestRule.onNodeWithTag(DATE_TEXT_INPUT_FIELD).performSemanticsAction(
      SemanticsActions.SetText,
    ) {
      it(dateTextInput.toAnnotatedString())
    }
    composeTestRule.waitUntil { answers != null }

    assertThat(answers!!).isEmpty()
  }

  @Test
  fun doNotClearTheTextFieldInputForInvalidDate() {
    setLocale(Locale.US)
    val questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
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
    composeTestRule.onNodeWithTag(DATE_TEXT_INPUT_FIELD).performSemanticsAction(
      SemanticsActions.SetText,
    ) {
      it("11/19".toAnnotatedString())
    }
    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
      .assertTextEquals("11/19")
  }

  @Test
  fun clearQuestionnaireResponseAnswerOnDraftAnswerUpdate() {
    var answers: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? =
      listOf(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent())
    setLocale(Locale.US)
    val questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
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
  fun clearDraftValueOnAnValidAnswerUpdate() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(DateType(2026, 0, 1))
    var partialValue: String? = "02/07"
    setLocale(Locale.US)
    val questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
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
  fun displayPartialAnswerInTheTextFieldOfRecycledItems() {
    setLocale(Locale.US)
    var questionnaireItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
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
    setLocale(Locale.US)
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
  fun displayValidationResult_error_shouldShowErrorMessage() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          required = true
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/minValue"
            setValue(DateType(2020, 0, 1))
          }
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/maxValue"
            setValue(DateType(2025, 0, 1))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateType(2026, 0, 1)),
          )
        },
        validationResult = Invalid(listOf("Maximum value allowed is:2025-01-01")),
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .assert(
        SemanticsMatcher.expectValue(
          SemanticsProperties.Error,
          "Maximum value allowed is:2025-01-01",
        ),
      )
  }

  @Test
  fun showDateFormatInLowercaseInTheErrorMessage() {
    val itemViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "1119202",
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
  fun displayValidationResult_noError_shouldShowNoErrorMessage() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/minValue"
            setValue(DateType(2020, 0, 1))
          }
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/maxValue"
            setValue(DateType(2025, 0, 1))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DateType(2023, 0, 1)),
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Error))
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
    composeTestRule
      .onNodeWithContentDescription(viewHolder.itemView.context.getString(R.string.select_date))
      .assertIsNotEnabled()
  }

  @Test
  fun bindMultipleTimesWithDifferentQuestionnaireItemViewItemShouldShowProperDate() {
    setLocale(Locale.US)

    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
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
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
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
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
      .assertTextEquals("")
  }

  @Test
  fun shouldUseDateFormatInTheEntryFormatExtension() {
    setLocale(Locale.US)
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension(EXTENSION_ENTRY_FORMAT_URL, StringType("yyyy-MM-dd"))
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .assertTextEquals("yyyy-mm-dd", includeEditableText = false)
  }

  @Test
  fun shouldSetLocalDateInputFormatWhenEntryFormatExtensionHasIncorrectFormatStringInQuestionnaire() {
    setLocale(Locale.US)
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension(EXTENSION_ENTRY_FORMAT_URL, StringType("yMyd"))
        },
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
  fun shouldUseDateFormatInTheEntryFormatExtensionThoughDateSeparatorIsMissing() {
    setLocale(Locale.US)
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension(EXTENSION_ENTRY_FORMAT_URL, StringType("yyyyMMdd"))
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .assertTextEquals("yyyymmdd", includeEditableText = false)
  }

  @Test
  fun shouldUseDateFormatInTheEntryFormatAfterConvertingItToShortFormatStyle() {
    setLocale(Locale.US)
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension(EXTENSION_ENTRY_FORMAT_URL, StringType("yyyy MMMM dd"))
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .assertTextEquals("yyyy mm dd", includeEditableText = false)
  }

  @Test
  fun shouldSetLocalDateInputFormatWhenEntryFormatExtensionHasEmptyStringInQuestionnaire() {
    setLocale(Locale.US)
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension(EXTENSION_ENTRY_FORMAT_URL, StringType(""))
        },
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
  fun shouldSetLocalDateInputFormatWhenNoEntryFormatExtensionInQuestionnaire() {
    setLocale(Locale.US)
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
  fun showAsterisk() {
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

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
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

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question?")
  }

  @Test
  fun showRequiredText() {
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

  private fun setLocale(locale: Locale) {
    Locale.setDefault(locale)
    parent.context.resources.configuration.setLocale(locale)
  }
}
