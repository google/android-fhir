package com.google.android.fhir.document.interfaces

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.google.android.fhir.document.dataClasses.SHLData

interface SHLGenerator {

  /* Should generateKey be part of this interface or should it just be in utils? */
  fun generateKey() : String

  /* Passcode added to generateSHL as it shouldn't be wrapped in the SHL object */
  fun generateSHL(context: Context, shlData: SHLData, passcode: String, qrView: ImageView): Bitmap?
}