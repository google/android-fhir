package com.google.android.fhir.demo.care
/*
 * Copyright 2023 Google LLC
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

import android.content.Context
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.demo.FhirApplication
import com.google.android.fhir.search.Search
import com.google.android.fhir.search.search
import com.google.android.fhir.workflow.FhirOperator
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.CarePlan
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Library
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.PlanDefinition
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.Task

/**
 * CarePlanManager draft version currently in app side.
 * TODO(mjajoo@): Move it to workflow lib`
 */
class CarePlanManager(
    private val fhirEngine: FhirEngine,
    private val fhirOperator: FhirOperator) {

    //currently 1 PD only
    private var planDefinitionId: String? = null
    private val jsonParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

    fun getPlanDefinitionDependentResources(planDefinition: PlanDefinition): Collection<Resource> {
        var bundleCollection: Collection<Resource> = mutableListOf()
        this.planDefinitionId = IdType(planDefinition.id).idPart
        for (resource in planDefinition.contained) {
            if (resource is Bundle) {
                for (entry in resource.entry) {
                    entry.resource.meta.lastUpdated = planDefinition.meta.lastUpdated
                    if (entry.resource is Library) fhirOperator.loadLib(entry.resource as Library)

                    bundleCollection += entry.resource
                }
            }
        }
        return bundleCollection
    }

    suspend fun generateCarePlanForAllPatients(){
        fhirEngine.search<Patient>{ }
            .forEach { generateCarePlanForPatient(it) }
    }

    suspend fun generateCarePlanForPatient(patient: Patient) {
        val patientId = IdType(patient.id).idPart
        val carePlanTmp =
            fhirOperator.generateCarePlan(planDefinitionId = planDefinitionId!!, patientId = patientId)

        // The workaround below is done because the CarePlan generated above has duplicated resources
        // within contained. This issue does not exist in the newer version of the workflow library
        val carePlanTransient =
            jsonParser.parseResource(jsonParser.encodeResourceToString(carePlanTmp)) as CarePlan

        // Create a new CarePlan of record for the Patient
        val carePlanOfRecord = createCarePlanOfRecordForPatient(patient)

        // Accept the proposed (transient) CarePlan by default and add tasks to the CarePlan of record
        acceptCarePlan(carePlanTransient, carePlanOfRecord)
    }

    private suspend fun createCarePlanOfRecordForPatient(patient: Patient): CarePlan {
        val carePlanOfRecord = CarePlan()
        carePlanOfRecord.status = CarePlan.CarePlanStatus.ACTIVE
        carePlanOfRecord.subject = Reference(patient)
        fhirEngine.create(carePlanOfRecord)
        return carePlanOfRecord
    }

    private fun updateCarePlanWithProtocol(carePlan: CarePlan, uris: List<CanonicalType>) {
        for (uri in uris) carePlan.addInstantiatesCanonical(uri.value)
    }

    private fun addTasksToCarePlan(carePlan: CarePlan, taskList: List<Task>) {
        for (task in taskList) {
            carePlan.addActivity().setReference(Reference(task)).detail.status =
                mapTaskStatusToCarePlanStatus(task.status)
        }
    }

    private suspend fun linkTasksToCarePlan(carePlan: CarePlan, taskList: List<Task>) {
        for (task in taskList) {
            // We will not be creating RequestGroup resources so remove references if they exist
            if (task.basedOn.size > 0 && task.basedOn[0].fhirType() == "RequestGroup")
                task.basedOn = null
            task.addBasedOn(Reference(carePlan).setType(carePlan.fhirType()))
            fhirEngine.update(task)
        }
    }

    private suspend fun acceptCarePlan(carePlanTransient: CarePlan, carePlanOfRecord: CarePlan) {
        val taskList = TaskManager.createTasks(carePlanTransient.contained)
        updateCarePlanWithProtocol(carePlanOfRecord, carePlanTransient.instantiatesCanonical)
        addTasksToCarePlan(carePlanOfRecord, taskList)
        fhirEngine.update(carePlanOfRecord)
        linkTasksToCarePlan(carePlanOfRecord, taskList)
    }

    companion object{
        fun mapTaskStatusToCarePlanStatus(
            taskStatus: Task.TaskStatus
        ): CarePlan.CarePlanActivityStatus {
            // Refer: http://hl7.org/fhir/R4/valueset-care-plan-activity-status.html for some mapping
            // guidelines
            return when (taskStatus) {
                Task.TaskStatus.ACCEPTED -> CarePlan.CarePlanActivityStatus.SCHEDULED
                Task.TaskStatus.DRAFT -> CarePlan.CarePlanActivityStatus.NOTSTARTED
                Task.TaskStatus.REQUESTED -> CarePlan.CarePlanActivityStatus.NOTSTARTED
                Task.TaskStatus.RECEIVED -> CarePlan.CarePlanActivityStatus.NOTSTARTED
                Task.TaskStatus.REJECTED -> CarePlan.CarePlanActivityStatus.STOPPED
                Task.TaskStatus.READY -> CarePlan.CarePlanActivityStatus.NOTSTARTED
                Task.TaskStatus.CANCELLED -> CarePlan.CarePlanActivityStatus.CANCELLED
                Task.TaskStatus.INPROGRESS -> CarePlan.CarePlanActivityStatus.INPROGRESS
                Task.TaskStatus.ONHOLD -> CarePlan.CarePlanActivityStatus.ONHOLD
                Task.TaskStatus.FAILED -> CarePlan.CarePlanActivityStatus.STOPPED
                Task.TaskStatus.COMPLETED -> CarePlan.CarePlanActivityStatus.COMPLETED
                Task.TaskStatus.ENTEREDINERROR -> CarePlan.CarePlanActivityStatus.ENTEREDINERROR
                Task.TaskStatus.NULL -> CarePlan.CarePlanActivityStatus.NULL
                else -> CarePlan.CarePlanActivityStatus.NULL
            }
        }
    }
}
