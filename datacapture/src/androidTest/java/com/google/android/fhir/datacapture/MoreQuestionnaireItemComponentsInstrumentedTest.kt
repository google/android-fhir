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

package com.google.android.fhir.datacapture

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Attachment
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MoreQuestionnaireItemComponentsInstrumentedTest {

  @Test
  fun fetchBitmapFromUrl_returnsBitmap() {
    val attachment =
      Attachment().apply {
        contentType = "image/png"
        url = "https://some-image-server.com/images/f0006.png"
      }

    val expectedBitmap =
      BitmapFactory.decodeByteArray(IMAGE_BASE64_DECODED, 0, IMAGE_BASE64_DECODED.size)
    ApplicationProvider.getApplicationContext<DataCaptureTestApplication>()
      .getDataCaptureConfig()
      .urlResolver = TestUrlResolver(expectedBitmap)

    val bitmap: Bitmap?
    runBlocking {
      bitmap = attachment.fetchBitmapFromUrl(ApplicationProvider.getApplicationContext())
    }

    assertThat(bitmap).isNotNull()
    assertThat(bitmap).isEqualTo(expectedBitmap)
  }

  class TestUrlResolver(var testBitmap: Bitmap? = null) : UrlResolver {

    override suspend fun resolveBitmapUrl(url: String): Bitmap? {
      return if (url == "https://some-image-server.com/images/f0006.png") testBitmap else null
    }
  }

  companion object {
    private val IMAGE_BASE64_ENCODED =
      "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7".encodeToByteArray()
    private val IMAGE_BASE64_DECODED = Base64.decode(IMAGE_BASE64_ENCODED, Base64.DEFAULT)
  }
}
