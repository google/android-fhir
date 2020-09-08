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

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import ca.uhn.fhir.parser.IParser
import ca.uhn.fhir.rest.annotation.Transaction
import com.google.fhirengine.Util.toTimeZoneString
import com.google.fhirengine.db.InvalidLocalChangeException
import com.google.fhirengine.db.impl.entities.DateIndexEntity
import com.google.fhirengine.db.impl.entities.LocalChange
import com.google.fhirengine.db.impl.entities.NumberIndexEntity
import com.google.fhirengine.db.impl.entities.QuantityIndexEntity
import com.google.fhirengine.db.impl.entities.ReferenceIndexEntity
import com.google.fhirengine.db.impl.entities.ResourceEntity
import com.google.fhirengine.db.impl.entities.StringIndexEntity
import com.google.fhirengine.db.impl.entities.TokenIndexEntity
import com.google.fhirengine.db.impl.entities.UriIndexEntity
import com.google.fhirengine.index.FhirIndexer
import com.google.fhirengine.index.ResourceIndices
import com.google.fhirengine.sync.model.Update.Type
import java.util.Date
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

@Dao
internal abstract class ResourceDao {
    // this is ugly but there is no way to inject these right now in Room as it is the one creating
    // the dao
    lateinit var fhirIndexer: FhirIndexer
    lateinit var iParser: IParser

    @Transaction
    open fun update(resource: Resource) {
        deleteResource(resource.id, resource.resourceType)
        insert(resource)
    }

    @Transaction
    open fun insert(resource: Resource) {
        insertResource(resource)
    }

