/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir.datacapture

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.commitNow
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.datacapture.test.R
import com.google.android.fhir.datacapture.utilities.clickIcon
import com.google.android.fhir.datacapture.utilities.clickOnText
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseValidator
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.views.factories.localDate
import com.google.android.fhir.datacapture.views.factories.localDateTime
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date
import org.hamcrest.CoreMatchers
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireUiEspressoTest {

  @Rule
  @JvmField
  var activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

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

    onView(withId(R.id.review_mode_button))
      .check(
        ViewAssertions.matches(
          ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
        )
      )

    clickOnText("Yes")
    onView(withId(R.id.review_mode_button))
      .check(
        ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE))
      )

    clickOnText("No")
    onView(withId(R.id.review_mode_button))
      .check(
        ViewAssertions.matches(
          ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
        )
      )
  }

  @Test
  fun integerTextEdit_inputOutOfRange_shouldShowError() {
    buildFragmentFromQuestionnaire("/text_questionnaire_integer.json")

    onView(withId(R.id.text_input_edit_text)).perform(typeText("12345678901"))
    onView(withId(R.id.text_input_layout)).check { view, _ ->
      val actualError = (view as TextInputLayout).error
      assertThat(actualError).isEqualTo("Number must be between -2,147,483,648 and 2,147,483,647")
    }
  }

  @Test
  fun integerTextEdit_typingZeroBeforeAnyIntegerShouldKeepZeroDisplayed() {
    // Do not skip cursor when typing on the numeric field if the initial value is set to 0
    // as from an integer comparison, leading zeros do not change how the answer is saved.
    // e.g whether 000001 or 1 is input, the answer saved will be 1.
    buildFragmentFromQuestionnaire("/text_questionnaire_integer.json")

    onView(withId(R.id.text_input_edit_text)).perform(typeText("0"))
    assertThat(getQuestionnaireResponse().item.first().answer.first().valueIntegerType.value)
      .isEqualTo(0)

    onView(withId(R.id.text_input_edit_text)).perform(typeText("01"))
    assertThat(getQuestionnaireResponse().item.first().answer.first().valueIntegerType.value)
      .isEqualTo(1)

    onView(withId(R.id.text_input_edit_text)).check { view, _ ->
      assertThat((view as TextInputEditText).text.toString()).isEqualTo("001")
    }

    assertThat(getQuestionnaireResponse().item.first().answer.first().valueIntegerType.value)
      .isEqualTo(1)
  }

  @Test
  fun decimalTextEdit_typingZeroBeforeAnyIntegerShouldKeepZeroDisplayed() {
    buildFragmentFromQuestionnaire("/text_questionnaire_decimal.json")

    onView(withId(R.id.text_input_edit_text)).perform(typeText("0."))
    assertThat(getQuestionnaireResponse().item.first().answer.first().valueDecimalType.value)
      .isEqualTo(BigDecimal.valueOf(0.0))

    onView(withId(R.id.text_input_edit_text)).perform(typeText("01"))
    assertThat(getQuestionnaireResponse().item.first().answer.first().valueDecimalType.value)
      .isEqualTo(BigDecimal.valueOf(0.01))

    onView(withId(R.id.text_input_edit_text)).check { view, _ ->
      assertThat((view as TextInputEditText).text.toString()).isEqualTo("0.01")
    }

    assertThat(getQuestionnaireResponse().item.first().answer.first().valueDecimalType.value)
      .isEqualTo(BigDecimal.valueOf(0.01))
  }

  @Test
  fun decimalTextEdit_typingInvalidTextShouldShowError() {
    buildFragmentFromQuestionnaire("/text_questionnaire_decimal.json")

    onView(withId(R.id.text_input_edit_text)).perform(typeText("1.1.1.1"))

    onView(withId(R.id.text_input_layout)).check { view, _ ->
      assertThat((view as TextInputLayout).error).isEqualTo("Invalid number")
    }
  }

  @Test
  fun dateTimePicker_shouldShowErrorForWrongDate() {
    buildFragmentFromQuestionnaire("/component_date_time_picker.json")

    // Add month and day. No need to add slashes as they are added automatically
    onView(withId(R.id.date_input_edit_text))
      .perform(ViewActions.click())
      .perform(ViewActions.typeTextIntoFocusedView("0105"))

    onView(withId(R.id.date_input_layout)).check { view, _ ->
      val actualError = (view as TextInputLayout).error
      assertThat(actualError).isEqualTo("Date format needs to be mm/dd/yyyy (e.g. 01/31/2023)")
    }
    onView(withId(R.id.time_input_layout)).check { view, _ -> assertThat(view.isEnabled).isFalse() }
  }

  @Test
  fun dateTimePicker_shouldEnableTimePickerWithCorrectDate_butNotSaveInQuestionnaireResponse() {
    buildFragmentFromQuestionnaire("/component_date_time_picker.json")

    onView(withId(R.id.date_input_edit_text))
      .perform(ViewActions.click())
      .perform(ViewActions.typeTextIntoFocusedView("01052005"))

    onView(withId(R.id.date_input_layout)).check { view, _ ->
      val actualError = (view as TextInputLayout).error
      assertThat(actualError).isEqualTo(null)
    }

    onView(withId(R.id.time_input_layout)).check { view, _ -> assertThat(view.isEnabled).isTrue() }

    assertThat(getQuestionnaireResponse().item.size).isEqualTo(1)
    assertThat(getQuestionnaireResponse().item.first().answer.size).isEqualTo(0)
  }

  @Test
  fun dateTimePicker_shouldSetAnswerWhenDateAndTimeAreFilled() {
    buildFragmentFromQuestionnaire("/component_date_time_picker.json")

    onView(withId(R.id.date_input_edit_text))
      .perform(ViewActions.click())
      .perform(ViewActions.typeTextIntoFocusedView("01052005"))

    onView(withId(R.id.time_input_layout)).perform(clickIcon(true))
    clickOnText("AM")
    clickOnText("6")
    clickOnText("10")
    clickOnText("OK")

    val answer = getQuestionnaireResponse().item.first().answer.first().valueDateTimeType

    assertThat(answer.localDateTime).isEqualTo(LocalDateTime.of(2005, 1, 5, 6, 10))
  }

  @Test
  fun datePicker_shouldShowErrorForWrongDate() {
    buildFragmentFromQuestionnaire("/component_date_picker.json")

    // Add month and day. No need to add slashes as they are added automatically
    onView(withId(R.id.text_input_edit_text))
      .perform(ViewActions.click())
      .perform(ViewActions.typeTextIntoFocusedView("0105"))

    onView(withId(R.id.text_input_layout)).check { view, _ ->
      val actualError = (view as TextInputLayout).error
      assertThat(actualError).isEqualTo("Date format needs to be mm/dd/yyyy (e.g. 01/31/2023)")
    }
  }

  @Test
  fun datePicker_shouldSaveInQuestionnaireResponseWhenCorrectDateEntered() {
    buildFragmentFromQuestionnaire("/component_date_picker.json")

    onView(withId(R.id.text_input_edit_text))
      .perform(ViewActions.click())
      .perform(ViewActions.typeTextIntoFocusedView("01052005"))

    onView(withId(R.id.text_input_layout)).check { view, _ ->
      val actualError = (view as TextInputLayout).error
      assertThat(actualError).isEqualTo(null)
    }

    val answer = getQuestionnaireResponse().item.first().answer.first().valueDateType
    assertThat(answer.localDate).isEqualTo(LocalDate.of(2005, 1, 5))
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
          }
        )
      }

    buildFragmentFromQuestionnaire(questionnaire)
    onView(withId(R.id.text_input_layout)).perform(clickIcon(true))
    onView(CoreMatchers.allOf(ViewMatchers.withText("OK")))
      .inRoot(RootMatchers.isDialog())
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
      .perform(ViewActions.click())

    val today = DateTimeType.today().valueAsString
    val answer = getQuestionnaireResponse().item.first().answer.first().valueDateType.valueAsString

    assertThat(answer).isEqualTo(today)
    val validationResult =
      QuestionnaireResponseValidator.validateQuestionnaireResponse(
        questionnaire,
        getQuestionnaireResponse(),
        context
      )

    assertThat(validationResult["link-1"]?.first()).isEqualTo(Valid)
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
          }
        )
      }

    buildFragmentFromQuestionnaire(questionnaire)
    onView(withId(R.id.text_input_layout)).perform(clickIcon(true))
    onView(CoreMatchers.allOf(ViewMatchers.withText("OK")))
      .inRoot(RootMatchers.isDialog())
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
      .perform(ViewActions.click())

    val maxDateAllowed = maxDate.valueAsString
    val validationResult =
      QuestionnaireResponseValidator.validateQuestionnaireResponse(
        questionnaire,
        getQuestionnaireResponse(),
        context
      )

    assertThat((validationResult["link-1"]?.first() as Invalid).getSingleStringValidationMessage())
      .isEqualTo("Maximum value allowed is:$maxDateAllowed")
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
          }
        )
      }

    buildFragmentFromQuestionnaire(questionnaire)
    onView(withId(R.id.text_input_layout)).perform(clickIcon(true))
    onView(CoreMatchers.allOf(ViewMatchers.withText("OK")))
      .inRoot(RootMatchers.isDialog())
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
      .perform(ViewActions.click())

    val minDateAllowed = minDate.valueAsString
    val validationResult =
      QuestionnaireResponseValidator.validateQuestionnaireResponse(
        questionnaire,
        getQuestionnaireResponse(),
        context
      )

    assertThat((validationResult["link-1"]?.first() as Invalid).getSingleStringValidationMessage())
      .isEqualTo("Minimum value allowed is:$minDateAllowed")
  }

  @Test
  fun datePicker_shouldThrowException_whenMinValueRangeIsGreaterThanMaxValueRange() {
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
          }
        )
      }

    buildFragmentFromQuestionnaire(questionnaire)
    val exception =
      Assert.assertThrows(IllegalArgumentException::class.java) {
        onView(withId(com.google.android.fhir.datacapture.R.id.text_input_layout))
          .perform(clickIcon(true))
        onView(CoreMatchers.allOf(ViewMatchers.withText("OK")))
          .inRoot(RootMatchers.isDialog())
          .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
          .perform(ViewActions.click())
      }
    assertThat(exception.message).isEqualTo("minValue cannot be greater than maxValue")
  }

  @Test
  fun displayItems_shouldGetEnabled_withAnswerChoice() {
    buildFragmentFromQuestionnaire("/questionnaire_with_enabled_display_items.json")

    onView(withId(R.id.hint)).check { view, _ ->
      val hintVisibility = (view as TextView).visibility
      assertThat(hintVisibility).isEqualTo(View.GONE)
    }

    onView(withId(R.id.yes_radio_button)).perform(ViewActions.click())

    onView(withId(R.id.hint)).check { view, _ ->
      val hintVisibility = (view as TextView).visibility
      val hintText = view.text.toString()
      assertThat(hintVisibility).isEqualTo(View.VISIBLE)
      assertThat(hintText).isEqualTo("Text when yes is selected")
    }

    onView(withId(R.id.no_radio_button)).perform(ViewActions.click())

    onView(withId(R.id.hint)).check { view, _ ->
      val hintVisibility = (view as TextView).visibility
      val hintText = view.text.toString()
      assertThat(hintVisibility).isEqualTo(View.VISIBLE)
      assertThat(hintText).isEqualTo("Text when no is selected")
    }

    onView(withId(R.id.no_radio_button)).perform(ViewActions.click())

    onView(withId(R.id.hint)).check { view, _ ->
      val hintVisibility = (view as TextView).visibility
      assertThat(hintVisibility).isEqualTo(View.GONE)
    }
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

    onView(CoreMatchers.allOf(withText("First Option"))).perform(ViewActions.click())

    onView(CoreMatchers.allOf(withText("Option Date"))).check { view, _ ->
      assertThat(view).isNull()
    }

    onView(CoreMatchers.allOf(withText("Provide \"First Option\" Date"))).check { view, _ ->
      assertThat(view.id).isEqualTo(R.id.question)
    }
  }

  private fun buildFragmentFromQuestionnaire(fileName: String, isReviewMode: Boolean = false) {
    val questionnaireJsonString = readFileFromAssets(fileName)
    val questionnaireFragment =
      QuestionnaireFragment.builder()
        .setQuestionnaire(questionnaireJsonString)
        .showReviewPageBeforeSubmit(isReviewMode)
        .build()
    activityScenarioRule.scenario.onActivity { activity ->
      activity.supportFragmentManager.commitNow {
        setReorderingAllowed(true)
        add(R.id.container_holder, questionnaireFragment)
      }
    }
  }

  private fun buildFragmentFromQuestionnaire(
    questionnaire: Questionnaire,
    isReviewMode: Boolean = false
  ) {
    val questionnaireFragment =
      QuestionnaireFragment.builder()
        .setQuestionnaire(parser.encodeResourceToString(questionnaire))
        .showReviewPageBeforeSubmit(isReviewMode)
        .build()
    activityScenarioRule.scenario.onActivity { activity ->
      activity.supportFragmentManager.commitNow {
        setReorderingAllowed(true)
        add(R.id.container_holder, questionnaireFragment)
      }
    }
  }

  private fun readFileFromAssets(filename: String) =
    javaClass.getResourceAsStream(filename)!!.bufferedReader().use { it.readText() }
  private fun getQuestionnaireResponse(): QuestionnaireResponse {
    var testQuestionnaireFragment: QuestionnaireFragment? = null
    activityScenarioRule.scenario.onActivity { activity ->
      testQuestionnaireFragment =
        activity.supportFragmentManager.findFragmentById(R.id.container_holder)
          as QuestionnaireFragment
    }
    return testQuestionnaireFragment!!.getQuestionnaireResponse()
  }
}
