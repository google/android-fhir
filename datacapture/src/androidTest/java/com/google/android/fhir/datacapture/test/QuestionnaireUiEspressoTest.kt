/*
 * Copyright 2023-2025 Google LLC
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

package com.google.android.fhir.datacapture.test

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.fragment.app.commitNow
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.datacapture.QuestionnaireFragment
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.localDate
import com.google.android.fhir.datacapture.extensions.localDateTime
import com.google.android.fhir.datacapture.test.utilities.clickOnText
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseValidator
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.views.compose.DATE_TEXT_INPUT_FIELD
import com.google.android.fhir.datacapture.views.compose.EDIT_TEXT_FIELD_TEST_TAG
import com.google.android.fhir.datacapture.views.compose.HANDLE_INPUT_DEBOUNCE_TIME
import com.google.android.fhir.datacapture.views.compose.TIME_PICKER_INPUT_FIELD
import com.google.android.fhir.datacapture.views.factories.NO_CHOICE_RADIO_BUTTON_TAG
import com.google.android.fhir.datacapture.views.factories.YES_CHOICE_RADIO_BUTTON_TAG
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.allOf
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireUiEspressoTest {

  @get:Rule
  val activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

  @get:Rule val composeTestRule = createEmptyComposeRule()

  private lateinit var parent: FrameLayout
  private val parser: IParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
  private val context = InstrumentationRegistry.getInstrumentation().context

  @Before
  fun setup() {
    activityScenarioRule.scenario.onActivity { activity -> parent = FrameLayout(activity) }
  }

  @Test
  fun shouldDisplayReviewButtonWhenNoMorePagesToDisplay() {
    buildFragmentFromQuestionnaire("/paginated_questionnaire_with_dependent_answer.json", true)

    // synchronize
    composeTestRule.waitForIdle()
    onView(withId(R.id.review_mode_button))
      .check(
        ViewAssertions.matches(
          ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
        ),
      )

    composeTestRule.onNodeWithText("Yes").performClick()

    // synchronize
    composeTestRule.waitForIdle()
    onView(withId(R.id.review_mode_button))
      .check(
        ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)),
      )

    composeTestRule.onNodeWithText("No").performClick()

    // synchronize
    composeTestRule.waitForIdle()
    onView(withId(R.id.review_mode_button))
      .check(
        ViewAssertions.matches(
          ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
        ),
      )
  }

  @Test
  fun shouldHideNextButtonIfDisabled() {
    buildFragmentFromQuestionnaire("/layout_paginated.json", true)

    clickOnText("Next")

    onView(withId(R.id.pagination_next_button))
      .check(
        ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)),
      )
  }

  @Test
  fun shouldDisplayNextButtonIfEnabled() {
    buildFragmentFromQuestionnaire("/layout_paginated.json", true)

    onView(withId(R.id.pagination_next_button))
      .check(
        ViewAssertions.matches(
          ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
        ),
      )
  }

  @Test
  fun integerTextEdit_inputOutOfRange_shouldShowError() {
    buildFragmentFromQuestionnaire("/text_questionnaire_integer.json")

    runBlocking {
      composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextInput("12345678901")
      delay(HANDLE_INPUT_DEBOUNCE_TIME + 10L)
      composeTestRule
        .onNodeWithText("Number must be between -2,147,483,648 and 2,147,483,647")
        .assertIsDisplayed()
      composeTestRule.onNodeWithContentDescription("Error").assertIsDisplayed()
    }
  }

  @Test
  fun integerTextEdit_typingZeroBeforeAnyIntegerShouldKeepZeroDisplayed() {
    // Do not skip cursor when typing on the numeric field if the initial value is set to 0
    // as from an integer comparison, leading zeros do not change how the answer is saved.
    // e.g whether 000001 or 1 is input, the answer saved will be 1.
    buildFragmentFromQuestionnaire("/text_questionnaire_integer.json")

    runBlocking {
      composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextInput("0")
      delay(HANDLE_INPUT_DEBOUNCE_TIME + 10L)
      assertThat(getQuestionnaireResponse().item.first().answer.first().valueIntegerType.value)
        .isEqualTo(0)

      composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextInput("01")
      delay(HANDLE_INPUT_DEBOUNCE_TIME + 10L)
      assertThat(getQuestionnaireResponse().item.first().answer.first().valueIntegerType.value)
        .isEqualTo(1)

      composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("001")

      assertThat(getQuestionnaireResponse().item.first().answer.first().valueIntegerType.value)
        .isEqualTo(1)
    }
  }

  @Test
  fun decimalTextEdit_typingZeroBeforeAnyIntegerShouldKeepZeroDisplayed() {
    buildFragmentFromQuestionnaire("/text_questionnaire_decimal.json")

    runBlocking {
      composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextInput("0.")
      delay(HANDLE_INPUT_DEBOUNCE_TIME + 10L)
      assertThat(getQuestionnaireResponse().item.first().answer.first().valueDecimalType.value)
        .isEqualTo(BigDecimal.valueOf(0.0))

      composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextInput("01")
      delay(HANDLE_INPUT_DEBOUNCE_TIME + 10L)
      assertThat(getQuestionnaireResponse().item.first().answer.first().valueDecimalType.value)
        .isEqualTo(BigDecimal.valueOf(0.01))

      composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("0.01")

      assertThat(getQuestionnaireResponse().item.first().answer.first().valueDecimalType.value)
        .isEqualTo(BigDecimal.valueOf(0.01))
    }
  }

  @Test
  fun decimalTextEdit_typingInvalidTextShouldShowError() {
    buildFragmentFromQuestionnaire("/text_questionnaire_decimal.json")

    runBlocking {
      composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextInput("1.1.1.1")
      delay(HANDLE_INPUT_DEBOUNCE_TIME + 10L)

      composeTestRule.onNodeWithText("Invalid number").assertIsDisplayed()
      composeTestRule.onNodeWithContentDescription("Error").assertIsDisplayed()
    }
  }

  @Test
  fun dateTimePicker_shouldShowErrorForWrongDate() {
    buildFragmentFromQuestionnaire("/component_date_time_picker.json")

    runBlocking {
      // Add month and day. No need to add slashes as they are added automatically
      composeTestRule.onNodeWithTag(DATE_TEXT_INPUT_FIELD).performTextReplacement("0105")
      delay(HANDLE_INPUT_DEBOUNCE_TIME + 10L)

      composeTestRule
        .onNodeWithTag(DATE_TEXT_INPUT_FIELD)
        .assert(
          SemanticsMatcher.expectValue(
            SemanticsProperties.Error,
            "Date format needs to be mm/dd/yyyy (e.g. 01/31/2023)",
          ),
        )
      composeTestRule.onNodeWithTag(TIME_PICKER_INPUT_FIELD).assertIsNotEnabled()
    }
  }

  @Test
  fun dateTimePicker_shouldEnableTimePickerWithCorrectDate_butNotSaveInQuestionnaireResponse() {
    buildFragmentFromQuestionnaire("/component_date_time_picker.json")

    runBlocking {
      composeTestRule.onNodeWithTag(DATE_TEXT_INPUT_FIELD).performTextReplacement("01052005")
      delay(HANDLE_INPUT_DEBOUNCE_TIME + 10L)

      composeTestRule
        .onNodeWithTag(DATE_TEXT_INPUT_FIELD)
        .assert(
          SemanticsMatcher.keyNotDefined(
            SemanticsProperties.Error,
          ),
        )
      composeTestRule.onNodeWithTag(TIME_PICKER_INPUT_FIELD).assertIsEnabled()

      val questionnaireResponse = getQuestionnaireResponse()
      assertThat(questionnaireResponse.item.size).isEqualTo(1)
      assertThat(questionnaireResponse.item.first().answer.size).isEqualTo(1)
      val answer = questionnaireResponse.item.first().answer.first().valueDateTimeType
      assertThat(answer.localDateTime).isEqualTo(LocalDateTime.of(2005, 1, 5, 0, 0))
    }
  }

  @Test
  fun dateTimePicker_shouldSetAnswerWhenDateAndTimeAreFilled() {
    buildFragmentFromQuestionnaire("/component_date_time_picker.json")

    runBlocking {
      composeTestRule.onNodeWithTag(DATE_TEXT_INPUT_FIELD).performTextReplacement("01052005")
      delay(HANDLE_INPUT_DEBOUNCE_TIME + 10L)
      composeTestRule
        .onNodeWithTag(TIME_PICKER_INPUT_FIELD)
        .onChildren()
        .filterToOne(
          SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button),
        )
        .performClick()

      composeTestRule.onNodeWithText("AM").performClick()
      composeTestRule.onNodeWithContentDescription("Select hour", substring = true).performClick()
      composeTestRule.onNodeWithContentDescription("6 o'clock", substring = true).performClick()

      composeTestRule
        .onNodeWithContentDescription("Select minutes", substring = true)
        .performClick()
      composeTestRule.onNodeWithContentDescription("10 minutes", substring = true).performClick()

      composeTestRule.onNodeWithText("OK").performClick()
      // Synchronize
      composeTestRule.waitForIdle()

      val questionnaireResponse = getQuestionnaireResponse()
      val answer = questionnaireResponse.item.first().answer.first().valueDateTimeType
      // check Locale
      assertThat(answer.localDateTime).isEqualTo(LocalDateTime.of(2005, 1, 5, 6, 10))
    }
  }

  @Test
  fun datePicker_shouldShowErrorForWrongDate() {
    buildFragmentFromQuestionnaire("/component_date_picker.json")

    runBlocking {
      // Add month and day. No need to add slashes as they are added automatically
      composeTestRule.onNodeWithTag(DATE_TEXT_INPUT_FIELD).performTextInput("0105")
      delay(HANDLE_INPUT_DEBOUNCE_TIME + 10L)
      composeTestRule
        .onNodeWithTag(DATE_TEXT_INPUT_FIELD)
        .assert(
          SemanticsMatcher.expectValue(
            SemanticsProperties.Error,
            "Date format needs to be mm/dd/yyyy (e.g. 01/31/2023)",
          ),
        )
    }
  }

  @Test
  fun datePicker_shouldSaveInQuestionnaireResponseWhenCorrectDateEntered() {
    buildFragmentFromQuestionnaire("/component_date_picker.json")

    runBlocking {
      composeTestRule.onNodeWithTag(DATE_TEXT_INPUT_FIELD).performTextInput("01052005")
      delay(HANDLE_INPUT_DEBOUNCE_TIME + 10L)
      composeTestRule
        .onNodeWithTag(DATE_TEXT_INPUT_FIELD)
        .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Error))

      val answer = getQuestionnaireResponse().item.first().answer.first().valueDateType
      assertThat(answer.localDate).isEqualTo(LocalDate.of(2005, 1, 5))
    }
  }

  @Test
  fun datePicker_shouldSetDateInput_withinRange() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            type = Questionnaire.QuestionnaireItemType.DATE
            linkId = "link-1"
            addExtension().apply {
              url = "http://hl7.org/fhir/StructureDefinition/minValue"
              val minDate = DateType(Date()).apply { add(Calendar.YEAR, -1) }
              setValue(minDate)
            }
            addExtension().apply {
              url = "http://hl7.org/fhir/StructureDefinition/maxValue"
              val maxDate = DateType(Date()).apply { add(Calendar.YEAR, 4) }
              setValue(maxDate)
            }
          },
        )
      }

    buildFragmentFromQuestionnaire(questionnaire)
    composeTestRule
      .onNodeWithContentDescription(context.getString(R.string.select_date))
      .performClick()
    composeTestRule
      .onNode(hasText("OK") and hasAnyAncestor(isDialog()))
      .assertIsDisplayed()
      .performClick()
    composeTestRule.waitForIdle() // Synchronize

    val today = DateTimeType.today().valueAsString

    runBlocking {
      val answer =
        getQuestionnaireResponse().item.first().answer.first().valueDateType.valueAsString
      assertThat(answer).isEqualTo(today)

      val validationResult =
        QuestionnaireResponseValidator.validateQuestionnaireResponse(
          questionnaire,
          getQuestionnaireResponse(),
          context,
        )
      assertThat(validationResult["link-1"]?.first()).isEqualTo(Valid)
    }
  }

  @Test
  fun datePicker_shouldNotSetDateInput_outsideMaxRange() {
    val maxDate = DateType(Date()).apply { add(Calendar.YEAR, -2) }
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            type = Questionnaire.QuestionnaireItemType.DATE
            linkId = "link-1"
            addExtension().apply {
              url = "http://hl7.org/fhir/StructureDefinition/minValue"
              val minDate = DateType(Date()).apply { add(Calendar.YEAR, -4) }
              setValue(minDate)
            }
            addExtension().apply {
              url = "http://hl7.org/fhir/StructureDefinition/maxValue"
              setValue(maxDate)
            }
          },
        )
      }

    buildFragmentFromQuestionnaire(questionnaire)
    composeTestRule
      .onNodeWithContentDescription(context.getString(R.string.select_date))
      .performClick()
    composeTestRule
      .onNode(hasText("OK") and hasAnyAncestor(isDialog()))
      .assertIsDisplayed()
      .performClick()
    composeTestRule.waitForIdle() // Synchronize

    val maxDateAllowed = maxDate.valueAsString

    runBlocking {
      val validationResult =
        QuestionnaireResponseValidator.validateQuestionnaireResponse(
          questionnaire,
          getQuestionnaireResponse(),
          context,
        )

      assertThat(
          (validationResult["link-1"]?.first() as Invalid).getSingleStringValidationMessage(),
        )
        .isEqualTo("Maximum value allowed is:$maxDateAllowed")
    }
  }

  @Test
  fun datePicker_shouldNotSetDateInput_outsideMinRange() {
    val minDate = DateType(Date()).apply { add(Calendar.YEAR, 1) }
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            type = Questionnaire.QuestionnaireItemType.DATE
            linkId = "link-1"
            addExtension().apply {
              url = "http://hl7.org/fhir/StructureDefinition/minValue"
              setValue(minDate)
            }
            addExtension().apply {
              url = "http://hl7.org/fhir/StructureDefinition/maxValue"
              val maxDate = DateType(Date()).apply { add(Calendar.YEAR, 2) }
              setValue(maxDate)
            }
          },
        )
      }

    buildFragmentFromQuestionnaire(questionnaire)
    composeTestRule
      .onNodeWithContentDescription(context.getString(R.string.select_date))
      .performClick()
    composeTestRule
      .onNode(hasText("OK") and hasAnyAncestor(isDialog()))
      .assertIsDisplayed()
      .performClick()
    composeTestRule.waitForIdle() // Synchronize

    val minDateAllowed = minDate.valueAsString

    runBlocking {
      val validationResult =
        QuestionnaireResponseValidator.validateQuestionnaireResponse(
          questionnaire,
          getQuestionnaireResponse(),
          context,
        )

      assertThat(
          (validationResult["link-1"]?.first() as Invalid).getSingleStringValidationMessage(),
        )
        .isEqualTo("Minimum value allowed is:$minDateAllowed")
    }
  }

  @Test
  fun datePicker_shouldProhibitInputWithErrorMessage_whenMinValueRangeIsGreaterThanMaxValueRange() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            type = Questionnaire.QuestionnaireItemType.DATE
            linkId = "link-1"
            addExtension().apply {
              url = "http://hl7.org/fhir/StructureDefinition/minValue"
              val minDate = DateType(Date()).apply { add(Calendar.YEAR, 1) }

              setValue(minDate)
            }
            addExtension().apply {
              url = "http://hl7.org/fhir/StructureDefinition/maxValue"
              val maxDate = DateType(Date()).apply { add(Calendar.YEAR, -1) }
              setValue(maxDate)
            }
          },
        )
      }

    buildFragmentFromQuestionnaire(questionnaire)
    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .assert(
        SemanticsMatcher.expectValue(
          SemanticsProperties.Error,
          "minValue cannot be greater than maxValue",
        ),
      )
    composeTestRule.onNodeWithTag(DATE_TEXT_INPUT_FIELD).assertIsNotEnabled()
    composeTestRule
      .onNodeWithContentDescription(context.getString(R.string.select_date))
      .assertIsNotEnabled()
  }

  @Test
  fun displayItems_shouldGetEnabled_withAnswerChoice() {
    buildFragmentFromQuestionnaire("/questionnaire_with_enabled_display_items.json")

    // Synchronize
    composeTestRule.waitForIdle()
    onView(withId(R.id.hint)).check(doesNotExist())

    composeTestRule.onNodeWithTag(YES_CHOICE_RADIO_BUTTON_TAG).performClick()

    // Synchronize
    composeTestRule.waitForIdle()
    onView(withId(R.id.hint)).check { view, _ ->
      val hintVisibility = (view as TextView).visibility
      val hintText = view.text.toString()
      assertThat(hintVisibility).isEqualTo(View.VISIBLE)
      assertThat(hintText).isEqualTo("Text when yes is selected")
    }

    composeTestRule.onNodeWithTag(NO_CHOICE_RADIO_BUTTON_TAG).performClick()

    // Synchronize
    composeTestRule.waitForIdle()
    onView(withId(R.id.hint)).check { view, _ ->
      val hintVisibility = (view as TextView).visibility
      val hintText = view.text.toString()
      assertThat(hintVisibility).isEqualTo(View.VISIBLE)
      assertThat(hintText).isEqualTo("Text when no is selected")
    }

    composeTestRule.onNodeWithTag(NO_CHOICE_RADIO_BUTTON_TAG).performClick()

    // Synchronize
    composeTestRule.waitForIdle()
    onView(withId(R.id.hint)).check(doesNotExist())
  }

  @Test
  fun cqfExpression_shouldSetText_withEvaluatedAnswer() {
    buildFragmentFromQuestionnaire("/questionnaire_with_dynamic_question_text.json")

    onView(CoreMatchers.allOf(withText("Option Date"))).check { view, _ ->
      assertThat(view.id).isEqualTo(R.id.question)
    }

    onView(CoreMatchers.allOf(withText("Provide \"First Option\" Date"))).check { view, _ ->
      assertThat(view).isNull()
    }

    composeTestRule.onNodeWithText("First Option").performClick()

    onView(CoreMatchers.allOf(withText("Option Date"))).check { view, _ ->
      assertThat(view).isNull()
    }

    onView(CoreMatchers.allOf(withText("Provide \"First Option\" Date"))).check { view, _ ->
      assertThat(view.id).isEqualTo(R.id.question)
    }
  }

  @Test
  fun clearAllAnswers_shouldClearDraftAnswer() {
    val questionnaireFragment = buildFragmentFromQuestionnaire("/component_date_picker.json")

    runBlocking {
      // Add month and day. No need to add slashes as they are added automatically
      composeTestRule
        .onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
        .performTextInput("0105")
      composeTestRule
        .onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
        .assertTextEquals("01/05")
      delay(1.seconds) // Add delay to give time for new questionnaire state
      composeTestRule.awaitIdle()

      questionnaireFragment.clearAllAnswers()
      composeTestRule.awaitIdle()

      composeTestRule
        .onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
        .assertTextEquals("")
    }
  }

  @Test
  fun progressBar_shouldBeVisible_withSinglePageQuestionnaire() {
    buildFragmentFromQuestionnaire("/text_questionnaire_integer.json")

    onView(withId(R.id.questionnaire_progress_indicator)).check { view, _ ->
      val linearProgressIndicator = (view as LinearProgressIndicator)
      assertThat(linearProgressIndicator.visibility).isEqualTo(View.VISIBLE)
      assertThat(linearProgressIndicator.progress).isEqualTo(100)
    }
  }

  @Test
  fun progressBar_shouldBeVisible_withPaginatedQuestionnaire() {
    buildFragmentFromQuestionnaire("/layout_paginated.json")

    onView(withId(R.id.questionnaire_progress_indicator)).check { view, _ ->
      val linearProgressIndicator = (view as LinearProgressIndicator)
      assertThat(linearProgressIndicator.visibility).isEqualTo(View.VISIBLE)
      assertThat(linearProgressIndicator.progress).isEqualTo(50)
    }
  }

  @Test
  fun progressBar_shouldProgress_onPaginationNext() {
    buildFragmentFromQuestionnaire("/layout_paginated.json")

    onView(withId(R.id.pagination_next_button)).perform(ViewActions.click())

    onView(withId(R.id.questionnaire_progress_indicator)).check { view, _ ->
      val linearProgressIndicator = (view as LinearProgressIndicator)
      assertThat(linearProgressIndicator.progress).isEqualTo(100)
    }
  }

  @Test
  fun progressBar_shouldBeGone_whenNavigatedToReviewScreen() {
    buildFragmentFromQuestionnaire("/text_questionnaire_integer.json", isReviewMode = true)

    onView(withId(R.id.review_mode_button)).perform(ViewActions.click())

    onView(withId(R.id.questionnaire_progress_indicator)).check { view, _ ->
      val linearProgressIndicator = (view as LinearProgressIndicator)
      assertThat(linearProgressIndicator.visibility).isEqualTo(View.GONE)
    }
  }

  @Test
  fun progressBar_shouldBeVisible_whenNavigatedToEditScreenFromReview() {
    buildFragmentFromQuestionnaire("/text_questionnaire_integer.json", isReviewMode = true)

    onView(withId(R.id.review_mode_button)).perform(ViewActions.click())

    onView(withId(R.id.review_mode_edit_button)).perform(ViewActions.click())

    onView(withId(R.id.questionnaire_progress_indicator)).check { view, _ ->
      val linearProgressIndicator = (view as LinearProgressIndicator)
      assertThat(linearProgressIndicator.visibility).isEqualTo(View.VISIBLE)
    }
  }

  @Test
  fun test_add_item_button_does_not_exist_for_non_repeated_groups() {
    buildFragmentFromQuestionnaire("/component_non_repeated_group.json")
    onView(withId(R.id.add_item_to_repeated_group)).check(doesNotExist())
  }

  @Test
  fun test_repeated_group_is_added() {
    buildFragmentFromQuestionnaire("/component_repeated_group.json")
    onView(withId(R.id.add_item_to_repeated_group)).perform(ViewActions.click())

    composeTestRule
      .onNodeWithTag(QuestionnaireFragment.QUESTIONNAIRE_EDIT_LIST)
      .assertExists()
      .assertIsDisplayed()

    onView(withId(R.id.repeated_group_instance_header_title))
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    onView(withText(R.string.delete)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
  }

  @Test
  fun test_repeated_group_adds_multiple_items() {
    buildFragmentFromQuestionnaire("/component_multiple_repeated_group.json")
    onView(allOf(withText("Add Repeated Group"))).perform(ViewActions.click())

    onView(allOf(withText(R.string.delete)))
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    onView(
        allOf(
          withId(R.id.repeated_group_instance_header_title),
        ),
      )
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
  }

  @Test
  fun test_repeated_group_is_deleted() {
    buildFragmentFromQuestionnaire(
      "/component_repeated_group.json",
      responseFileName = "/repeated_group_response.json",
    )

    composeTestRule
      .onNodeWithTag(QuestionnaireFragment.QUESTIONNAIRE_EDIT_LIST)
      .assertExists()
      .assertIsDisplayed()

    onView(withId(R.id.repeated_group_instance_header_title))
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    onView(withText(R.string.delete)).perform(ViewActions.click())

    onView(withText(R.id.repeated_group_instance_header_title)).check(doesNotExist())
  }

  private fun buildFragmentFromQuestionnaire(
    fileName: String,
    isReviewMode: Boolean = false,
    responseFileName: String? = null,
  ): QuestionnaireFragment {
    val questionnaireJsonString = readFileFromAssets(fileName)
    val builder =
      QuestionnaireFragment.builder()
        .setQuestionnaire(questionnaireJsonString)
        .setShowCancelButton(true)
        .showReviewPageBeforeSubmit(isReviewMode)

    responseFileName?.let { builder.setQuestionnaireResponse(readFileFromAssets(it)) }

    return builder.build().also { fragment ->
      activityScenarioRule.scenario.onActivity { activity ->
        activity.supportFragmentManager.commitNow {
          setReorderingAllowed(true)
          add(com.google.android.fhir.datacapture.test.R.id.container_holder, fragment)
        }
      }
    }
  }

  private fun buildFragmentFromQuestionnaire(
    questionnaire: Questionnaire,
    isReviewMode: Boolean = false,
  ) {
    val questionnaireFragment =
      QuestionnaireFragment.builder()
        .setQuestionnaire(parser.encodeResourceToString(questionnaire))
        .showReviewPageBeforeSubmit(isReviewMode)
        .build()
    activityScenarioRule.scenario.onActivity { activity ->
      activity.supportFragmentManager.commitNow {
        setReorderingAllowed(true)
        add(com.google.android.fhir.datacapture.test.R.id.container_holder, questionnaireFragment)
      }
    }
  }

  private fun readFileFromAssets(filename: String) =
    javaClass.getResourceAsStream(filename)!!.bufferedReader().use { it.readText() }

  private suspend fun getQuestionnaireResponse(): QuestionnaireResponse {
    var testQuestionnaireFragment: QuestionnaireFragment? = null
    activityScenarioRule.scenario.onActivity { activity ->
      testQuestionnaireFragment =
        activity.supportFragmentManager.findFragmentById(
          com.google.android.fhir.datacapture.test.R.id.container_holder,
        ) as QuestionnaireFragment
    }
    return testQuestionnaireFragment!!.getQuestionnaireResponse()
  }
}
