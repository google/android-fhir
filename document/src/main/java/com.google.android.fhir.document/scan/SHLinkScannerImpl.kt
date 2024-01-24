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

package com.google.android.fhir.document.scan

import androidx.camera.core.ImageAnalysis

/*
Implementation of the SHLinkScanner interface.
Calls successfulCallback with a newly instantiated SHLinkScanData if a SHL is successfully scanned.
Otherwise, an error is thrown.
 */
class SHLinkScannerImpl(
  private val cameraManager: CameraManager,
  private val barcodeDetectorManager: BarcodeDetectorManager,
  private val imageAnalysis: ImageAnalysis,
) : SHLinkScanner {

  override fun scanSHLQRCode(
    successCallback: (SHLinkScanData) -> Unit,
    failCallback: (Error) -> Unit,
  ) {
    if (cameraManager.hasCameraPermission()) {
      try {
        // Open camera and scan qr code
        val shLinkScanData = scan()
        releaseScanner()
        successCallback(shLinkScanData)
      } catch (error: Error) {
        // There was an error trying to scan the QR code
        failCallback(error)
      }
    } else {
      val error = Error("Camera permission not granted")
      failCallback(error)
    }
  }

  private fun scan(): SHLinkScanData {
    // val imageAnalysis =
    //   ImageAnalysis.Builder()
    //     .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
    //     .build()

    var scannedData: SHLinkScanData? = null

    imageAnalysis.setAnalyzer(cameraManager.cameraExecutor) { imageProxy ->
      barcodeDetectorManager.processImage(imageProxy) { result ->
        // Handle the scanned value as needed
        val scannedValue = result?.displayValue
        if (scannedValue != null) {
          scannedData = SHLinkScanData(scannedValue)
        }
      }
    }

    cameraManager.bindCamera(imageAnalysis)

    return scannedData ?: throw Error("No valid scan data found")
  }

  private fun releaseScanner() {
    cameraManager.releaseExecutor()
    barcodeDetectorManager.releaseBarcodeScanner()
  }
}
