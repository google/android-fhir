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

import android.util.Base64
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.EXTENSION_ITEM_MEDIA
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.TestActivity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.Questionnaire
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemMediaViewInstrumentedTest {

  @Rule
  @JvmField
  var activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

  private lateinit var parent: FrameLayout
  private lateinit var view: QuestionnaireItemMediaView

  @Before
  fun setUp() {
    activityScenarioRule.scenario.onActivity { activity -> parent = FrameLayout(activity) }
    view = QuestionnaireItemMediaView(parent.context, null)
    setTestLayout(view)
  }

  @Test
  fun shouldShowImage_whenItemMediaExtensionIsSet_withImageContentType() = runBlocking {
    val attachment =
      Attachment().apply {
        data =
          Base64.decode("R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7", Base64.DEFAULT)
        contentType = "image/png"
      }
    val questionnaireItemComponent =
      Questionnaire.QuestionnaireItemComponent().apply {
        text = "Kindly collect the reading as shown below in the figure"
        extension = listOf(Extension(EXTENSION_ITEM_MEDIA, attachment))
      }

    runOnUI { view.bind(questionnaireItemComponent) }

    delay(1000)

    assertThat(view.findViewById<ImageView>(R.id.item_image).visibility).isEqualTo(View.VISIBLE)
  }

  @Test
  fun shouldHideImageView_whenItemMediaExtensionIsSet_withNonImageContentType() = runBlocking {
    val attachment =
      Attachment().apply {
        data =
          Base64.decode("R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7", Base64.DEFAULT)
        contentType = "document/pdf"
      }
    val questionnaireItemComponent =
      Questionnaire.QuestionnaireItemComponent().apply {
        text = "Kindly collect the reading as shown below in the figure"
        extension = listOf(Extension(EXTENSION_ITEM_MEDIA, attachment))
      }

    runOnUI { view.bind(questionnaireItemComponent) }

    delay(1000)

    assertThat(view.findViewById<ImageView>(R.id.item_image).visibility).isEqualTo(View.GONE)
  }

  @Test
  fun shouldHideImageView_whenItemMediaExtensionIsNotSet() = runBlocking {
    val questionnaireItemComponent =
      Questionnaire.QuestionnaireItemComponent().apply {
        text = "Kindly collect the reading as shown below in the figure"
      }

    runOnUI { view.bind(questionnaireItemComponent) }

    delay(1000)

    assertThat(view.findViewById<ImageView>(R.id.item_image).visibility).isEqualTo(View.GONE)
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
}
