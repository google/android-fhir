// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.fhirengine.db.impl

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import androidx.room.Dao
import ca.uhn.fhir.parser.IParser
import com.google.fhirengine.db.ResourceAlreadyExistsInDbException
import com.google.fhirengine.db.ResourceNotFoundInDbException
import com.google.fhirengine.index.FhirIndexer
import com.google.fhirengine.resource.ResourceUtils
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import javax.inject.Inject

@Database(
        entities = [ResourceEntity::class, StringIndexEntity::class, ReferenceIndexEntity::class],
        version = 1,
        exportSchema = false
)
@TypeConverters(
        DbTypeConverters::class
)
abstract class RoomResourceDb : RoomDatabase() {
    abstract fun dao(): com.google.fhirengine.db.impl.Dao
}

@Dao
abstract class Dao {
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
        // TODO Move StringIndices to persistable types
        //  https://github.com/jingtang10/fhir-engine/issues/31
        //  we can either use room-autovalue integration or go w/ embedded data classes.
        //  we may also want to merge them:
        //  https://github.com/jingtang10/fhir-engine/issues/33
        index.stringIndices.forEach {
            insertStringIndex(
                    StringIndexEntity(
                            id = 0,
                            resourceType = entity.resourceType,
                            indexName = it.name(),
                            indexPath = it.path(),
                            indexValue = it.value(),
                            resourceId = entity.resourceId
                    )
            )
        }
        index.referenceIndices.forEach {
            insertReferenceIndex(
                    ReferenceIndexEntity(
                            id = 0,
                            resourceType = entity.resourceType,
                            indexName = it.name(),
                            indexPath = it.path(),
                            indexValue = it.value(),
                            resourceId = entity.resourceId
                    )
            )
        }
    }

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract fun insertResource(resource: ResourceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertStringIndex(stringIndexEntity: StringIndexEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertReferenceIndex(referenceIndexEntity: ReferenceIndexEntity)

    @Query("DELETE FROM ResourceEntity WHERE resourceId = :resourceId AND resourceType = :resourceType")
    abstract fun deleteResource(
            resourceId: String,
            resourceType: ResourceType
    )

    @Query("SELECT serializedResource FROM ResourceEntity WHERE resourceId = :resourceId AND resourceType = :resourceType")
    abstract fun getResource(
            resourceId: String,
            resourceType: ResourceType
    ): String?
}


class DatabaseImpl @Inject constructor(
        context: Context,
        private val iParser: IParser,
        fhirIndexer: FhirIndexer
) : com.google.fhirengine.db.Database {
    val db = Room
            .inMemoryDatabaseBuilder(context, RoomResourceDb::class.java)
            // TODO https://github.com/jingtang10/fhir-engine/issues/32
            //  don't allow main thread queries
            .allowMainThreadQueries()
            .build()
    val dao by lazy {
        db.dao().also {
            it.fhirIndexer = fhirIndexer
            it.iParser = iParser
        }
    }

    override fun <R : Resource> insert(resource: R) {
        try {
            dao.insert(resource)
        } catch (constraintException : SQLiteConstraintException) {
            throw ResourceAlreadyExistsInDbException(
                    resource.resourceType.name,
                    resource.id,
                    constraintException
            )
        }
    }

    override fun <R : Resource> update(resource: R) {
        dao.update(resource)
    }

    override fun <R : Resource> select(clazz: Class<R>, id: String): R {
        val type = ResourceUtils.getResourceType(clazz)
        return dao.getResource(
                resourceId = id,
                resourceType = type
        )?.let {
            iParser.parseResource(clazz, it)
        } ?: throw ResourceNotFoundInDbException(type.name, id)
    }

    override fun <R : Resource> delete(clazz: Class<R>, id: String) {
        val type = ResourceUtils.getResourceType(clazz)
        dao.deleteResource(
                resourceId = id,
                resourceType = type
        )
    }
}
