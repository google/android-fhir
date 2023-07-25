/*
 * Copyright 2022-2023 Google LLC
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

package com.google.android.fhir.workflow

import ca.uhn.fhir.rest.gclient.DateClientParam
import ca.uhn.fhir.rest.gclient.NumberClientParam
import ca.uhn.fhir.rest.gclient.ReferenceClientParam
import ca.uhn.fhir.rest.gclient.StringClientParam
import ca.uhn.fhir.rest.gclient.TokenClientParam
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.db.ResourceNotFoundException
import com.google.android.fhir.search.Search
import com.google.android.fhir.search.filter.TokenParamFilterCriterion
import com.google.android.fhir.search.query.XFhirQueryTranslator.applyFilterParam
import java.math.BigDecimal
import java.util.Date
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.opencds.cqf.cql.engine.retrieve.TerminologyAwareRetrieveProvider
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo

internal class FhirEngineRetrieveProvider(private val fhirEngine: FhirEngine) :
  TerminologyAwareRetrieveProvider() {
  override fun retrieve(
    context: String?,
    contextPath: String?,
    contextValue: Any?,
    dataType: String?,
    templateId: String?,
    codePath: String?,
    codes: MutableIterable<Code>?,
    valueSet: String?,
    datePath: String?,
    dateLowPath: String?,
    dateHighPath: String?,
    dateRange: Interval?
  ): Iterable<Any> = runBlockingOrThrowMainThreadException {
    if (dataType == null) {
      emptyList()
    } else if (contextPath == "id" && contextValue == null) {
      emptyList()
    } else if (contextPath == "id") {
      listOfNotNull(
        safeGet(fhirEngine, ResourceType.fromCode(dataType), "$contextValue"),
        safeGet(fhirEngine, ResourceType.fromCode(dataType), "urn:uuid:$contextValue"),
        safeGet(fhirEngine, ResourceType.fromCode(dataType), "urn:oid:$contextValue")
      )
    } else if (codePath == "id" && codes != null) {
      codes.mapNotNull { safeGet(fhirEngine, ResourceType.fromCode(dataType), it.code) }
    } else {
      val search = Search(ResourceType.fromCode(dataType))
      filterByContext(context, contextPath, contextValue, dataType, search)
      filterByCode(codePath, codes, search)
      filterByValueSet(codePath, valueSet, search)
      filterByDateRange(datePath, dateLowPath, dateHighPath, dateRange, search)
      fhirEngine.search<Resource>(search).map { it.resource }
    }
  }

  private fun filterByDateRange(
    datePath: String?,
    dateLowPath: String?,
    dateHighPath: String?,
    dateRange: Interval?,
    search: Search
  ) {
    if (datePath != null && dateRange?.low != null) {
      search.filter(
        DateClientParam(datePath),
        {
          prefix =
            if (dateRange.lowClosed) ParamPrefixEnum.GREATERTHAN_OR_EQUALS
            else ParamPrefixEnum.GREATERTHAN
          value = of(DateTimeType(convertDate(dateRange.low)))
        }
      )
    }

    if (datePath != null && dateRange?.high != null) {
      search.filter(
        DateClientParam(datePath),
        {
          prefix =
            if (dateRange.highClosed) ParamPrefixEnum.LESSTHAN_OR_EQUALS
            else ParamPrefixEnum.LESSTHAN
          value = of(DateTimeType(convertDate(dateRange.high)))
        }
      )
    }

    if (dateLowPath != null && dateRange?.low != null) {
      search.filter(
        DateClientParam(dateLowPath),
        {
          prefix =
            if (dateRange.lowClosed) ParamPrefixEnum.GREATERTHAN_OR_EQUALS
            else ParamPrefixEnum.GREATERTHAN
          value = of(DateTimeType(convertDate(dateRange.low)))
        }
      )
    }

    if (dateHighPath != null && dateRange?.high != null) {
      search.filter(
        DateClientParam(dateHighPath),
        {
          prefix =
            if (dateRange.highClosed) ParamPrefixEnum.LESSTHAN_OR_EQUALS
            else ParamPrefixEnum.LESSTHAN
          value = of(DateTimeType(convertDate(dateRange.high)))
        }
      )
    }
  }

  private fun convertDate(obj: Any): Date {
    return when (obj) {
      is Date -> obj
      is DateTime -> obj.toJavaDate()
      else ->
        throw UnsupportedOperationException(
          "FhirEngineRetrieveProvider doesn't know " +
            "how to convert (${obj.javaClass.name}) into a java.util.Date"
        )
    }
  }

  private fun filterByCode(codePath: String?, codes: Iterable<Code>?, search: Search) {
    if (codes == null || codePath == null) return

    val inCodes =
      codes.map {
        val apply: TokenParamFilterCriterion.() -> Unit = {
          this.value = of(Coding(it.system, it.code, it.display))
        }
        apply
      }

    if (inCodes.isNotEmpty()) search.filter(TokenClientParam(codePath), *inCodes.toTypedArray())
  }

  private fun filterByValueSet(codePath: String?, valueSetUrl: String?, search: Search) {
    if (valueSetUrl == null || codePath == null) return

    val valueSet = terminologyProvider.expand(ValueSetInfo().withId(valueSetUrl))

    val inCodes =
      valueSet.map {
        val apply: TokenParamFilterCriterion.() -> Unit = {
          this.value = of(Coding(it.system, it.code, it.display))
        }
        apply
      }

    if (inCodes.isNotEmpty()) search.filter(TokenClientParam(codePath), *inCodes.toTypedArray())
  }

  private fun filterByContext(
    context: String?,
    contextPath: String?,
    contextValue: Any?,
    dataType: String,
    search: Search,
  ) {
    if (context == null || contextPath == null || contextValue == null) return
    // Finds the SearchParamDefinition annotation that matches the incoming contextPath
    val ann = findSearchParamDefinition(dataType, contextPath)

    if (ann != null) {
      // If found, uses the applyFilterParam
      if (ann.type == Enumerations.SearchParamType.REFERENCE) {
        search.filter(
          ReferenceClientParam(ann.name),
          { value = "$context/$contextValue" },
          { value = "urn:uuid:$contextValue" },
          { value = "urn:oid:$contextValue" }
        )
      } else {
        search.applyFilterParam(ann, "$contextValue")
      }
    } else {
      // Tries to identify the right param class by type
      when (contextValue) {
        is String -> search.filter(StringClientParam(contextPath), { value = contextValue })
        is DateTimeType -> search.filter(DateClientParam(contextPath), { value = of(contextValue) })
        is BigDecimal -> search.filter(NumberClientParam(contextPath), { value = contextValue })
        else ->
          throw UnsupportedOperationException(
            "FhirEngineRetrieveProvider doesn't know " +
              "how to search for $dataType.$contextPath = $contextValue" +
              "(${contextValue.javaClass.name}) and get $context"
          )
      }
    }
  }

  private suspend fun safeGet(fhirEngine: FhirEngine, type: ResourceType, id: String): Resource? {
    return try {
      fhirEngine.get(type, id)
    } catch (e: ResourceNotFoundException) {
      null
    }
  }

  private fun findSearchParamDefinition(dataType: String, path: String) =
    getClass(dataType)
      .fields
      .asSequence()
      .mapNotNull {
        it.getAnnotation(ca.uhn.fhir.model.api.annotation.SearchParamDefinition::class.java)
      }
      .filter { it.path == "$dataType.$path" }
      .map {
        com.google.android.fhir.index.SearchParamDefinition(
          it.name,
          Enumerations.SearchParamType.fromCode(it.type),
          it.path
        )
      }
      .firstOrNull()

  private fun getClass(dataType: String): Class<*> {
    return Class.forName("org.hl7.fhir.r4.model.$dataType")
  }
}
