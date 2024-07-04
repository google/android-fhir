/*
 * Copyright 2024 Google LLC
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

package com.google.android.fhir.workflow.activity

import ca.uhn.fhir.model.api.IQueryParameterType
import ca.uhn.fhir.rest.param.ReferenceParam
import com.google.android.fhir.logicalId
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.CommunicationRequest
import org.hl7.fhir.r4.model.MedicationRequest
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ServiceRequest
import org.hl7.fhir.r4.model.Task
import org.opencds.cqf.fhir.api.Repository

class ActivityFlow3<R : Resource>(private val repository: Repository, val requestResource: R) :
  StartPlan<R>, EndPlan<R>, StartOrder<R>, EndOrder<R>, StartPerform<R>, EndPerform<R> {

  enum class RequestIntent {
    PROPOSAL,
    PLAN,
    ORDER,
    PERFORM,
    UNKNOWN,
  }

  override suspend fun startPlan(init: R.() -> Unit): EndPlan<R> {
    requestResource.init()
    return ActivityFlow3(repository, PlanFlow.start(repository, requestResource) as R)
  }

  override suspend fun endPlan(init: R.() -> Unit): StartOrder<R> {
    requestResource.init()
    PlanFlow.end(repository, requestResource)
    return this
  }

  override suspend fun startOrder(init: R.() -> Unit): EndOrder<R> {
    requestResource.init()
    return ActivityFlow3(repository, OrderFlow.start(repository, requestResource) as R)
  }

  override suspend fun endOrder(init: R.() -> Unit): StartPerform<R> {
    requestResource.init()
    OrderFlow.end(repository, requestResource)
    return this
  }

  override suspend fun <E : Resource> startPerform(
    klass: Class<E>,
    init: R.() -> Unit,
  ): EndPerform<E> {
    requestResource.init()
    return ActivityFlow3(repository, PerformFlow(klass).start(repository, requestResource) as E)
  }

  override suspend fun endPerform(init: R.() -> Unit) {
    requestResource.init()
    PerformFlow(requestResource::class.java).end(repository, requestResource)
  }

  fun currentIntent(): RequestIntent {
    // This could be a request or event.
    val inputProposal = Request(requestResource)
    return when (inputProposal.intent) {
      Request.Intent.PROPOSAL -> RequestIntent.PROPOSAL
      Request.Intent.PLAN -> RequestIntent.PLAN
      Request.Intent.ORDER -> {
        if (inputProposal.status == Request.Status.COMPLETED) {
          RequestIntent.PERFORM
        } else {
          RequestIntent.ORDER
        }
      }
      else -> RequestIntent.UNKNOWN
    }
  }

  companion object {
    fun of(repository: Repository, resource: Task) =
      (ActivityFlow3(repository, resource) as StartPlan<Task>)

    fun of(repository: Repository, resource: MedicationRequest) =
      (ActivityFlow3(repository, resource) as StartPlan<MedicationRequest>)

    fun of(repository: Repository, resource: CommunicationRequest) =
      (ActivityFlow3(repository, resource) as StartPlan<CommunicationRequest>)

    fun of(repository: Repository, resource: ServiceRequest) =
      (ActivityFlow3(repository, resource) as StartPlan<ServiceRequest>)

    fun of(
      repository: Repository,
      patientId: String,
    ): List<ActivityFlow3<Resource>> {
      val tasks =
        repository
          .search(
            Bundle::class.java,
            Task::class.java,
            mutableMapOf<String, MutableList<IQueryParameterType>>(
              Task.SUBJECT.paramName to mutableListOf(ReferenceParam("Patient/$patientId")),
            ),
            null,
          )
          .entry
          .map { it.resource }

      val medicationRequests =
        repository
          .search(
            Bundle::class.java,
            MedicationRequest::class.java,
            mutableMapOf<String, MutableList<IQueryParameterType>>(
              MedicationRequest.SUBJECT.paramName to
                mutableListOf(ReferenceParam("Patient/$patientId")),
            ),
            null,
          )
          .entry
          .map { it.resource }

      val communicationRequests =
        repository
          .search(
            Bundle::class.java,
            CommunicationRequest::class.java,
            mutableMapOf<String, MutableList<IQueryParameterType>>(
              CommunicationRequest.SUBJECT.paramName to
                mutableListOf(ReferenceParam("Patient/$patientId")),
            ),
            null,
          )
          .entry
          .map { it.resource }

      val serviceRequests =
        repository
          .search(
            Bundle::class.java,
            ServiceRequest::class.java,
            mutableMapOf<String, MutableList<IQueryParameterType>>(
              ServiceRequest.SUBJECT.paramName to
                mutableListOf(ReferenceParam("Patient/$patientId")),
            ),
            null,
          )
          .entry
          .map { it.resource }

      val cache: MutableMap<String, Request<Resource>> =
        sequenceOf(tasks, communicationRequests, serviceRequests, medicationRequests)
          .flatten()
          .map { Request<Resource>(it) }
          .associateByTo(LinkedHashMap()) { "${it.resource.resourceType}/${it.resource.logicalId}" }

      fun addBasedOn(request: RequestChain<Resource>): RequestChain<Resource>? {
        request.request.getBasedOn()?.let { reference ->
          cache[reference.reference]?.let {
            cache.remove(reference.reference)
            request.basedOn = RequestChain(it, addBasedOn(RequestChain(it, null)))
            request.basedOn
          }
        }
        return null
      }

      val requestChain =
        cache.values
          .filter {
            it.intent == Request.Intent.ORDER ||
              it.intent == Request.Intent.PLAN ||
              it.intent == Request.Intent.PROPOSAL
          }
          .sortedByDescending { it.intent.ordinal }
          .mapNotNull {
            if (cache.containsKey("${it.resource.resourceType}/${it.resource.logicalId}")) {
              RequestChain(it, addBasedOn(RequestChain(it, null)))
            } else {
              null
            }
          }

      return requestChain.map { ActivityFlow3(repository, it.request.resource) }
    }
  }
}

internal data class RequestChain<R : Resource>(
  val request: Request<R>,
  var basedOn: RequestChain<R>?,
)
