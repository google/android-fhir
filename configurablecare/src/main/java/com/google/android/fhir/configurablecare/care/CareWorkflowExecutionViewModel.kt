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

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.configurablecare.FhirApplication
import com.google.android.fhir.configurablecare.screening.ListScreeningsViewModel
import com.google.android.fhir.get
import com.google.android.fhir.search.search
import com.google.android.fhir.testing.jsonParser
import java.time.Instant
import java.util.Date
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.MedicationRequest
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Task

/** TODO(mjajoo@): Explore if applying PlanDefinition could be done in background. */
class CareWorkflowExecutionViewModel(application: Application) : AndroidViewModel(application) {
  private val fhirEngine =
    FhirApplication.fhirEngine(getApplication<Application>().applicationContext)
  private val carePlanManager =
    FhirApplication.carePlanManager(getApplication<Application>().applicationContext)
  private val requestManager = FhirApplication.requestManager(getApplication<Application>().applicationContext)
  private lateinit var activeRequestConfiguration: List<RequestConfiguration>
  lateinit var currentPlanDefinitionId: String
  lateinit var currentIg: String
  var currentStructureMapId: String = ""
  var currentTargetResourceType: String = ""
  lateinit var currentQuestionnaireId: String
  lateinit var selectedRequestItem: ListScreeningsViewModel.TaskItem

  /**
   * Shared flow of [CareWorkflowExecutionRequest]. For each collected patient the execution shall
   * run in blocking mode as asynchronous execution is resource exhaustive. extraBufferCapacity > 0
   * so that pending executions are collected properly.
   */
  // Having a map of patients is better ? Check TVPF.updateWorkflowExecutionBar
  // replay to 5 is not good since for multiple patients will be in queue. map makes lot more sense
  val patientFlowForCareWorkflowExecution =
    MutableSharedFlow<CareWorkflowExecutionRequest>(replay = 5)
  private var totalPlanDefinitionsToApply = AtomicInteger(0)
  private var totalPlanDefinitionsApplied = AtomicInteger(0)

