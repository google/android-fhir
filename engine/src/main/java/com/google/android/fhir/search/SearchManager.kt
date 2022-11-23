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
import org.hl7.fhir.instance.model.api.IBaseCoding
import org.hl7.fhir.instance.model.api.ICompositeType
import org.hl7.fhir.instance.model.api.IPrimitiveType

interface SearchManager {
  fun createDateTimeType(value: String): DateTimeType

  fun createDateTimeType(value: IPrimitiveType<Date>): DateTimeType

  fun createDateType(value: String): DateType
  fun createDateType(value: IPrimitiveType<Date>): DateType

  fun createIdentifierType(value: ICompositeType): Identifier

  fun createCodingType(value: IBaseCoding): Coding

  fun createCodeableConceptType(value: ICompositeType): CodeableConcept

  fun createContactPointType(value: ICompositeType): ContactPoint

  fun createCodeType(value: IPrimitiveType<String>): CodeType

  fun createUriType(value: IPrimitiveType<String>): UriType
  fun validateResourceType(resourceType: String)
}
