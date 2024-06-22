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

package com.google.android.fhir.datacapture.fhirpath

import com.google.android.fhir.datacapture.XFhirQueryResolver
import com.google.android.fhir.datacapture.extensions.calculatedExpression
import com.google.android.fhir.datacapture.extensions.findVariableExpression
import com.google.android.fhir.datacapture.extensions.flattened
import com.google.android.fhir.datacapture.extensions.isFhirPath
import com.google.android.fhir.datacapture.extensions.isReferencedBy
import com.google.android.fhir.datacapture.extensions.isXFhirQuery
import com.google.android.fhir.datacapture.extensions.variableExpressions
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.ExpressionNode
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemComponent
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.Type
import timber.log.Timber

/**
 * Evaluates an expression and returns its result.
 *
 * The evaluator works in the context of a [Questionnaire] and the corresponding
 * [QuestionnaireResponse]. It is the caller's responsibility to make sure to call the evaluator
 * with [QuestionnaireItemComponent] and [QuestionnaireResponseItemComponent] that belong to the
 * [Questionnaire] and the [QuestionnaireResponse].
 *
 * Expressions can be defined at questionnaire level and questionnaire item level. This
 * [ExpressionEvaluator] supports evaluation of
 * [variable expression](http://hl7.org/fhir/R4/extension-variable.html) defined at either
 * questionnaire level or questionnaire item level.
 *
 * @param questionnaire the [Questionnaire] where the expression belong to
 * @param questionnaireResponse the [QuestionnaireResponse] related to the [Questionnaire]
 * @param questionnaireItemParentMap the [Map] of items parent
 * @param questionnaireLaunchContextMap the [Map] of launchContext names to their resource values
 */
