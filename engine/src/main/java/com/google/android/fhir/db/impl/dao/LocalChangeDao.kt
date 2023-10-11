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

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import ca.uhn.fhir.parser.IParser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jsonpatch.diff.JsonDiff
import com.google.android.fhir.LocalChangeToken
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.db.impl.entities.LocalChangeEntity.Type
import com.google.android.fhir.db.impl.entities.ResourceEntity
import com.google.android.fhir.logicalId
import com.google.android.fhir.versionId
import java.time.Instant
import java.util.Date
import java.util.UUID
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.json.JSONArray
import timber.log.Timber

/**
 * Dao for local changes made to a resource. One row in LocalChangeEntity corresponds to one change
 * e.g. an INSERT or UPDATE. The UPDATES (diffs) are stored as RFC 6902 JSON patches. When a
 * resource needs to be synced, all corresponding LocalChanges are 'squashed' to create a a single
 * LocalChangeEntity to sync with the server.
 */
@Dao
internal abstract class LocalChangeDao {

  lateinit var iParser: IParser

  @Insert abstract suspend fun addLocalChange(localChangeEntity: LocalChangeEntity)

  @Transaction
  open suspend fun addInsert(resource: Resource, resourceUuid: UUID, timeOfLocalChange: Instant) {
    val resourceId = resource.logicalId
    val resourceType = resource.resourceType
    val resourceString = iParser.encodeResourceToString(resource)

    addLocalChange(
      LocalChangeEntity(
        id = 0,
        resourceType = resourceType.name,
        resourceId = resourceId,
        resourceUuid = resourceUuid,
        timestamp = timeOfLocalChange,
        type = Type.INSERT,
        payload = resourceString,
        versionId = resource.versionId,
      ),
    )
  }

  suspend fun addUpdate(oldEntity: ResourceEntity, resource: Resource, timeOfLocalChange: Instant) {
    val resourceId = resource.logicalId
    val resourceType = resource.resourceType

    if (
      !localChangeIsEmpty(resourceId, resourceType) &&
        lastChangeType(resourceId, resourceType)!! == Type.DELETE
    ) {
      throw InvalidLocalChangeException(
        "Unexpected DELETE when updating $resourceType/$resourceId. UPDATE failed.",
      )
    }
    val jsonDiff =
      diff(iParser, iParser.parseResource(oldEntity.serializedResource) as Resource, resource)
    if (jsonDiff.length() == 0) {
      Timber.i(
        "New resource ${resource.resourceType}/${resource.id} is same as old resource. " +
          "Not inserting UPDATE LocalChange.",
      )
      return
    }
    addLocalChange(
      LocalChangeEntity(
        id = 0,
        resourceType = resourceType.name,
        resourceId = resourceId,
        resourceUuid = oldEntity.resourceUuid,
        timestamp = timeOfLocalChange,
        type = Type.UPDATE,
        payload = jsonDiff.toString(),
        versionId = oldEntity.versionId,
      ),
    )
  }

  suspend fun addDelete(
    resourceId: String,
    resourceUuid: UUID,
    resourceType: ResourceType,
    remoteVersionId: String?,
  ) {
    addLocalChange(
      LocalChangeEntity(
        id = 0,
        resourceType = resourceType.name,
        resourceId = resourceId,
        resourceUuid = resourceUuid,
        timestamp = Date().toInstant(),
        type = Type.DELETE,
        payload = "",
        versionId = remoteVersionId,
      ),
    )
  }

  @Query(
    """
        SELECT type 
        FROM LocalChangeEntity 
        WHERE resourceId = :resourceId 
        AND resourceType = :resourceType 
        ORDER BY id ASC
        LIMIT 1
    """,
  )
  abstract suspend fun lastChangeType(resourceId: String, resourceType: ResourceType): Type?

  @Query(
    """
        SELECT COUNT(type) 
        FROM LocalChangeEntity 
        WHERE resourceId = :resourceId 
        AND resourceType = :resourceType
        LIMIT 1
    """,
  )
  abstract suspend fun countLastChange(resourceId: String, resourceType: ResourceType): Int

  private suspend fun localChangeIsEmpty(resourceId: String, resourceType: ResourceType): Boolean =
    countLastChange(resourceId, resourceType) == 0

  @Query(
    """
        SELECT *
        FROM LocalChangeEntity
        ORDER BY LocalChangeEntity.id ASC""",
  )
  abstract suspend fun getAllLocalChanges(): List<LocalChangeEntity>

  @Query(
    """
        SELECT COUNT(*)
        FROM LocalChangeEntity
        """,
  )
  abstract suspend fun getLocalChangesCount(): Int

  @Query(
    """
        DELETE FROM LocalChangeEntity
        WHERE LocalChangeEntity.id = (:id)
    """,
  )
  abstract suspend fun discardLocalChanges(id: Long)

  @Transaction
  open suspend fun discardLocalChanges(token: LocalChangeToken) {
    token.ids.forEach { discardLocalChanges(it) }
  }

  @Query(
    """
        DELETE FROM LocalChangeEntity
        WHERE resourceId = (:resourceId)
        AND resourceType = :resourceType
    """,
  )
  abstract suspend fun discardLocalChanges(resourceId: String, resourceType: ResourceType)

  suspend fun discardLocalChanges(resources: List<Resource>) {
    resources.forEach { discardLocalChanges(it.logicalId, it.resourceType) }
  }

  @Query(
    """
        SELECT *
        FROM LocalChangeEntity
        WHERE resourceId = :resourceId AND resourceType = :resourceType
    """,
  )
  abstract suspend fun getLocalChanges(
    resourceType: ResourceType,
    resourceId: String,
  ): List<LocalChangeEntity>

  class InvalidLocalChangeException(message: String?) : Exception(message)
}

/** Calculates the JSON patch between two [Resource] s. */
internal fun diff(parser: IParser, source: Resource, target: Resource): JSONArray {
  val objectMapper = ObjectMapper()
  return getFilteredJSONArray(
    JsonDiff.asJson(
      objectMapper.readValue(parser.encodeResourceToString(source), JsonNode::class.java),
      objectMapper.readValue(parser.encodeResourceToString(target), JsonNode::class.java),
    ),
  )
}

/**
 * This function returns the json diff as a json array of operation objects. We remove the "/meta"
 * and "/text" paths as they cause path not found issue when we update the resource. They are
 * usually present in the downloaded resource object but are missing in the edited object as these
 * aren't supposed to be edited. Thus, the Json diff creates a DELETE- OP for "/meta" and "/text"
 * and causes the issue with server update.
 *
 * An unfiltered JSON Array for family name update looks like
 *
 * ```
 * [{"op":"remove","path":"/meta"}, {"op":"remove","path":"/text"},
 * {"op":"replace","path":"/name/0/family","value":"Nucleus"}]
 * ```
 *
 * A filtered JSON Array for family name update looks like
 *
 * ```
 * [{"op":"replace","path":"/name/0/family","value":"Nucleus"}]
 * ```
 */
private fun getFilteredJSONArray(jsonDiff: JsonNode) =
  with(JSONArray(jsonDiff.toString())) {
    val ignorePaths = setOf("/meta", "/text")
    return@with JSONArray(
      (0 until length())
        .map { optJSONObject(it) }
        .filterNot { jsonObject ->
          ignorePaths.any { jsonObject.optString("path").startsWith(it) }
        },
    )
  }
