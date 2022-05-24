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
import com.google.android.fhir.get
import com.google.android.fhir.search.search
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.CarePlan
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.DiagnosticReport
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.EpisodeOfCare
import org.hl7.fhir.r4.model.Group
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ServiceRequest
import org.opencds.cqf.cql.engine.retrieve.TerminologyAwareRetrieveProvider
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Interval

class FhirEngineRetrieveProvider(private val fhirEngine: FhirEngine) :
    TerminologyAwareRetrieveProvider() {
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
                "Patient" ->
                    if (contextValue is String) {
                        mutableListOf(fhirEngine.get<Patient>(contextValue))
                    } else {
                        fhirEngine.search<Patient> { filter(Patient.ACTIVE, { value = of(true) }) }
                    }
                "EpisodeOfCare" ->
                    fhirEngine.search<EpisodeOfCare> {
                        if (contextValue is String) {
                            filter(EpisodeOfCare.PATIENT, { value = "$context/$contextValue" })
                        }
                    }
                "Encounter" ->
                    fhirEngine.search<Encounter> {
                        if (contextValue is String) {
                            filter(Encounter.SUBJECT, { value = "$context/$contextValue" })
                        }
                    }
                "Condition" ->
                    fhirEngine.search<Condition> {
                        if (contextValue is String) {
                            filter(Condition.SUBJECT, { value = "$context/$contextValue" })
                        }
                    }
                "Observation" ->
                    fhirEngine.search<Observation> {
                        if (contextValue is String) {
                            filter(Observation.SUBJECT, { value = "$context/$contextValue" })
                        }
                    }
                "DiagnosticReport" ->
                    fhirEngine.search<DiagnosticReport> {
                        if (contextValue is String) {
                            filter(DiagnosticReport.SUBJECT, { value = "$context/$contextValue" })
                        }
                    }
                "ServiceRequest" ->
                    fhirEngine.search<ServiceRequest> {
                        if (contextValue is String) {
                            filter(ServiceRequest.SUBJECT, { value = "$context/$contextValue" })
                        }
                    }
                "CarePlan" ->
                    fhirEngine.search<CarePlan> {
                        if (contextValue is String) {
                            filter(CarePlan.SUBJECT, { value = "$context/$contextValue" })
                        }
                    }
                "Group" ->
                    if (context == dataType && contextValue is String)
                        mutableListOf(fhirEngine.get<Group>(contextValue))
                    else fhirEngine.search<Group> {}.let { it }
                else -> throw NotImplementedError("Data type $dataType Not implemented yet")
            }
        }
    }
}
