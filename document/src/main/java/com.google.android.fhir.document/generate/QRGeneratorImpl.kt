package com.google.android.fhir.document.generate

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.widget.ImageView

internal class QRGeneratorImpl(private val qrGeneratorUtils: QRGeneratorUtils) : QRGenerator {

  private val handler = Handler(Looper.getMainLooper())

  /* Generate and display the SHL QR code */
  override fun generateAndSetQRCode(context: Context, shLink: String, qrView: ImageView) {
    val qrCodeBitmap = generateQRCode(context, shLink)
    updateImageViewOnMainThread(qrView, qrCodeBitmap)
  }

  /* Generates the SHL QR code for the given payload */
  private fun generateQRCode(context: Context, content: String): Bitmap {
    val qrCodeBitmap = qrGeneratorUtils.createQRCodeBitmap(content)
    val logoBitmap = qrGeneratorUtils.createLogoBitmap(context, qrCodeBitmap)
    return qrGeneratorUtils.overlayLogoOnQRCode(qrCodeBitmap, logoBitmap)
  }

  /* Set the image view to the QR code */
  private fun updateImageViewOnMainThread(qrView: ImageView, qrCodeBitmap: Bitmap) {
    handler.post { qrView.setImageBitmap(qrCodeBitmap) }
  }

}