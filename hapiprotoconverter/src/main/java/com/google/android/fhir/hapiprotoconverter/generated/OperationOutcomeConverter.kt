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
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIssue(issueList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.OperationOutcome.toProto(): OperationOutcome {
    val protoValue =
      OperationOutcome.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIssue(issue.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.OperationOutcome.OperationOutcomeIssueComponent.toProto():
    OperationOutcome.Issue {
    val protoValue =
      OperationOutcome.Issue.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setSeverity(
          OperationOutcome.Issue.SeverityCode.newBuilder()
            .setValue(
              IssueSeverityCode.Value.valueOf(
                severity.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setCode(
          OperationOutcome.Issue.CodeType.newBuilder()
            .setValue(
              IssueTypeCode.Value.valueOf(
                code.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setDetails(details.toProto())
        .setDiagnostics(diagnosticsElement.toProto())
        .addAllLocation(location.map { it.toProto() })
        .addAllExpression(expression.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun OperationOutcome.Issue.toHapi():
    org.hl7.fhir.r4.model.OperationOutcome.OperationOutcomeIssueComponent {
    val hapiValue = org.hl7.fhir.r4.model.OperationOutcome.OperationOutcomeIssueComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
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
    hapiValue.setDetails(details.toHapi())
    hapiValue.setDiagnosticsElement(diagnostics.toHapi())
    hapiValue.setLocation(locationList.map { it.toHapi() })
    hapiValue.setExpression(expressionList.map { it.toHapi() })
    return hapiValue
  }
}
