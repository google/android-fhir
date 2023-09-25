package com.google.android.fhir.document.interfaces

import com.google.android.fhir.document.dataClasses.SHLData

interface SHLScanner {

  /* Scans a SHL and returns an initialised SHLData object */
  fun scanSHLQRCode(callback: (SHLData?) -> Unit)

  /* Handles a successful scan */
  fun onScanSuccess(callback : (SHLData) -> Unit)

  /* Handles a failed scan */
  fun onScanFail(callback : (Error) -> Unit)
}