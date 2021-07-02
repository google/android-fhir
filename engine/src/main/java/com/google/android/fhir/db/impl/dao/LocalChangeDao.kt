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

package com.google.android.fhir.db.impl.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.db.impl.entities.LocalChangeEntity.Type
import com.google.android.fhir.logicalId
import com.google.android.fhir.toTimeZoneString
import java.util.Date
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/**
 * Dao for local changes made to a resource. One row in LocalChangeEntity corresponds to one change
 * e.g. an INSERT or UPDATE. The UPDATES (diffs) are stored as RFC 6902 JSON patches. When a
 * resource needs to be synced, all corresponding LocalChanges are 'squashed' to create a a single
 * LocalChangeEntity to sync with the server.
 */
@Dao
internal abstract class LocalChangeDao {

  lateinit var iParser: IParser

  @Insert abstract suspend fun addLocalChange(localChangeEntity: LocalChangeEntity)

  @Transaction
  open suspend fun addInsertAll(resources: List<Resource>) {
    resources.forEach { resource -> addInsert(resource) }
  }

  suspend fun addInsert(resource: Resource) {
    val resourceId = resource.logicalId
    val resourceType = resource.resourceType
    val timestamp = Date().toTimeZoneString()
    val resourceString = iParser.encodeResourceToString(resource)

    addLocalChange(
      LocalChangeEntity(
        id = 0,
        resourceType = resourceType.name,
        resourceId = resourceId,
        timestamp = timestamp,
        type = Type.INSERT,
        payload = resourceString
      )
    )
  }

  suspend fun addUpdate(oldResource: Resource, resource: Resource) {
    val resourceId = resource.logicalId
    val resourceType = resource.resourceType
    val timestamp = Date().toTimeZoneString()

    if (!localChangeIsEmpty(resourceId, resourceType) &&
        lastChangeType(resourceId, resourceType)!!.equals(Type.DELETE)
    ) {
      throw InvalidLocalChangeException(
        "Unexpected DELETE when updating $resourceType/$resourceId. UPDATE failed."
      )
    }
    val jsonDiff = LocalChangeUtils.diff(iParser, oldResource, resource)
    if (jsonDiff.length() == 0) {
      Log.i(
        "LocalChangeDao",
        "New resource ${resource.resourceType}/${resource.id} is same as old resource. " +
          "Not inserting UPDATE LocalChange."
      )
      return
    }
    addLocalChange(
      LocalChangeEntity(
        id = 0,
        resourceType = resourceType.name,
        resourceId = resourceId,
        timestamp = timestamp,
        type = Type.UPDATE,
        payload = jsonDiff.toString()
      )
    )
  }

  suspend fun addDelete(resourceId: String, resourceType: ResourceType) {
    val timestamp = Date().toTimeZoneString()
    addLocalChange(
      LocalChangeEntity(
        id = 0,
        resourceType = resourceType.name,
        resourceId = resourceId,
        timestamp = timestamp,
        type = Type.DELETE,
        payload = ""
      )
    )
  }

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
  abstract suspend fun lastChangeType(resourceId: String, resourceType: ResourceType): Type?

  @Query(
    """
        SELECT COUNT(type) 
        FROM LocalChangeEntity 
        WHERE resourceId = :resourceId 
        AND resourceType = :resourceType
        LIMIT 1
    """
  )
  abstract suspend fun countLastChange(resourceId: String, resourceType: ResourceType): Int

  private suspend fun localChangeIsEmpty(resourceId: String, resourceType: ResourceType): Boolean =
    countLastChange(resourceId, resourceType) == 0

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

  suspend fun discardLocalChanges(token: LocalChangeToken) {
    token.ids.forEach { discardLocalChanges(it) }
  }

  class InvalidLocalChangeException(message: String?) : Exception(message)
}
