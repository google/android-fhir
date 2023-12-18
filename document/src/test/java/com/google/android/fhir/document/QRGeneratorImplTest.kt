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

package com.google.android.fhir.document

import android.graphics.Bitmap
import android.widget.ImageView
import com.google.android.fhir.document.generate.QRGeneratorImpl
import com.google.android.fhir.document.generate.QRGeneratorUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class QRGeneratorImplTest {

  @Mock private lateinit var qrGeneratorUtils: QRGeneratorUtils

  @Mock private lateinit var qrView: ImageView

  private lateinit var qrGeneratorImpl: QRGeneratorImpl

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    qrGeneratorImpl = QRGeneratorImpl(qrGeneratorUtils)
  }

  @Test
  fun testGenerateAndSetQRCode() {
    val shLink = "SHLink"
    val qrCodeBitmap = mock(Bitmap::class.java)

    `when`(qrGeneratorUtils.createQRCodeBitmap(shLink)).thenReturn(qrCodeBitmap)
    `when`(qrGeneratorUtils.createLogoBitmap(qrCodeBitmap)).thenReturn(qrCodeBitmap)
    `when`(
        qrGeneratorUtils.overlayLogoOnQRCode(
          qrCodeBitmap,
          qrCodeBitmap,
        ),
      )
      .thenReturn(qrCodeBitmap)

    qrGeneratorImpl.generateAndSetQRCode(shLink, qrView)
    verify(qrView).setImageBitmap(qrCodeBitmap)
  }

  @Test
  fun testGenerateQRCode() {
    val content = "content"
    val qrCodeBitmap = mock(Bitmap::class.java)
    `when`(qrGeneratorUtils.createQRCodeBitmap(content)).thenReturn(qrCodeBitmap)
    `when`(qrGeneratorUtils.createLogoBitmap(qrCodeBitmap)).thenReturn(qrCodeBitmap)
    `when`(
        qrGeneratorUtils.overlayLogoOnQRCode(
          qrCodeBitmap,
          qrCodeBitmap,
        ),
      )
      .thenReturn(qrCodeBitmap)
    val result = qrGeneratorImpl.generateQRCode(content)
    assertEquals(qrCodeBitmap, result)
  }

  @Test
  fun testGenerateQRCodeWithNullBitmap() {
    val content = "content"
    `when`(qrGeneratorUtils.createQRCodeBitmap(content)).thenReturn(null)
    val result = qrGeneratorImpl.generateQRCode(content)
    assertNull(result)
  }
}
