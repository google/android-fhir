package com.google.android.fhir.cqlreference

import org.hl7.fhir.r4.model.Resource

interface FhirEngine {
  fun <R : Resource> save(resource: R)
}