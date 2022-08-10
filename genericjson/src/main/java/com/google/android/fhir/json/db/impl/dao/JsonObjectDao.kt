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

package com.google.android.fhir.json.db.impl.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.google.android.fhir.json.db.impl.entities.JsonResourceEntity
import com.google.android.fhir.json.sync.JsonResource
import java.time.Instant
import java.util.UUID

@Dao
internal abstract class JsonResourceDao {

  open suspend fun update(resource: JsonResource) {
    updateResource(
      resource.id,
      resource.resourceType,
      resource.payload.toString(),
    )
  }

  open suspend fun insert(resource: JsonResource): String {
    return insertResource(resource)
  }

  open suspend fun insertAll(resources: List<JsonResource>): List<String> {
    return resources.map { resource -> insertResource(resource) }
  }

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract suspend fun insertResource(resource: JsonResourceEntity)

  @Query(
    """
        UPDATE JsonResourceEntity
        SET serializedResource = :serializedResource
        WHERE resourceId = :resourceId
        AND resourceType = :resourceType
        """
  )
  abstract suspend fun updateResource(
    resourceId: String,
    resourceType: String,
    serializedResource: String
  )

  @Query(
    """
        UPDATE JsonResourceEntity
        SET versionId = :versionId,
            lastUpdatedRemote = :lastUpdatedRemote
        WHERE resourceId = :resourceId
        AND resourceType = :resourceType
    """
  )
  abstract suspend fun updateRemoteVersionIdAndLastUpdate(
    resourceId: String,
    resourceType: String,
    versionId: String?,
    lastUpdatedRemote: Instant?
  )

  @Query(
    """
        DELETE FROM JsonResourceEntity
        WHERE resourceId = :resourceId AND resourceType = :resourceType"""
  )
  abstract suspend fun deleteResource(resourceId: String, resourceType: String): Int

  @Query(
    """
        SELECT serializedResource
        FROM JsonResourceEntity
        WHERE resourceId = :resourceId AND resourceType = :resourceType"""
  )
  abstract suspend fun getResource(resourceId: String, resourceType: String): String?

  @Query(
    """
        SELECT *
        FROM JsonResourceEntity
        WHERE resourceId = :resourceId AND resourceType = :resourceType
    """
  )
  abstract suspend fun getResourceEntity(
    resourceId: String,
    resourceType: String
  ): JsonResourceEntity?

  private suspend fun insertResource(resource: JsonResource): String {
    val resourceUuid = UUID.randomUUID()

    val entity =
      JsonResourceEntity(
        id = 0,
        resourceType = resource.resourceType,
        resourceUuid = resourceUuid,
        resourceId = resource.id,
        serializedResource = resource.payload.toString(),
        versionId = resource.versionId,
        lastUpdatedRemote = resource.lastUpdated?.toInstant()
      )
    insertResource(entity)
    return resource.id
  }
}
