/*
 * Copyright 2021 Google LLC
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

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.TestActivity
import com.google.android.fhir.datacapture.utilities.clickIcon
import com.google.common.truth.Truth
import org.hamcrest.CoreMatchers
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class QuestionnaireItemDialogMultiSelectViewHolderFactoryEspressoTest {
  @Rule
  @JvmField
  var activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule<TestActivity>(TestActivity::class.java)

  private lateinit var parent: FrameLayout
  private lateinit var viewHolder: QuestionnaireItemViewHolder

  @Before
  fun setup() {
    activityScenarioRule.getScenario().onActivity { activity -> parent = FrameLayout(activity) }
    viewHolder = QuestionnaireItemDialogSelectViewHolderFactory.create(parent)
    setTestLayout(viewHolder.itemView)
  }

  @Test
  fun shouldSelectMultipleOptionsFromDropDown() {

    val questionnaireItemView =
      QuestionnaireItemViewItem(
        answerOptions(true, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions()
      ) {}

    runOnUI { viewHolder.bind(questionnaireItemView) }

    onView(ViewMatchers.withId(R.id.multi_select_summary_holder)).perform(clickIcon(true))
    onView(ViewMatchers.withText("Coding 1"))
      .inRoot(RootMatchers.isDialog())
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
      .perform(ViewActions.click())

    onView(ViewMatchers.withText("Coding 3")).perform(ViewActions.click())
    onView(ViewMatchers.withText("Coding 5")).perform(ViewActions.click())

    onView(CoreMatchers.allOf(ViewMatchers.withText("OK")))
      .inRoot(RootMatchers.isDialog())
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
      .perform(ViewActions.click())

    Truth.assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.multi_select_summary).text.toString()
      )
      .isEqualTo("Coding 1, Coding 3, Coding 5")
  }

  @Test
  fun shouldSelectNothingFromMultipleChoiceFromDropDown() {

    val questionnaireItemView =
      QuestionnaireItemViewItem(
        answerOptions(true, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions()
      ) {}

    runOnUI { viewHolder.bind(questionnaireItemView) }

    onView(ViewMatchers.withId(R.id.multi_select_summary_holder)).perform(clickIcon(true))

    onView(CoreMatchers.allOf(ViewMatchers.withText("OK")))
      .inRoot(RootMatchers.isDialog())
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
      .perform(ViewActions.click())

    Truth.assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.multi_select_summary).text.toString()
      )
      .isEmpty()
  }

  @Test
  fun shouldCancelMultipleChoiceDropDown() {

    val questionnaireItemView =
      QuestionnaireItemViewItem(
        answerOptions(true, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions()
      ) {}

    runOnUI { viewHolder.bind(questionnaireItemView) }

    onView(ViewMatchers.withId(R.id.multi_select_summary_holder)).perform(clickIcon(true))

    onView(CoreMatchers.allOf(ViewMatchers.withText("Cancel")))
      .inRoot(RootMatchers.isDialog())
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
      .perform(ViewActions.click())

    Truth.assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.multi_select_summary).text.toString()
      )
      .isEmpty()
  }

  @Test
  fun shouldSelectSingleOptionOnChangeInOptionFromDropDown() {
    val questionnaireItemView =
      QuestionnaireItemViewItem(
        answerOptions(false, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions()
      ) {}

    runOnUI { viewHolder.bind(questionnaireItemView) }

    onView(ViewMatchers.withId(R.id.multi_select_summary_holder)).perform(clickIcon(true))
    onView(ViewMatchers.withText("Coding 1"))
      .inRoot(RootMatchers.isDialog())
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
      .perform(ViewActions.click())

    onView(ViewMatchers.withText("Coding 5")).perform(ViewActions.click())
    onView(ViewMatchers.withText("Coding 3")).perform(ViewActions.click())

    onView(CoreMatchers.allOf(ViewMatchers.withText("OK")))
      .inRoot(RootMatchers.isDialog())
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
      .perform(ViewActions.click())

    Truth.assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.multi_select_summary).text.toString()
      )
      .isEqualTo("Coding 3")
  }

  @Test
  fun shouldSelectSingleOptionFromDropDown() {
    val questionnaireItemView =
      QuestionnaireItemViewItem(
        answerOptions(false, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions()
      ) {}

    runOnUI { viewHolder.bind(questionnaireItemView) }

    onView(ViewMatchers.withId(R.id.multi_select_summary_holder)).perform(clickIcon(true))
    onView(ViewMatchers.withText("Coding 2"))
      .inRoot(RootMatchers.isDialog())
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
      .perform(ViewActions.click())

    onView(CoreMatchers.allOf(ViewMatchers.withText("OK")))
      .inRoot(RootMatchers.isDialog())
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
      .perform(ViewActions.click())

    Truth.assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.multi_select_summary).text.toString()
      )
      .isEqualTo("Coding 2")
  }

  @Test
  fun shouldSelectNothingFromSingleOptionFromDropDown() {
    val questionnaireItemView =
      QuestionnaireItemViewItem(
        answerOptions(false, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions()
      ) {}

    runOnUI { viewHolder.bind(questionnaireItemView) }

    onView(ViewMatchers.withId(R.id.multi_select_summary_holder)).perform(clickIcon(true))

    onView(CoreMatchers.allOf(ViewMatchers.withText("OK")))
      .inRoot(RootMatchers.isDialog())
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
      .perform(ViewActions.click())

    Truth.assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.multi_select_summary).text.toString()
      )
      .isEmpty()
  }

  @Test
  fun shouldCancelSingleOptionDropDown() {
    val questionnaireItemView =
      QuestionnaireItemViewItem(
        answerOptions(false, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions()
      ) {}

    runOnUI { viewHolder.bind(questionnaireItemView) }

    onView(ViewMatchers.withId(R.id.multi_select_summary_holder)).perform(clickIcon(true))

    onView(CoreMatchers.allOf(ViewMatchers.withText("Cancel")))
      .inRoot(RootMatchers.isDialog())
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
      .perform(ViewActions.click())

    Truth.assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.multi_select_summary).text.toString()
      )
      .isEmpty()
  }

  /** Method to run code snippet on UI/main thread */
  private fun runOnUI(action: () -> Unit) {
    activityScenarioRule.getScenario().onActivity { activity -> action() }
  }

  /** Method to set content view for test activity */
  private fun setTestLayout(view: View) {
    activityScenarioRule.getScenario().onActivity { activity -> activity.setContentView(view) }
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
  }

  private fun answerOptions(multiSelect: Boolean, vararg options: String) =
    Questionnaire.QuestionnaireItemComponent().apply {
      if (multiSelect) {
        this.repeats = true
      } else {
        this.repeats = false
      }
      linkId = "1"
      options.forEach { option ->
        addAnswerOption(
          Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
            value = Coding().apply { display = option }
          }
        )
      }
    }

  private fun responseOptions(vararg responses: String) =
    QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
      responses.forEach { response ->
        addAnswer(
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
            value = Coding().apply { display = response }
          }
        )
      }
    }
}
