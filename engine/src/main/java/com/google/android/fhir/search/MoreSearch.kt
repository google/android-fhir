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
import com.google.android.fhir.ConverterException
import com.google.android.fhir.UcumValue
import com.google.android.fhir.UnitConverter
import com.google.android.fhir.db.Database
import com.google.android.fhir.epochDay
import com.google.android.fhir.search.filter.FilterCriterion
import com.google.android.fhir.ucumUrl
import java.math.BigDecimal
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

internal suspend fun <R : Resource> Search.execute(database: Database): List<R> {
  return database.search(getQuery())
}

internal suspend fun Search.count(database: Database): Long {
  return database.count(getQuery(true))
}

fun Search.getQuery(isCount: Boolean = false): SearchQuery {
  return getQuery(isCount, null)
}

internal fun Search.getQuery(
  isCount: Boolean = false,
  nestedContext: NestedContext? = null
): SearchQuery {
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
  val allFilters =
    stringFilterCriteria +
      referenceFilterCriteria +
      dateTimeFilterCriteria +
      tokenFilterCriteria +
      numberFilterCriteria +
      quantityFilterCriteria

  val filterQuery =
    (allFilters.mapNonSingleParamValues(type) + allFilters.joinSingleParamValues(type, operation))
      .filterNotNull()
  filterQuery.forEachIndexed { i, it ->
    filterStatement +=
      """
      ${if (i == 0) "AND a.resourceId IN (" else "a.resourceId IN ("}
      ${it.query}
      )
      ${if (i != filterQuery.lastIndex) operation.name else ""}
      """.trimIndent()
    filterArgs.addAll(it.args)
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

  filterStatement += nestedSearches.nestedQuery(filterStatement, filterArgs, type, operation)
  val whereArgs = mutableListOf<Any>()
  val query =
    when {
        isCount -> {
          """ 
        SELECT COUNT(*)
        FROM ResourceEntity a
        $sortJoinStatement
        WHERE a.resourceType = ?
        $filterStatement
        $sortOrderStatement
        $limitStatement
        """
        }
        nestedContext != null -> {
          whereArgs.add(nestedContext.param.paramName)
          val start = "${nestedContext.parentType.name}/".length + 1
          """ 
        SELECT substr(a.index_value, $start)
        FROM ReferenceIndexEntity a
        $sortJoinStatement
        WHERE a.resourceType = ? AND a.index_name = ?
        $filterStatement
        $sortOrderStatement
        $limitStatement
        """
        }
        else ->
          """ 
        SELECT a.serializedResource
        FROM ResourceEntity a
        $sortJoinStatement
        WHERE a.resourceType = ?
        $filterStatement
        $sortOrderStatement
        $limitStatement
        """
      }
      .split("\n")
      .filter { it.isNotBlank() }
      .joinToString("\n") { it.trim() }
  return SearchQuery(query, sortArgs + type.name + whereArgs + filterArgs + limitArgs)
}

private fun List<FilterCriterion>.query(
  type: ResourceType,
  op: Operation = Operation.OR
): SearchQuery {
  return map { it.query(type) }.let {
    SearchQuery(
      it.joinToString("\n${op.resultSetCombiningOperator}\n") { it.query },
      it.flatMap { it.args }
    )
  }
}

internal fun List<SearchQuery>.joinSet(operation: Operation): SearchQuery? {
  return if (isEmpty()) {
    null
  } else {
    SearchQuery(
      joinToString("\n${operation.resultSetCombiningOperator}\n") { it.query },
      flatMap { it.args }
    )
  }
}

/**
 * Maps all the [FilterCriterion]s with multiple values into respective [SearchQuery] joined by
 * [Operation.resultSetCombiningOperator] set in [Pair.second]. e.g. filter(Patient.GIVEN, {"John"},
 * {"Jane"},OR) AND filter(Patient.FAMILY, {"Doe"}, {"Roe"},OR) will result in SearchQuery( id in
 * (given="John" UNION given="Jane")) and SearchQuery( id in (family="Doe" UNION name="Roe")) and
 */
private fun List<FilterCriteria>.mapNonSingleParamValues(type: ResourceType) =
  filterNot { it.filters.size == 1 }.map { it.filters.query(type, it.operation) }

/**
 * Takes all the [FilterCriterion]s with single values and converts them into a single [SearchQuery]
 * joined by [Operation.resultSetCombiningOperator] set in [Search.operation]. e.g.
 * filter(Patient.GIVEN, {"John"}) OR filter(Patient.FAMILY, {"Doe"}) will result in SearchQuery( id
 * in (given="John" UNION family="Doe"))
 */
private fun List<FilterCriteria>.joinSingleParamValues(
  type: ResourceType,
  op: Operation = Operation.AND
) = filter { it.filters.size == 1 }.map { it.filters.query(type, op) }.joinSet(op)

private val Order?.sqlString: String
  get() =
    when (this) {
      Order.ASCENDING -> "ASC"
      Order.DESCENDING -> "DESC"
      null -> ""
    }

internal fun getConditionParamPair(prefix: ParamPrefixEnum, value: DateType): ConditionParam<Long> {
  val start = value.rangeEpochDays.first
  val end = value.rangeEpochDays.last
  return when (prefix) {
    ParamPrefixEnum.APPROXIMATE -> TODO("Not Implemented")
    // see https://github.com/google/android-fhir/issues/568
    // https://www.hl7.org/fhir/search.html#prefix
    ParamPrefixEnum.STARTS_AFTER -> ConditionParam("index_from > ?", end)
    ParamPrefixEnum.ENDS_BEFORE -> ConditionParam("index_to < ?", start)
    ParamPrefixEnum.NOT_EQUAL ->
      ConditionParam(
        "index_from NOT BETWEEN ? AND ? OR index_to NOT BETWEEN ? AND ?",
        start,
        end,
        start,
        end
      )
    ParamPrefixEnum.EQUAL ->
      ConditionParam(
        "index_from BETWEEN ? AND ? AND index_to BETWEEN ? AND ?",
        start,
        end,
        start,
        end
      )
    ParamPrefixEnum.GREATERTHAN -> ConditionParam("index_to > ?", end)
    ParamPrefixEnum.GREATERTHAN_OR_EQUALS -> ConditionParam("index_to >= ?", start)
    ParamPrefixEnum.LESSTHAN -> ConditionParam("index_from < ?", start)
    ParamPrefixEnum.LESSTHAN_OR_EQUALS -> ConditionParam("index_from <= ?", end)
  }
}

internal fun getConditionParamPair(
  prefix: ParamPrefixEnum,
  value: DateTimeType
): ConditionParam<Long> {
  val start = value.rangeEpochMillis.first
  val end = value.rangeEpochMillis.last
  return when (prefix) {
    ParamPrefixEnum.APPROXIMATE -> TODO("Not Implemented")
    // see https://github.com/google/android-fhir/issues/568
    // https://www.hl7.org/fhir/search.html#prefix
    ParamPrefixEnum.STARTS_AFTER -> ConditionParam("index_from > ?", end)
    ParamPrefixEnum.ENDS_BEFORE -> ConditionParam("index_to < ?", start)
    ParamPrefixEnum.NOT_EQUAL ->
      ConditionParam(
        "(index_from NOT BETWEEN ? AND ? OR index_to NOT BETWEEN ? AND ?)",
        start,
        end,
        start,
        end
      )
    ParamPrefixEnum.EQUAL ->
      ConditionParam(
        "index_from BETWEEN ? AND ? AND index_to BETWEEN ? AND ?",
        start,
        end,
        start,
        end
      )
    ParamPrefixEnum.GREATERTHAN -> ConditionParam("index_to > ?", end)
    ParamPrefixEnum.GREATERTHAN_OR_EQUALS -> ConditionParam("index_to >= ?", start)
    ParamPrefixEnum.LESSTHAN -> ConditionParam("index_from < ?", start)
    ParamPrefixEnum.LESSTHAN_OR_EQUALS -> ConditionParam("index_from <= ?", end)
  }
}

/**
 * Returns the condition and list of params required in NumberFilter.query see
 * https://www.hl7.org/fhir/search.html#number.
 */
internal fun getConditionParamPair(
  prefix: ParamPrefixEnum?,
  value: BigDecimal
): ConditionParam<Double> {
  // Ends_Before and Starts_After are not used with integer values. see
  // https://www.hl7.org/fhir/search.html#prefix
  require(
    value.scale() > 0 ||
      (prefix != ParamPrefixEnum.STARTS_AFTER && prefix != ParamPrefixEnum.ENDS_BEFORE)
  ) { "Prefix $prefix not allowed for Integer type" }
  return when (prefix) {
    ParamPrefixEnum.EQUAL, null -> {
      val precision = value.getRange()
      ConditionParam(
        "index_value >= ? AND index_value < ?",
        (value - precision).toDouble(),
        (value + precision).toDouble()
      )
    }
    ParamPrefixEnum.GREATERTHAN -> ConditionParam("index_value > ?", value.toDouble())
    ParamPrefixEnum.GREATERTHAN_OR_EQUALS -> ConditionParam("index_value >= ?", value.toDouble())
    ParamPrefixEnum.LESSTHAN -> ConditionParam("index_value < ?", value.toDouble())
    ParamPrefixEnum.LESSTHAN_OR_EQUALS -> ConditionParam("index_value <= ?", value.toDouble())
    ParamPrefixEnum.NOT_EQUAL -> {
      val precision = value.getRange()
      ConditionParam(
        "(index_value < ? OR index_value >= ?)",
        (value - precision).toDouble(),
        (value + precision).toDouble()
      )
    }
    ParamPrefixEnum.ENDS_BEFORE -> {
      ConditionParam("index_value < ?", value.toDouble())
    }
    ParamPrefixEnum.STARTS_AFTER -> {
      ConditionParam("index_value > ?", value.toDouble())
    }
    // Approximate to a 10% range see https://www.hl7.org/fhir/search.html#prefix
    ParamPrefixEnum.APPROXIMATE -> {
      val range = value.divide(BigDecimal(10))
      ConditionParam(
        "index_value >= ? AND index_value <= ?",
        (value - range).toDouble(),
        (value + range).toDouble()
      )
    }
  }
}

/**
 * Returns the condition and list of params required in Quantity.query see
 * https://www.hl7.org/fhir/search.html#quantity.
 */
internal fun getConditionParamPair(
  prefix: ParamPrefixEnum?,
  value: BigDecimal,
  system: String?,
  unit: String?
): ConditionParam<Any> {
  // value cannot be null -> the value condition will always be present
  val valueConditionParam = getConditionParamPair(prefix, value)
  val argList = mutableListOf<Any>()

  val condition = StringBuilder()
  val canonicalCondition = StringBuilder()
  val nonCanonicalCondition = StringBuilder()

  // system condition will be preceded by a value condition so if exists append an AND here
  if (system != null) {
    argList.add(system)
    condition.append("index_system = ? AND ")
  }
  // if the unit condition will be preceded by a value condition so if exists append an AND here
  if (unit != null) {
    argList.add(unit)
    if (condition.isNotEmpty()) {
      nonCanonicalCondition.append("index_code = ? AND ")
    } else {
      nonCanonicalCondition.append("(index_code = ? OR index_unit = ?) AND ")
      argList.add(unit)
    }
  }

  // add value condition
  nonCanonicalCondition.append(valueConditionParam.condition)
  argList.addAll(valueConditionParam.params)

  if (system == ucumUrl && unit != null) {
    try {
      val ucumUnit = UnitConverter.getCanonicalForm(UcumValue(unit, value))
      val canonicalConditionParam = getConditionParamPair(prefix, ucumUnit.value)
      argList.add(ucumUnit.code)
      argList.addAll(canonicalConditionParam.params)
      canonicalCondition
        .append("index_canonicalCode = ? AND ")
        .append(canonicalConditionParam.condition.replace("index_value", "index_canonicalValue"))
    } catch (exception: ConverterException) {
      exception.printStackTrace()
    }
  }

  // Add OR only when canonical match is possible
  if (canonicalCondition.isNotEmpty()) {
    condition.append("($nonCanonicalCondition OR $canonicalCondition)")
  } else {
    condition.append(nonCanonicalCondition)
  }
  return ConditionParam(condition.toString(), argList)
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

private val DateType.rangeEpochDays: LongRange
  get() {
    return LongRange(value.epochDay, precision.add(value, 1).epochDay - 1)
  }

/**
 * The range of the range of the Date's epoch Timestamp. The value is related to the precision of
 * the DateTimeType
 *
 * For example 2001-01-01 includes all values on the given day and thus this functions will return
 * 978307200 (epoch timestamp of 2001-01-01) and 978393599 ( which is one second less than the epoch
 * of 2001-01-02)
 */
private val DateTimeType.rangeEpochMillis
  get() = LongRange(value.time, precision.add(value, 1).time - 1)

internal data class ConditionParam<T>(val condition: String, val params: List<T>) {
  constructor(condition: String, vararg params: T) : this(condition, params.asList())
}
