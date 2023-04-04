/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.fhir.implementationguide.npm

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import org.hl7.fhir.utilities.TextFile
import org.hl7.fhir.utilities.Utilities
import org.hl7.fhir.utilities.npm.PackageClient
import org.hl7.fhir.utilities.npm.PackageServer

/**
 * Implementation of a package client that keeps a local disk cache of downloaded artifacts in order
 * to avoid re-downloading things.
 *
 * This version is modified from the original to support injecting the folder path instead of
 * hardcoded one
 */
internal class CachingPackageClient(cacheFolderRoot: String, server: PackageServer) :
  PackageClient(server) {
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
