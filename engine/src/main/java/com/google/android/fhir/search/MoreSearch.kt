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

package com.google.android.fhir.search

import android.annotation.SuppressLint
import androidx.annotation.VisibleForTesting
import androidx.room.util.convertUUIDToByte
import ca.uhn.fhir.rest.gclient.DateClientParam
import ca.uhn.fhir.rest.gclient.IParam
import ca.uhn.fhir.rest.gclient.NumberClientParam
import ca.uhn.fhir.rest.gclient.StringClientParam
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import com.google.android.fhir.ConverterException
import com.google.android.fhir.DateProvider
import com.google.android.fhir.SearchResult
import com.google.android.fhir.UcumValue
import com.google.android.fhir.UnitConverter
import com.google.android.fhir.db.Database
import com.google.android.fhir.epochDay
import com.google.android.fhir.logicalId
import com.google.android.fhir.ucumUrl
import java.math.BigDecimal
import java.util.Date
import java.util.UUID
import kotlin.math.absoluteValue
import kotlin.math.roundToLong
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Resource
import timber.log.Timber

/**
 * The multiplier used to determine the range for the `ap` search prefix. See
 * https://www.hl7.org/fhir/search.html#prefix for more details.
 */
private const val APPROXIMATION_COEFFICIENT = 0.1

/**
 * SQLite supports signed and unsigned integers with a maximum length of 8 bytes. The signed
 * integers can range from `-9223372036854775808` to `+9223372036854775807`. See
 * [Storage Classes and Datatypes](https://www.sqlite.org/datatype3.html)
 */
private const val MIN_VALUE = "-9223372036854775808"
private const val MAX_VALUE = "9223372036854775808"

internal suspend fun <R : Resource> Search.execute(database: Database): List<SearchResult<R>> {
  val baseResources = database.search<R>(getQuery())
  val includedResources =
    if (forwardIncludes.isEmpty() || baseResources.isEmpty()) {
      null
    } else {
      database.searchForwardReferencedResources(
        getIncludeQuery(includeIds = baseResources.map { it.uuid }),
      )
    }
  val revIncludedResources =
    if (revIncludes.isEmpty() || baseResources.isEmpty()) {
      null
    } else {
      database.searchReverseReferencedResources(
        getRevIncludeQuery(
          includeIds = baseResources.map { "${it.resource.resourceType}/${it.resource.logicalId}" },
        ),
      )
    }

  return baseResources.map { (uuid, baseResource) ->
    SearchResult(
      baseResource,
      included =
        includedResources
          ?.asSequence()
          ?.filter { it.baseResourceUUID == uuid }
          ?.groupBy({ it.searchIndex }, { it.resource }),
      revIncluded =
        revIncludedResources
          ?.asSequence()
          ?.filter {
            it.baseResourceTypeWithId == "${baseResource.fhirType()}/${baseResource.logicalId}"
          }
          ?.groupBy({ it.resource.resourceType to it.searchIndex }, { it.resource }),
    )
  }
}

internal suspend fun Search.count(database: Database): Long {
  return database.count(getQuery(true))
}

fun Search.getQuery(isCount: Boolean = false): SearchQuery {
  return getQuery(isCount, null)
}

