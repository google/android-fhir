/*
 * Copyright 2021 Google LLC
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
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import androidx.room.withTransaction
import androidx.sqlite.db.SimpleSQLiteQuery
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.DatabaseErrorStrategy
import com.google.android.fhir.db.ResourceNotFoundException
import com.google.android.fhir.db.impl.DatabaseImpl.Companion.UNENCRYPTED_DATABASE_NAME
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.db.impl.dao.LocalChangeUtils
import com.google.android.fhir.db.impl.dao.SquashedLocalChange
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.db.impl.entities.ResourceEntity
import com.google.android.fhir.db.impl.entities.SyncedResourceEntity
import com.google.android.fhir.logicalId
import com.google.android.fhir.resource.getResourceType
import com.google.android.fhir.search.SearchQuery
import java.time.Instant
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/**
 * The implementation for the persistence layer using Room. See docs for
 * [com.google.android.fhir.db.Database] for the API docs.
 */
@Suppress("UNCHECKED_CAST")
internal class DatabaseImpl(
  private val context: Context,
  private val iParser: IParser,
  databaseConfig: DatabaseConfig
) : com.google.android.fhir.db.Database {

  val db: ResourceDatabase

  init {
    val enableEncryption =
      databaseConfig.enableEncryption &&
        DatabaseEncryptionKeyProvider.isDatabaseEncryptionSupported()

    // The detection of unintentional switching of database encryption across releases can't be
    // placed inside withTransaction because the database is opened within withTransaction. The
    // default handling of corruption upon open in the room database is to re-create the database,
    // which is undesirable.
    val unexpectedDatabaseName =
      if (enableEncryption) {
        UNENCRYPTED_DATABASE_NAME
      } else {
        ENCRYPTED_DATABASE_NAME
      }
    check(!context.getDatabasePath(unexpectedDatabaseName).exists()) {
      "Unexpected database, $unexpectedDatabaseName, has already existed. " +
        "Check if you have accidentally enabled / disabled database encryption across releases."
    }

    @SuppressWarnings("NewApi")
    db =
      // Initializes builder with the database file name
      when {
          databaseConfig.inMemory ->
            Room.inMemoryDatabaseBuilder(context, ResourceDatabase::class.java)
          enableEncryption ->
            Room.databaseBuilder(context, ResourceDatabase::class.java, ENCRYPTED_DATABASE_NAME)
          else ->
            Room.databaseBuilder(context, ResourceDatabase::class.java, UNENCRYPTED_DATABASE_NAME)
        }
        .apply {
          // Provide the SupportSQLiteOpenHelper which enables the encryption.
          if (enableEncryption) {
            openHelperFactory {
              SQLCipherSupportHelper(
                it,
                databaseErrorStrategy = databaseConfig.databaseErrorStrategy
              ) { DatabaseEncryptionKeyProvider.getOrCreatePassphrase(DATABASE_PASSPHRASE_NAME) }
            }
          }
        }
        .build()
  }

  private val resourceDao by lazy { db.resourceDao().also { it.iParser = iParser } }
  private val syncedResourceDao = db.syncedResourceDao()
  private val localChangeDao = db.localChangeDao().also { it.iParser = iParser }

  override suspend fun <R : Resource> insert(vararg resource: R): List<String> {
    val logicalIds = mutableListOf<String>()
    db.withTransaction {
      logicalIds.addAll(resourceDao.insertAll(resource.toList()))
      localChangeDao.addInsertAll(resource.toList())
    }
    return logicalIds
  }

  override suspend fun <R : Resource> insertRemote(vararg resource: R) {
    db.withTransaction { resourceDao.insertAll(resource.toList()) }
  }

  override suspend fun <R : Resource> update(vararg resources: R) {
    db.withTransaction { resources.forEach { update(it) } }
  }

  private suspend fun <R : Resource> update(resource: R) {
    val oldResourceEntity = selectEntity(resource.javaClass, resource.logicalId)
    resourceDao.update(resource)
    localChangeDao.addUpdate(oldResourceEntity, resource)
  }

  override suspend fun updateVersionIdAndLastUpdated(
    resourceId: String,
    resourceType: ResourceType,
    versionId: String,
    lastUpdated: Instant
  ) {
    db.withTransaction {
      resourceDao.updateRemoteVersionIdAndLastUpdate(
        resourceId,
        resourceType,
        versionId,
        lastUpdated
      )
    }
  }

  override suspend fun <R : Resource> select(clazz: Class<R>, id: String): R {
    return db.withTransaction {
      val type = getResourceType(clazz)
      resourceDao.getResource(resourceId = id, resourceType = type)?.let {
        iParser.parseResource(clazz, it)
      }
        ?: throw ResourceNotFoundException(type.name, id)
    }
  }

  override suspend fun lastUpdate(resourceType: ResourceType): String? {
    return db.withTransaction { syncedResourceDao.getLastUpdate(resourceType) }
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
      val remoteVersionId: String? =
        try {
          selectEntity(clazz, id).versionId
        } catch (e: ResourceNotFoundException) {
          null
        }
      val type = getResourceType(clazz)
      val rowsDeleted = resourceDao.deleteResource(resourceId = id, resourceType = type)
      if (rowsDeleted > 0)
        localChangeDao.addDelete(
          resourceId = id,
          resourceType = type,
          remoteVersionId = remoteVersionId
        )
    }
  }

  override suspend fun <R : Resource> search(query: SearchQuery): List<R> {
    return db.withTransaction {
      resourceDao
        .getResources(SimpleSQLiteQuery(query.query, query.args.toTypedArray()))
        .map { iParser.parseResource(it) as R }
        .distinctBy { it.id }
    }
  }

  override suspend fun count(query: SearchQuery): Long {
    return db.withTransaction {
      resourceDao.countResources(SimpleSQLiteQuery(query.query, query.args.toTypedArray()))
    }
  }

  /**
   * @returns a list of pairs. Each pair is a token + squashed local change. Each token is a list of
   * [LocalChangeEntity.id] s of rows of the [LocalChangeEntity].
   */
  override suspend fun getAllLocalChanges(): List<SquashedLocalChange> {
    return db.withTransaction {
      localChangeDao.getAllLocalChanges().groupBy { it.resourceId to it.resourceType }.values.map {
        SquashedLocalChange(LocalChangeToken(it.map { it.id }), LocalChangeUtils.squash(it))
      }
    }
  }

  override suspend fun deleteUpdates(token: LocalChangeToken) {
    db.withTransaction { localChangeDao.discardLocalChanges(token) }
  }

  override suspend fun <R : Resource> selectEntity(clazz: Class<R>, id: String): ResourceEntity {
    return db.withTransaction {
      val type = getResourceType(clazz)
      resourceDao.getResourceEntity(resourceId = id, resourceType = type)
        ?: throw ResourceNotFoundException(type.name, id)
    }
  }

  companion object {
    /**
     * The name for unencrypted database.
     *
     * We use a separate name for unencrypted & encrypted database in order to detect any
     * unintentional switching of database encryption across releases. When this happens, we throw
     * [IllegalStateException] so that app developers have a chance to fix the issue.
     */
    const val UNENCRYPTED_DATABASE_NAME = "resources.db"

    /**
     * The name for encrypted database.
     *
     * See [UNENCRYPTED_DATABASE_NAME] for the reason we use a separate name.
     */
    const val ENCRYPTED_DATABASE_NAME = "resources_encrypted.db"

    @VisibleForTesting const val DATABASE_PASSPHRASE_NAME = "fhirEngineDbPassphrase"
  }
}

data class DatabaseConfig(
  val inMemory: Boolean,
  val enableEncryption: Boolean,
  val databaseErrorStrategy: DatabaseErrorStrategy
)
