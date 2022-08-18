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

package com.google.android.fhir.r4.db.impl.dao

import androidx.room.Dao
import androidx.room.Transaction
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.db.impl.dao.BaseLocalChangeDao
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.db.impl.entities.ResourceEntity
import com.google.android.fhir.r4.logicalId
import com.google.android.fhir.r4.toTimeZoneString
import com.google.android.fhir.r4.versionId
import java.util.Date
import org.hl7.fhir.r4.model.Resource
import timber.log.Timber

/**
 * Dao for local changes made to a resource. One row in LocalChangeEntity corresponds to one change
 * e.g. an INSERT or UPDATE. The UPDATES (diffs) are stored as RFC 6902 JSON patches. When a
 * resource needs to be synced, all corresponding LocalChanges are 'squashed' to create a a single
 * LocalChangeEntity to sync with the server.
 */
@Dao
internal abstract class LocalChangeDao : BaseLocalChangeDao<Resource> {

  override lateinit var iParser: IParser

  @Transaction
  override suspend fun addInsertAll(resources: List<Resource>) {
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
        type = LocalChangeEntity.Type.INSERT,
        payload = resourceString,
        versionId = resource.versionId
      )
    )
  }

  override suspend fun addUpdate(oldEntity: ResourceEntity, resource: Resource) {
    val resourceId = resource.logicalId
    val resourceType = resource.resourceType.name
    val timestamp = Date().toTimeZoneString()

    if (!localChangeIsEmpty(resourceId, resourceType) &&
        lastChangeType(resourceId, resourceType)!!.equals(LocalChangeEntity.Type.DELETE)
    ) {
      throw BaseLocalChangeDao.InvalidLocalChangeException(
        "Unexpected DELETE when updating $resourceType/$resourceId. UPDATE failed."
      )
    }
    val jsonDiff =
      LocalChangeUtils.diff(
        iParser,
        iParser.parseResource(oldEntity.serializedResource) as Resource,
        resource
      )
    if (jsonDiff.length() == 0) {
      Timber.i(
        "New resource ${resource.resourceType}/${resource.id} is same as old resource. " +
          "Not inserting UPDATE LocalChange."
      )
      return
    }
    addLocalChange(
      LocalChangeEntity(
        id = 0,
        resourceType = resourceType,
        resourceId = resourceId,
        timestamp = timestamp,
        type = LocalChangeEntity.Type.UPDATE,
        payload = jsonDiff.toString(),
        versionId = oldEntity.versionId
      )
    )
  }

  override suspend fun addDelete(
    resourceId: String,
    resourceType: String,
    remoteVersionId: String?
  ) {
    val timestamp = Date().toTimeZoneString()
    addLocalChange(
      LocalChangeEntity(
        id = 0,
        resourceType = resourceType,
        resourceId = resourceId,
        timestamp = timestamp,
        type = LocalChangeEntity.Type.DELETE,
        payload = "",
        versionId = remoteVersionId
      )
    )
  }

  private suspend fun localChangeIsEmpty(resourceId: String, resourceType: String): Boolean =
    countLastChange(resourceId, resourceType) == 0

  override suspend fun discardLocalChanges(resources: List<Resource>) {
    resources.forEach { discardLocalChanges(it.logicalId, it.resourceType.name) }
  }
}
