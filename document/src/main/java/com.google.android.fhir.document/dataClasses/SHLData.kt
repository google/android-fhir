package com.google.android.fhir.document.dataClasses

import com.google.android.fhir.document.dataClasses.IPSDocument
import java.io.Serializable

/* This data class holds all the information stored in a SHL.
   If the P flag is present, a passcode is needed to decode the data */
data class SHLData(
  var fullLink: String,
  var shl: String,
  var manifestUrl: String,
  var key: String,
  var label: String,
  var flag: String,
  var exp: String,
  var v: String,
  var ipsDoc: IPSDocument
) : Serializable {

  constructor() : this("", "", "", "", "", "", "", "", IPSDocument())
  constructor(fullLink : String) : this(fullLink, "",  "", "", "", "", "", "", IPSDocument())
  constructor(doc : IPSDocument) : this("", "", "", "", "", "", "", "", doc)

}
