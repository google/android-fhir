package com.google.android.fhir.datacapture.mapping

/** Thrown to indicate that the FHIR core package could not be initialized successfully */
class NpmPackageInitializationError : RuntimeException {

  constructor(cause: Throwable?) : super(cause)

  constructor(message: String, cause: Throwable?) : super(message, cause)

  constructor(message: String) : super(message)
}
