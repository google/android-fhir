/*
 * Copyright 2023-2024 Google LLC
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

package com.google.android.fhir

import com.google.android.fhir.db.LocalChangeResourceReference
import com.google.android.fhir.db.ResourceNotFoundException
import com.google.android.fhir.search.Search
import com.google.android.fhir.sync.ConflictResolver
import com.google.android.fhir.sync.upload.SyncUploadProgress
import com.google.android.fhir.sync.upload.UploadRequestResult
import com.google.android.fhir.sync.upload.UploadStrategy
import java.time.OffsetDateTime
import kotlinx.coroutines.flow.Flow
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/**
 * Provides an interface for managing FHIR resources in local storage.
 *
 * The FHIR Engine allows you to create, read, update, and delete (CRUD) FHIR resources, as well as
 * perform searches and synchronize data with a remote FHIR server. The FHIR resources are
 * represented using HAPI FHIR Structures [Resource] and [ResourceType].
 *
 * To use a FHIR Engine instance, first call [FhirEngineProvider.init] with a
 * [FhirEngineConfiguration]. This must be done only once; we recommend doing this in the
 * `onCreate()` function of your `Application` class.
 *
 * ```
 * class MyApplication : Application() {
 *   override fun onCreate() {
 *     super.onCreate()
 *
 *     FhirEngineProvider.init(
 *       FhirEngineConfiguration(
 *         enableEncryptionIfSupported = true,
 *         RECREATE_AT_OPEN
 *       )
 *     )
 *   }
 * }
 * ```
 *
 * To get a `FhirEngine` to interact with, use [FhirEngineProvider.getInstance]:
 * ```
 * val fhirEngine = FhirEngineProvider.getInstance(this)
 * ```
 */
interface FhirEngine {
  /**
   * Creates one or more FHIR [Resource]s in the local storage. FHIR Engine requires all stored
   * resources to have a logical [Resource.id]. If the `id` is specified in the resource passed to
   * [create], the resource created in `FhirEngine` will have the same `id`. If no `id` is
   * specified, `FhirEngine` will generate a UUID as that resource's `id` and include it in the
   * returned list of IDs.
   *
   * @param resource The FHIR resources to create.
   * @return A list of logical IDs of the newly created resources.
   */
  suspend fun create(vararg resource: Resource): List<String>

  /**
   * Loads a FHIR resource given its [ResourceType] and logical ID.
   *
   * @param type The type of the resource to load.
   * @param id The logical ID of the resource.
   * @return The requested FHIR resource.
   * @throws ResourceNotFoundException if the resource is not found.
   */
  @Throws(ResourceNotFoundException::class)
  suspend fun get(type: ResourceType, id: String): Resource

  /**
   * Updates one or more FHIR [Resource]s in the local storage.
   *
   * @param resource The FHIR resources to update.
   */
  suspend fun update(vararg resource: Resource)

  /**
   * Removes a FHIR resource given its [ResourceType] and logical ID.
   *
   * @param type The type of the resource to delete.
   * @param id The logical ID of the resource.
   */
  suspend fun delete(type: ResourceType, id: String)

  /**
   * Searches the database and returns a list of resources matching the [Search] specifications.
   *
   * @param search The search criteria to apply.
   * @return A list of [SearchResult] objects containing the matching resources and any included
   *   references.
   */
  suspend fun <R : Resource> search(search: Search): List<SearchResult<R>>

  /**
   * Synchronizes upload results with the database.
   *
   * This function initiates multiple server calls to upload local changes. The results of each call
   * are emitted as [UploadRequestResult] objects, which can be collected using a [Flow].
   *
   * @param uploadStrategy Defines strategies for uploading FHIR resource.
   * @param upload A suspending function that takes a list of [LocalChange] objects and returns a
   *   [Flow] of [UploadRequestResult] objects.
   * @return A [Flow] that emits the progress of the synchronization process as [SyncUploadProgress]
   *   objects.
   */
  @Deprecated("To be deprecated.")
  suspend fun syncUpload(
    uploadStrategy: UploadStrategy,
    upload:
      (suspend (List<LocalChange>, List<LocalChangeResourceReference>) -> Flow<
          UploadRequestResult,
        >),
  ): Flow<SyncUploadProgress>

