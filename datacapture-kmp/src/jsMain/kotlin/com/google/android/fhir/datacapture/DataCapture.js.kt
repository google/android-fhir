package com.google.android.fhir.datacapture

actual object DataCapture {
  actual fun getConfiguration(): DataCaptureConfig {
    return DataCaptureConfig()
  }
}