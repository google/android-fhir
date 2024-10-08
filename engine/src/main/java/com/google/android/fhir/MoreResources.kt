/*
 * Copyright 2022-2024 Google LLC
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
import java.time.Instant
import java.util.Date
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.InstantType
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/** The HAPI Fhir package prefix for R4 resources. */
internal const val R4_RESOURCE_PACKAGE_PREFIX = "org.hl7.fhir.r4.model."

/**
 * Returns the FHIR resource type.
 *
 * @throws IllegalArgumentException if class name cannot be mapped to valid resource type
 */
fun <R : Resource> getResourceType(clazz: Class<R>): ResourceType {
  try {
    return clazz.getConstructor().newInstance().resourceType
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
fun <R : Resource> getResourceClass(resourceType: ResourceType): Class<R> =
  getResourceClass(resourceType.name)

/** Returns the {@link Class} object for the resource type. */
fun <R : Resource> getResourceClass(resourceType: String): Class<R> {
  // Remove any curly brackets in the resource type string. This is to work around an issue with
  // JSON deserialization in the CQL engine on Android. The resource type string incorrectly
  // includes namespace prefix in curly brackets, e.g. "{http://hl7.org/fhir}Patient" instead of
  // "Patient".
  // TODO: remove this once a fix has been found for the CQL engine on Android.
  val className = resourceType.replace(Regex("\\{[^}]*\\}"), "")
  return Class.forName(R4_RESOURCE_PACKAGE_PREFIX + className) as Class<R>
}

internal val Resource.versionId: String?
  get() = if (hasMeta()) meta.versionId else null

internal val Resource.lastUpdated
  get() = if (hasMeta()) meta.lastUpdated?.toInstant() else null

/**
 * Updates the meta information of a FHIR [Resource] with the provided version ID and last updated
 * timestamp. This extension function sets the version ID and last updated time in the resource's
 * metadata. If the provided values are null, the respective fields in the meta will remain
 * unchanged.
 */
internal fun Resource.updateMeta(versionId: String?, lastUpdatedRemote: Instant?) {
  meta.apply {
    versionId?.let { versionIdElement = IdType(it) }
    lastUpdatedRemote?.let { lastUpdatedElement = InstantType(Date.from(it)) }
  }
}
