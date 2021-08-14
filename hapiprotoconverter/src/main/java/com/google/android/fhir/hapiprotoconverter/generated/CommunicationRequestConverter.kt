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
import com.google.fhir.r4.core.Attachment
import com.google.fhir.r4.core.CommunicationRequest
import com.google.fhir.r4.core.CommunicationRequest.Payload
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.RequestPriorityCode
import com.google.fhir.r4.core.RequestStatusCode
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

public object CommunicationRequestConverter {
  @JvmStatic
  private fun CommunicationRequest.Payload.ContentX.communicationRequestPayloadContentToHapi():
    Type {
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    if (this.getAttachment() != Attachment.newBuilder().defaultInstanceForType) {
      return (this.getAttachment()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for CommunicationRequest.payload.content[x]")
  }

  @JvmStatic
  private fun Type.communicationRequestPayloadContentToProto():
    CommunicationRequest.Payload.ContentX {
    val protoValue = CommunicationRequest.Payload.ContentX.newBuilder()
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Attachment) {
      protoValue.setAttachment(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun CommunicationRequest.OccurrenceX.communicationRequestOccurrenceToHapi(): Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for CommunicationRequest.occurrence[x]")
  }

  @JvmStatic
  private fun Type.communicationRequestOccurrenceToProto(): CommunicationRequest.OccurrenceX {
    val protoValue = CommunicationRequest.OccurrenceX.newBuilder()
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun CommunicationRequest.toHapi(): org.hl7.fhir.r4.model.CommunicationRequest {
    val hapiValue = org.hl7.fhir.r4.model.CommunicationRequest()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setBasedOn(basedOnList.map { it.toHapi() })
    hapiValue.setReplaces(replacesList.map { it.toHapi() })
    hapiValue.setGroupIdentifier(groupIdentifier.toHapi())
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.CommunicationRequest.CommunicationRequestStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    hapiValue.setStatusReason(statusReason.toHapi())
    hapiValue.setCategory(categoryList.map { it.toHapi() })
    hapiValue.setPriority(
      org.hl7.fhir.r4.model.CommunicationRequest.CommunicationPriority.valueOf(
        priority.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    hapiValue.setDoNotPerformElement(doNotPerform.toHapi())
    hapiValue.setMedium(mediumList.map { it.toHapi() })
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setAbout(aboutList.map { it.toHapi() })
    hapiValue.setEncounter(encounter.toHapi())
    hapiValue.setPayload(payloadList.map { it.toHapi() })
    hapiValue.setOccurrence(occurrence.communicationRequestOccurrenceToHapi())
    hapiValue.setAuthoredOnElement(authoredOn.toHapi())
    hapiValue.setRequester(requester.toHapi())
    hapiValue.setRecipient(recipientList.map { it.toHapi() })
    hapiValue.setSender(sender.toHapi())
    hapiValue.setReasonCode(reasonCodeList.map { it.toHapi() })
    hapiValue.setReasonReference(reasonReferenceList.map { it.toHapi() })
    hapiValue.setNote(noteList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.CommunicationRequest.toProto(): CommunicationRequest {
    val protoValue =
      CommunicationRequest.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .addAllBasedOn(basedOn.map { it.toProto() })
        .addAllReplaces(replaces.map { it.toProto() })
        .setGroupIdentifier(groupIdentifier.toProto())
        .setStatus(
          CommunicationRequest.StatusCode.newBuilder()
            .setValue(
              RequestStatusCode.Value.valueOf(
                status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setStatusReason(statusReason.toProto())
        .addAllCategory(category.map { it.toProto() })
        .setPriority(
          CommunicationRequest.PriorityCode.newBuilder()
            .setValue(
              RequestPriorityCode.Value.valueOf(
                priority.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setDoNotPerform(doNotPerformElement.toProto())
        .addAllMedium(medium.map { it.toProto() })
        .setSubject(subject.toProto())
        .addAllAbout(about.map { it.toProto() })
        .setEncounter(encounter.toProto())
        .addAllPayload(payload.map { it.toProto() })
        .setOccurrence(occurrence.communicationRequestOccurrenceToProto())
        .setAuthoredOn(authoredOnElement.toProto())
        .setRequester(requester.toProto())
        .addAllRecipient(recipient.map { it.toProto() })
        .setSender(sender.toProto())
        .addAllReasonCode(reasonCode.map { it.toProto() })
        .addAllReasonReference(reasonReference.map { it.toProto() })
        .addAllNote(note.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.CommunicationRequest.CommunicationRequestPayloadComponent.toProto():
    CommunicationRequest.Payload {
    val protoValue =
      CommunicationRequest.Payload.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setContent(content.communicationRequestPayloadContentToProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun CommunicationRequest.Payload.toHapi():
    org.hl7.fhir.r4.model.CommunicationRequest.CommunicationRequestPayloadComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.CommunicationRequest.CommunicationRequestPayloadComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setContent(content.communicationRequestPayloadContentToHapi())
    return hapiValue
  }
}
