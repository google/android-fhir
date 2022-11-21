package com.google.android.fhir.index

import ca.uhn.fhir.model.api.TemporalPrecisionEnum
import java.math.BigDecimal
import java.util.Date
import org.hl7.fhir.instance.model.api.IBase

interface ResourceIndexerManager {

  fun evaluateFunction(resource: IBase, path: String):  List<IBase>

  fun createDateType(value: IBase) : DateType

  fun createDateTimeType(value: IBase) : DateTimeType
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

data class Money(val currency: String, val value: BigDecimal)

data class Identifier(val system: String?, val value: String?)

data class Quantity(
  val unit: String?,
  val code: String?,
  val system: String?,
  val value: BigDecimal,
)

data class Timing(
  val hasEvent: Boolean,
  val event: List<DateTimeType>,
)

data class Coding(val system: String?, val code: String?)

data class CodeableConcept(val coding: List<Coding>)
