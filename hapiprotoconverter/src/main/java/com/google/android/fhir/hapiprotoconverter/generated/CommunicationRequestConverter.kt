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
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
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
import com.google.fhir.r4.core.CommunicationRequest
import com.google.fhir.r4.core.CommunicationRequest.Payload
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.RequestPriorityCode
import com.google.fhir.r4.core.RequestStatusCode
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

object CommunicationRequestConverter {
  private fun CommunicationRequest.Payload.ContentX.communicationRequestPayloadContentToHapi():
    Type {
    if (hasStringValue()) {
      return (this.stringValue).toHapi()
    }
    if (hasAttachment()) {
      return (this.attachment).toHapi()
    }
    if (hasReference()) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for CommunicationRequest.payload.content[x]")
  }

  private fun Type.communicationRequestPayloadContentToProto():
    CommunicationRequest.Payload.ContentX {
    val protoValue = CommunicationRequest.Payload.ContentX.newBuilder()
    if (this is StringType) {
      protoValue.stringValue = this.toProto()
    }
    if (this is Attachment) {
      protoValue.attachment = this.toProto()
    }
    if (this is Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  private fun CommunicationRequest.OccurrenceX.communicationRequestOccurrenceToHapi(): Type {
    if (hasDateTime()) {
      return (this.dateTime).toHapi()
    }
    if (hasPeriod()) {
      return (this.period).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for CommunicationRequest.occurrence[x]")
  }

  private fun Type.communicationRequestOccurrenceToProto(): CommunicationRequest.OccurrenceX {
    val protoValue = CommunicationRequest.OccurrenceX.newBuilder()
    if (this is DateTimeType) {
      protoValue.dateTime = this.toProto()
    }
    if (this is Period) {
      protoValue.period = this.toProto()
    }
    return protoValue.build()
  }

  fun CommunicationRequest.toHapi(): org.hl7.fhir.r4.model.CommunicationRequest {
    val hapiValue = org.hl7.fhir.r4.model.CommunicationRequest()
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
    if (identifierCount > 0) {
      hapiValue.identifier = identifierList.map { it.toHapi() }
    }
    if (basedOnCount > 0) {
      hapiValue.basedOn = basedOnList.map { it.toHapi() }
    }
    if (replacesCount > 0) {
      hapiValue.replaces = replacesList.map { it.toHapi() }
    }
    if (hasGroupIdentifier()) {
      hapiValue.groupIdentifier = groupIdentifier.toHapi()
    }
    if (hasStatus()) {
      hapiValue.status =
        org.hl7.fhir.r4.model.CommunicationRequest.CommunicationRequestStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasStatusReason()) {
      hapiValue.statusReason = statusReason.toHapi()
    }
    if (categoryCount > 0) {
      hapiValue.category = categoryList.map { it.toHapi() }
    }
    if (hasPriority()) {
      hapiValue.priority =
        org.hl7.fhir.r4.model.CommunicationRequest.CommunicationPriority.valueOf(
          priority.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasDoNotPerform()) {
      hapiValue.doNotPerformElement = doNotPerform.toHapi()
    }
    if (mediumCount > 0) {
      hapiValue.medium = mediumList.map { it.toHapi() }
    }
    if (hasSubject()) {
      hapiValue.subject = subject.toHapi()
    }
    if (aboutCount > 0) {
      hapiValue.about = aboutList.map { it.toHapi() }
    }
    if (hasEncounter()) {
      hapiValue.encounter = encounter.toHapi()
    }
    if (payloadCount > 0) {
      hapiValue.payload = payloadList.map { it.toHapi() }
    }
    if (hasOccurrence()) {
      hapiValue.occurrence = occurrence.communicationRequestOccurrenceToHapi()
    }
    if (hasAuthoredOn()) {
      hapiValue.authoredOnElement = authoredOn.toHapi()
    }
    if (hasRequester()) {
      hapiValue.requester = requester.toHapi()
    }
    if (recipientCount > 0) {
      hapiValue.recipient = recipientList.map { it.toHapi() }
    }
    if (hasSender()) {
      hapiValue.sender = sender.toHapi()
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
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.CommunicationRequest.toProto(): CommunicationRequest {
    val protoValue = CommunicationRequest.newBuilder()
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
    if (hasIdentifier()) {
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    if (hasBasedOn()) {
      protoValue.addAllBasedOn(basedOn.map { it.toProto() })
    }
    if (hasReplaces()) {
      protoValue.addAllReplaces(replaces.map { it.toProto() })
    }
    if (hasGroupIdentifier()) {
      protoValue.groupIdentifier = groupIdentifier.toProto()
    }
    if (hasStatus()) {
      protoValue.status =
        CommunicationRequest.StatusCode.newBuilder()
          .setValue(
            RequestStatusCode.Value.valueOf(
              status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasStatusReason()) {
      protoValue.statusReason = statusReason.toProto()
    }
    if (hasCategory()) {
      protoValue.addAllCategory(category.map { it.toProto() })
    }
    if (hasPriority()) {
      protoValue.priority =
        CommunicationRequest.PriorityCode.newBuilder()
          .setValue(
            RequestPriorityCode.Value.valueOf(
              priority.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasDoNotPerform()) {
      protoValue.doNotPerform = doNotPerformElement.toProto()
    }
    if (hasMedium()) {
      protoValue.addAllMedium(medium.map { it.toProto() })
    }
    if (hasSubject()) {
      protoValue.subject = subject.toProto()
    }
    if (hasAbout()) {
      protoValue.addAllAbout(about.map { it.toProto() })
    }
    if (hasEncounter()) {
      protoValue.encounter = encounter.toProto()
    }
    if (hasPayload()) {
      protoValue.addAllPayload(payload.map { it.toProto() })
    }
    if (hasOccurrence()) {
      protoValue.occurrence = occurrence.communicationRequestOccurrenceToProto()
    }
    if (hasAuthoredOn()) {
      protoValue.authoredOn = authoredOnElement.toProto()
    }
    if (hasRequester()) {
      protoValue.requester = requester.toProto()
    }
    if (hasRecipient()) {
      protoValue.addAllRecipient(recipient.map { it.toProto() })
    }
    if (hasSender()) {
      protoValue.sender = sender.toProto()
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
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.CommunicationRequest.CommunicationRequestPayloadComponent.toProto():
    CommunicationRequest.Payload {
    val protoValue = CommunicationRequest.Payload.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasContent()) {
      protoValue.content = content.communicationRequestPayloadContentToProto()
    }
    return protoValue.build()
  }

  private fun CommunicationRequest.Payload.toHapi():
    org.hl7.fhir.r4.model.CommunicationRequest.CommunicationRequestPayloadComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.CommunicationRequest.CommunicationRequestPayloadComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasContent()) {
      hapiValue.content = content.communicationRequestPayloadContentToHapi()
    }
    return hapiValue
  }
}
