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

package com.google.android.fhir.datacapture.fhirpath

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import org.hl7.fhir.r4.hapi.ctx.HapiWorkerContext
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.ExpressionNode
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemComponent
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.utils.FHIRPathEngine

private val fhirPathEngine: FHIRPathEngine =
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

/** Evaluates the expression over resource [Resource] and returns string value */
internal fun evaluateToString(
  expression: ExpressionNode,
  data: Resource?,
  contextMap: Map<String, Base?>,
) =
  fhirPathEngine.evaluateToString(
    /* appInfo = */ contextMap,
    /* focusResource = */ null,
    /* rootResource = */ null,
    /* base = */ data,
    /* node = */ expression,
  )

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
  expression: String,
  contextMap: Map<String, Base?> = mapOf(),
): Boolean {
  val expressionNode = fhirPathEngine.parse(expression)
  return fhirPathEngine.evaluateToBoolean(
    contextMap,
    questionnaireResponse,
    null,
    questionnaireResponseItemComponent,
    expressionNode,
  )
}

/**
 * Evaluates the expression and returns the list of [Base]. The resources [QuestionnaireResponse]
 * and [QuestionnaireResponseItemComponent] are passed as fhirPath supplements as defined in fhir
 * specs https://build.fhir.org/ig/HL7/sdc/expressions.html#fhirpath-supplements. All other
 * constants are passed as contextMap
 *
 * %resource = [QuestionnaireResponse], %context = [QuestionnaireResponseItemComponent]
 */
internal fun evaluateToBase(
  questionnaireResponse: QuestionnaireResponse?,
  questionnaireResponseItem: QuestionnaireResponseItemComponent?,
  expression: String,
  contextMap: Map<String, Base?> = mapOf(),
): List<Base> {
  return fhirPathEngine.evaluate(
    /* appContext = */ contextMap,
    /* focusResource = */ questionnaireResponse,
    /* rootResource = */ null,
    /* base = */ questionnaireResponseItem,
    /* path = */ expression,
  )
}

/** Evaluates the given expression and returns list of [Base] */
internal fun evaluateToBase(type: Type, expression: String): List<Base> {
  return fhirPathEngine.evaluate(
    /* base = */ type,
    /* path = */ expression,
  )
}

/** Evaluates the given list of [Base] elements and returns boolean result */
internal fun convertToBoolean(items: List<Base>) = fhirPathEngine.convertToBoolean(items)

/** Parse the given expression into [ExpressionNode] */
internal fun extractExpressionNode(fhirPath: String) = fhirPathEngine.parse(fhirPath)
