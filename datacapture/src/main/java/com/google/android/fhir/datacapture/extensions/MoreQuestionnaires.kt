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

package com.google.android.fhir.datacapture.extensions

import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.Questionnaire

/**
 * The StructureMap url in the
 * [target structure-map extension](http://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-targetStructureMap.html)
 * s.
 */
val Questionnaire.targetStructureMap: String?
  get() {
    val extensionValue =
      this.extension.singleOrNull { it.url == TARGET_STRUCTURE_MAP }?.value ?: return null
    return if (extensionValue is CanonicalType) extensionValue.valueAsString else null
  }

internal val Questionnaire.variableExpressions: List<Expression>
  get() =
    this.extension.filter { it.url == EXTENSION_VARIABLE_URL }.map { it.castToExpression(it.value) }

/**
 * Finds the specific variable name [String] at questionnaire [Questionnaire] level
 *
 * @param variableName the [String] to match the variable at questionnaire [Questionnaire] level
 * @return [Expression] the matching expression
 */
internal fun Questionnaire.findVariableExpression(variableName: String): Expression? =
  variableExpressions.find { it.name == variableName }

/**
 * Validates the questionnaire launch context extension, if it exists, and well formed, and
 * validates if the resource type is applicable as a launch context.
 */
internal fun validateLaunchContext(extension: Extension, resourceType: String) {
  val nameExtension =
    extension.extension
      .firstOrNull { it.url == "name" }
      ?.value.takeIf { type ->
        type is Coding &&
          QuestionnaireLaunchContextSet.values().any {
            it.code == type.code && it.display == type.display && it.system == type.system
          }
      }

  val typeExtension =
    extension.extension
      .firstOrNull { it.url == "type" }
      ?.takeIf { it.valueAsPrimitive.valueAsString == resourceType }

  if (nameExtension == null) {
    error(
      "The value of the extension:name field in " +
        "$EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT is not one of the ones defined in " +
        "$EXTENSION_LAUNCH_CONTEXT."
    )
  }

  if (typeExtension == null) {
    error(
      "The resource type set in the extension:type field in " +
        "$EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT does not match the resource type of the " +
        "context passed in: $resourceType."
    )
  }
}

/**
 * The set of supported launch contexts, as per: http://hl7.org/fhir/uv/sdc/ValueSet/launchContext
 */
private enum class QuestionnaireLaunchContextSet(
  val code: String,
  val display: String,
  val system: String,
) {
  PATIENT("patient", "Patient", EXTENSION_LAUNCH_CONTEXT),
  ENCOUNTER("encounter", "Encounter", EXTENSION_LAUNCH_CONTEXT),
  LOCATION("location", "Location", EXTENSION_LAUNCH_CONTEXT),
  USER("user", "User", EXTENSION_LAUNCH_CONTEXT),
  STUDY("study", "ResearchStudy", EXTENSION_LAUNCH_CONTEXT),
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

internal const val EXTENSION_LAUNCH_CONTEXT = "http://hl7.org/fhir/uv/sdc/CodeSystem/launchContext"

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
  SEQUENTIAL("sequential");

  companion object {
    fun from(type: String?): EntryMode? = values().find { it.value == type }
  }
}
