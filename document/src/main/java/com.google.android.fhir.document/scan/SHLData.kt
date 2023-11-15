package com.google.android.fhir.document.scan

import com.google.android.fhir.document.IPSDocument

/* This data class holds all the information stored in a SHL.
   If the P flag is present, a passcode is needed to decode the data */
data class SHLinkScanData(
  val fullLink: String,
  val shl: String,
  val manifestUrl: String,
  val key: String,
  val label: String,
  val flag: String,
  val exp: String,
  val v: String,
  val ipsDoc: IPSDocument
)
