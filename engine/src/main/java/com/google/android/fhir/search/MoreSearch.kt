/*
 * Copyright 2023 Google LLC
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

import ca.uhn.fhir.rest.gclient.DateClientParam
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
import kotlin.math.absoluteValue
import kotlin.math.roundToLong
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Resource

/**
 * The multiplier used to determine the range for the `ap` search prefix. See
 * https://www.hl7.org/fhir/search.html#prefix for more details.
 */
private const val APPROXIMATION_COEFFICIENT = 0.1

internal suspend fun <R : Resource> Search.execute(database: Database): List<SearchResult<R>> {
  val baseResources = database.search<R>(getQuery())
  val includedResources =
    if (forwardIncludes.isEmpty() || baseResources.isEmpty()) {
      null
    } else {
      database.searchReferencedResources(
        getIncludeQuery(includeIds = baseResources.map { it.logicalId }),
      )
    }
  val revIncludedResources =
    if (revIncludes.isEmpty() || baseResources.isEmpty()) {
      null
    } else {
      database.searchReferencedResources(
        getRevIncludeQuery(includeIds = baseResources.map { "${it.resourceType}/${it.logicalId}" }),
      )
    }

  return baseResources.map { baseResource ->
    SearchResult(
      baseResource,
      included =
        includedResources
          ?.asSequence()
          ?.filter { it.idOfBaseResourceOnWhichThisMatched == baseResource.logicalId }
          ?.groupBy({ it.matchingIndex }, { it.resource }),
      revIncluded =
        revIncludedResources
          ?.asSequence()
          ?.filter {
            it.idOfBaseResourceOnWhichThisMatched ==
              "${baseResource.fhirType()}/${baseResource.logicalId}"
          }
          ?.groupBy({ it.resource.resourceType to it.matchingIndex }, { it.resource }),
    )
  }
}

internal suspend fun Search.count(database: Database): Long {
  return database.count(getQuery(true))
}

fun Search.getQuery(isCount: Boolean = false): SearchQuery {
  return getQuery(isCount, null)
}

private fun Search.getRevIncludeQuery(includeIds: List<String>): SearchQuery {
  var matchQuery = ""
  val args = mutableListOf<Any>()
  args.addAll(includeIds)

  // creating the match and filter query
  revIncludes.forEachIndexed { index, (param, search) ->
    val resourceToInclude = search.type
    args.add(resourceToInclude.name)
    args.add(param.paramName)
    matchQuery += " ( a.resourceType = ? and a.index_name IN (?) "

    val allFilters = search.getFilterQueries()

    if (allFilters.isNotEmpty()) {
      val iterator = allFilters.listIterator()
      matchQuery += "AND b.resourceUuid IN (\n"
      do {
        iterator.next().let {
          matchQuery += it.query
          args.addAll(it.args)
        }

        if (iterator.hasNext()) {
          matchQuery +=
            if (search.operation == Operation.OR) {
              "\n UNION \n"
            } else {
              "\n INTERSECT \n"
            }
        }
      } while (iterator.hasNext())
      matchQuery += "\n)"
    }

    matchQuery += " \n)"

    if (index != revIncludes.lastIndex) matchQuery += " OR "
  }

  return SearchQuery(
    query =
      """
    SELECT a.index_name, a.index_value, b.serializedResource 
    FROM ReferenceIndexEntity a 
    JOIN  ResourceEntity b
    ON  a.resourceUuid = b.resourceUuid
    AND  a.index_value IN( ${ CharArray(includeIds.size) { '?' }.joinToString()} ) 
    ${if (matchQuery.isEmpty()) "" else "AND ($matchQuery) " }
        """
        .trimIndent(),
    args = args,
  )
}

private fun Search.getIncludeQuery(includeIds: List<String>): SearchQuery {
  var matchQuery = ""
  val args = mutableListOf<Any>(type.name)
  args.addAll(includeIds)

  // creating the match and filter query
  forwardIncludes.forEachIndexed { index, (param, search) ->
    val resourceToInclude = search.type
    args.add(resourceToInclude.name)
    args.add(param.paramName)
    matchQuery += " ( c.resourceType = ? and b.index_name IN (?) "

    val allFilters = search.getFilterQueries()

    if (allFilters.isNotEmpty()) {
      val iterator = allFilters.listIterator()
      matchQuery += "AND c.resourceUuid IN (\n"
      do {
        iterator.next().let {
          matchQuery += it.query
          args.addAll(it.args)
        }

        if (iterator.hasNext()) {
          matchQuery +=
            if (search.operation == Operation.OR) {
              "\n UNION \n"
            } else {
              "\n INTERSECT \n"
            }
        }
      } while (iterator.hasNext())
      matchQuery += "\n)"
    }

    matchQuery += " \n)"

    if (index != forwardIncludes.lastIndex) matchQuery += " OR "
  }

  return SearchQuery(
    query =
      //  spotless:off
    """
    SELECT b.index_name,  a.resourceId, c.serializedResource from ResourceEntity a 
    JOIN ReferenceIndexEntity b 
    On a.resourceUuid = b.resourceUuid
    AND a.resourceType = ?
    AND a.resourceId IN ( ${ CharArray(includeIds.size) { '?' }.joinToString()} ) 
    JOIN ResourceEntity c
    ON c.resourceType||"/"||c.resourceId = b.index_value
    ${if (matchQuery.isEmpty()) "" else "AND ($matchQuery) " }
    """.trimIndent(),
    //  spotless:on
    args = args,
  )
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
  var sortJoinStatement = ""
  var sortOrderStatement = ""
  val sortArgs = mutableListOf<Any>()
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
    sortJoinStatement = ""

    sortTableNames.forEachIndexed { index, sortTableName ->
      val tableAlias = 'b' + index

      sortJoinStatement +=
        //  spotless:off
      """
      LEFT JOIN ${sortTableName.tableName} $tableAlias
      ON a.resourceType = $tableAlias.resourceType AND a.resourceUuid = $tableAlias.resourceUuid AND $tableAlias.index_name = ?
      """
      //  spotless:on
      sortArgs += sort.paramName
    }

    sortTableNames.forEachIndexed { index, sortTableName ->
      val tableAlias = 'b' + index
      sortOrderStatement +=
        if (index == 0) {
          """
            ORDER BY $tableAlias.${sortTableName.columnName} ${order.sqlString}
                    """
            .trimIndent()
        } else {
          ", $tableAlias.${SortTableInfo.DATE_TIME_SORT_TABLE_INFO.columnName} ${order.sqlString}"
        }
    }
  }

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
        SELECT a.serializedResource
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
