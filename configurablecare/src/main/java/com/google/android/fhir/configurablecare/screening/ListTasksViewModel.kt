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
package com.google.android.fhir.configurablecare.screening

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.search.Order
import com.google.android.fhir.configurablecare.FhirApplication
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.MedicationRequest
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.Task
import org.hl7.fhir.r4.model.Task.TaskStatus

class ListScreeningsViewModel(application: Application) : AndroidViewModel(application) {

  private val iParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
  private val taskManager =
    FhirApplication.taskManager(getApplication<Application>().applicationContext)
  private val carePlanManager =
    FhirApplication.carePlanManager(getApplication<Application>().applicationContext)
  private val requestManager =
    FhirApplication.requestManager(getApplication<Application>().applicationContext)

  val liveSearchedTasks = MutableLiveData<List<TaskItem>>()

  fun getTasksForPatient(patientId: String, taskStatus: String) {
    viewModelScope.launch {
      liveSearchedTasks.value =
        requestManager.getAllRequestsForPatient(patientId)//, taskStatus)
          .mapIndexed { index, fhirTask ->
            if (fhirTask is Task) fhirTask.toTaskItem(index + 1)
            else if (fhirTask is MedicationRequest) fhirTask.toTaskItem(index + 1)
            else TaskItem(
              id = (index + 1).toString(),
              resourceType = "Task",
              resourceId = if (fhirTask.hasIdElement()) fhirTask.idElement.idPart else "",
              description = "",
              status = "no status",
              intent = "no intent",
              dueDate = "due date",
              completedDate = "completed date",
              owner = "owner",
              fhirResourceId = "",
              clickable = false
            )
          }
    }
  }

  /**
   * Alternatively we could cache the questionnaireStrings for each tasks in this viewModel instead
   * of [runBlocking]
   */
  fun fetchQuestionnaireString(taskItem: TaskItem): String = runBlocking {
    // iParser.encodeResourceToString(
    //   taskManager.fetchQuestionnaireFromTaskLogicalId(taskItem.resourceId)
    // )

    val questionnaire = taskManager.fetchQuestionnaire(taskItem.fhirResourceId)

    // update request
    // requestManager.updateIntent(taskItem.resourceId, taskItem.resourceType)
    iParser.encodeResourceToString(questionnaire)
  }

  data class TaskItem(
    val id: String,
    val resourceType: String,
    // for Task/123/... this should be 123
    val resourceId: String,
    val description: String,
    val status: String,
    val intent: String,
    val dueDate: String,
    val completedDate: String,
    val owner: String,
    val fhirResourceId: String,  // resource to be opened
    // for Questionnaire/456 - this should be "Questionnaire/456"
    val clickable: Boolean
  ) {
    override fun toString() = description
  }

  data class RequestItem(
    val id: String,
    // for Task/123/... this should be 123
    val resourceType: String,
    val resourceId: String,
    val description: String,
    val status: String,
    val intent: String,
    val dueDate: String,
    val completedDate: String,
    val fhirResourceId: String,  // resource to be opened
    // for Questionnaire/456 - this should be "Questionnaire/456"
    val clickable: Boolean
  ) {
    override fun toString() = description
  }
}

internal fun Task.toRequestItem(position: Int): ListScreeningsViewModel.RequestItem {
  val taskResourceId = if (hasIdElement()) idElement.idPart else ""
  val description = if (hasDescription()) description else "Sample Task Name"
  // status and intent are always present
  val taskStatus = status.toCode()
  val taskIntent = intent.toCode()
  val dueDate =
    if (hasRestriction() && restriction.hasPeriod()) restriction.period.end.toString()
    else "unknown"
  val completedDate = if (hasLastModified()) lastModified.toString() else dueDate
  val clickable =
    focus.reference.contains("Questionnaire") && taskStatus != TaskStatus.COMPLETED.toCode()

  return ListScreeningsViewModel.RequestItem(
    id = position.toString(),
    resourceType = resourceType.toString(),
    resourceId = taskResourceId,
    description = description,
    status = taskStatus,
    intent = taskIntent,
    dueDate = dueDate,
    completedDate = completedDate,
    fhirResourceId = if (clickable) focus.reference else "",
    clickable = clickable
  )
}

