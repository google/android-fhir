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

import android.content.Context
import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning

class ScannerUtils(
  context: Context,
  lifecycleOwner: LifecycleOwner,
) {

  private val cameraManager = CameraManager(context, lifecycleOwner)
  private val barcodeDetectorManager: BarcodeDetectorManager by lazy {
    BarcodeDetectorManager(
      BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build(),
      ),
    )
  }

  fun setup(): SHLinkScanData {
    val imageAnalysis =
      ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()

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

  internal fun hasCameraPermission(): Boolean {
    return cameraManager.hasCameraPermission()
  }

  fun releaseScanner() {
    cameraManager.releaseExecutor()
    barcodeDetectorManager.releaseBarcodeScanner()
  }
}
