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

package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DurationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DurationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.DataRequirement
import com.google.fhir.r4.core.DataRequirement.DateFilter
import com.google.fhir.r4.core.DataRequirement.Sort
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Duration
import com.google.fhir.r4.core.FHIRAllTypesValueSet
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.SortDirectionCode
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Type

object DataRequirementConverter {
  @JvmStatic
  private fun DataRequirement.SubjectX.dataRequirementSubjectToHapi(): Type {
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for DataRequirement.subject[x]")
  }

  @JvmStatic
  private fun Type.dataRequirementSubjectToProto(): DataRequirement.SubjectX {
    val protoValue = DataRequirement.SubjectX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
        protoValue.codeableConcept = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
        protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun DataRequirement.DateFilter.ValueX.dataRequirementDateFilterValueToHapi(): Type {
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    if (this.duration != Duration.newBuilder().defaultInstanceForType) {
      return (this.duration).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for DataRequirement.dateFilter.value[x]")
  }

  @JvmStatic
  private fun Type.dataRequirementDateFilterValueToProto(): DataRequirement.DateFilter.ValueX {
    val protoValue = DataRequirement.DateFilter.ValueX.newBuilder()
    if (this is DateTimeType) {
        protoValue.dateTime = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
        protoValue.period = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Duration) {
        protoValue.duration = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun DataRequirement.toHapi(): org.hl7.fhir.r4.model.DataRequirement {
    val hapiValue = org.hl7.fhir.r4.model.DataRequirement()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
      hapiValue.type = type.value.name
    if (profileCount > 0) {
        hapiValue.profile = profileList.map { it.toHapi() }
    }
    if (hasSubject()) {
        hapiValue.subject = subject.dataRequirementSubjectToHapi()
    }
    if (mustSupportCount > 0) {
        hapiValue.mustSupport = mustSupportList.map { it.toHapi() }
    }
    if (codeFilterCount > 0) {
        hapiValue.codeFilter = codeFilterList.map { it.toHapi() }
    }
    if (dateFilterCount > 0) {
        hapiValue.dateFilter = dateFilterList.map { it.toHapi() }
    }
    if (hasLimit()) {
        hapiValue.limitElement = limit.toHapi()
    }
    if (sortCount > 0) {
        hapiValue.sort = sortList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.DataRequirement.toProto(): DataRequirement {
    val protoValue = DataRequirement.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
      protoValue.type = DataRequirement.TypeCode.newBuilder()
          .setValue(FHIRAllTypesValueSet.Value.valueOf(type))
          .build()
    if (hasProfile()) {
      protoValue.addAllProfile(profile.map { it.toProto() })
    }
    if (hasSubject()) {
        protoValue.subject = subject.dataRequirementSubjectToProto()
    }
    if (hasMustSupport()) {
      protoValue.addAllMustSupport(mustSupport.map { it.toProto() })
    }
    if (hasCodeFilter()) {
      protoValue.addAllCodeFilter(codeFilter.map { it.toProto() })
    }
    if (hasDateFilter()) {
      protoValue.addAllDateFilter(dateFilter.map { it.toProto() })
    }
    if (hasLimit()) {
        protoValue.limit = limitElement.toProto()
    }
    if (hasSort()) {
      protoValue.addAllSort(sort.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.DataRequirement.DataRequirementCodeFilterComponent.toProto():
    DataRequirement.CodeFilter {
    val protoValue = DataRequirement.CodeFilter.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasPath()) {
        protoValue.path = pathElement.toProto()
    }
    if (hasSearchParam()) {
        protoValue.searchParam = searchParamElement.toProto()
    }
    if (hasValueSet()) {
        protoValue.valueSet = valueSetElement.toProto()
    }
    if (hasCode()) {
      protoValue.addAllCode(code.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.DataRequirement.DataRequirementDateFilterComponent.toProto():
    DataRequirement.DateFilter {
    val protoValue = DataRequirement.DateFilter.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasPath()) {
        protoValue.path = pathElement.toProto()
    }
    if (hasSearchParam()) {
        protoValue.searchParam = searchParamElement.toProto()
    }
    if (hasValue()) {
        protoValue.value = value.dataRequirementDateFilterValueToProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.DataRequirement.DataRequirementSortComponent.toProto():
    DataRequirement.Sort {
    val protoValue = DataRequirement.Sort.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasPath()) {
        protoValue.path = pathElement.toProto()
    }
      protoValue.direction = DataRequirement.Sort.DirectionCode.newBuilder()
          .setValue(
              SortDirectionCode.Value.valueOf(
                  direction.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    return protoValue.build()
  }

  @JvmStatic
  private fun DataRequirement.CodeFilter.toHapi():
    org.hl7.fhir.r4.model.DataRequirement.DataRequirementCodeFilterComponent {
    val hapiValue = org.hl7.fhir.r4.model.DataRequirement.DataRequirementCodeFilterComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (hasPath()) {
        hapiValue.pathElement = path.toHapi()
    }
    if (hasSearchParam()) {
        hapiValue.searchParamElement = searchParam.toHapi()
    }
    if (hasValueSet()) {
        hapiValue.valueSetElement = valueSet.toHapi()
    }
    if (codeCount > 0) {
        hapiValue.code = codeList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun DataRequirement.DateFilter.toHapi():
    org.hl7.fhir.r4.model.DataRequirement.DataRequirementDateFilterComponent {
    val hapiValue = org.hl7.fhir.r4.model.DataRequirement.DataRequirementDateFilterComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (hasPath()) {
        hapiValue.pathElement = path.toHapi()
    }
    if (hasSearchParam()) {
        hapiValue.searchParamElement = searchParam.toHapi()
    }
    if (hasValue()) {
        hapiValue.value = value.dataRequirementDateFilterValueToHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun DataRequirement.Sort.toHapi():
    org.hl7.fhir.r4.model.DataRequirement.DataRequirementSortComponent {
    val hapiValue = org.hl7.fhir.r4.model.DataRequirement.DataRequirementSortComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (hasPath()) {
        hapiValue.pathElement = path.toHapi()
    }
      hapiValue.direction = org.hl7.fhir.r4.model.DataRequirement.SortDirection.valueOf(
          direction.value.name.hapiCodeCheck().replace("_", "")
      )
    return hapiValue
  }
}
