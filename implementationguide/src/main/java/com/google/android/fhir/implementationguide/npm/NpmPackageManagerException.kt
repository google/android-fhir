package com.google.android.fhir.implementationguide.npm

/**
 * This exception is thrown whenever there is an issue with the [NpmPackageManager].
 */
class NpmPackageManagerException @JvmOverloads constructor(message: String? = null,  cause: Throwable? = null) : RuntimeException(message, cause) {
  companion object {
    const val serialVersionUID = 1L
  }
}