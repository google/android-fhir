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
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Type

object DetectedIssueConverter {
  private fun DetectedIssue.IdentifiedX.detectedIssueIdentifiedToHapi(): Type {
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for DetectedIssue.identified[x]")
  }

  private fun Type.detectedIssueIdentifiedToProto(): DetectedIssue.IdentifiedX {
    val protoValue = DetectedIssue.IdentifiedX.newBuilder()
    if (this is DateTimeType) {
      protoValue.dateTime = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.period = this.toProto()
    }
    return protoValue.build()
  }

  fun DetectedIssue.toHapi(): org.hl7.fhir.r4.model.DetectedIssue {
    val hapiValue = org.hl7.fhir.r4.model.DetectedIssue()
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
    if (identifierCount > 0) {
      hapiValue.identifier = identifierList.map { it.toHapi() }
    }
    hapiValue.status =
      org.hl7.fhir.r4.model.DetectedIssue.DetectedIssueStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    hapiValue.severity =
      org.hl7.fhir.r4.model.DetectedIssue.DetectedIssueSeverity.valueOf(
        severity.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasPatient()) {
      hapiValue.patient = patient.toHapi()
    }
    if (hasIdentified()) {
      hapiValue.identified = identified.detectedIssueIdentifiedToHapi()
    }
    if (hasAuthor()) {
      hapiValue.author = author.toHapi()
    }
    if (implicatedCount > 0) {
      hapiValue.implicated = implicatedList.map { it.toHapi() }
    }
    if (evidenceCount > 0) {
      hapiValue.evidence = evidenceList.map { it.toHapi() }
    }
    if (hasDetail()) {
      hapiValue.detailElement = detail.toHapi()
    }
    if (hasReference()) {
      hapiValue.referenceElement = reference.toHapi()
    }
    if (mitigationCount > 0) {
      hapiValue.mitigation = mitigationList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.DetectedIssue.toProto(): DetectedIssue {
    val protoValue = DetectedIssue.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasIdentifier()) {
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    protoValue.status =
      DetectedIssue.StatusCode.newBuilder()
        .setValue(
          ObservationStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    protoValue.severity =
      DetectedIssue.SeverityCode.newBuilder()
        .setValue(
          DetectedIssueSeverityCode.Value.valueOf(
            severity.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasPatient()) {
      protoValue.patient = patient.toProto()
    }
    if (hasIdentified()) {
      protoValue.identified = identified.detectedIssueIdentifiedToProto()
    }
    if (hasAuthor()) {
      protoValue.author = author.toProto()
    }
    if (hasImplicated()) {
      protoValue.addAllImplicated(implicated.map { it.toProto() })
    }
    if (hasEvidence()) {
      protoValue.addAllEvidence(evidence.map { it.toProto() })
    }
    if (hasDetail()) {
      protoValue.detail = detailElement.toProto()
    }
    if (hasReference()) {
      protoValue.reference = referenceElement.toProto()
    }
    if (hasMitigation()) {
      protoValue.addAllMitigation(mitigation.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.DetectedIssue.DetectedIssueEvidenceComponent.toProto():
    DetectedIssue.Evidence {
    val protoValue = DetectedIssue.Evidence.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.addAllCode(code.map { it.toProto() })
    }
    if (hasDetail()) {
      protoValue.addAllDetail(detail.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.DetectedIssue.DetectedIssueMitigationComponent.toProto():
    DetectedIssue.Mitigation {
    val protoValue = DetectedIssue.Mitigation.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasAction()) {
      protoValue.action = action.toProto()
    }
    if (hasDate()) {
      protoValue.date = dateElement.toProto()
    }
    if (hasAuthor()) {
      protoValue.author = author.toProto()
    }
    return protoValue.build()
  }

  private fun DetectedIssue.Evidence.toHapi():
    org.hl7.fhir.r4.model.DetectedIssue.DetectedIssueEvidenceComponent {
    val hapiValue = org.hl7.fhir.r4.model.DetectedIssue.DetectedIssueEvidenceComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (codeCount > 0) {
      hapiValue.code = codeList.map { it.toHapi() }
    }
    if (detailCount > 0) {
      hapiValue.detail = detailList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun DetectedIssue.Mitigation.toHapi():
    org.hl7.fhir.r4.model.DetectedIssue.DetectedIssueMitigationComponent {
    val hapiValue = org.hl7.fhir.r4.model.DetectedIssue.DetectedIssueMitigationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasAction()) {
      hapiValue.action = action.toHapi()
    }
    if (hasDate()) {
      hapiValue.dateElement = date.toHapi()
    }
    if (hasAuthor()) {
      hapiValue.author = author.toHapi()
    }
    return hapiValue
  }
}
