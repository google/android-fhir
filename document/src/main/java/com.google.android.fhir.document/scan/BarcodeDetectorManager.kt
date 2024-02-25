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

import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.common.InputImage

/**
 * A class handling the detection and scanning of SHL QR codes by a device's camera.
 *
 * @param barcodeScanner The instance of BarcodeScanner used for scanning.
 */
class BarcodeDetectorManager(
  private val barcodeScanner: BarcodeScanner,
) {

  /**
   * Processes the image using the provided [ImageProxy] and performs barcode scanning.
   *
   * @param imageProxy The ImageProxy containing the captured image.
   * @param onResult Callback function to handle the scanning result.
   */
  fun processImage(imageProxy: ImageProxy, onResult: (Barcode?) -> Unit) {
    // Check if planes are available
    if (imageProxy.planes.isEmpty()) {
      onResult(null)
      return
    }

    val plane = imageProxy.planes[0]
    if (plane == null) {
      onResult(null)
      return
    }

    // Extract the data from the ImageProxy and create an InputImage from this data
    val data = ByteArray(plane.buffer.capacity())
    plane.buffer.get(data)
    val rotationDegrees = imageProxy.imageInfo.rotationDegrees

    // Process the input image using the barcode scanner
    val inputImage =
      InputImage.fromByteArray(
        data,
        imageProxy.width,
        imageProxy.height,
        rotationDegrees,
        InputImage.IMAGE_FORMAT_NV21,
      )

    barcodeScanner
      .process(inputImage)
      .addOnSuccessListener { barcodes ->
        // If barcodes are detected, pass the result to the callback - otherwise, pass null
        onResult(barcodes.firstOrNull())
      }
      .addOnFailureListener { onResult(null) }
      .addOnCompleteListener {
        // Close the ImageProxy in the finally block to ensure it is closed even if an exception
        // occurs
        imageProxy.close()
      }
  }

  /** Closes the [barcodeScanner]. */
  fun releaseBarcodeScanner() {
    barcodeScanner.close()
  }
}
