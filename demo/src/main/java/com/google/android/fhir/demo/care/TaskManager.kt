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

import com.google.android.fhir.search.Search
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.Task

/** We could also have APIs like getCompletedTasks, getReadyTasks, getTasksWithStatus */
interface TaskManager {
  suspend fun createTasks(resourceList: List<Resource>): List<Task>

  /**
   * Returns [Questionnaire] resource given [Task]'s logicalId. Reason is currently the resource
   * being manipulated by this task is a Questionnaire and stored in task.focus. Consider returning
   * questionnaireId only ?
   */
  suspend fun fetchQuestionnaireFromTaskLogicalId(taskResourceId: String): Questionnaire?
  suspend fun getTasksForPatient(
    patientId: String,
    extraFilter: (Search.() -> Unit)? = null
  ): List<Task>
  suspend fun updateTaskStatus(
    task: Task,
    status: Task.TaskStatus,
    updateCarePlanStatus: Boolean = true
  )
  suspend fun getTasksCount(patientId: String, extraFilter: (Search.() -> Unit)? = null): Int
}
