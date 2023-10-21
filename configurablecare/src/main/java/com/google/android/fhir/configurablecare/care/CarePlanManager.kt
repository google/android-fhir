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
package com.google.android.fhir.configurablecare.care

import android.content.Context
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.knowledge.FhirNpmPackage
import com.google.android.fhir.knowledge.KnowledgeManager
import com.google.android.fhir.search.search
import com.google.android.fhir.workflow.FhirOperator.Builder
import com.google.android.fhir.workflow.TestBundleLoader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hl7.fhir.r4.model.ActivityDefinition
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.CarePlan
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Library
import org.hl7.fhir.r4.model.MedicationRequest
import org.hl7.fhir.r4.model.MetadataResource
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.PlanDefinition
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.RequestGroup
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.Task


/** Responsible for creating and managing CarePlans */
class CarePlanManager(
  private var fhirEngine: FhirEngine,
  private val fhirContext: FhirContext,
  private val context: Context,
) {
  private var knowledgeManager = KnowledgeManager.create(context, inMemory = true)
  private var fhirOperator =
    Builder(context.applicationContext)
      .fhirContext(fhirContext)
      .fhirEngine(fhirEngine)
      .knowledgeManager(knowledgeManager)
      .build()

  private var taskManager: RequestResourceManager<Task> = TaskManager(fhirEngine)
  private var requestManager: RequestManager = RequestManager(fhirEngine, fhirContext, TestRequestHandler())
  private var cqlLibraryIdList = ArrayList<String>()
  private val jsonParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

  private fun writeToFile(resource: Resource): File {
    val fileName =
      if (resource is MetadataResource && resource.name != null) {
        resource.name
      } else {
        // resource.idElement.idPart
        "${resource.fhirType()}_${resource.idElement.idPart}"
      }
    return File(context.filesDir, fileName).apply {
      writeText(jsonParser.encodeResourceToString(resource))
    }
  }

  private fun readFileFromAssets(context: Context, filename: String): String {
    return context.assets.open(filename).bufferedReader().use { it.readText() }
  }

  suspend fun saveKnowledgeResources() {
    val rootDirectory = File(context.filesDir, "smart-imm/ig")
    if (rootDirectory.exists()) {
      initializeKnowledgeManager(rootDirectory)
      return
    }
    rootDirectory.mkdirs()

    val fileList = context.assets.list("smart-imm/ig")
    if (fileList != null) {
      for (filename in fileList) {
        if (filename.contains(".json")) {
          val contents = readFileFromAssets(context, "smart-imm/ig/$filename")
          try {
            val resource = jsonParser.parseResource(contents)
            if (resource is Resource) {
              fhirEngine.create(resource)

              withContext(Dispatchers.IO) {
                val fis = FileOutputStream(File(context.filesDir, "smart-imm/ig/$filename"))
                fis.write(contents.toByteArray())
                println("Saved: ${context.filesDir}/smart-imm/ig/$filename")
              }
            }
          } catch (exception: Exception) {
            // do nothing
          }
        }
      }
    }
  }

  private suspend fun importToFhirEngine(resource: Resource) {
    fhirEngine.create(resource)
  }

  suspend fun installKnowledgeResource(
    resource: Resource,
  ): Collection<Resource> {
    var bundleCollection: Collection<Resource> = mutableListOf()

    if (resource is Library) {
      cqlLibraryIdList.add(IdType(resource.id).idPart)
      knowledgeManager.install(writeToFile(resource))
    }
    knowledgeManager.install(writeToFile(resource))
    bundleCollection += resource

    return bundleCollection
  }


  /**
   * Extracts resources present in PlanDefinition.contained field
   *
   * We cannot use $data-requirements on the [PlanDefinition] yet. So, we assume that all knowledge
   * resources required to $apply a [PlanDefinition] are present within `PlanDefinition.contained`
   *
   * @param planDefinition PlanDefinition resource for which dependent resources are extracted
   */
  suspend fun getPlanDefinitionDependentResources(
    planDefinition: PlanDefinition,
  ): Collection<Resource> {
    var bundleCollection: Collection<Resource> = mutableListOf()

    for (resource in planDefinition.contained) {
      if (resource is Bundle) {
        for (entry in resource.entry) {
          entry.resource.meta.lastUpdated = planDefinition.meta.lastUpdated
          if (entry.resource is Library) {
            cqlLibraryIdList.add(IdType(entry.resource.id).idPart)
            knowledgeManager.install(writeToFile(entry.resource))
          }
          knowledgeManager.install(writeToFile(entry.resource))

          bundleCollection += entry.resource
        }
      }
    }
    return bundleCollection
  }

  suspend fun initializeKnowledgeManager(rootDirectory: File) {
    // val rootDirectory = File(context.filesDir, "smart-imm/ig")
    knowledgeManager.install(
      FhirNpmPackage(
        "who.fhir.immunization",
        "1.0.0",
        "https://github.com/WorldHealthOrganization/smart-immunizations",
      ),
      rootDirectory,
    )
    println("KM installed")
  }

  suspend fun installKnowledgeResources() {
    val rootDirectory = File(context.filesDir, "smart-imm/ig")

    rootDirectory.listFiles()?.forEach { file ->
      try {
        val resource = jsonParser.parseResource(FileInputStream(file))
        if (resource is Resource) {
          //   if (!(resource is PlanDefinition || resource is ActivityDefinition || resource is Library)) {
          //     fhirEngine.create(resource)
          //     println("Saved to engine: ${resource.id}")
          //   }
          // } else {
          //   // Timber.d("Unable to import file: %file")
          // }
          fhirEngine.create(resource)
          // println("Saved to engine: ${resource.id}")
        }
      } catch (exception: Exception) {
        // Timber.d(exception, "Unable to import file: %file")
      }
    }

    // initializeKnowledgeManager()
    // knowledgeManager.install(
    //   FhirNpmPackage(
    //     "who.fhir.immunization",
    //     "1.0.0",
    //     "https://github.com/WorldHealthOrganization/smart-immunizations",
    //   ),
    //   rootDirectory,
    // )
    // println("KM installed")
  }

  /**
   * Knowledge resources are loaded from [FhirEngine] and installed so that they may be used when
   * running $apply on a [PlanDefinition]
   */
  private suspend fun loadCarePlanResourcesFromDb() {

    // installKnowledgeResources()

    // Load Library resources
    val availableCqlLibraries = fhirEngine.search<Library> {}
    val availablePlanDefinitions = fhirEngine.search<PlanDefinition> {}
    val availableActivityDefinitions = fhirEngine.search<ActivityDefinition> {}
    for (cqlLibrary in availableCqlLibraries) {
      knowledgeManager.install(writeToFile(cqlLibrary.resource))
      cqlLibraryIdList.add(IdType(cqlLibrary.resource.id).idPart)
      println(jsonParser.encodeResourceToString(cqlLibrary.resource))
    }
    for (planDefinition in availablePlanDefinitions) {
      knowledgeManager.install(writeToFile(planDefinition.resource))
      println(jsonParser.encodeResourceToString(planDefinition.resource))
    }
    for (activityDefinition in availableActivityDefinitions) {
      knowledgeManager.install(writeToFile(activityDefinition.resource))
      println(jsonParser.encodeResourceToString(activityDefinition.resource))
    }
  }

  /**
   * Executes $apply on a [PlanDefinition] for a [Patient] and creates the request resources as per
   * the proposed [CarePlan]
   *
   * @param planDefinitionId PlanDefinition resource ID for which $apply is run
   * @param patient Patient resource for which the [PlanDefinition] $apply is run
   * @param requestResourceConfigs List of configurations that need to be applied to the request
   * resources as a result of the proposed [CarePlan]
   */
  suspend fun applyPlanDefinitionOnPatient(
    planDefinitionId: String,
    patient: Patient,
    requestConfiguration: List<RequestConfiguration>,
  ) {
    // smartIgTest()

    val patientId = IdType(patient.id).idPart
    // loadCarePlanResourcesFromDb()
    // if (cqlLibraryIdList.isEmpty()) {
    //   loadCarePlanResourcesFromDb()
    // }

    // initializeKnowledgeManager(File(context.filesDir, "smart-imm/ig"))

    // val carePlanProposal =
    //   fhirOperator.generateCarePlan(planDefinitionId = IdType(planDefinitionId).idPart, subject = "Patient/$patientId")
    //     as CarePlan
    println("PD: ${CanonicalType(planDefinitionId)}")
    val carePlanProposal =
      fhirOperator.generateCarePlan(planDefinition = CanonicalType(planDefinitionId), subject = "Patient/$patientId")
        as CarePlan

    println(jsonParser.encodeResourceToString(carePlanProposal))

    // Fetch existing CarePlan of record for the Patient or create a new one if it does not exist
    // val carePlanOfRecord = getCarePlanOfRecordForPatient(patient)

    // Accept the proposed (transient) CarePlan by default and add tasks to the CarePlan of record
    val resourceList = acceptCarePlan(carePlanProposal, requestConfiguration)

    if (resourceList.isEmpty() && planDefinitionId.contains("IMMZD2DTMeaslesCI")) {
      // begin order and end plan
      val medicationRequestPlans = requestManager.getRequestsForPatient(patientId, ResourceType.MedicationRequest, intent = "plan")
      println("moving to order for ${jsonParser.encodeResourceToString(medicationRequestPlans.first())}")
      requestManager.beginOrder(medicationRequestPlans.first() as MedicationRequest, requestConfiguration, "No Contraindications detected so proceeding with order")
    }
  }



  /**
   * Executes $apply on a [PlanDefinition] for a list of patients and creates the request resources
   * as per the proposed CarePlans
   *
   * @param planDefinitionId PlanDefinition resource ID for which $apply is run
   * @param patientList List of Patient resources for which the [PlanDefinition] $apply is run
   * @param requestResourceConfigs List of configurations that need to be applied to the request
   * resources as a result of the proposed [CarePlan]
   */
  // suspend fun applyPlanDefinitionOnMultiplePatients(
  //   planDefinitionId: String,
  //   patientList: List<Patient>,
  //   requestResourceConfigs: List<RequestResourceConfig>,
  // ) {
  //   if (cqlLibraryIdList.isEmpty()) {
  //     loadCarePlanResourcesFromDb()
  //   }
  //
  //   for (patient in patientList) {
  //     val patientId = IdType(patient.id).idPart
  //
  //     val carePlanProposal =
  //       fhirOperator.generateCarePlan(planDefinitionId = planDefinitionId, subject = patientId)
  //         as CarePlan
  //
  //     // Fetch existing CarePlan of record for the Patient or create a new one if it does not exist
  //     val carePlanOfRecord = getCarePlanOfRecordForPatient(patient)
  //
  //     // Accept the proposed (transient) CarePlan by default and add tasks to the CarePlan of record
  //     acceptCarePlan(carePlanProposal, carePlanOfRecord, requestResourceConfigs)
  //   }
  // }

  /** Fetch the [CarePlan] of record for a given [Patient], if it exists, otherwise create it */
  private suspend fun getCarePlanOfRecordForPatient(patient: Patient): CarePlan {
    val patientId = IdType(patient.id).idPart
    val existingCarePlans = fhirEngine.search("CarePlan?subject=$patientId")

    val carePlanOfRecord = CarePlan()
    return if (existingCarePlans.isEmpty()) {
      carePlanOfRecord.status = CarePlan.CarePlanStatus.ACTIVE
      carePlanOfRecord.subject = Reference(patient)
      carePlanOfRecord.description = "CarePlan of Record"
      fhirEngine.create(carePlanOfRecord)
      carePlanOfRecord
    } else {
      existingCarePlans.first() as CarePlan
    }
  }

  /** Update the [CarePlan] to include a reference to the FHIR-define protocol or guideline */
  private fun updateCarePlanWithProtocol(carePlan: CarePlan, uris: List<CanonicalType>) {
    for (uri in uris) carePlan.addInstantiatesCanonical(uri.value)
  }

  /** Link the request resources created for the [Patient] back to the [CarePlan] of record */
  private fun addRequestResourcesToCarePlanOfRecord(
    carePlan: CarePlan,
    requestResourceList: List<Resource>,
  ) {
    for (resource in requestResourceList) {
      when (resource.fhirType()) {
        "Task" ->
          carePlan.addActivity().setReference(Reference(resource)).detail.status =
            taskManager.mapRequestResourceStatusToCarePlanStatus(resource as Task)
        "ServiceRequest" -> TODO("Not supported yet")
        "MedicationRequest" -> TODO("Not supported yet")
        "SupplyRequest" -> TODO("Not supported yet")
        "Procedure" -> TODO("Not supported yet")
        "DiagnosticReport" -> TODO("Not supported yet")
        "Communication" -> TODO("Not supported yet")
        "CommunicationRequest" -> TODO("Not supported yet")
        else -> TODO("Not a valid request resource")
      }
    }
  }

  /** Add the [CarePlan] reference to the request resources */
  private suspend fun linkRequestResourcesToCarePlan(
    carePlan: CarePlan,
    requestResourceList: List<Resource>,
  ) {
    for (resource in requestResourceList) {
      when (resource.fhirType()) {
        "Task" -> taskManager.linkCarePlanToRequestResource(resource as Task, carePlan)
        "ServiceRequest" -> TODO("Not supported yet")
        "MedicationRequest" -> TODO("Not supported yet")
        "SupplyRequest" -> TODO("Not supported yet")
        "Procedure" -> TODO("Not supported yet")
        "DiagnosticReport" -> TODO("Not supported yet")
        "Communication" -> TODO("Not supported yet")
        "CommunicationRequest" -> TODO("Not supported yet")
        else -> TODO("Not a valid request resource")
      }
    }
  }

  // /**
  //  * Invokes the respective [RequestResourceManager] to create new request resources as per the
  //  * proposed [CarePlan]
  //  *
  //  * @param resourceList List of request resources to be created
  //  * @param requestResourceConfigs Application-specific configurations to be applied on the created
  //  * request resources
  //  */
  // private suspend fun createProposedRequestResources(
  //   resourceList: List<Resource>,
  //   requestResourceConfigs: List<RequestResourceConfig>,
  // ): List<Resource> {
  //   val createdRequestResources = ArrayList<Resource>()
  //   for (resource in resourceList) {
  //     when (resource.fhirType()) {
  //       "Task" -> {
  //         val task =
  //           taskManager.updateRequestResource(
  //             resource as Task,
  //             requestResourceConfigs.firstOrNull { it.resourceType == "Task" }!!
  //           )
  //         createdRequestResources.add(task)
  //       }
  //       "ServiceRequest" -> TODO("Not supported yet")
  //       "MedicationRequest" -> TODO("Not supported yet")
  //       "SupplyRequest" -> TODO("Not supported yet")
  //       "Procedure" -> TODO("Not supported yet")
  //       "DiagnosticReport" -> TODO("Not supported yet")
  //       "Communication" -> TODO("Not supported yet")
  //       "CommunicationRequest" -> TODO("Not supported yet")
  //       "RequestGroup" -> {}
  //       else -> fhirEngine.create(resource) // TODO("Not a valid request resource")
  //     }
  //   }
  //   return createdRequestResources
  // }

  /**
   * Accept the proposed [CarePlan] and create the proposed request resources as per the
   * configurations
   *
   * @param proposedCarePlan Proposed [CarePlan] generated when $apply is run on a [PlanDefinition]
   * @param carePlanOfRecord CarePlan of record for a [Patient] which needs to be updated with the
   * new request resources created as per the proposed CarePlan
   * @param requestResourceConfigs Application-specific configurations to be applied on the created
   * request resources
   */
  // private suspend fun acceptCarePlan(
  //   proposedCarePlan: CarePlan,
  //   carePlanOfRecord: CarePlan,
  //   requestResourceConfigs: List<RequestResourceConfig>,
  // ) {
  //   // modify this and use:
  //   val resourceList: MutableList<Resource> = mutableListOf()
  //   for (request in proposedCarePlan.contained) {
  //     if (request is RequestGroup) {
  //       resourceList.addAll(requestManager.createRequestFromRequestGroup(request))
  //     }
  //   }
  //
  //   // val resourceList =
  //   //   createProposedRequestResources(proposedCarePlan.contained, requestResourceConfigs)
  //   updateCarePlanWithProtocol(carePlanOfRecord, proposedCarePlan.instantiatesCanonical)
  //   addRequestResourcesToCarePlanOfRecord(carePlanOfRecord, resourceList)
  //
  //   fhirEngine.update(carePlanOfRecord)
  //   linkRequestResourcesToCarePlan(carePlanOfRecord, resourceList)
  // }

  private suspend fun acceptCarePlan(
    proposedCarePlan: CarePlan,
    requestConfiguration: List<RequestConfiguration>,
  ): List<Resource>  {
    // modify this and use:
    val resourceList: MutableList<Resource> = mutableListOf()
    for (request in proposedCarePlan.contained) {
      if (request is RequestGroup) {
        resourceList.addAll(requestManager.createRequestFromRequestGroup(request))
      }
    }

    for (resource in resourceList) {
      if (resource is MedicationRequest) {
        if (resource.intent == MedicationRequest.MedicationRequestIntent.PROPOSAL) {
          requestManager.beginProposal(resource, requestConfiguration)
        }
        // val intentCondition = getNextActionForMedicationRequest(resource, requestConfiguration)
        // if (intentCondition != null) {
        //   if (intentCondition.condition == "automatic") {  // transition to the next intent level
        //     if (intentCondition.action == "transition-to-plan") {  // intent = proposal
        //       requestManager.beginPlan(resource, requestConfiguration)
        //       requestManager.endPlan(resource)
        //     } else if (intentCondition.action == "transition-to-order") {  // intent = plan
        //       requestManager.beginOrder(resource, requestConfiguration)
        //       requestManager.endOrder(resource)
        //     }
        //   } else {
        //     if (intentCondition.action == "transition-to-plan") {  // intent = proposal
        //       requestManager.beginPlan(resource, requestConfiguration)
        //     } else if (intentCondition.action == "transition-to-order") {  // intent = plan
        //       requestManager.endPlan(resource)
        //       requestManager.beginOrder(resource, requestConfiguration)
        //     }
        //
        //     requestManager.updateIntent(IdType(resource.id).idPart, "MedicationRequest", requestConfiguration)
        //   } else {
        //     resource.addSupportingInformation(Reference(condition))
        //     fhirEngine.update(resource)
        //   }
        // }
      }
    }

    // val resourceList =
    //   createProposedRequestResources(proposedCarePlan.contained, requestResourceConfigs)
    // updateCarePlanWithProtocol(carePlanOfRecord, proposedCarePlan.instantiatesCanonical)
    // addRequestResourcesToCarePlanOfRecord(carePlanOfRecord, resourceList)
    //
    // fhirEngine.update(carePlanOfRecord)
    // linkRequestResourcesToCarePlan(carePlanOfRecord, resourceList)
    return resourceList
  }

  /** Update status of a [CarePlan] activity */
  // private suspend fun updateCarePlanStatus(
  //   carePlan: CarePlan,
  //   requestedActivityResource: Resource,
  //   carePlanActivityStatus: CarePlanActivityStatus,
  //   outcomeReferences: List<Reference>,
  // ) {
  //   if (carePlan.isEmpty) return
  //   for (activity in carePlan.activity) {
  //     if (activity.reference.reference.equals(
  //         requestedActivityResource.fhirType() + "/" + IdType(requestedActivityResource.id).idPart
  //       )
  //     ) {
  //       activity.detail.status = carePlanActivityStatus
  //       activity.outcomeReference = outcomeReferences
  //       fhirEngine.update(carePlan)
  //       break
  //     }
  //   }
  // }

  /**
   * Find and update the status of the [CarePlan] activity as per the corresponding request resource
   * status
   */
  // suspend fun updateCarePlanActivity(
  //   requestResource: Resource,
  //   requestResourceStatus: String,
  //   outcomeReferences: List<Reference>,
  //   updateCarePlan: Boolean = true,
  // ) {
  //   val carePlanActivityStatus: CarePlanActivityStatus
  //   val carePlan: CarePlan
  //   when (requestResource.fhirType()) {
  //     "Task" -> {
  //       taskManager.updateRequestResourceStatus(requestResource as Task, requestResourceStatus)
  //       if (updateCarePlan) {
  //         carePlanActivityStatus =
  //           taskManager.mapRequestResourceStatusToCarePlanStatus(requestResource)
  //         carePlan =
  //           if (requestResource.hasBasedOn())
  //             fhirEngine.get(
  //               ResourceType.CarePlan,
  //               IdType(requestResource.basedOnFirstRep.referenceElement.value).idPart
  //             ) as CarePlan
  //           else return
  //         updateCarePlanStatus(carePlan, requestResource, carePlanActivityStatus, outcomeReferences)
  //       }
  //     }
  //     "ServiceRequest" -> TODO("Not supported yet")
  //     "MedicationRequest" -> TODO("Not supported yet")
  //     "SupplyRequest" -> TODO("Not supported yet")
  //     "Procedure" -> TODO("Not supported yet")
  //     "DiagnosticReport" -> TODO("Not supported yet")
  //     "Communication" -> TODO("Not supported yet")
  //     "CommunicationRequest" -> TODO("Not supported yet")
  //     "RequestGroup" -> {}
  //     else -> TODO("Not a valid request resource")
  //   }
  // }

  companion object {
    fun getNextActionForMedicationRequest(medicationRequest: MedicationRequest, requestConfiguration: List<RequestConfiguration>): RequestConfiguration.IntentCondition? {
      val mrConfig = requestConfiguration.firstOrNull {
        it.requestType == "MedicationRequest"
      }?.intentConditions?.firstOrNull {
        it.intent == medicationRequest.intent.toCode()
      }
      return mrConfig
      // return if (mrConfig != null) {
      //   if (mrConfig.condition == "automatic")
      //     ""
      //   else
      //     mrConfig.condition
      // } else
      //   ""
    }
  }

}
