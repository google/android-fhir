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

package com.google.android.fhir.datacapture.views

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.TestActivity
import com.google.android.fhir.datacapture.utilities.clickIcon
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseItemValidator
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.material.textfield.TextInputLayout
import com.google.common.truth.Truth.assertThat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import org.hamcrest.CoreMatchers.allOf
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemDatePickerViewHolderFactoryEspressoTest {

  @Rule
  @JvmField
  var activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

  private lateinit var parent: FrameLayout
  private lateinit var viewHolder: QuestionnaireItemViewHolder
  @Before
  fun setup() {
    activityScenarioRule.getScenario().onActivity { activity -> parent = FrameLayout(activity) }
    viewHolder = QuestionnaireItemDatePickerViewHolderFactory.create(parent)
    setTestLayout(viewHolder.itemView)
  }

  @Test
  fun shouldSetDateInput() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val questionnaireItemView =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }

    onView(withId(R.id.text_input_layout)).perform(clickIcon(true))
    onView(allOf(withText("OK")))
      .inRoot(isDialog())
      .check(matches(isDisplayed()))
      .perform(ViewActions.click())

    val today = DateTimeType.today().valueAsString

    assertThat(answerHolder!!.single().valueDateType?.valueAsString).isEqualTo(today)
  }

  @Test
  fun shouldSetDateInput_withinRange() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val questionnaireItemView =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
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
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }

    onView(withId(R.id.text_input_layout)).perform(clickIcon(true))
    onView(allOf(withText("OK")))
      .inRoot(isDialog())
      .check(matches(isDisplayed()))
      .perform(ViewActions.click())

    val today = DateTimeType.today().valueAsString
    val validationResult =
      QuestionnaireResponseItemValidator.validate(
        questionnaireItemView.questionnaireItem,
        questionnaireItemView.answers,
        viewHolder.itemView.context
      )

    assertThat(answerHolder!!.single().valueDateType?.valueAsString).isEqualTo(today)
    assertThat(validationResult).isEqualTo(Valid)
  }

  @Test
  fun shouldSetDateInput_invalid_date_entry_invalid_month_day_year() {
    val questionnaireItemView =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }

    onView(withId(R.id.text_input_layout)).perform(ViewActions.click())
    onView(withId(R.id.text_input_edit_text)).perform(ViewActions.typeText("40/0/-9992"))

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.text_input_layout).error)
      .isEqualTo("Date format needs to be MM/dd/yyyy (e.g. 01/31/2023)")
  }

  @Test
  fun shouldSetDateInput_invalid_date_entry_invalid_day() {
    val questionnaireItemView =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }

    onView(withId(R.id.text_input_layout)).perform(ViewActions.click())
    onView(withId(R.id.text_input_edit_text)).perform(ViewActions.typeText("1/100/2"))

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.text_input_layout).error)
      .isEqualTo("Date format needs to be MM/dd/yyyy (e.g. 01/31/2023)")
  }

  @Test
  fun shouldSetDateInput_invalid_date_entry_invalid_month() {
    val questionnaireItemView =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }

    onView(withId(R.id.text_input_layout)).perform(ViewActions.click())
    onView(withId(R.id.text_input_edit_text)).perform(ViewActions.typeText("40/1/2"))

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.text_input_layout).error)
      .isEqualTo("Date format needs to be MM/dd/yyyy (e.g. 01/31/2023)")
  }

  @Test
  fun shouldSetDateInput_invalid_date_entry_invalid_year() {
    val questionnaireItemView =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }

    onView(withId(R.id.text_input_layout)).perform(ViewActions.click())
    onView(withId(R.id.text_input_edit_text)).perform(ViewActions.typeText("1/1/22222"))

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.text_input_layout).error)
      .isEqualTo("Date format needs to be MM/dd/yyyy (e.g. 01/31/2023)")
  }
  @Test
  fun shouldNotSetDateInput_outsideMaxRange() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val maxDate = DateType(Date()).apply { add(Calendar.YEAR, -2) }
    val questionnaireItemView =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
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
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }

    onView(withId(R.id.text_input_layout)).perform(clickIcon(true))
    onView(allOf(withText("OK")))
      .inRoot(isDialog())
      .check(matches(isDisplayed()))
      .perform(ViewActions.click())

    val maxDateAllowed = maxDate.valueAsString
    val validationResult =
      QuestionnaireResponseItemValidator.validate(
        questionnaireItemView.questionnaireItem,
        answerHolder!!,
        viewHolder.itemView.context
      )

    assertThat((validationResult as Invalid).getSingleStringValidationMessage())
      .isEqualTo("Maximum value allowed is:$maxDateAllowed")
  }

  @Test
  fun shouldNotSetDateInput_outsideMinRange() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val minDate = DateType(Date()).apply { add(Calendar.YEAR, 1) }
    val questionnaireItemView =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
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
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }

    onView(withId(R.id.text_input_layout)).perform(clickIcon(true))
    onView(allOf(withText("OK")))
      .inRoot(isDialog())
      .check(matches(isDisplayed()))
      .perform(ViewActions.click())

    val minDateAllowed = minDate.valueAsString
    val validationResult =
      QuestionnaireResponseItemValidator.validate(
        questionnaireItemView.questionnaireItem,
        answerHolder!!,
        viewHolder.itemView.context
      )

    assertThat((validationResult as Invalid).getSingleStringValidationMessage())
      .isEqualTo("Minimum value allowed is:$minDateAllowed")
  }

  @Test
  fun shouldThrowException_whenMinValueRangeIsGreaterThanMaxValueRange() {
    val questionnaireItemView =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
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
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }

    val exception =
      assertThrows(IllegalArgumentException::class.java) {
        onView(withId(R.id.text_input_layout)).perform(clickIcon(true))
        onView(allOf(withText("OK")))
          .inRoot(isDialog())
          .check(matches(isDisplayed()))
          .perform(ViewActions.click())
      }
    assertThat(exception.message).isEqualTo("minValue cannot be greater than maxValue")
  }

  /** Runs code snippet on UI/main thread */
  private fun runOnUI(action: () -> Unit) {
    activityScenarioRule.scenario.onActivity { activity -> action() }
  }

  /** Sets content view for test activity */
  private fun setTestLayout(view: View) {
    activityScenarioRule.scenario.onActivity { activity -> activity.setContentView(view) }
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
  }

  private fun setLocale(locale: Locale, context: Context) {
    Locale.setDefault(locale)
    context.resources.configuration.setLocale(locale)
  }
}
