package com.google.android.fhir.document.generate

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView

internal class QRGeneratorImpl(private val qrGeneratorUtils: QRGeneratorUtils) : QRGenerator {

  /* Generate and display the SHL QR code */
  override fun generateAndSetQRCode(shLink: String, qrView: ImageView) {
    val qrCodeBitmap = generateQRCode(shLink)
    updateImageViewOnMainThread(qrView, qrCodeBitmap)
  }

  /* Generates the SHL QR code for the given payload */
  internal fun generateQRCode(content: String): Bitmap {
    val qrCodeBitmap = qrGeneratorUtils.createQRCodeBitmap(content)
    val logoBitmap = qrGeneratorUtils.createLogoBitmap(qrCodeBitmap)
    return qrGeneratorUtils.overlayLogoOnQRCode(qrCodeBitmap, logoBitmap)
  }

  /* Set the image view to the QR code */
  internal fun updateImageViewOnMainThread(qrView: ImageView, qrCodeBitmap: Bitmap) {
    qrView.setImageBitmap(qrCodeBitmap)
  }

}