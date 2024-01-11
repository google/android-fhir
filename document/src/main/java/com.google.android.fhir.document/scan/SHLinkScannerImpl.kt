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

/*
Implementation of the SHLinkScanner interface.
Calls successfulCallback with a newly instantiated SHLinkScanData if a SHL is successfully scanned.
Otherwise, an error is thrown.
 */
class SHLinkScannerImpl(private val scannerUtils: ScannerUtils) : SHLinkScanner {

  override fun scanSHLQRCode(
    successCallback: (SHLinkScanData) -> Unit,
    failCallback: (Error) -> Unit,
  ) {
    if (scannerUtils.hasCameraPermission()) {
      try {
        // Open camera and scan qr code
        val shLinkScanData = scannerUtils.setup()
        scannerUtils.releaseScanner()
        successCallback(shLinkScanData)
      } catch (error: Error) {
        // There was an error trying to scan the QR code
        failCallback(error)
      }
    }
    else {
      val error = Error("Camera permission not granted")
      failCallback(error)
    }
  }
}