internal fun MedicationRequest.toRequestItem(position: Int): ListScreeningsViewModel.RequestItem {
  val medicationRequestResourceId = if (hasIdElement()) idElement.idPart else ""
  val description = if (hasMedicationCodeableConcept() && medicationCodeableConcept.hasCoding()) "${medicationCodeableConcept.codingFirstRep.display} [${medicationCodeableConcept.codingFirstRep.code}]" else "No description"
  // status and intent are always present
  val medicationRequestStatus = status.toCode()
  val medicationRequestIntent = intent.toCode()
  val dueDate = if (hasDispenseRequest() && dispenseRequest.hasValidityPeriod()) dispenseRequest.validityPeriod.start.toString() else "unknown"
  val completedDate = dueDate
  val clickable = true
  // focus.reference.contains("Questionnaire") && taskStatus != TaskStatus.COMPLETED.toCode()

  return ListScreeningsViewModel.RequestItem(
    id = position.toString(),
    resourceType = resourceType.toString(),
    resourceId = medicationRequestResourceId,
    description = description,
    status = medicationRequestStatus,
    intent = medicationRequestIntent,
    dueDate = dueDate,
    completedDate = completedDate,
    fhirResourceId = "Questionnaire/Questionnaire-IMMZD4CheckContraindications",
    clickable = clickable
  )
}

internal fun Task.toTaskItem(position: Int): ListScreeningsViewModel.TaskItem {
  val taskResourceId = if (hasIdElement()) idElement.idPart else ""
  val description = if (hasDescription()) "$description [$resourceType]" else ""
  // status and intent are always present
  val taskStatus = status.toCode()
  val taskIntent = intent.toCode()
  val dueDate =
    if (hasRestriction() && restriction.hasPeriod()) restriction.period.end.toString()
    else "Sample End Date"
  val completedDate = if (hasLastModified()) lastModified.toString() else dueDate
  val owner = if (owner == null) "" else if (owner.hasDisplay()) owner.display else ""
  val clickable =
    focus.reference.contains("Questionnaire") && taskStatus != TaskStatus.COMPLETED.toCode()

  return ListScreeningsViewModel.TaskItem(
    id = position.toString(),
    resourceType = resourceType.toString(),
    resourceId = taskResourceId,
    description = description,
    status = taskStatus,
    intent = taskIntent,
    dueDate = dueDate,
    completedDate = completedDate,
    owner = owner,
    fhirResourceId = if (clickable) focus.reference else "",
    clickable = clickable
  )
}

internal fun MedicationRequest.toTaskItem(position: Int): ListScreeningsViewModel.TaskItem {
  val taskResourceId = if (hasIdElement()) idElement.idPart else ""
  val description = if (hasMedicationCodeableConcept() && medicationCodeableConcept.hasCoding()) "${medicationCodeableConcept.codingFirstRep.display} [$resourceType]" else ""
  // status and intent are always present
  val taskStatus = status.toCode()
  val taskIntent = intent.toCode()
  val dueDate = if (hasDispenseRequest() && dispenseRequest.hasValidityPeriod()) dispenseRequest.validityPeriod.start.toString() else "unknown"
  val completedDate = dueDate
  val owner = ""
  val clickable = true
  val fhirResourceId = if (hasSupportingInformation()) supportingInformation.first().reference else "Questionnaire/IMMZD4CheckContraindicationsMeasles"
    // focus.reference.contains("Questionnaire") && taskStatus != TaskStatus.COMPLETED.toCode()

  return ListScreeningsViewModel.TaskItem(
    id = position.toString(),
    resourceType = resourceType.toString(),
    resourceId = taskResourceId,
    description = description,
    status = taskStatus,
    intent = taskIntent,
    dueDate = dueDate,
    completedDate = completedDate,
    owner = owner,
    fhirResourceId = fhirResourceId,
    clickable = clickable
  )
}
