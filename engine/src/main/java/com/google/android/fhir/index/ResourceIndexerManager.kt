package com.google.android.fhir.index

import org.hl7.fhir.instance.model.api.IBase

interface ResourceIndexerManager {

  fun evaluateFunction(resource: IBase, path: String):  List<IBase>

  fun createDateType(value: IBase) : DateType

  fun createDateTimeType(value: IBase) : DateTimeType
  fun createInstantType(value: IBase): InstantType
  fun createPeriodType(value: IBase): Period

  fun createTimingType(value: IBase): Timing

  fun convertResourceToString(value: IBase): String?
  fun getPrimitiveValue(value: IBase): String
  fun createIdentifierType(value: IBase): Identifier
  fun createCodeableConceptType(value: IBase): CodeableConcept
  fun createCodingType(value: IBase): Coding
  fun createMoneyType(value: IBase): Money
  fun createQuantityType(value: IBase): Quantity
  fun createLocationPositionComponentType(value: IBase): LocationPositionComponent


}