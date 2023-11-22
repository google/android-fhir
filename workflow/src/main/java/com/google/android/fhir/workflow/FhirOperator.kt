/*
 * Copyright 2023 Google LLC
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

import android.content.Context
import androidx.annotation.WorkerThread
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.knowledge.KnowledgeManager
import com.google.android.fhir.workflow.repositories.FhirEngineRepository
import com.google.android.fhir.workflow.repositories.KnowledgeRepository
import org.hl7.fhir.instance.model.api.IBaseParameters
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Measure
import org.hl7.fhir.r4.model.MeasureReport
import org.hl7.fhir.r4.model.Parameters
import org.opencds.cqf.fhir.cql.EvaluationSettings
import org.opencds.cqf.fhir.cql.LibraryEngine
import org.opencds.cqf.fhir.cr.measure.MeasureEvaluationOptions
import org.opencds.cqf.fhir.cr.measure.r4.R4MeasureProcessor
import org.opencds.cqf.fhir.cr.plandefinition.r4.PlanDefinitionProcessor
import org.opencds.cqf.fhir.utility.monad.Eithers
import org.opencds.cqf.fhir.utility.repository.ProxyRepository

class FhirOperator
internal constructor(
  fhirContext: FhirContext,
  fhirEngine: FhirEngine,
  knowledgeManager: KnowledgeManager,
) {
  init {
    require(fhirContext.version.version == FhirVersionEnum.R4) {
      "R4 is the only supported version by FhirOperator"
    }
  }

  private var dataRepo = FhirEngineRepository(fhirContext, fhirEngine)
  private var contentRepo = KnowledgeRepository(fhirContext, knowledgeManager)
  private var terminologyRepo = KnowledgeRepository(fhirContext, knowledgeManager)

  private val repository = ProxyRepository(dataRepo, contentRepo, terminologyRepo)
  private val evaluationSettings: EvaluationSettings = EvaluationSettings.getDefault()

  private val measureEvaluationOptions =
    MeasureEvaluationOptions().apply { evaluationSettings = this@FhirOperator.evaluationSettings }

  private val libraryProcessor = LibraryEngine(repository, evaluationSettings)

  private val measureProcessor = R4MeasureProcessor(repository, measureEvaluationOptions)
  private val planDefinitionProcessor = PlanDefinitionProcessor(repository, evaluationSettings)

  /**
   * The function evaluates a FHIR library against the database.
   *
   * NOTE: The API may internally result in a blocking IO operation. The user should call the API
   * from a worker thread or it may throw [BlockingMainThreadException] exception.
   *
   * @param libraryUrl the url of the Library to evaluate
   * @param expressions names of expressions in the Library to evaluate.
   * @return a Parameters resource that contains an evaluation result for each expression requested.
   */
  @WorkerThread
  fun evaluateLibrary(libraryUrl: String, expressions: Set<String>): IBaseParameters {
    return evaluateLibrary(libraryUrl, null, null, expressions)
  }

  /**
   * The function evaluates a FHIR library against a patient's records.
   *
   * NOTE: The API may internally result in a blocking IO operation. The user should call the API
   * from a worker thread or it may throw [BlockingMainThreadException] exception.
   *
   * @param libraryUrl the url of the Library to evaluate
   * @param patientId the Id of the patient to be evaluated
   * @param expressions names of expressions in the Library to evaluate.
   * @return a Parameters resource that contains an evaluation result for each expression requested
   */
  @WorkerThread
  fun evaluateLibrary(
    libraryUrl: String,
    patientId: String,
    expressions: Set<String>,
  ): IBaseParameters {
    return evaluateLibrary(libraryUrl, patientId, null, expressions)
  }

  /**
   * The function evaluates a FHIR library against the database.
   *
   * NOTE: The API may internally result in a blocking IO operation. The user should call the API
   * from a worker thread or it may throw [BlockingMainThreadException] exception.
   *
   * @param libraryUrl the url of the Library to evaluate
   * @param parameters list of parameters to be passed to the CQL library
   * @param expressions names of expressions in the Library to evaluate.
   * @return a Parameters resource that contains an evaluation result for each expression requested
   */
  @WorkerThread
  fun evaluateLibrary(
    libraryUrl: String,
    parameters: Parameters,
    expressions: Set<String>,
  ): IBaseParameters {
    return evaluateLibrary(libraryUrl, null, parameters, expressions)
  }

  /**
   * The function evaluates a FHIR library against the database.
   *
   * NOTE: The API may internally result in a blocking IO operation. The user should call the API
   * from a worker thread or it may throw [BlockingMainThreadException] exception.
   *
   * @param libraryUrl the url of the Library to evaluate
   * @param patientId the Id of the patient to be evaluated, if applicable
   * @param parameters list of parameters to be passed to the CQL library, if applicable
   * @param expressions names of expressions in the Library to evaluate.
   * @return a Parameters resource that contains an evaluation result for each expression requested
   */
  @WorkerThread
  fun evaluateLibrary(
    libraryUrl: String,
    patientId: String?,
    parameters: Parameters?,
    expressions: Set<String>,
  ): IBaseParameters {
    return libraryProcessor.evaluate(
      /* url = */ libraryUrl,
      /* patientId = */ patientId,
      /* parameters = */ parameters,
      /* additionalData = */ null,
      /* expressions = */ expressions,
    )
  }

  /**
   * Generates a [MeasureReport] based on the provided inputs.
   *
   * NOTE: The API may internally result in a blocking IO operation. The user should call the API
   * from a worker thread or it may throw [BlockingMainThreadException] exception.
   */
  @WorkerThread
  fun evaluateMeasure(
    measureUrl: String,
    start: String,
    end: String,
    reportType: String,
    subjectId: String?,
    practitioner: String?,
  ): MeasureReport {
    val measure = Eithers.forLeft3<CanonicalType, IdType, Measure>(CanonicalType(measureUrl))
    return measureProcessor.evaluateMeasure(
      /* measure = */ measure,
      /* periodStart = */ start,
      /* periodEnd = */ end,
      /* reportType = */ reportType,
      /* subjectIds = */ listOf(
        subjectId,
      ), // https://github.com/cqframework/clinical-reasoning/issues/358
      /* additionalData = */ null,
    )
  }

  /**
   * Generates a [CarePlan] based on the provided inputs.
   *
   * NOTE: The API may internally result in a blocking IO operation. The user should call the API
   * from a worker thread or it may throw [BlockingMainThreadException] exception.
   */
  @WorkerThread
  @Deprecated(
    "Use generateCarePlan with the planDefinition's url instead.",
    ReplaceWith("this.generateCarePlan(CanonicalType, String)"),
  )
  fun generateCarePlan(planDefinitionId: String, subject: String): IBaseResource {
    return generateCarePlan(planDefinitionId, subject, encounterId = null)
  }

  @WorkerThread
  fun generateCarePlan(planDefinition: CanonicalType, subject: String): IBaseResource {
    return generateCarePlan(planDefinition, subject, encounterId = null)
  }

  /**
   * Generates a [CarePlan] based on the provided inputs.
   *
   * NOTE: The API may internally result in a blocking IO operation. The user should call the API
   * from a worker thread or it may throw [BlockingMainThreadException] exception.
   */
  @WorkerThread
  @Deprecated(
    "Use generateCarePlan with the planDefinition's url instead.",
    ReplaceWith("this.generateCarePlan(CanonicalType, String, String)"),
  )
  fun generateCarePlan(
    planDefinitionId: String,
    subject: String,
    encounterId: String?,
  ): IBaseResource {
    return planDefinitionProcessor.apply(
      /* id = */ IdType("PlanDefinition", planDefinitionId),
      /* canonical = */ null,
      /* planDefinition = */ null,
      /* subject = */ subject,
      /* encounterId = */ encounterId,
      /* practitionerId = */ null,
      /* organizationId = */ null,
      /* userType = */ null,
      /* userLanguage = */ null,
      /* userTaskContext = */ null,
      /* setting = */ null,
      /* settingContext = */ null,
      /* parameters = */ null,
      /* useServerData = */ null,
      /* bundle = */ null,
      /* prefetchData = */ null,
      libraryProcessor,
    ) as IBaseResource
  }

  @WorkerThread
  fun generateCarePlan(
    planDefinition: CanonicalType,
    subject: String,
    encounterId: String?,
  ): IBaseResource {
    return planDefinitionProcessor.apply(
      /* id = */ null,
      /* canonical = */ planDefinition,
      /* planDefinition = */ null,
      /* subject = */ subject,
      /* encounterId = */ encounterId,
      /* practitionerId = */ null,
      /* organizationId = */ null,
      /* userType = */ null,
      /* userLanguage = */ null,
      /* userTaskContext = */ null,
      /* setting = */ null,
      /* settingContext = */ null,
      /* parameters = */ null,
      /* useServerData = */ null,
      /* bundle = */ null,
      /* prefetchData = */ null,
      libraryProcessor,
    ) as IBaseResource
  }

  class Builder(private val applicationContext: Context) {
    private var fhirContext: FhirContext? = null
    private var fhirEngine: FhirEngine? = null
    private var knowledgeManager: KnowledgeManager? = null

    fun fhirEngine(fhirEngine: FhirEngine) = apply { this.fhirEngine = fhirEngine }

    fun knowledgeManager(knowledgeManager: KnowledgeManager) = apply {
      this.knowledgeManager = knowledgeManager
    }

    fun fhirContext(fhirContext: FhirContext) = apply { this.fhirContext = fhirContext }

    fun build(): FhirOperator {
      return FhirOperator(
        fhirContext ?: FhirContext(FhirVersionEnum.R4),
        fhirEngine ?: FhirEngineProvider.getInstance(applicationContext),
        knowledgeManager ?: KnowledgeManager.create(applicationContext),
      )
    }
  }
}
