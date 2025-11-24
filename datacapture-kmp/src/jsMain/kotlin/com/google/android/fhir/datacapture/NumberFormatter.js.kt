package com.google.android.fhir.datacapture

actual object NumberFormatter {
  actual fun formatInteger(value: Int): String {
    return value.toString()
  }
}
