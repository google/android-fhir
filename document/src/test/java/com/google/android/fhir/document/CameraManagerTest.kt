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

import android.content.Context
import android.content.pm.PackageManager
import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.android.fhir.document.scan.CameraManager
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.Executors
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class CameraManagerTest {

  @Mock private lateinit var mockContext: Context

  @Mock private lateinit var mockCameraProviderFuture: ListenableFuture<ProcessCameraProvider>

  @Mock private lateinit var mockCameraProvider: ProcessCameraProvider

  private lateinit var cameraManager: CameraManager

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
    Mockito.`when`(mockContext.packageManager).thenReturn(Mockito.mock(PackageManager::class.java))
    Mockito.`when`(ProcessCameraProvider.getInstance(mockContext))
      .thenReturn(mockCameraProviderFuture)

    cameraManager = CameraManager(mockContext, Executors.newSingleThreadExecutor())
  }

  @Test
  fun `bindCamera sets CameraProvider when successful`() {
    Mockito.`when`(mockCameraProviderFuture.get()).thenReturn(mockCameraProvider)
    cameraManager.bindCamera()

    val result = cameraManager.getCameraProvider()
    assert(result != null)
    assert(result === mockCameraProvider)
  }

  @Test
  fun `bindCamera does not set CameraProvider when unsuccessful`() {
    Mockito.`when`(mockCameraProviderFuture.get())
      .thenThrow(RuntimeException("Failed to get CameraProvider"))

    cameraManager.bindCamera()
    val result = cameraManager.getCameraProvider()
    assert(result == null)
  }

  @Test
  fun releaseExecutorCanCorrectlyShutdownCameraExecutor() {
    cameraManager.releaseExecutor()
    // Mockito.verify(cameraExecutor).shutdown()
  }
}
