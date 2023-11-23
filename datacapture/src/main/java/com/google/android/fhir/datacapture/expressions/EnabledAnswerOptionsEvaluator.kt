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

package com.google.android.fhir.datacapture.expressions

import com.google.android.fhir.datacapture.ExternalAnswerValueSetResolver
import com.google.android.fhir.datacapture.XFhirQueryResolver
import com.google.android.fhir.datacapture.extensions.answerExpression
import com.google.android.fhir.datacapture.extensions.answerOptionsToggleExpressions
import com.google.android.fhir.datacapture.extensions.extractAnswerOptions
import com.google.android.fhir.datacapture.extensions.isFhirPath
import com.google.android.fhir.datacapture.extensions.isXFhirQuery
import com.google.android.fhir.datacapture.fhirpath.ExpressionEvaluator
import com.google.android.fhir.datacapture.fhirpath.fhirPathEngine
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemComponent
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.ValueSet

/**
 * Evaluates and manages answer options within a [Questionnaire] and its corresponding
 * [QuestionnaireResponse]. It handles enablement, disablement, and presentation of options based on
 * expressions and criteria.
 *
 * The evaluator works in the context of a [Questionnaire] and the corresponding
 * [QuestionnaireResponse]. It is the caller's responsibility to make sure to call the evaluator
 * with [QuestionnaireItemComponent] and [QuestionnaireResponseItemComponent] that belong to the
 * [Questionnaire] and the [QuestionnaireResponse].
 *
 * @param questionnaire the [Questionnaire] where the expression belong to
 * @param questionnaireResponse the [QuestionnaireResponse] related to the [Questionnaire]
 * @param xFhirQueryResolver the [XFhirQueryResolver] to resolve resources based on the X-FHIR-Query
 * @param externalValueSetResolver the [ExternalAnswerValueSetResolver] to resolve value sets
 *   externally/outside of the [Questionnaire]
 * @param questionnaireItemParentMap the [Map] of items parent
 * @param questionnaireLaunchContextMap the [Map] of launchContext names to their resource values
 */
