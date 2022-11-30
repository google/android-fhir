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
import org.hl7.fhir.instance.model.api.IAnyResource

/**
 * Returns the FHIR resource type.
 *
 * @throws IllegalArgumentException if class name cannot be mapped to valid resource type
 */
fun <R : IAnyResource> getResourceType(clazz: Class<R>): String {
  try {
    return clazz.getConstructor().newInstance().fhirType()
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
inline fun <reified R : IAnyResource> getResourceClass(resourceType: String): Class<R> {
  // Remove any curly brackets in the resource type string. This is to work around an issue with
  // JSON deserialization in the CQL engine on Android. The resource type string incorrectly
  // includes namespace prefix in curly brackets, e.g. "{http://hl7.org/fhir}Patient" instead of
  // "Patient".
  // TODO: remove this once a fix has been found for the CQL engine on Android.
  val className = resourceType.replace(Regex("\\{[^}]*\\}"), "")
  return Class.forName(R::class.java.`package`?.name + "." + className) as Class<R>
}

val IAnyResource.resourceType: String
  get() = this.fhirType()

/**
 * The logical (unqualified) part of the ID. For example, if the ID is
 * "http://example.com/fhir/Patient/123/_history/456", then this value would be "123".
 */
val IAnyResource.logicalId: String
  get() {
    return this.idElement?.idPart.orEmpty()
  }

internal val IAnyResource.lastUpdated
  get() = if (this.meta != null && !this.meta.isEmpty) meta.lastUpdated?.toInstant() else null
