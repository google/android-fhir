/*
 * Copyright 2023 Google LLC
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
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.knowledge.db.KnowledgeDatabase
import com.google.android.fhir.knowledge.db.entities.ResourceMetadataEntity
import com.google.android.fhir.knowledge.db.entities.toEntity
import com.google.android.fhir.knowledge.files.NpmFileManager
import com.google.android.fhir.knowledge.npm.NpmPackageDownloader
import com.google.android.fhir.knowledge.npm.OkHttpNpmPackageDownloader
import java.io.File
import java.io.FileInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.MetadataResource
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import timber.log.Timber

/**
 * Manages knowledge artifacts on the Android device. Knowledge artifacts can be installed
 * individually as JSON files or from FHIR NPM packages.
 *
 * Coordinates the management of knowledge artifacts by using the three following components:
 * - database: indexing knowledge artifacts stored in the local file system,
 * - file manager: managing files containing the knowledge artifacts, and
 * - NPM downloader: downloading from an NPM package server the knowledge artifacts.
 */
class KnowledgeManager
internal constructor(
  knowledgeDatabase: KnowledgeDatabase,
  private val npmFileManager: NpmFileManager,
  private val npmPackageDownloader: NpmPackageDownloader,
  private val jsonParser: IParser = FhirContext.forR4().newJsonParser(),
) {
  private val knowledgeDao = knowledgeDatabase.knowledgeDao()

  /**
   * Checks if the [fhirNpmPackages] are present in DB. If necessary, downloads the dependencies
   * from NPM and imports data from the package manager (populates the metadata of the FHIR
   * Resources).
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
          install(it, localFhirNpmPackageMetadata.rootDirectory)
          install(*localFhirNpmPackageMetadata.dependencies.toTypedArray())
        } catch (e: Exception) {
          Timber.w("Unable to install package ${it.name} ${it.version}")
        }
      }
  }

  /**
   * Checks if the [fhirNpmPackage] is present in DB. If necessary, populates the database with the
   * metadata of FHIR Resource from the provided [rootDirectory].
   */
  suspend fun install(fhirNpmPackage: FhirNpmPackage, rootDirectory: File) {
    // TODO(ktarasenko) copy files to the safe space?
    val igId = knowledgeDao.insert(fhirNpmPackage.toEntity(rootDirectory))
    rootDirectory.listFiles()?.forEach { file ->
      try {
        val resource = jsonParser.parseResource(FileInputStream(file))
        if (resource is Resource) {
          importResource(igId, resource, file)
        } else {
          Timber.d("Unable to import file: %file")
        }
      } catch (exception: Exception) {
        Timber.d(exception, "Unable to import file: %file")
      }
    }
  }

  /** Imports the Knowledge Artifact from the provided [file] to the default dependency. */
  suspend fun install(file: File) {
    importFile(null, file)
  }

  /** Loads resources from IGs listed in dependencies. */
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
          listOfNotNull(knowledgeDao.getResourceWithUrlAndVersion(url, version))
        url != null -> listOfNotNull(knowledgeDao.getResourceWithUrl(url))
        id != null -> listOfNotNull(knowledgeDao.getResourceWithUrlLike("%$id"))
        name != null && version != null ->
          listOfNotNull(knowledgeDao.getResourcesWithNameAndVersion(resType, name, version))
        name != null -> knowledgeDao.getResourcesWithName(resType, name)
        else -> knowledgeDao.getResources(resType)
      }
    return resourceEntities.map { loadResource(it) }
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

  private suspend fun importFile(igId: Long?, file: File) {
    val resource =
      withContext(Dispatchers.IO) {
        try {
          FileInputStream(file).use(jsonParser::parseResource)
        } catch (exception: Exception) {
          Timber.d(exception, "Unable to import file: $file. Parsing to FhirResource failed.")
        }
      }
    when (resource) {
      is Resource -> importResource(igId, resource, file)
    }
  }

  private suspend fun importResource(igId: Long?, resource: Resource, file: File) {
    val metadataResource = resource as? MetadataResource
    val res =
      ResourceMetadataEntity(
        0L,
        resource.resourceType,
        metadataResource?.url,
        metadataResource?.name,
        metadataResource?.version,
        file,
      )
    knowledgeDao.insertResource(igId, res)
  }

  private fun loadResource(resourceEntity: ResourceMetadataEntity): IBaseResource {
    return jsonParser.parseResource(FileInputStream(resourceEntity.resourceFile))
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
