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

package com.google.android.fhir.workflow

import com.google.android.fhir.FhirEngine
import com.google.android.fhir.search.Operation
import com.google.android.fhir.search.Search
import com.google.android.fhir.search.search
import java.time.Instant
import java.time.Period
import java.util.Date
import org.hl7.fhir.r4.model.CarePlan
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.PractitionerRole
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.Task
import org.hl7.fhir.r4.model.Task.TaskStatus

class TaskManager(
  private var fhirEngine: FhirEngine,
  private val taskConfigMap: MutableMap<String, String>
) : RequestResourceManager<Task> {

  override suspend fun createRequestResource(resource: Task): Task {
    resource.id = null // purge any temporary id that might exist
    resource.status = TaskStatus.READY
    resource.owner = null
    resource.requester = null
    resource.basedOn = null
    resource.intent = Task.TaskIntent.ORDER
    resource.lastModified = Date.from(Instant.now())

    resource.restriction.period.end =
      setDeadline(taskConfigMap["maxDuration"], taskConfigMap["unit"])
    taskConfigMap["owner"]?.let { assignOwner(resource, it) }
    taskConfigMap["requester"]?.let { assignRequester(resource, it) }

    fhirEngine.create(resource)
    return resource
  }

  override suspend fun updateRequestResourceStatus(resource: Task, status: String) {
    resource.status = TaskStatus.valueOf(status)
    resource.executionPeriod.end = Date.from(Instant.now())
    fhirEngine.update(resource)
  }

  override fun mapRequestResourceStatusToCarePlanStatus(
    resource: Task
  ): CarePlan.CarePlanActivityStatus {
    // Refer: http://hl7.org/fhir/R4/valueset-care-plan-activity-status.html for some mapping
    // guidelines
    return when (resource.status) {
      TaskStatus.ACCEPTED -> CarePlan.CarePlanActivityStatus.SCHEDULED
      TaskStatus.DRAFT -> CarePlan.CarePlanActivityStatus.NOTSTARTED
      TaskStatus.REQUESTED -> CarePlan.CarePlanActivityStatus.NOTSTARTED
      TaskStatus.RECEIVED -> CarePlan.CarePlanActivityStatus.NOTSTARTED
      TaskStatus.REJECTED -> CarePlan.CarePlanActivityStatus.STOPPED
      TaskStatus.READY -> CarePlan.CarePlanActivityStatus.NOTSTARTED
      TaskStatus.CANCELLED -> CarePlan.CarePlanActivityStatus.CANCELLED
      TaskStatus.INPROGRESS -> CarePlan.CarePlanActivityStatus.INPROGRESS
      TaskStatus.ONHOLD -> CarePlan.CarePlanActivityStatus.ONHOLD
      TaskStatus.FAILED -> CarePlan.CarePlanActivityStatus.STOPPED
      TaskStatus.COMPLETED -> CarePlan.CarePlanActivityStatus.COMPLETED
      TaskStatus.ENTEREDINERROR -> CarePlan.CarePlanActivityStatus.ENTEREDINERROR
      TaskStatus.NULL -> CarePlan.CarePlanActivityStatus.NULL
      else -> CarePlan.CarePlanActivityStatus.NULL
    }
  }

  override suspend fun linkCarePlanToRequestResource(resource: Task, carePlan: CarePlan) {
    // resource.basedOn.add(Reference(IdType(carePlan.id).idPart).setType(carePlan.fhirType()))
    resource.basedOn.add(Reference(carePlan).setType(carePlan.fhirType()))
    fhirEngine.update(resource)
  }

  suspend fun fetchQuestionnaireFromTaskLogicalId(taskResourceId: String): Questionnaire? {
    val task = fhirEngine.get(ResourceType.Task, taskResourceId) as Task
    val questionnaires =
      fhirEngine.search<Questionnaire> {
        filter(
          Questionnaire.IDENTIFIER,
          { value = of(task.focus.reference.substring("Questionnaire/".length)) }
        )
      }
    return questionnaires.firstOrNull()
  }

  suspend fun getTasksForPatient(
    patientId: String,
    extraFilter: (Search.() -> Unit)?,
    sort: (Search.() -> Unit)?
  ): List<Task> {
    return fhirEngine.search {
      filter(Task.SUBJECT, { value = "Patient/$patientId" })
      if (extraFilter != null) {
        extraFilter()
      }
      operation = Operation.AND
      if (sort != null) {
        sort()
      }
    }
  }

  private suspend fun assignRequester(resource: Task, requesterId: String) {
    val requesterResource =
      fhirEngine.get(
        ResourceType.fromCode(requesterId.substringBefore("/")),
        IdType(requesterId).idPart
      )
    val requesterReference = Reference(requesterResource)
    if (requesterResource is PractitionerRole) {
      requesterReference.apply {
        display = requesterResource.specialty.first()?.coding?.first()?.display
      }
    }
    resource.apply { requester = requesterReference }
  }

  override suspend fun assignOwner(resource: Task, ownerId: String) {
    val ownerResource =
      fhirEngine.get(ResourceType.fromCode(ownerId.substringBefore("/")), IdType(ownerId).idPart)
    val ownerReference = Reference(ownerResource)
    if (ownerResource is PractitionerRole) {
      ownerReference.apply { display = ownerResource.specialty.first()?.coding?.first()?.display }
    }
    resource.apply { owner = ownerReference }
  }

  suspend fun getTasksCount(patientId: String, extraFilter: (Search.() -> Unit)?): Int {
    return extraFilter?.let { getTasksForPatient(patientId, it, null).count() } ?: 0
  }

  companion object {
    fun setDeadline(maxDuration: String?, unit: String?): Date {
      if (unit.isNullOrEmpty() || maxDuration.isNullOrEmpty())
        return Date.from(Instant.now().plus(Period.ofDays(365))) // default: 1 year
      return when (unit) {
        "years" -> Date.from(Instant.now().plus(Period.ofDays(365 * maxDuration.toInt())))
        "months" -> Date.from(Instant.now().plus(Period.ofDays(30 * maxDuration.toInt())))
        "days" -> Date.from(Instant.now().plus(Period.ofDays(maxDuration.toInt())))
        else -> Date.from(Instant.now().plus(Period.ofDays(365))) // default: 1 year
      }
    }
  }
}
