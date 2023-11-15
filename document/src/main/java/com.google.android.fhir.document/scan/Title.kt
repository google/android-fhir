package com.google.android.fhir.document.scan

import org.hl7.fhir.r4.model.Resource

/* A Title contains a string storing the title itself and a list of resource associated to it */
data class Title(
  val name: String,
  val dataEntries: ArrayList<Resource>
)