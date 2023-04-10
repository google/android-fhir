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

package com.google.android.fhir.demo.screening

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.datacapture.mapping.ResourceMapper
import com.google.android.fhir.demo.FhirApplication
import com.google.android.fhir.demo.care.ConfigurationManager.getServiceRequestConfigMap
import com.google.android.fhir.workflow.TaskManager
import java.time.Instant
import java.util.Date
import java.util.UUID
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ServiceRequest
import org.hl7.fhir.r4.model.Task

/** ViewModel for screener questionnaire screen {@link ScreenerEncounterFragment}. */
class ScreenerViewModel(application: Application, private val state: SavedStateHandle) :
  AndroidViewModel(application) {
  lateinit var questionnaireString: String
  private val questionnaireResource: Questionnaire
    get() =
      FhirContext.forCached(FhirVersionEnum.R4).newJsonParser().parseResource(questionnaireString)
        as Questionnaire
  private var fhirEngine: FhirEngine = FhirApplication.fhirEngine(application.applicationContext)
  val isResourcesSaved = MutableLiveData<Boolean>()

  /**
   * Saves screener encounter questionnaire response into the application database.
   *
   * @param questionnaireResponse screener encounter questionnaire response
   */
  fun saveScreenerEncounter(questionnaireResponse: QuestionnaireResponse, patientId: String) {
    viewModelScope.launch {
      val bundle = ResourceMapper.extract(questionnaireResource, questionnaireResponse)
      val subjectReference = Reference("Patient/$patientId")
      val encounterId = generateUuid()
      if (isRequiredFieldMissing(bundle)) {
        isResourcesSaved.value = false
        return@launch
      }
      saveResources(bundle, subjectReference, encounterId)
      isResourcesSaved.value = true
    }
  }

  private suspend fun saveResources(
    bundle: Bundle,
    subjectReference: Reference,
    encounterId: String
  ) {
    val encounterReference = Reference("Encounter/$encounterId")
    bundle.entry.forEach {
      when (val resource = it.resource) {
        is Observation -> {
          if (resource.hasCode()) {
            resource.id = generateUuid()
            resource.subject = subjectReference
            resource.encounter = encounterReference
            saveResourceToDatabase(resource)
          }
        }
        is Condition -> {
          if (resource.hasCode()) {
            resource.id = generateUuid()
            resource.subject = subjectReference
            resource.encounter = encounterReference
            saveResourceToDatabase(resource)
          }
        }
        is Encounter -> {
          resource.subject = subjectReference
          resource.id = encounterId
          saveResourceToDatabase(resource)
        }
        is ServiceRequest -> {
          if (resource.hasPerformerType()) { // Empty ServiceRequest --> does not need to be saved
            resource.subject = subjectReference
            resource.status = ServiceRequest.ServiceRequestStatus.ACTIVE

            // TODO: Use a different field in ServiceRequest to capture owner details
            if (resource.category.isNotEmpty()) {
              resource.requester.reference = resource.category.first().coding.first().code
              resource.requester.display = resource.category.first().coding.first().display
            } else resource.requester = null

            // Create a tracking task
            // Create UUID for linking the tracking Task to this resource
            val serviceRequestId = generateUuid()
            resource.id = serviceRequestId
            saveResourceToDatabase(resource)
            val task =
              createTrackingTaskForServiceRequest(resource, serviceRequestId, subjectReference)
            saveResourceToDatabase(task)
          }
        }
      }
    }
  }

  private fun createTrackingTaskForServiceRequest(
    serviceRequest: ServiceRequest,
    serviceRequestId: String,
    subjectReference: Reference
  ): Task {
    val task = Task()
    task.owner = serviceRequest.requester
    task.description = "[Tracking Task] " + questionnaireResource.description
    task.`for` = subjectReference
    task.focus = Reference("ServiceRequest/$serviceRequestId")
    task.status = Task.TaskStatus.READY
    task.intent = Task.TaskIntent.ORDER
    task.lastModified = Date.from(Instant.now())

    val serviceRequestFieldMap = getServiceRequestConfigMap()
    task.restriction.period.end =
      TaskManager.setDeadline(serviceRequestFieldMap["maxDuration"], serviceRequestFieldMap["unit"])
    return task
  }

  private fun isRequiredFieldMissing(bundle: Bundle): Boolean {
    bundle.entry.forEach {
      when (val resource = it.resource) {
        is Observation -> {
          if (resource.hasValueQuantity() && !resource.valueQuantity.hasValueElement()) {
            return true
          }
        }
      // TODO check other resources inputs
      }
    }
    return false
  }

  private suspend fun saveResourceToDatabase(resource: Resource) {
    fhirEngine.create(resource)
  }

  private fun generateUuid(): String {
    return UUID.randomUUID().toString()
  }
}
