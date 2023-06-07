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

package com.google.android.fhir.knowledge.npm

import com.google.android.fhir.knowledge.ImplementationGuide
import java.io.File
import java.io.IOException

/** Caching and downloading packages */
internal class NpmPackageManager
constructor(
  private val cacheManager: CacheManager,
  private val packageDownloader: PackageDownloader,
) {

  /**
   * Downloads the implementation guides and its dependencies and return the list of directory
   * roots.
   */
  @Throws(IOException::class)
  suspend fun install(implementationGuide: ImplementationGuide): List<NpmPackage> {
    val containsPackage =
      cacheManager.containsPackage(implementationGuide.packageId, implementationGuide.version)
    if (containsPackage) {
      return listOf(
        cacheManager.getPackage(implementationGuide.packageId, implementationGuide.version)
      )
    }

    val npmPackage = packageDownloader.downloadPackage(implementationGuide, PACKAGE_SERVER)
    return listOf(npmPackage) +
      npmPackage.dependencies.flatMap { dependency -> install(dependency) }
  }

  companion object {

    private const val PACKAGE_SERVER = "https://packages.fhir.org/packages/"
    fun create(rootCacheFolder: File): NpmPackageManager {
      val cacheManager = CacheManager(File(rootCacheFolder, ".fhir_package_cache"))
      val packageDownloader = OkHttpPackageDownloader(cacheManager)
      return NpmPackageManager(cacheManager, packageDownloader)
    }
  }
}
