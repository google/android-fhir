/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir.json.db.impl

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import androidx.room.withTransaction
import com.google.android.fhir.json.DatabaseErrorStrategy
import com.google.android.fhir.json.db.Database
import com.google.android.fhir.json.db.ResourceNotFoundException
import com.google.android.fhir.json.db.impl.dao.LocalChangeToken
import com.google.android.fhir.json.db.impl.dao.LocalChangeUtils
import com.google.android.fhir.json.db.impl.dao.SquashedLocalChange
import com.google.android.fhir.json.db.impl.entities.JsonObjectEntity
import java.time.Instant
import org.json.JSONObject

/**
 * The implementation for the persistence layer using Room. See docs for
 * [com.google.android.fhir.db.Database] for the API docs.
 */
@Suppress("UNCHECKED_CAST")
internal class DatabaseImpl(context: Context, databaseConfig: DatabaseConfig) : Database {

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

  private val resourceDao by lazy { db.resourceDao() }
  private val localChangeDao = db.localChangeDao()

  override suspend fun insert(vararg resource: JSONObject): List<String> {
    val logicalIds = mutableListOf<String>()
    db.withTransaction {
      logicalIds.addAll(resourceDao.insertAll(resource.toList()))
      localChangeDao.addInsertAll(resource.toList())
    }
    return logicalIds
  }

  override suspend fun insertRemote(vararg resource: JSONObject) {
    db.withTransaction { resourceDao.insertAll(resource.toList()) }
  }

  override suspend fun update(vararg resources: JSONObject) {
    db.withTransaction {
      resources.forEach {
        val oldResourceEntity = selectEntity(it["id"].toString())
        resourceDao.update(it)
        localChangeDao.addUpdate(oldResourceEntity, it)
      }
    }
  }

  override suspend fun updateVersionIdAndLastUpdated(resourceId: String, lastUpdated: Instant) {
    db.withTransaction { resourceDao.updateRemoteVersionIdAndLastUpdate(resourceId, lastUpdated) }
  }

  override suspend fun select(id: String): JSONObject {
    return db.withTransaction {
      resourceDao.getResource(id)?.let { JSONObject(it) } ?: throw ResourceNotFoundException(id)
    }
  }

  override suspend fun insertSyncedResources(resources: List<JSONObject>) {
    db.withTransaction { insertRemote(*resources.toTypedArray()) }
  }

  override suspend fun delete(id: String) {
    db.withTransaction {
      val rowsDeleted = resourceDao.deleteResource(resourceId = id)
      if (rowsDeleted > 0)
        localChangeDao.addDelete(
          resourceId = id,
        )
    }
  }

  /**
   * @returns a list of pairs. Each pair is a token + squashed local change. Each token is a list of
   * [LocalChangeEntity.id] s of rows of the [LocalChangeEntity].
   */
  override suspend fun getAllLocalChanges(): List<SquashedLocalChange> {
    return db.withTransaction {
      localChangeDao.getAllLocalChanges().groupBy { it.resourceId }.values.map {
        SquashedLocalChange(LocalChangeToken(it.map { it.id }), LocalChangeUtils.squash(it))
      }
    }
  }

  override suspend fun deleteUpdates(token: LocalChangeToken) {
    db.withTransaction { localChangeDao.discardLocalChanges(token) }
  }

  override suspend fun selectEntity(id: String): JsonObjectEntity {
    return db.withTransaction {
      resourceDao.getResourceEntity(resourceId = id) ?: throw ResourceNotFoundException(id)
    }
  }

  override suspend fun withTransaction(block: suspend () -> Unit) {
    db.withTransaction(block)
  }

  override suspend fun deleteUpdates(resources: List<JSONObject>) {
    localChangeDao.discardLocalChanges(resources)
  }

  override fun close() {
    db.close()
  }

  companion object {
    /**
     * The name for unencrypted database.
     *
     * We use a separate name for unencrypted & encrypted database in order to detect any
     * unintentional switching of database encryption across releases. When this happens, we throw
     * [IllegalStateException] so that app developers have a chance to fix the issue.
     */
    const val UNENCRYPTED_DATABASE_NAME = "json_resources.db"

    /**
     * The name for encrypted database.
     *
     * See [UNENCRYPTED_DATABASE_NAME] for the reason we use a separate name.
     */
    const val ENCRYPTED_DATABASE_NAME = "json_resources_encrypted.db"

    @VisibleForTesting const val DATABASE_PASSPHRASE_NAME = "jsonEngineDbPassphrase"
  }
}

data class DatabaseConfig(
  val inMemory: Boolean,
  val enableEncryption: Boolean,
  val databaseErrorStrategy: DatabaseErrorStrategy
)
