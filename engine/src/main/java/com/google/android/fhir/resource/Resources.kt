/*
 * Copyright 2020 Google LLC
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

package com.google.android.fhir.resource

import java.lang.reflect.InvocationTargetException
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/** The HAPI Fhir package prefix for R4 resources. */
internal val R4_RESOURCE_PACKAGE_PREFIX = "org.hl7.fhir.r4.model."

/** Returns the FHIR resource type. */
internal fun <R : Resource> getResourceType(clazz: Class<R>): ResourceType {
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
internal fun <R : Resource> getResourceClass(resourceType: String): Class<R> {
  // Remove any curly brackets in the resource type string. This is to work around an issue with
  // JSON deserialization in the CQL engine on Android. The resource type string incorrectly
  // includes namespace prefix in curly brackets, e.g. "{http://hl7.org/fhir}Patient" instead of
  // "Patient".
  // TODO: remove this once a fix has been found for the CQL engine on Android.
  val className = resourceType.replace(Regex("\\{[^}]*\\}"), "")
  return Class.forName(R4_RESOURCE_PACKAGE_PREFIX + className) as Class<R>
}
