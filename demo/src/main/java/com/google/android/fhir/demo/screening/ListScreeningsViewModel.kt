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
import com.google.android.fhir.demo.FhirApplication
import com.google.android.fhir.logicalId
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.Task

class ListScreeningsViewModel(application: Application) : AndroidViewModel(application) {
  var taskPosition: Int = 0
  var taskList = MutableLiveData<List<Task>>()
  private var taskListMutable = ArrayList<Task>()

  val fhirEngine = FhirApplication.fhirEngine(application.applicationContext)

  fun getTasksForPatient(patientId: String, tabPosition: Int) {
    viewModelScope.launch {
      taskListMutable =
        FhirApplication.taskManager(getApplication<Application>().applicationContext)
          .getTasksForPatient(
            patientId = patientId,
            extraFilter = {
              filter(
                Task.STATUS,
                { if (tabPosition == 1) Task.TaskStatus.COMPLETED else Task.TaskStatus.READY }
              )
            }
          ) as ArrayList<Task>
      taskList.value = taskListMutable
    }
  }

  fun setUpdatedTask() {
    viewModelScope.launch {
      taskListMutable[taskPosition] =
        fhirEngine.get(ResourceType.Task, taskListMutable[taskPosition].logicalId) as Task
      taskList.value = taskListMutable
    }
  }
}
