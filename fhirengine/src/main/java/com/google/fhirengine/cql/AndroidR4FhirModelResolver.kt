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

package com.google.fhirengine.cql

import org.hl7.fhir.instance.model.api.IBase
import org.opencds.cqf.cql.model.R4FhirModelResolver

/** An [R4FhirModelResolver] on Android.  */
internal class AndroidR4FhirModelResolver : R4FhirModelResolver() {
  override fun getContextPath(contextType: String?, targetType: String?): Any? {
    var targetType: String? = targetType
    if (targetType == null || contextType == null) {
      return null
    }
    when (contextType) {
      "Unspecified", "Population" -> return null
      targetType -> return "id"
    }

    // Workaround for the issue of target type incorrectly including a namespace URI prefix.
    // TODO: remove this.
    if (targetType.startsWith(NAMESPACE_URI_PREFIX)) {
      targetType = targetType.substring(NAMESPACE_URI_PREFIX.length)
    }

    val resourceDefinition = fhirContext.getResourceDefinition(targetType)
    val theValue = this.createInstance(contextType)
    val type = theValue.javaClass as Class<out IBase?>
    val children = resourceDefinition.children
    return children.asSequence()
      .mapNotNull { child -> innerGetContextPath(child, type) }
      .firstOrNull()
  }

  companion object {
    /**
     * A prefix that is incorrectly included on Android due to the inconsistency of JSON
     * deserialization.
     */
    const val NAMESPACE_URI_PREFIX = "{http://hl7.org/fhir}"
  }
}
