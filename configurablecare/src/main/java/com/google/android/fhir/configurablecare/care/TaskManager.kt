/*
 * Copyright 2022-2023 Google LLC
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
package com.google.android.fhir.configurablecare.care

import com.google.android.fhir.FhirEngine
import com.google.android.fhir.get
import com.google.android.fhir.search.Operation
import com.google.android.fhir.search.Search
import com.google.android.fhir.search.search
import java.lang.StringBuilder
import java.time.Instant
import java.time.Period
import java.util.Date
import org.hl7.fhir.r4.model.CarePlan
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.MedicationRequest
import org.hl7.fhir.r4.model.PractitionerRole
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.ServiceRequest
import org.hl7.fhir.r4.model.Task
import org.hl7.fhir.r4.model.Task.TaskStatus
import timber.log.Timber

/** Responsible for creating and managing Task resources */
class TaskManager(private var fhirEngine: FhirEngine) : RequestResourceManager<Task> {

  suspend fun getMedicationRequestsForPatient(
    patientId: String,
    extraFilter: (Search.() -> Unit)?,
    sort: (Search.() -> Unit)?
  ): List<MedicationRequest> {
    return fhirEngine.search<MedicationRequest> {
      filter(MedicationRequest.SUBJECT, { value = "Patient/$patientId" })
      if (extraFilter != null) {
        extraFilter()
      }
      operation = Operation.AND
      if (sort != null) {
        sort()
      }
    }.map { it.resource }
  }

  suspend fun getRequestsForPatient(
    patientId: String,
    requestType: ResourceType,
    status: String = "",
    intent: String = ""
  ): List<Resource> {
    val requestList: MutableList<Resource> = mutableListOf()
    if (enumValues<SupportedRequestResources>().any { it.value.toCode() == requestType.name }) {
      val xFhirQueryBuilder = StringBuilder()
      xFhirQueryBuilder.append("${requestType.name}?subject=Patient/$patientId")
      if (status.isNotEmpty()) {
        xFhirQueryBuilder.append("&status=$status")
      }
      if (intent.isNotEmpty()) {
        xFhirQueryBuilder.append("&intent=$intent")
      }
      val searchList = fhirEngine.search(xFhirQueryBuilder.toString())
      for (item in searchList) {
        requestList.add(item.resource)
      }
    }
    return requestList  // not a valid or supported request
  }

  suspend fun getAllRequestsForPatient(patientId: String,
                                       status: String = "",
                                       intent: String = ""): List<Resource> {
    val requestList: MutableList<Resource> = mutableListOf()
    for (requestResourceType in SupportedRequestResources.values()) {
      val xFhirQueryBuilder = StringBuilder()
      xFhirQueryBuilder.append("${requestResourceType.value.toCode()}?subject=Patient/$patientId")
      if (status.isNotEmpty()) {
        xFhirQueryBuilder.append("&status=$status")
      }
      if (intent.isNotEmpty()) {
        xFhirQueryBuilder.append("&intent=$intent")
      }
      val searchList = fhirEngine.search(xFhirQueryBuilder.toString())
      for (item in searchList) {
        requestList.add(item.resource)
      }
    }
    return requestList
  }

  /** Update [Task] resource with application-specific configurations and add it to [FhirEngine]. */
  override suspend fun updateRequestResource(
    resource: Task,
    requestResourceConfig: RequestResourceConfig
  ): Task {
    resource.id = null // purge any temporary id that might exist
    resource.status = TaskStatus.READY
    resource.owner = null
    resource.requester = null
    resource.basedOn = null
    resource.intent = Task.TaskIntent.ORDER
    resource.lastModified = Date.from(Instant.now())

    resource.restriction.period.end =
      setDeadline(requestResourceConfig.maxDuration, requestResourceConfig.unit)
    requestResourceConfig.values
      .firstOrNull { it.field == "owner" }
      ?.let { assignOwner(resource, it.value) }
    requestResourceConfig.values
      .firstOrNull { it.field == "requester" }
      ?.let { assignRequester(resource, it.value) }

    fhirEngine.create(resource)
    return resource
  }

  /** Update [Task] status */
  override suspend fun updateRequestResourceStatus(resource: Task, status: String) {
    resource.status = TaskStatus.valueOf(status)
    resource.executionPeriod.end = Date.from(Instant.now())
    fhirEngine.update(resource)
  }

  /** Map [Task] status to [CarePlan] status */
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

  /** Add [CarePlan] reference to the [Task] */
  override suspend fun linkCarePlanToRequestResource(resource: Task, carePlan: CarePlan) {
    resource.basedOn.add(Reference(carePlan).setType(carePlan.fhirType()))
    fhirEngine.update(resource)
  }

