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

import com.google.android.fhir.datacapture.extensions.calculatedExpression
import com.google.android.fhir.datacapture.extensions.findVariableExpression
import com.google.android.fhir.datacapture.extensions.flattened
import com.google.android.fhir.datacapture.extensions.isReferencedBy
import com.google.android.fhir.datacapture.extensions.variableExpressions
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.Expression
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
 * Expressions can be defined at questionnaire level and questionnaire item level. This
 * [ExpressionEvaluator] supports evaluation of
 * [variable expression](http://hl7.org/fhir/R4/extension-variable.html) defined at either
 * questionnaire level or questionnaire item level.
 */
object ExpressionEvaluator {

  private val reservedVariables =
    listOf("sct", "loinc", "ucum", "resource", "rootResource", "context", "map-codes")

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

  /** Detects if any item into list is referencing a dependent item in its calculated expression */
  internal fun detectExpressionCyclicDependency(
    items: List<Questionnaire.QuestionnaireItemComponent>
  ) {
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
  fun evaluateExpression(
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse,
    questionnaireItem: QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponseItemComponent?,
    expression: Expression,
    questionnaireItemParentMap: Map<QuestionnaireItemComponent, QuestionnaireItemComponent>
  ): List<Base> {
    val appContext =
      mutableMapOf<String, Base?>().apply {
        extractDependentVariables(
          expression,
          questionnaire,
          questionnaireResponse,
          questionnaireItemParentMap,
          questionnaireItem,
          this
        )
      }
    return fhirPathEngine.evaluate(
      appContext,
      questionnaireResponse,
      null,
      questionnaireResponseItem,
      expression.expression
    )
  }

  /**
   * Returns a list of pair of item and the calculated and evaluated value for all items with
   * calculated expression extension, which is dependent on value of updated response
   */
  fun evaluateCalculatedExpressions(
    updatedQuestionnaireItem: QuestionnaireItemComponent,
    updatedQuestionnaireResponseItemComponent: QuestionnaireResponseItemComponent?,
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse,
    questionnaireItemParentMap: Map<QuestionnaireItemComponent, QuestionnaireItemComponent>
  ): List<ItemToAnswersPair> {
    return questionnaire.item
      .flattened()
      .filter { item ->
        // Condition 1. item is calculable
        // Condition 2. item answer depends on the updated item answer OR has a variable dependency
        item.calculatedExpression != null &&
          (updatedQuestionnaireItem.isReferencedBy(item) ||
            findDependentVariables(item.calculatedExpression!!).isNotEmpty())
      }
      .map { questionnaireItem ->
        val updatedAnswer =
          evaluateExpression(
              questionnaire,
              questionnaireResponse,
              questionnaireItem,
              updatedQuestionnaireResponseItemComponent,
              questionnaireItem.calculatedExpression!!,
              questionnaireItemParentMap
            )
            .map { it.castToType(it) }
        questionnaireItem to updatedAnswer
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
   * @param questionnaire the [Questionnaire] respective questionnaire
   * @param questionnaireResponse the [QuestionnaireResponse] respective questionnaire response
   * @param questionnaireItemParentMap the [Map<Questionnaire.QuestionnaireItemComponent,
   * Questionnaire.QuestionnaireItemComponent>] of child to parent
   * @param questionnaireItem the [Questionnaire.QuestionnaireItemComponent] where this expression
   * is defined,
   * @param variablesMap the [Map<String, Base>] of variables, the default value is empty map
   *
   * @return [Base] the result of expression
   */
  internal fun evaluateQuestionnaireItemVariableExpression(
    expression: Expression,
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse,
    questionnaireItemParentMap:
      Map<Questionnaire.QuestionnaireItemComponent, Questionnaire.QuestionnaireItemComponent>,
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    variablesMap: MutableMap<String, Base?> = mutableMapOf()
  ): Base? {
    require(
      questionnaireItem.variableExpressions.any {
        it.name == expression.name && it.expression == expression.expression
      }
    ) { "The expression should come from the same questionnaire item" }
    extractDependentVariables(
      expression,
      questionnaire,
      questionnaireResponse,
      questionnaireItemParentMap,
      questionnaireItem,
      variablesMap
    )

    return evaluateVariable(expression, questionnaireResponse, variablesMap)
  }

  /**
   * Parses the expression using regex [Regex] for variable and build a map of variables and its
   * values respecting the scope and hierarchy level
   *
   * @param expression the [Expression] expression to find variables applicable
   * @param questionnaire the [Questionnaire] respective questionnaire
   * @param questionnaireResponse the [QuestionnaireResponse] respective questionnaire response
   * @param questionnaireItemParentMap the [Map<Questionnaire.QuestionnaireItemComponent,
   * Questionnaire.QuestionnaireItemComponent>] of child to parent
   * @param questionnaireItem the [Questionnaire.QuestionnaireItemComponent] where this expression
   * @param variablesMap the [Map<String, Base>] of variables, the default value is empty map is
   * defined
   */
  private fun extractDependentVariables(
    expression: Expression,
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse,
    questionnaireItemParentMap:
      Map<Questionnaire.QuestionnaireItemComponent, Questionnaire.QuestionnaireItemComponent>,
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    variablesMap: MutableMap<String, Base?> = mutableMapOf()
  ) =
    findDependentVariables(expression).forEach { variableName ->
      if (variablesMap[variableName] == null) {
        findAndEvaluateVariable(
          variableName,
          questionnaireItem,
          questionnaire,
          questionnaireResponse,
          questionnaireItemParentMap,
          variablesMap
        )
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
   * @param questionnaire the [Questionnaire] respective questionnaire
   * @param questionnaireResponse the [QuestionnaireResponse] respective questionnaire response
   * @param variablesMap the [Map<String, Base>] of variables, the default value is empty map
   *
   * @return [Base] the result of expression
   */
  internal fun evaluateQuestionnaireVariableExpression(
    expression: Expression,
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse,
    variablesMap: MutableMap<String, Base?> = mutableMapOf()
  ): Base? {
    findDependentVariables(expression).forEach { variableName ->
      questionnaire.findVariableExpression(variableName)?.let { expression ->
        if (variablesMap[expression.name] == null) {
          variablesMap[expression.name] =
            evaluateQuestionnaireVariableExpression(
              expression,
              questionnaire,
              questionnaireResponse,
              variablesMap
            )
        }
      }
    }

    return evaluateVariable(expression, questionnaireResponse, variablesMap)
  }

  /**
   * Creates an x-fhir-query string for evaluation
   *
   * @param expression x-fhir-query expression
   * @param launchContextMap if passed, the launch context to evaluate the expression against
   */
  internal fun createXFhirQueryFromExpression(
    expression: Expression,
    launchContextMap: Map<String, Resource>?
  ): String {
    if (launchContextMap == null) {
      return expression.expression
    }
    return evaluateXFhirEnhancement(expression, launchContextMap).fold(expression.expression) {
      acc: String,
      pair: Pair<String, String> ->
      acc.replace(pair.first, pair.second)
    }
  }

  /**
   * Evaluates an x-fhir-query that contains fhir-paths, returning a sequence of pairs. The first
   * element in the pair is the FhirPath expression surrounded by curly brackets {{ fhir.path }},
   * and the second element is the evaluated string result from evaluating the resource passed in.
   *
   * @param expression x-fhir-query expression containing a FHIRpath, e.g.
   * Practitioner?active=true&{{Practitioner.name.family}}
   * @param launchContextMap the launch context to evaluate the expression against
   */
  private fun evaluateXFhirEnhancement(
    expression: Expression,
    launchContextMap: Map<String, Resource>
  ): Sequence<Pair<String, String>> =
    xFhirQueryEnhancementRegex
      .findAll(expression.expression)
      .map { it.groupValues }
      .map { (fhirPathWithParentheses, fhirPath) ->
        // TODO(omarismail94): See if FHIRPathEngine.check() can be used to distinguish invalid
        // expression vs an expression that is valid, but does not return one resource only.
        val expressionNode = fhirPathEngine.parse(fhirPath)
        val resourceType =
          expressionNode.constant?.primitiveValue()?.substring(1)
            ?: expressionNode.name?.lowercase()
        val evaluatedResult =
          fhirPathEngine.evaluateToString(
            launchContextMap,
            null,
            null,
            launchContextMap[resourceType],
            expressionNode
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
              "replaced with a blank string."
          )
        }
        fhirPathWithParentheses to evaluatedResult
      }

  private fun findDependentVariables(expression: Expression) =
    variableRegex
      .findAll(expression.expression)
      .map { it.groupValues[1] }
      .toList()
      .filterNot { variable -> reservedVariables.contains(variable) }

  /**
   * Finds the dependent variables at questionnaire item level first, then in ancestors and then at
   * questionnaire level
   *
   * @param variableName the [String] to match the variable in the ancestors
   * @param questionnaireItem the [Questionnaire.QuestionnaireItemComponent] from where we have to
   * track hierarchy up in the ancestors
   * @param questionnaire the [Questionnaire] respective questionnaire
   * @param questionnaireResponse the [QuestionnaireResponse] respective questionnaire response
   * @param questionnaireItemParentMap the [Map<Questionnaire.QuestionnaireItemComponent,
   * Questionnaire.QuestionnaireItemComponent>] of child to parent
   * @param variablesMap the [Map<String, Base>] of variables
   */
  private fun findAndEvaluateVariable(
    variableName: String,
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse,
    questionnaireItemParentMap:
      Map<Questionnaire.QuestionnaireItemComponent, Questionnaire.QuestionnaireItemComponent>,
    variablesMap: MutableMap<String, Base?>
  ) {
    // First, check the questionnaire item itself
    val evaluatedValue =
      questionnaireItem.findVariableExpression(variableName)?.let { expression ->
        evaluateQuestionnaireItemVariableExpression(
          expression,
          questionnaire,
          questionnaireResponse,
          questionnaireItemParentMap,
          questionnaireItem,
          variablesMap
        )
      } // Secondly, check the ancestors of the questionnaire item
        ?: findVariableInAncestors(variableName, questionnaireItemParentMap, questionnaireItem)
          ?.let { (questionnaireItem, expression) ->
            evaluateQuestionnaireItemVariableExpression(
              expression,
              questionnaire,
              questionnaireResponse,
              questionnaireItemParentMap,
              questionnaireItem,
              variablesMap
            )
          } // Finally, check the variables defined on the questionnaire itself
          ?: questionnaire.findVariableExpression(variableName)?.let { expression ->
          evaluateQuestionnaireVariableExpression(
            expression,
            questionnaire,
            questionnaireResponse,
            variablesMap
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
   * visit
   * @param questionnaireItemParentMap the [Map<Questionnaire.QuestionnaireItemComponent,
   * Questionnaire.QuestionnaireItemComponent>] of child to parent
   * @return [Pair] containing [Questionnaire.QuestionnaireItemComponent] and an [Expression]
   */
  private fun findVariableInAncestors(
    variableName: String,
    questionnaireItemParentMap:
      Map<Questionnaire.QuestionnaireItemComponent, Questionnaire.QuestionnaireItemComponent>,
    questionnaireItem: Questionnaire.QuestionnaireItemComponent
  ): Pair<Questionnaire.QuestionnaireItemComponent, Expression>? {
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
   * @param questionnaireResponse the [QuestionnaireResponse] respective questionnaire response
   * @param dependentVariables the [Map] of variable names to their values
   *
   * @return [Base] the result of an expression
   */
  private fun evaluateVariable(
    expression: Expression,
    questionnaireResponse: QuestionnaireResponse,
    dependentVariables: Map<String, Base?> = mapOf()
  ) =
    try {
      require(expression.name?.isNotBlank() == true) {
        "Expression name should be a valid expression name"
      }

      require(expression.hasLanguage() && expression.language == "text/fhirpath") {
        "Unsupported expression language, language should be text/fhirpath"
      }

      fhirPathEngine
        .evaluate(dependentVariables, questionnaireResponse, null, null, expression.expression)
        .firstOrNull()
    } catch (exception: FHIRException) {
      Timber.w("Could not evaluate expression with FHIRPathEngine", exception)
      null
    }
}

/** Pair of a [Questionnaire.QuestionnaireItemComponent] with its evaluated answers */
internal typealias ItemToAnswersPair = Pair<Questionnaire.QuestionnaireItemComponent, List<Type>>
