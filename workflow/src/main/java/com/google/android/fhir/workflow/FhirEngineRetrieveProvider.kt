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
import org.hl7.fhir.r4.model.*
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
  ): Iterable<Any> {
    return runBlocking {
      when (dataType) {
        "Patient" -> {
          if (contextValue is String) {
            mutableListOf(fhirEngine.load(Patient::class.java, contextValue))
          } else {
            val patients =
              fhirEngine.search<Patient> { filter(Patient.ACTIVE, { value = of(true) }) }
            patients.toMutableList()
          }
        }
        "EpisodeOfCare" -> {
          if (contextValue is String) {
            val patientsEpisodesOfCare =
              fhirEngine.search<EpisodeOfCare> {
                filter(EpisodeOfCare.PATIENT, { value = "$context/$contextValue" })
              }
            patientsEpisodesOfCare.toMutableList()
          } else {
            val patientsEpisodesOfCare =
              fhirEngine.search<EpisodeOfCare> { filter(Patient.ACTIVE, { value = of(true) }) }
            patientsEpisodesOfCare.toMutableList()
          }
        }
        "Encounter" -> {
          if (contextValue is String) {
            val encounters =
              fhirEngine.search<Encounter> {
                filter(Encounter.SUBJECT, { value = "$context/$contextValue" })
              }
            encounters.toMutableList()
          } else {
            val encounters =
              fhirEngine.search<Encounter> { filter(Patient.ACTIVE, { value = of(true) }) }
            encounters.toMutableList()
          }
        }
        "Condition" -> {
          if (contextValue is String) {
            val conditions =
              fhirEngine.search<Condition> {
                filter(Condition.SUBJECT, { value = "$context/$contextValue" })
              }
            conditions.toMutableList()
          } else {
            val conditions =
              fhirEngine.search<Condition> { filter(Patient.ACTIVE, { value = of(true) }) }
            conditions.toMutableList()
          }
        }
        "Observation" -> {
          if (contextValue is String) {
            val observations =
              fhirEngine.search<Observation> {
                filter(Observation.SUBJECT, { value = "$context/$contextValue" })
              }
            observations.toMutableList()
          } else {
            val observations =
              fhirEngine.search<Observation> { filter(Patient.ACTIVE, { value = of(true) }) }
            observations.toMutableList()
          }
        }
        "DiagnosticReport" -> {
          if (contextValue is String) {
            val diagnosis =
              fhirEngine.search<DiagnosticReport> {
                filter(DiagnosticReport.SUBJECT, { value = "$context/$contextValue" })
              }
            diagnosis.toMutableList()
          } else {
            val diagnosis =
              fhirEngine.search<DiagnosticReport> { filter(Patient.ACTIVE, { value = of(true) }) }
            diagnosis.toMutableList()
          }
        }
        "ServiceRequest" -> {
          if (contextValue is String) {
            val serviceRequests =
              fhirEngine.search<ServiceRequest> {
                filter(ServiceRequest.SUBJECT, { value = "$context/$contextValue" })
              }
            serviceRequests.toMutableList()
          } else {
            val serviceRequests =
              fhirEngine.search<ServiceRequest> { filter(Patient.ACTIVE, { value = of(true) }) }
            serviceRequests.toMutableList()
          }
        }

        "CarePlan" -> {
          if (contextValue is String) {
            val careplan =
              fhirEngine.search<CarePlan> {
                filter(CarePlan.SUBJECT, { value = "$context/$contextValue" })
              }
            careplan.toMutableList()
          } else {
            val careplan =
              fhirEngine.search<CarePlan> { filter(Patient.ACTIVE, { value = of(true) }) }
            careplan.toMutableList()
          }
        }
        else -> {
          throw NotImplementedError("Not implemented yet")
        }
      }
    }
  }
}
