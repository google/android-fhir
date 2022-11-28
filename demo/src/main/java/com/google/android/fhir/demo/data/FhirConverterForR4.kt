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

package com.google.android.fhir.demo.data

import com.google.android.fhir.CodeType
import com.google.android.fhir.CodeableConcept
import com.google.android.fhir.Coding
import com.google.android.fhir.ContactPoint
import com.google.android.fhir.DateTimeType
import com.google.android.fhir.DateType
import com.google.android.fhir.FhirConverter
import com.google.android.fhir.Identifier
import com.google.android.fhir.InstantType
import com.google.android.fhir.LocationPositionComponent
import com.google.android.fhir.Money
import com.google.android.fhir.Period
import com.google.android.fhir.Quantity
import com.google.android.fhir.Timing
import com.google.android.fhir.UriType
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.instance.model.api.IBase
import org.hl7.fhir.instance.model.api.IBaseMetaType
import org.hl7.fhir.instance.model.api.ICompositeType
import org.hl7.fhir.instance.model.api.IPrimitiveType
import org.hl7.fhir.r4.context.SimpleWorkerContext
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.ICoding
import org.hl7.fhir.r4.model.Location
import org.hl7.fhir.r4.model.Meta
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.utils.FHIRPathEngine

object FhirConverterForR4 : FhirConverter {
  override fun evaluateFunction(resource: IBase, path: String): List<IBase> {
    return FHIRPathEngine(SimpleWorkerContext()).evaluate(resource as Base, path)
  }

  override fun createDateType(value: IBase): DateType =
    DateType((value as org.hl7.fhir.r4.model.DateType).value, value.precision)

  override fun createDateType(value: String): DateType {
    val date = org.hl7.fhir.r4.model.DateType(value)
    return DateType(date.value, date.precision)
  }

  override fun createDateTimeType(value: IBase): DateTimeType =
    when (value) {
      is org.hl7.fhir.r4.model.DateTimeType -> DateTimeType(value.value, value.precision)
      is StringType ->
        DateTimeType(
          org.hl7.fhir.r4.model.DateTimeType(value.value).value,
          org.hl7.fhir.r4.model.DateTimeType(value.value).precision
        )
      else -> throw FHIRException("Invalid conversion to DateTimeType")
    }

  override fun createDateTimeType(value: String): DateTimeType {
    val dateTime = org.hl7.fhir.r4.model.DateTimeType(value)
    return DateTimeType(dateTime.value, dateTime.precision)
  }

  override fun createContactPointType(value: ICompositeType): ContactPoint {
    val contactPoint = value as org.hl7.fhir.r4.model.ContactPoint
    return ContactPoint(
      contactPoint.system.toCode(),
      contactPoint.value,
      contactPoint.use?.toCode()
    )
  }

  override fun createCodeType(value: IPrimitiveType<String>): CodeType {
    return CodeType(value.value)
  }

  override fun createUriType(value: IPrimitiveType<String>): UriType {
    return UriType(value.value)
  }

  override fun createInstantType(value: IBase): InstantType =
    InstantType((value as org.hl7.fhir.r4.model.InstantType).value)

  override fun createPeriodType(value: IBase): Period =
    Period(
      (value as org.hl7.fhir.r4.model.Period).hasStart(),
      value.hasEnd(),
      value.start,
      DateTimeType(value.endElement.value, value.endElement.precision),
      value.end
    )

  override fun createTimingType(value: IBase): Timing =
    Timing(
      (value as org.hl7.fhir.r4.model.Timing).hasEvent(),
      value.event.map { DateTimeType(it.value, it.precision) }
    )

  override fun createStringType(value: IBase): String =
    when (value) {
      is HumanName -> value.asString()
      is Address -> value.asString()
      else -> value.toString()
    }

  override fun createReferenceType(value: IBase): String? =
    when (value) {
      is Reference -> value.reference
      is CanonicalType -> value.value
      is org.hl7.fhir.r4.model.UriType -> value.value
      else -> throw UnsupportedOperationException("Value $value is not readable by SDK")
    }

  override fun createIdentifierType(value: IBase): Identifier =
    Identifier((value as org.hl7.fhir.r4.model.Identifier).system, value.value)

  override fun createCodeableConceptType(value: IBase): CodeableConcept =
    CodeableConcept(
      (value as org.hl7.fhir.r4.model.CodeableConcept).coding.map { Coding(it.system, it.code) }
    )

  override fun createCodingType(value: IBase): Coding =
    Coding((value as ICoding).system, value.code)

  override fun createMoneyType(value: IBase): Money =
    Money((value as org.hl7.fhir.r4.model.Money).currency, value.value)

  override fun createQuantityType(value: IBase): Quantity =
    Quantity((value as org.hl7.fhir.r4.model.Quantity).unit, value.code, value.system, value.value)

  override fun createLocationPositionComponentType(value: IBase): LocationPositionComponent =
    LocationPositionComponent(
      (value as Location.LocationPositionComponent).latitude.toDouble(),
      value.longitude.toDouble()
    )

  override fun getPrimitiveValue(value: IBase): String = (value as Base).primitiveValue()
  override fun hasLastUpdated(meta: IBaseMetaType?): Boolean = (meta as Meta).hasLastUpdated()

  override fun getLastUpdatedElement(meta: IBaseMetaType?): Long =
    (meta as Meta).lastUpdatedElement.value.time

  override fun hasProfile(meta: IBaseMetaType?): Boolean = (meta as Meta).hasProfile()

  override fun hasTag(meta: IBaseMetaType?): Boolean = (meta as Meta).hasTag()
  @Throws(FHIRException::class)
  override fun validateResourceType(resourceType: String) {
    ResourceType.fromCode(resourceType)
  }
}

/**
 * Extension to expresses [HumanName] as a separated string using [separator]. See
 * https://www.hl7.org/fhir/patient.html#search
 */
private fun HumanName.asString(separator: CharSequence = " "): String {
  return (prefix.filterNotNull().map { it.value } +
      given.filterNotNull().map { it.value } +
      family +
      suffix.filterNotNull().map { it.value } +
      text)
    .filterNotNull()
    .filter { it.isNotBlank() }
    .joinToString(separator)
}

/**
 * Extension to expresses [Address] as a string using [separator]. See
 * https://www.hl7.org/fhir/patient.html#search
 */
private fun Address.asString(separator: CharSequence = ", "): String {
  return (line.filterNotNull().map { it.value } +
      city +
      district +
      state +
      country +
      postalCode +
      text)
    .filterNotNull()
    .filter { it.isNotBlank() }
    .joinToString(separator)
}
