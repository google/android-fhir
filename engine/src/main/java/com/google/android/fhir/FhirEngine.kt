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

package com.google.android.fhir

import com.google.android.fhir.db.ResourceNotFoundException
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.db.impl.dao.SquashedLocalChange
import com.google.android.fhir.search.Search
import java.time.OffsetDateTime
import kotlinx.coroutines.flow.Flow
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/** The FHIR Engine interface that handles the local storage of FHIR resources. */
interface FhirEngine {
  /**
   * Saves one or more FHIR [resource]s in the local storage. If any of the resources already exist,
   * they will be overwritten.
   *
   * @param <R> The resource type which should be a subtype of [Resource].
   */
  suspend fun <R : Resource> save(vararg resource: R)

  /**
   * Updates a FHIR [resource] in the local storage.
   *
   * @param <R> The resource type which should be a subtype of [Resource].
   */
  suspend fun <R : Resource> update(resource: R)

  /**
   * Searches the database and returns a list resources according to the [search] specifications.
   */
  suspend fun <R : Resource> search(search: Search): List<R>

  /**
   * Synchronizes the [upload] result in the database. [upload] operation may result in multiple
   * calls to the server to upload the data. Result of each call will be emitted by [upload] and the
   * api caller should [Flow.collect] it.
   */
  suspend fun syncUpload(
    upload: (suspend (List<SquashedLocalChange>) -> Flow<Pair<LocalChangeToken, Resource>>)
  )

  /**
   * Synchronizes the [download] result in the database. The database will be updated to reflect the
   * result of the [download] operation.
   */
  suspend fun syncDownload(download: suspend (SyncDownloadContext) -> Flow<List<Resource>>)

  /**
   * Returns the total count of entities available for given search.
   *
   * @param search
   */
  suspend fun count(search: Search): Long

  /** Returns the timestamp when data was last synchronized. */
  suspend fun getLastSyncTimeStamp(): OffsetDateTime?

  /**
   * DO NOT USE. Internal and test only function to load a FHIR resource given the class and the
   * logical ID.
   *
   * The `@Deprecated` annotation suggests replacement if this function is called.
   */
  @Deprecated("Internal and test only function", ReplaceWith("this.get<R>(id)"))
  suspend fun <R : Resource> load(clazz: Class<R>, id: String): R

  /**
   * DO NOT USE. Internal and test only function to delete a FHIR resource given the class and the
   * logical ID.
   *
   * The `@Deprecated` annotation suggests replacement if this function is called.
   */
  @Deprecated("Internal and test only function", ReplaceWith("this.delete<R>(id)"))
  suspend fun <R : Resource> remove(clazz: Class<R>, id: String)
}

/**
 * Deletes a FHIR resource of type [R] with [id] from the local storage.
 *
 * @param <R> The resource type which should be a subtype of [Resource].
 */
suspend inline fun <reified R : Resource> FhirEngine.delete(id: String) {
  remove(R::class.java, id)
}

/**
 * Returns a FHIR resource of type [R] with [id] from the local storage.
 *
 * @param <R> The resource type which should be a subtype of [Resource].
 * @throws ResourceNotFoundException if the resource is not found
 */
@Throws(ResourceNotFoundException::class)
suspend inline fun <reified R : Resource> FhirEngine.get(id: String): R {
  return load(R::class.java, id)
}

interface SyncDownloadContext {
  suspend fun getLatestTimestampFor(type: ResourceType): String?
}
