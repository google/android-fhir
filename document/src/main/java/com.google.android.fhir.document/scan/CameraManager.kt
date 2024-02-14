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

package com.google.android.fhir.document.scan

import android.content.Context
import android.content.pm.PackageManager
import androidx.camera.core.CameraProvider
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * A class handling the device's camera operations needed to start and stop the camera.
 *
 * @param context The context in which the CameraManager is instantiated.
 * @param cameraExecutor The executor service used for camera operations.
 */
class CameraManager(
  private val context: Context,
  val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor(),
) {
  private var cameraProvider: CameraProvider? = null

  /** Initializes the camera provider by binding it to the given context. */
  fun bindCamera() {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener(
      { cameraProvider = cameraProviderFuture.get() },
      ContextCompat.getMainExecutor(context),
    )
  }

  /** Shuts down the camera executor service. */
  fun releaseExecutor() {
    cameraExecutor.shutdown()
  }

  /**
   * Checks if the app has the required camera permission.
   *
   * @return True if the camera permission is granted, false otherwise.
   */
  internal fun hasCameraPermission(): Boolean {
    return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
  }

  /**
   * Gets the CameraProvider instance.
   *
   * @return The CameraProvider instance or null if not initialized.
   */
  fun getCameraProvider(): CameraProvider? {
    return cameraProvider
  }
}
