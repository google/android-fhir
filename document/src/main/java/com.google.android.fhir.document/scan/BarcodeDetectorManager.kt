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

class BarcodeDetectorManager(
  private val barcodeScanner: BarcodeScanner,
) {
  fun processImage(imageProxy: ImageProxy, onResult: (Barcode?) -> Unit) {
    val data = ByteArray(imageProxy.planes[0].buffer.capacity())
    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
    val inputImage =
      com.google.mlkit.vision.common.InputImage.fromByteArray(
        data,
        imageProxy.width,
        imageProxy.height,
        rotationDegrees,
        com.google.mlkit.vision.common.InputImage.IMAGE_FORMAT_NV21,
      )

    barcodeScanner
      .process(inputImage)
      .addOnSuccessListener { barcodes ->
        if (barcodes.isNotEmpty()) {
          val result = barcodes.single()
          onResult(result)
        } else {
          onResult(null)
        }
      }
      .addOnFailureListener {
        // Handle failure
        onResult(null)
      }
      .addOnCompleteListener { imageProxy.close() }
  }

  fun releaseBarcodeScanner() {
    barcodeScanner.close()
  }
}
