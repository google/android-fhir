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

package com.google.fhirengine.db.impl

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteQuery
import ca.uhn.fhir.parser.IParser
import ca.uhn.fhir.rest.annotation.Transaction
import com.google.fhirengine.index.FhirIndexer
import com.google.fhirengine.index.ResourceIndices
import java.math.BigDecimal
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

@Database(
        entities = [
            ResourceEntity::class,
            StringIndexEntity::class,
            ReferenceIndexEntity::class,
            CodeIndexEntity::class,
            QuantityIndexEntity::class,
            UriIndexEntity::class,
            SyncedResourceEntity::class
        ],
        version = 1,
        exportSchema = false
)
@TypeConverters(
    DbTypeConverters::class
)
internal abstract class RoomResourceDb : RoomDatabase() {
    abstract fun resourceDao(): ResourceDao
    abstract fun syncedResourceDao(): SyncedResourceDao
}

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
        val entity = ResourceEntity(
            id = 0,
            resourceType = resource.resourceType,
            resourceId = resource.id,
            serializedResource = iParser.encodeResourceToString(resource)
        )
        insertResource(entity)
        val index = fhirIndexer.index(resource)
        updateIndicesForResource(index, entity)
    }

    @Transaction
    open fun insertAll(resources: List<Resource>) {
        resources.forEach { resource ->
            val entity = ResourceEntity(
                id = 0,
                resourceType = resource.resourceType,
                resourceId = resource.id,
                serializedResource = iParser.encodeResourceToString(resource)
            )
            insertResource(entity)
            val index = fhirIndexer.index(resource)
            updateIndicesForResource(index, entity)
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
        index.codeIndices.forEach {
            insertCodeIndex(CodeIndexEntity(
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
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertResource(resource: ResourceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertStringIndex(stringIndexEntity: StringIndexEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertReferenceIndex(referenceIndexEntity: ReferenceIndexEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCodeIndex(codeIndexEntity: CodeIndexEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertQuantityIndex(quantityIndexEntity: QuantityIndexEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertUriIndex(uriIndexEntity: UriIndexEntity)

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
        JOIN CodeIndexEntity
        ON ResourceEntity.resourceType = CodeIndexEntity.resourceType
            AND ResourceEntity.resourceId = CodeIndexEntity.resourceId
        WHERE CodeIndexEntity.resourceType = :resourceType
            AND CodeIndexEntity.index_path = :indexPath
            AND CodeIndexEntity.index_system = :indexSystem
            AND CodeIndexEntity.index_value = :indexValue""")
    abstract fun getResourceByCodeIndex(
      resourceType: String,
      indexPath: String,
      indexSystem: String,
      indexValue: String
    ): List<String>

    @Query("""
        SELECT ResourceEntity.serializedResource
        FROM ResourceEntity
        JOIN QuantityIndexEntity
        ON ResourceEntity.resourceType = QuantityIndexEntity.resourceType
            AND ResourceEntity.resourceId = QuantityIndexEntity.resourceId
        WHERE QuantityIndexEntity.resourceType = :resourceType
            AND QuantityIndexEntity.index_name = :indexName
            AND QuantityIndexEntity.index_path = :indexPath
            AND QuantityIndexEntity.index_system = :indexSystem
            AND QuantityIndexEntity.index_value = :indexValue
            AND QuantityIndexEntity.index_unit = :indexUnit""")
    abstract fun getResourceByQuantityIndex(
      resourceType: String,
      indexName: String,
      indexPath: String,
      indexSystem: String,
      indexValue: BigDecimal,
      indexUnit: String
    ): List<String>

    @Query("""
        SELECT ResourceEntity.serializedResource
        FROM ResourceEntity
        JOIN UriIndexEntity
        ON ResourceEntity.resourceType = UriIndexEntity.resourceType
            AND ResourceEntity.resourceId = UriIndexEntity.resourceId
        WHERE UriIndexEntity.resourceType = :resourceType
            AND UriIndexEntity.index_name = :indexName
            AND UriIndexEntity.index_path = :indexPath
            AND UriIndexEntity.index_uri = :indexUri""")
    abstract fun getResourceByUriIndex(
      resourceType: String,
      indexName: String,
      indexPath: String,
      indexUri: String
    ): List<String>

    @RawQuery
    abstract fun getResources(query: SupportSQLiteQuery): List<String>
}
