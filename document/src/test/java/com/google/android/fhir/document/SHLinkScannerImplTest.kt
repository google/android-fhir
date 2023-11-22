package com.google.android.fhir.document

import com.google.android.fhir.document.scan.SHLinkScanData
import com.google.android.fhir.document.scan.SHLinkScannerImpl
import com.google.android.fhir.document.scan.ScannerUtils
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
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

  @Test
  fun testScanSHLQRCodeWithCameraPermissionAndScannerSetupFailure() {
    `when`(scannerUtils.hasCameraPermission()).thenReturn(true)
    `when`(scannerUtils.setup()).thenThrow(Error("Scanner setup failed"))

    shLinkScannerImpl.scanSHLQRCode(successCallback, failCallback)

    verify(scannerUtils, times(1)).setup()
    verify(scannerUtils, times(0)).releaseScanner()
    verify(successCallback, times(0)).invoke(anyOrNull())
    verify(failCallback, times(1)).invoke(argThat { message == "Scanner setup failed" })
  }


}