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

package com.google.android.fhir.search

import com.google.android.fhir.index.CodeType
import com.google.android.fhir.index.CodeableConcept
import com.google.android.fhir.index.Coding
import com.google.android.fhir.index.ContactPoint
import com.google.android.fhir.index.DateTimeType
import com.google.android.fhir.index.DateType
import com.google.android.fhir.index.Identifier
import com.google.android.fhir.index.UriType
import java.util.Date
import kotlin.jvm.Throws
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.instance.model.api.IBaseCoding
import org.hl7.fhir.instance.model.api.ICompositeType
import org.hl7.fhir.instance.model.api.IPrimitiveType
import org.hl7.fhir.r4.model.ResourceType

object SearchManagerForR4Test : SearchManager {
  override fun createDateTimeType(value: String): DateTimeType {
    val dateTime = org.hl7.fhir.r4.model.DateTimeType(value)
    return DateTimeType(dateTime.value, dateTime.precision)
  }

  override fun createDateTimeType(value: IPrimitiveType<Date>): DateTimeType {
    val dateTime = value as org.hl7.fhir.r4.model.DateTimeType
    return DateTimeType(dateTime.value, dateTime.precision)
  }

  override fun createDateType(value: String): DateType {
    val date = org.hl7.fhir.r4.model.DateType(value)
    return DateType(date.value, date.precision)
  }

  override fun createDateType(value: IPrimitiveType<Date>): DateType {
    val date = value as org.hl7.fhir.r4.model.DateType
    return DateType(date.value, date.precision)
  }

  override fun createIdentifierType(value: ICompositeType): Identifier {
    val identifier = value as org.hl7.fhir.r4.model.Identifier
    return Identifier(identifier.system, identifier.value)
  }

  override fun createCodingType(value: IBaseCoding): Coding {
    val coding = value as org.hl7.fhir.r4.model.ICoding
    return Coding(coding.system, coding.code)
  }

  override fun createCodeableConceptType(value: ICompositeType): CodeableConcept {
    val codeableConcept = value as org.hl7.fhir.r4.model.CodeableConcept
    return CodeableConcept(codeableConcept.coding.map { Coding(it.system, it.code) })
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

  @Throws(FHIRException::class)
  override fun validateResourceType(resourceType: String) {
    ResourceType.fromCode(resourceType)
  }
}
