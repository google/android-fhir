/*
 * Copyright 2023 Google LLC
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
import com.google.android.fhir.datacapture.test.TestActivity
import com.google.android.fhir.datacapture.test.utilities.clickIcon
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.factories.DateTimePickerViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolder
import org.hamcrest.CoreMatchers.allOf
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DateTimePickerViewHolderFactoryEspressoTest {

  @Rule
  @JvmField
  var activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

  private lateinit var parent: FrameLayout
  private lateinit var viewHolder: QuestionnaireItemViewHolder
  @Before
  fun setup() {
    activityScenarioRule.scenario.onActivity { activity -> parent = FrameLayout(activity) }
    viewHolder = DateTimePickerViewHolderFactory.create(parent)
    setTestLayout(viewHolder.itemView)
  }

  @Test
  fun showsTimePickerInInputMode() {
    val questionnaireItemView =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }
    onView(withId(R.id.date_input_layout)).perform(clickIcon(true))
    onView(allOf(withText("OK")))
      .inRoot(isDialog())
      .check(matches(isDisplayed()))
      .perform(ViewActions.click())
    onView(withId(R.id.time_input_edit_text)).perform(ViewActions.click())
    // R.id.material_textinput_timepicker is the id for the text input in the time picker.
    onView(allOf(withId(com.google.android.material.R.id.material_textinput_timepicker)))
      .inRoot(isDialog())
      .check(matches(isDisplayed()))
  }

  @Test
  fun showsTimePickerInClockMode() {
    val questionnaireItemView =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemView) }
    onView(withId(R.id.date_input_layout)).perform(clickIcon(true))
    onView(allOf(withText("OK")))
      .inRoot(isDialog())
      .check(matches(isDisplayed()))
      .perform(ViewActions.click())
    onView(withId(R.id.time_input_layout)).perform(clickIcon(true))
    // R.id.material_clock_face is the id for the clock input in the time picker.
    onView(allOf(withId(com.google.android.material.R.id.material_clock_face)))
      .inRoot(isDialog())
      .check(matches(isDisplayed()))
  }

  /** Method to run code snippet on UI/main thread */
  private fun runOnUI(action: () -> Unit) {
    activityScenarioRule.scenario.onActivity { activity -> action() }
  }

  /** Method to set content view for test activity */
  private fun setTestLayout(view: View) {
    activityScenarioRule.scenario.onActivity { activity -> activity.setContentView(view) }
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
  }
}
