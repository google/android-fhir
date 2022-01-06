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

package com.google.android.fhir.datacapture

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.fhir.datacapture.testing.DataCaptureTestApplication
import com.google.common.truth.Truth.assertThat
import java.nio.charset.Charset
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.Binary
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MoreQuestionnaireItemComponentsInstrumentedTest {

  @Test
  fun fetchBitmap_shouldReturnNull_whenAttachmentHasDataAndIncorrectContentType() {
    val attachment =
      Attachment().apply {
        data = "some-byte".toByteArray(Charset.forName("UTF-8"))
        contentType = "document/pdf"
      }
    val bitmap: Bitmap?
    runBlocking { bitmap = attachment.fetchBitmap() }
    assertThat(bitmap).isNull()
  }

  @Test
  fun fetchBitmap_shouldReturnBitmap_whenAttachmentHasDataAndCorrectContentType() {
    val attachment =
      Attachment().apply {
        data =
          Base64.decode("R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7", Base64.DEFAULT)
        contentType = "image/png"
      }
    val bitmap: Bitmap?
    runBlocking { bitmap = attachment.fetchBitmap() }
    assertThat(bitmap).isNotNull()
  }

  @Test
  fun isImage_shouldTrue_whenAttachmentContentTypeIsImage() {
    val attachment = Attachment().apply { contentType = "image/png" }
    assertThat(attachment.isImage).isTrue()
  }

  @Test
  fun isImage_shouldFalseWhenAttachmentContentTypeIsNotImage() {
    val attachment = Attachment().apply { contentType = "document/pdf" }
    assertThat(attachment.isImage).isFalse()
  }

  @Test
  fun fetchBitmap_shouldReturnBitmapAndCallAttachmentResolverResolveBinaryResource() {
    val attachment = Attachment().apply { url = "https://hapi.fhir.org/Binary/f006" }
    ApplicationProvider.getApplicationContext<DataCaptureTestApplication>()
      .getDataCaptureConfig()
      .attachmentResolver = TestAttachmentResolver()

    val bitmap: Bitmap?
    runBlocking { bitmap = attachment.fetchBitmap() }

    assertThat(bitmap).isNotNull()
  }

  @Test
  fun fetchBitmap_shouldReturnBitmapAndCallAttachmentResolverResolveImageUrl() {
    val attachment = Attachment().apply { url = "https://some-image-server.com/images/f0006.png" }

    val byteArray =
      Base64.decode("R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7", Base64.DEFAULT)
    val expectedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

    ApplicationProvider.getApplicationContext<DataCaptureTestApplication>()
      .getDataCaptureConfig()
      .attachmentResolver = TestAttachmentResolver(expectedBitmap)

    val resolvedBitmap: Bitmap?
    runBlocking { resolvedBitmap = attachment.fetchBitmap() }

    assertThat(resolvedBitmap).isEqualTo(expectedBitmap)
  }

  class TestAttachmentResolver(var testBitmap: Bitmap? = null) : AttachmentResolver {

    override suspend fun resolveBinaryResource(uri: String): Binary? {
      return if (uri == "https://hapi.fhir.org/Binary/f006") {
        Binary().apply {
          contentType = "image/png"
          data =
            Base64.decode(
              "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7",
              Base64.DEFAULT
            )
        }
      } else null
    }

    override suspend fun resolveImageUrl(uri: String): Bitmap? {
      return if (uri == "https://some-image-server.com/images/f0006.png") {
        testBitmap
      } else null
    }
  }
}
