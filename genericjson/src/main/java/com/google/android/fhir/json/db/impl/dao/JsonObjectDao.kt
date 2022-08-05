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
import com.google.android.fhir.json.db.impl.entities.JsonObjectEntity
import java.time.Instant
import java.util.UUID
import org.json.JSONObject

@Dao
internal abstract class JsonObjectDao {

  open suspend fun update(resource: JSONObject) {
    val logicalId = resource["id"].toString()
    updateResource(logicalId, resource.toString())
  }

  open suspend fun insert(resource: JSONObject): String {
    return insertResource(resource)
  }

  open suspend fun insertAll(resources: List<JSONObject>): List<String> {
    return resources.map { resource -> insertResource(resource) }
  }

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract suspend fun insertResource(resource: JsonObjectEntity)

  @Query(
    """
        UPDATE JsonObjectEntity
        SET serializedResource = :serializedResource
        WHERE resourceId = :resourceId
        """
  )
  abstract suspend fun updateResource(resourceId: String, serializedResource: String)

  @Query(
    """
        UPDATE JsonObjectEntity
        SET lastUpdatedRemote = :lastUpdatedRemote
        WHERE resourceId = :resourceId
    """
  )
  abstract suspend fun updateRemoteVersionIdAndLastUpdate(
    resourceId: String,
    lastUpdatedRemote: Instant?
  )

  @Query("""
        DELETE FROM JsonObjectEntity
        WHERE resourceId = :resourceId""")
  abstract suspend fun deleteResource(resourceId: String): Int

  @Query(
    """
        SELECT serializedResource
        FROM JsonObjectEntity
        WHERE resourceId = :resourceId"""
  )
  abstract suspend fun getResource(resourceId: String): String?

  @Query(
    """
        SELECT *
        FROM JsonObjectEntity
        WHERE resourceId = :resourceId
    """
  )
  abstract suspend fun getResourceEntity(
    resourceId: String,
  ): JsonObjectEntity?

  private suspend fun insertResource(resource: JSONObject): String {
    val resourceUuid = UUID.randomUUID()

    val resourceId =
      if (!resource.has("id")) {
        resourceUuid.toString()
      } else {
        resource.get("id").toString()
      }

    //    val lastUpdatedRemote =
    //      resource.get("lastUpdated").let { Instant.parse(it.toString()) }

    val entity =
      JsonObjectEntity(
        id = 0,
        resourceUuid = resourceUuid,
        resourceId = resourceId,
        serializedResource = resource.toString(),
        lastUpdatedRemote = Instant.now()
      )
    insertResource(entity)
    return resourceId
  }
}
