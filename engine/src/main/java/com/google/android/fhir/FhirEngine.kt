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
import com.google.android.fhir.index.getSearchParamList
import com.google.android.fhir.search.Search
import com.google.android.fhir.search.filter
import com.google.android.fhir.search.sort
import java.time.OffsetDateTime
import kotlinx.coroutines.flow.Flow
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/** The FHIR Engine interface that handles the local storage of FHIR resources. */
interface FhirEngine {
  /**
   * Creates one or more FHIR [resource]s in the local storage.
   *
   * @return the logical IDs of the newly created resources.
   */
  suspend fun create(vararg resource: Resource): List<String>

  /** Loads a FHIR resource given the class and the logical ID. */
  suspend fun get(type: ResourceType, id: String): Resource

  /** Updates a FHIR [resource] in the local storage. */
  suspend fun update(vararg resource: Resource)

  /** Removes a FHIR resource given the class and the logical ID. */
  suspend fun delete(type: ResourceType, id: String)

  /**
   * Searches the database and returns a list resources according to the [search] specifications.
   */
  suspend fun <R : Resource> search(search: Search): List<R>

  /**
   * Searches the database and returns a list resources according to the string complying
   * [XFhirQuery] specifications.
   */
  suspend fun search(xFhirQuery: String): List<Resource> {
    // Patient?active=true&gender=male&_sort=name,gender&_count=11
    val (path, queryString) = xFhirQuery.split("?").let { it.first() to it.elementAtOrNull(1) }
    val type = ResourceType.fromCode(path)
    val queryParams =
      queryString?.split("&")?.map { it.split("=").let { it.first() to it.elementAtOrNull(1) } }
        ?: listOf()
    return XFhirQuery(
        type = type,
        sort = queryParams.firstOrNull { it.first == XFHIR_QUERY_SORT_PARAM }?.second?.split(","),
        count = queryParams.firstOrNull { it.first == XFHIR_QUERY_COUNT_PARAM }?.second?.toInt()
            ?: 50,
        search =
          queryParams
            .filter {
              listOf(XFHIR_QUERY_COUNT_PARAM, XFHIR_QUERY_SORT_PARAM).contains(it.first).not()
            }
            .associate { it.first to it.second }
      )
      .let { search(it) }
  }

  /**
   * Searches the database and returns a list resources according to the [XFhirQuery]
   * specifications.
   */
  suspend fun search(xFhirQuery: XFhirQuery): List<Resource> {
    val searchObject = Search(xFhirQuery.type, xFhirQuery.count)

    val clazz: Class<out Resource> = getResourceClass(xFhirQuery.type)
    val resourceSearchParameters = getSearchParamList(clazz.newInstance())

    val querySearchParameters =
      xFhirQuery.search.keys
        .map { searchKey ->
          resourceSearchParameters.find { it.name == searchKey }
            ?: throw IllegalArgumentException("$searchKey not found in ${xFhirQuery.type.name}")
        }
        .map { Pair(it, xFhirQuery.search[it.name]!!) }

    querySearchParameters.forEach {
      val (param, filterValue) = it
      searchObject.filter(param, filterValue)
    }

    xFhirQuery.sort?.let { sortParams ->
      resourceSearchParameters.filter { sortParams.contains(it.name) }.forEach { sort ->
        searchObject.sort(sort)
      }
    }

    return search(searchObject)
  }

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
}

/**
 * Returns a FHIR resource of type [R] with [id] from the local storage.
 *
 * @param <R> The resource type which should be a subtype of [Resource].
 * @throws ResourceNotFoundException if the resource is not found
 */
@Throws(ResourceNotFoundException::class)
suspend inline fun <reified R : Resource> FhirEngine.get(id: String): R {
  return get(getResourceType(R::class.java), id) as R
}

/**
 * Deletes a FHIR resource of type [R] with [id] from the local storage.
 *
 * @param <R> The resource type which should be a subtype of [Resource].
 */
suspend inline fun <reified R : Resource> FhirEngine.delete(id: String) {
  delete(getResourceType(R::class.java), id)
}

interface SyncDownloadContext {
  suspend fun getLatestTimestampFor(type: ResourceType): String?
}
