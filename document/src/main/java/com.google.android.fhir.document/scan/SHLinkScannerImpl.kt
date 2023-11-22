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

package com.google.android.fhir.document.scan

class SHLinkScannerImpl(private val scannerUtils: ScannerUtils) :
  SHLinkScanner {

  private var scanCallback: ((SHLinkScanData) -> Unit)? = null
  private var failCallback: ((Error) -> Unit)? = null

  override fun scanSHLQRCode(
    successCallback: (SHLinkScanData) -> Unit,
    failCallback: (Error) -> Unit,
  ) {
    this.scanCallback = successCallback
    this.failCallback = failCallback

    return if (scannerUtils.hasCameraPermission()) {
      try {
        // Open camera and scan qr code
        val shLinkScanData = scannerUtils.setup()
        scannerUtils.releaseScanner()
        successCallback.invoke(shLinkScanData)
      }
      catch (error: Error) {
        failCallback.invoke(error)
      }
    } else {
      val error = Error("Camera permission not granted")
      failCallback.invoke(error)
    }
  }


}
