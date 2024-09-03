/*
 * Copyright 2023-2024 Google LLC
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
import androidx.room.Transaction
import ca.uhn.fhir.parser.IParser
import ca.uhn.fhir.util.FhirTerser
import ca.uhn.fhir.util.ResourceReferenceInfo
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jsonpatch.diff.JsonDiff
import com.google.android.fhir.LocalChangeToken
import com.google.android.fhir.db.impl.addUpdatedReferenceToResource
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.db.impl.entities.LocalChangeEntity.Type
import com.google.android.fhir.db.impl.entities.LocalChangeResourceReferenceEntity
import com.google.android.fhir.db.impl.entities.ResourceEntity
import com.google.android.fhir.db.impl.replaceJsonValue
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
 * e.g. an INSERT or UPDATE. The UPDATES (diffs) are stored as RFC 6902 JSON patches.
 */
@Dao
internal abstract class LocalChangeDao {

  lateinit var iParser: IParser
  lateinit var fhirTerser: FhirTerser

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract suspend fun addLocalChange(localChangeEntity: LocalChangeEntity): Long

  @Query(
    """
        UPDATE LocalChangeEntity 
        SET resourceId = :updatedResourceId 
        WHERE id = :localChangeId
    """,
  )
  abstract suspend fun updateResourceId(localChangeId: Long, updatedResourceId: String): Int

  @Transaction
  open suspend fun addInsert(resource: Resource, resourceUuid: UUID, timeOfLocalChange: Instant) {
    val resourceId = resource.logicalId
    val resourceType = resource.resourceType
    val resourceString = iParser.encodeResourceToString(resource)

    val localChangeEntity =
      LocalChangeEntity(
        id = DEFAULT_ID_VALUE,
        resourceType = resourceType.name,
        resourceId = resourceId,
        resourceUuid = resourceUuid,
        timestamp = timeOfLocalChange,
        type = Type.INSERT,
        payload = resourceString,
        versionId = resource.versionId,
      )

    val localChangeReferences =
      extractResourceReferences(resource).mapNotNull { resourceReferenceInfo ->
        if (resourceReferenceInfo.resourceReference.referenceElement.value != null) {
          LocalChangeResourceReferenceEntity(
            id = DEFAULT_ID_VALUE,
            localChangeId = DEFAULT_ID_VALUE,
            resourceReferencePath = resourceReferenceInfo.name,
            resourceReferenceValue = resourceReferenceInfo.resourceReference.referenceElement.value,
          )
        } else {
          null
        }
      }
    createLocalChange(localChangeEntity, localChangeReferences)
  }

  private suspend fun createLocalChange(
    localChange: LocalChangeEntity,
    localChangeReferences: List<LocalChangeResourceReferenceEntity>,
  ) {
    val localChangeId = addLocalChange(localChange)
    if (localChangeReferences.isNotEmpty()) {
      insertLocalChangeResourceReferences(
        localChangeReferences.map { it.copy(localChangeId = localChangeId) },
      )
    }
  }

  suspend fun addUpdate(
    oldEntity: ResourceEntity,
    updatedResource: Resource,
    timeOfLocalChange: Instant,
  ) {
    val resourceId = updatedResource.logicalId
    val resourceType = updatedResource.resourceType

    if (
      !localChangeIsEmpty(resourceId, resourceType) &&
        lastChangeType(resourceId, resourceType)!! == Type.DELETE
    ) {
      throw InvalidLocalChangeException(
        "Unexpected DELETE when updating $resourceType/$resourceId. UPDATE failed.",
      )
    }
    val oldResource = iParser.parseResource(oldEntity.serializedResource) as Resource
    val jsonDiff = diff(iParser, oldResource, updatedResource)
    if (jsonDiff.length() == 0) {
      Timber.i(
        "New resource ${updatedResource.resourceType}/${updatedResource.id} is same as old resource. " +
          "Not inserting UPDATE LocalChange.",
      )
      return
    }
    val localChangeEntity =
      LocalChangeEntity(
        id = DEFAULT_ID_VALUE,
        resourceType = resourceType.name,
        resourceId = resourceId,
        resourceUuid = oldEntity.resourceUuid,
        timestamp = timeOfLocalChange,
        type = Type.UPDATE,
        payload = jsonDiff.toString(),
        versionId = oldEntity.versionId,
      )

    val localChangeReferences =
      extractReferencesDiff(oldResource, updatedResource).map { resourceReferenceInfo ->
        LocalChangeResourceReferenceEntity(
          id = DEFAULT_ID_VALUE,
          localChangeId = DEFAULT_ID_VALUE,
          resourceReferencePath = resourceReferenceInfo.name,
          resourceReferenceValue = resourceReferenceInfo.resourceReference.referenceElement.value,
        )
      }
    createLocalChange(localChangeEntity, localChangeReferences)
  }

