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

import com.google.android.fhir.FhirEngine
import com.google.android.fhir.search.Search
import com.google.android.fhir.search.search
import org.hl7.fhir.r4.model.CarePlan
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.Task

class TaskManager(private val fhirEngine: FhirEngine) {
  suspend fun createTasks(resourceList: List<Resource>): List<Task> {
    val taskList = ArrayList<Task>()
    for (resource in resourceList) {
      if (resource is Task) {
        val task = resource
        task.id = null // purge any temporary id that might exist
        task.status = Task.TaskStatus.READY
        task.basedOn = null
        task.intent = Task.TaskIntent.ORDER

        fhirEngine.create(task)
        taskList.add(task)
      }
    }
    return taskList
  }

  suspend fun fetchQuestionnaireFromTask(task: Task): Questionnaire? {
    val questionnaires =
      fhirEngine.search<Questionnaire> {
        // task.focus.reference = "Questionnaire/<ID>"
        filter(
          Questionnaire.IDENTIFIER,
          { value = of(task.focus.reference.substring("Questionnaire/".length)) }
        )
      }
    return questionnaires.firstOrNull()
  }

  suspend fun getTasksForPatient(patientId: String, extraFilter: Search.() -> Unit): List<Task> {
    return fhirEngine.search<Task> {
      filter(Task.SUBJECT, { value = "Patient/$patientId" })
      extraFilter()
    }
  }

  suspend fun updateTaskStatus(
    task: Task,
    status: Task.TaskStatus,
    updateCarePlanStatus: Boolean = true
  ) {
    task.status = status
    fhirEngine.update(task)
    if (updateCarePlanStatus) {
      val carePlan =
        fhirEngine.get(ResourceType.CarePlan, task.basedOnFirstRep.reference.substring(9))
          as CarePlan
      for (activity in carePlan.activity) {
        if (activity.reference.reference.equals(task.id)) {
          activity.detail.status = CarePlanManager.mapTaskStatusToCarePlanStatus(status)
          fhirEngine.update(carePlan)
          break
        }
      }
    }
  }

  companion object {
    fun getTaskName(task: Task): String {
      return task.identifier[0].value
    }
  }
}
