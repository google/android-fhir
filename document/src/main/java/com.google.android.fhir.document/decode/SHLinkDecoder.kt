package com.google.android.fhir.document.decode

import com.google.android.fhir.document.IPSDocument

interface SHLinkDecoder {

  /* */
  suspend fun decodeSHLinkToDocument(jsonData: String): IPSDocument?

}