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

package com.google.android.fhir.workflow

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import java.util.function.Supplier
import org.hl7.fhir.instance.model.api.IBaseParameters
import org.hl7.fhir.r4.model.CarePlan
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Endpoint
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.MeasureReport
import org.hl7.fhir.r4.model.Parameters
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.fhir.converter.FhirTypeConverterFactory
import org.opencds.cqf.cql.engine.fhir.model.R4FhirModelResolver
import org.opencds.cqf.cql.evaluator.CqlOptions
import org.opencds.cqf.cql.evaluator.activitydefinition.r4.ActivityDefinitionProcessor
import org.opencds.cqf.cql.evaluator.builder.Constants
import org.opencds.cqf.cql.evaluator.builder.CqlEvaluatorBuilder
import org.opencds.cqf.cql.evaluator.builder.EndpointConverter
import org.opencds.cqf.cql.evaluator.builder.ModelResolverFactory
import org.opencds.cqf.cql.evaluator.builder.data.DataProviderFactory
import org.opencds.cqf.cql.evaluator.builder.data.FhirModelResolverFactory
import org.opencds.cqf.cql.evaluator.builder.data.TypedRetrieveProviderFactory
import org.opencds.cqf.cql.evaluator.builder.library.LibrarySourceProviderFactory
import org.opencds.cqf.cql.evaluator.builder.library.TypedLibrarySourceProviderFactory
import org.opencds.cqf.cql.evaluator.builder.terminology.TerminologyProviderFactory
import org.opencds.cqf.cql.evaluator.builder.terminology.TypedTerminologyProviderFactory
import org.opencds.cqf.cql.evaluator.cql2elm.util.LibraryVersionSelector
import org.opencds.cqf.cql.evaluator.engine.model.CachingModelResolverDecorator
import org.opencds.cqf.cql.evaluator.expression.ExpressionEvaluator
import org.opencds.cqf.cql.evaluator.fhir.adapter.r4.AdapterFactory
import org.opencds.cqf.cql.evaluator.library.CqlFhirParametersConverter
import org.opencds.cqf.cql.evaluator.library.LibraryProcessor
import org.opencds.cqf.cql.evaluator.measure.r4.R4MeasureProcessor
import org.opencds.cqf.cql.evaluator.plandefinition.r4.OperationParametersParser
import org.opencds.cqf.cql.evaluator.plandefinition.r4.PlanDefinitionProcessor

class FhirOperator(fhirContext: FhirContext, vararg providers: WorkflowApiProvider) {

  private val adapterFactory = AdapterFactory()
  private val fhirTypeConverter = FhirTypeConverterFactory().create(FhirVersionEnum.R4)

  private val terminologyProvider =
    CompoundTerminologyProvider(providers.map { it.terminologyProvider() })
  private val libraryContentProvider =
    CompoundLibraryContentProvider(providers.map { it.libraryContentProvider() })
  private val retrieveProvider = CompoundRetrieveProvider(providers.map { it.retrieveProvider() })
  private val dataProvider =
    CompositeDataProvider(CachingModelResolverDecorator(R4FhirModelResolver()), retrieveProvider)
  private val fhirDal = CompoundFhirDal(providers.map { it.fhirDal() })

  private val measureProcessor =
    R4MeasureProcessor(terminologyProvider, libraryContentProvider, dataProvider, fhirDal)

  // Initialize the plan definition processor
  private val cqlFhirParameterConverter =
    CqlFhirParametersConverter(fhirContext, adapterFactory, fhirTypeConverter)
  private val libraryContentProviderFactory =
    LibrarySourceProviderFactory(
      fhirContext,
      adapterFactory,
      hashSetOf<TypedLibrarySourceProviderFactory>(
        object : TypedLibrarySourceProviderFactory {
          override fun getType() = Constants.HL7_FHIR_FILES
          override fun create(url: String?, headers: MutableList<String>?) = libraryContentProvider
        }
      ),
      LibraryVersionSelector(adapterFactory)
    )
  private val fhirModelResolverFactory = FhirModelResolverFactory()

