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

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.view.SurfaceHolder
import androidx.core.app.ActivityCompat
import com.google.android.fhir.document.scan.CameraManager
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.common.truth.Truth.assertThat
import java.io.IOException
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.argThat
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class CameraManagerTest {

  private lateinit var mockContext: Context
  private lateinit var mockSurfaceHolder: SurfaceHolder
  private lateinit var mockCameraSource: CameraSource
  private lateinit var mockBarcodeDetector: BarcodeDetector
  private val mockPackageManager: PackageManager = mock()

  @Before
  fun setUp() {
    mockSurfaceHolder = mock(SurfaceHolder::class.java)
    mockCameraSource = mock(CameraSource::class.java)
    mockBarcodeDetector = mock(BarcodeDetector::class.java)
    mockContext = mock(Context::class.java)
    MockitoAnnotations.openMocks(this)
  }

  @Test
  fun `test startCameraPreview with camera permissions granted`() {
    `when`(ActivityCompat.checkSelfPermission(mockContext, Manifest.permission.CAMERA))
      .thenReturn(PackageManager.PERMISSION_GRANTED)

    `when`(mockContext.packageManager).thenReturn(mockPackageManager)
    `when`(mockPackageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)).thenReturn(true)

    CameraManager.startCameraPreview(mockContext, mockSurfaceHolder, mockCameraSource, null)

    verify(mockCameraSource).start(mockSurfaceHolder)
  }

  @Test
  fun `test startCameraPreview with camera permissions denied`() {
    `when`(mockContext.packageManager).thenReturn(mockPackageManager)
    `when`(mockPackageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)).thenReturn(false)
    CameraManager.startCameraPreview(mockContext, mockSurfaceHolder, mockCameraSource, null)

    verify(mockCameraSource, never()).start(mockSurfaceHolder)
  }

  @Test
  fun `test startCameraPreview with CameraFeature`() {
    `when`(
        ActivityCompat.checkSelfPermission(
          mockContext,
          "android.permission.CAMERA",
        ),
      )
      .thenReturn(PackageManager.PERMISSION_GRANTED)
    `when`(mockContext.packageManager).thenReturn(mockPackageManager)
    `when`(mockPackageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)).thenReturn(true)

    CameraManager.startCameraPreview(mockContext, mockSurfaceHolder, mockCameraSource, null)

    verify(mockCameraSource).start(mockSurfaceHolder)
  }

  @Test
  fun `test startCameraPreview without CameraFeature`() {
    `when`(
        ActivityCompat.checkSelfPermission(
          mockContext,
          "android.permission.CAMERA",
        ),
      )
      .thenReturn(PackageManager.PERMISSION_DENIED)
    `when`(mockContext.packageManager).thenReturn(mockPackageManager)
    `when`(mockPackageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)).thenReturn(false)

    CameraManager.startCameraPreview(mockContext, mockSurfaceHolder, mockCameraSource, null)

    verify(mockCameraSource, never()).start(mockSurfaceHolder)
  }

  @Test
  fun `test startCameraPreview with an IOException`() {
    `when`(mockContext.packageManager).thenReturn(mockPackageManager)
    `when`(mockPackageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)).thenReturn(true)
    `when`(mockCameraSource.start(mockSurfaceHolder)).thenThrow(IOException())

    val errorCallback: (Error) -> Unit = mock()

    CameraManager.startCameraPreview(
      mockContext,
      mockSurfaceHolder,
      mockCameraSource,
      errorCallback,
    )

    verify(errorCallback)
      .invoke(
        argThat { message == "Failed to start camera" },
      )
  }

  @Test
  fun `test hasCameraPermission with camera permissions granted`() {
    `when`(mockContext.packageManager).thenReturn(mockPackageManager)
    `when`(mockPackageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)).thenReturn(true)

    val result = CameraManager.hasCameraPermission(mockContext)

    assertThat(result).isTrue()
  }

  @Test
  fun `test hasCameraPermission with camera permissions denied`() {
    `when`(mockContext.packageManager).thenReturn(mockPackageManager)
    `when`(mockPackageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)).thenReturn(false)

    val result = CameraManager.hasCameraPermission(mockContext)

    assertThat(result).isFalse()
  }

  @Test
  fun `test that createCameraSource works as intended`() {
    `when`(mockContext.packageManager).thenReturn(mockPackageManager)
    `when`(mockPackageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)).thenReturn(true)

    val cameraSource = CameraManager.createCameraSource(mockContext, mockBarcodeDetector)

    assertThat(cameraSource).isNotNull()
    assertThat(cameraSource.cameraFacing).isEqualTo(CameraSource.CAMERA_FACING_BACK)
  }
}
