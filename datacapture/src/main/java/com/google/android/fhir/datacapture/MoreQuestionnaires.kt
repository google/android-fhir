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

package com.google.android.fhir.datacapture

import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.Expression
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
