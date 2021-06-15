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
import java.math.BigDecimal
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

internal suspend fun <R : Resource> Search.execute(database: Database): List<R> {
  return database.search(getQuery())
}

internal suspend fun Search.count(database: Database): Long {
  return database.count(getQuery(true))
}

fun Search.getQuery(isCount: Boolean = false): SearchQuery {
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
    SELECT ${ if (isCount) "COUNT(*)" else "a.serializedResource" }
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

/**
 * Extension function that returns a SearchQuery based on the value and prefix of the NumberFilter
 */
fun NumberFilter.query(type: ResourceType): SearchQuery {

  val conditionParamPair = getConditionParamPair(prefix, value!!)

  return SearchQuery(
    """
     SELECT resourceId FROM NumberIndexEntity
     WHERE resourceType = ? AND index_name = ? AND ${conditionParamPair.first}
       """,
    listOf(type.name, parameter.paramName) + conditionParamPair.second
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

/**
 * Returns the condition and list of params required in NumberFilter.query see
 * https://www.hl7.org/fhir/search.html#number.
 */
private fun getConditionParamPair(
  prefix: ParamPrefixEnum?,
  value: BigDecimal
): Pair<String, List<Double>> {
  // Ends_Before and Starts_After are not used with integer values. see
  // https://www.hl7.org/fhir/search.html#prefix
  require(
    value.scale() > 0 ||
      (prefix != ParamPrefixEnum.STARTS_AFTER && prefix != ParamPrefixEnum.ENDS_BEFORE)
  ) { "Prefix $prefix not allowed for Integer type" }
  return when (prefix) {
    ParamPrefixEnum.EQUAL, null -> {
      val precision = value.getRange()
      "index_value >= ? AND index_value < ?" to
        listOf((value - precision).toDouble(), (value + precision).toDouble())
    }
    ParamPrefixEnum.GREATERTHAN -> "index_value > ?" to listOf(value.toDouble())
    ParamPrefixEnum.GREATERTHAN_OR_EQUALS -> "index_value >= ?" to listOf(value.toDouble())
    ParamPrefixEnum.LESSTHAN -> "index_value < ?" to listOf(value.toDouble())
    ParamPrefixEnum.LESSTHAN_OR_EQUALS -> "index_value <= ?" to listOf(value.toDouble())
    ParamPrefixEnum.NOT_EQUAL -> {
      val precision = value.getRange()
      "index_value < ? OR index_value >= ?" to
        listOf((value - precision).toDouble(), (value + precision).toDouble())
    }
    ParamPrefixEnum.ENDS_BEFORE -> {
      "index_value < ?" to listOf(value.toDouble())
    }
    ParamPrefixEnum.STARTS_AFTER -> {
      "index_value > ?" to listOf(value.toDouble())
    }
    // Approximate to a 10% range see https://www.hl7.org/fhir/search.html#prefix
    ParamPrefixEnum.APPROXIMATE -> {
      val range = value.divide(BigDecimal(10))
      "index_value >= ? AND index_value <= ?" to
        listOf((value - range).toDouble(), (value + range).toDouble())
    }
  }
}

/**
 * Returns the range in which the value should lie for it to be considered a match (@see
 * NumberFilter.query). The value is directly related to the scale of the BigDecimal.
 *
 * For example, a search with a value 100.00 (has a scale of 2) would match any value in [99.995,
 * 100.005) and the function returns 0.005.
 *
 * For Big integers which have a negative scale the function returns 5 For example A search with a
 * value 1000 would match any value in [995, 1005) and the function returns 5.
 */
private fun BigDecimal.getRange(): BigDecimal {
  return if (scale() >= 0) {
    BigDecimal(0.5).divide(BigDecimal(10).pow(scale()))
  } else {
    BigDecimal(5)
  }
}
