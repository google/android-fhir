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

package com.google.android.fhir.demo.care

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.demo.FhirApplication
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.Task
import timber.log.Timber

/**
 * TODO(mjajoo@): Move this implementation to background in CarePlanManager(?) using
 * BlockingQueue(?) and WorkManager. Earlier I thought that PlanDefinition should be running while
 * the application is active which is not true.
 */
class CareWorkflowExecutionViewModel(application: Application) : AndroidViewModel(application) {
  private val _careWorkflowExecutionState = MutableSharedFlow<CareWorkflowExecutionStatus>()
  val careWorkflowExecutionState: Flow<CareWorkflowExecutionStatus>
    get() = _careWorkflowExecutionState
  private var totalPlanDefinitionsToApply = AtomicInteger(0)
  private var totalPlanDefinitionsApplied = AtomicInteger(0)

  private val fhirEngine =
    FhirApplication.fhirEngine(getApplication<Application>().applicationContext)
  private val carePlanManager =
    FhirApplication.carePlanManager(getApplication<Application>().applicationContext)
  private val taskManager =
    FhirApplication.taskManager(getApplication<Application>().applicationContext)
  private val careConfiguration = ConfigurationManager.getCareConfiguration()

  /**
   * Shared flow of [CareWorkflowExecutionRequest]. For each collected patient the execution shall
   * run in blocking mode as asynchronous execution is resource exhaustive. extraBufferCapacity > 0
   * so that pending executions are collected properly.
   */
  private val patientFlowForCareWorkflowExecution =
    MutableSharedFlow<CareWorkflowExecutionRequest>(extraBufferCapacity = 5)

  /**
   * [patientFlowForCareWorkflowExecution] collects each patient in a coroutine and executes
   * workflow blocking. This can be invoked when there is an operation on a Patient or some Task is
   * updated. TODO(mjajoo@): Add configuration to support for WHICH EVENTS this should be invoked.
   */
  fun executeCareWorkflowForPatient(patient: Patient) {
    viewModelScope.launch(Dispatchers.IO) {
      patientFlowForCareWorkflowExecution.collect { careWorkflowExecutionRequest ->
        /**
         * runBlocking because we want to run care workflows sequentially to avoid resource
         * exhaustion.
         */
        runBlocking {
          careConfiguration.supportedImplementationGuides
            .map { it.implementationGuideConfig.entryPoint.substring("PlanDefinition/".length) }
            .forEach {
              _careWorkflowExecutionState.emit(
                CareWorkflowExecutionStatus.Started(totalPlanDefinitionsToApply.incrementAndGet())
              )
              carePlanManager.applyPlanDefinitionOnPatient(careWorkflowExecutionRequest.patient, it)
              _careWorkflowExecutionState.emit(
                CareWorkflowExecutionStatus.Finished(
                  totalPlanDefinitionsApplied.incrementAndGet(),
                  totalPlanDefinitionsToApply.get()
                )
              )
            }
        }
      }
    }
    viewModelScope.launch {
      Timber.i("emitting patient ${patient.id}")
      patientFlowForCareWorkflowExecution.emit(CareWorkflowExecutionRequest(patient))
    }
  }

  /**
   * Updating task statuses should be done in scope of [CareWorkflowExecutionViewModel] under
   * activity context. Also re-triggering of [PlanDefinition].apply is done here by fetching the
   * [Patient] from FhirEngine. Update: Updating tasks could also happen in background!
   */
  fun updateTaskStatus(
    taskLogicalId: String,
    taskStatus: Task.TaskStatus,
    updateCarePlan: Boolean
  ) {
    viewModelScope.launch {
      val task = fhirEngine.get(ResourceType.Task, taskLogicalId) as Task
      taskManager.updateTaskStatus(task, taskStatus, updateCarePlan)
      executeCareWorkflowForPatient(
        fhirEngine.get(ResourceType.Patient, task.`for`.reference.substring("Patient/".length))
          as Patient
      )
    }
  }
}
