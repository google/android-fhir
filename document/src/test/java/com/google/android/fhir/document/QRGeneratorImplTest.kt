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

  @Mock
  private lateinit var qrGeneratorUtils: QRGeneratorUtils

  @Mock
  private lateinit var qrView: ImageView

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
        qrCodeBitmap
      )
    ).thenReturn(qrCodeBitmap)

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
        qrCodeBitmap
      )
    ).thenReturn(qrCodeBitmap)
    val result = qrGeneratorImpl.generateQRCode(content)
    assertEquals(qrCodeBitmap, result)
  }

  @Test
  fun testUpdateImageViewOnMainThread() {
    val qrCodeBitmap = mock(Bitmap::class.java)
    qrGeneratorImpl.updateImageViewOnMainThread(qrView, qrCodeBitmap)
    verify(qrView).setImageBitmap(qrCodeBitmap)
  }

  @Test
  fun testGenerateQRCodeWithNullBitmap() {
    val content = "content"
    `when`(qrGeneratorUtils.createQRCodeBitmap(content)).thenReturn(null)
    val result = qrGeneratorImpl.generateQRCode(content)
    assertNull(result)
  }
}