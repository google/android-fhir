package com.google.android.fhir.configurablecare.care

import org.hl7.fhir.r4.model.RequestGroup
import org.hl7.fhir.r4.model.RequestGroup.RequestStatus
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.Task

interface RequestHandler {
  fun acceptProposedRequest(request: Resource): Boolean {
    return true
  }
}

class RequestUtils {

  companion object  {

    fun isValidRequest(resourceType: ResourceType): Boolean {
      return when (resourceType) {
        ResourceType.Task, ResourceType.MedicationRequest, ResourceType.ServiceRequest, ResourceType.CommunicationRequest -> true
        else -> false
      }
    }

    // valid status change:
    // do by resource and then valid next states for present state
    fun mapTaskStatusToRequestStatus(taskStatus: Task.TaskStatus): RequestStatus {
      val requestStatus = when (taskStatus) {
        Task.TaskStatus.DRAFT -> RequestStatus.DRAFT
        Task.TaskStatus.ACCEPTED -> RequestStatus.DRAFT
        Task.TaskStatus.RECEIVED -> RequestStatus.DRAFT
        Task.TaskStatus.INPROGRESS -> RequestStatus.ACTIVE
        Task.TaskStatus.ONHOLD -> RequestStatus.ONHOLD
        Task.TaskStatus.REJECTED -> RequestStatus.REVOKED
        Task.TaskStatus.CANCELLED -> RequestStatus.REVOKED
        Task.TaskStatus.COMPLETED -> RequestStatus.COMPLETED
        Task.TaskStatus.ENTEREDINERROR -> RequestStatus.ENTEREDINERROR
        Task.TaskStatus.NULL -> RequestStatus.NULL
        else -> RequestStatus.NULL
      }
      return requestStatus
    }

    fun mapRequestStatusToTaskStatus(requestStatus: RequestStatus): Task.TaskStatus {
      val taskStatus = when (requestStatus) {
        RequestStatus.DRAFT -> Task.TaskStatus.DRAFT
        RequestStatus.ACTIVE -> Task.TaskStatus.INPROGRESS
        RequestStatus.ONHOLD -> Task.TaskStatus.ONHOLD
        RequestStatus.REVOKED -> Task.TaskStatus.REJECTED
        RequestStatus.COMPLETED -> Task.TaskStatus.COMPLETED
        RequestStatus.ENTEREDINERROR -> Task.TaskStatus.ENTEREDINERROR
        RequestStatus.UNKNOWN -> Task.TaskStatus.NULL
        RequestStatus.NULL -> Task.TaskStatus.NULL
        else -> Task.TaskStatus.NULL
      }
      return taskStatus
    }

    fun mapTaskIntentToRequestIntent(taskIntent: Task.TaskIntent): RequestGroup.RequestIntent {
      val requestIntent = when (taskIntent) {
        Task.TaskIntent.PLAN -> RequestGroup.RequestIntent.PLAN
        Task.TaskIntent.PROPOSAL -> RequestGroup.RequestIntent.PROPOSAL
        Task.TaskIntent.ORDER -> RequestGroup.RequestIntent.ORDER
        else -> RequestGroup.RequestIntent.NULL
      }
      return requestIntent
    }

    fun mapRequestIntentToTaskIntent(requestIntent: RequestGroup.RequestIntent): Task.TaskIntent {
      val taskIntent = when (requestIntent) {
        RequestGroup.RequestIntent.PLAN -> Task.TaskIntent.PLAN
        RequestGroup.RequestIntent.PROPOSAL -> Task.TaskIntent.PROPOSAL
        RequestGroup.RequestIntent.ORDER -> Task.TaskIntent.ORDER
        else -> Task.TaskIntent.NULL
      }
      return taskIntent
    }
  }
}