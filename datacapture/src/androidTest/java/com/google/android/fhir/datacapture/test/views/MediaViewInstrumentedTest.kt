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

import android.util.Base64
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.EXTENSION_ITEM_MEDIA
import com.google.android.fhir.datacapture.test.TestActivity
import com.google.android.fhir.datacapture.views.MediaView
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.Questionnaire
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MediaViewInstrumentedTest {

  @Rule
  @JvmField
  var activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

  private lateinit var parent: FrameLayout
  private lateinit var view: MediaView

  @Before
  fun setUp() {
    activityScenarioRule.scenario.onActivity { activity -> parent = FrameLayout(activity) }
    view = MediaView(parent.context, null)
    setTestLayout(view)
  }

  @Test
  fun shouldShowImage_whenItemMediaExtensionIsSet_withImageContentType() = runBlocking {
    val attachment =
      Attachment().apply {
        contentType = "image/png"
        data = IMAGE_BASE64_DECODED
        size = IMAGE_BASE64_DECODED.size
      }

    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        text = "Kindly collect the reading as shown below in the figure"
        extension = listOf(Extension(EXTENSION_ITEM_MEDIA, attachment))
      }

    runOnUI { view.bind(questionnaireItem) }

    assertThat(view.findViewById<ImageView>(R.id.image_attachment).visibility)
      .isEqualTo(View.VISIBLE)
  }

  @Test
  fun shouldHideImageView_whenItemMediaExtensionIsSet_withNonImageContentType() = runBlocking {
    val attachment =
      Attachment().apply {
        contentType = "document/pdf"
        data = DOCUMENT_BASE64_DECODED
        size = DOCUMENT_BASE64_DECODED.size
      }

    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        text = "Kindly collect the reading as shown below in the figure"
        extension = listOf(Extension(EXTENSION_ITEM_MEDIA, attachment))
      }

    runOnUI { view.bind(questionnaireItem) }

    assertThat(view.findViewById<ImageView>(R.id.image_attachment).visibility).isEqualTo(View.GONE)
  }

  @Test
  fun shouldHideImageView_whenItemMediaExtensionIsNotSet() = runBlocking {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        text = "Kindly collect the reading as shown below in the figure"
      }

    runOnUI { view.bind(questionnaireItem) }

    assertThat(view.findViewById<ImageView>(R.id.image_attachment).visibility).isEqualTo(View.GONE)
  }

  @Test
  fun shouldHaveContentDescription_whenAttachmentTitleIsSet() = runBlocking {
    val attachment =
      Attachment().apply {
        title = "Image Attachment"
        contentType = "image/png"
        data = IMAGE_BASE64_DECODED
        size = IMAGE_BASE64_DECODED.size
      }

    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        text = "Kindly collect the reading as shown below in the figure"
        extension = listOf(Extension(EXTENSION_ITEM_MEDIA, attachment))
      }

    runOnUI { view.bind(questionnaireItem) }

    assertThat(view.findViewById<ImageView>(R.id.image_attachment).contentDescription)
      .isEqualTo(attachment.title)
  }

  @Test
  fun shouldHaveNullContentDescription_whenAttachmentTitleIsNotSet() = runBlocking {
    val attachment =
      Attachment().apply {
        contentType = "image/png"
        data = IMAGE_BASE64_DECODED
        size = IMAGE_BASE64_DECODED.size
      }

    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        text = "Kindly collect the reading as shown below in the figure"
        extension = listOf(Extension(EXTENSION_ITEM_MEDIA, attachment))
      }

    runOnUI { view.bind(questionnaireItem) }

    assertThat(view.findViewById<ImageView>(R.id.image_attachment).contentDescription).isNull()
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

  companion object {
    private val IMAGE_BASE64_ENCODED =
      "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7".encodeToByteArray()
    private val IMAGE_BASE64_DECODED = Base64.decode(IMAGE_BASE64_ENCODED, Base64.DEFAULT)
    private val DOCUMENT_BASE64_DECODED = "document".encodeToByteArray()
  }
}
