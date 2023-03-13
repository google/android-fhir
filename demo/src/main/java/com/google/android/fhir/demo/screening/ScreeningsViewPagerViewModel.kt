package com.google.android.fhir.demo.screening

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.demo.care.CarePlanManager
import com.google.android.fhir.demo.care.TaskManager
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Task

class ScreeningsViewPagerViewModel(application: Application, private val state: SavedStateHandle) :
    AndroidViewModel(application) {
    private val iParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

    fun fetchQuestionnaireString(task: Task): LiveData<String>{
        val questionnaireString = MediatorLiveData<String>()
        viewModelScope.launch {
            questionnaireString.value = iParser.encodeResourceToString(TaskManager.fetchQuestionnaireFromTask(task))
        }
        return questionnaireString
    }
}