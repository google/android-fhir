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

import com.google.android.fhir.document.scan.SHLinkScanData
import com.google.android.fhir.document.scan.SHLinkScannerImpl
import com.google.android.fhir.document.scan.ScannerUtils
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.never
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
  private val failedInvocation = 0
  private val successfulInvocation = 1

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    shLinkScannerImpl = SHLinkScannerImpl(scannerUtils)
  }

  @Test
  fun testScanSHLQRCodeWithCameraPermission() {
    `when`(scannerUtils.hasCameraPermission()).thenReturn(true)

    shLinkScannerImpl.scanSHLQRCode(successCallback, failCallback)

    verify(scannerUtils, times(successfulInvocation)).setup()
    verify(scannerUtils, times(successfulInvocation)).releaseScanner()
    verify(successCallback, times(successfulInvocation)).invoke(anyOrNull())
    verify(failCallback, times(failedInvocation)).invoke(anyOrNull())
  }

  @Test
  fun testScanSHLQRCodeWithoutCameraPermission() {
    `when`(scannerUtils.hasCameraPermission()).thenReturn(false)

    shLinkScannerImpl.scanSHLQRCode(successCallback, failCallback)

    verify(scannerUtils, times(failedInvocation)).setup()
    verify(scannerUtils, times(failedInvocation)).releaseScanner()
    verify(successCallback, times(failedInvocation)).invoke(anyOrNull())
    verify(
        failCallback,
        times(successfulInvocation),
      )
      .invoke(argThat { message == "Camera permission not granted" })
  }

  @Test
  fun testScanSHLQRCodeWithCameraPermissionAndScannerSetupFailure() {
    `when`(scannerUtils.hasCameraPermission()).thenReturn(true)
    `when`(scannerUtils.setup()).thenThrow(Error("Scanner setup failed"))

    shLinkScannerImpl.scanSHLQRCode(successCallback, failCallback)

    verify(scannerUtils, times(successfulInvocation)).setup()
    verify(scannerUtils, times(failedInvocation)).releaseScanner()
    verify(successCallback, times(failedInvocation)).invoke(anyOrNull())
    verify(
        failCallback,
        times(successfulInvocation),
      )
      .invoke(argThat { message == "Scanner setup failed" })
  }

  @Test
  fun testScanSHLQRCodeSuccessWithData() {
    `when`(scannerUtils.hasCameraPermission()).thenReturn(true)
    val mockSHLinkScanData = SHLinkScanData("MockScanData")
    `when`(scannerUtils.setup()).thenReturn(mockSHLinkScanData)

    shLinkScannerImpl.scanSHLQRCode(successCallback, failCallback)

    verify(scannerUtils, times(successfulInvocation)).setup()
    verify(scannerUtils, times(successfulInvocation)).releaseScanner()
    verify(successCallback, times(successfulInvocation)).invoke(mockSHLinkScanData)
    verify(failCallback, times(failedInvocation)).invoke(anyOrNull())
  }

  @Test
  fun testCallbacksInvocationOrder() {
    `when`(scannerUtils.hasCameraPermission()).thenReturn(true)

    shLinkScannerImpl.scanSHLQRCode(successCallback, failCallback)

    val inOrder = inOrder(scannerUtils, successCallback, failCallback)
    inOrder.verify(scannerUtils).setup()
    inOrder.verify(scannerUtils).releaseScanner()
    inOrder.verify(successCallback).invoke(anyOrNull())
    inOrder.verify(failCallback, never()).invoke(anyOrNull())
  }
}