internal class ExpressionEvaluator(
  private val questionnaire: Questionnaire,
  private val questionnaireResponse: QuestionnaireResponse,
  private val questionnaireItemParentMap:
    Map<QuestionnaireItemComponent, QuestionnaireItemComponent> =
    emptyMap(),
  private val questionnaireLaunchContextMap: Map<String, Resource>? = emptyMap(),
  private val xFhirQueryResolver: XFhirQueryResolver? = null,
) {

  private val reservedItemVariables =
    listOf(
      "sct",
      "loinc",
      "ucum",
      "resource",
      "rootResource",
      "context",
      "map-codes",
      "questionnaire",
      "qItem",
    )

  private val reservedRootVariables =
    listOf(
      "sct",
      "loinc",
      "ucum",
      "resource",
      "rootResource",
      "context",
      "map-codes",
      "questionnaire",
    )

  /**
   * Finds all the matching occurrences of variables. For example, when we apply regex to the
   * expression "%X + %Y", if we simply groupValues, it returns [%X, X], [%Y, Y] The group with
   * index 0 is always the entire matched string (%X and %Y). The indices greater than 0 represent
   * groups in the regular expression (X and Y) so we groupValues by first index to get only the
   * variables name without % as prefix i.e, ([X, Y])
   *
   * If we apply regex to the expression "X + Y", it returns nothing as there are no matching groups
   * in this expression
   */
  private val variableRegex = Regex("[%]([A-Za-z0-9\\-]{1,64})")

  /**
   * Finds all the matching occurrences of FHIRPaths in x-fhir-query. See:
   * https://build.fhir.org/ig/HL7/sdc/expressions.html#x-fhir-query-enhancements
   */
  private val xFhirQueryEnhancementRegex = Regex("\\{\\{(.*?)\\}\\}")

  /**
   * Variable %questionnaire corresponds to the Questionnaire resource into
   * QuestionnaireResponse.questionnaire element.
   * https://build.fhir.org/ig/HL7/sdc/expressions.html#fhirpath-supplements
   */
  private val questionnaireFhirPathSupplement = "questionnaire"

  /**
   * Variable %qitem refer to Questionnaire.item that corresponds to context
   * QuestionnaireResponse.item. It is only valid for FHIRPath expressions defined within a
   * Questionnaire item. https://build.fhir.org/ig/HL7/sdc/expressions.html#fhirpath-supplements
   */
  private val questionnaireItemFhirPathSupplement = "qItem"

  /** Detects if any item into list is referencing a dependent item in its calculated expression */
  internal fun detectExpressionCyclicDependency(items: List<QuestionnaireItemComponent>) {
    items
      .flattened()
      .filter { it.calculatedExpression != null }
      .run {
        forEach { current ->
          // no calculable item depending on current item should be used as dependency into current
          // item
          this.forEach { dependent ->
            check(!(current.isReferencedBy(dependent) && dependent.isReferencedBy(current))) {
              "${current.linkId} and ${dependent.linkId} have cyclic dependency in expression based extension"
            }
          }
        }
      }
  }

  /**
   * Returns the evaluation result of the expression.
   *
   * FHIRPath supplements are handled according to
   * https://build.fhir.org/ig/HL7/sdc/expressions.html#fhirpath-supplements.
   *
   * %resource = [QuestionnaireResponse] %context = [QuestionnaireResponseItemComponent]
   */
  suspend fun evaluateExpression(
    questionnaireItem: QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponseItemComponent?,
    expression: Expression,
  ): List<Base> {
    val appContext = extractItemDependentVariables(expression, questionnaireItem)
    return evaluateToBase(
      questionnaireResponse,
      questionnaireResponseItem,
      expression.expression,
      appContext,
    )
  }

  /**
   * Returns single [Type] evaluation value result of an expression, including cqf-expression and
   * cqf-calculatedValue expressions
   */
  suspend fun evaluateExpressionValue(
    questionnaireItem: QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponseItemComponent?,
    expression: Expression,
  ): Type? {
    if (!expression.isFhirPath) {
      throw UnsupportedOperationException("${expression.language} not supported yet")
    }
    return try {
      evaluateExpression(questionnaireItem, questionnaireResponseItem, expression).singleOrNull()
        as? Type
    } catch (e: Exception) {
      Timber.w("Could not evaluate expression ${expression.expression} with FHIRPathEngine", e)
      null
    }
  }

  /**
   * Returns a list of pair of item and the calculated and evaluated value for all items with
   * calculated expression extension, which is dependent on value of updated response
   */
  suspend fun evaluateCalculatedExpressions(
    questionnaireItem: QuestionnaireItemComponent,
    updatedQuestionnaireResponseItemComponent: QuestionnaireResponseItemComponent?,
  ): List<ItemToAnswersPair> {
    return questionnaire.item
      .flattened()
      .filter { item ->
        // Condition 1. item is calculable
        // Condition 2. item answer depends on the updated item answer OR has a variable dependency
        item.calculatedExpression != null &&
          (questionnaireItem.isReferencedBy(item) ||
            findDependentVariables(item.calculatedExpression!!).isNotEmpty())
      }
      .map { item ->
        val updatedAnswer =
          evaluateExpression(
              item,
              updatedQuestionnaireResponseItemComponent,
              item.calculatedExpression!!,
            )
            .map { it.castToType(it) }
        item to updatedAnswer
      }
  }

  /**
   * Evaluates variable expression defined at questionnaire item level and returns the evaluated
   * result.
   *
   * Parses the expression using regex [Regex] for variable (For example: A variable name could be
   * %weight) and build a list of variables that the expression contains and for every variable, we
   * first find it at questionnaire item, then up in the ancestors and then at questionnaire level,
   * if found we get their expressions and pass them into the same function to evaluate its value
   * recursively, we put the variable name and its evaluated value into the map [Map] to use this
   * map to pass into fhirPathEngine's evaluate method to apply the evaluated values to the
   * expression being evaluated.
   *
   * @param expression the [Expression] Variable expression
   *   Questionnaire.QuestionnaireItemComponent>] of child to parent
   * @param questionnaireItem the [Questionnaire.QuestionnaireItemComponent] where this expression
   *   is defined,
   * @param variablesMap the [Map<String, Base>] of variables, the default value is empty map
   * @return [Base] the result of expression
   */
  internal suspend fun evaluateQuestionnaireItemVariableExpression(
    expression: Expression,
    questionnaireItem: QuestionnaireItemComponent,
    variablesMap: MutableMap<String, Base?> = mutableMapOf(),
  ): Base? {
    require(
      questionnaireItem.variableExpressions.any {
        it.name == expression.name && it.expression == expression.expression
      },
    ) {
      "The expression should come from the same questionnaire item"
    }
    extractItemDependentVariables(
      expression,
      questionnaireItem,
      variablesMap,
    )

    return evaluateVariable(
      expression,
      variablesMap,
    )
  }

  /**
   * Parses the expression using regex [Regex] for variable and build a map of variables and its
   * values respecting the scope and hierarchy level
   *
   * @param expression the [Expression] expression to find variables applicable
   * @param questionnaireItem the [Questionnaire.QuestionnaireItemComponent] where this expression
   * @param variablesMap the [Map<String, Base>] of variables, the default value is empty map is
   *   defined
   */
  internal suspend fun extractItemDependentVariables(
    expression: Expression,
    questionnaireItem: QuestionnaireItemComponent,
    variablesMap: MutableMap<String, Base?> = mutableMapOf(),
  ): MutableMap<String, Base?> {
    questionnaireLaunchContextMap?.let { variablesMap.putAll(it) }
    findDependentVariables(expression)
      .filterNot { variable -> reservedItemVariables.contains(variable) }
      .forEach { variableName ->
        if (variablesMap[variableName] == null) {
          findAndEvaluateVariable(
            variableName,
            questionnaireItem,
            variablesMap,
          )
        }
      }
    return variablesMap.apply {
      put(questionnaireFhirPathSupplement, questionnaire)
      put(questionnaireItemFhirPathSupplement, questionnaireItem)
    }
  }

  /**
   * Evaluates variable expression defined at questionnaire level and returns the evaluated result.
   *
   * Parses the expression using [Regex] for variable (For example: A variable name could be
   * %weight) and build a list of variables that the expression contains and for every variable, we
   * first find it at questionnaire level, if found we get their expressions and pass them into the
   * same function to evaluate its value recursively, we put the variable name and its evaluated
   * value into the map [Map] to use this map to pass into fhirPathEngine's evaluate method to apply
   * the evaluated values to the expression being evaluated.
   *
   * @param expression the [Expression] Variable expression
   * @param variablesMap the [Map<String, Base>] of variables, the default value is empty map
   * @return [Base] the result of expression
   */
  internal suspend fun evaluateQuestionnaireVariableExpression(
    expression: Expression,
    variablesMap: MutableMap<String, Base?> = mutableMapOf(),
  ): Base? {
    findDependentVariables(expression)
      .filterNot { variable -> reservedRootVariables.contains(variable) }
      .forEach { variableName ->
        questionnaire.findVariableExpression(variableName)?.let { expression ->
          if (variablesMap[expression.name] == null) {
            variablesMap[expression.name] =
              evaluateQuestionnaireVariableExpression(
                expression,
                variablesMap,
              )
          }
        }
      }

    return evaluateVariable(
      expression,
      variablesMap,
    )
  }

  /**
   * Creates an x-fhir-query string for evaluation. For this, it evaluates both variables and
   * fhir-paths in the expression.
   */
  internal fun createXFhirQueryFromExpression(
    expression: Expression,
    variablesMap: Map<String, Base?> = emptyMap(),
  ): String {
    // get all dependent variables and their evaluated values
    val variablesEvaluatedPairs =
      variablesMap
        .filterKeys { expression.expression.contains("{{%$it}}") }
        .map { Pair("{{%${it.key}}}", it.value?.primitiveValue() ?: "") }

    val fhirPathsEvaluatedPairs =
      questionnaireLaunchContextMap
        ?.toMutableMap()
        .takeIf { !it.isNullOrEmpty() }
        ?.also { it.put(questionnaireFhirPathSupplement, questionnaire) }
        ?.let { evaluateXFhirEnhancement(expression, it) }
        ?: emptySequence()

    return (variablesEvaluatedPairs + fhirPathsEvaluatedPairs).fold(expression.expression) {
      acc: String,
      pair: Pair<String, String>,
      ->
      acc.replace(pair.first, pair.second)
    }
  }

  /**
   * Evaluates an x-fhir-query that contains fhir-paths, returning a sequence of pairs. The first
   * element in the pair is the FhirPath expression surrounded by curly brackets {{ fhir.path }},
   * and the second element is the evaluated string result from evaluating the resource passed in.
   *
   * @param expression x-fhir-query expression containing a FHIRpath, e.g.
   *   Practitioner?active=true&{{Practitioner.name.family}}
   * @param launchContextMap the launch context to evaluate the expression against
   */
  private fun evaluateXFhirEnhancement(
    expression: Expression,
    launchContextMap: Map<String, Resource>,
  ): Sequence<Pair<String, String>> =
    xFhirQueryEnhancementRegex
      .findAll(expression.expression)
      .map { it.groupValues }
      .map { (fhirPathWithParentheses, fhirPath) ->
        val expressionNode = extractExpressionNode(fhirPath)
        val evaluatedResult =
          evaluateToString(
            expression = expressionNode,
            data = launchContextMap[extractResourceType(expressionNode)],
            contextMap = launchContextMap,
          )

        // If the result of evaluating the FHIRPath expressions is an invalid query, it returns
        // null. As per the spec:
        // Systems SHOULD log it and continue with extraction as if the query had returned no
        // data.
        // See : http://build.fhir.org/ig/HL7/sdc/extraction.html#structuremap-based-extraction
        if (evaluatedResult.isEmpty()) {
          Timber.w(
            "$fhirPath evaluated to null. The expression is either invalid, or the " +
              "expression returned no, or more than one resource. The expression will be " +
              "replaced with a blank string.",
          )
        }
        fhirPathWithParentheses to evaluatedResult
      }

  private fun findDependentVariables(expression: Expression) =
    variableRegex.findAll(expression.expression).map { it.groupValues[1] }.toList()

  /**
   * Finds the dependent variables at questionnaire item level first, then in ancestors and then at
   * questionnaire level
   *
   * @param variableName the [String] to match the variable in the ancestors
   * @param questionnaireItem the [Questionnaire.QuestionnaireItemComponent] from where we have to
   *   track hierarchy up in the ancestors
   * @param variablesMap the [Map<String, Base>] of variables
   */
  private suspend fun findAndEvaluateVariable(
    variableName: String,
    questionnaireItem: QuestionnaireItemComponent,
    variablesMap: MutableMap<String, Base?> = mutableMapOf(),
  ) {
    // First, check the questionnaire item itself
    val evaluatedValue =
      questionnaireItem.findVariableExpression(variableName)?.let { expression ->
        evaluateQuestionnaireItemVariableExpression(
          expression,
          questionnaireItem,
          variablesMap,
        )
      } // Secondly, check the ancestors of the questionnaire item
        ?: findVariableInAncestors(variableName, questionnaireItem)?.let {
          (questionnaireItem, expression) ->
          evaluateQuestionnaireItemVariableExpression(
            expression,
            questionnaireItem,
            variablesMap,
          )
        } // Finally, check the variables defined on the questionnaire itself
          ?: questionnaire.findVariableExpression(variableName)?.let { expression ->
          evaluateQuestionnaireVariableExpression(
            expression,
            variablesMap,
          )
        }

    evaluatedValue?.also { variablesMap[variableName] = it }
  }

  /**
   * Finds the questionnaire item having specific variable name [String] in the ancestors of
   * questionnaire item [Questionnaire.QuestionnaireItemComponent]
   *
   * @param variableName the [String] to match the variable in the ancestors
   * @param questionnaireItem the [Questionnaire.QuestionnaireItemComponent] whose ancestors we
   *   visit
   * @return [Pair] containing [Questionnaire.QuestionnaireItemComponent] and an [Expression]
   */
  private fun findVariableInAncestors(
    variableName: String,
    questionnaireItem: QuestionnaireItemComponent,
  ): Pair<QuestionnaireItemComponent, Expression>? {
    var parent = questionnaireItemParentMap[questionnaireItem]
    while (parent != null) {
      val expression = parent.findVariableExpression(variableName)
      if (expression != null) return Pair(parent, expression)

      parent = questionnaireItemParentMap[parent]
    }
    return null
  }

  /**
   * Evaluates the value of variable expression and returns its evaluated value
   *
   * @param expression the [Expression] the expression to evaluate
   * @param dependentVariables the [Map] of variable names to their values
   * @return [Base] the result of an expression
   */
  private suspend fun evaluateVariable(
    expression: Expression,
    dependentVariables: Map<String, Base?> = emptyMap(),
  ) =
    try {
      require(expression.name?.isNotBlank() == true) {
        "Expression name should be a valid expression name"
      }

      if (expression.isXFhirQuery) {
        checkNotNull(xFhirQueryResolver) {
          "XFhirQueryResolver cannot be null. Please provide the XFhirQueryResolver via DataCaptureConfig."
        }

        val xFhirExpressionString = createXFhirQueryFromExpression(expression, dependentVariables)

        if (dependentVariables.contains(expression.name)) dependentVariables[expression.name]!!

        Bundle().apply {
          entry =
            xFhirQueryResolver.resolve(xFhirExpressionString).map {
              BundleEntryComponent().apply { resource = it }
            }
        }
      } else if (expression.isFhirPath) {
        evaluateToBase(
            questionnaireResponse = questionnaireResponse,
            questionnaireResponseItem = null,
            expression = expression.expression,
            contextMap = dependentVariables,
          )
          .firstOrNull()
      } else {
        throw UnsupportedOperationException(
          "${expression.language} not supported for variable-expression yet",
        )
      }
    } catch (exception: FHIRException) {
      Timber.w("Could not evaluate expression with FHIRPathEngine", exception)
      null
    }
}

/**
 * Extract [ResourceType] string representation from constant or name property of given
 * [ExpressionNode].
 */
private fun extractResourceType(expressionNode: ExpressionNode): String? {
  // TODO(omarismail94): See if FHIRPathEngine.check() can be used to distinguish invalid
  // expression vs an expression that is valid, but does not return one resource only.
  return expressionNode.constant?.primitiveValue()?.substring(1) ?: expressionNode.name?.lowercase()
}

/** Pair of a [Questionnaire.QuestionnaireItemComponent] with its evaluated answers */
internal typealias ItemToAnswersPair = Pair<QuestionnaireItemComponent, List<Type>>
