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

package com.google.android.fhir.search

import com.google.android.fhir.db.Database
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

suspend fun <R : Resource> Search.execute(database: Database): List<R> {
  return database.search(getQuery())
}

fun Search.getQuery(): SearchQuery {
  var sortJoinStatement = ""
  var sortOrderStatement = ""
  var sortArgs = mutableListOf<Any>()
  if (sort != null) {
    sortJoinStatement =
      """
      LEFT JOIN StringIndexEntity b
      ON a.resourceType = b.resourceType AND a.resourceId = b.resourceId AND b.index_name = ?
      """.trimIndent()
    sortOrderStatement =
      """
      ORDER BY b.index_value ${
        when (order!!) {
          Order.ASCENDING -> "ASC"
          Order.DESCENDING -> "DESC"
        }
      }
      """.trimIndent()
    sortArgs.add(sort!!.paramName)
  }

  var filterStatement = ""
  var filterArgs = mutableListOf<Any>()
  val filterQuery =
    (stringFilters.map { it.query(type) } + referenceFilter.map { it.query(type) }).intersect()
  if (filterQuery != null) {
    filterStatement =
      """
      AND a.resourceId IN (
      ${filterQuery.query}
      )
      """.trimIndent()
    filterArgs.addAll(filterQuery.args)
  }

  var limitStatement = ""
  var limitArgs = mutableListOf<Any>()
  if (count != null) {
    limitStatement = "LIMIT ?"
    limitArgs.add(count!!)
    if (from != null) {
      limitStatement = "LIMIT ? OFFSET ?"
      limitArgs.add(from!!)
    }
  }

  val query =
    """
    SELECT a.serializedResource
    FROM ResourceEntity a
    $sortJoinStatement
    WHERE a.resourceType = ?
    $filterStatement
    $sortOrderStatement
    $limitStatement
    """
      .split("\n")
      .filter { it.isNotBlank() }
      .joinToString("\n") { it.trim() }
  return SearchQuery(query, sortArgs + type.name + filterArgs + limitArgs)
}

fun StringFilter.query(type: ResourceType): SearchQuery {
  return SearchQuery(
    """
    SELECT resourceId FROM StringIndexEntity
    WHERE resourceType = ? AND index_name = ? AND index_value = ?
    """,
    listOf(type.name, parameter.paramName, value!!)
  )
}

fun ReferenceFilter.query(type: ResourceType): SearchQuery {
  return SearchQuery(
    """
    SELECT resourceId FROM ReferenceIndexEntity
    WHERE resourceType = ? AND index_name = ? AND index_value = ?
    """,
    listOf(type.name, parameter!!.paramName, value!!)
  )
}

fun List<SearchQuery>.intersect(): SearchQuery? {
  return if (isEmpty()) {
    null
  } else {
    SearchQuery(joinToString("\nINTERSECT\n") { it.query }, flatMap { it.args })
  }
}
