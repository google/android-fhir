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
 * A list of extensions that define the resources that provide context for form processing logic:
 * https://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-launchContext.html
 */
val Questionnaire.questionnaireLaunchContexts: List<Extension>?
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
  variableExpressions.find { it.name == variableName }

/**
 * Validates each questionnaire launch context extension to match:
 * https://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-launchContext.html
 */
internal fun validateLaunchContextExtensions(launchContextExtensions: List<Extension>) =
  launchContextExtensions.forEach { launchContextExtension ->
    val nameExtension = launchContextExtension.extension.firstOrNull { it.url == "name" }
    validateLaunchContextNameExtension(nameExtension)
    validateLaunchContextTypeExtension(
      launchContextExtension.extension.firstOrNull { it.url == "type" },
      (nameExtension!!.value as Coding).display
    )
  }

/**
 * Checks that the extension:name extension exists and its value contains a valid code from
 * [QuestionnaireLaunchContextSet]
 */
private fun validateLaunchContextNameExtension(nameExtension: Extension?) {
  if (nameExtension == null) {
    error("The extension:name extension is missing in $EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT")
  }
  val isValidCode =
    QuestionnaireLaunchContextSet.values().any {
      nameExtension.equalsDeep(
        Extension().apply {
          url = "name"
          setValue(
            Coding().apply {
              code = it.code
              display = it.display
              system = it.system
            }
          )
        }
      )
    }

  if (!isValidCode) {
    error(
      "The value of the extension:name extension in " +
        "$EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT is not a valid code in $EXTENSION_LAUNCH_CONTEXT."
    )
  }
}

/**
 * Checks that the type of resources provided is defined in the type extension field and is the same
 * as the resource type in the name extension:
 * https://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-launchContext.html
 */
private fun validateLaunchContextTypeExtension(
  typeExtension: Extension?,
  resourceTypeInNameExtension: String
) {
  if (typeExtension == null) {
    error("The extension:type extension is missing in $EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT")
  }

  val resourceTypeInTypeExtension = typeExtension.valueAsPrimitive.valueAsString
  if ((resourceTypeInNameExtension == "User") and
      (resourceTypeInTypeExtension !in USER_CODE_RESOURCE_TYPE_SET)
  ) {
    error(
      "Types must be from the specified value set of resource types based on User: $USER_CODE_RESOURCE_TYPE_SET"
    )
  } else if (!resourceTypeInTypeExtension.equals(resourceTypeInNameExtension)) {
    error(
      "The resource type, $resourceTypeInTypeExtension, in the extension:type extension is not the same as the value set defined in the extension:name extension"
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

/**
 * Set of resource types that the User code encompasses. See the User row in:
 * https://build.fhir.org/ig/HL7/sdc/ValueSet-launchContext.html#expansion
 */
private val USER_CODE_RESOURCE_TYPE_SET =
  listOf("Device", "Organization", "Patient", "Practitioner", "PractitionerRole", "RelatedPerson")
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