  suspend fun addDelete(
    resourceId: String,
    resourceUuid: UUID,
    resourceType: ResourceType,
    remoteVersionId: String?,
  ) {
    createLocalChange(
      LocalChangeEntity(
        id = DEFAULT_ID_VALUE,
        resourceType = resourceType.name,
        resourceId = resourceId,
        resourceUuid = resourceUuid,
        timestamp = Date().toInstant(),
        type = Type.DELETE,
        payload = "",
        versionId = remoteVersionId,
      ),
      emptyList(),
    )
  }

  private fun extractResourceReferences(resource: Resource) =
    fhirTerser.getAllResourceReferences(resource).toSet()

  /**
   * Extract the difference in the [ResourceReferenceInfo]s in the two versions of the resource.
   *
   * Two versions of a resource can vary in two ways in terms of the resources they refer:
   * 1) A reference present in oldVersionResource is removed, hence, not present in
   *    newVersionResource.
   * 2) A new reference is added to the oldVersionResource, hence, the reference is present in
   *    newVersionResource and not in oldVersionResource.
   *
   * We compute the differences of both the above kinds to return the entire set of differences.
   *
   * This method is useful to extract differences for UPDATE kind of [LocalChange]
   *
   * @param oldVersionResource: The older version of the resource
   * @param newVersionResource: The new version of the resource
   * @return A set of [ResourceReferenceInfo] containing the differences in references between the
   *   two resource versions.
   */
  private fun extractReferencesDiff(
    oldVersionResource: Resource,
    newVersionResource: Resource,
  ): Set<ResourceReferenceInfo> {
    require(oldVersionResource.resourceType.equals(newVersionResource.resourceType))
    val oldVersionResourceReferences = extractResourceReferences(oldVersionResource).toSet()
    val newVersionResourceReferences = extractResourceReferences(newVersionResource).toSet()
    return oldVersionResourceReferences.minus(newVersionResourceReferences) +
      newVersionResourceReferences.minus(oldVersionResourceReferences)
  }

