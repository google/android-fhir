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
import com.fasterxml.jackson.databind.ObjectReader
import com.github.fge.jsonpatch.JsonPatch
import com.github.fge.jsonpatch.diff.JsonDiff
import com.google.fhirengine.db.impl.entities.LocalChange
import com.google.fhirengine.db.impl.entities.LocalChange.Type
import com.google.fhirengine.toTimeZoneString
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

        if (!localChanges.isEmpty() && localChanges.last().type != Type.DELETE) {
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

        if (localChanges.last().type == Type.DELETE) {
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
        val localChanges = getLocalChanges(
            resourceId = resourceId,
            resourceType = resourceType.name
        )

        val timestamp = Date().toTimeZoneString()

        when {
            localChanges.isEmpty() -> throw InvalidLocalChangeException(
                "Can not DELETE non-existent resource $resourceType/$resourceId"
            )
            localChanges.last().type in arrayOf(Type.UPDATE, Type.INSERT) -> {
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
            else -> {
                val lastType = localChanges.last().type
                throw InvalidLocalChangeException("Can not DELETE on top of $lastType")
            }
        }
    }

    /**
     * Squashes all local changes in to a single local change representing the current state of the
     * resource.
     *
     * Algo:
     * 1. Read the latest change, if it's a DELETE or INSERT then we are done as that's the
     * latest representation of the resource.
     * 2. If it's an UPDATE then squash the rest of the list recursively. Merge the result of the
     * squash using [applyPatch].
     *
     * @throws InvalidLocalChangeException when no INSERT is found in the list.
     */
    fun squashOld(localChanges: List<LocalChange>): LocalChange {

        if (localChanges.isEmpty())
            throw InvalidLocalChangeException("Could not find INSERT in local changes.")

        val last = localChanges.last()
        return when (last.type) {
            Type.DELETE -> LocalChange(
                resourceId = last.resourceId,
                resourceType = last.resourceType,
                type = Type.DELETE,
                diff = ""
            )
            Type.INSERT -> LocalChange(
                resourceId = last.resourceId,
                resourceType = last.resourceType,
                type = Type.INSERT,
                diff = last.diff
            )
            Type.UPDATE -> {
                val first = squashOld(localChanges.dropLast(1))

                // assertion $first.type != DELETE
                if (first.type == Type.DELETE) {
                    throw InvalidLocalChangeException(
                        "Expected INSERT/UPDATE at head of local changes. Found DELETE instead."
                    )
                }

                LocalChange(
                    resourceId = last.resourceId,
                    resourceType = last.resourceType,
                    type = Type.INSERT,
                    diff = applyPatch(first.diff, last.diff)
                )
            }
        }
    }

    fun squash(localChanges: List<LocalChange>): LocalChange =
        localChanges.reduce {first, second -> mergeLocalChanges(first, second)}

    private fun applyPatch(resourceString: String, patchString: String): String {
        val objectMapper = ObjectMapper()
        val resourceJson = objectMapper.readValue(resourceString, JsonNode::class.java)
        val patchJson = objectMapper.readValue(patchString, JsonPatch::class.java)
        return patchJson.apply(resourceJson).toString()
    }

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

    private fun mergeLocalChanges(first: LocalChange, second: LocalChange): LocalChange {
        return LocalChange(
            resourceId = second.resourceId,
            resourceType = second.resourceType,
            type = Type.UPDATE,
            diff = mergePatches(first.diff, second.diff)
        )
    }
    private fun mergePatches(firstPatch: String, secondPatch: String): String {
        val objectMapper = ObjectMapper()
        // TODO: validate patches are RFC 6902 compliant JSON patches
        val first = objectMapper.readValue(firstPatch, JsonPatch::class.java)
        val updater = objectMapper.readerForUpdating(first)
        val merged = updater.readValue(secondPatch, JsonPatch::class.java)
        return merged.toString()
    }

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
