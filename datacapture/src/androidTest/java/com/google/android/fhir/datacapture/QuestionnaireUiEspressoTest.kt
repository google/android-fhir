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

import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.add
import androidx.fragment.app.commitNow
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.fhir.datacapture.TestQuestionnaireFragment.Companion.QUESTIONNAIRE_FILE_PATH_KEY
import com.google.android.fhir.datacapture.test.R
import com.google.android.fhir.datacapture.utilities.clickIcon
import com.google.android.fhir.datacapture.utilities.clickOnText
import com.google.android.fhir.datacapture.views.localDate
import com.google.android.fhir.datacapture.views.localDateTime
import com.google.android.material.textfield.TextInputLayout
import com.google.common.truth.Truth.assertThat
import java.time.LocalDate
import java.time.LocalDateTime
import org.hl7.fhir.r4.model.QuestionnaireResponse
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

  @Before
  fun setup() {
    activityScenarioRule.scenario.onActivity { activity -> parent = FrameLayout(activity) }
  }

  @Test
  fun shouldDisplayReviewButtonWhenNoMorePagesToDisplay() {
    buildFragmentFromQuestionnaire("/paginated_questionnaire_with_dependent_answer.json")

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
  fun dateTimePicker_shouldShowErrorForWrongDate() {
    buildFragmentFromQuestionnaire("/component_date_time_picker.json")

    // Add month and day. No need to add slashes as they are added automatically
    onView(withId(R.id.date_input_edit_text))
      .perform(ViewActions.click())
      .perform(ViewActions.typeTextIntoFocusedView("0105"))

    onView(withId(R.id.date_input_layout)).check { view, _ ->
      val actualError = (view as TextInputLayout).error
      assertThat(actualError).isEqualTo("Date format needs to be MM/dd/yyyy (e.g. 01/31/2023)")
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
      assertThat(actualError).isEqualTo("Date format needs to be MM/dd/yyyy (e.g. 01/31/2023)")
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

  private fun buildFragmentFromQuestionnaire(fileName: String) {
    val bundle = bundleOf(QUESTIONNAIRE_FILE_PATH_KEY to fileName)
    activityScenarioRule.scenario.onActivity { activity ->
      activity.supportFragmentManager.commitNow {
        setReorderingAllowed(true)
        add<TestQuestionnaireFragment>(R.id.container_holder, args = bundle)
      }
    }
  }
  private fun getQuestionnaireResponse(): QuestionnaireResponse {
    var testQuestionnaireFragment: QuestionnaireFragment? = null
    activityScenarioRule.scenario.onActivity { activity ->
      testQuestionnaireFragment =
        activity.supportFragmentManager
          .findFragmentById(R.id.container_holder)
          ?.childFragmentManager?.findFragmentById(R.id.container) as QuestionnaireFragment
    }
    return testQuestionnaireFragment!!.getQuestionnaireResponse()
  }
}
