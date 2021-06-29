package com.google.android.fhir.datacapture.mapping

import android.content.Context
import org.hl7.fhir.utilities.npm.FilesystemPackageCacheManager
import org.hl7.fhir.utilities.npm.NpmPackage
import org.hl7.fhir.utilities.npm.ToolsVersion
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

/**
 * Shadow of [ResourceMapper] class implementing original NPM package management for running on
 * local JVM
 */
@Implements(ResourceMapper::class)
class ResourceMapperTestVersion {

  @Implementation
  fun loadNpmPackage(context: Context): NpmPackage {
    // Package name manually checked from
    // https://simplifier.net/packages/hl7.fhir.r4.core/4.0.1
    val npmPackage =
      FilesystemPackageCacheManager(true, ToolsVersion.TOOLS_VERSION)
        .loadPackage("hl7.fhir.r4.core", "4.0.1")

    ResourceMapper.npmPackage = npmPackage

    return ResourceMapper.npmPackage
  }
}
