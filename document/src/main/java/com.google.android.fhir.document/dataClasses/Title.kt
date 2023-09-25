package com.google.android.fhir.document.dataClasses

import java.io.Serializable
import org.hl7.fhir.r4.model.Resource

/* A Title contains a string storing the title itself and a list of resource associated to it */
data class Title(
  var name: String,
  var dataEntries: ArrayList<Resource>
) : Serializable {

  constructor(name: String) : this(name, ArrayList())

}
