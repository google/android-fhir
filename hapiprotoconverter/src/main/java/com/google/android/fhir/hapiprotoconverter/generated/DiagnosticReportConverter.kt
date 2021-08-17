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

import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toProto
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
import com.google.fhir.r4.core.DiagnosticReport
import com.google.fhir.r4.core.DiagnosticReportStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Type

object DiagnosticReportConverter {
  private fun DiagnosticReport.EffectiveX.diagnosticReportEffectiveToHapi(): Type {
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for DiagnosticReport.effective[x]")
  }

  private fun Type.diagnosticReportEffectiveToProto(): DiagnosticReport.EffectiveX {
    val protoValue = DiagnosticReport.EffectiveX.newBuilder()
    if (this is DateTimeType) {
      protoValue.dateTime = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.period = this.toProto()
    }
    return protoValue.build()
  }

  fun DiagnosticReport.toHapi(): org.hl7.fhir.r4.model.DiagnosticReport {
    val hapiValue = org.hl7.fhir.r4.model.DiagnosticReport()
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
    if (basedOnCount > 0) {
      hapiValue.basedOn = basedOnList.map { it.toHapi() }
    }
    hapiValue.status =
      org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (categoryCount > 0) {
      hapiValue.category = categoryList.map { it.toHapi() }
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (hasSubject()) {
      hapiValue.subject = subject.toHapi()
    }
    if (hasEncounter()) {
      hapiValue.encounter = encounter.toHapi()
    }
    if (hasEffective()) {
      hapiValue.effective = effective.diagnosticReportEffectiveToHapi()
    }
    if (hasIssued()) {
      hapiValue.issuedElement = issued.toHapi()
    }
    if (performerCount > 0) {
      hapiValue.performer = performerList.map { it.toHapi() }
    }
    if (resultsInterpreterCount > 0) {
      hapiValue.resultsInterpreter = resultsInterpreterList.map { it.toHapi() }
    }
    if (specimenCount > 0) {
      hapiValue.specimen = specimenList.map { it.toHapi() }
    }
    if (resultCount > 0) {
      hapiValue.result = resultList.map { it.toHapi() }
    }
    if (imagingStudyCount > 0) {
      hapiValue.imagingStudy = imagingStudyList.map { it.toHapi() }
    }
    if (mediaCount > 0) {
      hapiValue.media = mediaList.map { it.toHapi() }
    }
    if (hasConclusion()) {
      hapiValue.conclusionElement = conclusion.toHapi()
    }
    if (conclusionCodeCount > 0) {
      hapiValue.conclusionCode = conclusionCodeList.map { it.toHapi() }
    }
    if (presentedFormCount > 0) {
      hapiValue.presentedForm = presentedFormList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.DiagnosticReport.toProto(): DiagnosticReport {
    val protoValue = DiagnosticReport.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasBasedOn()) {
      protoValue.addAllBasedOn(basedOn.map { it.toProto() })
    }
    protoValue.status =
      DiagnosticReport.StatusCode.newBuilder()
        .setValue(
          DiagnosticReportStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasCategory()) {
      protoValue.addAllCategory(category.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    if (hasSubject()) {
      protoValue.subject = subject.toProto()
    }
    if (hasEncounter()) {
      protoValue.encounter = encounter.toProto()
    }
    if (hasEffective()) {
      protoValue.effective = effective.diagnosticReportEffectiveToProto()
    }
    if (hasIssued()) {
      protoValue.issued = issuedElement.toProto()
    }
    if (hasPerformer()) {
      protoValue.addAllPerformer(performer.map { it.toProto() })
    }
    if (hasResultsInterpreter()) {
      protoValue.addAllResultsInterpreter(resultsInterpreter.map { it.toProto() })
    }
    if (hasSpecimen()) {
      protoValue.addAllSpecimen(specimen.map { it.toProto() })
    }
    if (hasResult()) {
      protoValue.addAllResult(result.map { it.toProto() })
    }
    if (hasImagingStudy()) {
      protoValue.addAllImagingStudy(imagingStudy.map { it.toProto() })
    }
    if (hasMedia()) {
      protoValue.addAllMedia(media.map { it.toProto() })
    }
    if (hasConclusion()) {
      protoValue.conclusion = conclusionElement.toProto()
    }
    if (hasConclusionCode()) {
      protoValue.addAllConclusionCode(conclusionCode.map { it.toProto() })
    }
    if (hasPresentedForm()) {
      protoValue.addAllPresentedForm(presentedForm.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportMediaComponent.toProto():
    DiagnosticReport.Media {
    val protoValue = DiagnosticReport.Media.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasComment()) {
      protoValue.comment = commentElement.toProto()
    }
    if (hasLink()) {
      protoValue.link = link.toProto()
    }
    return protoValue.build()
  }

  private fun DiagnosticReport.Media.toHapi():
    org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportMediaComponent {
    val hapiValue = org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportMediaComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasComment()) {
      hapiValue.commentElement = comment.toHapi()
    }
    if (hasLink()) {
      hapiValue.link = link.toHapi()
    }
    return hapiValue
  }
}
