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
package com.google.android.fhir.configurablecare

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.configurablecare.care.TaskManager
import com.google.android.fhir.configurablecare.util.TransformSupportServicesMatchBox
import com.google.android.fhir.datacapture.mapping.ResourceMapper
import com.google.android.fhir.datacapture.mapping.StructureMapExtractionContext
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseValidator
import com.google.android.fhir.get
import com.google.android.fhir.search.search
import com.google.android.fhir.testing.jsonParser
import java.io.File
import java.time.Instant
import java.time.Period
import java.util.Date
import java.util.UUID
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Immunization
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceFactory
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.StructureDefinition
import org.hl7.fhir.r4.model.StructureMap
import org.hl7.fhir.r4.model.Task
import org.hl7.fhir.r4.terminologies.ConceptMapEngine
import org.hl7.fhir.r4.utils.StructureMapUtilities


/** ViewModel for patient registration screen {@link AddPatientFragment}. */
class AddPatientViewModel(application: Application, private val state: SavedStateHandle) :
  AndroidViewModel(application) {

  var questionnaire: String = ""
    // get() = getQuestionnaireJson()
    var savedPatient = MutableLiveData<Patient?>()
  var structureMapId: String = ""
  var currentTargetResourceType: String = ""
  val context = application.applicationContext

  private val questionnaireResource: Questionnaire
    get() =
      FhirContext.forCached(FhirVersionEnum.R4).newJsonParser().parseResource(questionnaire)
        as Questionnaire
  private var fhirEngine: FhirEngine = FhirApplication.fhirEngine(application.applicationContext)
  // private var questionnaireJson: String? = null

  /**
   * Saves patient registration questionnaire response into the application database.
   *
   * @param questionnaireResponse patient registration questionnaire response
   */
  fun savePatient(questionnaireResponse: QuestionnaireResponse) {
    viewModelScope.launch {
      if (QuestionnaireResponseValidator.validateQuestionnaireResponse(
          questionnaireResource,
          questionnaireResponse,
          getApplication()
        )
          .values
          .flatten()
          .any { it is Invalid }
      ) {
        savedPatient.value = null
        return@launch
      }

      if (structureMapId.isEmpty()) {  // no structure map needed
        println(" Structure map is empty")
        val bundle = ResourceMapper.extract(questionnaireResource, questionnaireResponse)
        var flag = false
        var patient: Patient

        for (entry in bundle.entry) {
          if (entry.resource is Patient) {
            patient = entry.resource as Patient
            patient.id = generateUuid()
            fhirEngine.create(patient)
            savedPatient.value = patient

            flag = true
          }
        }
        if (!flag)
          return@launch

      } else {
        println(" Structure map is: $structureMapId")
        println(" Target resource: $currentTargetResourceType")
        val outputFile =
          File(getApplication<Application>().externalCacheDir, "questionnaireResponse.json")
        outputFile.writeText(
          FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
            .encodeResourceToString(questionnaireResponse)
        )

        val contextR4 =
          FhirApplication.contextR4(getApplication<FhirApplication>().applicationContext)
        if (contextR4 == null) {
          savedPatient.value = null
          println("**** contextR4 not created yet")
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
        val targetResource: Base =
          if (currentTargetResourceType == "Patient")
            Patient()
          else
            Bundle()

        val baseElement =
          jsonParser.parseResource(
            QuestionnaireResponse::class.java, jsonParser.encodeResourceToString(questionnaireResponse))

        println("QR: ${jsonParser.encodeResourceToString(baseElement)}")

        structureMapUtilities.transform(contextR4, baseElement, structureMap, targetResource)

        if (targetResource is Bundle) {
          if (!targetResource.hasEntry()) {
            savedPatient.value = null
            return@launch
          }
        }

        if (targetResource is Bundle) {
          var flag = false
          var patient = Patient()
          val outputFile = File(getApplication<Application>().externalCacheDir, "bundle.json")
          outputFile.writeText(
            FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
              .encodeResourceToString(targetResource)
          )

          targetResource.entry.forEach { bundleEntryComponent ->
            val resource = bundleEntryComponent.resource
            if (resource is Observation && resource.effective is DateType) {
              resource.effective = null
            }
            resource.id = UUID.randomUUID().toString()
            fhirEngine.create(resource)
            if (resource is Patient) {
              createImmunizationReviewTask(resource.id)
              flag = true
              patient = resource
            }
          }
          if (flag) {
            savedPatient.value = patient
          }
          else {
            savedPatient.value = null
          }
        } else if (targetResource is Resource) {
          targetResource.id = UUID.randomUUID().toString()
          println("Patient: ${jsonParser.encodeResourceToString(targetResource)}")
          fhirEngine.create(targetResource)

          questionnaireResponse.id = UUID.randomUUID().toString()
          questionnaireResponse.subject = Reference("${targetResource.resourceType}/${IdType(targetResource.id).idPart}")
          println("QR: ${jsonParser.encodeResourceToString(questionnaireResponse)}")
          fhirEngine.create(questionnaireResponse)

          createImmunizationReviewTask(targetResource.id)

          savedPatient.value = if (targetResource is Patient) targetResource else null
        }
      }

    }
  }

  private suspend fun createImmunizationReviewTask(patientId: String) {
    val task = Task().apply {
      id = UUID.randomUUID().toString()
      status = Task.TaskStatus.DRAFT
      intent = Task.TaskIntent.PROPOSAL
      description = "Immunization Review"
      focus.reference = "Questionnaire/IMMZD1ClientHistoryMeasles"
      `for`.reference ="Patient/${IdType(patientId).idPart}"
      restriction.period.end = Date.from(Instant.now().plus(Period.ofDays(180)))
    }
    fhirEngine.create(task)

  }

  private fun readFileFromAssets(filename: String): String {
    return getApplication<Application>().assets.open(filename).bufferedReader().use {
      it.readText()
    }
  }

  private fun generateUuid(): String {
    return UUID.randomUUID().toString()
  }
}
