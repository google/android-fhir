package com.google.android.fhir.implementationguide.npm

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.Date
import org.hl7.fhir.utilities.TextFile
import org.hl7.fhir.utilities.Utilities
import org.hl7.fhir.utilities.npm.PackageClient
import org.hl7.fhir.utilities.npm.PackageInfo

/**
 * Implementation of a package client that keeps a local disk cache of downloaded artifacts
 * in order to avoid re-downloading things.
 *
 * This version is modified from the original to support injecting the folder path instead of hardcoded one
 */
internal class CachingPackageClient(cacheFolderRoot: String, address: String) :
  PackageClient(address) {
  private val cacheFolder = Utilities.path(cacheFolderRoot, ".fhir", "package-client")

  init {
    Utilities.createDirectory(cacheFolder)
  }

  @Throws(IOException::class)
  override fun exists(id: String, ver: String): Boolean {
    return getVersions(id).any { pi -> ver == pi.version }
  }

  @Throws(IOException::class)
  override fun fetchCached(url: String): InputStream {
    val cacheFile = File(Utilities.path(cacheFolder, fn(url)))
    if (cacheFile.exists()) {
      return FileInputStream(cacheFile)
    }
    val fetchedPackage = super.fetchCached(url)
    TextFile.bytesToFile(TextFile.streamToBytes(fetchedPackage), cacheFile)
    return FileInputStream(cacheFile)
  }

}