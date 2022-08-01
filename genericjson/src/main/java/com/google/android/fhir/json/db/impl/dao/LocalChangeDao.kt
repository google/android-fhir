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

package com.google.android.fhir.json.db.impl.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.google.android.fhir.json.db.impl.entities.JsonObjectEntity
import com.google.android.fhir.json.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.json.toTimeZoneString
import java.util.Date
import org.json.JSONObject
import timber.log.Timber

/**
 * Dao for local changes made to a resource. One row in LocalChangeEntity corresponds to one change
 * e.g. an INSERT or UPDATE. The UPDATES (diffs) are stored as RFC 6902 JSON patches. When a
 * resource needs to be synced, all corresponding LocalChanges are 'squashed' to create a a single
 * LocalChangeEntity to sync with the server.
 */
@Dao
internal abstract class LocalChangeDao {

  @Insert abstract suspend fun addLocalChange(localChangeEntity: LocalChangeEntity)

  @Transaction
  open suspend fun addInsertAll(resources: List<JSONObject>) {
    resources.forEach { resource -> addInsert(resource) }
  }

  suspend fun addInsert(resource: JSONObject) {
    val resourceId = resource["id"].toString()
    val timestamp = Date().toTimeZoneString()
    val resourceString = resource.toString()

    addLocalChange(
      LocalChangeEntity(
        id = 0,
        resourceId = resourceId,
        timestamp = timestamp,
        type = LocalChangeEntity.Type.INSERT,
        payload = resourceString,
      )
    )
  }

  suspend fun addUpdate(oldEntity: JsonObjectEntity, resource: JSONObject) {
    val resourceId = resource["id"].toString()
    val timestamp = Date().toTimeZoneString()

    if (!localChangeIsEmpty(resourceId) &&
        lastChangeType(resourceId)!!.equals(LocalChangeEntity.Type.DELETE)
    ) {
      throw InvalidLocalChangeException(
        "Unexpected DELETE when updating $resourceId. UPDATE failed."
      )
    }
    val jsonDiff = LocalChangeUtils.diff(JSONObject(oldEntity.serializedResource), resource)
    if (jsonDiff.length() == 0) {
      Timber.i(
        "New resource $resourceId is same as old resource. " + "Not inserting UPDATE LocalChange."
      )
      return
    }
    addLocalChange(
      LocalChangeEntity(
        id = 0,
        resourceId = resourceId,
        timestamp = timestamp,
        type = LocalChangeEntity.Type.UPDATE,
        payload = jsonDiff.toString(),
      )
    )
  }

  suspend fun addDelete(resourceId: String) {
    val timestamp = Date().toTimeZoneString()
    addLocalChange(
      LocalChangeEntity(
        id = 0,
        resourceId = resourceId,
        timestamp = timestamp,
        type = LocalChangeEntity.Type.DELETE,
        payload = "",
      )
    )
  }

  @Query(
    """
        SELECT type 
        FROM LocalChangeEntity 
        WHERE resourceId = :resourceId 
        ORDER BY id ASC
        LIMIT 1
    """
  )
  abstract suspend fun lastChangeType(resourceId: String): LocalChangeEntity.Type?

  @Query(
    """
        SELECT COUNT(type) 
        FROM LocalChangeEntity 
        WHERE resourceId = :resourceId 
        LIMIT 1
    """
  )
  abstract suspend fun countLastChange(resourceId: String): Int

  private suspend fun localChangeIsEmpty(resourceId: String): Boolean =
    countLastChange(resourceId) == 0

  @Query(
    """
        SELECT *
        FROM LocalChangeEntity
        ORDER BY LocalChangeEntity.id ASC"""
  )
  abstract suspend fun getAllLocalChanges(): List<LocalChangeEntity>

  @Query(
    """
        DELETE FROM LocalChangeEntity
        WHERE LocalChangeEntity.id = (:id)
    """
  )
  abstract suspend fun discardLocalChanges(id: Long)

  @Transaction
  open suspend fun discardLocalChanges(token: LocalChangeToken) {
    token.ids.forEach { discardLocalChanges(it) }
  }

  @Query("""
        DELETE FROM LocalChangeEntity
        WHERE resourceId = (:resourceId)
    """)
  abstract suspend fun discardLocalChanges(resourceId: String)

  suspend fun discardLocalChanges(resources: List<JSONObject>) {
    resources.forEach { discardLocalChanges(it["id"].toString()) }
  }

  class InvalidLocalChangeException(message: String?) : Exception(message)
}
