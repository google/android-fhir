/*
 * Copyright 2020 Google LLC
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

package com.google.android.fhir.datacapture.gallery

import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matchers

fun questionnaireContainerRobot(func: QuestionnaireContainerFragmentRobot.() -> Unit) =
  QuestionnaireContainerFragmentRobot().apply { func() }

class QuestionnaireContainerFragmentRobot {
  init {
    onView(withText(R.string.app_name)).check(matches(isDisplayed()))
  }

  fun assert_questionnaire_recycler_view_displayed() {
    onView(ViewMatchers.withId(R.id.recycler_view)).check(matches(isDisplayed()))
  }

  fun click_Real_world_lifelines_questionnaire() {
    clickRecyclerViewItems(R.string.questionnaire_list_entry_one_title)
  }

  fun assert_displayed_Real_world_lifelines_questionnaire() {
    onView(withText(R.string.questionnaire_list_entry_one_title)).check(matches(isDisplayed()))
  }

  fun assert_submit_button_displayed() {
    onView(withText(R.string.submit_button_text)).check(matches(isDisplayed()))
  }

  fun click_Neonate_record_from_New_South_Wales_Australia() {
    clickRecyclerViewItems(R.string.questionnaire_list_entry_two_title)
  }

  fun assert_displayed_Neonate_record_from_New_SouthWales_Australia_questionnaire() {
    onView(withText(R.string.questionnaire_list_entry_two_title)).check(matches(isDisplayed()))
  }

  fun click_Patient_registration() {
    clickRecyclerViewItems(R.string.questionnaire_list_entry_three_title)
  }

  fun assert_displayed_patient_registration_questionnaire() {
    onView(withText(R.string.questionnaire_list_entry_three_title)).check(matches(isDisplayed()))
  }

  fun click_HIV_Case_Report() {
    clickRecyclerViewItems(R.string.questionnaire_list_entry_four_title)
  }

  fun assert_displayed_HIV_Case_Report_questionnaire() {
    onView(withText(R.string.questionnaire_list_entry_four_text)).check(matches(isDisplayed()))
  }

  fun click_COVID19_Case_Report() {
    clickRecyclerViewItems(R.string.questionnaire_list_entry_five_title)
  }

  fun assert_displayed_COVID19_Case_Report_questionnaire() {
    onView(withText(R.string.questionnaire_list_entry_five_title)).check(matches(isDisplayed()))
  }

  fun click_MyPAIN() {
    clickRecyclerViewItems(R.string.questionnaire_list_entry_six_title)
  }

  fun assert_displayed_MyPAIN_questionnaire() {
    onView(withText(R.string.questionnaire_list_entry_six_title)).check(matches(isDisplayed()))
  }

  fun click_HIV_Risk_Assessment() {
    clickRecyclerViewItems(R.string.questionnaire_list_entry_seven_title)
  }
  fun assert_displayed_HIV_Risk_Assessment_questionnaire() {
    onView(withText(R.string.questionnaire_list_entry_seven_title)).check(matches(isDisplayed()))
  }

  private fun clickRecyclerViewItems(@StringRes vararg stringIds: Int) {
    onView(ViewMatchers.withResourceName("recycler_view"))
      .perform(
        RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
          hasDescendant(Matchers.anyOf(*stringIds.matchers())),
          click()
        )
      )
  }
  private fun IntArray.matchers() = map(ViewMatchers::withText).toTypedArray()

  private val backIcon =
    onView(Matchers.allOf(ViewMatchers.withContentDescription("Navigate up"), isDisplayed()))

  fun pressBack(): ViewInteraction = backIcon.perform(click())
}
