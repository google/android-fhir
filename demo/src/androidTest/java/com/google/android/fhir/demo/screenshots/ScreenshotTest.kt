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

package com.google.android.fhir.demo.screenshots

import android.widget.TextView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.facebook.testing.screenshot.Screenshot.snap
import com.facebook.testing.screenshot.Screenshot.snapActivity
import com.google.android.fhir.demo.MainActivity
import com.google.android.fhir.demo.R
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScreenshotTest {

  @get:Rule var activityTestRule = ActivityTestRule(MainActivity::class.java, false, false)

  @Before
  fun before() {
    InstrumentationRegistry.getInstrumentation().uiAutomation
  }

  @Test
  fun testScreenshotEntireActivity() {
    val activity = activityTestRule.launchActivity(null)
    Thread.sleep(5000)
    Espresso.closeSoftKeyboard()
    val view = activityTestRule.activity.findViewById<TextView>(R.id.search_src_text)
    snapActivity(activity).setName("Registered Patient List").record()
    snap(view).setName("sample_view_test").record()
    Thread.sleep(5000)
  }

  @Test
  @Throws(Throwable::class)
  fun mainActivityTestAddPatientScreenOpen() {
    val activity = activityTestRule.launchActivity(null)
    Thread.sleep(5000)
    Espresso.closeSoftKeyboard()
    val floatingActionButton = onView(allOf(withId(R.id.add_patient), isDisplayed()))
    Thread.sleep(3000)
    floatingActionButton.perform(click())
    snapActivity(activity).record()
  }
}