@VisibleForTesting
internal fun Search.getRevIncludeQuery(includeIds: List<String>): SearchQuery {
  val args = mutableListOf<Any>()
  val uuidsString = CharArray(includeIds.size) { '?' }.joinToString()

  fun generateFilterQuery(nestedSearch: NestedSearch): String {
    val (param, search) = nestedSearch
    val resourceToInclude = search.type
    args.add(resourceToInclude.name)
    args.add(param.paramName)
    args.addAll(includeIds)
    args.add(resourceToInclude.name)

    var filterQuery = ""
    val filters = search.getFilterQueries()
    val iterator = filters.listIterator()
    while (iterator.hasNext()) {
      iterator.next().let {
        filterQuery += it.query
        args.addAll(it.args)
      }

      if (iterator.hasNext()) {
        filterQuery +=
          if (search.operation == Operation.OR) {
            "\n UNION \n"
          } else {
            "\n INTERSECT \n"
          }
      }
    }
    return filterQuery
  }

  return revIncludes
    .map {
      val (join, order) =
        it.search.getSortOrder(otherTable = "re", groupByColumn = "rie.index_value")
      args.addAll(join.args)
      val filterQuery = generateFilterQuery(it)
      """
      SELECT rie.index_name, rie.index_value, re.serializedResource
      FROM ResourceEntity re
      JOIN ReferenceIndexEntity rie
      ON re.resourceUuid = rie.resourceUuid
      ${join.query}
      WHERE rie.resourceType = ?  AND rie.index_name = ?  AND rie.index_value IN ($uuidsString) AND re.resourceType = ?
      ${if (filterQuery.isNotEmpty()) "AND re.resourceUuid IN ($filterQuery)" else ""}
      $order
            """
        .trimIndent()
    }
    .joinToString("\nUNION ALL\n") {
      StringBuilder("SELECT * FROM (\n").append(it.trim()).append("\n)")
    }
    .split("\n")
    .filter { it.isNotBlank() }
    .joinToString("\n") { it.trim() }
    .let { SearchQuery(it, args) }
}

@SuppressLint("RestrictedApi")
@VisibleForTesting
internal fun Search.getIncludeQuery(includeIds: List<UUID>): SearchQuery {
  val args = mutableListOf<Any>()
  val baseResourceType = type
  val uuidsString = CharArray(includeIds.size) { '?' }.joinToString()

  fun generateFilterQuery(nestedSearch: NestedSearch): String {
    val (param, search) = nestedSearch
    val resourceToInclude = search.type
    args.add(baseResourceType.name)
    args.add(param.paramName)
    args.addAll(includeIds.map { convertUUIDToByte(it) })
    args.add(resourceToInclude.name)

    var filterQuery = ""
    val filters = search.getFilterQueries()
    val iterator = filters.listIterator()
    while (iterator.hasNext()) {
      iterator.next().let {
        filterQuery += it.query
        args.addAll(it.args)
      }

      if (iterator.hasNext()) {
        filterQuery +=
          if (search.operation == Operation.OR) {
            "\nUNION\n"
          } else {
            "\nINTERSECT\n"
          }
      }
    }
    return filterQuery
  }

  return forwardIncludes
    .map {
      val (join, order) =
        it.search.getSortOrder(otherTable = "re", groupByColumn = "rie.resourceuuid")
      args.addAll(join.args)
      val filterQuery = generateFilterQuery(it)
      """
      SELECT rie.index_name, rie.resourceUuid, re.serializedResource
      FROM ResourceEntity re
      JOIN ReferenceIndexEntity rie
      ON re.resourceType||"/"||re.resourceId = rie.index_value
      ${join.query}
      WHERE rie.resourceType = ?  AND rie.index_name = ?  AND rie.resourceUuid IN ($uuidsString) AND re.resourceType = ?
      ${if (filterQuery.isNotEmpty()) "AND re.resourceUuid IN ($filterQuery)" else ""}
      $order
      """
        .trimIndent()
    }
    .joinToString("\nUNION ALL\n") {
      StringBuilder("SELECT * FROM (\n").append(it.trim()).append("\n)")
    }
    .split("\n")
    .filter { it.isNotBlank() }
    .joinToString("\n") { it.trim() }
    .let { SearchQuery(it, args) }
}

