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

package com.google.android.fhir.implementationguide

import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.implementationguide.db.impl.ImplementationGuideDatabase
import com.google.android.fhir.implementationguide.db.impl.entities.ResourceMetadataEntity
import com.google.android.fhir.implementationguide.db.impl.entities.toEntity
import java.io.File
import java.io.FileInputStream
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.MetadataResource
import org.hl7.fhir.r4.model.Resource
import timber.log.Timber

/** Responsible for downloading, accessing and deleting Implementation Guides. */
class IgManager internal constructor(igDatabase: ImplementationGuideDatabase) {

  private val igDao = igDatabase.implementationGuideDao()
  private val jsonParser = FhirContext.forR4().newJsonParser()

  /**
   * Ensures that IG dependencies from provided [IgContext] are downloaded, downloading if they are
   * missing.
   */
  suspend fun ensureDownload(igContext: IgContext): IgResourceRetriever {
    TODO("not implemented yet")
  }

  /** Deletes Implementation Guide, cleans up files. */
  suspend fun delete(igContext: IgContext) {
    igContext.dependencies.forEach { igDependency ->
      val igEntity = igDao.getImplementationGuide(igDependency.packageId, igDependency.version)
      igDao.deleteImplementationGuide(igEntity)
      igEntity.rootDirectory.deleteRecursively()
    }
  }

  /** An object for accessing Ig resources from provided dependencies. */
  internal suspend fun createResourceRetriever(igContext: IgContext): IgResourceRetriever {
    val igIds =
      igContext.dependencies
        .map { igDao.getImplementationGuide(it.packageId, it.version) }
        .map { it.id }
    return IgResourceRetriever(igDao, jsonParser, igIds)
  }

  /** Imports Implementation Guide from local files. */
  internal suspend fun import(
    igDependency: IgContext.Dependency,
    rootDirectory: File,
    files: Iterable<File>,
  ) {
    val igId = igDao.insert(igDependency.toEntity(rootDirectory))
    files.forEach { file ->
      try {
        when (val resource = jsonParser.parseResource(FileInputStream(file))) {
          is Bundle -> importBundle(igId, resource, file)
          is Resource -> importResource(igId, resource, file)
        }
      } catch (exception: Exception) {
        Timber.d(exception, "Unable to import file: %file")
      }
    }
  }

  private suspend fun importBundle(igId: Long, resource: Bundle, file: File) {
    // TODO: Multiple resources will point to the same file, and it needs to be accounted for while
    // reading it.
    // for (entry in resource.entry) {
    //   when (val bundleResource = entry.resource) {
    //     is Resource -> importResource(igId, bundleResource, fileUri)
    //     else -> println("Can't import resource ${entry.resource.resourceType}")
    //   }
    // }
  }

  private suspend fun importResource(igId: Long, resource: Resource, file: File) {
    val metadataResource = resource as? MetadataResource
    val res =
      ResourceMetadataEntity(
        0L,
        resource.resourceType,
        resource.id,
        metadataResource?.url,
        metadataResource?.name,
        metadataResource?.version,
        file,
        igId
      )
    igDao.insert(res)
  }
}
