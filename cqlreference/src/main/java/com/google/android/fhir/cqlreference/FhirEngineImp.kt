package com.google.android.fhir.cqlreference

import android.content.Context
import org.hl7.fhir.r4.model.Resource

class FhirEngineImp constructor(context: Context) : FhirEngine {
  override fun <R : Resource> save(resource: R) {
    // TODO: implement all methods as well using db
  }
}
