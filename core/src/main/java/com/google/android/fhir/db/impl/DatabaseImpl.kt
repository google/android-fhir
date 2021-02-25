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

package com.google.android.fhir.db.impl

import android.content.Context
import androidx.room.Room
import androidx.room.Transaction
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.db.ResourceNotFoundInDbException
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.db.impl.dao.LocalChangeUtils
import com.google.android.fhir.db.impl.entities.LocalChange
import com.google.android.fhir.db.impl.entities.SyncedResourceEntity
import com.google.android.fhir.resource.getResourceType
import com.google.android.fhir.search.impl.Query
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/**
 * The implementation for the persistence layer using Room.
 * See docs for [com.google.android.fhir.db.Database] for the API docs.
 */
internal class DatabaseImpl(
    context: Context,
    private val iParser: IParser,
    databaseName: String?
) : com.google.android.fhir.db.Database {
    constructor(
        context: Context,
        iParser: IParser,
    ) : this(
        context = context,
        iParser = iParser,
        databaseName = DEFAULT_DATABASE_NAME)

    val builder = if (databaseName == null) {
        Room.inMemoryDatabaseBuilder(context, ResourceDatabase::class.java)
    } else {
        Room.databaseBuilder(context, ResourceDatabase::class.java, databaseName)
    }
    val db = builder
        // TODO https://github.com/jingtang10/fhir-engine/issues/32
        //  don't allow main thread queries
        .allowMainThreadQueries()
        .build()
    val resourceDao by lazy {
        db.resourceDao().also {
            it.iParser = iParser
        }
    }
    val syncedResourceDao = db.syncedResourceDao()
    val localChangeDao = db.localChangeDao().also {
        it.iParser = iParser
    }

    @Transaction
    override fun <R : Resource> insert(resource: R) {
        resourceDao.insert(resource)
        localChangeDao.addInsert(resource)
    }

    override fun <R : Resource> insertRemote(resource: R) {
        resourceDao.insert(resource)
    }

    @Transaction
    override fun <R : Resource> insertAll(resources: List<R>) {
        resourceDao.insertAll(resources)
        localChangeDao.addInsertAll(resources)
    }

    override fun <R : Resource> insertAllRemote(resources: List<R>) {
        resourceDao.insertAll(resources)
    }

    @Transaction
    override fun <R : Resource> update(resource: R) {
        val oldResource = select(resource.javaClass, resource.id)
        resourceDao.update(resource)
        localChangeDao.addUpdate(oldResource, resource)
    }

    override fun <R : Resource> select(clazz: Class<R>, id: String): R {
        val type = getResourceType(clazz)
        return resourceDao.getResource(
            resourceId = id,
            resourceType = type
        )?.let {
            iParser.parseResource(clazz, it)
        } ?: throw ResourceNotFoundInDbException(type.name, id)
    }

    override suspend fun lastUpdate(resourceType: ResourceType): String? {
        return syncedResourceDao.getLastUpdate(resourceType)
    }

    @Transaction
    override suspend fun insertSyncedResources(
        syncedResourceEntity: SyncedResourceEntity,
        resources: List<Resource>
    ) {
        syncedResourceDao.insert(syncedResourceEntity)
        insertAll(resources)
    }

    @Transaction
    override fun <R : Resource> delete(clazz: Class<R>, id: String) {
        val type = getResourceType(clazz)
        val rowsDeleted = resourceDao.deleteResource(
            resourceId = id,
            resourceType = type
        )
        if (rowsDeleted > 0) localChangeDao.addDelete(resourceId = id, resourceType = type)
    }

    override fun <R : Resource> searchByReference(
        clazz: Class<R>,
        reference: String,
        value: String
    ): List<R> {
        return resourceDao.getResourceByReferenceIndex(
            getResourceType(clazz).name, reference, value)
            .map { iParser.parseResource(it) as R }
    }

    override fun <R : Resource> searchByString(
        clazz: Class<R>,
        string: String,
        value: String
    ): List<R> {
        return resourceDao.getResourceByStringIndex(
            resourceType = getResourceType(clazz).name,
            indexPath = string,
            indexValue = value
        ).map { iParser.parseResource(it) as R }
    }

    override fun <R : Resource> searchByCode(
        clazz: Class<R>,
        code: String,
        system: String,
        value: String
    ): List<R> {
        return resourceDao.getResourceByCodeIndex(
            resourceType = getResourceType(clazz).name,
            indexPath = code,
            indexSystem = system,
            indexValue = value
        ).map { iParser.parseResource(it) as R }
    }

    override fun <R : Resource> searchByReferenceAndCode(
        clazz: Class<R>,
        reference: String,
        referenceValue: String,
        code: String,
        codeSystem: String,
        codeValue: String
    ): List<R> {
        val refs = searchByReference(clazz, reference, referenceValue).map { it.id }
        return searchByCode(clazz, code, codeSystem, codeValue).filter { refs.contains(it.id) }
    }

    override fun <R : Resource> search(query: Query): List<R> =
        resourceDao.getResources(query.getSupportSQLiteQuery())
            .map { iParser.parseResource(it) as R }

    /**
     * @returns a list of pairs. Each pair is a token + squashed local change. Each token is a list
     * of [LocalChange.id]s of rows of the [LocalChange].
     */
    // TODO: create a data class for squashed local change and merge token in to it.
    override fun getAllLocalChanges(): List<Pair<LocalChangeToken, LocalChange>> =
        localChangeDao.getAllLocalChanges().groupBy { it.resourceId to it.resourceType }
            .values
            .map {
                LocalChangeToken(it.map { it.id }) to LocalChangeUtils.squash(it)
            }

    override fun deleteUpdates(token: LocalChangeToken) {
        localChangeDao.discardLocalChanges(token)
    }

    companion object {
        private const val DEFAULT_DATABASE_NAME = "ResourceDatabase"
    }
}
