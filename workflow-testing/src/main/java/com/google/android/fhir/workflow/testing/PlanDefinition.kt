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
import org.hl7.fhir.instance.model.api.IBaseBundle
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Endpoint
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Parameters
import org.json.JSONException
import org.junit.Assert.fail
import org.opencds.cqf.cql.engine.fhir.converter.FhirTypeConverterFactory
import org.opencds.cqf.cql.evaluator.activitydefinition.r4.ActivityDefinitionProcessor
import org.opencds.cqf.cql.evaluator.builder.Constants
import org.opencds.cqf.cql.evaluator.builder.CqlEvaluatorBuilder
import org.opencds.cqf.cql.evaluator.builder.EndpointConverter
import org.opencds.cqf.cql.evaluator.builder.data.DataProviderFactory
import org.opencds.cqf.cql.evaluator.builder.data.FhirModelResolverFactory
import org.opencds.cqf.cql.evaluator.builder.data.TypedRetrieveProviderFactory
import org.opencds.cqf.cql.evaluator.builder.library.LibrarySourceProviderFactory
import org.opencds.cqf.cql.evaluator.builder.library.TypedLibrarySourceProviderFactory
import org.opencds.cqf.cql.evaluator.builder.terminology.TerminologyProviderFactory
import org.opencds.cqf.cql.evaluator.builder.terminology.TypedTerminologyProviderFactory
import org.opencds.cqf.cql.evaluator.cql2elm.content.fhir.BundleFhirLibrarySourceProvider
import org.opencds.cqf.cql.evaluator.cql2elm.util.LibraryVersionSelector
import org.opencds.cqf.cql.evaluator.engine.retrieve.BundleRetrieveProvider
import org.opencds.cqf.cql.evaluator.engine.terminology.BundleTerminologyProvider
import org.opencds.cqf.cql.evaluator.expression.ExpressionEvaluator
import org.opencds.cqf.cql.evaluator.fhir.adapter.r4.AdapterFactory
import org.opencds.cqf.cql.evaluator.fhir.dal.FhirDal
import org.opencds.cqf.cql.evaluator.library.CqlFhirParametersConverter
import org.opencds.cqf.cql.evaluator.library.LibraryProcessor
import org.opencds.cqf.cql.evaluator.plandefinition.OperationParametersParser
import org.opencds.cqf.cql.evaluator.plandefinition.r4.PlanDefinitionProcessor
import org.skyscreamer.jsonassert.JSONAssert

object PlanDefinition : Loadable() {
  private val fhirContext = FhirContext.forCached(FhirVersionEnum.R4)
  private val jsonParser = fhirContext.newJsonParser()

  fun parse(assetName: String): IBaseResource {
    return jsonParser.parseResource(open(assetName))
  }

