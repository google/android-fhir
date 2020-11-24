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
import java.util.Date
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/**
 * Dao for local changes made to a resource. One row in LocalChangeEntity corresponds to one change
 * e.g. an INSERT or UPDATE. The UPDATES (diffs) are stored as RFC 6902 JSON patches.
 * When a ResourceEntity is read, all corresponding LocalChanges are 'squashed' to create a
 * final representation of the resource.
 *
 * Can be potentially used for implementing resource editing with undo/redo.
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

        if (localChanges.isEmpty() ||
            localChanges.last().type == Type.DELETE
        ) {
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
        } else {
            // Can't add an INSERT on top of an INSERT or UPDATE
            throw InvalidLocalChangeException("Can not INSERT on top of $localChanges.last().type")
        }
    }

    fun addUpdate(resource: Resource) {
        val resourceId = resource.id
        val resourceType = resource.resourceType
        val localChanges = getLocalChanges(
            resourceId = resourceId,
            resourceType = resourceType.name
        )
        val timestamp = Date().toTimeZoneString()

        if (localChanges.isEmpty()) {
            // TODO retrieve from resource dao???
        } else if (localChanges.last().type in arrayOf(Type.UPDATE, Type.INSERT)) {
            // squash all changes to get the resource to diff against
            val squashedLocalChanges = squash(localChanges)

            if (squashedLocalChanges.type.equals(Type.DELETE))
                throw InvalidLocalChangeException(
                    "Unexpected DELETE when squashing $resourceType.name/$resourceId.UPDATE failed"
                )

            val squashedResource = iParser.parseResource(squashedLocalChanges.diff) as Resource

            // insert the diff as an update
            addLocalChange(
                LocalChange(
                    id = 0,
                    resourceType = resourceType.name,
                    resourceId = resourceId,
                    timestamp = timestamp,
                    type = Type.UPDATE,
                    diff = diff(squashedResource, resource)
                )
            )
        } else {
            throw InvalidLocalChangeException("Can not UPDATE on top of $localChanges.type")
        }
    }

    fun addDelete(resourceId: String, resourceType: ResourceType) {
        val localChanges = getLocalChanges(
            resourceId = resourceId,
            resourceType = resourceType.name
        )

        if (localChanges.isEmpty())
            throw InvalidLocalChangeException(
                "Can not DELETE non-existent resource $resourceType/$resourceId"
            )

        val timestamp = Date().toTimeZoneString()
        val topChange = localChanges.last()

        if (topChange.type in arrayOf(Type.UPDATE, Type.INSERT)) {
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
        } else {
            throw InvalidLocalChangeException("Can not DELETE on top of $topChange.type")
        }
    }

    /**
     * Merges all local changes in to a single local change representing the current state of the
     * resource.
     *
     * Algo:
     * 1. Read the latest change, if it's a DELETE or INSERT then we are done as that's the
     * latest representation of the resource.
     * 2. If it's an UPDATE then squash the rest of the list recursively. Merge the result of the
     * squash using [applyPatch].
     */
    fun squash(localChanges: List<LocalChange>): LocalChange {

        val last = localChanges.last()

        // Special case to handle remote-created resource which was
        // updated locally
        if (localChanges.size == 1 && last.type == Type.UPDATE) {
            // TODO retrieve resource from ResourceEntity and
            //  return as INSERT for local changes
        }

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
                val first = squash(localChanges.dropLast(1))

                // assertion $first.type == INSERT
                if (first.type != Type.INSERT) {
                    throw InvalidLocalChangeException(
                        "Expected INSERT at head of local changes. Found ${first.type} instead."
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
