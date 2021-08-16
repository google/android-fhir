/*
 * Copyright 2020 Google LLC
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

package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.IssueSeverityCode
import com.google.fhir.r4.core.IssueTypeCode
import com.google.fhir.r4.core.OperationOutcome
import com.google.fhir.r4.core.OperationOutcome.Issue
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic

object OperationOutcomeConverter {
  @JvmStatic
  fun OperationOutcome.toHapi(): org.hl7.fhir.r4.model.OperationOutcome {
    val hapiValue = org.hl7.fhir.r4.model.OperationOutcome()
    hapiValue.id = id.value
    if (hasMeta()) {
        hapiValue.meta = meta.toHapi()
    }
    if (hasImplicitRules()) {
        hapiValue.implicitRulesElement = implicitRules.toHapi()
    }
    if (hasText()) {
        hapiValue.text = text.toHapi()
    }
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (issueCount > 0) {
        hapiValue.issue = issueList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.OperationOutcome.toProto(): OperationOutcome {
    val protoValue = OperationOutcome.newBuilder().setId(Id.newBuilder().setValue(id))
    if (hasMeta()) {
        protoValue.meta = meta.toProto()
    }
    if (hasImplicitRules()) {
        protoValue.implicitRules = implicitRulesElement.toProto()
    }
    if (hasText()) {
        protoValue.text = text.toProto()
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasIssue()) {
      protoValue.addAllIssue(issue.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.OperationOutcome.OperationOutcomeIssueComponent.toProto():
    OperationOutcome.Issue {
    val protoValue = OperationOutcome.Issue.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
      protoValue.severity = OperationOutcome.Issue.SeverityCode.newBuilder()
          .setValue(
              IssueSeverityCode.Value.valueOf(
                  severity.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
      protoValue.code = OperationOutcome.Issue.CodeType.newBuilder()
          .setValue(
              IssueTypeCode.Value.valueOf(
                  code.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasDetails()) {
        protoValue.details = details.toProto()
    }
    if (hasDiagnostics()) {
        protoValue.diagnostics = diagnosticsElement.toProto()
    }
    if (hasLocation()) {
      protoValue.addAllLocation(location.map { it.toProto() })
    }
    if (hasExpression()) {
      protoValue.addAllExpression(expression.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun OperationOutcome.Issue.toHapi():
    org.hl7.fhir.r4.model.OperationOutcome.OperationOutcomeIssueComponent {
    val hapiValue = org.hl7.fhir.r4.model.OperationOutcome.OperationOutcomeIssueComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
      hapiValue.severity = org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity.valueOf(
          severity.value.name.hapiCodeCheck().replace("_", "")
      )
      hapiValue.code = org.hl7.fhir.r4.model.OperationOutcome.IssueType.valueOf(
          code.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasDetails()) {
        hapiValue.details = details.toHapi()
    }
    if (hasDiagnostics()) {
        hapiValue.diagnosticsElement = diagnostics.toHapi()
    }
    if (locationCount > 0) {
        hapiValue.location = locationList.map { it.toHapi() }
    }
    if (expressionCount > 0) {
        hapiValue.expression = expressionList.map { it.toHapi() }
    }
    return hapiValue
  }
}
