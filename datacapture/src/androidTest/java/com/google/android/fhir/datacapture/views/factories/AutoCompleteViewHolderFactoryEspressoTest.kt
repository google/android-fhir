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

package com.google.android.fhir.datacapture.views.factories

import android.view.View
import android.widget.AutoCompleteTextView
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
import com.google.android.fhir.datacapture.test.TestActivity
import com.google.android.fhir.datacapture.test.utilities.delayMainThread
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AutoCompleteViewHolderFactoryEspressoTest {
  @Rule
  @JvmField
  var activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

  private lateinit var parent: FrameLayout
  private lateinit var viewHolder: QuestionnaireItemViewHolder

  @Before
  fun setup() {
    activityScenarioRule.scenario.onActivity { activity -> parent = FrameLayout(activity) }
    viewHolder = AutoCompleteViewHolderFactory.create(parent)
    setTestLayout(viewHolder.itemView)
  }

  @Test
  fun shouldReturnFilteredDropDownMenuItems() {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(false, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    runOnUI { viewHolder.bind(questionnaireViewItem) }

    onView(ViewMatchers.withId(R.id.autoCompleteTextView)).perform(ViewActions.typeText("Coding 1"))
    assertThat(
        viewHolder.itemView
          .findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteTextView)
          .adapter
          .count,
      )
      .isEqualTo(1)
  }

  @Test
  fun shouldAddDropDownValueSelectedForMultipleAnswersAutoCompleteTextView() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(true, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions("Coding 1", "Coding 5"),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )
    runOnUI { viewHolder.bind(questionnaireViewItem) }

    onView(ViewMatchers.withId(R.id.autoCompleteTextView)).perform(ViewActions.typeText("Coding 3"))
    runOnUI {
      viewHolder.itemView
        .findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        .showDropDown()
    }
    onView(ViewMatchers.withId(R.id.autoCompleteTextView)).perform(delayMainThread())
    onView(ViewMatchers.withText("Coding 3"))
      .inRoot(RootMatchers.isPlatformPopup())
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
      .perform(ViewActions.click())
    assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.autoCompleteTextView).text.toString(),
      )
      .isEmpty()
    assertThat(answerHolder!!.map { it.valueCoding.display })
      .containsExactly("Coding 1", "Coding 5", "Coding 3")
  }

  @Test
  fun shouldSetCorrectNumberOfChipsForSelectedAnswers() {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(true, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions("Coding 1", "Coding 5"),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    runOnUI { viewHolder.bind(questionnaireViewItem) }

    assertThat(viewHolder.itemView.findViewById<ChipGroup>(R.id.chipContainer).childCount)
      .isEqualTo(2)
  }

  /** Method to run code snippet on UI/main thread */
  private fun runOnUI(action: () -> Unit) {
    activityScenarioRule.scenario.onActivity { action() }
  }

  /** Method to set content view for test activity */
  private fun setTestLayout(view: View) {
    activityScenarioRule.scenario.onActivity { activity -> activity.setContentView(view) }
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
  }

  private fun answerOptions(repeats: Boolean, vararg options: String) =
    Questionnaire.QuestionnaireItemComponent().apply {
      this.repeats = repeats
      options.forEach { option ->
        addAnswerOption(
          Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
            value =
              Coding().apply {
                code = option.replace(" ", "_")
                display = option
              }
          },
        )
      }
    }

  private fun responseOptions(vararg options: String) =
    QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
      options.forEach { option ->
        addAnswer(
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
            value =
              Coding().apply {
                code = option.replace(" ", "_")
                display = option
              }
          },
        )
      }
    }
}