  fun buildProcessor(fhirDal: FhirDal): PlanDefinitionProcessor {
    val adapterFactory = AdapterFactory()
    val libraryVersionSelector = LibraryVersionSelector(adapterFactory)
    val fhirTypeConverter = FhirTypeConverterFactory().create(fhirContext.version.version)
    val cqlFhirParametersConverter =
      CqlFhirParametersConverter(fhirContext, adapterFactory, fhirTypeConverter)

    val fhirModelResolverFactory = FhirModelResolverFactory()

    val librarySourceProviderFactories =
      setOf<TypedLibrarySourceProviderFactory>(
        object : TypedLibrarySourceProviderFactory {
          override fun getType() = Constants.HL7_FHIR_FILES

          override fun create(url: String, headers: List<String>?) =
            BundleFhirLibrarySourceProvider(
              fhirContext,
              parse(url) as IBaseBundle,
              adapterFactory,
              libraryVersionSelector,
            )
        },
      )

    val librarySourceProviderFactory =
      LibrarySourceProviderFactory(
        fhirContext,
        adapterFactory,
        librarySourceProviderFactories,
        libraryVersionSelector,
      )

    val retrieveProviderFactories =
      setOf<TypedRetrieveProviderFactory>(
        object : TypedRetrieveProviderFactory {
          override fun getType() = Constants.HL7_FHIR_FILES

          override fun create(url: String, headers: List<String>?) =
            BundleRetrieveProvider(fhirContext, parse(url) as IBaseBundle)
        },
      )

    val dataProviderFactory =
      DataProviderFactory(fhirContext, setOf(fhirModelResolverFactory), retrieveProviderFactories)

    val typedTerminologyProviderFactories =
      setOf<TypedTerminologyProviderFactory>(
        object : TypedTerminologyProviderFactory {
          override fun getType() = Constants.HL7_FHIR_FILES

          override fun create(url: String, headers: List<String>?) =
            BundleTerminologyProvider(fhirContext, parse(url) as IBaseBundle)
        },
      )

    val terminologyProviderFactory =
      TerminologyProviderFactory(fhirContext, typedTerminologyProviderFactories)

    val endpointConverter = EndpointConverter(adapterFactory)

    val libraryProcessor =
      LibraryProcessor(
        fhirContext,
        cqlFhirParametersConverter,
        librarySourceProviderFactory,
        dataProviderFactory,
        terminologyProviderFactory,
        endpointConverter,
        fhirModelResolverFactory,
      ) {
        CqlEvaluatorBuilder()
      }

    val evaluator =
      ExpressionEvaluator(
        fhirContext,
        cqlFhirParametersConverter,
        librarySourceProviderFactory,
        dataProviderFactory,
        terminologyProviderFactory,
        endpointConverter,
        fhirModelResolverFactory,
      ) {
        CqlEvaluatorBuilder()
      }

    val activityDefProcessor = ActivityDefinitionProcessor(fhirContext, fhirDal, libraryProcessor)
    val operationParametersParser = OperationParametersParser(adapterFactory, fhirTypeConverter)

    return PlanDefinitionProcessor(
      fhirContext,
      fhirDal,
      libraryProcessor,
      evaluator,
      activityDefProcessor,
      operationParametersParser,
    )
  }

  object Assert {
    fun that(planDefinitionID: String, patientID: String, encounterID: String?) =
      Apply(planDefinitionID, patientID, encounterID)

    fun that(planDefinitionID: String, patientID: String) = Apply(planDefinitionID, patientID, null)
  }

  class Apply(
    private val planDefinitionID: String,
    private val patientID: String?,
    private val encounterID: String?,
  ) {
    private val fhirDal = FakeFhirDal()
    private lateinit var dataEndpoint: Endpoint
    private lateinit var libraryEndpoint: Endpoint
    private lateinit var baseResource: IBaseResource

    fun withData(dataAssetName: String): Apply {
      dataEndpoint =
        Endpoint()
          .setAddress(dataAssetName)
          .setConnectionType(Coding().setCode(Constants.HL7_FHIR_FILES))
      baseResource = parse(dataAssetName)

      fhirDal.addAll(baseResource)
      return this
    }

    fun withLibrary(libraryAssetName: String): Apply {
      libraryEndpoint =
        Endpoint()
          .setAddress(libraryAssetName)
          .setConnectionType(Coding().setCode(Constants.HL7_FHIR_FILES))

      fhirDal.addAll(parse(libraryAssetName))
      return this
    }

    fun apply(): GeneratedCarePlan {
      return GeneratedCarePlan(
        buildProcessor(fhirDal)
          .apply(
            IdType("PlanDefinition", planDefinitionID),
            patientID,
            encounterID,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            Parameters(),
            null,
            baseResource as Bundle,
            null,
            dataEndpoint,
            libraryEndpoint,
            libraryEndpoint,
          ),
      )
    }
  }

  class GeneratedCarePlan(val carePlan: IBaseResource) {
    fun isEqualsTo(expectedCarePlanAssetName: String) {
      try {
        JSONAssert.assertEquals(
          load(expectedCarePlanAssetName),
          jsonParser.encodeResourceToString(carePlan),
          true,
        )
      } catch (e: JSONException) {
        e.printStackTrace()
        fail("Unable to compare Jsons: " + e.message)
      } catch (e: AssertionError) {
        println("Actual: " + jsonParser.encodeResourceToString(carePlan))
        throw e
      }
    }
  }
}
