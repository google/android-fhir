package com.google.android.fhir.demo.care.listscreening

import com.google.android.fhir.demo.FhirApplication
import com.google.android.fhir.demo.care.CareUtil
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.logicalId
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.Task

class ListScreeningsViewModel(application: Application): AndroidViewModel(application)  {
    var taskPosition: Int = 0
    var taskList = MutableLiveData<List<Task>>()
    var taskListMutable = ArrayList<Task>()

    val fhirEngine = FhirApplication.fhirEngine(application.applicationContext)

    private val iParser = FhirContext.forR4().newJsonParser()

    fun getTasksForPatient(patientId: String, tabPosition: Int) {
        viewModelScope.launch {
            taskListMutable = CareUtil.getTasksForPatient(
                patientId = patientId,
                extraFilter = {
                    filter(
                        Task.STATUS, {
                            if(tabPosition == 1) Task.TaskStatus.COMPLETED else Task.TaskStatus.READY
                        }
                    )
                }
            ) as ArrayList<Task>
            taskList.value = taskListMutable
        }
    }

    fun getTaskString(task: Task): String {
        return iParser.encodeResourceToString(task)
    }

    fun setUpdatedTask() {
        viewModelScope.launch {
            taskListMutable[taskPosition] =
                fhirEngine.get(ResourceType.Task, taskListMutable[taskPosition].logicalId) as Task
            taskList.value = taskListMutable
        }
    }
}