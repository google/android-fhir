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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/** Manages stored NPM packages. */
internal class CacheManager(private val cacheRoot: File) {

  /**
   * Returns the NpmPackage for the given [packageId] and [version] from cache or `null` if the
   * package is not cached.
   */
  suspend fun getPackage(packageId: String, version: String): NpmPackage {
    return withContext(Dispatchers.IO) {
      val packageFolder = File(getPackageFolder(packageId, version), "package")
      readNpmPackage(packageFolder)
    }
  }

  /**
   * Returns the NpmPackage for the given [packageId] and [version] from cache or `null` if the
   * package is not cached.
   */
  suspend fun containsPackage(packageId: String, version: String): Boolean {
    return withContext(Dispatchers.IO) {
      val packageFolder = File(getPackageFolder(packageId, version), "package")
      val packageJson = File(packageFolder, "package.json")
      packageJson.exists()
    }
  }

  /** Returns the package folder for the given [packageId] and [version]. */
  fun getPackageFolder(packageId: String, version: String) = File(cacheRoot, "$packageId#$version")

  /** Creates an [NpmPackage] parsing the package manifest file. */
  private fun readNpmPackage(packageFolder: File): NpmPackage {
    val packageJson = File(packageFolder, "package.json")
    val json = JSONObject(packageJson.readText())
    with(json) {
      val dependenciesList = optJSONObject("dependencies")
      val dependencies =
        dependenciesList?.keys()?.asSequence()?.map { key ->
          ImplementationGuide(key, dependenciesList.getString(key))
        }

      return NpmPackage(
        getString("name"),
        getString("version"),
        optString("canonical"),
        dependencies?.toList() ?: emptyList(),
        packageFolder
      )
    }
  }
}