private fun Search.getSortOrder(
  otherTable: String,
  isReferencedSearch: Boolean = false,
  groupByColumn: String = "",
): Pair<SearchQuery, String> {
  var sortJoinStatement = ""
  var sortOrderStatement = ""
  val args = mutableListOf<Any>()
  if (isReferencedSearch && count != null) {
    Timber.e("count not supported for [rev]include search.")
  }
  sort?.let { sort ->
    val sortTableNames =
      when (sort) {
        is StringClientParam -> listOf(SortTableInfo.STRING_SORT_TABLE_INFO)
        is NumberClientParam -> listOf(SortTableInfo.NUMBER_SORT_TABLE_INFO)
        // The DateClientParam maps to two index tables (Date without timezone info and DateTime
        // with timezone info). Any data field in any resource will only have index records in one
        // of the two tables. So we simply sort by both in the SQL query.
        is DateClientParam ->
          listOf(SortTableInfo.DATE_SORT_TABLE_INFO, SortTableInfo.DATE_TIME_SORT_TABLE_INFO)
        else -> throw NotImplementedError("Unhandled sort parameter of type ${sort::class}: $sort")
      }

    sortJoinStatement =
      sortTableNames
        .mapIndexed { index, sortTableName ->
          val tableAlias = 'b' + index
          //  spotless:off
      """
      LEFT JOIN ${sortTableName.tableName} $tableAlias
      ON $otherTable.resourceUuid = $tableAlias.resourceUuid AND $tableAlias.index_name = ?
      """
        //  spotless:on
        }
        .joinToString(separator = "\n")
    sortTableNames.forEach { _ -> args.add(sort.paramName) }

    sortOrderStatement +=
      generateGroupAndOrderQuery(sort, order!!, otherTable, groupByColumn, sortTableNames)
  }
  return Pair(SearchQuery(sortJoinStatement, args), sortOrderStatement)
}

/**
 * Sorting by a field that has multiple indexed values may result in duplicated resources. So, we
 * use `GROUP BY` + `HAVING` clause to find distinct values in specified order.
 *
 * To make the sorting order a bit predictable, we use MIN and MAX functions with `HAVING` to use
 * the corresponding values for GROUPING to find the distinct results.
 *
 * e.g. If there are Two Patients resources with multiple first names P1 ( first names =`3`, `1`)
 * and P2 (first names = `2`, `4`), when sorting them in
 *
 * *ASCENDING order*: MIN function is used so that the smallest names of both the patients are
 * considered for Grouping `[P1(`1`), P2(`2`)]`.
 *
 * *DESCENDING order*: MAX function is used so that the largest names of both the patients are
 * considered for Grouping `[P2(`4`), P1(`3`)]`.
 *
 * For the special case where the index value is NULL, we use the default 0 value and to complete
 * the expression, we check that the value is greater than [MIN_VALUE], the minimum value an INTEGER
 * type can store in SQLITE. The reason to check against [MIN_VALUE] rather that 0, since string is
 * always greater than integer (StringIndexEntity) and Date/DateTimeIndexEntity will always have
 * positive integer values, is because the NumberIndexEntity table may contain negative values in
 * it.
 *
 * Without the `>= MIN_VALUE` check, NULL values are not included in the results if the default
 * is 0.
 *
 * The default values provided in GROUP BY stage are not carried forward during the ORDER BY, so we
 * provide [MAX_VALUE] and [MIN_VALUE] as default in ORDER BY respectively for ASCENDING and
 * DESCENDING to make sure that results with null index values are always at the bottom.
 */
