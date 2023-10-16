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

package com.google.android.fhir.workflow.testing

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.rest.api.EncodingEnum
import java.io.IOException
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.instance.model.api.IPrimitiveType
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.CarePlan
import org.hl7.fhir.r4.model.CommunicationRequest
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Parameters
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.json.JSONException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.opencds.cqf.fhir.api.Repository
import org.opencds.cqf.fhir.cql.EvaluationSettings
import org.opencds.cqf.fhir.cql.LibraryEngine
import org.opencds.cqf.fhir.cr.plandefinition.r4.PlanDefinitionProcessor
import org.opencds.cqf.fhir.utility.repository.IGLayoutMode
import org.opencds.cqf.fhir.utility.repository.InMemoryFhirRepository
import org.opencds.cqf.fhir.utility.repository.Repositories
import org.skyscreamer.jsonassert.JSONAssert

object PlanDefinition : Loadable() {
  private val fhirContext = FhirContext.forCached(FhirVersionEnum.R4)
  private val jsonParser = fhirContext.newJsonParser()
  private val evaluationSettings = EvaluationSettings.getDefault()

  fun parse(assetName: String): IBaseResource {
    return jsonParser.parseResource(open(assetName))
  }

  fun buildProcessor(repository: Repository): PlanDefinitionProcessor {
    val evaluationSettings: EvaluationSettings = EvaluationSettings.getDefault()

    return PlanDefinitionProcessor(repository, evaluationSettings)
  }

  object Assert {
    fun that(
      planDefinitionID: String,
      patientID: String?,
      encounterID: String? = null,
      practitionerID: String? = null,
    ) = Apply(planDefinitionID, patientID, encounterID, practitionerID)
  }

  class Apply(
    private val planDefinitionID: String,
    private val patientID: String?,
    private val encounterID: String?,
    private val practitionerID: String?,
  ) {
    private var overrideRepository: Repository? = null
    private var dataRepository: Repository? = null
    private var contentRepository: Repository? = null
    private var terminologyRepository: Repository? = null
    private var additionalData: Bundle? = null
    private var additionalDataId: IdType? = null
    private var parameters: Parameters? = null
    private var expectedBundleId: IdType? = null
    private var expectedCarePlanId: IdType? = null
    private var repositoryPath: String? = null

    fun withData(dataAssetName: String): Apply {
      dataRepository = InMemoryFhirRepository(fhirContext, parse(dataAssetName) as Bundle)
      return this
    }

    fun withContent(dataAssetName: String): Apply {
      contentRepository = InMemoryFhirRepository(fhirContext, parse(dataAssetName) as Bundle)
      return this
    }

    fun withTerminology(dataAssetName: String): Apply {
      terminologyRepository = InMemoryFhirRepository(fhirContext, parse(dataAssetName) as Bundle)
      return this
    }

    private fun loadAdditionalData(resource: IBaseResource) {
      additionalData =
        if (resource.idElement.resourceType == Enumerations.FHIRAllTypes.BUNDLE.toCode()) {
          resource as Bundle
        } else {
          Bundle()
            .setType(Bundle.BundleType.COLLECTION)
            .addEntry(Bundle.BundleEntryComponent().setResource(resource as Resource))
        }
    }

    fun withAdditionalData(dataAssetName: String): Apply {
      val data = parse(dataAssetName)
      loadAdditionalData(data)
      return this
    }

    fun withAdditionalDataId(id: IdType): Apply {
      additionalDataId = id
      return this
    }

    fun withRepositoryPath(path: String): Apply {
      repositoryPath = path
      return this
    }

    fun withParameters(params: Parameters): Apply {
      parameters = params
      return this
    }

    fun withRepository(repository: Repository): Apply {
      this.overrideRepository = repository
      return this
    }

    fun withExpectedBundleId(id: IdType): Apply {
      expectedBundleId = id
      return this
    }

    fun withExpectedCarePlanId(id: IdType): Apply {
      expectedCarePlanId = id
      return this
    }

    private fun buildRepository(): Repository {
      val local =
        IGInputStreamStructureRepository(
          fhirContext,
          repositoryPath ?: ".",
          IGLayoutMode.TYPE_PREFIX,
          EncodingEnum.JSON,
        )
      if (dataRepository == null && contentRepository == null && terminologyRepository == null) {
        return local
      }
      if (dataRepository == null) {
        dataRepository = local
      }
      if (contentRepository == null) {
        contentRepository = local
      }
      if (terminologyRepository == null) {
        terminologyRepository = local
      }
      return Repositories.proxy(dataRepository, contentRepository, terminologyRepository)
    }

    fun applyR5(): GeneratedBundle {
      val repository = overrideRepository ?: buildRepository()

      val libraryEngine = LibraryEngine(repository, evaluationSettings)

      val expectedBundle =
        if (expectedBundleId != null) {
          try {
            repository.read(
              Bundle::class.java,
              expectedBundleId,
            )
          } catch (e: java.lang.Exception) {
            null
          }
        } else {
          null
        }

      additionalDataId?.let {
        loadAdditionalData(
          repository.read(
            fhirContext.getResourceDefinition(it.resourceType).newInstance().javaClass,
            additionalDataId,
          ),
        )
      }

      return GeneratedBundle(
        buildProcessor(repository)
          .applyR5<IPrimitiveType<String>>(
            /* id = */ IdType("PlanDefinition", planDefinitionID),
            /* canonical = */ null,
            /* planDefinition = */ null,
            /* patientId = */ patientID,
            /* encounterId = */ encounterID,
            /* practitionerId = */ practitionerID,
            /* organizationId = */ null,
            /* userType = */ null,
            /* userLanguage = */ null,
            /* userTaskContext = */ null,
            /* setting = */ null,
            /* settingContext = */ null,
            /* parameters = */ parameters,
            /* useServerData = */ null,
            /* bundle = */ additionalData,
            /* prefetchData = */ null,
            /* libraryEngine = */ libraryEngine,
          ) as Bundle,
        expectedBundle,
      )
    }

    fun apply(): GeneratedCarePlan {
      val repository = overrideRepository ?: buildRepository()

      val libraryEngine = LibraryEngine(repository, evaluationSettings)

      val expectedCarePlan =
        if (expectedCarePlanId != null) {
          try {
            repository.read(
              CarePlan::class.java,
              expectedCarePlanId,
            )
          } catch (e: java.lang.Exception) {
            null
          }
        } else {
          null
        }

      additionalDataId?.let {
        loadAdditionalData(
          repository.read(
            fhirContext.getResourceDefinition(it.resourceType).newInstance().javaClass,
            additionalDataId,
          ),
        )
      }

      return GeneratedCarePlan(
        (buildProcessor(repository)
          .apply<IPrimitiveType<String>>(
            IdType("PlanDefinition", planDefinitionID),
            null,
            null,
            patientID,
            encounterID,
            practitionerID,
            null,
            null,
            null,
            null,
            null,
            null,
            parameters,
            null,
            additionalData,
            null,
            libraryEngine,
          ) as CarePlan),
        expectedCarePlan,
      )
    }

    fun packagePlanDefinition(): GeneratedPackage {
      val repository = overrideRepository ?: buildRepository()
      return GeneratedPackage(
        (buildProcessor(repository)
          .packagePlanDefinition<IPrimitiveType<String>>(
            IdType("PlanDefinition", planDefinitionID),
            null,
            null,
            true,
          ) as Bundle),
        null,
      )
    }
  }

