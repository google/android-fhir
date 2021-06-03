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

import ca.uhn.fhir.rest.gclient.NumberClientParam
import ca.uhn.fhir.rest.gclient.StringClientParam
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import com.google.android.fhir.db.Database
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

internal suspend fun <R : Resource> Search.execute(database: Database): List<R> {
  return database.search(getQuery())
}

fun Search.getQuery(): SearchQuery {
  var sortJoinStatement = ""
  var sortOrderStatement = ""
  val sortArgs = mutableListOf<Any>()
  sort?.let { sort ->
    val sortTableName =
      when (sort) {
        is StringClientParam -> "StringIndexEntity"
        is NumberClientParam -> "NumberIndexEntity"
        else -> throw NotImplementedError("Unhandled sort parameter of type ${sort::class}: $sort")
      }
    sortJoinStatement =
      """
      LEFT JOIN $sortTableName b
      ON a.resourceType = b.resourceType AND a.resourceId = b.resourceId AND b.index_name = ?
      """.trimIndent()
    sortOrderStatement = """
      ORDER BY b.index_value ${order.sqlString}
    """.trimIndent()
    sortArgs += sort.paramName
  }

  var filterStatement = ""
  val filterArgs = mutableListOf<Any>()
  val filterQuery =
    (stringFilters.map { it.query(type) } +
        referenceFilters.map { it.query(type) } +
        tokenFilters.map { it.query(type) } +
        numberFilter.map { it.query(type) })
      .intersect()
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
  val limitArgs = mutableListOf<Any>()
  if (count != null) {
    limitStatement = "LIMIT ?"
    limitArgs += count!!
    if (from != null) {
      limitStatement += " OFFSET ?"
      limitArgs += from!!
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

  val condition =
    when (modifier) {
      StringFilterModifier.STARTS_WITH -> "LIKE ? || '%' COLLATE NOCASE"
      StringFilterModifier.MATCHES_EXACTLY -> "= ?"
      StringFilterModifier.CONTAINS -> "LIKE '%' || ? || '%' COLLATE NOCASE"
    }
  return SearchQuery(
    """
    SELECT resourceId FROM StringIndexEntity
    WHERE resourceType = ? AND index_name = ? AND index_value $condition 
    """,
    listOf(type.name, parameter.paramName, value!!)
  )
}

fun NumberFilter.query(type: ResourceType): SearchQuery {
  val precision =
    when {
      value!!.scale() >= 0 ->
        BigDecimal(1).divide(BigDecimal(10).pow(value!!.scale())).divide(BigDecimal(2))
      else -> BigDecimal(5)
    }

  val condition =
    when (this.prefix) {
      ParamPrefixEnum.EQUAL, null ->
        return SearchQuery(
          """
            SELECT resourceId FROM NumberIndexEntity
            WHERE resourceType = ? AND index_name = ? AND index_value >= ? AND index_value < ?
            """,
          listOf(
            type.name,
            parameter.paramName,
            (value!! - precision).toDouble(),
            (value!! + precision).toDouble()
          )
        )
      ParamPrefixEnum.GREATERTHAN -> ">"
      ParamPrefixEnum.GREATERTHAN_OR_EQUALS -> ">="
      ParamPrefixEnum.LESSTHAN -> "<"
      ParamPrefixEnum.LESSTHAN_OR_EQUALS -> "<="
      ParamPrefixEnum.NOT_EQUAL ->
        return SearchQuery(
          """
            SELECT resourceId FROM NumberIndexEntity
            WHERE resourceType = ? AND index_name = ? AND index_value < ? OR index_value >= ?
            """,
          listOf(
            type.name,
            parameter.paramName,
            (value!! - precision).toDouble(),
            (value!! + precision).toDouble()
          )
        )
      // Ends_Before and Starts_After are not used with integer values.
      ParamPrefixEnum.ENDS_BEFORE ->
        if (value!!.scale() <= 0)
          throw IllegalArgumentException("Prefix not allowed for Integer type")
        else "<"
      ParamPrefixEnum.STARTS_AFTER ->
        if (value!!.scale() <= 0)
          throw IllegalArgumentException("Prefix not allowed for Integer type")
        else ">"
      // Approximate to a 10% range
      ParamPrefixEnum.APPROXIMATE -> {
        val range = value!!.divide(BigDecimal(10))
        return SearchQuery(
          """
            SELECT resourceId FROM NumberIndexEntity
            WHERE resourceType = ? AND index_name = ? AND index_value >= ? AND index_value <= ?
            """,
          listOf(
            type.name,
            parameter.paramName,
            (value!! - range).toDouble(),
            (value!! + range).toDouble()
          )
        )
      }
    }
  return SearchQuery(
    """
     SELECT resourceId FROM NumberIndexEntity
     WHERE resourceType = ? AND index_name = ? AND index_value $condition ?
       """,
    listOf(type.name, parameter.paramName, value!!.toDouble())
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

fun TokenFilter.query(type: ResourceType): SearchQuery {
  return SearchQuery(
    """
    SELECT resourceId FROM TokenIndexEntity
    WHERE resourceType = ? AND index_name = ? AND index_value = ?
    AND IFNULL(index_system,'') = ? 
    """,
    listOfNotNull(type.name, parameter!!.paramName, code, uri ?: "")
  )
}

fun List<SearchQuery>.intersect(): SearchQuery? {
  return if (isEmpty()) {
    null
  } else {
    SearchQuery(joinToString("\nINTERSECT\n") { it.query }, flatMap { it.args })
  }
}

val Order?.sqlString: String
  get() =
    when (this) {
      Order.ASCENDING -> "ASC"
      Order.DESCENDING -> "DESC"
      null -> ""
    }