internal class EnabledAnswerOptionsEvaluator(
  private val questionnaire: Questionnaire,
  private val questionnaireResponse: QuestionnaireResponse,
  private val questionnaireItemParentMap:
    Map<QuestionnaireItemComponent, QuestionnaireItemComponent> =
    emptyMap(),
  private val questionnaireLaunchContextMap: Map<String, Resource>? = emptyMap(),
  private val xFhirQueryResolver: XFhirQueryResolver? = null,
  private val externalValueSetResolver: ExternalAnswerValueSetResolver? = null,
) {

  private val expressionEvaluator =
    ExpressionEvaluator(
      questionnaire,
      questionnaireResponse,
      questionnaireItemParentMap,
      questionnaireLaunchContextMap,
      xFhirQueryResolver,
    )

  private val answerValueSetMap =
    mutableMapOf<String, List<Questionnaire.QuestionnaireItemAnswerOptionComponent>>()

  /**
   * The answer expression referencing an x-fhir-query has its evaluated data cached to avoid
   * reloading resources unnecessarily. The value is updated each time an item with answer
   * expression is evaluating the latest answer options.
   */
  private val answerExpressionMap =
    mutableMapOf<String, List<Questionnaire.QuestionnaireItemAnswerOptionComponent>>()

  /**
   * Returns a [Pair] of the enabled/allowed [Questionnaire.QuestionnaireItemAnswerOptionComponent]
   * options and the disabled/disallowed
   * [QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent] answers, based on the
   * evaluation of [Questionnaire.QuestionnaireItemComponent.answerOptionsToggleExpressions]
   * expressions
   */
  internal suspend fun evaluate(
    questionnaireItem: QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponseItemComponent,
  ): Pair<
    List<Questionnaire.QuestionnaireItemAnswerOptionComponent>,
    List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>,
  > {
    val resolvedAnswerOptions =
      answerOptions(
        questionnaireItem,
        questionnaireResponseItem,
      )

    if (questionnaireItem.answerOptionsToggleExpressions.isEmpty()) {
      return Pair(resolvedAnswerOptions, emptyList())
    }

    val enabledQuestionnaireAnswerOptions =
      evaluateAnswerOptionsToggleExpressions(
        questionnaireItem,
        questionnaireResponseItem,
        resolvedAnswerOptions,
      )
    val disabledAnswers =
      questionnaireResponseItem.answer
        .takeIf { it.isNotEmpty() }
        ?.filterNot { ans ->
          enabledQuestionnaireAnswerOptions.any { ans.value.equalsDeep(it.value) }
        }
        ?: emptyList()
    return Pair(enabledQuestionnaireAnswerOptions, disabledAnswers)
  }

  /**
   * In a `choice` or `open-choice` type question, the answer options are defined in one of the
   * three elements in the questionnaire:
   * - `Questionnaire.item.answerOption`: a list of permitted answers to the question
   * - `Questionnaire.item.answerValueSet`: a reference to a value set containing a list of
   *   permitted answers to the question
   * - `Extension answer-expression`: an expression based extension which defines the x-fhir-query
   *   or fhirpath to evaluate permitted answer options
   *
   * Returns the answer options defined in one of the sources above. If the answer options are
   * defined in `Questionnaire.item.answerValueSet`, the answer value set will be expanded.
   */
  private suspend fun answerOptions(
    questionnaireItem: QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponseItemComponent,
  ): List<Questionnaire.QuestionnaireItemAnswerOptionComponent> =
    when {
      questionnaireItem.answerOption.isNotEmpty() -> questionnaireItem.answerOption
      !questionnaireItem.answerValueSet.isNullOrEmpty() ->
        resolveAnswerValueSet(questionnaireItem.answerValueSet)
      questionnaireItem.answerExpression != null ->
        resolveAnswerExpression(
          questionnaireItem,
          questionnaireResponseItem,
        )
      else -> emptyList()
    }

  private suspend fun resolveAnswerValueSet(
    uri: String,
  ): List<Questionnaire.QuestionnaireItemAnswerOptionComponent> {
    // If cache hit, return it
    if (answerValueSetMap.contains(uri)) {
      return answerValueSetMap[uri]!!
    }

    val options =
      if (uri.startsWith("#")) {
        questionnaire.contained
          .firstOrNull { resource ->
            resource.id.equals(uri) &&
              resource.resourceType == ResourceType.ValueSet &&
              (resource as ValueSet).hasExpansion()
          }
          ?.let { resource ->
            val valueSet = resource as ValueSet
            valueSet.expansion.contains
              .filterNot { it.abstract || it.inactive }
              .map { component ->
                Questionnaire.QuestionnaireItemAnswerOptionComponent(
                  Coding(component.system, component.code, component.display),
                )
              }
          }
      } else {
        // Ask the client to provide the answers from an external expanded Valueset.
        externalValueSetResolver?.resolve(uri)?.map { coding ->
          Questionnaire.QuestionnaireItemAnswerOptionComponent(coding.copy())
        }
      }
        ?: emptyList()
    // save it so that we avoid have cache misses.
    answerValueSetMap[uri] = options
    return options
  }

  // TODO persist previous answers in case options are changing and new list does not have selected
  // answer and FHIRPath in x-fhir-query
  // https://build.fhir.org/ig/HL7/sdc/expressions.html#x-fhir-query-enhancements
  private suspend fun resolveAnswerExpression(
    item: QuestionnaireItemComponent,
    responseItem: QuestionnaireResponseItemComponent,
  ): List<Questionnaire.QuestionnaireItemAnswerOptionComponent> {
    // Check cache first for database queries
    val answerExpression = item.answerExpression ?: return emptyList()

    return when {
      answerExpression.isXFhirQuery -> {
        checkNotNull(xFhirQueryResolver) {
          "XFhirQueryResolver cannot be null. Please provide the XFhirQueryResolver via DataCaptureConfig."
        }
        val variablesMap = expressionEvaluator.extractDependentVariables(answerExpression, item)
        val xFhirExpressionString =
          expressionEvaluator.createXFhirQueryFromExpression(answerExpression, variablesMap)
        if (answerExpressionMap.containsKey(xFhirExpressionString)) {
          answerExpressionMap[xFhirExpressionString]
        }

        val data = xFhirQueryResolver.resolve(xFhirExpressionString)
        val options = item.extractAnswerOptions(data)

        answerExpressionMap[xFhirExpressionString] = options
        options
      }
      answerExpression.isFhirPath -> {
        val data = expressionEvaluator.evaluateExpression(item, responseItem, answerExpression)
        item.extractAnswerOptions(data)
      }
      else ->
        throw UnsupportedOperationException(
          "${answerExpression.language} not supported for answer-expression yet",
        )
    }
  }

  private suspend fun evaluateAnswerOptionsToggleExpressions(
    item: QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponseItemComponent,
    answerOptions: List<Questionnaire.QuestionnaireItemAnswerOptionComponent>,
  ): List<Questionnaire.QuestionnaireItemAnswerOptionComponent> {
    val results =
      item.answerOptionsToggleExpressions
        .map {
          val (expression, toggleOptions) = it
          val evaluationResult =
            if (expression.isFhirPath) {
              fhirPathEngine.convertToBoolean(
                expressionEvaluator.evaluateExpression(
                  item,
                  questionnaireResponseItem,
                  expression,
                ),
              )
            } else {
              throw UnsupportedOperationException(
                "${expression.language} not supported yet for answer-options-toggle-expression",
              )
            }
          evaluationResult to toggleOptions
        }
        .partition { it.first }
    val (allowed, disallowed) = results
    val allowedOptions = allowed.flatMap { it.second }

    val disallowedOptions =
      disallowed.flatMap {
        it.second.filterNot { option ->
          allowedOptions.any { type -> com.google.android.fhir.equals(type, option) }
        }
      }

    return answerOptions.filterNot { answerOption ->
      disallowedOptions.any { com.google.android.fhir.equals(answerOption.value, it) }
    }
  }
}
