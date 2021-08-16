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
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Attachment
import com.google.fhir.r4.core.Communication
import com.google.fhir.r4.core.Communication.Payload
import com.google.fhir.r4.core.EventStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.RequestPriorityCode
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

object CommunicationConverter {
  @JvmStatic
  private fun Communication.Payload.ContentX.communicationPayloadContentToHapi(): Type {
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    if (this.attachment != Attachment.newBuilder().defaultInstanceForType) {
      return (this.attachment).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Communication.payload.content[x]")
  }

  @JvmStatic
  private fun Type.communicationPayloadContentToProto(): Communication.Payload.ContentX {
    val protoValue = Communication.Payload.ContentX.newBuilder()
    if (this is StringType) {
      protoValue.stringValue = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Attachment) {
      protoValue.attachment = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun Communication.toHapi(): org.hl7.fhir.r4.model.Communication {
    val hapiValue = org.hl7.fhir.r4.model.Communication()
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
    if (instantiatesCanonicalCount > 0) {
      hapiValue.instantiatesCanonical = instantiatesCanonicalList.map { it.toHapi() }
    }
    if (instantiatesUriCount > 0) {
      hapiValue.instantiatesUri = instantiatesUriList.map { it.toHapi() }
    }
    if (basedOnCount > 0) {
      hapiValue.basedOn = basedOnList.map { it.toHapi() }
    }
    if (partOfCount > 0) {
      hapiValue.partOf = partOfList.map { it.toHapi() }
    }
    if (inResponseToCount > 0) {
      hapiValue.inResponseTo = inResponseToList.map { it.toHapi() }
    }
    hapiValue.status =
      org.hl7.fhir.r4.model.Communication.CommunicationStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasStatusReason()) {
      hapiValue.statusReason = statusReason.toHapi()
    }
    if (categoryCount > 0) {
      hapiValue.category = categoryList.map { it.toHapi() }
    }
    hapiValue.priority =
      org.hl7.fhir.r4.model.Communication.CommunicationPriority.valueOf(
        priority.value.name.hapiCodeCheck().replace("_", "")
      )
    if (mediumCount > 0) {
      hapiValue.medium = mediumList.map { it.toHapi() }
    }
    if (hasSubject()) {
      hapiValue.subject = subject.toHapi()
    }
    if (hasTopic()) {
      hapiValue.topic = topic.toHapi()
    }
    if (aboutCount > 0) {
      hapiValue.about = aboutList.map { it.toHapi() }
    }
    if (hasEncounter()) {
      hapiValue.encounter = encounter.toHapi()
    }
    if (hasSent()) {
      hapiValue.sentElement = sent.toHapi()
    }
    if (hasReceived()) {
      hapiValue.receivedElement = received.toHapi()
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
    if (payloadCount > 0) {
      hapiValue.payload = payloadList.map { it.toHapi() }
    }
    if (noteCount > 0) {
      hapiValue.note = noteList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.Communication.toProto(): Communication {
    val protoValue = Communication.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasInstantiatesCanonical()) {
      protoValue.addAllInstantiatesCanonical(instantiatesCanonical.map { it.toProto() })
    }
    if (hasInstantiatesUri()) {
      protoValue.addAllInstantiatesUri(instantiatesUri.map { it.toProto() })
    }
    if (hasBasedOn()) {
      protoValue.addAllBasedOn(basedOn.map { it.toProto() })
    }
    if (hasPartOf()) {
      protoValue.addAllPartOf(partOf.map { it.toProto() })
    }
    if (hasInResponseTo()) {
      protoValue.addAllInResponseTo(inResponseTo.map { it.toProto() })
    }
    protoValue.status =
      Communication.StatusCode.newBuilder()
        .setValue(
          EventStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasStatusReason()) {
      protoValue.statusReason = statusReason.toProto()
    }
    if (hasCategory()) {
      protoValue.addAllCategory(category.map { it.toProto() })
    }
    protoValue.priority =
      Communication.PriorityCode.newBuilder()
        .setValue(
          RequestPriorityCode.Value.valueOf(
            priority.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasMedium()) {
      protoValue.addAllMedium(medium.map { it.toProto() })
    }
    if (hasSubject()) {
      protoValue.subject = subject.toProto()
    }
    if (hasTopic()) {
      protoValue.topic = topic.toProto()
    }
    if (hasAbout()) {
      protoValue.addAllAbout(about.map { it.toProto() })
    }
    if (hasEncounter()) {
      protoValue.encounter = encounter.toProto()
    }
    if (hasSent()) {
      protoValue.sent = sentElement.toProto()
    }
    if (hasReceived()) {
      protoValue.received = receivedElement.toProto()
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
    if (hasPayload()) {
      protoValue.addAllPayload(payload.map { it.toProto() })
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Communication.CommunicationPayloadComponent.toProto():
    Communication.Payload {
    val protoValue = Communication.Payload.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasContent()) {
      protoValue.content = content.communicationPayloadContentToProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Communication.Payload.toHapi():
    org.hl7.fhir.r4.model.Communication.CommunicationPayloadComponent {
    val hapiValue = org.hl7.fhir.r4.model.Communication.CommunicationPayloadComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasContent()) {
      hapiValue.content = content.communicationPayloadContentToHapi()
    }
    return hapiValue
  }
}
