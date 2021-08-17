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

import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DataRequirementConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DataRequirementConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.GuidanceResponse
import com.google.fhir.r4.core.GuidanceResponseStatusCode
import com.google.fhir.r4.core.Id
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UriType

object GuidanceResponseConverter {
  private fun GuidanceResponse.ModuleX.guidanceResponseModuleToHapi(): Type {
    if (hasUri()) {
      return (this.uri).toHapi()
    }
    if (hasCanonical()) {
      return (this.canonical).toHapi()
    }
    if (hasCodeableConcept()) {
      return (this.codeableConcept).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for GuidanceResponse.module[x]")
  }

  private fun Type.guidanceResponseModuleToProto(): GuidanceResponse.ModuleX {
    val protoValue = GuidanceResponse.ModuleX.newBuilder()
    if (this is UriType) {
      protoValue.uri = this.toProto()
    }
    if (this is CanonicalType) {
      protoValue.canonical = this.toProto()
    }
    if (this is CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    return protoValue.build()
  }

  fun GuidanceResponse.toHapi(): org.hl7.fhir.r4.model.GuidanceResponse {
    val hapiValue = org.hl7.fhir.r4.model.GuidanceResponse()
    if (hasId()) {
      hapiValue.id = id.value
    }
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
    if (hasRequestIdentifier()) {
      hapiValue.requestIdentifier = requestIdentifier.toHapi()
    }
    if (identifierCount > 0) {
      hapiValue.identifier = identifierList.map { it.toHapi() }
    }
    if (hasModule()) {
      hapiValue.module = module.guidanceResponseModuleToHapi()
    }
    if (hasStatus()) {
      hapiValue.status =
        org.hl7.fhir.r4.model.GuidanceResponse.GuidanceResponseStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasSubject()) {
      hapiValue.subject = subject.toHapi()
    }
    if (hasEncounter()) {
      hapiValue.encounter = encounter.toHapi()
    }
    if (hasOccurrenceDateTime()) {
      hapiValue.occurrenceDateTimeElement = occurrenceDateTime.toHapi()
    }
    if (hasPerformer()) {
      hapiValue.performer = performer.toHapi()
    }
    if (reasonCodeCount > 0) {
      hapiValue.reasonCode = reasonCodeList.map { it.toHapi() }
    }
    if (reasonReferenceCount > 0) {
      hapiValue.reasonReference = reasonReferenceList.map { it.toHapi() }
    }
    if (noteCount > 0) {
      hapiValue.note = noteList.map { it.toHapi() }
    }
    if (evaluationMessageCount > 0) {
      hapiValue.evaluationMessage = evaluationMessageList.map { it.toHapi() }
    }
    if (hasOutputParameters()) {
      hapiValue.outputParameters = outputParameters.toHapi()
    }
    if (hasResult()) {
      hapiValue.result = result.toHapi()
    }
    if (dataRequirementCount > 0) {
      hapiValue.dataRequirement = dataRequirementList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.GuidanceResponse.toProto(): GuidanceResponse {
    val protoValue = GuidanceResponse.newBuilder()
    if (hasId()) {
      protoValue.setId(Id.newBuilder().setValue(id))
    }
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
    if (hasRequestIdentifier()) {
      protoValue.requestIdentifier = requestIdentifier.toProto()
    }
    if (hasIdentifier()) {
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    if (hasModule()) {
      protoValue.module = module.guidanceResponseModuleToProto()
    }
    if (hasStatus()) {
      protoValue.status =
        GuidanceResponse.StatusCode.newBuilder()
          .setValue(
            GuidanceResponseStatusCode.Value.valueOf(
              status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasSubject()) {
      protoValue.subject = subject.toProto()
    }
    if (hasEncounter()) {
      protoValue.encounter = encounter.toProto()
    }
    if (hasOccurrenceDateTime()) {
      protoValue.occurrenceDateTime = occurrenceDateTimeElement.toProto()
    }
    if (hasPerformer()) {
      protoValue.performer = performer.toProto()
    }
    if (hasReasonCode()) {
      protoValue.addAllReasonCode(reasonCode.map { it.toProto() })
    }
    if (hasReasonReference()) {
      protoValue.addAllReasonReference(reasonReference.map { it.toProto() })
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    if (hasEvaluationMessage()) {
      protoValue.addAllEvaluationMessage(evaluationMessage.map { it.toProto() })
    }
    if (hasOutputParameters()) {
      protoValue.outputParameters = outputParameters.toProto()
    }
    if (hasResult()) {
      protoValue.result = result.toProto()
    }
    if (hasDataRequirement()) {
      protoValue.addAllDataRequirement(dataRequirement.map { it.toProto() })
    }
    return protoValue.build()
  }
}
