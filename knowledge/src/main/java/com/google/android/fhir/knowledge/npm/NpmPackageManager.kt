package com.google.android.fhir.knowledge.npm

import com.google.android.fhir.implementationguide.npm.CachingPackageClient
import com.google.android.fhir.knowledge.ImplementationGuide
import java.io.File
import org.hl7.fhir.utilities.npm.PackageServer

internal class NpmPackageManager(val cachingPackageClient: CachingPackageClient) {
  constructor(dataFolder: File) : this(
    CachingPackageClient(
      dataFolder.absolutePath,
      PackageServer.primaryServer()
    )
  )

  /** Installs the implementation guides and its dependencies and return the list of directory roots. */
  fun install(vararg implementationGuides: ImplementationGuide): List<File> {
      // collect dependencies
    // download all dependencies
    // collect root dirs
    TODO()
  }
}