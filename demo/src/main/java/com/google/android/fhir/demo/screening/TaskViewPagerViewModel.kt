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
import com.google.android.fhir.demo.FhirApplication
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.Task
import org.hl7.fhir.r4.model.Task.TaskStatus

class TaskViewPagerViewModel(application: Application, private val state: SavedStateHandle) :
  AndroidViewModel(application) {
  val livePendingTasksCount = MutableLiveData<Int>()
  val liveCompletedTasksCount = MutableLiveData<Int>()
  val patientName = MutableLiveData<String>()

  private val taskManager =
    FhirApplication.taskManager(getApplication<Application>().applicationContext)
  private val fhirEngine =
    FhirApplication.fhirEngine(getApplication<Application>().applicationContext)

  fun getTasksCount(patientId: String) {
    viewModelScope.launch {
      livePendingTasksCount.value =
        taskManager.getTasksCount(patientId) {
          filter(Task.STATUS, { value = of(getTaskStatus(0)) })
        }!!
      liveCompletedTasksCount.value =
        taskManager.getTasksCount(patientId) {
          filter(Task.STATUS, { value = of(getTaskStatus(1)) })
        }!!
    }
  }

  fun getPatientName(patientId: String) {
    viewModelScope.launch {
      patientName.value =
        (fhirEngine.get(ResourceType.Patient, patientId) as Patient).name[0].nameAsSingleString
    }
  }

  /**
   * Currently only [Task.TaskStatus.COMPLETED] & [Task.TaskStatus.READY] are shown. This logic
   * could be extended.
   */
  fun getTaskStatus(position: Int): String {
    return when (position) {
      0 -> TaskStatus.READY
      1 -> TaskStatus.COMPLETED
      else -> TaskStatus.READY
    }.toCode()
  }
}
