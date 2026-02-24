/*
 * Copyright 2023-2026 Google LLC
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

package com.google.android.fhir.datacapture.extensions

import com.google.fhir.model.r4.Expression
import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import com.google.fhir.model.r4.Resource
import com.google.fhir.model.r4.terminologies.ResourceType

/**
 * The StructureMap url in the
 * [target structure-map extension](http://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-targetStructureMap.html)
 * s.
 */
val Questionnaire.targetStructureMap: String?
  get() {
    val extensionValue =
      this.extension.singleOrNull { it.url == TARGET_STRUCTURE_MAP }?.value ?: return null
    return if (extensionValue is Extension.Value.Canonical) {
      extensionValue.asString()?.value?.value
    } else {
      null
    }
  }

internal val Questionnaire.variableExpressions: List<Expression>
  get() =
    this.extension
      .filter { it.url == EXTENSION_VARIABLE_URL }
      .mapNotNull { it.value?.asExpression()?.value }

/**
 * A list of extensions that define the resources that provide context for form processing logic:
 * https://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-launchContext.html
 */
internal val Questionnaire.questionnaireLaunchContexts: List<Extension>?
  get() =
    this.extension
      .filter { it.url == EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT }
      .takeIf { it.isNotEmpty() }

/**
 * Finds the specific variable name [String] at questionnaire [Questionnaire] level
 *
 * @param variableName the [String] to match the variable at questionnaire [Questionnaire] level
 * @return [Expression] the matching expression
 */
internal fun Questionnaire.findVariableExpression(variableName: String): Expression? =
  variableExpressions.find { it.name?.value == variableName }

/**
 * Validates each questionnaire launch context extension matches:
 * https://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-launchContext.html
 */
internal fun validateLaunchContextExtensions(launchContextExtensions: List<Extension>) =
  launchContextExtensions.forEach { launchExtension ->
    validateLaunchContextExtension(launchExtension)
  }

/**
 * Verifies the existence of extension:name and extension:type with valid name system and type
 * values.
 */
private fun validateLaunchContextExtension(launchExtension: Extension) {
  val nameCoding =
    launchExtension.takeIf { it.url == "name" }?.value?.asCoding()
      ?: error(
        "The extension:name is missing or is not of type Coding in $EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT",
      )

  val typeCodeType =
    launchExtension.takeIf { it.url == "type" }?.value?.asCode()
      ?: error(
        "The extension:type is missing or is not of type CodeType in $EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT",
      )

  val isValidResourceType =
    try {
      typeCodeType.value.value?.let {
        ResourceType.fromCode(it)
        true
      }
        ?: false
    } catch (exception: IllegalArgumentException) {
      false
    }

  if (CODE_SYSTEM_LAUNCH_CONTEXT != nameCoding.value.system?.value || !isValidResourceType) {
    error(
      "The extension:name and/or extension:type do not follow the format specified in $EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT",
    )
  }
}

/**
 * Filters the provided launch contexts by matching the keys with the `code` values found in the
 * "name" extensions.
 */
internal fun filterByCodeInNameExtension(
  launchContexts: Map<String, Resource>,
  launchContextExtensions: List<Extension>,
): Map<String, Resource> {
  val nameCodes: Set<String> =
    launchContextExtensions
      .mapNotNull { extension ->
        (extension.takeIf { it.url == "name" }?.value?.asCoding()?.value)?.code?.value
      }
      .toSet()

  return launchContexts.filterKeys { nameCodes.contains(it) }
}

/**
 * See
 * [Extension: target structure map](http://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-targetStructureMap.html)
 * .
 */
private const val TARGET_STRUCTURE_MAP: String =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-targetStructureMap"

val Questionnaire.isPaginated: Boolean
  get() = item.any { item -> item.displayItemControl == DisplayItemControlType.PAGE }

/**
 * See
 * [Extension: Entry mode](http://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-entryMode.html)
 * .
 */
internal const val EXTENSION_ENTRY_MODE_URL: String =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-entryMode"

internal const val EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-launchContext"

internal const val CODE_SYSTEM_LAUNCH_CONTEXT =
  "http://hl7.org/fhir/uv/sdc/CodeSystem/launchContext"

val Questionnaire.entryMode: EntryMode?
  get() {
    val entryMode =
      this.extension
        .firstOrNull { it.url == EXTENSION_ENTRY_MODE_URL }
        ?.value
        ?.toString()
        ?.lowercase()
    return EntryMode.from(entryMode)
  }

enum class EntryMode(val value: String) {
  PRIOR_EDIT("prior-edit"),
  RANDOM("random"),
  SEQUENTIAL("sequential"),
  ;

  companion object {
    fun from(type: String?): EntryMode? = entries.find { it.value == type }
  }
}

/**
 * Applies `forEach` on each questionnaire item and questionnaire response item pair in the
 * questionnaire and the given `questionnaireResponse`.
 *
 * Questionnaire items and questionnaire response items are visited in pre-order.
 *
 * Items nested under repeated groups and repeated questions will be repeated for each repeated
 * group instance or answer provided by the user.
 *
 * Note: use this function only with a questionnaire response that has been packed using
 * [QuestionnaireResponse.packRepeatedGroups].
 */
internal suspend fun Questionnaire.forEachItemPair(
  questionnaireResponse: QuestionnaireResponse,
  forEach:
    suspend (
      questionnaireItem: Questionnaire.Item,
      questionnaireResponseItem: QuestionnaireResponse.Item,
    ) -> Unit,
) {
  forEachItemPair(item, questionnaireResponse.item, forEach)
}

private suspend fun forEachItemPair(
  questionnaireItems: List<Questionnaire.Item>,
  questionnaireResponseItems: List<QuestionnaireResponse.Item>,
  forEach:
    suspend (
      questionnaireItem: Questionnaire.Item,
      questionnaireResponseItem: QuestionnaireResponse.Item,
    ) -> Unit,
) {
  require(questionnaireItems.size == questionnaireResponseItems.size)
  questionnaireItems.zip(questionnaireResponseItems).forEach {
    (questionnaireItem, questionnaireResponseItem) ->
    require(questionnaireItem.linkId == questionnaireResponseItem.linkId)

    // Apply forEach on the current questionnaire item and questionnaire response item
    forEach(questionnaireItem, questionnaireResponseItem)

    // For non-repeated groups, simply match the child questionnaire items with child questionnaire
    // response items.
    if (
      questionnaireItem.type.value == Questionnaire.QuestionnaireItemType.Group &&
        questionnaireItem.repeats?.value == false &&
        questionnaireItem.item.isNotEmpty()
    ) {
      forEachItemPair(questionnaireItem.item, questionnaireResponseItem.item, forEach)
    }

    // The following block handles two separate cases:
    // 1. questionnaire items nested under repeated group are repeated for each instance of the
    // repeated group, each represented as an answer components in the questionnaire response item.
    // 2. questionnaire items nested directly under question are repeated for each answer.
    if (questionnaireItem.repeats?.value == true && questionnaireItem.item.isNotEmpty()) {
      questionnaireResponseItem.answer.forEach {
        forEachItemPair(questionnaireItem.item, it.item, forEach)
      }
    }
  }
}