    @Transaction
    open fun insertAll(resources: List<Resource>) {
        resources.forEach { resource ->
            insertResource(resource)
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertResource(resource: ResourceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertStringIndex(stringIndexEntity: StringIndexEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertReferenceIndex(referenceIndexEntity: ReferenceIndexEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCodeIndex(tokenIndexEntity: TokenIndexEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertQuantityIndex(quantityIndexEntity: QuantityIndexEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertUriIndex(uriIndexEntity: UriIndexEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertDateIndex(dateIndexEntity: DateIndexEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertNumberIndex(numberIndexEntity: NumberIndexEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertLocalChange(localChange: LocalChange)

    @Query("""
        DELETE FROM ResourceEntity
        WHERE resourceId = :resourceId AND resourceType = :resourceType""")
    abstract fun deleteResource(
      resourceId: String,
      resourceType: ResourceType
    )

    @Query("""
        SELECT serializedResource
        FROM ResourceEntity
        WHERE resourceId = :resourceId AND resourceType = :resourceType""")
    abstract fun getResource(
      resourceId: String,
      resourceType: ResourceType
    ): String?

    @Query("""
        SELECT ResourceEntity.serializedResource
        FROM ResourceEntity 
        JOIN ReferenceIndexEntity
        ON ResourceEntity.resourceType = ReferenceIndexEntity.resourceType
            AND ResourceEntity.resourceId = ReferenceIndexEntity.resourceId
        WHERE ReferenceIndexEntity.resourceType = :resourceType
            AND ReferenceIndexEntity.index_path = :indexPath
            AND ReferenceIndexEntity.index_value = :indexValue""")
    abstract fun getResourceByReferenceIndex(
      resourceType: String,
      indexPath: String,
      indexValue: String
    ): List<String>

    @Query("""
        SELECT ResourceEntity.serializedResource
        FROM ResourceEntity
        JOIN StringIndexEntity
        ON ResourceEntity.resourceType = StringIndexEntity.resourceType
            AND ResourceEntity.resourceId = StringIndexEntity.resourceId
        WHERE StringIndexEntity.resourceType = :resourceType
            AND StringIndexEntity.index_path = :indexPath
            AND StringIndexEntity.index_value = :indexValue""")
    abstract fun getResourceByStringIndex(
      resourceType: String,
      indexPath: String,
      indexValue: String
    ): List<String>

    @Query("""
        SELECT ResourceEntity.serializedResource
        FROM ResourceEntity
        JOIN TokenIndexEntity
        ON ResourceEntity.resourceType = TokenIndexEntity.resourceType
            AND ResourceEntity.resourceId = TokenIndexEntity.resourceId
        WHERE TokenIndexEntity.resourceType = :resourceType
            AND TokenIndexEntity.index_path = :indexPath
            AND TokenIndexEntity.index_system = :indexSystem
            AND TokenIndexEntity.index_value = :indexValue""")
    abstract fun getResourceByCodeIndex(
      resourceType: String,
      indexPath: String,
      indexSystem: String,
      indexValue: String
    ): List<String>

    @RawQuery
    abstract fun getResources(query: SupportSQLiteQuery): List<String>

    private fun insertResource(resource: Resource) {
        val localChanges = allLocalChanges(
                resourceId = resource.id,
                resourceType = resource.resourceType.name)
        val timestamp = Date().toTimeZoneString()
        val resourceType = resource.resourceType
        val resourceId = resource.id
        val resourceString = iParser.encodeResourceToString(resource)

        // There will be no prior LocalChange if it's the first time we're storing this resource.
        if (localChanges.isEmpty()) {
            val entity = ResourceEntity(
                    id = 0,
                    resourceType = resourceType,
                    resourceId = resourceId,
                    serializedResource = resourceString,
                    lastUpdatedLocally = timestamp
            )
            insertResource(entity)
            val index = fhirIndexer.index(resource)
            updateIndicesForResource(index, entity)
        } else {
            val topChange = localChanges.last()

            // Can't add another INSERT on top of an INSERT or UPDATE
            if (topChange.type !in arrayOf(Type.INSERT, Type.UPDATE)) {
                if (topChange.type.equals(Type.DELETE)) {
                    // Update the last changed timestamp in the resource table
                    updateLocalChangeTimestamp(
                            resourceId = resourceId,
                            resourceType = resourceType,
                            ts = timestamp
                    )
                    // Insert this change in the local changes table
                    insertLocalChange(
                            LocalChange(
                                    id = 0,
                                    resourceType = resourceType,
                                    resourceId = resourceId,
                                    timestamp = timestamp,
                                    type = Type.INSERT,
                                    diff = resourceString
                            )
                    )
                }
            } else {
                throw InvalidLocalChangeException("Can not add INSERT on top of $topChange.type")
            }
        }
    }

    private fun updateIndicesForResource(index: ResourceIndices, resource: ResourceEntity) {
        // TODO Move StringIndices to persistable types
        //  https://github.com/jingtang10/fhir-engine/issues/31
        //  we can either use room-autovalue integration or go w/ embedded data classes.
        //  we may also want to merge them:
        //  https://github.com/jingtang10/fhir-engine/issues/33
        index.stringIndices.forEach {
            insertStringIndex(
                    StringIndexEntity(
                            id = 0,
                            resourceType = resource.resourceType,
                            index = it,
                            resourceId = resource.resourceId
                    )
            )
        }
        index.referenceIndices.forEach {
            insertReferenceIndex(
                    ReferenceIndexEntity(
                            id = 0,
                            resourceType = resource.resourceType,
                            index = it,
                            resourceId = resource.resourceId
                    )
            )
        }
        index.tokenIndices.forEach {
            insertCodeIndex(TokenIndexEntity(
                    id = 0,
                    resourceType = resource.resourceType,
                    index = it,
                    resourceId = resource.resourceId))
        }
        index.quantityIndices.forEach {
            insertQuantityIndex(QuantityIndexEntity(
                    id = 0,
                    resourceType = resource.resourceType,
                    index = it,
                    resourceId = resource.resourceId))
        }
        index.uriIndices.forEach {
            insertUriIndex(UriIndexEntity(
                    id = 0,
                    resourceType = resource.resourceType,
                    index = it,
                    resourceId = resource.resourceId))
        }
        index.dateIndices.forEach {
            insertDateIndex(DateIndexEntity(
                    id = 0,
                    resourceType = resource.resourceType,
                    index = it,
                    resourceId = resource.resourceId))
        }
        index.numberIndices.forEach {
            insertNumberIndex(NumberIndexEntity(
                    id = 0,
                    resourceType = resource.resourceType,
                    index = it,
                    resourceId = resource.resourceId))
        }
    }

    @Query("""
        SELECT *
        FROM LocalChange
        WHERE LocalChange.resourceId = (:resourceId)
        AND LocalChange.resourceType  = (:resourceType)
        ORDER BY LocalChange.timestamp ASC""")
    abstract fun allLocalChanges(resourceId: String, resourceType: String): List<LocalChange>

    @Query("""
        UPDATE ResourceEntity
        SET lastUpdatedLocally = :ts
        WHERE resourceType = :resourceType
        AND resourceId = :resourceId""")
    abstract fun updateLocalChangeTimestamp(
      resourceId: String,
      resourceType: ResourceType,
      ts: String
    )
}
