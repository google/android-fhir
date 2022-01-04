/*
 * Copyright 2021 Google LLC
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

package com.google.android.fhir.workflow

import com.google.android.fhir.FhirEngine
import com.google.android.fhir.search.search
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.EpisodeOfCare
import org.hl7.fhir.r4.model.Patient
import org.opencds.cqf.cql.engine.retrieve.TerminologyAwareRetrieveProvider
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Interval

class FhirEngineRetrieveProvider(val fhirEngine: FhirEngine) : TerminologyAwareRetrieveProvider() {
  override fun retrieve(
    context: String?,
    contextPath: String?,
    contextValue: Any?,
    dataType: String?,
    templateId: String?,
    codePath: String?,
    codes: MutableIterable<Code>?,
    valueSet: String?,
    datePath: String?,
    dateLowPath: String?,
    dateHighPath: String?,
    dateRange: Interval?
  ): MutableIterable<Any> {
    return runBlocking {
      when (dataType) {
        "Patient" -> mutableListOf(fhirEngine.load(Patient::class.java, contextValue.toString()))
        "EpisodeOfCare" -> {
          val a =
            fhirEngine.search<EpisodeOfCare> {
              filter(EpisodeOfCare.PATIENT, { value = context + "/" + contextValue.toString() })
            }
          a.toMutableList()
        }
        "Encounter" -> {
          val a =
            fhirEngine.search<Encounter> {
              filter(Encounter.SUBJECT, { value = context + "/" + contextValue.toString() })
            }
          a.toMutableList()
        }
        else -> {
          throw NotImplementedError("Not implemented yet")
        }
      }
    }
  }
}