  init {
    /**
     * [patientFlowForCareWorkflowExecution] collects each patient in a coroutine and executes
     * workflow blocking. This can be invoked when there is an operation on a Patient or some Task
     * is updated.
     */
    viewModelScope.launch(Dispatchers.IO) {
      patientFlowForCareWorkflowExecution.collect { careWorkflowExecutionRequest ->
        if (careWorkflowExecutionRequest.careWorkflowExecutionStatus
            is CareWorkflowExecutionStatus.Finished
        )
          return@collect
        /**
         * runBlocking because we want to run care workflows sequentially to avoid resource
         * exhaustion.
         */
        runBlocking {
          // carePlanManager.smartIgTest()
          if (currentPlanDefinitionId != "") {
            if (currentPlanDefinitionId.contains("CreateImmunizationRecord")) {
              // println("About to create Immunization record")
              // val patientId = IdType(careWorkflowExecutionRequest.patient.id).idPart
              // val medicationRequest = requestManager.getRequestsForPatient(
              //   patientId,
              //   ResourceType.MedicationRequest,
              //   status = "draft", // "active",
              //   intent = "order"
              // ).first() as MedicationRequest
              // requestManager.endOrder(medicationRequest, MedicationRequest.MedicationRequestStatus.COMPLETED, "Completed successfully")

              // var vaccineCoding: Coding? = null
              // if (medicationRequest.hasMedicationCodeableConcept() && medicationRequest.medicationCodeableConcept.hasCoding()) {
              //   vaccineCoding = medicationRequest.medicationCodeableConcept.codingFirstRep
              // }
              // val immunization = Immunization().apply {
              //   id = UUID.randomUUID().toString()
              //   vaccineCode.addCoding(vaccineCoding)
              //   patient = Reference("Patient/$patientId")
              //   status = Immunization.ImmunizationStatus.COMPLETED
              // }
              //
              // fhirEngine.create(immunization)
              // fhirEngine.update(medicationRequest)

              // println(jsonParser.encodeResourceToString(immunization))
              // println(jsonParser.encodeResourceToString(medicationRequest))
            }
            else {
              println("About to apply $currentPlanDefinitionId")
              carePlanManager.applyPlanDefinitionOnPatient(
                currentPlanDefinitionId,
                careWorkflowExecutionRequest.patient,
                getActiveRequestConfiguration()
              )
            }
          }
          else {
            // do nothing
          }
        }
        patientFlowForCareWorkflowExecution.emit(
          CareWorkflowExecutionRequest(
            careWorkflowExecutionRequest.patient,
            CareWorkflowExecutionStatus.Finished(
              totalPlanDefinitionsApplied.incrementAndGet(),
              totalPlanDefinitionsToApply.get()
            )
          )
        )
      }
    }
  }
  fun executeCareWorkflowForPatient(patient: Patient) {
    viewModelScope.launch {
      patientFlowForCareWorkflowExecution.emit(
        CareWorkflowExecutionRequest(
          patient,
          CareWorkflowExecutionStatus.Started(totalPlanDefinitionsToApply.incrementAndGet())
        )
      )
    }
  }
  /**
   * Updating task statuses should be done in scope of [CareWorkflowExecutionViewModel] under
   * activity context. Also re-triggering of [PlanDefinition].apply is done here by fetching the
   * [Patient] from FhirEngine. Update: Updating tasks could also happen in background!
   */
  fun updateTaskAndCarePlanStatus(
    taskLogicalId: String,
    taskStatus: Task.TaskStatus,
    encounterReferences: List<Reference>,
    updateCarePlan: Boolean,
  ) {
    viewModelScope.launch {

      // val resourceType: ResourceType = when (requestResourceType) {
      //   "Task" -> ResourceType.Task
      //   "MedicationRequest" -> ResourceType.MedicationRequest
      //   "ServiceRequest" -> ResourceType.ServiceRequest
      //   else -> ResourceType.Task
      // }

      // val request = fhirEngine.search<Task> { filter(
      //         Task.RES_ID,
      //         { taskLogicalId }
      //       )}
      val taskSearch = fhirEngine.search<Task> {
        filter(
          Task.RES_ID,
          { value = of(taskLogicalId) }
        )
      }

      val medicationRequestSearch = fhirEngine.search<MedicationRequest> {
        filter(
          MedicationRequest.RES_ID,
          { value = of(taskLogicalId) }
        )
      }

      val patient: Patient
      if (taskSearch.isNotEmpty()) {
        val task = taskSearch.first().resource
        patient =
          fhirEngine.get(task.`for`.reference.substring("Patient/".length))
        executeCareWorkflowForPatient(patient)

        task.status = Task.TaskStatus.COMPLETED
        task.lastModified = Date.from(Instant.now())
        task.meta.lastUpdated = Date.from(Instant.now())
        fhirEngine.update(task)
      }
      else if (medicationRequestSearch.isNotEmpty()) {
        val medicationRequest = medicationRequestSearch.first().resource
        patient =
          fhirEngine.get(medicationRequest.subject.reference.substring("Patient/".length))
        executeCareWorkflowForPatient(patient)

        // val nextAction = getNextActionForMedicationRequest(medicationRequest.intent.toCode())
        // println("intent: ${medicationRequest.intent} nextAction: $nextAction")
        // if (nextAction.isEmpty()) {
        //   requestManager.updateIntent(IdType(medicationRequest.id).idPart, ResourceType.MedicationRequest.toString())
        // }
        // else {
        //   medicationRequest.addSupportingInformation(Reference(nextAction))
        // }

        // medicationRequest.status = MedicationRequest.MedicationRequestStatus.COMPLETED
        // fhirEngine.update(medicationRequest)
      }
      else
        patient = Patient()

      // carePlanManager.updateCarePlanActivity(
      //   task,
      //   taskStatus.toString(),
      //   encounterReferences,
      //   updateCarePlan
      // )

      // executeCareWorkflowForPatient(patient)
    }
  }

  fun setActiveRequestConfiguration(planDefinitionId: String) {
    activeRequestConfiguration =
      ConfigurationManager.careConfiguration
        ?.supportedImplementationGuides
        ?.firstOrNull { it.implementationGuideConfig.entryPoint.contains(planDefinitionId) }
        ?.implementationGuideConfig
        ?.requestConfigurations!!
  }

  fun getActiveRequestConfiguration(): List<RequestConfiguration> {
    return activeRequestConfiguration
  }

  suspend fun getActivePatientRegistrationQuestionnaire(): String {
    currentQuestionnaireId = ConfigurationManager.careConfiguration
      ?.supportedImplementationGuides
      ?.firstOrNull { it.implementationGuideConfig.entryPoint.contains(currentIg) }
      ?.implementationGuideConfig
      ?.patientRegistrationQuestionnaire!!
    val questionnaire = fhirEngine.get<Questionnaire>(IdType(currentQuestionnaireId).idPart)
    return jsonParser.encodeResourceToString(questionnaire)
  }

  fun setCurrentStructureMap() {
    for (implementationGuide in ConfigurationManager.careConfiguration?.supportedImplementationGuides!!) {
      val triggers = implementationGuide.implementationGuideConfig.triggers
      for (trigger in triggers)
        if (trigger.event.contains(currentQuestionnaireId)) {
          currentStructureMapId = trigger.structureMap
          currentTargetResourceType = trigger.targetResourceType
          println("StructureMap: $currentStructureMapId :: Target resource type: $currentTargetResourceType")
        }
    }
  }

  fun setPlanDefinitionId(event: String) {
    for (implementationGuide in ConfigurationManager.careConfiguration?.supportedImplementationGuides!!) {
      val triggers = implementationGuide.implementationGuideConfig.triggers
      for (trigger in triggers)
        if (trigger.event == event)
          currentPlanDefinitionId = trigger.planDefinition
    }
  }
}
