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
import androidx.room.withTransaction
import androidx.sqlite.db.SimpleSQLiteQuery
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.db.ResourceNotFoundException
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.db.impl.dao.LocalChangeUtils
import com.google.android.fhir.db.impl.dao.SquashedLocalChange
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.db.impl.entities.SyncedResourceEntity
import com.google.android.fhir.logicalId
import com.google.android.fhir.resource.getResourceType
import com.google.android.fhir.search.SearchQuery
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/**
 * The implementation for the persistence layer using Room. See docs for
 * [com.google.android.fhir.db.Database] for the API docs.
 */
@Suppress("UNCHECKED_CAST")
internal class DatabaseImpl(context: Context, private val iParser: IParser, databaseName: String?) :
  com.google.android.fhir.db.Database {
  constructor(
    context: Context,
    iParser: IParser
  ) : this(context = context, iParser = iParser, databaseName = DEFAULT_DATABASE_NAME)

  val builder =
    if (databaseName == null) {
      Room.inMemoryDatabaseBuilder(context, ResourceDatabase::class.java)
    } else {
      Room.databaseBuilder(context, ResourceDatabase::class.java, databaseName)
    }
  val db =
    builder
      // TODO https://github.com/jingtang10/fhir-engine/issues/32
      //  don't allow main thread queries
      .allowMainThreadQueries()
      .build()
  private val resourceDao by lazy { db.resourceDao().also { it.iParser = iParser } }
  private val syncedResourceDao = db.syncedResourceDao()
  private val localChangeDao = db.localChangeDao().also { it.iParser = iParser }

  override suspend fun <R : Resource> insert(vararg resource: R) {
    db.withTransaction {
      resourceDao.insertAll(resource.toList())
      localChangeDao.addInsertAll(resource.toList())
    }
  }

  override suspend fun <R : Resource> insertRemote(vararg resource: R) {
    resourceDao.insertAll(resource.toList())
  }

  override suspend fun <R : Resource> update(resource: R) {
    db.withTransaction {
      val oldResource = select(resource.javaClass, resource.logicalId)
      resourceDao.update(resource)
      localChangeDao.addUpdate(oldResource, resource)
    }
  }

  override suspend fun <R : Resource> select(clazz: Class<R>, id: String): R {
    val type = getResourceType(clazz)
    return resourceDao.getResource(resourceId = id, resourceType = type)?.let {
      iParser.parseResource(clazz, it)
    }
      ?: throw ResourceNotFoundException(type.name, id)
  }

  override suspend fun lastUpdate(resourceType: ResourceType): String? {
    return syncedResourceDao.getLastUpdate(resourceType)
  }

  override suspend fun insertSyncedResources(
    syncedResources: List<SyncedResourceEntity>,
    resources: List<Resource>
  ) {
    db.withTransaction {
      syncedResourceDao.insertAll(syncedResources)
      insertRemote(*resources.toTypedArray())
    }
  }

  override suspend fun <R : Resource> delete(clazz: Class<R>, id: String) {
    db.withTransaction {
      val type = getResourceType(clazz)
      val rowsDeleted = resourceDao.deleteResource(resourceId = id, resourceType = type)
      if (rowsDeleted > 0) localChangeDao.addDelete(resourceId = id, resourceType = type)
    }
  }

  override suspend fun <R : Resource> search(query: SearchQuery): List<R> =
    resourceDao
      .getResources(SimpleSQLiteQuery(query.query, query.args.toTypedArray()))
      .map { iParser.parseResource(it) as R }
      .distinctBy { it.id }

  override suspend fun count(query: SearchQuery): Long =
    resourceDao.countResources(SimpleSQLiteQuery(query.query, query.args.toTypedArray()))

  /**
   * @returns a list of pairs. Each pair is a token + squashed local change. Each token is a list of
   * [LocalChangeEntity.id] s of rows of the [LocalChangeEntity].
   */
  override suspend fun getAllLocalChanges(): List<SquashedLocalChange> =
    localChangeDao.getAllLocalChanges().groupBy { it.resourceId to it.resourceType }.values.map {
      SquashedLocalChange(LocalChangeToken(it.map { it.id }), LocalChangeUtils.squash(it))
    }

  override suspend fun deleteUpdates(token: LocalChangeToken) {
    localChangeDao.discardLocalChanges(token)
  }

  companion object {
    private const val DEFAULT_DATABASE_NAME = "ResourceDatabase"
  }
}
