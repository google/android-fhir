package com.google.android.fhir

import android.app.Application
import android.content.Context

abstract class CoreApplication : Application() {
  abstract val fhirEngine: FhirEngine
  abstract fun constructFhirEngine(): FhirEngine

  companion object {
    fun fhirEngine(context: Context) = (context.applicationContext as CoreApplication).fhirEngine
  }
}
