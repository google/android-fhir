package com.google.android.fhir.configurablecare.care

import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.get
import com.google.android.fhir.search.search
import java.lang.StringBuilder
import java.util.UUID
import org.hl7.fhir.r4.model.CommunicationRequest
import org.hl7.fhir.r4.model.CommunicationRequest.CommunicationRequestStatus
import org.hl7.fhir.r4.model.Enumerations.RequestResourceType
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.MedicationRequest
import org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestStatus
import org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestIntent
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.RequestGroup
import org.hl7.fhir.r4.model.RequestGroup.RequestIntent
import org.hl7.fhir.r4.model.RequestGroup.RequestStatus
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.ServiceRequest
import org.hl7.fhir.r4.model.ServiceRequest.ServiceRequestStatus
import org.hl7.fhir.r4.model.ServiceRequest.ServiceRequestIntent
import org.hl7.fhir.r4.model.Task
import org.hl7.fhir.r4.model.Task.TaskIntent
import org.hl7.fhir.r4.model.Task.TaskStatus

class RequestManager(
  private var fhirEngine: FhirEngine,
  fhirContext: FhirContext,
  private val requestHandler: RequestHandler,
) {
  private val jsonParser = fhirContext.newJsonParser()

  /** Ensure the Task has a status and intent defined */
  private fun validateTask(task: Task) {
    task.id = UUID.randomUUID().toString()
    if (task.status == null)
      task.status = TaskStatus.DRAFT
    if (task.intent == null)
      task.intent = TaskIntent.PROPOSAL
  }

  private fun validateServiceRequest(serviceRequest: ServiceRequest) {
    serviceRequest.id = UUID.randomUUID().toString()
    if (serviceRequest.status == null)
      serviceRequest.status = ServiceRequestStatus.DRAFT
    serviceRequest.intent = ServiceRequestIntent.PROPOSAL
  }

  private fun validateMedicationRequest(medicationRequest: MedicationRequest) {
    medicationRequest.id = UUID.randomUUID().toString()
    if (medicationRequest.status == null)
      medicationRequest.status = MedicationRequestStatus.DRAFT
    medicationRequest.intent = MedicationRequestIntent.PROPOSAL
  }


  /** Creates the request given a RequestGroup or RequestOrchestration? */
  suspend fun createRequestFromRequestGroup(requestGroup: RequestGroup): List<Resource> {
    val resourceList: MutableList<Resource> = mutableListOf()
    for (request in requestGroup.contained) {
      when (request) {
        is Task -> validateTask(request)
        is MedicationRequest -> validateMedicationRequest(request)
        is ServiceRequest -> validateServiceRequest(request)
        is CommunicationRequest -> TODO("Implement validateCommunicationRequest()")
        else -> TODO("Not a valid request")
      }
      if (requestHandler.acceptProposedRequest(request)) {
        fhirEngine.create(request)
        resourceList.add(request)
      }
    }
    return resourceList
  }

  // suspend fun getRequestsForPatient(patientId: String,
  //                                   extraFilter: (Search.() -> Unit)?,
  //                                   sort: (Search.() -> Unit)?): List<Resource> {

  suspend fun getRequestsForPatient(
    patientId: String,
    requestType: ResourceType,
    status: String = "",
    intent: String = ""
  ): List<Resource> {
    val requestList: MutableList<Resource> = mutableListOf()
    if (enumValues<TaskManager.Companion.SupportedRequestResources>().any { it.value.toCode() == requestType.name }) {
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

  suspend fun getAllRequestsForPatient(
    patientId: String,
    status: String = "",
    intent: String = ""
  ): List<Resource> {
    val requestList: MutableList<Resource> = mutableListOf()
    for (requestResourceType in TaskManager.Companion.SupportedRequestResources.values()) {
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

  /** Updates the request status */
  // suspend fun updateStatus(
  //   request: Resource,
  //   status: RequestStatus,
  // ) { // should this be RequestStatus mapped to the Request
  //   when (request) {
  //     "Task" -> updateTaskStatus(
  //       request as Task,
  //       RequestUtils.mapRequestStatusToTaskStatus(status)
  //     )
  //
  //     "MedicationRequest" -> TODO("Implement updateMedicationRequestStatus()")
  //     "ServiceRequest" -> TODO("Implement updateServiceRequestStatus()")
  //     "CommunicationRequest" -> TODO("Implement updateCommunicationRequestStatus()")
  //     "ImmunizationRecommendation" -> TODO("Implement updateImmunizationRecommendationStatus()")
  //     else -> TODO("Not a valid request")
  //   }
  // }

  /** Update intent */
  suspend fun updateIntent(request: Resource, intent: RequestIntent) {
    when (request) {
      is Task -> updateTaskIntent(
        request,
        RequestUtils.mapRequestIntentToTaskIntent(intent)
      )

      is MedicationRequest -> TODO("Implement updateMedicationRequestStatus()")
      is ServiceRequest -> TODO("Implement updateServiceRequestStatus()")
      is CommunicationRequest -> TODO("Implement updateCommunicationRequestStatus()")
      else -> TODO("Not a valid request")
    }
  }

  private fun isValidStatusTransition(
    currentStatus: RequestStatus?,
    newStatus: RequestStatus,
  ): Boolean {
    if (newStatus == RequestStatus.DRAFT)
      return currentStatus == RequestStatus.NULL

    if (newStatus == RequestStatus.ACTIVE)
      return (currentStatus == RequestStatus.DRAFT || currentStatus == RequestStatus.ONHOLD)

    if (newStatus == RequestStatus.ONHOLD)
      return currentStatus == RequestStatus.ACTIVE

    if (newStatus == RequestStatus.COMPLETED)
      return currentStatus == RequestStatus.ACTIVE

    if (newStatus == RequestStatus.REVOKED)
      return currentStatus == RequestStatus.ACTIVE

    return newStatus == RequestStatus.ENTEREDINERROR
  }

  private suspend fun updateTaskStatus(task: Task, status: TaskStatus) {
    // is transition valid - refer: https://www.hl7.org/fhir/request.html#statemachine
    val currentStatus = task.status
    if (isValidStatusTransition(
        RequestUtils.mapTaskStatusToRequestStatus(currentStatus),
        RequestUtils.mapTaskStatusToRequestStatus(status)
      )
    ) {
      task.status = status
      fhirEngine.update(task)
    }
    // else do nothing
  }

  private suspend fun updateTaskIntent(task: Task, intent: TaskIntent) {
    // a new Task has to be created if the intent is updated
    // a new instance 'basedOn' the prior instance should be created with the new 'intent' value.
    val newTask: Task = Task().apply {
      id = UUID.randomUUID().toString()
      status = TaskStatus.DRAFT
      task.intent = intent
      basedOn.add(Reference(task))
    }
    fhirEngine.create(newTask)
  }

  /*
  "Plan an active CommunicationRequest proposal

Begin CommunicationRequest Plan [proposal -> plan]:
Request requestApi.beginPlan(Request inputProposal)
    check inputProposal.intent = proposal
    check inputProposal.status = active
    var result = new Request(copy from inputProposal)
    set result.id = null
    set result.intent = plan
    set result.status = draft
    set result.basedOn = referenceTo(inputProposal)"
"End CommunicationRequest Plan:
requestApi.endPlan(Request inputPlan)
    check inputPlan.basedOn is not null
    var basedOnProposal = engine.get(inputPlan.basedOn)
    check basedOnProposal.intent = proposal
    check basedOnProposal.status = active
    check inputPlan.status in { draft | active }
    check inputPlan.intent = plan
    set basedOnProposal.status = completed
    try
        engine.save(inputPlan)
        engine.save(basedOnProposal)
    commit"

   */

  private suspend fun beginPlan(medicationRequest: MedicationRequest) {
    if (medicationRequest.status == MedicationRequestStatus.DRAFT) {
      medicationRequest.status = MedicationRequestStatus.ACTIVE
    }
    if (medicationRequest.status == MedicationRequestStatus.ACTIVE) {
      val newMedicationRequest: MedicationRequest = medicationRequest.copy()
      newMedicationRequest.id = UUID.randomUUID().toString()
      newMedicationRequest.status = MedicationRequestStatus.DRAFT
      newMedicationRequest.intent = MedicationRequestIntent.PLAN
      newMedicationRequest.basedOn.add(Reference(medicationRequest))

      fhirEngine.create(newMedicationRequest)
    } else {
      // do nothing
    }
  }

  // suspend fun


  suspend fun endPlan(medicationRequest: MedicationRequest) {
    if (medicationRequest.basedOn.isNotEmpty()) {
      val basedOnProposal =
        fhirEngine.get<MedicationRequest>(IdType(medicationRequest.basedOnFirstRep.id).idPart)
      if (basedOnProposal.status == MedicationRequestStatus.ACTIVE && basedOnProposal.intent == MedicationRequestIntent.PLAN) {
        if (medicationRequest.status == MedicationRequestStatus.DRAFT)
          medicationRequest.status = MedicationRequestStatus.ACTIVE
        if (medicationRequest.status == MedicationRequestStatus.ACTIVE && medicationRequest.intent == MedicationRequestIntent.PROPOSAL) {
          basedOnProposal.status = MedicationRequestStatus.COMPLETED

          fhirEngine.update(medicationRequest)
          fhirEngine.update(basedOnProposal)

        } else {
          // do nothing
        }
      } else {
        // do nothing
      }
    } else {
      // do nothing
    }
  }

  suspend fun updateMedicationRequestIntent(medicationRequest: MedicationRequest, intent: MedicationRequestIntent) {
    // a new MedicationRequest has to be created if the intent is updated
    // a new instance 'basedOn' the prior instance should be created with the new 'intent' value.

    val newMedicationRequest: MedicationRequest = MedicationRequest().apply {
      id = UUID.randomUUID().toString()
      status = MedicationRequestStatus.ACTIVE
      medicationRequest.intent = intent
      basedOn.add(Reference(medicationRequest))
    }
    fhirEngine.create(newMedicationRequest)
  }

  private suspend fun updateServiceRequestIntent(serviceRequest: ServiceRequest, intent: ServiceRequestIntent) {
    // a new ServiceRequest has to be created if the intent is updated
    // a new instance 'basedOn' the prior instance should be created with the new 'intent' value.
    val newServiceRequest: ServiceRequest = ServiceRequest().apply {
      id = UUID.randomUUID().toString()
      status = ServiceRequestStatus.DRAFT
      serviceRequest.intent = intent
      basedOn.add(Reference(serviceRequest))
    }
    fhirEngine.create(newServiceRequest)
  }


  suspend fun planRequest(request: Resource) {
    when (request) {
      is Task -> if (request.intent == TaskIntent.PROPOSAL) updateTaskIntent(request, TaskIntent.PLAN)
      is MedicationRequest -> if (request.intent == MedicationRequestIntent.PROPOSAL) updateMedicationRequestIntent(request, MedicationRequestIntent.PLAN)
      is ServiceRequest -> if (request.intent == ServiceRequestIntent.PROPOSAL) updateServiceRequestIntent(request, ServiceRequestIntent.PLAN)
      // CommunicationRequest does not have an intent
      else -> {}
    }
  }

  suspend fun orderRequest(request: Resource) {
    when (request) {
      is Task -> if (request.intent == TaskIntent.PLAN) updateTaskIntent(request, TaskIntent.ORDER)
      is MedicationRequest -> if (request.intent == MedicationRequestIntent.PLAN) updateMedicationRequestIntent(request, MedicationRequestIntent.ORDER)
      is ServiceRequest -> if (request.intent == ServiceRequestIntent.PLAN) updateServiceRequestIntent(request, ServiceRequestIntent.ORDER)
      // CommunicationRequest does not have an intent
      else -> {}
    }
  }



  // /** Change request status to accepted (or equivalent) as long as the status change is allowed */
  // fun acceptRequest(request: Resource) {
  //   when (request) {
  //     is Task -> {
  //       when (request.status) {
  //         TaskStatus.DRAFT -> request.status = TaskStatus.ACCEPTED
  //         else -> {}
  //       }
  //     }
  //
  //     is MedicationRequest -> {
  //       when (request.status) {
  //         MedicationRequestStatus.DRAFT,
  //         MedicationRequestStatus.ONHOLD,
  //         -> request.status = MedicationRequestStatus.ACTIVE
  //
  //         else -> {}
  //       }
  //     }
  //
  //     is ServiceRequest -> {
  //       when (request.status) {
  //         ServiceRequestStatus.DRAFT, ServiceRequestStatus.ONHOLD -> request.status =
  //           ServiceRequestStatus.ACTIVE
  //
  //         else -> {}
  //       }
  //     }
  //
  //     is CommunicationRequest -> {
  //       when (request.status) {
  //         CommunicationRequestStatus.DRAFT, CommunicationRequestStatus.ONHOLD -> request.status =
  //           CommunicationRequestStatus.ACTIVE
  //
  //         else -> {}
  //       }
  //     }
  //   }
  //   // else not a valid or supported request
  // }

  /** Change request status to accepted (or equivalent) as long as the status change is allowed */
  suspend fun acceptRequest(request: Resource) {
    when (request) {
      is Task -> updateTaskStatus(request, TaskStatus.ACCEPTED)
      is MedicationRequest -> TODO("Implement updateMedicationRequestStatus()")
      is ServiceRequest -> TODO("Implement updateServiceRequestStatus()")
      is CommunicationRequest -> TODO("Implement updateCommunicationRequestStatus()")
      else -> {}
    }
  }

  /** Cancel/revoke request */
  fun revokeRequest(request: Resource) {
    // assumes that a request could be cancelled at any stage
    when (request) {
      is Task -> request.status = TaskStatus.CANCELLED
      is MedicationRequest -> request.status =
        MedicationRequestStatus.CANCELLED

      is ServiceRequest -> request.status = ServiceRequestStatus.REVOKED
      is CommunicationRequest -> request.status =
        CommunicationRequestStatus.REVOKED

      else -> TODO("Not a valid request")
    }
    // else not a valid or supported request
  }

  /** Start work on request */
  fun setRequestToActive(request: Resource) {
    when (request) {
      is Task -> {
        when (request.status) {
          TaskStatus.DRAFT, TaskStatus.ONHOLD -> request.status = TaskStatus.INPROGRESS
          else -> {}
        }
      }

      is MedicationRequest -> {
        when (request.status) {
          MedicationRequestStatus.DRAFT,
          MedicationRequestStatus.ONHOLD,
          -> request.status = MedicationRequestStatus.ACTIVE

          else -> {}
        }
      }

      is ServiceRequest -> {
        when (request.status) {
          ServiceRequestStatus.DRAFT, ServiceRequestStatus.ONHOLD -> request.status =
            ServiceRequestStatus.ACTIVE

          else -> {}
        }
      }

      is CommunicationRequest -> {
        when (request.status) {
          CommunicationRequestStatus.DRAFT, CommunicationRequestStatus.ONHOLD -> request.status =
            CommunicationRequestStatus.ACTIVE

          else -> {}
        }
      }
    }
    // else not a valid or supported request
  }

  /** Mark request as complete */
  fun completeRequest(request: Resource) {
    when (request) {
      is Task -> request.status = TaskStatus.COMPLETED
      is MedicationRequest -> request.status = MedicationRequestStatus.COMPLETED
      is ServiceRequest -> request.status = ServiceRequestStatus.REVOKED
      is CommunicationRequest -> request.status =
        CommunicationRequestStatus.COMPLETED

      else -> TODO("Not a valid request")
    }
  }

  companion object {
    enum class SupportedRequestResources(val value: RequestResourceType) {
      TASK(RequestResourceType.TASK),
      MEDICATIONREQUEST(RequestResourceType.MEDICATIONREQUEST),
      SERVICEREQUEST(RequestResourceType.SERVICEREQUEST),
      // COMMUNICATIONREQUEST(RequestResourceType.COMMUNICATIONREQUEST)
    }
  }
  // }
}