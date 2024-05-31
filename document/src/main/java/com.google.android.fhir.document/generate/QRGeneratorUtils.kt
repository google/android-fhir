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

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.google.android.fhir.document.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

internal class QRGeneratorUtils(private val context: Context) {

  /* Creates a QR for a given string */
  fun createQRCodeBitmap(content: String): Bitmap {
    val hints = mutableMapOf<EncodeHintType, Any>()
    hints[EncodeHintType.MARGIN] = 2
    val qrCodeWriter = QRCodeWriter()
    val bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 512, 512, hints)
    val width = bitMatrix.width
    val height = bitMatrix.height
    val qrCodeBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

    for (x in 0 until width) {
      for (y in 0 until height) {
        qrCodeBitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
      }
    }
    return qrCodeBitmap
  }

  /* Creates a bitmap containing the SMART logo */
  fun createLogoBitmap(qrCodeBitmap: Bitmap): Bitmap {
    val logoScale = 0.4
    val logoDrawable = ContextCompat.getDrawable(context, R.drawable.smart_logo)
    val logoAspectRatio =
      logoDrawable!!.intrinsicWidth.toFloat() / logoDrawable.intrinsicHeight.toFloat()
    val width = qrCodeBitmap.width
    val logoWidth = (width * logoScale).toInt()
    val logoHeight = (logoWidth / logoAspectRatio).toInt()

    return convertDrawableToBitmap(logoDrawable, logoWidth, logoHeight)
  }

  /* Overlays the SMART logo in the centre of a QR code */
  fun overlayLogoOnQRCode(qrCodeBitmap: Bitmap, logoBitmap: Bitmap): Bitmap {
    val centerX = (qrCodeBitmap.width - logoBitmap.width) / 2
    val centerY = (qrCodeBitmap.height - logoBitmap.height) / 2

    val backgroundBitmap =
      Bitmap.createBitmap(logoBitmap.width, logoBitmap.height, Bitmap.Config.RGB_565)
    backgroundBitmap.eraseColor(Color.WHITE)

    val canvas = Canvas(backgroundBitmap)
    canvas.drawBitmap(logoBitmap, 0f, 0f, null)

    val finalBitmap = Bitmap.createBitmap(qrCodeBitmap)
    val finalCanvas = Canvas(finalBitmap)
    finalCanvas.drawBitmap(backgroundBitmap, centerX.toFloat(), centerY.toFloat(), null)

    return finalBitmap
  }

  /* Converts the logo into a bitmap */
  private fun convertDrawableToBitmap(drawable: Drawable, width: Int, height: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
  }
}
