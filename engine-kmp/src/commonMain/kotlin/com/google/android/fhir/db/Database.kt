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

package com.google.android.fhir.db

import com.google.android.fhir.LocalChange
import com.google.android.fhir.LocalChangeToken
import com.google.android.fhir.search.ReferencedResourceResult
import com.google.android.fhir.search.SearchQuery
import com.google.fhir.model.r4.Resource
import com.google.fhir.model.r4.terminologies.ResourceType
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Instant
import kotlin.uuid.Uuid

/** The interface for the FHIR resource database. */
internal interface Database {
  /**
   * Inserts a list of local `resources` into the FHIR resource database. If any of the resources
   * already exists, it will be overwritten.
   *
   * @return the logical IDs of the newly created resources.
   */
  suspend fun <R : Resource> insert(vararg resource: R): List<String>

  /**
   * Inserts a list of remote `resources` into the FHIR resource database. If any of the resources
   * already exists, it will be overwritten.
   */
  suspend fun <R : Resource> insertRemote(vararg resource: R)

  /**
   * Updates the `resource` in the FHIR resource database. If the resource does not already exist,
   * then it will not be created.
   */
  suspend fun update(vararg resources: Resource)

  /** Updates the `resource` meta in the FHIR resource database. */
  suspend fun updateVersionIdAndLastUpdated(
    resourceId: String,
    resourceType: ResourceType,
    versionId: String?,
    lastUpdated: Instant?,
  )

  /**
   * Updates the existing [oldResourceId] with the new [newResourceId]. Even if [oldResourceId] and
   * [newResourceId] are the same, it is still necessary to update the resource meta.
   */
  suspend fun updateResourcePostSync(
    oldResourceId: String,
    newResourceId: String,
    resourceType: ResourceType,
    versionId: String?,
    lastUpdated: Instant?,
  )

  /**
   * Selects the FHIR resource of type with `id`.
   *
   * @throws ResourceNotFoundException if the resource is not found in the database
   */
  @Throws(ResourceNotFoundException::class, CancellationException::class)
  suspend fun select(type: ResourceType, id: String): Resource

  /** Insert resources that were synchronized. */
  suspend fun insertSyncedResources(resources: List<Resource>)

  /** Deletes the FHIR resource of type with `id`. */
  suspend fun delete(type: ResourceType, id: String)

  suspend fun <R : Resource> search(query: SearchQuery): List<ResourceWithUUID<R>>

  suspend fun count(query: SearchQuery): Long

  suspend fun searchReferencedResources(query: SearchQuery): List<ReferencedResourceResult>

  /**
   * Retrieves all [LocalChange]s for all [Resource]s, which can be used to update the remote FHIR
   * server.
   */
  suspend fun getAllLocalChanges(): List<LocalChange>

  /**
   * Retrieves all [LocalChange]s for the [Resource] which has the [LocalChange] with the oldest
   * [LocalChange.timestamp].
   */
  suspend fun getAllChangesForEarliestChangedResource(): List<LocalChange>

  /** Retrieves the count of [LocalChange]s stored in the database. */
  suspend fun getLocalChangesCount(): Int

  /** Remove the [LocalChange]s with given ids. Call this after a successful sync. */
  suspend fun deleteUpdates(token: LocalChangeToken)

  /** Remove the [LocalChange]s with matching resource ids. */
  suspend fun deleteUpdates(resources: List<Resource>)

  /**
   * Updates the existing resource identified by [currentResourceId] with the [updatedResource],
   * ensuring all associated references in the database are also updated accordingly.
   */
  suspend fun updateResourceAndReferences(
    currentResourceId: String,
    updatedResource: Resource,
  )

  /** Runs the block as a database transaction. */
  suspend fun withTransaction(block: suspend () -> Unit)

  /** Closes the database connection. */
  fun close()

  /** Clears all database tables. WARNING: This will clear the database and it's not recoverable. */
  suspend fun clearDatabase()

  /**
   * Retrieve a list of [LocalChange] for [Resource] with given type and id.
   *
   * @return A list of local changes, or an empty list if none exist.
   */
  suspend fun getLocalChanges(type: ResourceType, id: String): List<LocalChange>

  /**
   * Retrieve a list of [LocalChange] for a resource with the given UUID.
   *
   * @return A list of local changes, or an empty list if none exist.
   */
  suspend fun getLocalChanges(resourceUuid: Uuid): List<LocalChange>

  /**
   * Purges resources of the specified type from the database identified by their IDs without any
   * deletion of data from the server.
   */
  suspend fun purge(type: ResourceType, ids: Set<String>, forcePurge: Boolean = false)

  /**
   * @return List of [LocalChangeResourceReference] associated with the local change IDs. A single
   *   local change may have one or more [LocalChangeResourceReference] associated with it.
   */
  suspend fun getLocalChangeResourceReferences(
    localChangeIds: List<Long>,
  ): List<LocalChangeResourceReference>
}

internal data class ResourceWithUUID<R>(
  val uuid: Uuid,
  val resource: R,
)

data class LocalChangeResourceReference(
  val localChangeId: Long,
  val resourceReferenceValue: String,
  val resourceReferencePath: String?,
)
