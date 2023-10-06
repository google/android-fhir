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
@VisibleForTesting
internal abstract class LocalChangeDao {

  lateinit var iParser: IParser
  lateinit var fhirTerser: FhirTerser

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract suspend fun addLocalChange(localChangeEntity: LocalChangeEntity): Long

  @Transaction
  open suspend fun addInsert(resource: Resource, resourceUuid: UUID, timeOfLocalChange: Instant) {
    val resourceId = resource.logicalId
    val resourceType = resource.resourceType
    val resourceString = iParser.encodeResourceToString(resource)

    val localChangeEntity =
      LocalChangeEntity(
        id = 0,
        resourceType = resourceType.name,
        resourceId = resourceId,
        resourceUuid = resourceUuid,
        timestamp = timeOfLocalChange,
        type = Type.INSERT,
        payload = resourceString,
        versionId = resource.versionId,
      )

    val localChangeReferences =
      extractResourceReferences(resource).map { resourceReferenceInfo ->
        LocalChangeResourceReferenceEntity(
          id = 0,
          localChangeId = 0,
          resourceReferenceName = resourceReferenceInfo.name,
          resourceReferenceValue = resourceReferenceInfo.resourceReference.referenceElement.value,
        )
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
        id = 0,
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
          id = 0,
          localChangeId = 0,
          resourceReferenceName = resourceReferenceInfo.name,
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
        id = 0,
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
   * Extract the difference in the [ResourceReferenceInfo] by getting all the references from both
   * the versions of the resource and then finding out the difference in the two sets.
   */
  private fun extractReferencesDiff(
    resource1: Resource,
    resource2: Resource,
  ): Set<ResourceReferenceInfo> {
    require(resource1.resourceType.equals(resource2.resourceType))
    val resource1References = extractResourceReferences(resource1).toSet()
    val resource2References = extractResourceReferences(resource2).toSet()
    return resource1References.minus(resource2References) +
      resource2References.minus(resource1References)
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
        ORDER BY timestamp ASC
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
        SELECT *
        FROM LocalChangeResourceReferenceEntity
        WHERE resourceReferenceValue = :resourceReferenceValue
    """,
  )
  abstract suspend fun getLocalChangeReferencesWithValue(
    resourceReferenceValue: String,
  ): List<LocalChangeResourceReferenceEntity>

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

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract suspend fun insertLocalChangeResourceReferences(
    resourceReferences: List<LocalChangeResourceReferenceEntity>,
  )

  /**
   * Updates the [LocalChangeEntity]s for the updated resource by updating the
   * [LocalChangeEntity.resourceId]. Looks for [LocalChangeEntity] which refer to the updated
   * resource through [LocalChangeResourceReferenceEntity]. For each [LocalChangeEntity] which
   * contains reference to the updated resource in its payload, we update the payload with the
   * reference and also update the corresponding [LocalChangeResourceReferenceEntity]. We delete the
   * original [LocalChangeEntity] and create a new one with new
   * [LocalChangeResourceReferenceEntity]s in its place.
   */
  suspend fun updateResourceId(
    resourceUuid: UUID,
    oldResource: Resource,
    updatedResource: Resource,
  ): List<UUID> {
    // update the resource ID in the local change entity
    val localChanges = getLocalChanges(resourceUuid)
    localChanges
      .map { localChangeEntity -> localChangeEntity.copy(resourceId = updatedResource.logicalId) }
      // Add LocalChangeEntity with replace strategy, the references need not be updated
      .forEach { addLocalChange(it) }

    // update all local changes referring to the resource
    val oldReferenceValue = "${oldResource.resourceType.name}/${oldResource.logicalId}"
    val updatedReferenceValue = "${updatedResource.resourceType.name}/${updatedResource.logicalId}"
    val localChangeReferences = getLocalChangeReferencesWithValue(oldReferenceValue)
    val localChangeIds = localChangeReferences.map { it.localChangeId }.distinct()
    val localChangesWithReferences = getLocalChanges(localChangeIds).associateBy { it.id }

    localChangeIds.forEach { localChangeId ->
      val existingLocalChangeEntity = localChangesWithReferences[localChangeId]!!
      val updatedLocalChangeEntity =
        replaceReferencesInLocalChangeEntity(
            localChange = existingLocalChangeEntity,
            oldReference = oldReferenceValue,
            updatedReference = updatedReferenceValue,
          )
          .copy(id = 0)
      val updatedLocalChangeReferences =
        getReferencesForLocalChange(localChangeId).map { reference ->
          if (reference.resourceReferenceValue == oldReferenceValue) {
            LocalChangeResourceReferenceEntity(
              id = 0,
              localChangeId = 0,
              resourceReferenceName = reference.resourceReferenceName,
              resourceReferenceValue = updatedReferenceValue,
            )
          } else {
            reference.copy(id = 0, localChangeId = 0)
          }
        }
      discardLocalChanges(localChangeId)
      createLocalChange(updatedLocalChangeEntity, updatedLocalChangeReferences)
    }
    return localChangesWithReferences.values.map { it.resourceUuid }.distinct()
  }

  private fun replaceReferencesInLocalChangeEntity(
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
