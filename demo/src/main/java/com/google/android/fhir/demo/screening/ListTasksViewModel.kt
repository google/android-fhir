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
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.demo.FhirApplication
import com.google.android.fhir.search.Order
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Task

class ListScreeningsViewModel(application: Application) : AndroidViewModel(application) {

  private val iParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
  private val taskManager =
    FhirApplication.taskManager(getApplication<Application>().applicationContext)

  val liveSearchedTasks = MutableLiveData<List<TaskItem>>()

  fun getTasksForPatient(patientId: String, taskStatus: String) {
    viewModelScope.launch {
      liveSearchedTasks.value =
        taskManager
          .getTasksForPatient(
            patientId = patientId,
            extraFilter = { filter(Task.STATUS, { value = of(taskStatus) }) },
            sort = { sort(Task.MODIFIED, Order.ASCENDING) }
          )
          .mapIndexed { index, fhirTask -> fhirTask.toTaskItem(index + 1) }
    }
  }

  /**
   * Alternatively we could cache the questionnaireStrings for each tasks in this viewModel instead
   * of [runBlocking]
   */
  fun fetchQuestionnaireString(taskItem: TaskItem): String = runBlocking {
    iParser.encodeResourceToString(
      taskManager.fetchQuestionnaireFromTaskLogicalId(taskItem.resourceId)
    )
  }

  data class TaskItem(
    val id: String,
    // for Task/123/... this should be 123
    val resourceId: String,
    val description: String,
    val status: String,
    val intent: String,
    val dueDate: String,
    val completedDate: String,
    val owner: String
  ) {
    override fun toString() = description
  }
}

internal fun Task.toTaskItem(position: Int): ListScreeningsViewModel.TaskItem {
  val taskResourceId = if (hasIdElement()) idElement.idPart else ""
  val description = if (hasDescription()) description else "Sample Task Name"
  // status and intent are always present
  val taskStatus = status.toCode()
  val taskIntent = intent.toCode()
  val dueDate =
    if (hasRestriction() && restriction.hasPeriod()) restriction.period.end.toString()
    else "Sample End Date"
  val completedDate = if (hasLastModified()) lastModified.toString() else dueDate
  val owner = if (owner.hasDisplay()) owner.display else owner.reference

  return ListScreeningsViewModel.TaskItem(
    id = position.toString(),
    resourceId = taskResourceId,
    description = description,
    status = taskStatus,
    intent = taskIntent,
    dueDate = dueDate,
    completedDate = completedDate,
    owner = owner
  )
}
