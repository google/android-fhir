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
package com.google.android.fhir.configurablecare

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.datacapture.mapping.ResourceMapper
import com.google.android.fhir.datacapture.mapping.StructureMapExtractionContext
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseValidator
import com.google.android.fhir.get
import com.google.android.fhir.search.search
import java.time.Instant
import java.time.Period
import java.util.Date
import java.util.UUID
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Immunization
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceFactory
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.StructureDefinition
import org.hl7.fhir.r4.model.StructureMap
import org.hl7.fhir.r4.model.Task
import org.hl7.fhir.r4.terminologies.ConceptMapEngine
import org.hl7.fhir.r4.utils.StructureMapUtilities

class TransformSupportServices(private val outputs: MutableList<Base>) :
  StructureMapUtilities.ITransformerServices {

  lateinit var fhirEngine: FhirEngine

  override fun log(message: String) {}

  fun getContext(): org.hl7.fhir.r4.context.SimpleWorkerContext {
    return org.hl7.fhir.r4.context.SimpleWorkerContext()
  }

  fun setFhirEngine(engine: FhirEngine) {
    fhirEngine = engine
  }

  @Throws(FHIRException::class)
  override fun createType(appInfo: Any, name: String): Base {
     // when (name) {
      // "Immunization_Reaction" -> Immunization.ImmunizationReactionComponent()
      // else -> ResourceFactory.createResourceOrType(name)
        /*val structureMaps =*/
    var structureMap: StructureMap
    runBlocking {
      structureMap = fhirEngine.search<StructureMap> { filter(StructureMap.URL, { value = name }) }.first().resource
    }
    return structureMap
  }

  override fun createResource(appInfo: Any, res: Base, atRootofTransform: Boolean): Base {
    if (atRootofTransform) outputs.add(res)
    return res
  }

  @Throws(FHIRException::class)
  override fun translate(appInfo: Any, source: Coding, conceptMapUrl: String): Coding {
    val cme = ConceptMapEngine(getContext())
    return cme.translate(source, conceptMapUrl)
  }

  @Throws(FHIRException::class)
  override fun resolveReference(appContext: Any, url: String): Base {
    throw FHIRException("resolveReference is not supported yet")
  }

  @Throws(FHIRException::class)
  override fun performSearch(appContext: Any, url: String): List<Base> {
    throw FHIRException("performSearch is not supported yet")
  }
}


