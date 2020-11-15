package com.google.fhirengine.db.impl.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ca.uhn.fhir.parser.IParser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jsonpatch.JsonPatch
import com.github.fge.jsonpatch.diff.JsonDiff
import com.google.fhirengine.db.impl.entities.LocalChange
import com.google.fhirengine.db.impl.entities.LocalChange.Type
import com.google.fhirengine.toTimeZoneString
import org.hl7.fhir.r4.model.Resource
import java.util.Date


@Dao
internal abstract class LocalChangeDao {

    lateinit var iParser: IParser

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun addLocalChange(localChange: LocalChange)

    private fun addLocalInsert(resource: Resource) {
        val resourceId = resource.id
        val resourceType = resource.resourceType
        val localChanges = getLocalChanges(
                resourceId = resourceId,
                resourceType = resourceType.name)
        val timestamp = Date().toTimeZoneString()
        val resourceString = iParser.encodeResourceToString(resource)

        if (localChanges.isEmpty()
                || localChanges.last().type == Type.DELETE) {
            // Insert this change in the local changes table
            addLocalChange(LocalChange(
                    id = 0,
                    resourceType = resourceType,
                    resourceId = resourceId,
                    timestamp = timestamp,
                    type = LocalChange.Type.INSERT,
                    diff = resourceString
            ))
        } else {
            // Can't add an INSERT on top of an INSERT or UPDATE
            throw InvalidLocalChangeException("Can not INSERT on top of $localChanges.last().type")
        }
    }

    private fun addLocalUpdate(resource: Resource) {
        val resourceId = resource.id
        val resourceType = resource.resourceType
        val localChanges = getLocalChanges(
                resourceId = resourceId,
                resourceType = resourceType.name)
        val timestamp = Date().toTimeZoneString()

        if (localChanges.isEmpty()
                || localChanges.last().type in arrayOf(Type.UPDATE, Type.INSERT)) {
            // squash all changes to get the resource to diff against
            val squashedLocalChanges = squash(localChanges)

            if (squashedLocalChanges.type.equals(Type.DELETE))
                throw InvalidLocalChangeException("Unexpected DELETE when squashing $resourceType.name/$resourceId. UPDATE failed")

            val squashedResource = iParser.parseResource(squashedLocalChanges.diff) as Resource

            // insert the diff as an update
            addLocalChange(LocalChange(
                    id = 0,
                    resourceType = resourceType,
                    resourceId = resourceId,
                    timestamp = timestamp,
                    type = Type.UPDATE,
                    diff = diff(squashedResource, resource)
            ))

        } else {
            throw InvalidLocalChangeException("Can not UPDATE on top of $localChanges.type")
        }
    }

    private fun deleteLocalChange(resource: Resource) {
        val resourceId = resource.id
        val localChanges = getLocalChanges(
                resourceId = resourceId,
                resourceType = resource.resourceType.name)

        if (localChanges.isEmpty())
            throw InvalidLocalChangeException("Can not DELETE non-existent resource $resource.resourceType.name/$resourceId")

        val timestamp = Date().toTimeZoneString()
        val resourceType = resource.resourceType
        val topChange = localChanges.last()

        if (topChange.type in arrayOf(Type.UPDATE, Type.INSERT)) {
            addLocalChange(LocalChange(
                    id = 0,
                    resourceType = resourceType,
                    resourceId = resourceId,
                    timestamp = timestamp,
                    type = Type.DELETE,
                    diff = ""
            ))
        } else {
            throw InvalidLocalChangeException("Can not DELETE on top of $topChange.type")
        }
    }

    fun squash(localChanges: List<LocalChange>, forSync: Boolean = false): LocalChange {
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
                // assertion $first.type == INSERT
                val first = squash(localChanges.dropLast(1))
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
                JsonNode::class.java)
        val targetJson = objectMapper.readValue(
                iParser.encodeResourceToString(target),
                JsonNode::class.java)
        val jsonDiff = JsonDiff.asJson(sourceJson, targetJson);
        if (jsonDiff.size() == 0) Log.w("ResourceDao", "Trying to UPDATE resource ${target.resourceType}/${target.id} with no changes")
        return jsonDiff.toString()
    }

    @Query("""
        SELECT *
        FROM LocalChange
        WHERE LocalChange.resourceId = (:resourceId)
        AND LocalChange.resourceType  = (:resourceType)
        ORDER BY LocalChange.timestamp ASC""")
    abstract fun getLocalChanges(resourceId: String, resourceType: String): List<LocalChange>

    @Query("""
        DELETE FROM LocalChange
        WHERE LocalChange.resourceId = (:resourceId)
        AND LocalChange.resourceType  = (:resourceType)
    """)
    abstract fun discardLocalChanges(resourceId: String, resourceType: String)

    class InvalidLocalChangeException(message: String?) : Exception(message)
}