private fun generateGroupAndOrderQuery(
  sort: IParam,
  order: Order,
  otherTable: String,
  groupByColumn: String,
  sortTableNames: List<SortTableInfo>,
): String {
  var sortOrderStatement = ""
  val havingColumn =
    when (sort) {
      is StringClientParam,
      is NumberClientParam, -> "IFNULL(b.index_value,0)"
      /*  The DateClientParam is used for both Date and DateTime values and the value is present exclusively in either one of the DateIndexEntity or DateTimeIndexEntity tables.
      To find the MIN or MAX values, we add the values from both the tables. It results in the exact value as the other table would have null and hence default 0 would be added to actual date/datetime value.*/
      is DateClientParam -> "IFNULL(b.index_from,0) + IFNULL(c.index_from,0)"
      else -> throw NotImplementedError("Unhandled sort parameter of type ${sort::class}: $sort")
    }

  sortOrderStatement +=
    """
      GROUP BY $otherTable.resourceUuid ${if (groupByColumn.isNotEmpty()) ", $groupByColumn" else ""}
      HAVING ${if (order == Order.ASCENDING) "MIN($havingColumn) >= $MIN_VALUE" else "MAX($havingColumn) >= $MIN_VALUE"}
      
            """
      .trimIndent()
  val defaultValue = if (order == Order.ASCENDING) MAX_VALUE else MIN_VALUE
  sortTableNames.forEachIndexed { index, sortTableName ->
    val tableAlias = 'b' + index
    sortOrderStatement +=
      if (index == 0) {
        """
            ORDER BY IFNULL($tableAlias.${sortTableName.columnName}, $defaultValue) ${order.sqlString}
          """
          .trimIndent()
      } else {
        ", IFNULL($tableAlias.${SortTableInfo.DATE_TIME_SORT_TABLE_INFO.columnName}, $defaultValue) ${order.sqlString}"
      }
  }
  return sortOrderStatement
}

private fun Search.getFilterQueries() =
  (stringFilterCriteria +
      quantityFilterCriteria +
      numberFilterCriteria +
      referenceFilterCriteria +
      dateTimeFilterCriteria +
      tokenFilterCriteria +
      uriFilterCriteria)
    .map { it.query(type) }

internal fun Search.getQuery(
  isCount: Boolean = false,
  nestedContext: NestedContext? = null,
): SearchQuery {
  val (join, order) = getSortOrder(otherTable = "a")
  val sortJoinStatement = join.query
  val sortOrderStatement = order
  val sortArgs = join.args

  var filterStatement = ""
  val filterArgs = mutableListOf<Any>()
  val filterQuery = getFilterQueries()
  filterQuery.forEachIndexed { i, it ->
    filterStatement +=
      //  spotless:off
      """
      ${if (i == 0) "AND a.resourceUuid IN (" else "a.resourceUuid IN ("}
      ${it.query}
      )
      ${if (i != filterQuery.lastIndex) "${operation.logicalOperator} " else ""}
      """.trimIndent()
    //  spotless:on
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

  nestedSearches.nestedQuery(type, operation)?.let {
    filterStatement += it.query
    filterArgs.addAll(it.args)
  }
  val whereArgs = mutableListOf<Any>()
  val nestedArgs = mutableListOf<Any>()
  val query =
    when {
        isCount -> {
          //  spotless:off
        """ 
        SELECT COUNT(*)
        FROM ResourceEntity a
        $sortJoinStatement
        WHERE a.resourceType = ?
        $filterStatement
        $sortOrderStatement
        $limitStatement
        """
          //  spotless:on
        }
        nestedContext != null -> {
          whereArgs.add(nestedContext.param.paramName)
          val start = "${nestedContext.parentType.name}/".length + 1
          nestedArgs.add(nestedContext.parentType.name)
          //  spotless:off
        """
        SELECT resourceUuid
        FROM ResourceEntity a
        WHERE a.resourceType = ? AND a.resourceId IN (
        SELECT substr(a.index_value, $start)
        FROM ReferenceIndexEntity a
        $sortJoinStatement
        WHERE a.resourceType = ? AND a.index_name = ?
        $filterStatement
        $sortOrderStatement
        $limitStatement)
        """
          //  spotless:on
        }
        else ->
          //  spotless:off
        """ 
        SELECT a.resourceUuid, a.serializedResource
        FROM ResourceEntity a
        $sortJoinStatement
        WHERE a.resourceType = ?
        $filterStatement
        $sortOrderStatement
        $limitStatement
        """
      //  spotless:on
      }
      .split("\n")
      .filter { it.isNotBlank() }
      .joinToString("\n") { it.trim() }
  return SearchQuery(query, nestedArgs + sortArgs + type.name + whereArgs + filterArgs + limitArgs)
}

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
    // see https://www.hl7.org/fhir/search.html#prefix
    ParamPrefixEnum.APPROXIMATE -> {
      val currentDateType = DateType(Date.from(DateProvider().instant()), value.precision)
      val (diffStart, diffEnd) =
        getApproximateDateRange(value.rangeEpochDays, currentDateType.rangeEpochDays)

      ConditionParam(
        "index_from BETWEEN ? AND ? AND index_to BETWEEN ? AND ?",
        diffStart,
        diffEnd,
        diffStart,
        diffEnd,
      )
    }
    ParamPrefixEnum.STARTS_AFTER -> ConditionParam("index_from > ?", end)
    ParamPrefixEnum.ENDS_BEFORE -> ConditionParam("index_to < ?", start)
    ParamPrefixEnum.NOT_EQUAL ->
      ConditionParam(
        "index_from NOT BETWEEN ? AND ? OR index_to NOT BETWEEN ? AND ?",
        start,
        end,
        start,
        end,
      )
    ParamPrefixEnum.EQUAL ->
      ConditionParam(
        "index_from BETWEEN ? AND ? AND index_to BETWEEN ? AND ?",
        start,
        end,
        start,
        end,
      )
    ParamPrefixEnum.GREATERTHAN -> ConditionParam("index_to > ?", end)
    ParamPrefixEnum.GREATERTHAN_OR_EQUALS -> ConditionParam("index_to >= ?", start)
    ParamPrefixEnum.LESSTHAN -> ConditionParam("index_from < ?", start)
    ParamPrefixEnum.LESSTHAN_OR_EQUALS -> ConditionParam("index_from <= ?", end)
  }
}

