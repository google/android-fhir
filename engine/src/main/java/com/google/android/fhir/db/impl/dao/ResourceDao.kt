/*
 * Copyright 2021 Google LLC
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

package com.google.android.fhir.db.impl.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.db.ResourceNotFoundException
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
import com.google.android.fhir.index.ResourceIndexer
import com.google.android.fhir.index.ResourceIndices
import com.google.android.fhir.lastUpdated
import com.google.android.fhir.logicalId
import com.google.android.fhir.versionId
import java.time.Instant
import java.util.UUID
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

@Dao
internal abstract class ResourceDao {
  // this is ugly but there is no way to inject these right now in Room as it is the one creating
  // the dao
  lateinit var iParser: IParser

  open suspend fun update(resource: Resource) {
    updateResource(
      resource.logicalId,
      resource.resourceType,
      iParser.encodeResourceToString(resource),
    )
    getResourceEntity(resource.logicalId, resource.resourceType)?.let {
      val entity =
        ResourceEntity(
          id = 0,
          resourceType = resource.resourceType,
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

  open suspend fun insert(resource: Resource): String {
    return insertResource(resource)
  }

  open suspend fun insertAll(resources: List<Resource>): List<String> {
    return resources.map { resource -> insertResource(resource) }
  }

  //@Insert(onConflict = OnConflictStrategy.REPLACE)
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  abstract suspend fun insertResourceEntity(resourceEntity: ResourceEntity): Long

  @Update
  abstract suspend fun updateResourceEntity(resourceEntity: ResourceEntity)

  private suspend fun insertOrUpdateResourceEntity(resourceEntity: ResourceEntity){
    val id = insertResourceEntity(resourceEntity)
    if(id == -1L) {
      updateResourceEntity(resourceEntity)
    }
  }

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  abstract suspend fun insertStringIndex(stringIndexEntity: StringIndexEntity): Long

  @Update
  abstract suspend fun updateStringIndex(stringIndexEntity: StringIndexEntity)

  private suspend fun insertOrUpdateStringIndex(stringIndexEntity: StringIndexEntity){
    val id = insertStringIndex(stringIndexEntity)
    if(id == -1L) {
      updateStringIndex(stringIndexEntity)
    }
  }

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  abstract suspend fun insertReferenceIndex(referenceIndexEntity: ReferenceIndexEntity): Long

  @Update
  abstract suspend fun updateReferenceIndex(referenceIndexEntity: ReferenceIndexEntity)

  private suspend fun insertOrUpdateReferenceIndex(referenceIndexEntity: ReferenceIndexEntity){
    val id = insertReferenceIndex(referenceIndexEntity)
    if(id == -1L) {
      updateReferenceIndex(referenceIndexEntity)
    }
  }

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  abstract suspend fun insertCodeIndex(tokenIndexEntity: TokenIndexEntity): Long

  @Update
  abstract suspend fun updateCodeIndex(tokenIndexEntity: TokenIndexEntity)

  private suspend fun insertOrUpdateCodeIndex(tokenIndexEntity: TokenIndexEntity){
    val id = insertCodeIndex(tokenIndexEntity)
    if(id == -1L){
      updateCodeIndex(tokenIndexEntity)
    }
  }

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  abstract suspend fun insertQuantityIndex(quantityIndexEntity: QuantityIndexEntity): Long

  @Update
  abstract suspend fun updateQuantityIndex(quantityIndexEntity: QuantityIndexEntity)

  private suspend fun insertOrUpdateQuantityIndex(quantityIndexEntity: QuantityIndexEntity){
    val id = insertQuantityIndex(quantityIndexEntity)
    if(id == -1L){
      updateQuantityIndex(quantityIndexEntity)
    }
  }

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  abstract suspend fun insertUriIndex(uriIndexEntity: UriIndexEntity): Long

  @Update
  abstract suspend fun updateUriIndex(uriIndexEntity: UriIndexEntity)

  private suspend fun insertOrUpdateUriIndex(uriIndexEntity: UriIndexEntity){
    val id = insertUriIndex(uriIndexEntity)
    if(id == -1L){
      updateUriIndex(uriIndexEntity)
    }
  }


  @Insert(onConflict = OnConflictStrategy.IGNORE)
  abstract suspend fun insertDateIndex(dateIndexEntity: DateIndexEntity): Long

  @Update
  abstract suspend fun updateDateIndex(dateIndexEntity: DateIndexEntity)

  private suspend fun insertOrUpdateDateIndex(dateIndexEntity: DateIndexEntity){
    val id = insertDateIndex(dateIndexEntity)
    if(id == -1L){
      updateDateIndex(dateIndexEntity)
    }
  }

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  abstract suspend fun insertDateTimeIndex(dateTimeIndexEntity: DateTimeIndexEntity): Long

  @Update
  abstract suspend fun updateDateTimeIndex(dateTimeIndexEntity: DateTimeIndexEntity)

  private suspend fun insertOrUpdateDateTimeIndex(dateTimeIndexEntity: DateTimeIndexEntity){
    val id = insertDateTimeIndex(dateTimeIndexEntity)
    if(id == -1L){
      updateDateTimeIndex(dateTimeIndexEntity)
    }
  }

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  abstract suspend fun insertNumberIndex(numberIndexEntity: NumberIndexEntity): Long

  @Update
  abstract suspend fun updatetNumberIndex(numberIndexEntity: NumberIndexEntity)

  private suspend fun insertOrUpdateNumberIndex(numberIndexEntity: NumberIndexEntity){
    val id = insertNumberIndex(numberIndexEntity)
    if(id == -1L){
      updatetNumberIndex(numberIndexEntity)
    }
  }

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  abstract suspend fun insertPositionIndex(positionIndexEntity: PositionIndexEntity): Long

  @Update
  abstract suspend fun updatePositionIndex(positionIndexEntity: PositionIndexEntity)

  private suspend fun insertOrUpdatePositionIndex(positionIndexEntity: PositionIndexEntity){
    val id = insertPositionIndex(positionIndexEntity)
    if(id == -1L){
      updatePositionIndex(positionIndexEntity)
    }
  }

  @Query(
    """
        UPDATE ResourceEntity
        SET serializedResource = :serializedResource
        WHERE resourceId = :resourceId
        AND resourceType = :resourceType
        """
  )
  abstract suspend fun updateResource(
    resourceId: String,
    resourceType: ResourceType,
    serializedResource: String
  )

  @Query(
    """
        UPDATE ResourceEntity
        SET versionId = :versionId,
            lastUpdatedRemote = :lastUpdatedRemote
        WHERE resourceId = :resourceId
        AND resourceType = :resourceType
    """
  )
  abstract suspend fun updateRemoteVersionIdAndLastUpdate(
    resourceId: String,
    resourceType: ResourceType,
    versionId: String?,
    lastUpdatedRemote: Instant?
  )

  @Query(
    """
        DELETE FROM ResourceEntity
        WHERE resourceId = :resourceId AND resourceType = :resourceType"""
  )
  abstract suspend fun deleteResource(resourceId: String, resourceType: ResourceType): Int

  @Query(
    """
        SELECT serializedResource
        FROM ResourceEntity
        WHERE resourceId = :resourceId AND resourceType = :resourceType"""
  )
  abstract suspend fun getResource(resourceId: String, resourceType: ResourceType): String?

  @Query(
    """
        SELECT *
        FROM ResourceEntity
        WHERE resourceId = :resourceId AND resourceType = :resourceType
    """
  )
  abstract suspend fun getResourceEntity(
    resourceId: String,
    resourceType: ResourceType
  ): ResourceEntity?

  @RawQuery abstract suspend fun getResources(query: SupportSQLiteQuery): List<String>

  @RawQuery abstract suspend fun countResources(query: SupportSQLiteQuery): Long

  private suspend fun insertResource(resource: Resource): String {
    val resourceUuid = UUID.randomUUID()

    // Use the local UUID as the logical ID of the resource
    if (resource.id.isNullOrEmpty()) {
      resource.id = resourceUuid.toString()
    }

    val entity =
      ResourceEntity(
        id = 0,
        resourceType = resource.resourceType,
        resourceUuid = resourceUuid,
        resourceId = resource.logicalId,
        serializedResource = iParser.encodeResourceToString(resource),
        versionId = resource.versionId,
        lastUpdatedRemote = resource.lastUpdated
      )
    insertOrUpdateResourceEntity(entity)
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
      insertOrUpdateStringIndex(
        StringIndexEntity(
          id = 0,
          resourceType = resource.resourceType,
          index = it,
          resourceUuid = resourceUuid,
        )
      )
    }
    index.referenceIndices.forEach {
      insertOrUpdateReferenceIndex(
        ReferenceIndexEntity(
          id = 0,
          resourceType = resource.resourceType,
          index = it,
          resourceUuid = resourceUuid,
        )
      )
    }
    index.tokenIndices.forEach {
      insertOrUpdateCodeIndex(
        TokenIndexEntity(
          id = 0,
          resourceType = resource.resourceType,
          index = it,
          resourceUuid = resourceUuid,
        )
      )
    }
    index.quantityIndices.forEach {
      insertOrUpdateQuantityIndex(
        QuantityIndexEntity(
          id = 0,
          resourceType = resource.resourceType,
          index = it,
          resourceUuid = resourceUuid,
        )
      )
    }
    index.uriIndices.forEach {
      insertOrUpdateUriIndex(
        UriIndexEntity(
          id = 0,
          resourceType = resource.resourceType,
          index = it,
          resourceUuid = resourceUuid,
        )
      )
    }
    index.dateIndices.forEach {
      insertOrUpdateDateIndex(
        DateIndexEntity(
          id = 0,
          resourceType = resource.resourceType,
          index = it,
          resourceUuid = resourceUuid,
        )
      )
    }
    index.dateTimeIndices.forEach {
      insertOrUpdateDateTimeIndex(
        DateTimeIndexEntity(
          id = 0,
          resourceType = resource.resourceType,
          index = it,
          resourceUuid = resourceUuid,
        )
      )
    }
    index.numberIndices.forEach {
      insertOrUpdateNumberIndex(
        NumberIndexEntity(
          id = 0,
          resourceType = resource.resourceType,
          index = it,
          resourceUuid = resourceUuid,
        )
      )
    }
    index.positionIndices.forEach {
      insertOrUpdatePositionIndex(
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
