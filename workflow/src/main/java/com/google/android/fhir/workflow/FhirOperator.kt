/*
 * Copyright 2023-2024 Google LLC
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
import org.hl7.fhir.instance.model.api.IBaseBundle
import org.hl7.fhir.instance.model.api.IBaseDatatype
import org.hl7.fhir.instance.model.api.IBaseParameters
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.MeasureReport
import org.hl7.fhir.r4.model.Parameters
import org.hl7.fhir.r4.model.PlanDefinition
import org.hl7.fhir.r4.model.Reference
import org.opencds.cqf.fhir.cql.EvaluationSettings
import org.opencds.cqf.fhir.cql.LibraryEngine
import org.opencds.cqf.fhir.cr.measure.MeasureEvaluationOptions
import org.opencds.cqf.fhir.cr.measure.common.MeasureReportType
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
   * @param expressions names of expressions in the Library to evaluate. If null the result contains
   *   all evaluations or variables in library.
   * @return a Parameters resource that contains an evaluation result for each expression requested.
   *   Or if expressions param is null then result contains all evaluations or variables in given
   *   library.
   */
  @WorkerThread
  fun evaluateLibrary(libraryUrl: String, expressions: Set<String>?): IBaseParameters {
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
   * @param expressions names of expressions in the Library to evaluate. If null the result contains
   *   all evaluations or variables in library.
   * @return a Parameters resource that contains an evaluation result for each expression requested.
   *   Or if expressions param is null then result contains all evaluations or variables in given
   *   library.
   */
  @WorkerThread
  fun evaluateLibrary(
    libraryUrl: String,
    patientId: String,
    expressions: Set<String>?,
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
   * @param expressions names of expressions in the Library to evaluate. If null the result contains
   *   all evaluations or variables in library.
   * @return a Parameters resource that contains an evaluation result for each expression requested.
   *   Or if expressions param is null then result contains all evaluations or variables in given
   *   library.
   */
  @WorkerThread
  fun evaluateLibrary(
    libraryUrl: String,
    parameters: Parameters,
    expressions: Set<String>?,
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
   * @param expressions names of expressions in the Library to evaluate. If null the result contains
   *   all evaluations or variables in library.
   * @return a Parameters resource that contains an evaluation result for each expression requested.
   *   Or if expressions param is null then result contains all evaluations or variables in given
   *   library.
   */
  @WorkerThread
  fun evaluateLibrary(
    libraryUrl: String,
    patientId: String?,
    parameters: Parameters?,
    expressions: Set<String>?,
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
    subjectId: String? = null,
    practitioner: String? = null,
    additionalData: IBaseBundle? = null,
  ): MeasureReport {
    val subject =
      if (!practitioner.isNullOrBlank()) {
        checkAndAddType(practitioner, "Practitioner")
      } else if (!subjectId.isNullOrBlank()) {
        checkAndAddType(subjectId, "Patient")
      } else {
        // List of null is required to run population-level measures
        null
      }

    val report =
      measureProcessor.evaluateMeasure(
        /* measure = */ Eithers.forLeft3(CanonicalType(measureUrl)),
        /* periodStart = */ start,
        /* periodEnd = */ end,
        /* reportType = */ reportType,
        /* subjectIds = */ listOf(subject),
        /* additionalData = */ additionalData,
      )

    // add subject reference for non-individual reportTypes
    if (report.type.name == MeasureReportType.SUMMARY.name && !subject.isNullOrBlank()) {
      report.setSubject(Reference(subject))
    }
    return report
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
    encounterId: String? = null,
    practitionerId: String? = null,
    organizationId: String? = null,
    userType: IBaseDatatype? = null,
    userLanguage: IBaseDatatype? = null,
    userTaskContext: IBaseDatatype? = null,
    setting: IBaseDatatype? = null,
    settingContext: IBaseDatatype? = null,
    parameters: IBaseParameters? = null,
    useServerData: Boolean? = null,
    bundle: IBaseBundle? = null,
    prefetchData: IBaseParameters? = null,
  ): IBaseResource {
    return planDefinitionProcessor.apply(
      /* id = */ null,
      /* canonical = */ planDefinition,
      /* planDefinition = */ null,
      /* subject = */ subject,
      /* encounterId = */ encounterId,
      /* practitionerId = */ practitionerId,
      /* organizationId = */ organizationId,
      /* userType = */ userType,
      /* userLanguage = */ userLanguage,
      /* userTaskContext = */ userTaskContext,
      /* setting = */ setting,
      /* settingContext = */ settingContext,
      /* parameters = */ parameters,
      /* useServerData = */ useServerData,
      /* bundle = */ bundle,
      /* prefetchData = */ prefetchData,
      libraryProcessor,
    ) as IBaseResource
  }

  @WorkerThread
  fun generateCarePlan(
    planDefinition: PlanDefinition,
    subject: String,
    encounterId: String? = null,
    practitionerId: String? = null,
    organizationId: String? = null,
    userType: IBaseDatatype? = null,
    userLanguage: IBaseDatatype? = null,
    userTaskContext: IBaseDatatype? = null,
    setting: IBaseDatatype? = null,
    settingContext: IBaseDatatype? = null,
    parameters: IBaseParameters? = null,
    useServerData: Boolean? = null,
    bundle: IBaseBundle? = null,
    prefetchData: IBaseParameters? = null,
  ): IBaseResource {
    return planDefinitionProcessor.apply(
      /* id = */ null,
      /* canonical = */ null,
      /* planDefinition = */ planDefinition,
      /* subject = */ subject,
      /* encounterId = */ encounterId,
      /* practitionerId = */ practitionerId,
      /* organizationId = */ organizationId,
      /* userType = */ userType,
      /* userLanguage = */ userLanguage,
      /* userTaskContext = */ userTaskContext,
      /* setting = */ setting,
      /* settingContext = */ settingContext,
      /* parameters = */ parameters,
      /* useServerData = */ useServerData,
      /* bundle = */ bundle,
      /* prefetchData = */ prefetchData,
      libraryProcessor,
    ) as IBaseResource
  }

  /** Checks if the Resource ID contains a type and if not, adds a default type */
  fun checkAndAddType(id: String, defaultType: String): String {
    return if (id.indexOf("/") == -1) "$defaultType/$id" else id
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
