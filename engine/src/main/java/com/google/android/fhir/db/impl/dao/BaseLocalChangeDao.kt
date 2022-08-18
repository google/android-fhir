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

package com.google.android.fhir.db.impl.dao

import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.db.impl.entities.LocalChangeToken
import com.google.android.fhir.db.impl.entities.ResourceEntity

/**
 * Dao for local changes made to a resource. One row in LocalChangeEntity corresponds to one change
 * e.g. an INSERT or UPDATE. The UPDATES (diffs) are stored as RFC 6902 JSON patches. When a
 * resource needs to be synced, all corresponding LocalChanges are 'squashed' to create a a single
 * LocalChangeEntity to sync with the server.
 */
internal interface BaseLocalChangeDao<R> {

  var iParser: IParser

  @Insert suspend fun addLocalChange(localChangeEntity: LocalChangeEntity)

  suspend fun addInsertAll(resources: List<R>)

  suspend fun addUpdate(oldEntity: ResourceEntity, resource: R)

  suspend fun addDelete(resourceId: String, resourceType: String, remoteVersionId: String?)

  suspend fun discardLocalChanges(resources: List<R>)

  @Query(
    """
        SELECT type 
        FROM LocalChangeEntity 
        WHERE resourceId = :resourceId 
        AND resourceType = :resourceType 
        ORDER BY id ASC
        LIMIT 1
    """
  )
  suspend fun lastChangeType(resourceId: String, resourceType: String): LocalChangeEntity.Type?

  @Query(
    """
        SELECT COUNT(type) 
        FROM LocalChangeEntity 
        WHERE resourceId = :resourceId 
        AND resourceType = :resourceType
        LIMIT 1
    """
  )
  suspend fun countLastChange(resourceId: String, resourceType: String): Int

  @Query(
    """
        SELECT *
        FROM LocalChangeEntity
        ORDER BY LocalChangeEntity.id ASC"""
  )
  suspend fun getAllLocalChanges(): List<LocalChangeEntity>

  @Query(
    """
        DELETE FROM LocalChangeEntity
        WHERE LocalChangeEntity.id = (:id)
    """
  )
  suspend fun discardLocalChanges(id: Long)

  @Transaction
  suspend fun discardLocalChanges(token: LocalChangeToken) {
    token.ids.forEach { discardLocalChanges(it) }
  }

  @Query(
    """
        DELETE FROM LocalChangeEntity
        WHERE resourceId = (:resourceId)
        AND resourceType = :resourceType
    """
  )
  suspend fun discardLocalChanges(resourceId: String, resourceType: String)

  @Query(
    """
        SELECT *
        FROM LocalChangeEntity
        WHERE resourceId = :resourceId AND resourceType = :resourceType
    """
  )
  suspend fun getLocalChanges(resourceType: String, resourceId: String): List<LocalChangeEntity>

  class InvalidLocalChangeException(message: String?) : Exception(message)
}
