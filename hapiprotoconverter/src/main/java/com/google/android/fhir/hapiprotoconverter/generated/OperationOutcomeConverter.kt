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

public object OperationOutcomeConverter {
  @JvmStatic
  public fun OperationOutcome.toHapi(): org.hl7.fhir.r4.model.OperationOutcome {
    val hapiValue = org.hl7.fhir.r4.model.OperationOutcome()
    hapiValue.id = id.value
    if (hasMeta()) {
      hapiValue.setMeta(meta.toHapi())
    }
    if (hasImplicitRules()) {
      hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    }
    if (hasText()) {
      hapiValue.setText(text.toHapi())
    }
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (issueCount > 0) {
      hapiValue.setIssue(issueList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.OperationOutcome.toProto(): OperationOutcome {
    val protoValue = OperationOutcome.newBuilder().setId(Id.newBuilder().setValue(id))
    if (hasMeta()) {
      protoValue.setMeta(meta.toProto())
    }
    if (hasImplicitRules()) {
      protoValue.setImplicitRules(implicitRulesElement.toProto())
    }
    if (hasText()) {
      protoValue.setText(text.toProto())
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
    protoValue.setSeverity(
      OperationOutcome.Issue.SeverityCode.newBuilder()
        .setValue(
          IssueSeverityCode.Value.valueOf(
            severity.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    protoValue.setCode(
      OperationOutcome.Issue.CodeType.newBuilder()
        .setValue(
          IssueTypeCode.Value.valueOf(
            code.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasDetails()) {
      protoValue.setDetails(details.toProto())
    }
    if (hasDiagnostics()) {
      protoValue.setDiagnostics(diagnosticsElement.toProto())
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
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    hapiValue.setSeverity(
      org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity.valueOf(
        severity.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    hapiValue.setCode(
      org.hl7.fhir.r4.model.OperationOutcome.IssueType.valueOf(
        code.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasDetails()) {
      hapiValue.setDetails(details.toHapi())
    }
    if (hasDiagnostics()) {
      hapiValue.setDiagnosticsElement(diagnostics.toHapi())
    }
    if (locationCount > 0) {
      hapiValue.setLocation(locationList.map { it.toHapi() })
    }
    if (expressionCount > 0) {
      hapiValue.setExpression(expressionList.map { it.toHapi() })
    }
    return hapiValue
  }
}