  private val dataProviderFactory =
    DataProviderFactory(
      fhirContext,
      hashSetOf<ModelResolverFactory>(fhirModelResolverFactory),
      hashSetOf<TypedRetrieveProviderFactory>(
        object : TypedRetrieveProviderFactory {
          override fun getType() = Constants.HL7_FHIR_FILES
          override fun create(url: String?, headers: MutableList<String>?) = retrieveProvider
        }
      )
    )
  private val terminologyProviderFactory =
    TerminologyProviderFactory(
      fhirContext,
      hashSetOf<TypedTerminologyProviderFactory>(
        object : TypedTerminologyProviderFactory {
          override fun getType() = Constants.HL7_FHIR_FILES
          override fun create(url: String?, headers: MutableList<String>?) = terminologyProvider
        }
      )
    )
  private val endpointConverter = EndpointConverter(adapterFactory)

  private val evaluatorBuilderSupplier = Supplier {
    CqlEvaluatorBuilder()
      .withLibrarySourceProvider(libraryContentProvider)
      .withCqlOptions(CqlOptions.defaultOptions())
      .withTerminologyProvider(terminologyProvider)
  }

  private val libraryProcessor =
    LibraryProcessor(
      fhirContext,
      cqlFhirParameterConverter,
      libraryContentProviderFactory,
      dataProviderFactory,
      terminologyProviderFactory,
      endpointConverter,
      fhirModelResolverFactory,
      evaluatorBuilderSupplier
    )

  private val expressionEvaluator =
    ExpressionEvaluator(
      fhirContext,
      cqlFhirParameterConverter,
      libraryContentProviderFactory,
      dataProviderFactory,
      terminologyProviderFactory,
      endpointConverter,
      fhirModelResolverFactory,
      evaluatorBuilderSupplier
    )

  private val activityDefinitionProcessor =
    ActivityDefinitionProcessor(fhirContext, fhirDal, libraryProcessor)
  private val operationParametersParser =
    OperationParametersParser(adapterFactory, fhirTypeConverter)

  private val planDefinitionProcessor =
    PlanDefinitionProcessor(
      fhirContext,
      fhirDal,
      libraryProcessor,
      expressionEvaluator,
      activityDefinitionProcessor,
      operationParametersParser
    )

  /**
   * The function evaluates a FHIR library against a patient's records.
   * @param libraryUrl the url of the Library to evaluate
   * @param patientId the Id of the patient to be evaluated
   * @param expressions names of expressions in the Library to evaluate.
   * @return a Parameters resource that contains an evaluation result for each expression requested
   */
  fun evaluateLibrary(
    libraryUrl: String,
    patientId: String,
    expressions: Set<String>
  ): IBaseParameters {
    val dataEndpoint =
      Endpoint()
        .setAddress("localhost")
        .setConnectionType(Coding().setCode(Constants.HL7_FHIR_FILES))

    return libraryProcessor.evaluate(
      libraryUrl,
      patientId,
      null,
      null,
      null,
      dataEndpoint,
      null,
      expressions
    )
  }

  fun evaluateMeasure(
    measureUrl: String,
    start: String,
    end: String,
    reportType: String,
    subject: String?,
    practitioner: String?,
    lastReceivedOn: String?
  ): MeasureReport {
    return measureProcessor.evaluateMeasure(
      measureUrl,
      start,
      end,
      reportType,
      subject,
      practitioner,
      lastReceivedOn,
      /* contentEndpoint= */ null,
      /* terminologyEndpoint= */ null,
      /* dataEndpoint= */ null,
      /* additionalData= */ null
    )
  }

  fun generateCarePlan(planDefinitionId: String, patientId: String, encounterId: String): CarePlan {
    return planDefinitionProcessor.apply(
      IdType("PlanDefinition", planDefinitionId),
      patientId,
      encounterId,
      /* practitionerId= */ null,
      /* organizationId= */ null,
      /* userType= */ null,
      /* userLanguage= */ null,
      /* userTaskContext= */ null,
      /* setting= */ null,
      /* settingContext= */ null,
      /* mergeNestedCarePlans= */ null,
      /* parameters= */ Parameters(),
      /* useServerData= */ null,
      /* bundle= */ null,
      /* prefetchData= */ null,
      /* dataEndpoint= */ null,
      /* contentEndpoint*/ null,
      /* terminologyEndpoint= */ null
    )
  }
}
