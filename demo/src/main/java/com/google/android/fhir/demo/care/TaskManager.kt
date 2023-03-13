package com.google.android.fhir.demo.care

import android.content.Context
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.demo.FhirApplication
import com.google.android.fhir.search.Search
import com.google.android.fhir.search.search
import com.google.android.fhir.workflow.FhirOperator
import org.hl7.fhir.r4.model.CarePlan
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.Task

class TaskManager {
    companion object{
        private lateinit var fhirEngine: FhirEngine
        private lateinit var fhirOperator: FhirOperator
        private var planDefinitionId: String? = null
        private lateinit var jsonParser: IParser

        fun init(context: Context) {
            fhirEngine = FhirApplication.fhirEngine(context)
            fhirOperator = FhirApplication.fhirOperator(context)
            jsonParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
        }

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
                    filter(Questionnaire.IDENTIFIER, { value = of(task.focus.reference.substring("Questionnaire/".length)) })
                }
            return questionnaires.firstOrNull()
        }

        suspend fun getTasksForPatient(patientId: String, extraFilter: Search.() -> Unit): List<Task> {
            return fhirEngine.search<Task>{
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
                    fhirEngine.get(ResourceType.CarePlan, task.basedOnFirstRep.reference.substring(9)) as
                            CarePlan
                for (activity in carePlan.activity) {
                    if (activity.reference.reference.equals(task.id)) {
                        activity.detail.status = CarePlanManager.mapTaskStatusToCarePlanStatus(status)
                        fhirEngine.update(carePlan)
                        break
                    }
                }
            }
        }

        fun getTaskName(task: Task): String {
            return task.identifier[0].value
        }
    }
}