/*
 * Copyright 2025-2026 Google LLC
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

import com.google.android.fhir.ConverterException
import com.google.android.fhir.SearchResult
import com.google.android.fhir.UcumValue
import com.google.android.fhir.UnitConverter
import com.google.android.fhir.db.Database
import com.google.android.fhir.db.ResourceWithUUID
import com.google.android.fhir.resourceType
import com.google.android.fhir.ucumUrl
import com.google.fhir.model.r4.FhirDate
import com.google.fhir.model.r4.FhirDateTime
import com.google.fhir.model.r4.Resource
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlin.math.absoluteValue
import kotlin.math.roundToLong
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant

/**
 * The multiplier used to determine the range for the `ap` search prefix. See
 * https://www.hl7.org/fhir/search.html#prefix for more details.
 */
private const val APPROXIMATION_COEFFICIENT = 0.1

private const val MIN_VALUE = "-9223372036854775808"
private const val MAX_VALUE = "9223372036854775808"

internal suspend fun <R : Resource> Search.execute(database: Database): List<SearchResult<R>> {
  val baseResources = database.search<R>(getQuery())

  val includedResources =
    if (forwardIncludes.isEmpty() || baseResources.isEmpty()) {
      null
    } else {
      val uuids = baseResources.map { it.uuid.toString() }
      database.searchReferencedResources(getIncludeQuery(uuids))
    }

  val revIncludedResources =
    if (revIncludes.isEmpty() || baseResources.isEmpty()) {
      null
    } else {
      val typeIdPairs =
        baseResources.map { "${it.resource.resourceType}/${(it.resource as Resource).id}" }
      database.searchReferencedResources(getRevIncludeQuery(typeIdPairs))
    }

  return baseResources.map { (uuid, baseResource) ->
    SearchResult(
      baseResource,
      included =
        includedResources
          ?.asSequence()
          ?.filter { it.baseId == uuid.toString() }
          ?.groupBy({ it.searchIndex }, { it.resource }),
      revIncluded =
        revIncludedResources
          ?.asSequence()
          ?.filter {
            it.baseId == "${(baseResource as Resource).resourceType}/${baseResource.id}"
          }
          ?.groupBy(
            { com.google.fhir.model.r4.terminologies.ResourceType.fromCode(it.resource.resourceType) to it.searchIndex },
            { it.resource },
          ),
    )
  }
}

internal suspend fun Search.count(database: Database): Long {
  return database.count(getQuery(true))
}

fun Search.getQuery(isCount: Boolean = false): SearchQuery {
  return getQuery(isCount, null)
}

internal fun Search.getRevIncludeQuery(includeIds: List<String>): SearchQuery {
  val args = mutableListOf<Any>()
  val uuidsString = CharArray(includeIds.size) { '?' }.joinToString()

  fun generateFilterQuery(nestedSearch: NestedSearch): String {
    val (param, search) = nestedSearch
    val resourceToInclude = search.type
    args.add(resourceToInclude.name)
    args.add(param.paramName)
    args.addAll(includeIds)

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
          if (search.operation == Operation.OR) "\n UNION \n" else "\n INTERSECT \n"
      }
    }
    if (filters.isEmpty()) args.add(resourceToInclude.name)
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
      WHERE rie.resourceType = ?  AND rie.index_name = ?  AND rie.index_value IN ($uuidsString)
      ${if (filterQuery.isNotBlank()) "AND re.resourceUuid IN ($filterQuery)" else "AND re.resourceType = ?"}
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

