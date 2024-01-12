package com.google.android.fhir.document.scan

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.view.SurfaceHolder
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import java.io.IOException

class CameraManager(
  private val context: Context,
  private val surfaceHolder: SurfaceHolder,
  private val cameraCallback: (String) -> Unit
) {

  private lateinit var cameraSource: CameraSource

  fun setup() {
    initializeCameraSource()
    setSurfaceCallbacks()
    startScanning()
  }

  private fun initializeCameraSource() {
    cameraSource =
      CameraSource.Builder(context, null)
        .setRequestedPreviewSize(1920, 1080)
        .setAutoFocusEnabled(true)
        .build()
  }

  private fun setSurfaceCallbacks() {
    surfaceHolder.addCallback(surfaceCallback)
  }

  private val surfaceCallback =
    object : SurfaceHolder.Callback {
      override fun surfaceCreated(holder: SurfaceHolder) {
        try {
          if (hasCameraPermission()) {
            if (
              ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA,
              ) != PackageManager.PERMISSION_GRANTED
            ) {
              return
            }
            cameraSource.start(holder)
          }
        } catch (e: IOException) {
          e.printStackTrace()
          throw Error("Failed to start camera")
        }
      }

      override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Not necessary
      }

      override fun surfaceDestroyed(holder: SurfaceHolder) {
        cameraSource.stop()
      }
    }

  private fun startScanning() {
    // Implementation for scanning logic goes here
  }

  private fun hasCameraPermission(): Boolean {
    return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
  }

  fun releaseScanner() {
    cameraSource.stop()
    cameraSource.release()
  }
}
