package com.google.android.fhir.document

import ScannerUtils
import com.google.android.fhir.document.scan.SHLinkScanData
import com.google.android.fhir.document.scan.SHLinkScannerImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argThat
import org.mockito.kotlin.mock

class SHLinkScannerImplTest {

  @Mock private lateinit var scannerUtils: ScannerUtils
  private lateinit var shLinkScannerImpl: SHLinkScannerImpl
  private val successCallback = mock<(SHLinkScanData) -> Unit>()
  private val failCallback = mock<(Error) -> Unit>()

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    shLinkScannerImpl = SHLinkScannerImpl(scannerUtils)
  }

  @Test
  fun testScanSHLQRCodeWithCameraPermission() {
    val successCallback = mock<(SHLinkScanData) -> Unit>()
    val failCallback = mock<(Error) -> Unit>()

    `when`(scannerUtils.hasCameraPermission()).thenReturn(true)

    shLinkScannerImpl.scanSHLQRCode(successCallback, failCallback)

    verify(scannerUtils, times(1)).setup()
    verify(scannerUtils, times(1)).releaseScanner()
    verify(successCallback, times(1)).invoke(anyOrNull())
    verify(failCallback, times(0)).invoke(anyOrNull())
  }

  @Test
  fun testScanSHLQRCodeWithoutCameraPermission() {

    `when`(scannerUtils.hasCameraPermission()).thenReturn(false)

    shLinkScannerImpl.scanSHLQRCode(successCallback, failCallback)
    verify(scannerUtils, times(0)).setup()
    verify(scannerUtils, times(0)).releaseScanner()
    verify(successCallback, times(0)).invoke(anyOrNull())
    verify(failCallback, times(1)).invoke(argThat { message == "Camera permission not granted" })
  }

}