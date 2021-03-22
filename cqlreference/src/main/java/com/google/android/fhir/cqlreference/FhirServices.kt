package com.google.android.fhir.cqlreference

import android.content.Context

internal data class FhirServices(val fhirEngine: FhirEngine) {
  class Builder constructor(private val context: Context) {
    fun build(): FhirServices = FhirServices(FhirEngineImp(context))
  }

  companion object {
    fun builder(context: Context) = Builder(context)
  }
}
