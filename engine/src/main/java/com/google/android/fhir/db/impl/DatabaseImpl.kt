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
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import androidx.sqlite.db.SimpleSQLiteQuery
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.DatabaseErrorStrategy
import com.google.android.fhir.db.DatabaseEncryptionException
import com.google.android.fhir.db.DatabaseEncryptionException.DatabaseEncryptionErrorCode.TIMEOUT
import com.google.android.fhir.db.ResourceNotFoundException
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.db.impl.dao.LocalChangeUtils
import com.google.android.fhir.db.impl.dao.SquashedLocalChange
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.db.impl.entities.SyncedResourceEntity
import com.google.android.fhir.logicalId
import com.google.android.fhir.resource.getResourceType
import com.google.android.fhir.search.SearchQuery
import java.time.Duration
import kotlinx.coroutines.delay
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/**
 * The implementation for the persistence layer using Room. See docs for
 * [com.google.android.fhir.db.Database] for the API docs.
 */
@Suppress("UNCHECKED_CAST")
internal class DatabaseImpl(
  context: Context,
  private val iParser: IParser,
  databaseConfig: DatabaseConfig
) : com.google.android.fhir.db.Database {

  val builder =
    if (databaseConfig.inMemory) {
      Room.inMemoryDatabaseBuilder(context, ResourceDatabase::class.java)
    } else {
      Room.databaseBuilder(context, ResourceDatabase::class.java, DEFAULT_DATABASE_NAME)
    }
  val db: ResourceDatabase

  init {
    if (databaseConfig.enableEncryption &&
        DatabaseEncryptionKeyProvider.isDatabaseEncryptionSupported()
    ) {
      builder.openHelperFactory {
        SQLCipherSupportHelper(it, databaseErrorStrategy = databaseConfig.databaseErrorStrategy) {
          DatabaseEncryptionKeyProvider.getOrCreatePassphrase(DATABASE_PASSPHRASE_NAME)
        }
      }
    }
    db = builder.build()
  }
  private val resourceDao by lazy { db.resourceDao().also { it.iParser = iParser } }
  private val syncedResourceDao = db.syncedResourceDao()
  private val localChangeDao = db.localChangeDao().also { it.iParser = iParser }

  override suspend fun <R : Resource> insert(vararg resource: R) {
    db.withWrappedTransaction {
      resourceDao.insertAll(resource.toList())
      localChangeDao.addInsertAll(resource.toList())
    }
  }

  override suspend fun <R : Resource> insertRemote(vararg resource: R) {
    resourceDao.insertAll(resource.toList())
  }

  override suspend fun <R : Resource> update(resource: R) {
    db.withWrappedTransaction {
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
    db.withWrappedTransaction {
      syncedResourceDao.insertAll(syncedResources)
      insertRemote(*resources.toTypedArray())
    }
  }

  override suspend fun <R : Resource> delete(clazz: Class<R>, id: String) {
    db.withWrappedTransaction {
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
    private const val DEFAULT_DATABASE_NAME = "fhirEngine"

    @VisibleForTesting const val DATABASE_PASSPHRASE_NAME = "fhirEngine_db_passphrase"
  }
}

data class DatabaseConfig(
  val inMemory: Boolean,
  val enableEncryption: Boolean,
  val databaseErrorStrategy: DatabaseErrorStrategy
)

suspend fun <R> RoomDatabase.withWrappedTransaction(
  retryAttempt: Int = 0,
  block: suspend () -> R
): R {
  require(retryAttempt >= 0) { "$LOG_TAG: Database retry attempt must not be a negative integer" }
  return try {
    withTransaction(block)
  } catch (exception: DatabaseEncryptionException) {
    if (exception.errorCode == TIMEOUT) {
      if (retryAttempt > MAX_TIMEOUT_RETRIES) {
        Log.w(LOG_TAG, "Can't access the database encryption key after $retryAttempt attempts.")
        throw exception
      } else {
        Log.i(LOG_TAG, "Fail to get the encryption key on attempt: $retryAttempt")
        delay(retryDelay.toMillis() * retryAttempt)
        withWrappedTransaction(retryAttempt + 1, block)
      }
    } else {
      // TODO: recreate database per the caller request
      throw exception
    }
  }
}

private const val LOG_TAG = "Fhir-DatabaseImpl"

/** Maximum number of retries after a database operation timeout. */
private const val MAX_TIMEOUT_RETRIES = 3

/** The time delay before retrying a database operation. */
private val retryDelay = Duration.ofSeconds(1)
