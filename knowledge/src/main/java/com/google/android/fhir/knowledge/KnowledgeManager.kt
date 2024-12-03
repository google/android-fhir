/*
 * Copyright 2023-2024 Google LLC
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

package com.google.android.fhir.knowledge

import android.content.Context
import androidx.room.Room
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.knowledge.db.KnowledgeDatabase
import com.google.android.fhir.knowledge.db.entities.ImplementationGuideEntity
import com.google.android.fhir.knowledge.db.entities.ResourceMetadataEntity
import com.google.android.fhir.knowledge.files.NpmFileManager
import com.google.android.fhir.knowledge.npm.NpmPackageDownloader
import com.google.android.fhir.knowledge.npm.OkHttpNpmPackageDownloader
import java.io.File
import java.io.FileInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.context.IWorkerContext
import org.hl7.fhir.r4.context.SimpleWorkerContext
import org.hl7.fhir.r4.model.MetadataResource
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.utilities.npm.NpmPackage
import timber.log.Timber

/**
 * Manages knowledge artifacts on the Android device. Knowledge artifacts can be installed
 * individually as JSON files or from FHIR NPM packages.
 *
 * Coordinates the management of knowledge artifacts by using the three following components:
 * - knowledgeDatabase: indexing knowledge artifacts stored in the local file system,
 * - npmFileManager: managing files containing the knowledge artifacts, and
 * - npmPackageDownloader: downloading the knowledge artifacts from an NPM package server .
 *
 * Knowledge artifacts are scoped by the application. Multiple applications using the knowledge
 * manager will not share the same sets of knowledge artifacts.
 *
 * See [Clinical Reasoning](https://hl7.org/fhir/R4/clinicalreasoning-module.html) for the formal
 * definition of knowledge artifacts. In this implementation, however, knowledge artifacts are
 * represented as [MetadataResource]s.
 *
 * **Note** that the list of resources implementing the [MetadataResource] class differs from the
 * list of resources implementing the
 * [MetadataResource interface](https://www.hl7.org/fhir/R5/metadataresource.html) in FHIR R5.
 */
