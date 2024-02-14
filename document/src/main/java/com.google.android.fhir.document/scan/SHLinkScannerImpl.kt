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

/**
 * Implementation of the [SHLinkScanner] interface.
 *
 * @param cameraManager The manager for handling camera-related operations.
 * @param barcodeDetectorManager The manager for handling barcode detection operations.
 * @param imageAnalysis The [ImageAnalysis] object for processing camera images.
 */
class SHLinkScannerImpl(
  private val cameraManager: CameraManager,
  private val barcodeDetectorManager: BarcodeDetectorManager,
  private val imageAnalysis: ImageAnalysis,
) : SHLinkScanner {

  /**
   * Scans a SHL QR code and calls [successCallback] with the newly instantiated [SHLinkScanData] if
   * successful. Otherwise, invokes [failCallback] with an [Error].
   *
   * @param successCallback Callback function invoked when the SHL QR code is successfully scanned.
   *   It provides a newly instantiated [SHLinkScanData] object.
   * @param failCallback Callback function invoked when an error occurs during the scanning process.
   *   It provides an [Error] object with details about the failure.
   */
  override fun scanSHLQRCode(
    successCallback: (SHLinkScanData) -> Unit,
    failCallback: (Error) -> Unit,
  ) {
    if (cameraManager.hasCameraPermission()) {
      try {
        // Open camera and scan
        val shLinkScanData = scan()
        releaseScanner()
        successCallback(shLinkScanData)
      } catch (error: Error) {
        // There was an error trying to scan the QR code
        failCallback(error)
      }
    } else {
      failCallback(Error("Camera permission not granted"))
    }
  }

  /**
   * Scans the SHL QR code using camera and barcode detection.
   *
   * @return The newly instantiated [SHLinkScanData] if a valid SHL QR code is scanned.
   * @throws Error if no valid scan data is found.
   */
  private fun scan(): SHLinkScanData {
    var scannedData: SHLinkScanData? = null
    imageAnalysis.setAnalyzer(cameraManager.cameraExecutor) { imageProxy ->
      barcodeDetectorManager.processImage(imageProxy) { result ->
        val scannedValue = result?.displayValue
        if (scannedValue != null) {
          scannedData = SHLinkScanData(scannedValue)
        }
      }
    }
    cameraManager.bindCamera()
    return scannedData ?: throw Error("No valid scan data found")
  }

  /**
   * Releases resources associated with the scanner, including camera executor and barcode scanner.
   */
  private fun releaseScanner() {
    cameraManager.releaseExecutor()
    barcodeDetectorManager.releaseBarcodeScanner()
  }
}