internal fun Search.getIncludeQuery(includeIds: List<String>): SearchQuery {
  val args = mutableListOf<Any>()
  val baseResourceType = type
  val uuidsString = CharArray(includeIds.size) { '?' }.joinToString()

  fun generateFilterQuery(nestedSearch: NestedSearch): String {
    val (param, search) = nestedSearch
    val resourceToInclude = search.type
    args.add(baseResourceType.name)
    args.add(param.paramName)
    args.addAll(includeIds)

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
          if (search.operation == Operation.OR) "\nUNION\n" else "\nINTERSECT\n"
      }
    }
    if (filters.isEmpty()) args.add(resourceToInclude.name)
    return filterQuery
  }

  return forwardIncludes
    .map {
      val (join, order) =
        it.search.getSortOrder(otherTable = "re", groupByColumn = "rie.resourceUuid")
      args.addAll(join.args)
      val filterQuery = generateFilterQuery(it)
      """
      SELECT rie.index_name, rie.resourceUuid, re.serializedResource
      FROM ResourceEntity re
      JOIN ReferenceIndexEntity rie
      ON re.resourceType||"/"||re.resourceId = rie.index_value
      ${join.query}
      WHERE rie.resourceType = ?  AND rie.index_name = ?  AND rie.resourceUuid IN ($uuidsString)
      ${if (filterQuery.isNotBlank()) "AND re.resourceUuid IN ($filterQuery)" else "AND re.resourceType = ?"}
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

  sort?.let { sort ->
    val sortTableNames =
      when (sort) {
        is StringClientParam -> listOf(SortTableInfo.STRING_SORT_TABLE_INFO)
        is NumberClientParam -> listOf(SortTableInfo.NUMBER_SORT_TABLE_INFO)
        is DateClientParam ->
          listOf(SortTableInfo.DATE_SORT_TABLE_INFO, SortTableInfo.DATE_TIME_SORT_TABLE_INFO)
        else -> throw NotImplementedError("Unhandled sort parameter of type ${sort::class}: $sort")
      }

    sortJoinStatement =
      sortTableNames
        .mapIndexed { index, sortTableName ->
          val tableAlias = 'b' + index
          """
          LEFT JOIN ${sortTableName.tableName} $tableAlias
          ON $otherTable.resourceUuid = $tableAlias.resourceUuid AND $tableAlias.index_name = ?
          """
        }
        .joinToString(separator = "\n")
    sortTableNames.forEach { _ -> args.add(sort.paramName) }

    sortOrderStatement +=
      generateGroupAndOrderQuery(sort, order!!, otherTable, groupByColumn, sortTableNames)
  }
  return Pair(SearchQuery(sortJoinStatement, args), sortOrderStatement)
}

private fun generateGroupAndOrderQuery(
  sort: ClientParam,
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

  val filterQuery = getFilterQueries()
  val filterQueryStatement =
    filterQuery.joinToString(separator = "${operation.logicalOperator} ") {
      """
      a.resourceUuid IN (
      ${it.query}
      )

      """.trimIndent()
    }
  val filterQueryArgs = filterQuery.flatMap { it.args }

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

  val nestedFilterQuery = nestedSearches.nestedQuery(type, operation)
  val nestedQueryFilterStatement = nestedFilterQuery?.query ?: ""
  val nestedQueryFilterArgs = nestedFilterQuery?.args ?: emptyList()

  val filterStatement =
    listOf(filterQueryStatement, nestedQueryFilterStatement)
      .filter { it.isNotBlank() }
      .joinToString(separator = " AND ")
      .ifBlank { "a.resourceType = ?" }
  val filterArgs = (filterQueryArgs + nestedQueryFilterArgs).ifEmpty { listOf(type.name) }

  val whereArgs = mutableListOf<Any>()
  val nestedArgs = mutableListOf<Any>()
  val query =
    when {
        isCount -> {
          """
          SELECT COUNT(*)
          FROM ResourceEntity a
          $sortJoinStatement
          WHERE $filterStatement
          $sortOrderStatement
          $limitStatement
          """
        }
        nestedContext != null -> {
          whereArgs.add(nestedContext.param.paramName)
          val start = "${nestedContext.parentType.name}/".length + 1
          nestedArgs.add(nestedContext.parentType.name)
          """
          SELECT resourceUuid
          FROM ResourceEntity a
          WHERE a.resourceType = ? AND a.resourceId IN (
          SELECT substr(a.index_value, $start)
          FROM ReferenceIndexEntity a
          $sortJoinStatement
          WHERE a.index_name = ? AND $filterStatement
          $sortOrderStatement
          $limitStatement)
          """
        }
        else ->
          """
          SELECT a.resourceUuid, a.serializedResource
          FROM ResourceEntity a
          $sortJoinStatement
          WHERE $filterStatement
          $sortOrderStatement
          $limitStatement
          """
      }
      .split("\n")
      .filter { it.isNotBlank() }
      .joinToString("\n") { it.trim() }

  return SearchQuery(
    query,
    nestedArgs + sortArgs + whereArgs + filterArgs + limitArgs,
  )
}

private val Order?.sqlString: String
  get() =
    when (this) {
      Order.ASCENDING -> "ASC"
      Order.DESCENDING -> "DESC"
      null -> ""
    }

// --- Condition param pair helpers ---

internal fun getConditionParamPairForDate(
  prefix: ParamPrefixEnum,
  value: FhirDate,
): ConditionParam<Long> {
  val (start, end) = fhirDateToEpochDayRange(value)
  return when (prefix) {
    ParamPrefixEnum.APPROXIMATE -> {
      val now = Clock.System.now().toEpochMilliseconds()
      val nowDay = now / 86400000L
      val currentRange = nowDay to nowDay
      val (diffStart, diffEnd) =
        getApproximateDateRange(start..end, currentRange.first..currentRange.second)
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

internal fun getConditionParamPairForDateTime(
  prefix: ParamPrefixEnum,
  value: FhirDateTime,
): ConditionParam<Long> {
  val (start, end) = fhirDateTimeToEpochMillisRange(value)
  return when (prefix) {
    ParamPrefixEnum.APPROXIMATE -> {
      val nowMs = Clock.System.now().toEpochMilliseconds()
      val (diffStart, diffEnd) =
        getApproximateDateRange(start..end, nowMs..nowMs)
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
  require(
    (value.precision - 1 - value.exponent) > 0 ||
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
        (value - precision).doubleValue(false),
        (value + precision).doubleValue(false),
      )
    }
    ParamPrefixEnum.GREATERTHAN ->
      ConditionParam("index_value > ?", value.doubleValue(false))
    ParamPrefixEnum.GREATERTHAN_OR_EQUALS ->
      ConditionParam("index_value >= ?", value.doubleValue(false))
    ParamPrefixEnum.LESSTHAN ->
      ConditionParam("index_value < ?", value.doubleValue(false))
    ParamPrefixEnum.LESSTHAN_OR_EQUALS ->
      ConditionParam("index_value <= ?", value.doubleValue(false))
    ParamPrefixEnum.NOT_EQUAL -> {
      val precision = value.getRange()
      ConditionParam(
        "index_value < ? OR index_value >= ?",
        (value - precision).doubleValue(false),
        (value + precision).doubleValue(false),
      )
    }
    ParamPrefixEnum.ENDS_BEFORE ->
      ConditionParam("index_value < ?", value.doubleValue(false))
    ParamPrefixEnum.STARTS_AFTER ->
      ConditionParam("index_value > ?", value.doubleValue(false))
    ParamPrefixEnum.APPROXIMATE -> {
      val range = value.multiply(BigDecimal.fromDouble(APPROXIMATION_COEFFICIENT))
      ConditionParam(
        "index_value >= ? AND index_value <= ?",
        (value - range).doubleValue(false),
        (value + range).doubleValue(false),
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

  if (system == ucumUrl && unit != null) {
    try {
      val ucumValue = UnitConverter.getCanonicalFormOrOriginal(UcumValue(unit, value))
      canonicalizedUnit = ucumValue.code
      canonicalizedValue = ucumValue.value
    } catch (_: ConverterException) {
      // Fall through with original values
    }
  }

  val queryBuilder = StringBuilder()
  val argList = mutableListOf<Any>()

  if (system != null) {
    queryBuilder.append("index_system = ? AND ")
    argList.add(system)
  }

  if (canonicalizedUnit != null) {
    queryBuilder.append("index_code = ? AND ")
    argList.add(canonicalizedUnit)
  }

  val valueConditionParam = getConditionParamPair(prefix, canonicalizedValue)
  queryBuilder.append(valueConditionParam.condition)
  argList.addAll(valueConditionParam.params)

  return ConditionParam(queryBuilder.toString(), argList)
}

/**
 * Returns the range for an implicit precision search (see
 * https://www.hl7.org/fhir/search.html#number). The value is directly related to the number of
 * decimal digits.
 *
 * For example, a search with a value 100.00 (has 2 decimal places) would match any value in
 * [99.995, 100.005) and the function returns 0.005.
 *
 * For integers which have no decimal places the function returns 5. For example a search with a
 * value 1000 would match any value in [995, 1005) and the function returns 5.
 *
 * Note: ionspin BigDecimal's `scale` property comes from DecimalMode and is -1 when unset. We
 * compute Java-style scale (number of decimal places) from the exponent instead.
 */
private fun BigDecimal.getRange(): BigDecimal {
  // In ionspin BigDecimal, value = significand * 10^(exponent - precision + 1).
  // Java-style scale (number of decimal places) = precision - 1 - exponent.
  // For example: 5.403 → significand=5403, exponent=0, precision=4 → javaScale=3
  //              1000  → significand=1,    exponent=3, precision=1 → javaScale=-3 (integer)
  val javaScale = precision - 1 - exponent
  return if (javaScale > 0) {
    BigDecimal.fromDouble(0.5).divide(BigDecimal.fromInt(10).pow(javaScale))
  } else {
    BigDecimal.fromInt(5)
  }
}

data class ConditionParam<T>(val condition: String, val params: List<T>) {
  constructor(condition: String, vararg params: T) : this(condition, params.asList())

  val queryString = if (params.size > 1) "($condition)" else condition
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

// --- Date utility functions (reused from ResourceIndexer patterns) ---

internal fun fhirDateToEpochDayRange(date: FhirDate): Pair<Long, Long> =
  when (date) {
    is FhirDate.Date -> {
      val epochDay = date.date.toEpochDays().toLong()
      epochDay to epochDay
    }
    is FhirDate.YearMonth -> {
      val firstDay = LocalDate(date.value.year, date.value.month, 1)
      val nextMonth = firstDay.plus(1, DateTimeUnit.MONTH)
      firstDay.toEpochDays().toLong() to (nextMonth.toEpochDays().toLong() - 1)
    }
    is FhirDate.Year -> {
      val firstDay = LocalDate(date.value, 1, 1)
      val nextYear = LocalDate(date.value + 1, 1, 1)
      firstDay.toEpochDays().toLong() to (nextYear.toEpochDays().toLong() - 1)
    }
  }

internal fun fhirDateTimeToEpochMillis(dateTime: FhirDateTime): Long =
  when (dateTime) {
    is FhirDateTime.DateTime ->
      dateTime.dateTime.toInstant(dateTime.utcOffset).toEpochMilliseconds()
    is FhirDateTime.Date ->
      dateTime.date.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
    is FhirDateTime.YearMonth -> {
      val firstDay = LocalDate(dateTime.value.year, dateTime.value.month, 1)
      firstDay.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
    }
    is FhirDateTime.Year -> {
      val firstDay = LocalDate(dateTime.value, 1, 1)
      firstDay.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
    }
  }

internal fun fhirDateTimeToEndEpochMillis(dateTime: FhirDateTime): Long =
  when (dateTime) {
    is FhirDateTime.DateTime ->
      dateTime.dateTime.toInstant(dateTime.utcOffset).toEpochMilliseconds()
    is FhirDateTime.Date -> {
      val nextDay = dateTime.date.plus(1, DateTimeUnit.DAY)
      nextDay.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds() - 1
    }
    is FhirDateTime.YearMonth -> {
      val firstDay = LocalDate(dateTime.value.year, dateTime.value.month, 1)
      val nextMonth = firstDay.plus(1, DateTimeUnit.MONTH)
      nextMonth.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds() - 1
    }
    is FhirDateTime.Year -> {
      val nextYear = LocalDate(dateTime.value + 1, 1, 1)
      nextYear.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds() - 1
    }
  }

internal fun fhirDateTimeToEpochMillisRange(dateTime: FhirDateTime): Pair<Long, Long> =
  fhirDateTimeToEpochMillis(dateTime) to fhirDateTimeToEndEpochMillis(dateTime)

/** Result of a referenced resource search (used for include/revInclude). */
internal data class ReferencedResourceResult(
  val searchIndex: String,
  val baseId: String,
  val resource: Resource,
)
