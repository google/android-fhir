/*
 * Copyright 2020 Google LLC
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

package com.google.android.fhir.datacapture.utilities

import android.content.Context
import android.os.Environment
import android.util.Log
import com.google.android.fhir.datacapture.mapping.NpmPackageInitializationError
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import org.apache.commons.compress.utils.IOUtils
import org.hl7.fhir.utilities.npm.NpmPackage

/**
 * Manages extracting the fhir core package into app storage and loading it into memory. Extracting
 * the fhir core package should preferably be done after installation since it is a long running
 * operation that takes 1-2 minutes. Loading the package into memory also takes considerable time
 * (20 seconds to 1 minute) and should be preferably done at application start.
 *
 * However, both operations will be done automatically where required during StructureMap-based
 * extraction.
 */
object NpmPackageProvider {

  private const val FHIR_R4_CORE_PACKAGE_FILENAME = "packages.fhir.org-hl7.fhir.r4.core-4.0.1.tgz"

  /**
   *
   * NpmPackage containing all the [org.hl7.fhir.r4.model.StructureDefinition]s takes around 20
   * seconds to load. Therefore, reloading for each extraction is not desirable. This should happen
   * once and cache the variable throughout the app's lifecycle.
   *
   * Call [loadNpmPackage] to load it. The method handles skips the operation if it's already
   * loaded.
   */
  private lateinit var npmPackage: NpmPackage

  /**
   * Decompresses the hl7.fhir.r4.core archived package into app storage and loads it into memory.
   * It loads the package into [npmPackage]. The method skips any unnecessary operations. This
   * method can be called during initial app installation and run in the background so as to reduce
   * the time it takes for the whole process.
   *
   * The whole process can take 1-2 minutes on a clean installation.
   */
  suspend fun loadNpmPackage(context: Context): NpmPackage {
    setupNpmPackage(context)

    if (!this::npmPackage.isInitialized) {
      npmPackage = NpmPackage.fromFolder(getLocalFhirCorePackageDirectory(context))
    }

    return npmPackage
  }

  /**
   * Decompresses the hl7.fhir.r4.core archived package into app storage.
   *
   * The whole process can take 1-2 minutes.
   *
   * @Throws NpmPackageInitializationError
   */
  private suspend fun setupNpmPackage(context: Context) {
    val outDir = getLocalFhirCorePackageDirectory(context)

    if (File(outDir + "/package/package.json").exists()) {
      return
    }
    // Create any missing folders
    File(outDir).mkdirs()

    // Copy the tgz package to private app storage
    var inputStream: InputStream? = null
    var outputStream: FileOutputStream? = null
    try {
      inputStream = context.assets.open(FHIR_R4_CORE_PACKAGE_FILENAME)
      outputStream =
        FileOutputStream(
          File(getLocalNpmPackagesDirectory(context) + FHIR_R4_CORE_PACKAGE_FILENAME)
        )

      IOUtils.copy(inputStream, outputStream)
    } catch (e: IOException) {
      // Delete the folders
      try {
        val packageDirectory = File(outDir)
        if (packageDirectory.exists()) {
          packageDirectory.delete()
        }
      } catch (securityException: SecurityException) {
        Log.e(NpmPackageProvider.javaClass.name, securityException.stackTraceToString())
      }

      throw NpmPackageInitializationError(
        "Could not copy archived package [$FHIR_R4_CORE_PACKAGE_FILENAME] to app private storage",
        e
      )
    } finally {
      IOUtils.closeQuietly(inputStream)
      IOUtils.closeQuietly(outputStream)
    }

    // decompress the .tgz package
    TarGzipUtility.decompress(
      getLocalNpmPackagesDirectory(context) + FHIR_R4_CORE_PACKAGE_FILENAME,
      File(outDir)
    )
  }

  /** Generates the path to the local npm packages directory. */
  private fun getLocalNpmPackagesDirectory(context: Context): String =
    Environment.getDataDirectory().getAbsolutePath() +
      "/data/${context.applicationContext.packageName}/npm_packages/"

  /** Generates the path to the local hl7.fhir.r4.core package. */
  private fun getLocalFhirCorePackageDirectory(context: Context): String {
    return getLocalNpmPackagesDirectory(context) + "hl7.fhir.r4.core#4.0.1"
  }
}
