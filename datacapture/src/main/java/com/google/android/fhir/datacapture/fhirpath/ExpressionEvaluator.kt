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
import com.google.android.fhir.datacapture.variableExpressions
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.hapi.ctx.HapiWorkerContext
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.utils.FHIRPathEngine
import timber.log.Timber

/**
 * Evaluates an expression and return its result
 *
 * Expressions can be defined at questionnaire root level and questionnaire item level This
 * [ExpressionEvaluator] supports evaluation of
 * [variable expression](http://hl7.org/fhir/R4/extension-variable.html) defined at either root
 * level or questionnaire item level
 */
object ExpressionEvaluator {

  private val reservedVariables =
    listOf("sct", "loinc", "ucum", "resource", "rootResource", "context", "map-codes")

  private val variableRegex = Regex("[%]([A-Za-z0-9\\-]{1,64})")

  private val fhirPathEngine: FHIRPathEngine =
    with(FhirContext.forCached(FhirVersionEnum.R4)) {
      FHIRPathEngine(HapiWorkerContext(this, this.validationSupport)).apply {
        hostServices = FHIRPathEngineHostServices
      }
    }

  /**
   * Evaluates variable expression defined at questionnaire item level and return the evaluated
   * result.
   *
   * Parses the expression using regex [Regex] for variable (For example: A variable name could be
   * %weight) and build a list of variables that the expression contains and for every variable, we
   * first find it at origin, then up in the parent hierarchy and then at root/questionnaire level,
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
   * @param origin the [Questionnaire.QuestionnaireItemComponent] where this expression is defined,
   *
   * @return [Base] the result of expression
   */
  internal fun evaluateQuestionnaireItemVariableExpression(
    expression: Expression,
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse,
    questionnaireItemParentMap:
      Map<Questionnaire.QuestionnaireItemComponent, Questionnaire.QuestionnaireItemComponent>,
    origin: Questionnaire.QuestionnaireItemComponent
  ): Base? {
    buildMap<String, Base?> {
      variableRegex
        .findAll(expression.expression)
        .map { it.groupValues[1] }
        .toList()
        .filterNot { variable -> reservedVariables.any { it == variable } }
        .forEach { variableName ->
          // 1- Check at Origin
          findVariableAtOrigin(variableName, origin)?.also { (questionnaireItem, expression) ->
            put(
              expression.name,
              evaluateQuestionnaireItemVariableExpression(
                expression,
                questionnaire,
                questionnaireResponse,
                questionnaireItemParentMap,
                questionnaireItem
              )
            )
          }
          // 2- Check in Parent
          findVariableInParent(variableName, questionnaireItemParentMap, origin)?.also {
            (questionnaireItem, expression) ->
            put(
              expression.name,
              evaluateQuestionnaireItemVariableExpression(
                expression,
                questionnaire,
                questionnaireResponse,
                questionnaireItemParentMap,
                questionnaireItem
              )
            )
          }
          // 3-Check at Root/Questionnaire level
          findVariableAtRoot(variableName, questionnaire)?.also { expression ->
            put(
              expression.name,
              evaluateQuestionnaireRootVariableExpression(
                expression,
                questionnaire,
                questionnaireResponse
              )
            )
          }
        }
    }
      .also {
        return evaluateVariable(expression, questionnaireResponse, it)
      }
  }

  /**
   * Evaluates variable expression defined at questionnaire root level and return the evaluated
   * result.
   *
   * Parses the expression using regex [Regex] for variable (For example: A variable name could be
   * %weight) and build a list of variables that the expression contains and for every variable, we
   * first find it at root/questionnaire level, if found we get their expressions and pass them into
   * the same function to evaluate its value recursively, we put the variable name and its evaluated
   * value into the map [Map] to use this map to pass into fhirPathEngine's evaluate method to apply
   * the evaluated values to the expression being evaluated.
   *
   * @param expression the [Expression] Variable expression
   * @param questionnaire the [Questionnaire] respective questionnaire
   * @param questionnaireResponse the [QuestionnaireResponse] respective questionnaire response
   *
   * @return [Base] the result of expression
   */
  internal fun evaluateQuestionnaireRootVariableExpression(
    expression: Expression,
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse
  ): Base? {
    buildMap<String, Base?> {
      variableRegex
        .findAll(expression.expression)
        .map { it.groupValues[1] }
        .toList()
        .filterNot { variable -> reservedVariables.any { it == variable } }
        .forEach { variableName ->
          findVariableAtRoot(variableName, questionnaire)?.also { expression ->
            put(
              expression.name,
              evaluateQuestionnaireRootVariableExpression(
                expression,
                questionnaire,
                questionnaireResponse
              )
            )
          }
        }
    }
      .also {
        return evaluateVariable(expression, questionnaireResponse, it)
      }
  }

