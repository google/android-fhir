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

import android.content.Context
import android.view.SurfaceHolder
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.document.scan.CameraManager
import com.google.android.fhir.document.scan.SHLinkScanData
import com.google.android.fhir.document.scan.SHLinkScannerImpl
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.barcode.BarcodeDetector
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argThat
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SHLinkScannerImplTest {

  @Mock private lateinit var context: Context

  @Mock private lateinit var surfaceHolder: SurfaceHolder

  @Mock private lateinit var cameraManager: CameraManager

  private lateinit var shLinkScannerImpl: SHLinkScannerImpl

  private val successCallback = mock<(SHLinkScanData) -> Unit>()
  private val failCallback = mock<(Error) -> Unit>()
  private val failedInvocation = 0
  private val successfulInvocation = 1

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    shLinkScannerImpl = SHLinkScannerImpl(context, surfaceHolder, cameraManager)
  }

  @Test
  fun testScanSHLQRCodeWithCameraPermission() {
    `when`(cameraManager.hasCameraPermission(context)).thenReturn(true)
    val mockBarcodeDetector = mock<BarcodeDetector>()
    val mockCameraSource = mock<CameraSource>()
    `when`(cameraManager.createCameraSource(context, mockBarcodeDetector))
      .thenReturn(mockCameraSource)

    shLinkScannerImpl.scanSHLQRCode(successCallback, failCallback)

    verify(cameraManager, times(successfulInvocation))
      .createCameraSource(context, mockBarcodeDetector)
    verify(mockCameraSource).start(surfaceHolder)
    verify(successCallback, times(successfulInvocation)).invoke(anyOrNull())
    verify(failCallback, times(failedInvocation)).invoke(anyOrNull())
  }

  @Test
  fun testScanSHLQRCodeWithoutCameraPermission() {
    `when`(cameraManager.hasCameraPermission(context)).thenReturn(false)
    val mockBarcodeDetector = mock<BarcodeDetector>()
    `when`(cameraManager.createCameraSource(context, mockBarcodeDetector)).thenReturn(mock {})

    shLinkScannerImpl.scanSHLQRCode(successCallback, failCallback)

    verify(cameraManager, times(failedInvocation)).createCameraSource(context, mockBarcodeDetector)
    verify(successCallback, times(failedInvocation)).invoke(anyOrNull())
    verify(failCallback, times(2)).invoke(argThat { message == "Camera permission not granted" })
  }
}
