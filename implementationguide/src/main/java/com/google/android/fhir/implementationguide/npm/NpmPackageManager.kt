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

import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.function.Function
import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_40_50
import org.hl7.fhir.convertors.conv40_50.VersionConvertor_40_50
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r5.context.IWorkerContext
import org.hl7.fhir.r5.model.ImplementationGuide
import org.hl7.fhir.utilities.Utilities
import org.hl7.fhir.utilities.npm.BasePackageCacheManager
import org.hl7.fhir.utilities.npm.NpmPackage
import org.hl7.fhir.utilities.npm.PackageClient
import org.hl7.fhir.utilities.npm.PackageServer
import org.json.JSONObject
import timber.log.Timber

/**
 * The copy of
 * [Cqf
 * NpmPackageManager](https://github.com/cqframework/clinical_quality_language/blob/master/Src/java/cqf-fhir-npm/src/main/java/org/cqframework/fhir/npm/NpmPackageManager.java)
 * Fixes the nested dependency issue, uses android-friendly loggers and CacheManagers.
 */
class NpmPackageManager(
  cacheFolder: String,
  sourceIg: ImplementationGuide,
  clientFactory:Function<PackageServer, PackageClient> =  Function { address: PackageServer ->
    CachingPackageClient(
      cacheFolder,
      address
    )
  },
  vararg packageServers: String,
) : IWorkerContext.ILoggingService {
  private val pcm: BasePackageCacheManager
  val npmList = mutableListOf<NpmPackage>()

  init {
    pcm =
      try {
        SimplePackageCacheManager(cacheFolder, clientFactory, *packageServers)
      } catch (e: IOException) {
        val message = "error creating the SimplePackageCacheManager"
        logMessage(message)
        throw NpmPackageManagerException(message, e)
      }
    sourceIg.dependsOn.forEach(::loadIg)
  }

  @Throws(IOException::class)
  private fun loadIg(dep: ImplementationGuide.ImplementationGuideDependsOnComponent) {
    val name =
      if (!dep.hasId()) {
        logMessage(
          "Dependency '${idForDep(dep)}' has no id, so can't be referred to in markdown in the IG"
        )
        "u${Utilities.makeUuidLC().replace("-", "")}"
      } else {
        dep.id
      }
    require(isValidIGToken(name)) { "IG Name must be a valid token ($name)" }
    var canonical = determineCanonical(dep.uri)
    var packageId = dep.packageId
    if (packageId.isNullOrEmpty()) {
      packageId = pcm.getPackageId(canonical)
    }
    if (canonical.isNullOrEmpty() && packageId.isNotEmpty()) {
      canonical = pcm.getPackageUrl(packageId)
    }
    require(!canonical.isNullOrEmpty()) { "You must specify a canonical URL for the IG $name" }
    val igver = dep.version
    require(!igver.isNullOrEmpty()) {
      "You must specify a version for the IG $packageId ($canonical)"
    }
    var pi = if (packageId == null) null else pcm.loadPackageFromCacheOnly(packageId, igver)
    if (pi != null) {
      npmList.add(pi)
    }
    if (pi == null) {
      pi = resolveDependency(canonical, packageId, igver)
      if (pi == null) {
        require(!packageId.isNullOrEmpty()) {
          ("Package Id for guide at $canonical is unknown (contact FHIR Product Director")
        }
        throw IllegalArgumentException("Unknown Package $packageId#$igver")
      }
      npmList.add(pi)
    }
    logDebugMessage(
      IWorkerContext.ILoggingService.LogCategory.INIT,
      "Load $name ($canonical) from $packageId#$igver"
    )
    if (dep.hasUri() && !dep.uri.contains("/ImplementationGuide/")) {
      val cu = getIgUri(pi)
      if (cu != null) {
        logMessage("The correct canonical URL for this dependency is $cu")
      }
    }
  }

  private fun determineCanonical(url: String?): String? {
    if (url == null) {
      return null
    }
    if (url.contains("/ImplementationGuide/")) {
      return url.substring(0, url.indexOf("/ImplementationGuide/"))
    }
    logMessage(
      "The canonical URL for an Implementation Guide must point directly to the implementation guide resource, not to the Implementation Guide as a whole"
    )
    return url
  }

  private fun isValidIGToken(tail: String?): Boolean {
    if (tail.isNullOrEmpty()) {
      return false
    }
    return tail.drop(1).fold(tail[0].isLetter()) { acc, tailChar ->
      acc && (tailChar.isLetterOrDigit() || tailChar == '_')
    }
  }

  private fun idForDep(dep: ImplementationGuide.ImplementationGuideDependsOnComponent): String {
    return when {
      dep.hasPackageId() -> dep.packageId
      dep.hasUri() -> dep.uri
      else -> "{no id}"
    }
  }

  @Throws(IOException::class)
  private fun getIgUri(pi: NpmPackage): String? {
    for (rs in pi.listResources("ImplementationGuide")) {
      val json = parseJson(pi.loadResource(rs))
      if (json.optString("packageId") == pi.name()) {
        return json.optString("url")
      }
    }
    return null
  }

  @Throws(IOException::class)
  private fun resolveDependency(
    canonical: String?,
    packageId: String?,
    igver: String,
  ): NpmPackage? {
    if (packageId != null) {
      return pcm.loadPackage(packageId, igver)
    }
    logDebugMessage(
      IWorkerContext.ILoggingService.LogCategory.INIT,
      "Fetch Package history from " + Utilities.pathURL(canonical, "package-list.json")
    )
    val pl: JSONObject =
      try {
        fetchJson(Utilities.pathURL(canonical, "package-list.json"))
      } catch (e: Exception) {
        Timber.e(e, "Error parsing package-list.json")
        return null
      }
    require(canonical == pl.getString("canonical")) {
      ("Canonical mismatch fetching package list for $canonical #$igver, package-list.json says ${pl["canonical"]}")
    }
    val array = pl.getJSONArray("list")
    for (i in 0..array.length()) {
      val o = array[i] as JSONObject
      if (igver == o.getString("version")) {
        val src =
          fetchFromSource(
            "${pl.getString("package-id")}-$igver",
            Utilities.pathURL(o.getString("path"), "package.tgz")
          )
        return pcm.addPackageToCache(
          pl.getString("package-id"),
          igver,
          src,
          Utilities.pathURL(o.getString("path"), "package.tgz")
        )
      }
    }
    return null
  }

  @Throws(IOException::class)
  private fun fetchJson(source: String): JSONObject {
    val url = URL(source + "?nocache=" + System.currentTimeMillis())
    val c = url.openConnection() as HttpURLConnection
    c.instanceFollowRedirects = true
    return parseJson(c.inputStream)
  }

  @Throws(IOException::class)
  private fun parseJson(inputStream: InputStream): JSONObject {
    return JSONObject(InputStreamReader(inputStream).readText())
  }

  @Throws(IOException::class)
  private fun fetchFromSource(id: String, source: String): InputStream {
    logDebugMessage(
      IWorkerContext.ILoggingService.LogCategory.INIT,
      "Fetch $id package from $source"
    )
    val url = URL(source + "?nocache=" + System.currentTimeMillis())
    val c = url.openConnection()
    return c.getInputStream()
  }

  override fun logMessage(msg: String) {
    Timber.d(msg)
  }

  override fun logDebugMessage(category: IWorkerContext.ILoggingService.LogCategory, msg: String) {
    logMessage(msg)
  }

  override fun isDebugLogging(): Boolean = false

  companion object {

    @Throws(IOException::class)
    fun fromResource(
      cacheFolder: String,
      resource: Resource,
      vararg packageServers: String,
    ): NpmPackageManager {
      val versionConvertor40to50 = VersionConvertor_40_50(BaseAdvisor_40_50())
      return NpmPackageManager(
        cacheFolder = cacheFolder,
        sourceIg = versionConvertor40to50.convertResource(resource) as ImplementationGuide,
        packageServers = packageServers
      )
    }
  }
}