internal fun getConditionParamPair(
  prefix: ParamPrefixEnum,
  value: DateTimeType,
): ConditionParam<Long> {
  val start = value.rangeEpochMillis.first
  val end = value.rangeEpochMillis.last
  return when (prefix) {
    // see https://www.hl7.org/fhir/search.html#prefix
    ParamPrefixEnum.APPROXIMATE -> {
      val currentDateTime = DateTimeType(Date.from(DateProvider().instant()), value.precision)
      val (diffStart, diffEnd) =
        getApproximateDateRange(value.rangeEpochMillis, currentDateTime.rangeEpochMillis)

      ConditionParam(
        "index_from BETWEEN ? AND ? AND index_to BETWEEN ? AND ?",
        diffStart,
        diffEnd,
        diffStart,
        diffEnd,
      )
    }
    ParamPrefixEnum.STARTS_AFTER -> ConditionParam("index_from > ?", end)
    ParamPrefixEnum.ENDS_BEFORE -> ConditionParam("index_to < ?", start)
    ParamPrefixEnum.NOT_EQUAL ->
      ConditionParam(
        "index_from NOT BETWEEN ? AND ? OR index_to NOT BETWEEN ? AND ?",
        start,
        end,
        start,
        end,
      )
    ParamPrefixEnum.EQUAL ->
      ConditionParam(
        "index_from BETWEEN ? AND ? AND index_to BETWEEN ? AND ?",
        start,
        end,
        start,
        end,
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
  value: BigDecimal,
): ConditionParam<Double> {
  // Ends_Before and Starts_After are not used with integer values. see
  // https://www.hl7.org/fhir/search.html#prefix
  require(
    value.scale() > 0 ||
      (prefix != ParamPrefixEnum.STARTS_AFTER && prefix != ParamPrefixEnum.ENDS_BEFORE),
  ) {
    "Prefix $prefix not allowed for Integer type"
  }
  return when (prefix) {
    ParamPrefixEnum.EQUAL,
    null, -> {
      val precision = value.getRange()
      ConditionParam(
        "index_value >= ? AND index_value < ?",
        (value - precision).toDouble(),
        (value + precision).toDouble(),
      )
    }
    ParamPrefixEnum.GREATERTHAN -> ConditionParam("index_value > ?", value.toDouble())
    ParamPrefixEnum.GREATERTHAN_OR_EQUALS -> ConditionParam("index_value >= ?", value.toDouble())
    ParamPrefixEnum.LESSTHAN -> ConditionParam("index_value < ?", value.toDouble())
    ParamPrefixEnum.LESSTHAN_OR_EQUALS -> ConditionParam("index_value <= ?", value.toDouble())
    ParamPrefixEnum.NOT_EQUAL -> {
      val precision = value.getRange()
      ConditionParam(
        "index_value < ? OR index_value >= ?",
        (value - precision).toDouble(),
        (value + precision).toDouble(),
      )
    }
    ParamPrefixEnum.ENDS_BEFORE -> {
      ConditionParam("index_value < ?", value.toDouble())
    }
    ParamPrefixEnum.STARTS_AFTER -> {
      ConditionParam("index_value > ?", value.toDouble())
    }
    ParamPrefixEnum.APPROXIMATE -> {
      val range = value.multiply(BigDecimal(APPROXIMATION_COEFFICIENT))
      ConditionParam(
        "index_value >= ? AND index_value <= ?",
        (value - range).toDouble(),
        (value + range).toDouble(),
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
  unit: String?,
): ConditionParam<Any> {
  var canonicalizedUnit = unit
  var canonicalizedValue = value

  // Canonicalize the unit if possible. For example, 1 kg will be canonicalized to 1000 g
  if (system == ucumUrl && unit != null) {
    try {
      val ucumValue = UnitConverter.getCanonicalFormOrOriginal(UcumValue(unit, value))
      canonicalizedUnit = ucumValue.code
      canonicalizedValue = ucumValue.value
    } catch (exception: ConverterException) {
      exception.printStackTrace()
    }
  }

  val queryBuilder = StringBuilder()
  val argList = mutableListOf<Any>()

  // system condition will be preceded by a value condition so if exists append an AND here
  if (system != null) {
    queryBuilder.append("index_system = ? AND ")
    argList.add(system)
  }

  // if the unit condition will be preceded by a value condition so if exists append an AND here
  if (canonicalizedUnit != null) {
    queryBuilder.append("index_code = ? AND ")
    argList.add(canonicalizedUnit)
  }

  // add value condition
  // value cannot be null -> the value condition will always be present
  val valueConditionParam = getConditionParamPair(prefix, canonicalizedValue)
  queryBuilder.append(valueConditionParam.condition)
  argList.addAll(valueConditionParam.params)

  return ConditionParam(queryBuilder.toString(), argList)
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

internal val DateType.rangeEpochDays: LongRange
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
internal val DateTimeType.rangeEpochMillis
  get() = LongRange(value.time, precision.add(value, 1).time - 1)

data class ConditionParam<T>(val condition: String, val params: List<T>) {
  constructor(condition: String, vararg params: T) : this(condition, params.asList())
}

private enum class SortTableInfo(val tableName: String, val columnName: String) {
  STRING_SORT_TABLE_INFO("StringIndexEntity", "index_value"),
  NUMBER_SORT_TABLE_INFO("NumberIndexEntity", "index_value"),
  DATE_SORT_TABLE_INFO("DateIndexEntity", "index_from"),
  DATE_TIME_SORT_TABLE_INFO("DateTimeIndexEntity", "index_from"),
}

private fun getApproximateDateRange(
  valueRange: LongRange,
  currentRange: LongRange,
  approximationCoefficient: Double = APPROXIMATION_COEFFICIENT,
): ApproximateDateRange {
  return ApproximateDateRange(
    (valueRange.first -
        approximationCoefficient * (valueRange.first - currentRange.first).absoluteValue)
      .roundToLong(),
    (valueRange.last +
        approximationCoefficient * (valueRange.last - currentRange.last).absoluteValue)
      .roundToLong(),
  )
}

private data class ApproximateDateRange(val start: Long, val end: Long)
