package com.google.android.fhir.cqlreference

import android.content.Context

class FhirEngineBuilder constructor(context: Context) {
  private val services = FhirServices.builder(context)
  fun build() = services.build().fhirEngine
}
