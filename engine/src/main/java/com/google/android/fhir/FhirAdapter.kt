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

package com.google.android.fhir

import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import com.google.android.fhir.index.SearchParamDefinition
import com.google.android.fhir.index.entities.DateIndex
import com.google.android.fhir.index.entities.DateTimeIndex
import com.google.android.fhir.index.entities.NumberIndex
import com.google.android.fhir.index.entities.PositionIndex
import com.google.android.fhir.index.entities.QuantityIndex
import com.google.android.fhir.index.entities.ReferenceIndex
import com.google.android.fhir.index.entities.StringIndex
import com.google.android.fhir.index.entities.TokenIndex
import com.google.android.fhir.index.entities.UriIndex
import com.google.android.fhir.search.ConditionParam
import com.google.android.fhir.search.filter.TokenFilterValue
import java.math.BigDecimal
import java.util.Date
import org.hl7.fhir.instance.model.api.IBase
import org.hl7.fhir.instance.model.api.IBaseMetaType
import org.hl7.fhir.instance.model.api.IPrimitiveType

data class FhirAdapter
internal constructor(
  val fhirVersionEnum: FhirVersionEnum,
  val evaluateFunction: (IBase, String) -> List<IBase>,
  val hasLastUpdated: (IBaseMetaType) -> Boolean,
  val hasProfile: (IBaseMetaType) -> Boolean,
  val hasTag: (IBaseMetaType) -> Boolean,
  val toLastUpdatedElement: (IBaseMetaType) -> Date,
  val toNumberIndex: (SearchParamDefinition, IBase) -> NumberIndex?,
  val toDateIndex: (SearchParamDefinition, IBase) -> DateIndex,
  val toDateTimeIndex: (SearchParamDefinition, IBase) -> DateTimeIndex?,
  val toStringIndex: (SearchParamDefinition, IBase) -> StringIndex?,
  val toTokenIndexList: (SearchParamDefinition, IBase) -> List<TokenIndex>,
  val toReferenceIndex: (SearchParamDefinition, IBase) -> ReferenceIndex?,
  val toQuantityIndexList: (SearchParamDefinition, IBase) -> List<QuantityIndex>,
  val toUriIndex: (SearchParamDefinition, IBase?) -> UriIndex?,
  val toPositionIndex: (IBase?) -> PositionIndex?,
  val toTokenFilterValue: (IBase) -> TokenFilterValue,
  val toGetConditionParamPairForDateTimeType:
    (ParamPrefixEnum, IPrimitiveType<Date>) -> ConditionParam<Long>,
  val toGetConditionParamPairForDateType:
    (ParamPrefixEnum, IPrimitiveType<Date>) -> ConditionParam<Long>,
  val toDateTime: (String) -> IPrimitiveType<Date>,
  val toDate: (String) -> IPrimitiveType<Date>,
  val toQuantity: (String) -> Triple<BigDecimal, String, String>,
  val toCoding: (String) -> Pair<String, String>,
  val validateResourceType: (String) -> Unit
) {

  class Builder(private val fhirVersionEnum: FhirVersionEnum) {
    private lateinit var evaluateFunction: (IBase, String) -> List<IBase>
    private lateinit var hasLastUpdated: (IBaseMetaType) -> Boolean
    private lateinit var hasProfile: (IBaseMetaType) -> Boolean
    private lateinit var hasTag: (IBaseMetaType) -> Boolean
    private lateinit var toLastUpdatedElement: (IBaseMetaType) -> Date
    private lateinit var toNumberIndex: (SearchParamDefinition, IBase) -> NumberIndex?
    private lateinit var toDateIndex: (SearchParamDefinition, IBase) -> DateIndex
    private lateinit var toDateTimeIndex: (SearchParamDefinition, IBase) -> DateTimeIndex?
    private lateinit var toStringIndex: (SearchParamDefinition, IBase) -> StringIndex?
    private lateinit var toTokenIndexList: (SearchParamDefinition, IBase) -> List<TokenIndex>
    private lateinit var toReferenceIndex: (SearchParamDefinition, IBase) -> ReferenceIndex?
    private lateinit var toQuantityIndexList: (SearchParamDefinition, IBase) -> List<QuantityIndex>
    private lateinit var toUriIndex: (SearchParamDefinition, IBase?) -> UriIndex?
    private lateinit var toPositionIndex: (IBase?) -> PositionIndex?
    private lateinit var toTokenFilterValue: (IBase) -> TokenFilterValue
    private lateinit var toGetConditionParamPairForDateTimeType:
      (ParamPrefixEnum, IPrimitiveType<Date>) -> ConditionParam<Long>
    private lateinit var toGetConditionParamPairForDateType:
      (ParamPrefixEnum, IPrimitiveType<Date>) -> ConditionParam<Long>

    private lateinit var toDateTime: (String) -> IPrimitiveType<Date>
    private lateinit var toDate: (String) -> IPrimitiveType<Date>
    private lateinit var toQuantity: (String) -> Triple<BigDecimal, String, String>
    private lateinit var toCoding: (String) -> Pair<String, String>
    private lateinit var validateResourceType: (String) -> Unit

    fun setEvaluateFunction(evaluateFunction: (IBase, String) -> List<IBase>) = apply {
      this.evaluateFunction = evaluateFunction
    }

    fun setHasLastUpdate(hasLastUpdated: (IBaseMetaType) -> Boolean) = apply {
      this.hasLastUpdated = hasLastUpdated
    }

    fun setHasProfile(hasProfile: (IBaseMetaType) -> Boolean) = apply {
      this.hasProfile = hasProfile
    }

    fun setHasTag(hasTag: (IBaseMetaType) -> Boolean) = apply { this.hasTag = hasTag }

    fun setToLastUpdatedElement(toLastUpdatedElement: (IBaseMetaType) -> Date) = apply {
      this.toLastUpdatedElement = toLastUpdatedElement
    }

    fun setToNumberIndex(toNumberIndex: (SearchParamDefinition, IBase) -> NumberIndex?) = apply {
      this.toNumberIndex = toNumberIndex
    }

    fun setToDateIndex(toDateIndex: (SearchParamDefinition, IBase) -> DateIndex) = apply {
      this.toDateIndex = toDateIndex
    }

    fun setToDateTimeIndex(toDateTimeIndex: (SearchParamDefinition, IBase) -> DateTimeIndex?) =
      apply {
        this.toDateTimeIndex = toDateTimeIndex
      }

    fun setToStringIndex(toStringIndex: (SearchParamDefinition, IBase) -> StringIndex?) = apply {
      this.toStringIndex = toStringIndex
    }

    fun setToTokenIndexList(toTokenIndexList: (SearchParamDefinition, IBase) -> List<TokenIndex>) =
      apply {
        this.toTokenIndexList = toTokenIndexList
      }

    fun setToReferenceIndex(toReferenceIndex: (SearchParamDefinition, IBase) -> ReferenceIndex?) =
      apply {
        this.toReferenceIndex = toReferenceIndex
      }

    fun setToQuantityIndexList(
      toQuantityIndexList: (SearchParamDefinition, IBase) -> List<QuantityIndex>
    ) = apply { this.toQuantityIndexList = toQuantityIndexList }

    fun setToUriIndex(toUriIndex: (SearchParamDefinition, IBase?) -> UriIndex?) = apply {
      this.toUriIndex = toUriIndex
    }

    fun setToPositionIndex(toPositionIndex: (IBase?) -> PositionIndex?) = apply {
      this.toPositionIndex = toPositionIndex
    }

    fun setToGetConditionParamPairForDateTimeType(
      toGetConditionParamPairForDateTimeType:
        (ParamPrefixEnum, IPrimitiveType<Date>) -> ConditionParam<Long>
    ) = apply {
      this.toGetConditionParamPairForDateTimeType = toGetConditionParamPairForDateTimeType
    }

    fun setToGetConditionParamPairForDateType(
      toGetConditionParamPairForDateType:
        (ParamPrefixEnum, IPrimitiveType<Date>) -> ConditionParam<Long>
    ) = apply { this.toGetConditionParamPairForDateType = toGetConditionParamPairForDateType }

    fun setToTokenFilterValue(toTokenFilterValue: (IBase) -> TokenFilterValue) = apply {
      this.toTokenFilterValue = toTokenFilterValue
    }

    fun setToDate(toDate: (String) -> IPrimitiveType<Date>) = apply { this.toDate = toDate }

    fun setToDateTime(toDateTime: (String) -> IPrimitiveType<Date>) = apply {
      this.toDateTime = toDateTime
    }

    fun setToQuantity(toQuantity: (String) -> Triple<BigDecimal, String, String>) = apply {
      this.toQuantity = toQuantity
    }

    fun setToCoding(toCoding: (String) -> Pair<String, String>) = apply { this.toCoding = toCoding }

    fun setValidateResourceType(validateResourceType: (String) -> Unit) = apply {
      this.validateResourceType = validateResourceType
    }
    fun build(): FhirAdapter {
      check(fhirVersionEnum.isNewerThan(FhirVersionEnum.DSTU3))
      return FhirAdapter(
        fhirVersionEnum = fhirVersionEnum,
        evaluateFunction = evaluateFunction,
        hasLastUpdated = hasLastUpdated,
        hasProfile = hasProfile,
        hasTag = hasTag,
        toLastUpdatedElement = toLastUpdatedElement,
        toNumberIndex = toNumberIndex,
        toDateIndex = toDateIndex,
        toDateTimeIndex = toDateTimeIndex,
        toStringIndex = toStringIndex,
        toTokenIndexList = toTokenIndexList,
        toReferenceIndex = toReferenceIndex,
        toQuantityIndexList = toQuantityIndexList,
        toUriIndex = toUriIndex,
        toPositionIndex = toPositionIndex,
        toTokenFilterValue = toTokenFilterValue,
        toGetConditionParamPairForDateTimeType = toGetConditionParamPairForDateTimeType,
        toGetConditionParamPairForDateType = toGetConditionParamPairForDateType,
        toDateTime = toDateTime,
        toDate = toDate,
        toQuantity = toQuantity,
        toCoding = toCoding,
        validateResourceType = validateResourceType
      )
    }
  }

  companion object {
    fun builder(fhirVersionEnum: FhirVersionEnum) = Builder(fhirVersionEnum)
  }
}