  /**
   * Fetch the respective [Questionnaire] for a [Task] whose purpose is the completion of a FHIR
   * Questionnaire
   */
  suspend fun fetchQuestionnaireFromTaskLogicalId(taskResourceId: String): Questionnaire? {
    val task = fhirEngine.get(ResourceType.Task, taskResourceId) as Task
    // val questionnaires =
    //   fhirEngine.search<Questionnaire> {
    //     filter(
    //       Questionnaire.IDENTIFIER,
    //       { value = of(task.focus.reference.substring("Questionnaire/".length)) }
    //     )
    //   }
    val questionnaire = fhirEngine.get<Questionnaire>(task.focus.reference.substring("Questionnaire/".length))
    return questionnaire // questionnaires.firstOrNull()
  }

  suspend fun fetchQuestionnaire(questionnaireId: String): Questionnaire {
    // try {
      val questionnaire = fhirEngine.get<Questionnaire>(IdType(questionnaireId).idPart)
    return questionnaire
    // } catch (e: Exception) {
    //   Timber.w("Need to load Knowledge resources")
    // }
  }

  /** Fetch all Tasks for a given Patient */
  suspend fun getTasksForPatient(
    patientId: String,
    extraFilter: (Search.() -> Unit)?,
    sort: (Search.() -> Unit)?
  ): List<Task> {
    return fhirEngine.search<Task> {
      filter(Task.SUBJECT, { value = "Patient/$patientId" })
      if (extraFilter != null) {
        extraFilter()
      }
      operation = Operation.AND
      if (sort != null) {
        sort()
      }
    }.map { it.resource }
  }

  /** Populate the requester field in the given [Task] */
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

  /** Populate the owner field in the given [Task] */
  override suspend fun assignOwner(resource: Task, ownerId: String) {
    val ownerResource =
      fhirEngine.get(ResourceType.fromCode(ownerId.substringBefore("/")), IdType(ownerId).idPart)
    val ownerReference = Reference(ownerResource)
    if (ownerResource is PractitionerRole) {
      ownerReference.apply { display = ownerResource.specialty.first()?.coding?.first()?.display }
    }
    resource.apply { owner = ownerReference }
  }

  /** Compute the number of Tasks for a given Patient */
  // suspend fun getTasksCount(patientId: String, extraFilter: (Search.() -> Unit)?): Int {
  //   return extraFilter?.let { getAllRequestsForPatient(patientId).count() } ?: 0
  // }

  /** Create a new [Task] that can track the progress of a [ServiceRequest] */
  fun createTrackingTaskForServiceRequest(
    serviceRequest: ServiceRequest,
    subjectReference: Reference,
    description: String?,
    requestResourceConfig: RequestResourceConfig
  ): Task {
    val task = Task()
    task.owner = serviceRequest.requester
    task.description = "[Tracking Task] $description"
    task.`for` = subjectReference
    task.focus = Reference("ServiceRequest/${serviceRequest.id}")
    task.status = TaskStatus.READY
    task.intent = Task.TaskIntent.ORDER
    task.lastModified = Date.from(Instant.now())

    task.restriction.period.end =
      setDeadline(requestResourceConfig.maxDuration, requestResourceConfig.unit)

    return task
  }

  /**
   * Set the due date of a [Task]
   *
   * @param maxDuration Numerical value of the time duration
   * @param unit Unit of time: "days", "months" or "years"
   */
  private fun setDeadline(maxDuration: String?, unit: String?): Date {
    if (unit.isNullOrEmpty() || maxDuration.isNullOrEmpty())
      return Date.from(Instant.now().plus(Period.ofDays(365))) // default: 1 year
    return when (unit) {
      "years" -> Date.from(Instant.now().plus(Period.ofDays(365 * maxDuration.toInt())))
      "months" -> Date.from(Instant.now().plus(Period.ofDays(30 * maxDuration.toInt())))
      "days" -> Date.from(Instant.now().plus(Period.ofDays(maxDuration.toInt())))
      else -> Date.from(Instant.now().plus(Period.ofDays(365))) // default: 1 year
    }
  }

  companion object {
    enum class SupportedRequestResources(val value: Enumerations.RequestResourceType) {
      TASK(Enumerations.RequestResourceType.TASK),
      MEDICATIONREQUEST(Enumerations.RequestResourceType.MEDICATIONREQUEST),
      SERVICEREQUEST(Enumerations.RequestResourceType.SERVICEREQUEST),
      // COMMUNICATIONREQUEST(RequestResourceType.COMMUNICATIONREQUEST)
    }
  }
}
