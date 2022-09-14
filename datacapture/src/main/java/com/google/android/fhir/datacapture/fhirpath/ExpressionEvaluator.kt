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
import com.google.android.fhir.datacapture.calculatedExpression
import com.google.android.fhir.datacapture.expressionBasedExtensions
import com.google.android.fhir.datacapture.findVariableExpression
import com.google.android.fhir.datacapture.flattened
import com.google.android.fhir.datacapture.isReferencedBy
import com.google.android.fhir.datacapture.variableExpressions
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.hapi.ctx.HapiWorkerContext
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.utils.FHIRPathEngine
import timber.log.Timber

/**
 * Evaluates an expression and returns its result.
 *
 * Expressions can be defined at questionnaire level and questionnaire item level. This
 * [ExpressionEvaluator] supports evaluation of
 * [variable expression](http://hl7.org/fhir/R4/extension-variable.html) defined at either
 * questionnaire level or questionnaire item level.
 *
 * TODO(https://github.com/google/android-fhir/issues/1575): Add a global map to apply variable
 * values if already evaluated to avoid re-calculation
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

  private val fhirPathEngine: FHIRPathEngine =
    with(FhirContext.forCached(FhirVersionEnum.R4)) {
      FHIRPathEngine(HapiWorkerContext(this, this.validationSupport)).apply {
        hostServices = FHIRPathEngineHostServices
      }
    }

  /** Detects if any item into list is referencing a dependent item in its calculated expression */
  internal fun detectExpressionCyclicDependency(
    items: List<Questionnaire.QuestionnaireItemComponent>
  ) {
    items.flattened().filter { it.calculatedExpression != null }.run {
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

  /** Detects if any item into list is referencing a dependent item in its calculated expression */
  internal fun extractExpressionReferenceMap(
          items: List<Questionnaire.QuestionnaireItemComponent>
  ) {
    items.flattened().filter { it.expressionBasedExtensions.isNotEmpty() }.run {
      forEach { current ->

      }
    }
  }

  /**
   * Returns a pair of item and the calculated and evaluated value for all items with calculated
   * expression extension, which is dependent on value of updated response
   */
  fun evaluateCalculatedExpressions(
    updatedQuestionnaireItem: Questionnaire.QuestionnaireItemComponent,
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse,
    modifiedResponses: Set<QuestionnaireResponse.QuestionnaireResponseItemComponent>,
    questionnaireItemParentMap:
      Map<Questionnaire.QuestionnaireItemComponent, Questionnaire.QuestionnaireItemComponent>
  ): List<Pair<Questionnaire.QuestionnaireItemComponent, List<Type>>> {
    return questionnaire
      .item
      .flattened()
      .filter { item ->
        // 1- item is calculable
        // 2- item answer is not modified and touched by user;
        // https://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-calculatedExpression.html
        // 3- item answer depends on the updated item answer
        item.calculatedExpression != null &&
          modifiedResponses.none { it.linkId == item.linkId } &&
          updatedQuestionnaireItem.isReferencedBy(item)
      }
      .map { calculable ->
        val appContext =
          dependentVariablesMap(
            calculable.calculatedExpression!!,
            questionnaire,
            questionnaireResponse,
            questionnaireItemParentMap,
            calculable
          )
        val updatedAnswer =
          fhirPathEngine.evaluate(
              appContext,
              questionnaireResponse,
              null,
              null,
              calculable.calculatedExpression!!.expression
            )
            .map { it.castToType(it) }
        calculable to updatedAnswer
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
   *
   * @return [Base] the result of expression
   */
  internal fun evaluateQuestionnaireItemVariableExpression(
    expression: Expression,
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse,
    questionnaireItemParentMap:
      Map<Questionnaire.QuestionnaireItemComponent, Questionnaire.QuestionnaireItemComponent>,
    questionnaireItem: Questionnaire.QuestionnaireItemComponent
  ): Base? {
    require(
      questionnaireItem.variableExpressions.any {
        it.name == expression.name && it.expression == expression.expression
      }
    ) { "The expression should come from the same questionnaire item" }

    val dependentVariablesMap =
      dependentVariablesMap(
        expression,
        questionnaire,
        questionnaireResponse,
        questionnaireItemParentMap,
        questionnaireItem
      )
    return evaluateVariable(expression, questionnaireResponse, dependentVariablesMap)
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
   * is defined
   */
  internal fun dependentVariablesMap(
    expression: Expression,
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse,
    questionnaireItemParentMap:
      Map<Questionnaire.QuestionnaireItemComponent, Questionnaire.QuestionnaireItemComponent>,
    questionnaireItem: Questionnaire.QuestionnaireItemComponent
  ) = buildMap {
    findDependentVariables(expression).forEach { variableName ->
      findAndEvaluateVariable(
        variableName,
        questionnaireItem,
        questionnaire,
        questionnaireResponse,
        questionnaireItemParentMap,
        this
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
   *
   * @return [Base] the result of expression
   */
  internal fun evaluateQuestionnaireVariableExpression(
    expression: Expression,
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse
  ): Base? {
    val variableMap =
      buildMap<String, Base?> {
        findDependentVariables(expression).forEach { variableName ->
          questionnaire.findVariableExpression(variableName)?.let { expression ->
            put(
              expression.name,
              evaluateQuestionnaireVariableExpression(
                expression,
                questionnaire,
                questionnaireResponse
              )
            )
          }
        }
      }
    return evaluateVariable(expression, questionnaireResponse, variableMap)
  }

  private fun findDependentVariables(expression: Expression) =
    variableRegex.findAll(expression.expression).map { it.groupValues[1] }.toList().filterNot {
      variable ->
      reservedVariables.contains(variable)
    }

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
   * @param dependentVariablesMap the [Map<String, Base>] of dependent variables
   */
  private fun findAndEvaluateVariable(
    variableName: String,
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse,
    questionnaireItemParentMap:
      Map<Questionnaire.QuestionnaireItemComponent, Questionnaire.QuestionnaireItemComponent>,
    dependentVariablesMap: MutableMap<String, Base?>
  ) {
    when {
      // First, check the questionnaire item itself
      questionnaireItem.findVariableExpression(variableName) != null -> {
        questionnaireItem.findVariableExpression(variableName)?.let { expression ->
          dependentVariablesMap[expression.name] =
            evaluateQuestionnaireItemVariableExpression(
              expression,
              questionnaire,
              questionnaireResponse,
              questionnaireItemParentMap,
              questionnaireItem
            )
        }
      }
      // Secondly, check the ancestors of the questionnaire item
      findVariableInAncestors(variableName, questionnaireItemParentMap, questionnaireItem) !=
        null -> {
        findVariableInAncestors(variableName, questionnaireItemParentMap, questionnaireItem)?.let {
          (questionnaireItem, expression) ->
          dependentVariablesMap[expression.name] =
            evaluateQuestionnaireItemVariableExpression(
              expression,
              questionnaire,
              questionnaireResponse,
              questionnaireItemParentMap,
              questionnaireItem
            )
        }
      }
      // Finally, check the variables defined on the questionnaire itself
      else -> {
        questionnaire.findVariableExpression(variableName)?.also { expression ->
          dependentVariablesMap[expression.name] =
            evaluateQuestionnaireVariableExpression(
              expression,
              questionnaire,
              questionnaireResponse
            )
        }
      }
    }
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
   * @return [Pair] containing [Questionnaire.QuestionnaireItemComponent] and [Expression]
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
   * Evaluates the value of variable expression and return the evaluated value
   *
   * @param expression the [Expression] the expression to evaluate
   * @param questionnaireResponse the [QuestionnaireResponse] respective questionnaire response
   * @param dependentVariables the [Map] of Variable names to their values
   *
   * @return [Base] the result of expression
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