  /**
   * Finds the specific variable name [String] at the origin
   * [Questionnaire.QuestionnaireItemComponent]
   *
   * @param origin the [Questionnaire.QuestionnaireItemComponent] from where we have to track
   * hierarchy up in the parent
   * @param variableName the [String] to match the variable in the parent hierarchy
   *
   * @return [Pair] containing [Questionnaire.QuestionnaireItemComponent] and [Expression]
   */
  private fun findVariableAtOrigin(
    variableName: String,
    origin: Questionnaire.QuestionnaireItemComponent
  ): Pair<Questionnaire.QuestionnaireItemComponent, Expression>? {
    origin.variableExpressions.find { it.name == variableName }.also {
      it?.let {
        return Pair(origin, it)
      }
    }
    return null
  }

  /**
   * Finds the specific variable name [String] in the parent hierarchy of origin
   * [Questionnaire.QuestionnaireItemComponent]
   *
   * @param variableName the [String] to match the variable in the parent hierarchy
   * @param
   * @param origin the [Questionnaire.QuestionnaireItemComponent] from where we have to track
   * hierarchy up in the parent
   * @param questionnaireItemParentMap the [Map<Questionnaire.QuestionnaireItemComponent,
   * Questionnaire.QuestionnaireItemComponent>] of child to parent
   * @return [Pair] containing [Questionnaire.QuestionnaireItemComponent] and [Expression]
   */
  private fun findVariableInParent(
    variableName: String,
    questionnaireItemParentMap:
      Map<Questionnaire.QuestionnaireItemComponent, Questionnaire.QuestionnaireItemComponent>,
    origin: Questionnaire.QuestionnaireItemComponent
  ): Pair<Questionnaire.QuestionnaireItemComponent, Expression>? {
    var parent = questionnaireItemParentMap[origin]
    while (parent != null) {
      parent.variableExpressions.find { it.name == variableName }.also {
        it?.let {
          return Pair(parent!!, it)
        }
      }
      parent = questionnaireItemParentMap[parent]
    }
    return null
  }

  /**
   * Finds the specific variable name [String] at root/questionnaire [Questionnaire] level
   *
   * @param variableName the [String] to match the variable at questionnaire [Questionnaire] level
   * @param questionnaire the [Questionnaire] respective questionnaire
   * @return [Expression] the matching expression
   */
  private fun findVariableAtRoot(variableName: String, questionnaire: Questionnaire): Expression? {
    questionnaire.variableExpressions.find { it.name == variableName }.also {
      it?.let {
        return it
      }
    }
    return null
  }

  /**
   * Evaluates the value of variable expression and return the evaluated value
   *
   * @param expression the [Expression] the expression to evaluate
   * @param questionnaireResponse the [QuestionnaireResponse] respective questionnaire response
   * @param inputVariables the [Map] of Variable names to their values
   *
   * @return [Base] the result of expression
   */
  private fun evaluateVariable(
    expression: Expression,
    questionnaireResponse: QuestionnaireResponse,
    inputVariables: Map<String, Base?> = mapOf()
  ) =
    try {
      require(!expression.name.isNullOrEmpty()) {
        "Expression name should be a valid expression name"
      }

      require(expression.hasLanguage() && expression.language == "text/fhirpath") {
        "Unsupported expression language, language should be text/fhirpath"
      }

      fhirPathEngine
        .evaluate(inputVariables, questionnaireResponse, null, null, expression.expression)
        .firstOrNull()
    } catch (exception: FHIRException) {
      Timber.w("Could not evaluate expression with FHIRPathEngine", exception)
      null
    }
}
