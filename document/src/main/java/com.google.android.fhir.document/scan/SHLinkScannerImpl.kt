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

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.view.SurfaceHolder
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.IOException

class SHLinkScannerImpl(private val context: Context, private val surfaceHolder: SurfaceHolder) :
  SHLinkScanner {

  private lateinit var cameraSource: CameraSource
  private lateinit var barcodeDetector: BarcodeDetector
  private var scanCallback: ((SHLinkScanData) -> Unit)? = null
  private var failCallback: ((Error) -> Unit)? = null

  private var scannedData: SHLinkScanData? = null

  override fun scanSHLQRCode(
    successCallback: (SHLinkScanData) -> Unit,
    failCallback: (Error) -> Unit,
  ) {
    if (!hasCameraPermission()) {
      val error = Error("Camera permission not granted")
      failCallback.invoke(error)
      failCallback(error)
    } else {
      setup()
      // Set the scanCallback to handle the result
      scanCallback = { shlData ->
        // Handle the result here if needed
        successCallback(shlData)
      }
    }
  }

  private fun setup() {
    barcodeDetector = createBarcodeDetector()
    cameraSource = createCameraSource(barcodeDetector)
    surfaceHolder.addCallback(createSurfaceCallback())
    barcodeDetector.setProcessor(createBarcodeProcessor())
    println("FINISHED SETUP")
  }

  private fun createBarcodeProcessor(): Detector.Processor<Barcode> {
    return object : Detector.Processor<Barcode> {
      override fun release() {
        return
      }

      private var scanSucceeded = false

      override fun receiveDetections(detections: Detections<Barcode>) {
        if (scanSucceeded) {
          return
        }

        val barcodes = detections.detectedItems
        if (barcodes.size() == 1) {
          val scannedValue = barcodes.valueAt(0).rawValue
          val shlData = SHLinkScanData.create(scannedValue)
          scannedData = shlData
          scanCallback?.invoke(shlData)
          scanSucceeded = true
        }
      }
    }
  }

  private fun createBarcodeDetector(): BarcodeDetector {
    return BarcodeDetector.Builder(context).setBarcodeFormats(Barcode.ALL_FORMATS).build()
  }

  private fun createCameraSource(barcodeDetector: BarcodeDetector): CameraSource {
    return CameraSource.Builder(context, barcodeDetector)
      .setRequestedPreviewSize(1920, 1080)
      .setAutoFocusEnabled(true)
      .build()
  }

  private fun createSurfaceCallback(): SurfaceHolder.Callback {
    return object : SurfaceHolder.Callback {
      override fun surfaceCreated(holder: SurfaceHolder) {
        startCameraPreview(holder)
      }

      override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        return
      }

      override fun surfaceDestroyed(holder: SurfaceHolder) {
        cameraSource.stop()
      }
    }
  }

  private fun startCameraPreview(holder: SurfaceHolder) {
    try {
      if (hasCameraPermission()) {
        if (
          ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA,
          ) != PackageManager.PERMISSION_GRANTED
        ) {
          return
        }
        cameraSource.start(holder)
      }
    } catch (e: IOException) {
      e.printStackTrace()
      failCallback?.invoke(Error("Failed to start camera"))
    }
  }

  private fun hasCameraPermission(): Boolean {
    return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
  }

  fun release() {
    cameraSource.stop()
    cameraSource.release()
    barcodeDetector.release()
  }
}