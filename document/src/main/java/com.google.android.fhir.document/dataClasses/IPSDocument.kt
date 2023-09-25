package com.google.android.fhir.document.dataClasses

import java.io.Serializable
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Patient

/* A data class object which stores an IPS document, the patient it relates to, and a list of
`  titles present in the document */
data class IPSDocument(
  var document : Bundle,
  var titles : ArrayList<Title>,
  var patient : Patient
) : Serializable {

  constructor() : this(Bundle(), ArrayList<Title>(), Patient())
  constructor(bundle : Bundle) : this(bundle, ArrayList<Title>(), Patient())


}
