package com.google.fhirengine.db.roomimpl

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
        entities = [ResourceEntity::class, StringIndexEntity::class, ReferenceIndexEntitiy::class],
        version = 1,
        exportSchema = false
)
@TypeConverters(
        DbTypeConverters::class
)
abstract class RoomResourceDb : RoomDatabase() {
    abstract fun dao(): com.google.fhirengine.db.roomimpl.Dao
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
                    ReferenceIndexEntitiy(
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
    abstract fun insertReferenceIndex(referenceIndexEntity: ReferenceIndexEntitiy)

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


class RoomDbImpl @Inject constructor(
        context: Context,
        private val iParser: IParser,
        fhirIndexer: FhirIndexer
) : com.google.fhirengine.db.Database {
    val db = Room
            .inMemoryDatabaseBuilder(context, RoomResourceDb::class.java)
            .allowMainThreadQueries() // we shouldn't do this but robolectric ¯\_(ツ)_/¯
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