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

package com.google.android.fhir.r4.db.impl.dao

import androidx.room.Dao
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.db.ResourceNotFoundException
import com.google.android.fhir.db.impl.dao.BaseResourceDao
import com.google.android.fhir.db.impl.entities.DateIndexEntity
import com.google.android.fhir.db.impl.entities.DateTimeIndexEntity
import com.google.android.fhir.db.impl.entities.NumberIndexEntity
import com.google.android.fhir.db.impl.entities.PositionIndexEntity
import com.google.android.fhir.db.impl.entities.QuantityIndexEntity
import com.google.android.fhir.db.impl.entities.ReferenceIndexEntity
import com.google.android.fhir.db.impl.entities.ResourceEntity
import com.google.android.fhir.db.impl.entities.StringIndexEntity
import com.google.android.fhir.db.impl.entities.TokenIndexEntity
import com.google.android.fhir.db.impl.entities.UriIndexEntity
import com.google.android.fhir.index.entities.ResourceIndices
import com.google.android.fhir.r4.index.ResourceIndexer
import com.google.android.fhir.r4.lastUpdated
import com.google.android.fhir.r4.logicalId
import com.google.android.fhir.r4.versionId
import java.util.UUID
import org.hl7.fhir.r4.model.Resource

@Dao
internal abstract class ResourceDao : BaseResourceDao<Resource> {
  // this is ugly but there is no way to inject these right now in Room as it is the one creating
  // the dao
  override lateinit var iParser: IParser

  override suspend fun update(resource: Resource) {
    updateResource(
      resource.logicalId,
      resource.resourceType.name,
      iParser.encodeResourceToString(resource),
    )
    getResourceEntity(resource.logicalId, resource.resourceType.name)?.let {
      val entity =
        ResourceEntity(
          id = 0,
          resourceType = resource.resourceType.name,
          resourceUuid = it.resourceUuid,
          resourceId = resource.logicalId,
          serializedResource = iParser.encodeResourceToString(resource),
          versionId = it.versionId,
          lastUpdatedRemote = it.lastUpdatedRemote
        )
      val index = ResourceIndexer.index(resource)
      updateIndicesForResource(index, entity, it.resourceUuid)
    }
      ?: throw ResourceNotFoundException(resource.resourceType.name, resource.id)
  }

  override suspend fun insert(resource: Resource): String {
    return insertResource(resource)
  }

  override suspend fun insertAll(resources: List<Resource>): List<String> {
    return resources.map { resource -> insertResource(resource) }
  }

  private suspend fun insertResource(resource: Resource): String {
    val resourceUuid = UUID.randomUUID()

    // Use the local UUID as the logical ID of the resource
    if (resource.id.isNullOrEmpty()) {
      resource.id = resourceUuid.toString()
    }

    val entity =
      ResourceEntity(
        id = 0,
        resourceType = resource.resourceType.name,
        resourceUuid = resourceUuid,
        resourceId = resource.logicalId,
        serializedResource = iParser.encodeResourceToString(resource),
        versionId = resource.versionId,
        lastUpdatedRemote = resource.lastUpdated
      )
    insertResource(entity)
    val index = ResourceIndexer.index(resource)
    updateIndicesForResource(index, entity, resourceUuid)

    return resource.id
  }

  private suspend fun updateIndicesForResource(
    index: ResourceIndices,
    resource: ResourceEntity,
    resourceUuid: UUID
  ) {
    // TODO Move StringIndices to persistable types
    //  https://github.com/jingtang10/fhir-engine/issues/31
    //  we can either use room-autovalue integration or go w/ embedded data classes.
    //  we may also want to merge them:
    //  https://github.com/jingtang10/fhir-engine/issues/33
    index.stringIndices.forEach {
      insertStringIndex(
        StringIndexEntity(
          id = 0,
          resourceType = resource.resourceType,
          index = it,
          resourceUuid = resourceUuid,
        )
      )
    }
    index.referenceIndices.forEach {
      insertReferenceIndex(
        ReferenceIndexEntity(
          id = 0,
          resourceType = resource.resourceType,
          index = it,
          resourceUuid = resourceUuid,
        )
      )
    }
    index.tokenIndices.forEach {
      insertCodeIndex(
        TokenIndexEntity(
          id = 0,
          resourceType = resource.resourceType,
          index = it,
          resourceUuid = resourceUuid,
        )
      )
    }
    index.quantityIndices.forEach {
      insertQuantityIndex(
        QuantityIndexEntity(
          id = 0,
          resourceType = resource.resourceType,
          index = it,
          resourceUuid = resourceUuid,
        )
      )
    }
    index.uriIndices.forEach {
      insertUriIndex(
        UriIndexEntity(
          id = 0,
          resourceType = resource.resourceType,
          index = it,
          resourceUuid = resourceUuid,
        )
      )
    }
    index.dateIndices.forEach {
      insertDateIndex(
        DateIndexEntity(
          id = 0,
          resourceType = resource.resourceType,
          index = it,
          resourceUuid = resourceUuid,
        )
      )
    }
    index.dateTimeIndices.forEach {
      insertDateTimeIndex(
        DateTimeIndexEntity(
          id = 0,
          resourceType = resource.resourceType,
          index = it,
          resourceUuid = resourceUuid,
        )
      )
    }
    index.numberIndices.forEach {
      insertNumberIndex(
        NumberIndexEntity(
          id = 0,
          resourceType = resource.resourceType,
          index = it,
          resourceUuid = resourceUuid,
        )
      )
    }
    index.positionIndices.forEach {
      insertPositionIndex(
        PositionIndexEntity(
          id = 0,
          resourceType = resource.resourceType,
          index = it,
          resourceUuid = resourceUuid,
        )
      )
    }
  }
}
