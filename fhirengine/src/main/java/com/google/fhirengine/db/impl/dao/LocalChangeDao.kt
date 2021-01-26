/*
 * Copyright 2020 Google LLC
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

package com.google.fhirengine.db.impl.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ca.uhn.fhir.parser.IParser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jsonpatch.JsonPatch
import com.github.fge.jsonpatch.diff.JsonDiff
import com.google.fhirengine.db.impl.entities.LocalChange
import com.google.fhirengine.db.impl.entities.LocalChange.Type
import com.google.fhirengine.toTimeZoneString
import java.lang.IllegalArgumentException
import java.util.Date
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/**
 * Dao for local changes made to a resource. One row in LocalChangeEntity corresponds to one change
 * e.g. an INSERT or UPDATE. The UPDATES (diffs) are stored as RFC 6902 JSON patches.
 * When a resource needs to be synced, all corresponding LocalChanges are 'squashed' to create a
 * a single LocalChange to sync with the server.
 *
 */
@Dao
internal abstract class LocalChangeDao {

    lateinit var iParser: IParser

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun addLocalChange(localChange: LocalChange)

    @Transaction
    open fun addInsertAll(resources: List<Resource>) {
        resources.forEach { resource ->
            addInsert(resource)
        }
    }

    fun addInsert(resource: Resource) {
        val resourceId = resource.id
        val resourceType = resource.resourceType
        val localChanges = getLocalChanges(
            resourceId = resourceId,
            resourceType = resourceType.name
        )
        val timestamp = Date().toTimeZoneString()
        val resourceString = iParser.encodeResourceToString(resource)

        if (localChanges.isNotEmpty() && !localChanges.last().type.equals(Type.DELETE)) {
            // Can't add an INSERT on top of an INSERT or UPDATE
            val lastLocalChangeType = localChanges.last().type
            throw InvalidLocalChangeException("Can not INSERT on top of $lastLocalChangeType")
        }

        // Insert this change in the local changes table
        addLocalChange(
            LocalChange(
                id = 0,
                resourceType = resourceType.name,
                resourceId = resourceId,
                timestamp = timestamp,
                type = Type.INSERT,
                diff = resourceString
            )
        )
    }

    fun addUpdate(oldResource: Resource, resource: Resource) {
        val resourceId = resource.id
        val resourceType = resource.resourceType
        val localChanges = getLocalChanges(
            resourceId = resourceId,
            resourceType = resourceType.name
        )
        val timestamp = Date().toTimeZoneString()

        if (localChanges.isNotEmpty() && localChanges.last().type.equals(Type.DELETE)) {
            throw InvalidLocalChangeException(
                "Unexpected DELETE when updating $resourceType/$resourceId. UPDATE failed."
            )
        }

        addLocalChange(
            LocalChange(
                id = 0,
                resourceType = resourceType.name,
                resourceId = resourceId,
                timestamp = timestamp,
                type = Type.UPDATE,
                diff = diff(oldResource, resource)
            )
        )
    }

    fun addDelete(resourceId: String, resourceType: ResourceType) {
        val timestamp = Date().toTimeZoneString()
        addLocalChange(
            LocalChange(
                id = 0,
                resourceType = resourceType.name,
                resourceId = resourceId,
                timestamp = timestamp,
                type = Type.DELETE,
                diff = ""
            )
        )
    }

    /**
     * Update a [Resource] with a JSON patch (RFC 6902).
     */
    private fun applyPatch(resourceString: String, patchString: String): String {
        val objectMapper = ObjectMapper()
        val resourceJson = objectMapper.readValue(resourceString, JsonNode::class.java)
        val patchJson = objectMapper.readValue(patchString, JsonPatch::class.java)
        return patchJson.apply(resourceJson).toString()
    }

    /**
     * JSON patch diff b/w two [Resource]s.
     */
    private fun diff(source: Resource, target: Resource): String {
        val objectMapper = ObjectMapper()
        val sourceJson = objectMapper.readValue(
                iParser.encodeResourceToString(source),
                JsonNode::class.java
        )
        val targetJson = objectMapper.readValue(
                iParser.encodeResourceToString(target),
                JsonNode::class.java
        )
        val jsonDiff = JsonDiff.asJson(sourceJson, targetJson)
        if (jsonDiff.size() == 0)
            Log.w(
                    "ResourceDao",
                    "Trying to UPDATE resource ${target.resourceType}/${target.id} with no changes"
            )
        return jsonDiff.toString()
    }

    /**
     * Merge two JSON patch strings by concatenating their elements into a new JSON array.
     */
    private fun mergePatches(firstPatch: String, secondPatch: String): String {
        val objectMapper = ObjectMapper()
        // TODO: validate patches are RFC 6902 compliant JSON patches
        val first = objectMapper.readValue(firstPatch, JsonPatch::class.java)
        val updater = objectMapper.readerForUpdating(first)
        val merged = updater.readValue(secondPatch, JsonPatch::class.java)
        return merged.toString()
    }

    private fun mergeLocalChanges(first: LocalChange, second: LocalChange): LocalChange {
        val type: Type
        val diff: String
        when (second.type) {
            Type.UPDATE -> if (first.type.equals(Type.UPDATE)) {
                type = Type.UPDATE
                diff = mergePatches(first.diff, second.diff)
            } else if (first.type.equals(Type.INSERT)) {
                type = Type.INSERT
                diff = applyPatch(first.diff, second.diff)
            } else {
                throw IllegalArgumentException("Don't know how to merge $first and $second.")
            }
            Type.DELETE -> {
                type = Type.DELETE
                diff = ""
            }
            else ->
                throw IllegalArgumentException("Cannot merge local changes with type ${first.type} and ${second.type}.")
        }
        return LocalChange(
                id = 0,
                resourceId = second.resourceId,
                resourceType = second.resourceType,
                type = type,
                diff = diff
        )
    }

    /**
     * Squash the changes by merging them two at a time.
     */
    fun squash(localChanges: List<LocalChange>): LocalChange =
        localChanges.reduce { first, second -> mergeLocalChanges(first, second) }

    @Query(
        """
        SELECT *
        FROM LocalChange
        WHERE LocalChange.resourceId = (:resourceId)
        AND LocalChange.resourceType  = (:resourceType)
        ORDER BY LocalChange.timestamp ASC"""
    )
    abstract fun getLocalChanges(resourceId: String, resourceType: String): List<LocalChange>

    @Query(
        """
        SELECT *
        FROM LocalChange
        ORDER BY LocalChange.timestamp ASC"""
    )
    abstract fun getAllLocalChanges(): List<LocalChange>

    @Query(
        """
        DELETE FROM LocalChange
        WHERE LocalChange.resourceId = (:resourceId)
        AND LocalChange.resourceType  = (:resourceType)
    """
    )
    abstract fun discardLocalChanges(resourceId: String, resourceType: String)

    class InvalidLocalChangeException(message: String?) : Exception(message)
}
