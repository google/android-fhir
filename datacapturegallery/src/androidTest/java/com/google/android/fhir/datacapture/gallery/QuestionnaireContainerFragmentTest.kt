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
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withResourceName
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireContainerFragmentTest {
  @Rule @JvmField var activityTestRule = ActivityTestRule(MainActivity::class.java)

  @Test
  fun assertOnQuestionnaireFragment() {
    onView(withText(R.string.app_name)).check(matches(isDisplayed()))
  }

  @Test
  fun assertRecyclerViewDisplayed() {
    onView(withId(R.id.recycler_view)).check(matches(isDisplayed()))
  }

  @Test
  fun testQuestionnaireListElement1() {
    clickRecyclerViewItems(R.string.questionnaire_list_entry_one_title)
    onView(withText(R.string.questionnaire_list_entry_one_title)).check(matches(isDisplayed()))
    onView(withText(R.string.submit_button_text)).check(matches(isDisplayed()))
  }

  @Test
  fun testQuestionnaireListElement2() {
    clickRecyclerViewItems(R.string.questionnaire_list_entry_two_title)
    onView(withText(R.string.questionnaire_list_entry_two_title)).check(matches(isDisplayed()))
    onView(withText(R.string.submit_button_text)).check(matches(isDisplayed()))
  }

  @Test
  fun testQuestionnaireListElement3() {
    clickRecyclerViewItems(R.string.questionnaire_list_entry_three_title)
    onView(withText(R.string.questionnaire_list_entry_three_title)).check(matches(isDisplayed()))
    onView(withText(R.string.submit_button_text)).check(matches(isDisplayed()))
  }

  @Test
  fun testQuestionnaireListElement4() {
    clickRecyclerViewItems(R.string.questionnaire_list_entry_four_title)
    onView(withText(R.string.submit_button_text)).check(matches(isDisplayed()))
    onView(withText(R.string.questionnaire_list_entry_four_text)).check(matches(isDisplayed()))
  }

  @Test
  fun testQuestionnaireListElement5() {
    clickRecyclerViewItems(R.string.questionnaire_list_entry_five_title)
    onView(withText(R.string.questionnaire_list_entry_five_title)).check(matches(isDisplayed()))
    onView(withText(R.string.submit_button_text)).check(matches(isDisplayed()))
  }

  @Test
  fun testQuestionnaireListElement6() {
    clickRecyclerViewItems(R.string.questionnaire_list_entry_six_title)
    onView(withText(R.string.questionnaire_list_entry_six_title)).check(matches(isDisplayed()))
    onView(withText(R.string.submit_button_text)).check(matches(isDisplayed()))
  }

  @Test
  fun testQuestionnaireListElement7() {
    clickRecyclerViewItems(R.string.questionnaire_list_entry_seven_title)
    onView(withText(R.string.questionnaire_list_entry_seven_title)).check(matches(isDisplayed()))
    onView(withText(R.string.submit_button_text)).check(matches(isDisplayed()))
  }

  companion object {
    private fun clickRecyclerViewItems(@StringRes vararg stringIds: Int) {
      onView(withResourceName("recycler_view"))
        .perform(
          actionOnItem<RecyclerView.ViewHolder>(
            hasDescendant(Matchers.anyOf(*stringIds.matchers())),
            ViewActions.click()
          )
        )
    }
    private fun IntArray.matchers() = map(::withText).toTypedArray()
  }
}
