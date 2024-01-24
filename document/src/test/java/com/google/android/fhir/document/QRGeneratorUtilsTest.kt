/*
 * Copyright 2024 Google LLC
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

package com.google.android.fhir.document

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.google.android.fhir.document.generate.QRGeneratorUtils
import kotlin.math.floor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class QRGeneratorUtilsTest {
  private lateinit var context: Context
  private lateinit var qrGeneratorUtils: QRGeneratorUtils

  @Before
  fun setUp() {
    context = mock(Context::class.java)
    val resources = mock(Resources::class.java)
    `when`(context.resources).thenReturn(resources)
    qrGeneratorUtils = QRGeneratorUtils(context)
  }

  @Test
  fun testCreateQRCodeBitmap() {
    val content = "Test"
    val qrCodeBitmap: Bitmap = qrGeneratorUtils.createQRCodeBitmap(content)
    assertNotNull(qrCodeBitmap)
    assertEquals(512, qrCodeBitmap.width)
    assertEquals(512, qrCodeBitmap.height)
  }

  @Test
  fun testCreateQRCodeBitmapWithSpecialCharacters() {
    val content = "!@#$%^&*()_+"
    val qrCodeBitmap: Bitmap = qrGeneratorUtils.createQRCodeBitmap(content)
    assertEquals(512, qrCodeBitmap.width)
    assertEquals(512, qrCodeBitmap.height)
  }

  @Test
  fun testCreateQRCodeBitmapWithLongContent() {
    val longContent = "A".repeat(1000)
    val qrCodeBitmap: Bitmap = qrGeneratorUtils.createQRCodeBitmap(longContent)
    assertEquals(512, qrCodeBitmap.width)
    assertEquals(512, qrCodeBitmap.height)
  }

  @Test
  fun testCreateLogoBitmap() {
    val drawable = mock(Drawable::class.java)
    val resourceId = R.drawable.smart_logo
    `when`(ContextCompat.getDrawable(context, resourceId)).thenReturn(drawable)

    val qrCodeBitmap = qrGeneratorUtils.createQRCodeBitmap("Test")
    val logoBitmap: Bitmap = qrGeneratorUtils.createLogoBitmap(qrCodeBitmap)
    assertEquals((qrCodeBitmap.width * 0.4).toInt(), logoBitmap.width)
  }

  @Test
  fun testOverlayLogoOnQRCode() {
    val drawable = mock(Drawable::class.java)
    val resourceId = R.drawable.smart_logo
    `when`(ContextCompat.getDrawable(context, resourceId)).thenReturn(drawable)

    val qrCodeBitmap = qrGeneratorUtils.createQRCodeBitmap("TestContent")
    val logoBitmap: Bitmap = qrGeneratorUtils.createLogoBitmap(qrCodeBitmap)
    val finalBitmap = qrGeneratorUtils.overlayLogoOnQRCode(qrCodeBitmap, logoBitmap)

    assertEquals(logoBitmap.width, floor(finalBitmap.width * 0.4).toInt())
    assertEquals(logoBitmap.height, 1)
  }

  // Checks an error is thrown if the content is empty
  @Test(expected = IllegalArgumentException::class)
  fun testCreateQRCodeBitmapWithEmptyContent() {
    val emptyContent = ""
    qrGeneratorUtils.createQRCodeBitmap(emptyContent)
  }
}
