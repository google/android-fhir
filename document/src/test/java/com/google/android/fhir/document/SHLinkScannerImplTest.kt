/*
 * Copyright 2023-2024 Google LLC
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

import androidx.camera.core.ImageAnalysis
import com.google.android.fhir.document.scan.BarcodeDetectorManager
import com.google.android.fhir.document.scan.CameraManager
import com.google.android.fhir.document.scan.SHLinkScanData
import com.google.android.fhir.document.scan.SHLinkScannerImpl
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SHLinkScannerImplTest {

  @Mock private lateinit var cameraManager: CameraManager

  @Mock private lateinit var barcodeDetectorManager: BarcodeDetectorManager

  @Mock private lateinit var imageAnalysis: ImageAnalysis
  private lateinit var shLinkScannerImpl: SHLinkScannerImpl
  private val successCallback = mock<(SHLinkScanData) -> Unit>()
  private val failCallback = mock<(Error) -> Unit>()
  private val failedInvocation = 0
  private val successfulInvocation = 1

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    shLinkScannerImpl = SHLinkScannerImpl(cameraManager, barcodeDetectorManager, imageAnalysis)
  }

  @Test
  fun testScanSHLQRCodeWithCameraPermission() {
    `when`(cameraManager.hasCameraPermission()).thenReturn(true)

    shLinkScannerImpl.scanSHLQRCode(successCallback, failCallback)

    verify(barcodeDetectorManager, times(successfulInvocation)).processImage(any(), any())
    verify(cameraManager, times(successfulInvocation)).releaseExecutor()
    verify(barcodeDetectorManager, times(successfulInvocation)).releaseBarcodeScanner()
    verify(successCallback, times(successfulInvocation)).invoke(anyOrNull())
    verify(failCallback, times(failedInvocation)).invoke(anyOrNull())
  }

  // @Test
  // fun testScanSHLQRCodeWithoutCameraPermission() {
  //   `when`(cameraManager.hasCameraPermission()).thenReturn(false)
  //
  //   shLinkScannerImpl.scanSHLQRCode(successCallback, failCallback)
  //
  //   verify(scannerUtils, times(failedInvocation)).scan()
  //   verify(scannerUtils, times(failedInvocation)).releaseScanner()
  //   verify(successCallback, times(failedInvocation)).invoke(anyOrNull())
  //   verify(
  //       failCallback,
  //       times(successfulInvocation),
  //     )
  //     .invoke(argThat { message == "Camera permission not granted" })
  // }
  //
  // @Test
  // fun testScanSHLQRCodeWithCameraPermissionAndScannerSetupFailure() {
  //   `when`(scannerUtils.hasCameraPermission()).thenReturn(true)
  //   `when`(scannerUtils.scan()).thenThrow(Error("Scanner setup failed"))
  //
  //   shLinkScannerImpl.scanSHLQRCode(successCallback, failCallback)
  //
  //   verify(scannerUtils, times(successfulInvocation)).scan()
  //   verify(scannerUtils, times(failedInvocation)).releaseScanner()
  //   verify(successCallback, times(failedInvocation)).invoke(anyOrNull())
  //   verify(
  //       failCallback,
  //       times(successfulInvocation),
  //     )
  //     .invoke(argThat { message == "Scanner setup failed" })
  // }
  //
  // @Test
  // fun testScanSHLQRCodeSuccessWithData() {
  //   `when`(scannerUtils.hasCameraPermission()).thenReturn(true)
  //   val mockSHLinkScanData = SHLinkScanData("MockScanData")
  //   `when`(scannerUtils.scan()).thenReturn(mockSHLinkScanData)
  //
  //   shLinkScannerImpl.scanSHLQRCode(successCallback, failCallback)
  //
  //   verify(scannerUtils, times(successfulInvocation)).scan()
  //   verify(scannerUtils, times(successfulInvocation)).releaseScanner()
  //   verify(successCallback, times(successfulInvocation)).invoke(mockSHLinkScanData)
  //   verify(failCallback, times(failedInvocation)).invoke(anyOrNull())
  // }
  //
  // @Test
  // fun testCallbacksInvocationOrder() {
  //   `when`(scannerUtils.hasCameraPermission()).thenReturn(true)
  //
  //   shLinkScannerImpl.scanSHLQRCode(successCallback, failCallback)
  //
  //   val inOrder = inOrder(scannerUtils, successCallback, failCallback)
  //   inOrder.verify(scannerUtils).scan()
  //   inOrder.verify(scannerUtils).releaseScanner()
  //   inOrder.verify(successCallback).invoke(anyOrNull())
  //   inOrder.verify(failCallback, never()).invoke(anyOrNull())
  // }
}
