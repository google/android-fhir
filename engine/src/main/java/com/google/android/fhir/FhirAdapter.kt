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

/**
 * Data class encapsulating lambdas that are used in the Engine to create indices and FHIR data type
 * converters. These lambdas get called in different classes in the Engine, such as [ReferenceIndex]
 * and [XFhirQueryTranslator].
 *
 * @property fhirVersionEnum
 * @property evaluateFunction function to evaluate a path and return the matching elements using a
 * FHIRPathEngine
 * @property hasLastUpdated function to call `hasLastUpdated()` in a class implementing
 * [IBaseMetaType]
 * @property hasProfile function to call `hasProfile()` in a class implementing [IBaseMetaType]
 * @property hasTag function to call `hasTag()` in a class implementing [IBaseMetaType]
 * @property toLastUpdatedElement function to get `lastUpdatedElement` in a class implementing
 * [IBaseMetaType]
 * @property toNumberIndex function creates a [NumberIndex] from the passed search parameter and
 * value
 * @property toDateIndex function creates a [DateIndex] from the passed search parameter and value
 * @property toDateTimeIndex function create a [DateTimeIndex] from the passed search parameter and
 * value
 * @property toStringIndex function creates a [StringIndex] from the passed search parameter and
 * value
 * @property toTokenIndexList function creates a list of [TokenIndex]s from the passed search
 * parameter and value
 * @property toReferenceIndex function creates a [ReferenceIndex] from the passed search parameter
 * and value
 * @property toQuantityIndexList function creates a list of [QuantityIndex]s from the passed search
 * parameter and value
 * @property toUriIndex function creates a [UriIndex] from the passed search parameter and value
 * @property toPositionIndex function creates a [PositionIndex] from the passed search parameter and
 * value
 * @property toTokenFilterValue function creates a [TokenFilterValue] from the passed value
 * @property toGetConditionParamPairForDateTimeType function creates a [ConditionParam] from the
 * passed [ParamPrefixEnum] and [IPrimitiveType]. The [IPrimitiveType]'s concrete class is
 * DateTimeType
 * @property toGetConditionParamPairForDateType function creates a [ConditionParam] from the passed
 * [ParamPrefixEnum] and [IPrimitiveType]. The [IPrimitiveType]'s concrete class is DateType
 * @property toDateTime function converts a [String] to a DateTimeType object. The DateTimeType
 * object gets assigned as a [IPrimitiveType]
 * @property toDate function converts a [String] to a DateType object. The DateType object gets
 * assigned as a [IPrimitiveType]
 * @property toQuantity function converts a [String] to a [Triple] object. The [Triple] object
 * should contain 3 fields from the [Quantity](https://www.hl7.org/fhir/datatypes.html#Quantity)
 * type: value, system, and unit (in that order)
 * @property toCoding function converts a [String] to a [Pair] object. The [Pair] object should
 * contain 2 fields from the [Coding](https://www.hl7.org/fhir/datatypes.html#Coding) type: uri, and
 * code (in that order)
 * @property validateResourceType function to check if a [String] represents a FHIR resource type
 */
data class FhirAdapter(
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
  val toGetConditionParamPairForDateType:
    (ParamPrefixEnum, IPrimitiveType<Date>) -> ConditionParam<Long>,
  val toGetConditionParamPairForDateTimeType:
    (ParamPrefixEnum, IPrimitiveType<Date>) -> ConditionParam<Long>,
  val toDate: (String) -> IPrimitiveType<Date>,
  val toDateTime: (String) -> IPrimitiveType<Date>,
  val toQuantity: (String) -> Triple<BigDecimal, String, String>,
  val toCoding: (String) -> Pair<String, String>,
  val validateResourceType: (String) -> Unit
)
