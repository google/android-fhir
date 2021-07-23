package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
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
import com.google.fhir.r4.core.EventStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.RequestPriorityCode
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

public object CommunicationConverter {
  public fun Communication.Payload.ContentX.communicationPayloadContentToHapi(): Type {
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType ) {
      return (this.getStringValue()).toHapi()
    }
    if (this.getAttachment() != Attachment.newBuilder().defaultInstanceForType ) {
      return (this.getAttachment()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Communication.payload.content[x]")
  }

  public fun Type.communicationPayloadContentToProto(): Communication.Payload.ContentX {
    val protoValue = Communication.Payload.ContentX.newBuilder()
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

  public fun Communication.toHapi(): org.hl7.fhir.r4.model.Communication {
    val hapiValue = org.hl7.fhir.r4.model.Communication()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setInstantiatesCanonical(instantiatesCanonicalList.map{it.toHapi()})
    hapiValue.setInstantiatesUri(instantiatesUriList.map{it.toHapi()})
    hapiValue.setBasedOn(basedOnList.map{it.toHapi()})
    hapiValue.setPartOf(partOfList.map{it.toHapi()})
    hapiValue.setInResponseTo(inResponseToList.map{it.toHapi()})
    hapiValue.setStatus(org.hl7.fhir.r4.model.Communication.CommunicationStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setStatusReason(statusReason.toHapi())
    hapiValue.setCategory(categoryList.map{it.toHapi()})
    hapiValue.setPriority(org.hl7.fhir.r4.model.Communication.CommunicationPriority.valueOf(priority.value.name.replace("_","")))
    hapiValue.setMedium(mediumList.map{it.toHapi()})
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setTopic(topic.toHapi())
    hapiValue.setAbout(aboutList.map{it.toHapi()})
    hapiValue.setEncounter(encounter.toHapi())
    hapiValue.setSentElement(sent.toHapi())
    hapiValue.setReceivedElement(received.toHapi())
    hapiValue.setRecipient(recipientList.map{it.toHapi()})
    hapiValue.setSender(sender.toHapi())
    hapiValue.setReasonCode(reasonCodeList.map{it.toHapi()})
    hapiValue.setReasonReference(reasonReferenceList.map{it.toHapi()})
    hapiValue.setPayload(payloadList.map{it.toHapi()})
    hapiValue.setNote(noteList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Communication.toProto(): Communication {
    val protoValue = Communication.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .addAllInstantiatesCanonical(instantiatesCanonical.map{it.toProto()})
    .addAllInstantiatesUri(instantiatesUri.map{it.toProto()})
    .addAllBasedOn(basedOn.map{it.toProto()})
    .addAllPartOf(partOf.map{it.toProto()})
    .addAllInResponseTo(inResponseTo.map{it.toProto()})
    .setStatus(Communication.StatusCode.newBuilder().setValue(EventStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setStatusReason(statusReason.toProto())
    .addAllCategory(category.map{it.toProto()})
    .setPriority(Communication.PriorityCode.newBuilder().setValue(RequestPriorityCode.Value.valueOf(priority.toCode().replace("-",
        "_").toUpperCase())).build())
    .addAllMedium(medium.map{it.toProto()})
    .setSubject(subject.toProto())
    .setTopic(topic.toProto())
    .addAllAbout(about.map{it.toProto()})
    .setEncounter(encounter.toProto())
    .setSent(sentElement.toProto())
    .setReceived(receivedElement.toProto())
    .addAllRecipient(recipient.map{it.toProto()})
    .setSender(sender.toProto())
    .addAllReasonCode(reasonCode.map{it.toProto()})
    .addAllReasonReference(reasonReference.map{it.toProto()})
    .addAllPayload(payload.map{it.toProto()})
    .addAllNote(note.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Communication.CommunicationPayloadComponent.toProto():
      Communication.Payload {
    val protoValue = Communication.Payload.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setContent(content.communicationPayloadContentToProto())
    .build()
    return protoValue
  }

  public fun Communication.Payload.toHapi():
      org.hl7.fhir.r4.model.Communication.CommunicationPayloadComponent {
    val hapiValue = org.hl7.fhir.r4.model.Communication.CommunicationPayloadComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setContent(content.communicationPayloadContentToHapi())
    return hapiValue
  }
}