  /**
   * Synchronizes the download results with the database.
   *
   * This function updates the local database to reflect the results of the download operation,
   * resolving any conflicts using the provided [ConflictResolver].
   *
   * @param conflictResolver The [ConflictResolver] to use for resolving conflicts between local and
   *   remote data.
   * @param download A suspending function that returns a [Flow] of lists of [Resource] objects
   *   representing the downloaded data.
   */
  @Deprecated("To be deprecated.")
  suspend fun syncDownload(
    conflictResolver: ConflictResolver,
    download: suspend () -> Flow<List<Resource>>,
  )

  /**
   * Returns the total count of entities available for the given [Search].
   *
   * @param search The search criteria to apply.
   * @return The total number of matching resources.
   */
  suspend fun count(search: Search): Long

  /**
   * Returns the timestamp when data was last synchronized, or `null` if no synchronization has
   * occurred yet.
   */
  suspend fun getLastSyncTimeStamp(): OffsetDateTime?

  /**
   * Clears all database tables without resetting the auto-increment value generated by
   * PrimaryKey.autoGenerate.
   *
   * WARNING: This will permanently delete all data in the database.
   */
  suspend fun clearDatabase()

  /**
   * Retrieves a list of [LocalChange]s for the [Resource] with the given type and ID. This can be
   * used to select resources to purge from the database.
   *
   * @param type The [ResourceType] of the resource.
   * @param id The resource ID.
   * @return A list of [LocalChange] objects representing the local changes made to the resource, or
   *   an empty list if no changes.
   */
  suspend fun getLocalChanges(type: ResourceType, id: String): List<LocalChange>

  /**
   * Purges a resource from the database without deleting data from the server.
   *
   * @param type The [ResourceType] of the resource.
   * @param id The resource ID.
   * @param forcePurge If `true`, the resource will be purged even if it has local changes.
   *   Otherwise, an [IllegalStateException] will be thrown if local changes exist. Defaults to
   *   `false`.
   *
   *   If you need to purge resources in bulk use the method
   *   [FhirEngine.purge(type: ResourceType, ids: Set<String>, forcePurge: Boolean = false)]
   */
  suspend fun purge(type: ResourceType, id: String, forcePurge: Boolean = false)

  /**
   * Purges resources of the specified type from the database identified by their IDs without any
   * deletion of data from the server.
   *
   * @param type The [ResourceType]
   * @param ids The resource ids [Set]<[Resource.id]>
   * @param forcePurge If `true`, the resource will be purged even if it has local changes.
   *   Otherwise, an [IllegalStateException] will be thrown if local changes exist. Defaults to
   *   `false`.
   *
   *   In the case an exception is thrown by any entry in the list the whole transaction is rolled
   *   back and no record is purged.
   */
  suspend fun purge(type: ResourceType, ids: Set<String>, forcePurge: Boolean = false)

  /**
   * Adds support for performing actions on `FhirEngine` as a single atomic transaction where the
   * entire set of changes succeed or fail as a single entity
   */
  suspend fun withTransaction(block: suspend FhirEngine.() -> Unit)
}

/**
 * Retrieves a FHIR resource of type [R] with the given [id] from the local storage.
 *
 * @param R The type of the FHIR resource to retrieve.
 * @param id The logical ID of the resource to retrieve.
 * @return The requested FHIR resource.
 * @throws ResourceNotFoundException if the resource is not found.
 */
@Throws(ResourceNotFoundException::class)
suspend inline fun <reified R : Resource> FhirEngine.get(id: String): R {
  return get(getResourceType(R::class.java), id) as R
}

/**
 * Deletes a FHIR resource of type [R] with the given [id] from the local storage.
 *
 * @param R The type of the FHIR resource to delete.
 * @param id The logical ID of the resource to delete.
 */
suspend inline fun <reified R : Resource> FhirEngine.delete(id: String) {
  delete(getResourceType(R::class.java), id)
}

typealias SearchParamName = String

/**
 * Represents the result of a FHIR search query, containing a matching resource and any referenced
 * resources as specified in the query.
 *
 * @param R The type of the main FHIR resource in the search result.
 * @property resource The FHIR resource that matches the search criteria.
 * @property included A map of included resources, keyed by the search parameter name used for
 *   inclusion, as per the [Search.forwardIncludes] criteria in the query.
 * @property revIncluded A map of reverse included resources, keyed by the resource type and search
 *   parameter name used for inclusion, as per the [Search.revIncludes] criteria in the query.
 */
data class SearchResult<R : Resource>(
  val resource: R,
  val included: Map<SearchParamName, List<Resource>>?,
  val revIncluded: Map<Pair<ResourceType, SearchParamName>, List<Resource>>?,
)
