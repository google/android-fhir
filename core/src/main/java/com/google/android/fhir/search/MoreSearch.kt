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

suspend fun <R : Resource> Search.run(database: Database): List<R> {
  return database.search(getQuery())
}

fun Search.getQuery(): SearchQuery {
  val filterQuery =
    (stringFilters.map { it.query(type) } + referenceFilter.map { it.query(type) }).intersect()

  val queryBuilder = StringBuilder()
  queryBuilder.appendLine(
    """
    SELECT a.serializedResource
    FROM ResourceEntity a${sort?.let {
    """
    LEFT JOIN StringIndexEntity b
    ON a.resourceType = b.resourceType AND a.resourceId = b.resourceId AND b.index_name = ?"""
    } ?: ""}
    WHERE a.resourceType = ?${filterQuery?.let { " AND a.resourceId IN ("} ?: ""}
    """.trimIndent()
  )
  filterQuery?.also {
    queryBuilder.appendLine(it.query)
    queryBuilder.appendLine(")")
  }
  sort?.also {
    queryBuilder.appendLine(
      """
      ORDER BY b.index_value ${
        when (order!!) {
          Order.ASCENDING -> "ASC"
          Order.DESCENDING -> "DESC"
        }
      }
      """.trimIndent()
    )
  }
  size?.also {
    queryBuilder.appendLine("""
    LIMIT ?${from?.let { " OFFSET ?" } ?: ""}
    """.trimIndent())
  }
  val args = mutableListOf<Any>()
  if (sort != null) {
    args.add(sort!!.paramName)
  }
  args.add(type.name)
  filterQuery?.also { args.addAll(it.args) }
  size?.also {
    args.add(it)
    from?.also { args.add(it) }
  }

  return SearchQuery(queryBuilder.toString().trimIndent(), args)
}

fun StringFilter.query(type: ResourceType): SearchQuery {
  return SearchQuery(
    """
    SELECT resourceId FROM StringIndexEntity
    WHERE resourceType = ? AND index_name = ? AND index_value = ?
    """.trimIndent(),
    listOf(type.name, parameter.paramName, value!!)
  )
}

fun ReferenceFilter.query(type: ResourceType): SearchQuery {
  return SearchQuery(
    """
    SELECT resourceId FROM ReferenceIndexEntity
    WHERE resourceType = ? AND index_name = ? AND index_value = ?
    """.trimIndent(),
    listOf(type.name, parameter!!.paramName, value!!)
  )
}

fun List<SearchQuery>.intersect(): SearchQuery? {
  return if (isEmpty()) {
    null
  } else {
    SearchQuery(joinToString("\nINTERSECT\n".trimIndent()) { it.query }, flatMap { it.args })
  }
}
