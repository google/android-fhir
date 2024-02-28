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
import android.view.SurfaceHolder
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector

class SHLinkScannerImpl(
  private val context: Context,
  private val surfaceHolder: SurfaceHolder,
  private val cameraManager: CameraManager,
) : SHLinkScanner {

  private lateinit var cameraSource: CameraSource
  private lateinit var barcodeDetector: BarcodeDetector
  private var scanCallback: ((SHLinkScanData) -> Unit)? = null
  private var failCallback: ((Error) -> Unit)? = null

  /**
   * Scans a SHL QR code and returns an initialised [SHLinkScanData] object through the success
   * callback or reports an error through the fail callback.
   *
   * @param successCallback Callback function invoked when the SHL QR code is successfully scanned.
   *   It provides an initialised [SHLinkScanData] object.
   * @param failCallback Callback function invoked when an error occurs during the scanning process.
   *   It provides an [Error] object with details about the failure.
   */
  override fun scanSHLQRCode(
    successCallback: (SHLinkScanData) -> Unit,
    failCallback: (Error) -> Unit,
  ) {
    if (!cameraManager.hasCameraPermission(context)) {
      val error = Error("Camera permission not granted")
      failCallback.invoke(error)
      failCallback(error)
    } else {
      setup()
      scanCallback = { shlData -> successCallback(shlData) }
    }
  }

  /** Sets up the camera and barcode detector. */
  private fun setup() {
    barcodeDetector = createBarcodeDetector()
    cameraSource = cameraManager.createCameraSource(context, barcodeDetector)
    surfaceHolder.addCallback(createSurfaceCallback())
    barcodeDetector.setProcessor(createBarcodeProcessor())
  }

  /** Creates a barcode detector instance. */
  private fun createBarcodeDetector(): BarcodeDetector {
    return BarcodeDetector.Builder(context).setBarcodeFormats(Barcode.ALL_FORMATS).build()
  }

  /** Creates a barcode processor to handle detected barcodes. */
  private fun createBarcodeProcessor(): Detector.Processor<Barcode> {
    return object : Detector.Processor<Barcode> {
      override fun release() {
        return
      }

      // Process the detected barcodes.
      override fun receiveDetections(detections: Detector.Detections<Barcode>) {
        val barcodes = detections.detectedItems
        if (barcodes.size() == 1) {
          val scannedValue = barcodes.valueAt(0).rawValue
          val shlData = SHLinkScanData.create(scannedValue)
          scanCallback?.invoke(shlData)
        }
      }
    }
  }

  /** Creates a surface holder callback to handle camera preview and barcode detection. */
  private fun createSurfaceCallback(): SurfaceHolder.Callback {
    return object : SurfaceHolder.Callback {
      override fun surfaceCreated(holder: SurfaceHolder) {
        cameraManager.startCameraPreview(context, holder, cameraSource, failCallback)
      }

      override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        return
      }

      override fun surfaceDestroyed(holder: SurfaceHolder) {
        cameraSource.stop()
      }
    }
  }

  /** Releases resources. */
  fun release() {
    cameraSource.stop()
    cameraSource.release()
    barcodeDetector.release()
  }
}
