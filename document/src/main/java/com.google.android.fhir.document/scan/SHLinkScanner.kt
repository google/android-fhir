package com.google.android.fhir.document.scan

interface SHLinkScanner {

  /* Scans a SHL and returns an initialised SHLData object */
  fun scanSHLQRCode(
    successCallback: (SHLinkScanData) -> Unit,
    failCallback: (Error) -> Unit
  ) : SHLinkScanData?

}