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

import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.implementationguide.db.impl.dao.ImplementationGuideDao
import com.google.android.fhir.implementationguide.db.impl.entities.ResourceMetadataEntity
import java.io.FileInputStream
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.Library
import org.hl7.fhir.r4.model.ResourceType

/** Accessing resources from provided Implementation Guides. */
class IgResourceRetriever
internal constructor(
  private val igDao: ImplementationGuideDao,
  private val jsonParser: IParser,
  private val igIds: List<Long>,
) {

  suspend fun loadResourceByName(
    resourceType: String,
    name: String,
    version: String? = null,
  ): Library? {
    val resourceEntities =
      if (version != null) {
        igDao.getResourcesWithNameAndVersion(
          ResourceType.fromCode(resourceType),
          name,
          version,
          igIds
        )
      } else {
        igDao.getResourcesWithName(ResourceType.fromCode(resourceType), name, igIds)
      }

    return resourceEntities.firstOrNull()?.let { loadResource(it) } as Library?
  }

  suspend fun loadResources(
    resourceType: String,
  ): Iterable<IBaseResource> {
    val resourceEntities = igDao.getResources(ResourceType.fromCode(resourceType), igIds)
    return resourceEntities.map { loadResource(it) }
  }

  suspend fun loadResourcesByUrl(
    resourceType: String,
    url: String,
  ): Iterable<IBaseResource> {
    val resourceEntities =
      igDao.getResourcesWithUrl(ResourceType.fromCode(resourceType), url, igIds)
    return resourceEntities.map { loadResource(it) }
  }

  private fun loadResource(resourceEntity: ResourceMetadataEntity): IBaseResource {
    return jsonParser.parseResource(FileInputStream(resourceEntity.fileUri))
  }
}
