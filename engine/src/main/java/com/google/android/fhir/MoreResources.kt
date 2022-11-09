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
import org.hl7.fhir.r4.model.Configuration

/** The HAPI Fhir package prefix for R4 resources. */
internal const val R4_RESOURCE_PACKAGE_PREFIX = "org.hl7.fhir.r4.model."

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

// TODO: Figure out how to make the R4_RESOURCE_PACKAGE_PREFIX a variable where we can sub in
// version
/** Returns the {@link Class} object for the resource type. */
fun <R : IAnyResource> getResourceClass(resourceType: ResourceType): Class<R> =
  getResourceClass(resourceType.name)

/** Returns the {@link Class} object for the resource type. */
fun <R : IAnyResource> getResourceClass(resourceType: String): Class<R> {
  // Remove any curly brackets in the resource type string. This is to work around an issue with
  // JSON deserialization in the CQL engine on Android. The resource type string incorrectly
  // includes namespace prefix in curly brackets, e.g. "{http://hl7.org/fhir}Patient" instead of
  // "Patient".
  // TODO: remove this once a fix has been found for the CQL engine on Android.
  val className = resourceType.replace(Regex("\\{[^}]*\\}"), "")
  return Class.forName(R4_RESOURCE_PACKAGE_PREFIX + className) as Class<R>
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
      if (Configuration.errorOnAutoCreate()) {
        throw Error("Attempt to auto-create Meta.lastUpdated")
      }

      if (Configuration.doAutoCreate()) {
        this.lastUpdated = Date()
      }
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