/** ViewModel for patient registration screen {@link AddPatientFragment}. */
class AddPatientViewModel(application: Application, private val state: SavedStateHandle) :
  AndroidViewModel(application) {

  var questionnaire: String = ""
    // get() = getQuestionnaireJson()
  val savedPatient = MutableLiveData<Patient?>()
  val context = application.applicationContext

  private val questionnaireResource: Questionnaire
    get() =
      FhirContext.forCached(FhirVersionEnum.R4).newJsonParser().parseResource(questionnaire)
        as Questionnaire
  private var fhirEngine: FhirEngine = FhirApplication.fhirEngine(application.applicationContext)
  // private var questionnaireJson: String? = null

  /**
   * Saves patient registration questionnaire response into the application database.
   *
   * @param questionnaireResponse patient registration questionnaire response
   */
  fun savePatient(questionnaireResponse: QuestionnaireResponse) {
    viewModelScope.launch {
      if (QuestionnaireResponseValidator.validateQuestionnaireResponse(
            questionnaireResource,
            questionnaireResponse,
            getApplication()
          )
          .values
          .flatten()
          .any { it is Invalid }
      ) {
        savedPatient.value = null
        return@launch
      }

      val transformSupportServices = TransformSupportServices(mutableListOf())
      transformSupportServices.setFhirEngine(fhirEngine)
      val structureMap = fhirEngine.get<StructureMap>("IMMZCQRToPatient")
      val smUrl = "http://smart.who.int/ig/smart-immunizations/StructureMap/IMMZCQRToPatient"
      val smId = "IMMZCQRToPatient"
      val map = """
        map "http://smart.who.int/ig/smart-immunizations-measles/StructureMap/IMMZCQRToPatient" = "IMMZCQRToPatient"

        uses "http://hl7.org/fhir/StructureDefinition/QuestionnaireResponse" alias QResp as source
        uses "http://smart.who.int/ig/smart-immunizations-measles/StructureDefinition/IMMZCRegisterClient" alias IMMZC as source
        uses "http://hl7.org/fhir/StructureDefinition/Patient" alias Patient as target

        imports "http://smart.who.int/ig/smart-immunizations-measles/StructureMap/IMMZCQRToLM"
        imports "http://smart.who.int/ig/smart-immunizations-measles/StructureMap/IMMZCLMToPatient"

        group QRestToIMMZC(source qr : QResp, target patient : Patient) {
          qr -> create('http://smart.who.int/ig/smart-immunizations-measles/StructureDefinition/IMMZCRegisterClient') as model then {
            qr -> model then QRespToIMMZC(qr, model) "QRtoLM";
            qr -> patient then IMMZCToPatient(model, patient) "LMtoPatient";
          } "QRtoPatient";
        }
      """.trimIndent()
      val bundle =
        ResourceMapper.extractByStructureMap(
          questionnaireResource,
          questionnaireResponse,
          StructureMapExtractionContext(context, transformSupportServices) { _, _ ->
            // StructureMapUtilities(worker).parse(map, "")
            fhirEngine.get<StructureMap>(smId)
          },
        )

      // questionnaireResource.targetStructureMap = "http://smart.who.int/ig/smart-immunizations/StructureMap/IMMZCQRToPatient"
      // ResourceMapper.extract(
      //   questionnaireResource,
      //   questionnaireResponse,
      //   StructureMapExtractionContext(context, transformSupportServices) { _, worker ->
      //     StructureMapUtilities(worker).parse(map, "")
      //   },
      // )

      // val bundle = ResourceMapper.extract(questionnaireResource, questionnaireResponse)
      var flag = false
      var patient: Patient
      for (entry in bundle.entry) {
        if (entry.resource is Patient) {
          patient = entry.resource as Patient
          patient.id = generateUuid()
          fhirEngine.create(patient)

          // create Immunization Review Task
          // createImmunizationReviewTask(patient)
          savedPatient.value = patient

          flag = true
        }
      }
      if (!flag)
        return@launch

      // patient = entry.resource as Patient
      // patient.id = generateUuid()
      // fhirEngine.create(patient)
      // savedPatient.value = patient
    }
  }

  // private suspend fun createImmunizationReviewTask(patient: Patient) {
  //   val task = Task()
  //   task.id = UUID.randomUUID().toString()
  //   task.`for` = Reference(patient)
  //   task.status = Task.TaskStatus.READY
  //   task.intent = Task.TaskIntent.PROPOSAL
  //   task.description = "Immunization Recommendation for ${patient.name.first().given} ${patient.name.first().family}"
  //   task.lastModified = Date.from(Instant.now())
  //   task.restriction.period.end = Date.from(Instant.now().plus(Period.ofDays(30 * 6)))  // 6 months
  //   // Get CKD Questionnaire
  //   task.focus = Reference(fhirEngine.get(ResourceType.Questionnaire, "Questionnaire-IMMZD1ClientHistory"))
  //
  //   print("About to create task")
  //   fhirEngine.create(task)
  //   print("Task created")
  // }

  // private fun getQuestionnaireJson(): String {
  //   questionnaireJson?.let {
  //     return it
  //   }
  //   // questionnaireJson = care
  //     // readFileFromAssets(state[AddPatientFragment.QUESTIONNAIRE_FILE_PATH_KEY]!!)
  //   return questionnaireJson!!
  // }

  // private fun readFileFromAssets(filename: String): String {
  //   return getApplication<Application>().assets.open(filename).bufferedReader().use {
  //     it.readText()
  //   }
  // }

  private fun generateUuid(): String {
    return UUID.randomUUID().toString()
  }
}
