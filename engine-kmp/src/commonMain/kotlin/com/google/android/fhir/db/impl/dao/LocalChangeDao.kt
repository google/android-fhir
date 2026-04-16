/*
 * Copyright 2025-2026 Google LLC
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

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.db.impl.entities.LocalChangeResourceReferenceEntity

@Dao
internal interface LocalChangeDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun addLocalChange(localChangeEntity: LocalChangeEntity): Long

  @Query(
    """
    SELECT *
    FROM LocalChangeEntity
    ORDER BY timestamp ASC
    """,
  )
  suspend fun getAllLocalChanges(): List<LocalChangeEntity>

  @Query(
    """
    SELECT *
    FROM LocalChangeEntity
    WHERE resourceUuid = (
      SELECT resourceUuid
      FROM LocalChangeEntity
      ORDER BY timestamp ASC
      LIMIT 1)
    ORDER BY timestamp ASC
    """,
  )
  suspend fun getAllChangesForEarliestChangedResource(): List<LocalChangeEntity>

  @Query(
    """
    SELECT COUNT(*)
    FROM LocalChangeEntity
    """,
  )
  suspend fun getLocalChangesCount(): Int

  @Query(
    """
    DELETE FROM LocalChangeEntity
    WHERE id = :id
    """,
  )
  suspend fun discardLocalChanges(id: Long)

  @Query(
    """
    DELETE FROM LocalChangeEntity
    WHERE resourceId = :resourceId AND resourceType = :resourceType
    """,
  )
  suspend fun discardLocalChanges(resourceId: String, resourceType: String)

  @Query(
    """
    SELECT *
    FROM LocalChangeEntity
    WHERE resourceType = :resourceType AND resourceId = :resourceId
    ORDER BY timestamp ASC
    """,
  )
  suspend fun getLocalChanges(resourceType: String, resourceId: String): List<LocalChangeEntity>

  @Query(
    """
    SELECT *
    FROM LocalChangeEntity
    WHERE resourceUuid = :resourceUuid
    ORDER BY timestamp ASC
    """,
  )
  suspend fun getLocalChangesByUuid(resourceUuid: String): List<LocalChangeEntity>

  @Query(
    """
    SELECT type
    FROM LocalChangeEntity
    WHERE resourceId = :resourceId AND resourceType = :resourceType
    ORDER BY id ASC
    LIMIT 1
    """,
  )
  suspend fun lastChangeType(resourceId: String, resourceType: String): Int?

  @Query(
    """
    SELECT COUNT(type)
    FROM LocalChangeEntity
    WHERE resourceId = :resourceId AND resourceType = :resourceType
    LIMIT 1
    """,
  )
  suspend fun countLastChange(resourceId: String, resourceType: String): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLocalChangeResourceReferences(
    refs: List<LocalChangeResourceReferenceEntity>,
  )

  @Query(
    """
    SELECT *
    FROM LocalChangeResourceReferenceEntity
    WHERE localChangeId IN (:localChangeIds)
    """,
  )
  suspend fun getReferencesForLocalChanges(
    localChangeIds: List<Long>,
  ): List<LocalChangeResourceReferenceEntity>
}
