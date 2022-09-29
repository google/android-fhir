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

package com.google.android.fhir.catalog

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewHolder
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LayoutsFragmentTest {

  @Rule
  @JvmField
  var activityScenarioRule: ActivityScenarioRule<MainActivity> =
    ActivityScenarioRule(MainActivity::class.java)

  @Test
  fun scroll_default_questionnaire_should_increase_progress() {
    clickOnId(R.id.layoutListFragment)
    clickOnText("Default")
    onView(withId(R.id.questionnaire_edit_recycler_view))
      .perform(RecyclerViewActions.scrollToPosition<QuestionnaireItemViewHolder>(15))

    var progress = 0
    activityScenarioRule.scenario.onActivity {
      val progressIndicator =
        it.findViewById<LinearProgressIndicator>(R.id.questionnaire_progress_indicator)
      progress = progressIndicator.progress
    }
    assertThat(progress).isEqualTo(16) // incremented by 1 as initial starts with 0
  }

  @Test
  fun navigate_paginated_questionnaire_should_increase_progress() {
    clickOnId(R.id.layoutListFragment)
    clickOnText("Paginated")
    clickOnText("Next")
    clickOnText("Next")
    clickOnText("Next")
    var progress = 0
    activityScenarioRule.scenario.onActivity {
      val progressIndicator =
        it.findViewById<LinearProgressIndicator>(R.id.questionnaire_progress_indicator)
      progress = progressIndicator.progress
    }
    assertThat(progress).isEqualTo(4)
  }
}
