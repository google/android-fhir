package com.google.android.fhir.document.scan

import com.google.android.fhir.document.IPSDocument

/**
 * Represents a SHL data structure, which stores information related to SHL content required for
 * the scanning process.
 *
 * SHLs, or Smart Health Links, are a standardized format for securely sharing health-related
 * information. For official documentation and specifications, see
 * [SHL Documentation](https://docs.smarthealthit.org/smart-health-links/).
 *
 * @property fullLink The full Smart Health Link (could include an optional SHL viewer).
 * @property encodedShlPayload The Base64Url-encoded SHL payload.
 * @property manifestUrl The URL to the SHL manifest.
 * @property key The key for decoding the data.
 * @property label A label describing the SHL data.
 * @property flags Flags indicating specific conditions or requirements (e.g., "P" for passcode).
 * @property expirationTime The expiration time of the SHL data.
 * @property versionNumber The version number of the SHL data.
 * @property ipsDoc The IPS document linked to by the SHL.
 */
data class SHLinkScanData(
  val fullLink: String,
  val shl: String,
  val manifestUrl: String,
  val key: String,
  val label: String,
  val flag: String,
  val exp: String,
  val v: String,
  val ipsDoc: IPSDocument,
)
