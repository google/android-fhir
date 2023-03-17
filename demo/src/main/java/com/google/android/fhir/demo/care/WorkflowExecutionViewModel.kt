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
import com.google.android.fhir.logicalId
import com.google.android.fhir.search.search
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.Task
import timber.log.Timber

class WorkflowExecutionViewModel(application: Application) : AndroidViewModel(application) {
  private val _workflowExecutionState = MutableSharedFlow<WorkflowExecutionStatus>()
  val workflowExecutionState: Flow<WorkflowExecutionStatus>
    get() = _workflowExecutionState
  var totalWorkflowToBeExecuted = AtomicInteger(0)
  var completedWorkflows = AtomicInteger(0)

  private val fhirEngine =
    FhirApplication.fhirEngine(getApplication<Application>().applicationContext)
  private val carePlanManager =
    FhirApplication.carePlanManager(getApplication<Application>().applicationContext)
  private val taskManager =
    FhirApplication.taskManager(getApplication<Application>().applicationContext)

  /** Map of [Patient.logicalId] to a shared flow. */
  private val patientMapFlow = mutableMapOf<String, MutableSharedFlow<Patient>>()

  /** Gets all patients in database */
  fun applyWorkflowForAll() {
    viewModelScope.launch(Dispatchers.Default) {
      fhirEngine.search<Patient> {}.take(2).forEach { executeWorkflowForPatient(it) }
    }
  }

  /**
   * For each patient a new coroutine in [Dispatchers.IO] pool is responsible for executing workflow
   * sequentially. Using [patientMapFlow] it ensures the workflows are executed sequentially on same
   * patients. This can be invoked whenever there is a patient that is synced, created, updated,
   * etc.
   */
  fun executeWorkflowForPatient(patient: Patient) {
    if (patient.logicalId !in patientMapFlow) {
      // SharedFlow with bufferCapacity > 0 since emitting and collection are in separate coroutines
      patientMapFlow[patient.logicalId] = MutableSharedFlow(replay = 0, extraBufferCapacity = 0)
      viewModelScope.launch(Dispatchers.IO) {
        patientMapFlow[patient.logicalId]!!.collect {
          _workflowExecutionState.emit(
            WorkflowExecutionStatus.Started(totalWorkflowToBeExecuted.incrementAndGet())
          )
          carePlanManager.generateCarePlanForPatient(patient)
          _workflowExecutionState.emit(
            WorkflowExecutionStatus.Finished(
              completedWorkflows.incrementAndGet(),
              totalWorkflowToBeExecuted.get()
            )
          )
        }
      }
    }
    viewModelScope.launch {
      Timber.i("emitting patient ${patient.id}")
      patientMapFlow[patient.logicalId]!!.emit(patient)
    }
  }

  /**
   * Updating task statuses should be done in scope of WorkflowExecutionViewModel under activity
   * context. Reasons:-
   * 1.
   * 2. More importantly re-triggering of [PlanDefinition].apply will be done here.
   */
  fun updateTaskStatus(
    taskLogicalId: String,
    taskStatus: Task.TaskStatus,
    updateCarePlan: Boolean
  ) {
    viewModelScope.launch {
      val task = fhirEngine.get(ResourceType.Task, taskLogicalId) as Task
      taskManager.updateTaskStatus(task, taskStatus, updateCarePlan)
      executeWorkflowForPatient(
        fhirEngine.get(ResourceType.Patient, task.`for`.reference.substring("Patient/".length))
          as Patient
      )
    }
  }
}