  class GeneratedBundle(var generatedBundle: Bundle, var expectedBundle: Bundle?) {
    fun isEqualsTo(expectedBundleAssetName: String?) {
      try {
        JSONAssert.assertEquals(
          load(expectedBundleAssetName!!),
          jsonParser.encodeResourceToString(generatedBundle),
          true,
        )
      } catch (e: JSONException) {
        e.printStackTrace()
        fail("Unable to compare Jsons: " + e.message)
      } catch (e: IOException) {
        e.printStackTrace()
        fail("Unable to compare Jsons: " + e.message)
      }
    }

    fun equalsToExpected() {
      try {
        JSONAssert.assertEquals(
          jsonParser.encodeResourceToString(expectedBundle),
          jsonParser.encodeResourceToString(generatedBundle),
          true,
        )
      } catch (e: JSONException) {
        e.printStackTrace()
        fail("Unable to compare Jsons: " + e.message)
      }
    }

    fun hasEntry(count: Int) {
      assertEquals(count, generatedBundle.entry.size)
    }

    fun hasCommunicationRequestPayload() {
      assertTrue(
        generatedBundle.entry
          .stream()
          .filter { e: Bundle.BundleEntryComponent ->
            e.resource.resourceType == ResourceType.CommunicationRequest
          }
          .map { e: Bundle.BundleEntryComponent -> e.resource as CommunicationRequest }
          .allMatch { c -> c.hasPayload() },
      )
    }

    fun hasQuestionnaireOperationOutcome() {
      assertTrue(
        generatedBundle.entry
          .stream()
          .map<Resource> { e: Bundle.BundleEntryComponent -> e.resource }
          .anyMatch { r: Resource ->
            r.resourceType == ResourceType.Questionnaire &&
              (r as Questionnaire).getContained().stream().anyMatch { c ->
                c.getResourceType().equals(ResourceType.OperationOutcome)
              }
          },
      )
    }
  }

  class GeneratedCarePlan(val generatedCarePlan: CarePlan, val expectedCarePlan: CarePlan?) {
    fun isEqualsTo(expectedCarePlanAssetName: String) {
      try {
        JSONAssert.assertEquals(
          load(expectedCarePlanAssetName),
          jsonParser.encodeResourceToString(generatedCarePlan),
          true,
        )
      } catch (e: JSONException) {
        e.printStackTrace()
        fail("Unable to compare Jsons: " + e.message)
      } catch (e: IOException) {
        e.printStackTrace()
        fail("Unable to compare Jsons: " + e.message)
      }
    }

    fun equalsToExpected() {
      try {
        println(jsonParser.encodeResourceToString(generatedCarePlan))

        JSONAssert.assertEquals(
          jsonParser.encodeResourceToString(expectedCarePlan),
          jsonParser.encodeResourceToString(generatedCarePlan),
          true,
        )
      } catch (e: JSONException) {
        e.printStackTrace()
        fail("Unable to compare Jsons: " + e.message)
      }
    }

    fun hasContained(count: Int) {
      assertEquals(count, generatedCarePlan.contained.size)
    }

    fun hasOperationOutcome() {
      assertTrue(
        generatedCarePlan.getContained().stream().anyMatch { r ->
          r.resourceType.equals(ResourceType.OperationOutcome)
        },
      )
    }
  }

  class GeneratedPackage(val generatedBundle: Bundle, val expectedBundle: Bundle?) {
    fun hasEntry(count: Int) {
      assertEquals(count, generatedBundle.entry.size)
    }
  }
}
