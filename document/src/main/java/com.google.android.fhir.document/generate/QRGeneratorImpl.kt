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

package com.google.android.fhir.document.generate

import android.graphics.Bitmap
import android.widget.ImageView

internal class QRGeneratorImpl(private val qrGeneratorUtils: QRGeneratorUtils) : QRGenerator {

  /* Generate and display the SHL QR code */
  override fun generateAndSetQRCode(shLink: String, qrView: ImageView) {
    val qrCodeBitmap = generateQRCode(shLink)
    qrView.setImageBitmap(qrCodeBitmap)
  }

  /* Generates the SHL QR code for the given payload */
  internal fun generateQRCode(content: String): Bitmap {
    val qrCodeBitmap = qrGeneratorUtils.createQRCodeBitmap(content)
    val logoBitmap = qrGeneratorUtils.createLogoBitmap(qrCodeBitmap)
    return qrGeneratorUtils.overlayLogoOnQRCode(qrCodeBitmap, logoBitmap)
  }
}