class KnowledgeManager
internal constructor(
  knowledgeDatabase: KnowledgeDatabase,
  private val npmFileManager: NpmFileManager,
  private val npmPackageDownloader: NpmPackageDownloader,
) {
  private val knowledgeDao = knowledgeDatabase.knowledgeDao()

  /**
   * Downloads and installs the [fhirNpmPackages] from the NPM package server with transitive
   * dependencies. The NPM packages will be unzipped to a directory managed by the knowledge
   * manager. The resources will be indexed in the database for future retrieval.
   *
   * FHIR NPM packages already present in the database will be skipped.
   */
  suspend fun install(vararg fhirNpmPackages: FhirNpmPackage) {
    fhirNpmPackages
      .filter { knowledgeDao.getImplementationGuide(it.name, it.version) == null }
      .forEach {
        try {
          if (!npmFileManager.containsPackage(it.name, it.version)) {
            npmPackageDownloader.downloadPackage(
              it,
              npmFileManager.getPackageDir(it.name, it.version),
            )
          }
        } catch (e: Exception) {
          Timber.w("Unable to download package ${it.name} ${it.version}")
        }
        try {
          val localFhirNpmPackageMetadata =
            npmFileManager.getLocalFhirNpmPackageMetadata(it.name, it.version)
          import(it, localFhirNpmPackageMetadata.rootDirectory)
          install(*localFhirNpmPackageMetadata.dependencies.toTypedArray())
        } catch (e: Exception) {
          Timber.w("Unable to install package ${it.name} ${it.version}")
        }
      }
  }

  /**
   * Imports the content of the [fhirNpmPackage] from the provided [rootDirectory] by indexing the
   * metadata of the FHIR resources for future retrieval.
   *
   * FHIR NPM packages already present in the database will be skipped.
   */
  suspend fun import(fhirNpmPackage: FhirNpmPackage, rootDirectory: File) {
    // TODO(ktarasenko) copy files to the safe space?
    val implementationGuideId =
      knowledgeDao.insert(
        ImplementationGuideEntity(
          0L,
          fhirNpmPackage.canonical ?: "",
          fhirNpmPackage.name,
          fhirNpmPackage.version,
          rootDirectory,
        ),
      )
    val files = rootDirectory.listFiles() ?: return
    files.sorted().forEach { file ->
      // Ignore files that are not meta resources instead of throwing exceptions since unzipped
      // NPM package might contain other types of files e.g. package.json.
      val resource = readMetadataResourceOrNull(file) ?: return@forEach
      knowledgeDao.insertResource(
        implementationGuideId,
        ResourceMetadataEntity(
          0,
          resource.resourceType,
          resource.url,
          resource.name,
          resource.version,
          file,
        ),
      )
    }
  }

  /**
   * Indexes a knowledge artifact as a JSON object in the provided [file].
   *
   * This creates a record of the knowledge artifact's metadata and the file's location. When the
   * knowledge artifact is requested, knowledge manager will load the content of the file,
   * deserialize it and return the resulting FHIR resource.
   *
   * This operation does not make a copy of the knowledge artifact, nor does it checksum the content
   * of the file. Therefore, it cannot be guaranteed that subsequent retrievals of the knowledge
   * artifact will produce the same result. Applications using this function must be aware of the
   * risk of the content of the file being modified or corrupt, potentially resulting in incorrect
   * or inaccurate result of decision support or measure evaluation.
   *
   * Use this API for knowledge artifacts in immutable files (e.g. in the app's `assets` folder).
   */
  suspend fun index(file: File) {
    val resource = readMetadataResourceOrThrow(file)
    knowledgeDao.insertResource(
      null,
      ResourceMetadataEntity(
        0L,
        resource.resourceType,
        resource.url,
        resource.name,
        resource.version,
        file,
      ),
    )
  }

  /** Loads resources from IGs listed in dependencies. */
  @Deprecated("Load resources using URLs only")
  suspend fun loadResources(
    resourceType: String,
    url: String? = null,
    id: String? = null,
    name: String? = null,
    version: String? = null,
  ): Iterable<IBaseResource> {
    val resType = ResourceType.fromCode(resourceType)

    val resourceEntities =
      when {
        url != null && version != null ->
          listOfNotNull(knowledgeDao.getResourceWithUrlAndVersion(resType, url, version))
        url != null -> listOfNotNull(knowledgeDao.getResourceWithUrl(resType, url))
        id != null -> listOfNotNull(knowledgeDao.getResourcesWithId(id.toLong()))
        name != null && version != null ->
          listOfNotNull(knowledgeDao.getResourcesWithNameAndVersion(resType, name, version))
        name != null -> knowledgeDao.getResourcesWithName(resType, name)
        else -> knowledgeDao.getResources(resType)
      }
    return resourceEntities.map { readMetadataResourceOrThrow(it.resourceFile)!! }
  }

  /**
   * Loads knowledge artifact by its canonical URL and an optional version.
   *
   * The version can either be passed as a parameter or as part of the URL (using pipe `|` to
   * separate the URL and the version). For example, passing the URL
   * `http://abc.xyz/fhir/Library|1.0.0` with no version is the same as passing the URL
   * `http://abc.xyz/fhir/Library` and version `1.0.0`.
   *
   * However, if a version is specified both as a parameter and as part of the URL, the two must
   * match.
   *
   * @throws IllegalArgumentException if the url contains more than one pipe `|`
   * @throws IllegalArgumentException if the version specified in the URL and the explicit version
   *   do not match
   */
  suspend fun loadResources(
    url: String,
    version: String? = null,
  ): Iterable<IBaseResource> {
    val (canonicalUrl, canonicalVersion) = canonicalizeUrlAndVersion(url, version ?: "")

    val resourceEntities =
      if (canonicalVersion == "") {
        knowledgeDao.getResource(canonicalUrl)
      } else {
        listOfNotNull(knowledgeDao.getResource(canonicalUrl, canonicalVersion))
      }
    return resourceEntities.map { readMetadataResourceOrThrow(it.resourceFile) }
  }

  /**
   * Canonicalizes the URL and version. It will extract the version as part of the URL separated by
   * pipe `|`.
   *
   * For example, URL `http://abc.xyz/fhir/Library|1.0.0` will be canonicalized as URL
   * `http://abc.xyz/fhir/Library` and version `1.0.0`.
   *
   * @throws IllegalArgumentException if the URL contains more than one pipe
   * @throws IllegalArgumentException if the version specified in the URL and the explicit version
   *   do not match
   */
  private fun canonicalizeUrlAndVersion(
    url: String,
    version: String,
  ): Pair<String, String> {
    if (!url.contains('|')) {
      return Pair(url, version)
    }

    val parts = url.split('|')
    require(parts.size == 2) { "URL $url contains too many parts separated by \"|\"" }

    // If an explicit version is specified, it must match the one in the URL
    require(version == "" || version == parts[1]) {
      "Version specified in the URL $parts[1] and explicit version $version do not match"
    }
    return Pair(parts[0], parts[1])
  }

  /** Deletes Implementation Guide, cleans up files. */
  suspend fun delete(vararg igDependencies: FhirNpmPackage) {
    igDependencies.forEach { igDependency ->
      val igEntity = knowledgeDao.getImplementationGuide(igDependency.name, igDependency.version)
      if (igEntity != null) {
        knowledgeDao.deleteImplementationGuide(igEntity)
        igEntity.rootDirectory.deleteRecursively()
      }
    }
  }

  /**
   * Loads and initializes a worker context with the specified npm packages.
   *
   * This
   * [sample](https://github.com/google/android-fhir/blob/385dc32f2706ad5520c121bf39478a105a5d46eb/datacapture/src/test/java/com/google/android/fhir/datacapture/mapping/ResourceMapperTest.kt#L3065)
   * test demonstrates how to use `loadWorkerContext` API.
   *
   * @param npmPackages The npm packages to be loaded into the worker context.
   * @param allowLoadingDuplicates Flag indicating whether loading duplicate packages is allowed.
   *   Default is true.
   * @param loader Custom resource loader for the worker context. Default is null, meaning the
   *   default loader will be used.
   * @return An initialized instance of [IWorkerContext].
   */
  suspend fun loadWorkerContext(
    vararg npmPackages: NpmPackage,
    allowLoadingDuplicates: Boolean = true,
    loader: SimpleWorkerContext.IContextResourceLoader? = null,
  ): IWorkerContext {
    return withContext(Dispatchers.IO) {
      val simpleWorkerContext = SimpleWorkerContext()
      simpleWorkerContext.apply {
        isAllowLoadingDuplicates = allowLoadingDuplicates
        npmPackages.forEach { npmPackage -> loadFromPackage(npmPackage, loader) }
      }
    }
  }

  /**
   * Parses and returns the content of a file containing a FHIR resource in JSON, or null if the
   * file does not contain a FHIR resource.
   */
  private suspend fun readResourceOrNull(file: File): IBaseResource? =
    withContext(Dispatchers.IO) {
      try {
        FileInputStream(file)
          .use(FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()::parseResource)
      } catch (e: Exception) {
        Timber.e(e, "Unable to load resource from $file")
        null
      }
    }

  /**
   * Parses and returns the content of a file containing a FHIR metadata resource in JSON, or null
   * if the file does not contain a FHIR metadata resource.
   */
  private suspend fun readMetadataResourceOrNull(file: File) =
    readResourceOrNull(file) as? MetadataResource

  /**
   * Parses and returns the content of a file containing a FHIR metadata resource in JSON, or throws
   * an exception if the file does not contain a FHIR metadata resource.
   */
  private suspend fun readMetadataResourceOrThrow(file: File): MetadataResource {
    val resource = readResourceOrNull(file)!!
    check(resource is MetadataResource) {
      "Resource ${resource.idElement} is not a MetadataResource"
    }
    return resource
  }

  companion object {
    private const val DB_NAME = "knowledge.db"
    private const val DOWNLOADED_DATA_SUB_DIR = ".fhir_package_cache"
    private const val DEFAULT_PACKAGE_SERVER = "https://packages.fhir.org/packages/"

    /**
     * Creates a [KnowledgeManager] instance.
     *
     * @param context the application context
     * @param inMemory whether the knowledge manager instance is in-memory or backed by a Room
     *   database
     * @param downloadedNpmDir the directory to store downloaded NPM packages
     * @param packageServer the package server to download FHIR NPM packages from. Defaulted to
     *   https://packages.fhir.org/packages/.
     */
    fun create(
      context: Context,
      inMemory: Boolean = false,
      downloadedNpmDir: File? = context.dataDir,
      packageServer: String? = DEFAULT_PACKAGE_SERVER,
    ) =
      KnowledgeManager(
        if (inMemory) {
          Room.inMemoryDatabaseBuilder(context, KnowledgeDatabase::class.java).build()
        } else {
          Room.databaseBuilder(context, KnowledgeDatabase::class.java, DB_NAME).build()
        },
        NpmFileManager(File(downloadedNpmDir, DOWNLOADED_DATA_SUB_DIR)),
        OkHttpNpmPackageDownloader(packageServer!!),
      )
  }
}
