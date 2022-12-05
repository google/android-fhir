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

import ca.uhn.fhir.context.FhirVersionEnum
import org.hl7.fhir.exceptions.FHIRException

/** Provides a [FhirAdapter] from a supplied [FhirVersionEnum]. */
internal enum class FhirAdapterProvider {
  R4FhirAdapter {
    override fun createAdapter(): FhirAdapter {
      TODO("To be implemented in a follow up PR")
    }
  };
  internal abstract fun createAdapter(): FhirAdapter

  companion object {
    fun from(value: FhirVersionEnum): FhirAdapterProvider =
      when (value) {
        FhirVersionEnum.R4 -> R4FhirAdapter
        else -> throw FHIRException("$value version not yet supported in the FHIR Engine library")
      }
  }
}
