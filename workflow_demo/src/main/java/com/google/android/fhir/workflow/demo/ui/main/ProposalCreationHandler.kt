/*
 * Copyright 2024-2026 Google LLC
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

package com.google.android.fhir.workflow.demo.ui.main

import android.content.Context
import android.util.Log
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.knowledge.KnowledgeManager
import com.google.android.fhir.workflow.FhirOperator
import com.google.android.fhir.workflow.demo.helper.ResourceLoader
import java.io.File
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.CarePlan
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.MedicationRequest
import org.hl7.fhir.r4.model.MetadataResource
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Resource

class ProposalCreationHandler(
  private val fhirEngine: FhirEngine,
  private val fhirOperator: FhirOperator,
  private val applicationContext: Context,
  private val knowledgeManager: KnowledgeManager,
) {

  suspend fun checkInstalledDependencies(configuration: Configuration): Boolean {
    return configuration.planDefinitionCanonical?.let { knowledgeManager.loadResources(it).any() }
      ?: false
  }

  suspend fun installDependencies(configuration: Configuration) {
    withContext(Dispatchers.IO) {
      val patient =
        Patient().apply {
          id = "active_apple_guy"
          addName(
            HumanName().apply {
              addGiven("First")
              family = "Last"
            },
          )

          active = true

          birthDate = Date(1990, 2, 4)
        }

      importToFhirEngine(patient)

      val loader = ResourceLoader(androidContext = applicationContext)

      // load plan definition
      configuration.planDefinitionPath?.let { loader.loadFile(it, ::installToIgManager) }

      // load activity definition
      configuration.activityDefinitionPath?.let {
        // load plan definition
        loader.loadFile(it, ::installToIgManager)
      }
      // load library
      configuration.cqlLibraryPath?.let { loader.loadFile(it, ::installToIgManager) }

      configuration.inputBundlePath?.let { loader.loadFile(it, ::importToFhirEngine) }
    }
  }

  suspend fun prepareAndInitiateProposal(configuration: Configuration): Result<Resource> {
    val proposal = generateProposal(configuration)
    Log.d("TAG", "createProposalPhase: $proposal")
    proposal?.let { fhirEngine.create(proposal) }
      ?: return Result.failure(Exception("MedicationRequest not created."))
    return Result.success(proposal)
  }

  private suspend fun generateProposal(configuration: Configuration): Resource? =
    withContext(Dispatchers.IO) {
      //      val patient =
      //        Patient().apply {
      //          id = "active_apple_guy"
      //          addName(
      //            HumanName().apply {
      //              addGiven("First")
      //              family = "Last"
      //            },
      //          )
      //
      //          active = true
      //
      //          birthDate = Date(1990, 2, 4)
      //        }
      //
      //      importToFhirEngine(patient)
      //
      //      val loader = ResourceLoader(androidContext = applicationContext)
      //
      //      // load plan definition
      //      configuration.planDefinitionPath?.let { loader.loadFile(it, ::installToIgManager) }
      //
      //      // load activity definition
      //      configuration.activityDefinitionPath?.let {
      //        // load plan definition
      //        loader.loadFile(it, ::installToIgManager)
      //      }
      //      // load library
      //      configuration.cqlLibraryPath?.let { loader.loadFile(it, ::installToIgManager) }
      //
      //      configuration.inputBundlePath?.let { loader.loadFile(it, ::importToFhirEngine) }

      val carePlan =
        fhirOperator.generateCarePlan(
          planDefinitionCanonical = CanonicalType(configuration.planDefinitionCanonical),
          subject = "Patient/${configuration.patientId}",
        )

      if (carePlan !is CarePlan) return@withContext null

      carePlan.contained.forEach { if (it is MedicationRequest) return@withContext it as Resource }
      return@withContext null
    }

  private suspend fun importToFhirEngine(resource: Resource) {
    fhirEngine.create(resource)
    Log.i("TAG", "importToFhirEngine: ${resource.id}")
  }

  private suspend fun installToIgManager(resource: Resource) {
    try {
      knowledgeManager.index(writeToFile(resource))
      Log.d("TAG", "installToIgManager: ${resource.id}")
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun writeToFile(resource: Resource): File {
    val fileName =
      if (resource is MetadataResource && resource.name != null) {
        if (resource.version != null) {
          resource.name + "-" + resource.version
        } else {
          resource.name
        }
      } else {
        resource.idElement.toString()
      }
    return File(applicationContext.filesDir, fileName).apply {
      this.parentFile?.mkdirs()
      writeText(FhirContext.forR4Cached().newJsonParser().encodeResourceToString(resource))
    }
  }
}
