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

package com.google.fhirengine.index

import org.hl7.fhir.r4.model.ResourceType

/**
 * Encapsulation of a FHIR resource (its resource type and ID) and all the indices that are
 * extracted from the resource.
 *
 *
 * For example, for a [org.hl7.fhir.r4.model.Patient], this class should include the patient's
 * resource type, ID, and all the field values of the patient that should be indexed such as name,
 * email address.
 */
internal data class ResourceIndices(
  /** The resource type.  */
  val resourceType: ResourceType,
  /** The ID of the resource.  */
  val id: String,
  /** The string indices.  */
  val stringIndices: List<StringIndex>,
  /** The reference indices.  */
  val referenceIndices: List<ReferenceIndex>,
  /** The code indices.  */
  val codeIndices: List<CodeIndex>
) {

    class Builder(
      val resourceType: ResourceType,
      val id: String
    ) {
        private val stringIndices = mutableListOf<StringIndex>()
        private val referenceIndices = mutableListOf<ReferenceIndex>()
        private val codeIndices = mutableListOf<CodeIndex>()

        fun addStringIndex(stringIndex: StringIndex) = this.also {
            stringIndices.add(stringIndex)
        }

        fun addReferenceIndex(referenceIndex: ReferenceIndex) = this.also {
            referenceIndices.add(referenceIndex)
        }

        fun addCodeIndex(codeIndex: CodeIndex) = this.also {
            codeIndices.add(codeIndex)
        }

        fun build() = ResourceIndices(
                resourceType = resourceType,
                id = id,
                stringIndices = stringIndices.toList(),
                referenceIndices = referenceIndices.toList(),
                codeIndices = codeIndices.toList()
        )
    }
}
