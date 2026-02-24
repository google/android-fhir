/*
 * Copyright 2025-2026 Google LLC
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

package com.google.android.fhir.datacapture.test.views.compose

import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.fhir.datacapture.views.compose.MediaItem
import org.hl7.fhir.r4.model.Attachment
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MediaItemComposeTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun shouldShowImage_whenAttachmentIsSet_withImageContentType() {
    val attachment =
      Attachment().apply {
        contentType = "image/png"
        data = IMAGE_BASE64_DECODED
        size = IMAGE_BASE64_DECODED.size
      }

    composeTestRule.setContent { MediaItem(attachment = attachment) }

    composeTestRule.onNodeWithTag("media-image").assertIsDisplayed()
  }

  @Test
  fun shouldHideImage_whenAttachmentIsSet_withNonImageContentType() {
    val attachment =
      Attachment().apply {
        contentType = "application/pdf"
        data = DOCUMENT_BASE64_DECODED
        size = DOCUMENT_BASE64_DECODED.size
      }

    composeTestRule.setContent { MediaItem(attachment = attachment) }

    composeTestRule.onNodeWithTag("media-image").assertDoesNotExist()
  }

  @Test
  fun shouldHaveContentDescription_whenAttachmentTitleIsSet() {
    val attachment =
      Attachment().apply {
        title = "Image Attachment"
        contentType = "image/png"
        data = IMAGE_BASE64_DECODED
        size = IMAGE_BASE64_DECODED.size
      }

    composeTestRule.setContent { MediaItem(attachment = attachment) }

    composeTestRule.onNodeWithContentDescription(attachment.title).assertIsDisplayed()
  }

  @Test
  fun shouldHaveNullContentDescription_whenAttachmentTitleIsNotSet() {
    val attachment =
      Attachment().apply {
        contentType = "image/png"
        data = IMAGE_BASE64_DECODED
        size = IMAGE_BASE64_DECODED.size
      }

    composeTestRule.setContent { MediaItem(attachment = attachment) }

    // The node should still exist, but its content description should be null.
    // We can check this by asserting that a node with a non-null content description does not
    // exist.
    composeTestRule.onNodeWithContentDescription("", substring = true).assertDoesNotExist()
  }

  companion object {
    private val IMAGE_BASE64_ENCODED =
      "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7".encodeToByteArray()
    private val IMAGE_BASE64_DECODED = Base64.decode(IMAGE_BASE64_ENCODED, Base64.DEFAULT)
    private val DOCUMENT_BASE64_DECODED = "document".encodeToByteArray()
  }
}
