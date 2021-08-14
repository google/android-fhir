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
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.DetectedIssue
import com.google.fhir.r4.core.DetectedIssueSeverityCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.ObservationStatusCode
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Type

public object DetectedIssueConverter {
  @JvmStatic
  private fun DetectedIssue.IdentifiedX.detectedIssueIdentifiedToHapi(): Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for DetectedIssue.identified[x]")
  }

  @JvmStatic
  private fun Type.detectedIssueIdentifiedToProto(): DetectedIssue.IdentifiedX {
    val protoValue = DetectedIssue.IdentifiedX.newBuilder()
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun DetectedIssue.toHapi(): org.hl7.fhir.r4.model.DetectedIssue {
    val hapiValue = org.hl7.fhir.r4.model.DetectedIssue()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.DetectedIssue.DetectedIssueStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    hapiValue.setCode(code.toHapi())
    hapiValue.setSeverity(
      org.hl7.fhir.r4.model.DetectedIssue.DetectedIssueSeverity.valueOf(
        severity.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    hapiValue.setPatient(patient.toHapi())
    hapiValue.setIdentified(identified.detectedIssueIdentifiedToHapi())
    hapiValue.setAuthor(author.toHapi())
    hapiValue.setImplicated(implicatedList.map { it.toHapi() })
    hapiValue.setEvidence(evidenceList.map { it.toHapi() })
    hapiValue.setDetailElement(detail.toHapi())
    hapiValue.setReferenceElement(reference.toHapi())
    hapiValue.setMitigation(mitigationList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.DetectedIssue.toProto(): DetectedIssue {
    val protoValue =
      DetectedIssue.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setStatus(
          DetectedIssue.StatusCode.newBuilder()
            .setValue(
              ObservationStatusCode.Value.valueOf(
                status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setCode(code.toProto())
        .setSeverity(
          DetectedIssue.SeverityCode.newBuilder()
            .setValue(
              DetectedIssueSeverityCode.Value.valueOf(
                severity.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setPatient(patient.toProto())
        .setIdentified(identified.detectedIssueIdentifiedToProto())
        .setAuthor(author.toProto())
        .addAllImplicated(implicated.map { it.toProto() })
        .addAllEvidence(evidence.map { it.toProto() })
        .setDetail(detailElement.toProto())
        .setReference(referenceElement.toProto())
        .addAllMitigation(mitigation.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.DetectedIssue.DetectedIssueEvidenceComponent.toProto():
    DetectedIssue.Evidence {
    val protoValue =
      DetectedIssue.Evidence.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllCode(code.map { it.toProto() })
        .addAllDetail(detail.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.DetectedIssue.DetectedIssueMitigationComponent.toProto():
    DetectedIssue.Mitigation {
    val protoValue =
      DetectedIssue.Mitigation.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setAction(action.toProto())
        .setDate(dateElement.toProto())
        .setAuthor(author.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun DetectedIssue.Evidence.toHapi():
    org.hl7.fhir.r4.model.DetectedIssue.DetectedIssueEvidenceComponent {
    val hapiValue = org.hl7.fhir.r4.model.DetectedIssue.DetectedIssueEvidenceComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCode(codeList.map { it.toHapi() })
    hapiValue.setDetail(detailList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun DetectedIssue.Mitigation.toHapi():
    org.hl7.fhir.r4.model.DetectedIssue.DetectedIssueMitigationComponent {
    val hapiValue = org.hl7.fhir.r4.model.DetectedIssue.DetectedIssueMitigationComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setAction(action.toHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setAuthor(author.toHapi())
    return hapiValue
  }
}
