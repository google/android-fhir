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

import android.content.Intent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.TestActivity
import com.google.android.fhir.datacapture.utilities.showDropDown
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class QuestionnaireItemDropDownViewHolderFactoryEspressoTest {
  @Rule
  @JvmField
  var activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

  private lateinit var parent: FrameLayout
  private lateinit var viewHolder: QuestionnaireItemViewHolder

  @Before
  fun setup() {
    activityScenarioRule.scenario.onActivity { activity ->
      activity.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
      parent = FrameLayout(activity)
    }
    viewHolder = QuestionnaireItemDropDownViewHolderFactory.create(parent)
    setTestLayout(viewHolder.itemView)
  }

  @Test
  fun shouldClearAutoCompleteTextView() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        answerOptions("Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions(),
        validationResult = null,
        answersChangedCallback = { _, _, _ -> },
      )
    runOnUI { viewHolder.bind(questionnaireItemViewItem) }

    onView(withId(R.id.auto_complete)).perform(showDropDown())
    onView(withText("-")).inRoot(isPlatformPopup()).check(matches(isDisplayed())).perform(click())
    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.auto_complete).text.toString())
      .isEqualTo("-")
    assertThat(questionnaireItemViewItem.answers).isEmpty()
  }

  @Test
  fun shouldSetDropDownValueToAutoCompleteTextView() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        answerOptions("Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions(),
        validationResult = null,
        answersChangedCallback = { _, _, _ -> },
      )
    runOnUI { viewHolder.bind(questionnaireItemViewItem) }

    onView(withId(R.id.auto_complete)).perform(showDropDown())
    onView(withText("Coding 3"))
      .inRoot(isPlatformPopup())
      .check(matches(isDisplayed()))
      .perform(click())
    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.auto_complete).text.toString())
      .isEqualTo("Coding 3")
    assertThat((questionnaireItemViewItem.answers.single().value as Coding).display)
      .isEqualTo("Coding 3")
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

  private fun answerOptions(vararg options: String) =
    Questionnaire.QuestionnaireItemComponent().apply {
      options.forEach { option ->
        addAnswerOption(
          Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
            value =
              Coding().apply {
                code = option.replace(" ", "_")
                display = option
              }
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
