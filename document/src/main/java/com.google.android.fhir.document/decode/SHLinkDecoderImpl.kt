package com.google.android.fhir.document.decode

import com.google.android.fhir.document.IPSDocument
import com.google.android.fhir.document.scan.SHLinkScanData

class SHLinkDecoderImpl(
  private val shlData: SHLinkScanData?,
  private val readSHLinkUtils: ReadSHLinkUtils,
) : SHLinkDecoder {
  override suspend fun decodeSHLinkToDocument(jsonData: String): IPSDocument? {
    TODO("Not yet implemented")
  }
}