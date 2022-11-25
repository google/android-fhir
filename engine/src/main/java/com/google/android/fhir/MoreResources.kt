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

import java.lang.reflect.InvocationTargetException
import java.util.Date
import org.hl7.fhir.instance.model.api.IAnyResource
import org.hl7.fhir.instance.model.api.IBaseMetaType

/**
 * Returns the FHIR resource type.
 *
 * @throws IllegalArgumentException if class name cannot be mapped to valid resource type
 */
fun <R : IAnyResource> getResourceType(clazz: Class<R>): ResourceType {
  try {
    return ResourceType.fromCode(clazz.getConstructor().newInstance().fhirType())
  } catch (e: NoSuchMethodException) {
    throw IllegalArgumentException("Cannot resolve resource type for " + clazz.name, e)
  } catch (e: IllegalAccessException) {
    throw IllegalArgumentException("Cannot resolve resource type for " + clazz.name, e)
  } catch (e: InstantiationException) {
    throw IllegalArgumentException("Cannot resolve resource type for " + clazz.name, e)
  } catch (e: InvocationTargetException) {
    throw IllegalArgumentException("Cannot resolve resource type for " + clazz.name, e)
  }
}

/** Returns the {@link Class} object for the resource type. */
inline fun <reified R : IAnyResource> getResourceClass(resourceType: ResourceType): Class<R> =
  getResourceClass(resourceType.name)

/** Returns the {@link Class} object for the resource type. */
inline fun <reified R : IAnyResource> getResourceClass(resourceType: String): Class<R> {
  // Remove any curly brackets in the resource type string. This is to work around an issue with
  // JSON deserialization in the CQL engine on Android. The resource type string incorrectly
  // includes namespace prefix in curly brackets, e.g. "{http://hl7.org/fhir}Patient" instead of
  // "Patient".
  // TODO: remove this once a fix has been found for the CQL engine on Android.
  val className = resourceType.replace(Regex("\\{[^}]*\\}"), "")
  return Class.forName(R::class.java.`package`?.name + "." + className) as Class<R>
}

val IAnyResource.resourceType: ResourceType
  get() = ResourceType.fromCode(this.fhirType())

/**
 * The logical (unqualified) part of the ID. For example, if the ID is
 * "http://example.com/fhir/Patient/123/_history/456", then this value would be "123".
 */
val IAnyResource.logicalId: String
  get() {
    return this.idElement?.idPart.orEmpty()
  }

internal val IAnyResource.versionId
  get() = meta.versionId

internal val IAnyResource.lastUpdated
  get() = if (this.meta != null && !this.meta.isEmpty) meta.lastUpdated?.toInstant() else null

internal fun IAnyResource.hasMeta(): Boolean {
  return this.meta != null && !this.meta.isEmpty
}

internal fun IBaseMetaType.hasVersionId(): Boolean {
  return this.versionId != null && this.versionId.isNotEmpty()
}

internal fun IBaseMetaType.hasLastUpdated(): Boolean {
  return lastUpdated != null && lastUpdated.toTimeZoneString().isNotBlank()
}

internal val IBaseMetaType.lastUpdatedElement: Long
  get() {
    if (this.lastUpdated == null) {
      this.lastUpdated = Date()
    }
    return this.lastUpdated.time
  }

internal fun IBaseMetaType.hasProfile(): Boolean {
  if (this.profile == null) return false
  for (item in this.profile) if (!item.isEmpty) return true
  return false
}

internal fun IBaseMetaType.hasTag(): Boolean {
  if (this.tag == null) return false
  for (item in this.tag) if (!item.isEmpty) return true
  return false
}

// Type aliases
typealias R4Base = org.hl7.fhir.r4.model.Base

typealias R4Resource = org.hl7.fhir.r4.model.Resource

typealias R4Bundle = org.hl7.fhir.r4.model.Bundle

typealias R4BundleType = org.hl7.fhir.r4.model.Bundle.BundleType

typealias R4DateTimeType = org.hl7.fhir.r4.model.DateTimeType

typealias R4DateType = org.hl7.fhir.r4.model.DateType

typealias R4InstantType = org.hl7.fhir.r4.model.InstantType

typealias R4Period = org.hl7.fhir.r4.model.Period

typealias R4Timing = org.hl7.fhir.r4.model.Timing

typealias R4StringType = org.hl7.fhir.r4.model.StringType

typealias R4HumanName = org.hl7.fhir.r4.model.HumanName

typealias R4Address = org.hl7.fhir.r4.model.Address

typealias R4Identifier = org.hl7.fhir.r4.model.Identifier

typealias R4CodeableConcept = org.hl7.fhir.r4.model.CodeableConcept

typealias R4ICoding = org.hl7.fhir.r4.model.ICoding

typealias R4Reference = org.hl7.fhir.r4.model.Reference

typealias R4CanonicalType = org.hl7.fhir.r4.model.CanonicalType

typealias R4UriType = org.hl7.fhir.r4.model.UriType

typealias R4Money = org.hl7.fhir.r4.model.Money

typealias R4Quantity = org.hl7.fhir.r4.model.Quantity

typealias R4LocationPositionComponent = org.hl7.fhir.r4.model.Location.LocationPositionComponent

typealias R4OperationOutcome = org.hl7.fhir.r4.model.OperationOutcome

typealias R5Base = org.hl7.fhir.r5.model.Base

typealias R5Resource = org.hl7.fhir.r5.model.Resource

typealias R5Bundle = org.hl7.fhir.r5.model.Bundle

typealias R5BundleType = org.hl7.fhir.r5.model.Bundle.BundleType

typealias R5DateTimeType = org.hl7.fhir.r5.model.DateTimeType

typealias R5DateType = org.hl7.fhir.r5.model.DateType

typealias R5InstantType = org.hl7.fhir.r5.model.InstantType

typealias R5Period = org.hl7.fhir.r5.model.Period

typealias R5Timing = org.hl7.fhir.r5.model.Timing

typealias R5StringType = org.hl7.fhir.r5.model.StringType

typealias R5HumanName = org.hl7.fhir.r5.model.HumanName

typealias R5Address = org.hl7.fhir.r5.model.Address

typealias R5Identifier = org.hl7.fhir.r5.model.Identifier

typealias R5CodeableConcept = org.hl7.fhir.r5.model.CodeableConcept

typealias R5ICoding = org.hl7.fhir.r5.model.ICoding

typealias R5Reference = org.hl7.fhir.r5.model.Reference

typealias R5CanonicalType = org.hl7.fhir.r5.model.CanonicalType

typealias R5UriType = org.hl7.fhir.r5.model.UriType

typealias R5Money = org.hl7.fhir.r5.model.Money

typealias R5Quantity = org.hl7.fhir.r5.model.Quantity

typealias R5LocationPositionComponent = org.hl7.fhir.r5.model.Location.LocationPositionComponent

typealias R5OperationOutcome = org.hl7.fhir.r5.model.OperationOutcome
