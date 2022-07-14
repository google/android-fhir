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

const val XFHIR_QUERY_SORT_PARAM = "_sort"
const val XFHIR_QUERY_COUNT_PARAM = "_count"

suspend inline fun <reified R : Resource> FhirEngine.search(init: Search.() -> Unit): List<R> {
  val search = Search(type = R::class.java.newInstance().resourceType)
  search.init()
  return this.search(search)
}

suspend inline fun <reified R : Resource> FhirEngine.count(init: Search.() -> Unit): Long {
  val search = Search(type = R::class.java.newInstance().resourceType)
  search.init()
  return this.count(search)
}

suspend fun FhirEngine.search(xFhirQuery: String): List<Resource> {
  // Patient?active=true&gender=male&_sort=name,gender&_count=11
  val (type, queryStringPairs) =
    xFhirQuery.split("?").let {
      ResourceType.fromCode(it.first()) to it.elementAtOrNull(1)?.split("&")
    }
  val queryParams =
    queryStringPairs?.mapNotNull {
      it.split("=").takeIf { it.size > 1 }?.let { it.first() to it.elementAt(1) }
    }

  val sort = queryParams?.firstOrNull { it.first == XFHIR_QUERY_SORT_PARAM }?.second?.split(",")
  val count = queryParams?.firstOrNull { it.first == XFHIR_QUERY_COUNT_PARAM }?.second?.toInt()
  val searchParams =
    queryParams
      ?.filter { listOf(XFHIR_QUERY_COUNT_PARAM, XFHIR_QUERY_SORT_PARAM).contains(it.first).not() }
      ?.associate { it.first to it.second }

  val search = Search(type, count)

  val resourceSearchParameters = getSearchParamList(getResourceClass<Resource>(type).newInstance())

  val querySearchParameters =
    searchParams?.keys
      ?.map { searchKey ->
        resourceSearchParameters.find { it.name == searchKey }
          ?: throw IllegalArgumentException("$searchKey not found in ${type.name}")
      }
      ?.map { Pair(it, searchParams[it.name]!!) }

  querySearchParameters?.forEach {
    val (param, filterValue) = it
    search.filter(param, filterValue)
  }

  sort?.let { sortParams ->
    resourceSearchParameters.filter { sortParams.contains(it.name) }.forEach { sort ->
      search.sort(sort)
    }
  }

  return this.search(search)
}
