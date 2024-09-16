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

package com.google.android.fhir.workflow.activity.resource.request

import com.google.android.fhir.logicalId
import com.google.android.fhir.workflow.activity.resource.request.CPGRequestResource.Companion.of
import org.hl7.fhir.r4.model.CommunicationRequest
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.MedicationRequest
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.ServiceRequest
import org.hl7.fhir.r4.model.Task

/**
 * This abstracts the
 * [CPG Request Resources](https://build.fhir.org/ig/HL7/cqf-recommendations/profiles.html#activity-profiles)
 * used in various activities. The various subclasses of [CPGRequestResource] act as a wrapper
 * around the resource they are derived from and helps with the abstracted properties defined for
 * each [CPGRequestResource]. e.g. [CPGCommunicationRequest] is a wrapper around the
 * [CommunicationRequest] and helps with its [Intent], [Status] and basedOn [Reference]s.
 *
 * Any direct update to the [resource] can be done by using [update] api.
 *
 * The application users may use appropriate [of] static factories to create the required
 * [CPGRequestResource]s.
 *
 * **NOTE**
 *
 * The [resource] must contain appropriate [Resource.meta.profile] for the [of] factories to create
 * appropriate [CPGRequestResource]s. e.g. Both [CPGMedicationRequest] and [CPGImmunizationRequest]
 * are derived from [MedicationRequest]. So the [MedicationRequest.meta.profile] is required to
 * create the appropriate [CPGRequestResource].
 */
sealed class CPGRequestResource<R>(internal open val resource: R) where R : Resource {

  val resourceType: ResourceType
    get() = resource.resourceType

  val logicalId: String
    get() = resource.logicalId

  internal abstract fun setIntent(intent: Intent)

  internal abstract fun getIntent(): Intent

  abstract fun setStatus(status: Status, reason: String? = null)

  abstract fun getStatus(): Status

  abstract fun setBasedOn(reference: Reference)

  abstract fun getBasedOn(): Reference?

  fun update(update: R.() -> Unit) {
    resource.update()
  }

  internal abstract fun copy(): CPGRequestResource<R>

  internal fun copy(id: String, status: Status, intent: Intent): CPGRequestResource<R> {
    val parent: CPGRequestResource<R> = this
    return copy().apply {
      resource.idElement = IdType.of(resource).setValue(id)
      setStatus(status)
      setIntent(intent)
      setBasedOn(Reference("${parent.resource.resourceType}/${parent.resource.logicalId}"))
    }
  }

  fun asReference() = Reference("${resource.resourceType}/${resource.logicalId}")

  companion object {

    fun <R : Resource> of(klass: CPGRequestResource<*>, resource: R): CPGRequestResource<R> {
      return when (klass::class.java) {
        CPGCommunicationRequest::class.java ->
          CPGCommunicationRequest(resource as CommunicationRequest)
        CPGMedicationRequest::class.java -> CPGMedicationRequest(resource as MedicationRequest)
        CPGImmunizationRequest::class.java -> CPGImmunizationRequest(resource as MedicationRequest)
        CPGServiceRequest::class.java -> CPGServiceRequest(resource as ServiceRequest)
        else -> {
          throw IllegalArgumentException("Unknown CPG Request type ${resource::class}.")
        }
      }
        as CPGRequestResource<R>
    }

    //    fun of(resource: Task) = CPGTaskRequest(resource)

    fun of(resource: MedicationRequest) =
      if (
        resource.meta.hasProfile(
          "http://hl7.org/fhir/uv/cpg/StructureDefinition/cpg-medicationrequest",
        )
      ) {
        CPGMedicationRequest(resource)
      } else if (
        resource.meta.hasProfile(
          "http://hl7.org/fhir/uv/cpg/StructureDefinition/cpg-immunizationrequest",
        )
      ) {
        CPGImmunizationRequest(resource)
      } else {
        throw IllegalArgumentException("Unknown cpg profile")
      }

    fun of(resource: ServiceRequest) = CPGServiceRequest(resource)

    fun of(resource: CommunicationRequest) = CPGCommunicationRequest(resource)

    /**
     * the resource.meta.profile describes the activity and should be used to create particular cpg
     * request
     */
    fun <R : Resource> of(resource: R): CPGRequestResource<R> {
      return when (resource) {
        is Task -> of(resource)
        is MedicationRequest -> of(resource)
        is ServiceRequest -> of(resource)
        is CommunicationRequest -> of(resource)
        else -> {
          throw IllegalArgumentException("Unknown CPG Request type ${resource::class}.")
        }
      }
        as CPGRequestResource<R>
    }
  }
}

/**
 * PROPOSAL, PLAN and ORDER are the only intents we are interested in. All the other Request Intent
 * values are represented by OTHER. See
 * [codesystem-request-intent](https://www.hl7.org/FHIR/codesystem-request-intent.html) for the list
 * of intents.
 */
internal sealed class Intent(val code: String?) {
  data object PROPOSAL : Intent("proposal")

  data object PLAN : Intent("plan")

  data object ORDER : Intent("order")

  class OTHER(code: String?) : Intent(code)

  companion object {
    fun of(code: String?): Intent {
      return when (code) {
        "proposal" -> PROPOSAL
        "plan" -> PLAN
        "order" -> ORDER
        else -> OTHER(code)
      }
    }
  }
}

/**
 * This may not represent all the Request Resource status. For the activity flow, we may ony be
 * interested in a few values and they should be represented here.
 */
enum class Status(val string: String) {
  DRAFT("draft"),
  ACTIVE("active"),
  ONHOLD("on-hold"),
  REVOKED("revoked"),
  COMPLETED("completed"),
  ENTEREDINERROR("entered-in-error"),
  UNKNOWN("unknown"),
  NULL("null"),
  ;

  companion object {
    fun of(code: String): Status {
      return when (code) {
        "draft" -> DRAFT
        "active" -> ACTIVE
        "on-hold" -> ONHOLD
        "revoked" -> REVOKED
        "completed" -> COMPLETED
        "entered-in-error" -> ENTEREDINERROR
        "unknown" -> UNKNOWN
        "null" -> NULL
        else -> NULL
      }
    }
  }
}
