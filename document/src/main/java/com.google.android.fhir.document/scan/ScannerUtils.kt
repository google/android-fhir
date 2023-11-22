/*
 * Copyright 2023 Google LLC
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

import android.content.Context
import android.content.pm.PackageManager
import android.view.SurfaceHolder
import androidx.core.app.ActivityCompat
import com.google.android.fhir.document.scan.SHLinkScanData
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.IOException

class ScannerUtils(
  private val context: Context,
  private val surfaceHolder: SurfaceHolder,
) {

  private lateinit var cameraSource: CameraSource
  private lateinit var barcodeDetector: BarcodeDetector

  fun setup() : SHLinkScanData {
    barcodeDetector =
      BarcodeDetector.Builder(context).setBarcodeFormats(Barcode.ALL_FORMATS).build()

    cameraSource =
      CameraSource.Builder(context, barcodeDetector)
        .setRequestedPreviewSize(1920, 1080)
        .setAutoFocusEnabled(true)
        .build()

    var scannedData: SHLinkScanData? = null

    surfaceHolder.addCallback(
      object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
          try {
            // Start preview after 1s delay
            if (
              ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA,
              ) != PackageManager.PERMISSION_GRANTED
            ) {
              return
            }
            cameraSource.start(holder)
          } catch (e: IOException) {
            e.printStackTrace()
            throw Error("Failed to start camera")
          }
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
          // Not needed for this example
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
          stopScanning()
        }
      },
    )

    barcodeDetector.setProcessor(
      object : Detector.Processor<Barcode> {
        override fun release() {
          // Scanner has been closed
        }

        var scanSucceeded = false

        override fun receiveDetections(detections: Detector.Detections<Barcode>) {
          if (scanSucceeded) {
            return
          }

          val barcodes = detections.detectedItems
          if (barcodes.size() == 1) {
            val scannedValue = barcodes.valueAt(0).rawValue
            scannedData = SHLinkScanData(scannedValue)
            scanSucceeded = true
          }
        }
      },
    )
    return scannedData ?: throw Error("No valid scan data found")
  }

  private fun stopScanning() {
    cameraSource.stop()
  }

  internal fun hasCameraPermission(): Boolean {
    return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
  }

  fun releaseScanner() {
    stopScanning()
    cameraSource.release()
    barcodeDetector.release()
  }
}
