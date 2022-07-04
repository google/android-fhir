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

package com.google.android.fhir.search

import com.google.android.fhir.FhirEngine
import com.google.android.fhir.getResourceClass
import com.google.android.fhir.index.getSearchParamList
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

object SearchXFhirQuery {
  /**
   * Searches the database and returns a list resources according to the string complying
   * [XFhirQuery] specifications.
   */
  suspend fun search(xFhirQuery: String, fhirEngine: FhirEngine): List<Resource> {
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
      .let { search(it, fhirEngine) }
  }

  /**
   * Searches the database and returns a list resources according to the [XFhirQuery]
   * specifications.
   */
  suspend fun search(xFhirQuery: XFhirQuery, fhirEngine: FhirEngine): List<Resource> {
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

    return fhirEngine.search(searchObject)
  }
}
