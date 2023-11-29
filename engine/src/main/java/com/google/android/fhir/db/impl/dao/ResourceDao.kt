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

package com.google.android.fhir.db.impl.dao

import androidx.annotation.VisibleForTesting
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
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
import com.google.android.fhir.index.ResourceIndexer.Companion.createLastUpdatedIndex
import com.google.android.fhir.index.ResourceIndexer.Companion.createLocalLastUpdatedIndex
import com.google.android.fhir.index.ResourceIndices
import com.google.android.fhir.lastUpdated
import com.google.android.fhir.logicalId
import com.google.android.fhir.versionId
import java.time.Instant
import java.util.Date
import java.util.UUID
import org.hl7.fhir.r4.model.InstantType
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

@Dao
@VisibleForTesting
internal abstract class ResourceDao {
  // this is ugly but there is no way to inject these right now in Room as it is the one creating
  // the dao
  lateinit var iParser: IParser
  lateinit var resourceIndexer: ResourceIndexer

  /**
   * Updates the resource in the [ResourceEntity] and adds indexes as a result of changes made on
   * device.
   *
   * @param [resource] the resource with local (on device) updates
   * @param [timeOfLocalChange] time when the local change was made
   */
  suspend fun applyLocalUpdate(resource: Resource, timeOfLocalChange: Instant?) {
    getResourceEntity(resource.logicalId, resource.resourceType)?.let {
      val entity =
        it.copy(
          serializedResource = iParser.encodeResourceToString(resource),
          lastUpdatedLocal = timeOfLocalChange,
        )
      updateChanges(entity, resource)
    }
      ?: throw ResourceNotFoundException(
        resource.resourceType.name,
        resource.id,
      )
  }

  suspend fun updateResourceWithUuid(resourceUuid: UUID, updatedResource: Resource) {
    getResourceEntity(resourceUuid)?.let {
      val entity =
        it.copy(
          resourceId = updatedResource.logicalId,
          serializedResource = iParser.encodeResourceToString(updatedResource),
          lastUpdatedRemote = updatedResource.meta.lastUpdated?.toInstant() ?: it.lastUpdatedRemote,
        )
      updateChanges(entity, updatedResource)
    }
      ?: throw ResourceNotFoundException(
        resourceUuid,
      )
  }

  /**
   * Updates the resource in the [ResourceEntity] and adds indexes as a result of downloading the
   * resource from server.
   *
   * @param [resource] the resource with the remote(server) updates
   */
  private suspend fun applyRemoteUpdate(resource: Resource) {
    getResourceEntity(resource.logicalId, resource.resourceType)?.let {
      val entity =
        it.copy(
          serializedResource = iParser.encodeResourceToString(resource),
          lastUpdatedRemote = resource.meta.lastUpdated?.toInstant(),
          versionId = resource.versionId,
        )
      updateChanges(entity, resource)
    }
      ?: throw ResourceNotFoundException(resource.resourceType.name, resource.id)
  }

  private suspend fun updateChanges(entity: ResourceEntity, resource: Resource) {
    // The foreign key in Index entity tables is set with cascade delete constraint and
    // insertResource has REPLACE conflict resolution. So, when we do an insert to update the
    // resource, it deletes old resource and corresponding index entities (based on foreign key
    // constrain) before inserting the new resource.
    insertResource(entity)
    val index =
      ResourceIndices.Builder(resourceIndexer.index(resource))
        .apply {
          entity.lastUpdatedLocal?.let { instant ->
            addDateTimeIndex(
              createLocalLastUpdatedIndex(resource.resourceType, InstantType(Date.from(instant))),
            )
          }
          entity.lastUpdatedRemote?.let { instant ->
            addDateTimeIndex(
              createLastUpdatedIndex(resource.resourceType, InstantType(Date.from(instant))),
            )
          }
        }
        .build()
    updateIndicesForResource(index, resource.resourceType, entity.resourceUuid)
  }

