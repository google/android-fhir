package com.google.android.fhir.document.generate

import android.content.Context
import android.widget.ImageView

interface QRGenerator {

  /* Generate and display the SHL QR code */
  fun generateAndSetQRCode(shLink: String, qrView: ImageView)

}