  private fun Set<ResourceReferenceInfo>.minus(set: Set<ResourceReferenceInfo>) =
    filter { ref ->
        set.none {
          it.name == ref.name &&
            it.resourceReference.referenceElement.value ==
              ref.resourceReference.referenceElement.value
        }
      }
      .toSet()

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
        ORDER BY timestamp ASC""",
  )
  abstract suspend fun getAllLocalChanges(): List<LocalChangeEntity>

  @Query(
    """
        SELECT *
        FROM LocalChangeEntity
        WHERE LocalChangeEntity.id IN (:ids) 
        ORDER BY timestamp ASC""",
  )
  abstract suspend fun getLocalChanges(ids: List<Long>): List<LocalChangeEntity>

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
        ORDER BY timestamp ASC
    """,
  )
  abstract suspend fun getLocalChanges(
    resourceType: ResourceType,
    resourceId: String,
  ): List<LocalChangeEntity>

  @Query(
    """
        SELECT *
        FROM LocalChangeEntity
        WHERE resourceUuid = :resourceUuid 
        ORDER BY timestamp ASC
    """,
  )
  abstract suspend fun getLocalChanges(
    resourceUuid: UUID,
  ): List<LocalChangeEntity>

  @Query(
    """
        SELECT DISTINCT localChangeId
        FROM LocalChangeResourceReferenceEntity
        WHERE resourceReferenceValue = :resourceReferenceValue
    """,
  )
  abstract suspend fun getLocalChangeIdsWithReferenceValue(
    resourceReferenceValue: String,
  ): List<Long>

  @Query(
    """
        SELECT *
        FROM LocalChangeResourceReferenceEntity
        WHERE localChangeId = :localChangeId
    """,
  )
  abstract suspend fun getReferencesForLocalChange(
    localChangeId: Long,
  ): List<LocalChangeResourceReferenceEntity>

  @Query(
    """
        SELECT *
        FROM LocalChangeResourceReferenceEntity
        WHERE localChangeId IN (:localChangeId)
    """,
  )
  abstract suspend fun getReferencesForLocalChanges(
    localChangeId: List<Long>,
  ): List<LocalChangeResourceReferenceEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract suspend fun insertLocalChangeResourceReferences(
    resourceReferences: List<LocalChangeResourceReferenceEntity>,
  )

  /**
   * Updates the [LocalChangeEntity]s to reflect the change in the resource ID.
   *
   * This function performs the following steps:
   * 1. Updates the `resourceId` in `LocalChange` entities directly related to the updated resource
   * 2. Updates references within `LocalChange` payloads that point to the updated resource
   *
   * @param resourceUuid The UUID of the resource whose ID has changed
   * @param oldResource The original resource with the old ID
   * @param updatedResourceId The updated resource ID
   * @return A list of UUIDs representing resources that reference the affected resource
   */
  suspend fun updateResourceIdAndReferences(
    resourceUuid: UUID,
    oldResource: Resource,
    updatedResourceId: String,
  ): List<UUID> {
    updateResourceIdInResourceLocalChanges(
      resourceUuid = resourceUuid,
      updatedResourceId = updatedResourceId,
    )
    return updateReferencesInLocalChange(
      oldResource = oldResource,
      updatedResourceId = updatedResourceId,
    )
  }

  /**
   * Updates the [LocalChangeEntity]s for the updated resource by updating the
   * [LocalChangeEntity.resourceId].
   */
  private suspend fun updateResourceIdInResourceLocalChanges(
    resourceUuid: UUID,
    updatedResourceId: String,
  ) =
    getLocalChanges(resourceUuid).forEach { localChangeEntity ->
      updateResourceId(localChangeEntity.id, updatedResourceId)
    }

  /**
   * Updates references within [LocalChangeEntity] payloads to reflect a resource ID change.
   *
   * This function performs the following steps:
   * 1. Retrieves [LocalChangeEntity] records that reference the old resource.
   * 2. For each [LocalChangeEntity]:
   *     - Replaces the old resource reference with the new one in its payload.
   *     - Creates updated [LocalChangeResourceReferenceEntity] objects.
   *     - Deletes the original [LocalChangeEntity] record, which triggers a cascading delete in
   *       [LocalChangeResourceReferenceEntity].
   *     - Creates a new [LocalChangeEntity] record along with new
   *       [LocalChangeResourceReferenceEntity] records.
   *
   * @param oldResource The original resource whose ID has been updated.
   * @param updatedResource The updated resource with the new ID.
   * @return A list of distinct resource UUIDs for all `LocalChangeEntity` records that referenced
   *   the old resource.
   */
  private suspend fun updateReferencesInLocalChange(
    oldResource: Resource,
    updatedResourceId: String,
  ): List<UUID> {
    val oldReferenceValue = "${oldResource.resourceType.name}/${oldResource.logicalId}"
    val updatedReferenceValue = "${oldResource.resourceType.name}/$updatedResourceId"

    /**
     * [getLocalChangeIdsWithReferenceValue] and [getLocalChanges] cannot be combined due to a
     * limitation in Room. Fetching [LocalChangeEntity] in chunks is required to avoid the error
     * documented in https://github.com/google/android-fhir/issues/2559.
     */
    val referringLocalChangeIds = getLocalChangeIdsWithReferenceValue(oldReferenceValue)
    val localChangeEntitiesWithOldReferences =
      referringLocalChangeIds.chunked(SQLITE_LIMIT_MAX_VARIABLE_NUMBER).flatMap {
        getLocalChanges(it)
      }

    localChangeEntitiesWithOldReferences.forEach { localChangeEntityWithOldReferences ->
      val updatedLocalChangeEntity =
        replaceReferencesInLocalChangePayload(
            localChange = localChangeEntityWithOldReferences,
            oldReference = oldReferenceValue,
            updatedReference = updatedReferenceValue,
          )
          .copy(id = DEFAULT_ID_VALUE)
      val updatedLocalChangeReferences =
        getReferencesForLocalChange(localChangeEntityWithOldReferences.id).map {
          localChangeResourceReferenceEntity ->
          if (localChangeResourceReferenceEntity.resourceReferenceValue == oldReferenceValue) {
            LocalChangeResourceReferenceEntity(
              id = DEFAULT_ID_VALUE,
              localChangeId = DEFAULT_ID_VALUE,
              resourceReferencePath = localChangeResourceReferenceEntity.resourceReferencePath,
              resourceReferenceValue = updatedReferenceValue,
            )
          } else {
            localChangeResourceReferenceEntity.copy(
              id = DEFAULT_ID_VALUE,
              localChangeId = DEFAULT_ID_VALUE,
            )
          }
        }
      discardLocalChanges(localChangeEntityWithOldReferences.id)
      createLocalChange(updatedLocalChangeEntity, updatedLocalChangeReferences)
    }
    return localChangeEntitiesWithOldReferences.map { it.resourceUuid }.distinct()
  }

  private fun replaceReferencesInLocalChangePayload(
    localChange: LocalChangeEntity,
    oldReference: String,
    updatedReference: String,
  ): LocalChangeEntity {
    return when (localChange.type) {
      LocalChangeEntity.Type.INSERT -> {
        val insertResourcePayload = iParser.parseResource(localChange.payload) as Resource
        val updatedResourcePayload =
          addUpdatedReferenceToResource(
            iParser,
            insertResourcePayload,
            oldReference,
            updatedReference,
          )
        return localChange.copy(
          payload = iParser.encodeResourceToString(updatedResourcePayload),
        )
      }
      LocalChangeEntity.Type.UPDATE -> {
        val patchArray = JSONArray(localChange.payload)
        val updatedPatchArray = JSONArray()
        for (i in 0 until patchArray.length()) {
          val updatedPatch =
            replaceJsonValue(patchArray.getJSONObject(i), oldReference, updatedReference)
          updatedPatchArray.put(updatedPatch)
        }
        return localChange.copy(
          payload = updatedPatchArray.toString(),
        )
      }
      LocalChangeEntity.Type.DELETE -> localChange
    }
  }

  @Query(
    """
    SELECT * 
      FROM LocalChangeEntity 
      WHERE resourceUuid = (
        SELECT resourceUuid 
        FROM LocalChangeEntity 
        ORDER BY timestamp ASC 
        LIMIT 1)
      ORDER BY timestamp ASC
    """,
  )
  abstract suspend fun getAllChangesForEarliestChangedResource(): List<LocalChangeEntity>

  class InvalidLocalChangeException(message: String?) : Exception(message)

  companion object {
    const val DEFAULT_ID_VALUE = 0L

    /**
     * Represents SQLite limit on the size of parameters that can be passed in an IN(..) query See
     * https://issuetracker.google.com/issues/192284727 See https://www.sqlite.org/limits.html See
     * https://github.com/google/android-fhir/issues/2559
     */
    const val SQLITE_LIMIT_MAX_VARIABLE_NUMBER = 999
  }
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
