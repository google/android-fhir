package com.google.android.fhir.document.scan

import android.content.Context
import android.content.pm.PackageManager
import com.google.android.fhir.document.IPSDocument

class SHLinkScannerImpl(private val context: Context) : SHLinkScanner {

  private var scanCallback: ((SHLinkScanData) -> Unit)? = null
  private var failCallback: ((Error) -> Unit)? = null
  override fun scanSHLQRCode(
    callback: (SHLinkScanData) -> Unit,
    failCallback: (Error) -> Unit,
  ): SHLinkScanData? {
    this.scanCallback = callback
    this.failCallback = failCallback

    if (hasCameraPermission()) {
      // Open camera and scan qr code
      return SHLinkScanData()
    } else {
      val error = Error("Camera permission not granted")
      failCallback.invoke(error)
    }
    return null
  }

  private fun hasCameraPermission(): Boolean {
    return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
  }
}