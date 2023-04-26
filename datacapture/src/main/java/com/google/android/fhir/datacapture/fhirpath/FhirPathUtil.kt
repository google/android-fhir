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

package com.google.android.fhir.datacapture.fhirpath

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import org.hl7.fhir.r4.hapi.ctx.HapiWorkerContext
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemComponent
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.utils.FHIRPathEngine

internal val fhirPathEngine: FHIRPathEngine =
  with(FhirContext.forCached(FhirVersionEnum.R4)) {
    FHIRPathEngine(HapiWorkerContext(this, this.validationSupport)).apply {
      hostServices = FHIRPathEngineHostServices
    }
  }

/**
 * Evaluates the expressions over list of resources [Resource] and joins to space separated string
 */
internal fun evaluateToDisplay(expressions: List<String>, data: Resource) =
  expressions.joinToString(" ") { fhirPathEngine.evaluateToString(data, it) }

/**
 * Evaluates the expression and returns the boolean result. The resources [QuestionnaireResponse]
 * and [QuestionnaireResponseItemComponent] are passed as fhirPath supplements as defined in fhir
 * specs https://build.fhir.org/ig/HL7/sdc/expressions.html#fhirpath-supplements
 *
 * %resource = [QuestionnaireResponse], %context = [QuestionnaireResponseItemComponent]
 */
internal fun evaluateToBoolean(
  questionnaireResponse: QuestionnaireResponse,
  questionnaireResponseItemComponent: QuestionnaireResponseItemComponent,
  expression: String
) =
  fhirPathEngine.evaluateToBoolean(
    questionnaireResponse,
    null,
    questionnaireResponseItemComponent,
    expression
  )
