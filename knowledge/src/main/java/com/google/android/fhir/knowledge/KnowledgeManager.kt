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

package com.google.android.fhir.knowledge

import android.content.Context
import androidx.room.Room
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.knowledge.db.impl.KnowledgeDatabase
import com.google.android.fhir.knowledge.db.impl.entities.ResourceMetadataEntity
import com.google.android.fhir.knowledge.db.impl.entities.toEntity
import java.io.File
import java.io.FileInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.MetadataResource
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import timber.log.Timber

/** Responsible for importing, accessing and deleting Implementation Guides. */
class KnowledgeManager
internal constructor(
  private val knowledgeDatabase: KnowledgeDatabase,
  private val jsonParser: IParser = FhirContext.forR4().newJsonParser(),
) {

  private val knowledgeDao = knowledgeDatabase.knowledgeDao()

  /**
   * * Checks if the [implementationGuides] are present in DB. If necessary, downloads the
   * dependencies from NPM and imports data from the package manager (populates the metadata of the
   * FHIR Resources)
   */
  suspend fun install(vararg implementationGuides: ImplementationGuide) {
    TODO("[1937]Not implemented yet ")
  }

  /**
   * Checks if the [implementationGuide] is present in DB. If necessary, populates the database with
   * the metadata of FHIR Resource from the provided [rootDirectory].
   */
  suspend fun install(implementationGuide: ImplementationGuide, rootDirectory: File) {
    // TODO(ktarasenko) copy files to the safe space?
    val igId = knowledgeDao.insert(implementationGuide.toEntity(rootDirectory))
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

  /** Imports the Knolwedge Artifact from the provided [file] to the default dependency. */
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
  suspend fun delete(vararg igDependencies: ImplementationGuide) {
    igDependencies.forEach { igDependency ->
      val igEntity =
        knowledgeDao.getImplementationGuide(igDependency.packageId, igDependency.version)
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
        file
      )
    knowledgeDao.insertResource(igId, res)
  }

  private fun loadResource(resourceEntity: ResourceMetadataEntity): IBaseResource {
    return jsonParser.parseResource(FileInputStream(resourceEntity.resourceFile))
  }

  fun close() {
    knowledgeDatabase.close()
  }

  companion object {
    private const val DB_NAME = "knowledge.db"

    /** Creates an [KnowledgeManager] backed by the Room DB. */
    fun create(context: Context) =
      KnowledgeManager(
        Room.databaseBuilder(context, KnowledgeDatabase::class.java, DB_NAME).build()
      )

    /** Creates an [KnowledgeManager] backed by the in-memory DB. */
    fun createInMemory(context: Context) =
      KnowledgeManager(Room.inMemoryDatabaseBuilder(context, KnowledgeDatabase::class.java).build())
  }
}
