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
package com.google.android.fhir.configurablecare.screening

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.datacapture.mapping.ResourceMapper
import com.google.android.fhir.configurablecare.FhirApplication
import com.google.android.fhir.configurablecare.care.RequestConfiguration
import com.google.android.fhir.configurablecare.care.TaskManager
import com.google.android.fhir.configurablecare.util.TransformSupportServicesMatchBox
import com.google.android.fhir.get
import com.google.android.fhir.testing.jsonParser
import java.io.File
import java.util.UUID
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ServiceRequest
import org.hl7.fhir.r4.model.StructureMap
import org.hl7.fhir.r4.utils.StructureMapUtilities

data class ScreenerState(
  val isResourceSaved: Boolean = false,
  val encountersCreated: List<Reference> = emptyList()
)

/** ViewModel for screener questionnaire screen {@link ScreenerEncounterFragment}. */
class ScreenerViewModel(application: Application, private val state: SavedStateHandle) :
  AndroidViewModel(application) {
  lateinit var questionnaireString: String
  private val questionnaireResource: Questionnaire
    get() =
      FhirContext.forCached(FhirVersionEnum.R4).newJsonParser().parseResource(questionnaireString)
        as Questionnaire
  var structureMapId: String = ""
  private var fhirEngine: FhirEngine = FhirApplication.fhirEngine(application.applicationContext)
  private val taskManager: TaskManager = FhirApplication.taskManager(application.applicationContext)
  private val _screenerState = MutableLiveData(ScreenerState())
  lateinit var requestConfiguration: List<RequestConfiguration>
  val screenerState: LiveData<ScreenerState>
    get() = _screenerState

  /**
   * Saves screener encounter questionnaire response into the application database.
   *
   * @param questionnaireResponse screener encounter questionnaire response
   */
  fun saveScreenerEncounter(questionnaireResponse: QuestionnaireResponse, patientId: String) {
    viewModelScope.launch {
      // val bundle = ResourceMapper.extract(questionnaireResource, questionnaireResponse)
      // val subjectReference = Reference("Patient/$patientId")
      // val encounterId = generateUuid()
      // if (isRequiredFieldMissing(bundle)) {
      //   _screenerState.value = ScreenerState(false, encountersCreated = emptyList())
      //   return@launch
      // }
      // saveResources(bundle, subjectReference, encounterId)
      // _screenerState.value =
      //   ScreenerState(
      //     isResourceSaved = true,
      //     encountersCreated = listOf(Reference("Encounter/$encounterId"))
      //   )

      if (structureMapId.isEmpty()) { // no structure map needed
        println(" Structure map is empty")

        val bundle = ResourceMapper.extract(questionnaireResource, questionnaireResponse)
        val subjectReference = Reference("Patient/$patientId")
        val encounterId = generateUuid()
        if (isRequiredFieldMissing(bundle)) {
          _screenerState.value = ScreenerState(false, encountersCreated = emptyList())
          return@launch
        }
        saveResources(bundle, subjectReference, encounterId)
        _screenerState.value =
          ScreenerState(
            isResourceSaved = true,
            encountersCreated = listOf(Reference("Encounter/$encounterId"))
          )

      } else {
        println(" Structure map is: $structureMapId")
        val outputFile =
          File(getApplication<Application>().externalCacheDir, "questionnaireResponse.json")
        outputFile.writeText(
          FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
            .encodeResourceToString(questionnaireResponse)
        )

        val contextR4 =
          FhirApplication.contextR4(getApplication<FhirApplication>().applicationContext)
        if (contextR4 == null) {
          println("**** contextR4 not created yet")
          _screenerState.value = null
          return@launch
        }

        val outputs = mutableListOf<Base>()
        val transformSupportServices =
          TransformSupportServicesMatchBox(
            contextR4,
            outputs
          )
        val structureMapUtilities = StructureMapUtilities(contextR4, transformSupportServices)

        val structureMap = fhirEngine.get<StructureMap>(IdType(structureMapId).idPart)
        val targetResource = Bundle()

        questionnaireResponse.id = UUID.randomUUID().toString()
        questionnaireResponse.subject = Reference("Patient/${IdType(patientId).idPart}")
        println("QR before SM extraction: ${jsonParser.encodeResourceToString(questionnaireResponse)}")


        // val qr = readFileFromAssets("smart-imm/ig/QuestionnaireResponse-Example.IMMZ.D1.QuestionnaireResponse.1.json")

        val baseElement =
          jsonParser.parseResource(
            QuestionnaireResponse::class.java, jsonParser.encodeResourceToString(questionnaireResponse))

        structureMapUtilities.transform(contextR4, baseElement, structureMap, targetResource)

        // if (targetResource is Bundle) {
        //   if (!targetResource.hasEntry()) {
        //     _screenerState.value = null
        //     return@launch
        //   }
        // }

        if (targetResource is Bundle) {
          // val outputFile = File(getApplication<Application>().externalCacheDir, "bundle.json")
          // outputFile.writeText(
          //   FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
          //     .encodeResourceToString(targetResource)
          // )

          var flag = false
          var savedReference = Reference()
          targetResource.entry.forEach { bundleEntryComponent ->
            val resource = bundleEntryComponent.resource
            if (resource is Observation && resource.effective is DateType) {
              resource.effective = null
            }
            fhirEngine.create(resource)
            flag = true
            savedReference = Reference("${resource.resourceType}/${IdType(resource.id).idPart}")
          }
          if (true /*flag*/) {
            fhirEngine.create(questionnaireResponse)

            _screenerState.value =
              ScreenerState(
                isResourceSaved = true,
                encountersCreated = listOf(savedReference)
              )
          } else {
            _screenerState.value = null
          }
        } else if (targetResource is Resource) {
          targetResource.id = UUID.randomUUID().toString()
          println("Resource: ${jsonParser.encodeResourceToString(targetResource)}")
          fhirEngine.create(targetResource)

          questionnaireResponse.id = UUID.randomUUID().toString()
          questionnaireResponse.subject = Reference("Patient/${IdType(patientId).idPart}")
          println("QR: ${jsonParser.encodeResourceToString(questionnaireResponse)}")
          fhirEngine.create(questionnaireResponse)

          _screenerState.value =
            ScreenerState(
              isResourceSaved = true,
              encountersCreated = listOf(Reference("${targetResource.resourceType}/${IdType(targetResource.id).idPart}"))
            )
        }
      }

    }
  }

  private fun readFileFromAssets(filename: String): String {
    return getApplication<Application>().assets.open(filename).bufferedReader().use {
      it.readText()
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
              taskManager.createTrackingTaskForServiceRequest(
                resource,
                subjectReference,
                questionnaireResource.description,
                requestConfiguration.firstOrNull { it.requestType == "ServiceRequest" }!!
              )
            saveResourceToDatabase(task)
          }
        }
      }
    }
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