  suspend fun insertAllRemote(resources: List<Resource>): List<UUID> {
    return resources.map { resource -> insertRemoteResource(resource) }
  }

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract suspend fun insertResource(resource: ResourceEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract suspend fun insertStringIndex(stringIndexEntity: StringIndexEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract suspend fun insertReferenceIndex(referenceIndexEntity: ReferenceIndexEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract suspend fun insertCodeIndex(tokenIndexEntity: TokenIndexEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract suspend fun insertQuantityIndex(quantityIndexEntity: QuantityIndexEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract suspend fun insertUriIndex(uriIndexEntity: UriIndexEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract suspend fun insertDateIndex(dateIndexEntity: DateIndexEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract suspend fun insertDateTimeIndex(dateTimeIndexEntity: DateTimeIndexEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract suspend fun insertNumberIndex(numberIndexEntity: NumberIndexEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract suspend fun insertPositionIndex(positionIndexEntity: PositionIndexEntity)

  @Query(
    """
        UPDATE ResourceEntity
        SET versionId = :versionId,
            lastUpdatedRemote = :lastUpdatedRemote
        WHERE resourceId = :resourceId
        AND resourceType = :resourceType
    """,
  )
  abstract suspend fun updateRemoteVersionIdAndLastUpdate(
    resourceId: String,
    resourceType: ResourceType,
    versionId: String,
    lastUpdatedRemote: Instant,
  )

  @Query(
    """
        DELETE FROM ResourceEntity
        WHERE resourceId = :resourceId AND resourceType = :resourceType""",
  )
  abstract suspend fun deleteResource(resourceId: String, resourceType: ResourceType): Int

  @Query(
    """
        SELECT serializedResource
        FROM ResourceEntity
        WHERE resourceId = :resourceId AND resourceType = :resourceType""",
  )
  abstract suspend fun getResource(resourceId: String, resourceType: ResourceType): String?

  @Query(
    """
        SELECT *
        FROM ResourceEntity
        WHERE resourceId = :resourceId AND resourceType = :resourceType
    """,
  )
  abstract suspend fun getResourceEntity(
    resourceId: String,
    resourceType: ResourceType,
  ): ResourceEntity?

  @Query(
    """
        SELECT *
        FROM ResourceEntity
        WHERE resourceUuid = :resourceUuid
    """,
  )
  abstract suspend fun getResourceEntity(
    resourceUuid: UUID,
  ): ResourceEntity?

  @RawQuery abstract suspend fun getResources(query: SupportSQLiteQuery): List<String>

  @RawQuery
  abstract suspend fun getReferencedResources(
    query: SupportSQLiteQuery,
  ): List<IndexedIdAndSerializedResource>

  @RawQuery abstract suspend fun countResources(query: SupportSQLiteQuery): Long

  suspend fun insertLocalResource(resource: Resource, timeOfChange: Instant) =
    insertResource(resource, timeOfChange)

  // Check if the resource already exists using its logical ID, if it does, we just update the
  // existing [ResourceEntity]
  // Else, we insert with a new [ResourceEntity]
  private suspend fun insertRemoteResource(resource: Resource): UUID {
    val existingResourceEntity = getResourceEntity(resource.logicalId, resource.resourceType)
    if (existingResourceEntity != null) {
      applyRemoteUpdate(resource)
      return existingResourceEntity.resourceUuid
    }
    return insertResource(resource, null)
  }

  private suspend fun insertResource(resource: Resource, lastUpdatedLocal: Instant?): UUID {
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
        lastUpdatedRemote = resource.lastUpdated,
        lastUpdatedLocal = lastUpdatedLocal,
      )
    insertResource(entity)

    val index =
      ResourceIndices.Builder(resourceIndexer.index(resource))
        .apply {
          lastUpdatedLocal?.let {
            addDateTimeIndex(
              createLocalLastUpdatedIndex(entity.resourceType, InstantType(Date.from(it))),
            )
          }
        }
        .build()

    updateIndicesForResource(index, resource.resourceType, resourceUuid)

    return entity.resourceUuid
  }

  suspend fun updateAndIndexRemoteVersionIdAndLastUpdate(
    resourceId: String,
    resourceType: ResourceType,
    versionId: String,
    lastUpdated: Instant,
  ) {
    updateRemoteVersionIdAndLastUpdate(resourceId, resourceType, versionId, lastUpdated)
    // update the remote lastUpdated index
    getResourceEntity(resourceId, resourceType)?.let {
      val indicesToUpdate =
        ResourceIndices.Builder(resourceType, resourceId)
          .apply {
            addDateTimeIndex(
              createLastUpdatedIndex(resourceType, InstantType(Date.from(lastUpdated))),
            )
          }
          .build()
      updateIndicesForResource(indicesToUpdate, resourceType, it.resourceUuid)
    }
  }

  private suspend fun updateIndicesForResource(
    index: ResourceIndices,
    resourceType: ResourceType,
    resourceUuid: UUID,
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
          resourceType = resourceType,
          index = it,
          resourceUuid = resourceUuid,
        ),
      )
    }
    index.referenceIndices.forEach {
      insertReferenceIndex(
        ReferenceIndexEntity(
          id = 0,
          resourceType = resourceType,
          index = it,
          resourceUuid = resourceUuid,
        ),
      )
    }
    index.tokenIndices.forEach {
      insertCodeIndex(
        TokenIndexEntity(
          id = 0,
          resourceType = resourceType,
          index = it,
          resourceUuid = resourceUuid,
        ),
      )
    }
    index.quantityIndices.forEach {
      insertQuantityIndex(
        QuantityIndexEntity(
          id = 0,
          resourceType = resourceType,
          index = it,
          resourceUuid = resourceUuid,
        ),
      )
    }
    index.uriIndices.forEach {
      insertUriIndex(
        UriIndexEntity(
          id = 0,
          resourceType = resourceType,
          index = it,
          resourceUuid = resourceUuid,
        ),
      )
    }
    index.dateIndices.forEach {
      insertDateIndex(
        DateIndexEntity(
          id = 0,
          resourceType = resourceType,
          index = it,
          resourceUuid = resourceUuid,
        ),
      )
    }
    index.dateTimeIndices.forEach {
      insertDateTimeIndex(
        DateTimeIndexEntity(
          id = 0,
          resourceType = resourceType,
          index = it,
          resourceUuid = resourceUuid,
        ),
      )
    }
    index.numberIndices.forEach {
      insertNumberIndex(
        NumberIndexEntity(
          id = 0,
          resourceType = resourceType,
          index = it,
          resourceUuid = resourceUuid,
        ),
      )
    }
    index.positionIndices.forEach {
      insertPositionIndex(
        PositionIndexEntity(
          id = 0,
          resourceType = resourceType,
          index = it,
          resourceUuid = resourceUuid,
        ),
      )
    }
  }
}

/**
 * Data class representing the value returned by [getReferencedResources]. The optional fields may
 * or may-not contain values based on the search query.
 */
internal data class IndexedIdAndSerializedResource(
  @ColumnInfo(name = "index_name") val matchingIndex: String,
  @ColumnInfo(name = "index_value") val idOfBaseResourceOnWhichThisMatchedRev: String?,
  @ColumnInfo(name = "resourceId") val idOfBaseResourceOnWhichThisMatchedInc: String?,
  val serializedResource: String,
)

/**
 * Data class representing an included or revIncluded [Resource], index on which the match was done
 * and the id of the base [Resource] for which this [Resource] has been included.
 */
internal data class IndexedIdAndResource(
  val matchingIndex: String,
  val idOfBaseResourceOnWhichThisMatched: String,
  val resource: Resource,
)
