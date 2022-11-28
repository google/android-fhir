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

import ca.uhn.fhir.model.api.TemporalPrecisionEnum
import java.math.BigDecimal
import java.util.Date
import org.hl7.fhir.instance.model.api.IBase
import org.hl7.fhir.instance.model.api.IBaseMetaType
import org.hl7.fhir.instance.model.api.ICompositeType
import org.hl7.fhir.instance.model.api.IPrimitiveType

interface FhirConverter {

  fun evaluateFunction(resource: IBase, path: String): List<IBase>

  fun createDateType(value: IBase): DateType
  fun createDateType(value: String): DateType

  fun createDateTimeType(value: IBase): DateTimeType
  fun createDateTimeType(value: String): DateTimeType

  fun createContactPointType(value: ICompositeType): ContactPoint

  fun createCodeType(value: IPrimitiveType<String>): CodeType

  fun createUriType(value: IPrimitiveType<String>): UriType

  fun createInstantType(value: IBase): InstantType
  fun createPeriodType(value: IBase): Period

  fun createTimingType(value: IBase): Timing

  fun createStringType(value: IBase): String

  fun createReferenceType(value: IBase): String?

  fun createIdentifierType(value: IBase): Identifier
  fun createCodeableConceptType(value: IBase): CodeableConcept
  fun createCodingType(value: IBase): Coding
  fun createMoneyType(value: IBase): Money
  fun createQuantityType(value: IBase): Quantity
  fun createLocationPositionComponentType(value: IBase): LocationPositionComponent

  fun getPrimitiveValue(value: IBase): String
  fun hasLastUpdated(meta: IBaseMetaType?): Boolean
  fun getLastUpdatedElement(meta: IBaseMetaType?): Long
  fun hasProfile(meta: IBaseMetaType?): Boolean
  fun hasTag(meta: IBaseMetaType?): Boolean

  fun validateResourceType(resourceType: String)
}

data class DateType(val value: Date, val precision: TemporalPrecisionEnum)

data class DateTimeType(val value: Date?, val precision: TemporalPrecisionEnum)

data class InstantType(val value: Date)

data class Period(
  val hasStart: Boolean,
  val hasEnd: Boolean,
  val start: Date?,
  val endElement: DateTimeType,
  val end: Date?,
)

data class LocationPositionComponent(val latitude: Double, val longitude: Double)

data class Money(val currency: String, val value: BigDecimal?)

data class Identifier(val system: String?, val value: String?)

data class Quantity(
  val unit: String?,
  val code: String?,
  val system: String?,
  val value: BigDecimal?,
)

data class Timing(
  val hasEvent: Boolean,
  val event: List<DateTimeType>,
)

data class Coding(val system: String?, val code: String?)

data class CodeableConcept(val coding: List<Coding>)

data class ContactPoint(val system: String?, val value: String, val use: String?)

data class CodeType(val value: String)

data class UriType(val value: String)
