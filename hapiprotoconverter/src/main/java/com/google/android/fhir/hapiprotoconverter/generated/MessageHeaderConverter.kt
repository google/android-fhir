package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.UrlConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UrlConverter.toProto
import com.google.fhir.r4.core.Coding
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.MessageHeader
import com.google.fhir.r4.core.ResponseTypeCode
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.Uri
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UriType

public object MessageHeaderConverter {
  public fun MessageHeader.EventX.messageHeaderEventToHapi(): Type {
    if (this.getCoding() != Coding.newBuilder().defaultInstanceForType ) {
      return (this.getCoding()).toHapi()
    }
    if (this.getUri() != Uri.newBuilder().defaultInstanceForType ) {
      return (this.getUri()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for MessageHeader.event[x]")
  }

  public fun Type.messageHeaderEventToProto(): MessageHeader.EventX {
    val protoValue = MessageHeader.EventX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Coding) {
      protoValue.setCoding(this.toProto())
    }
    if (this is UriType) {
      protoValue.setUri(this.toProto())
    }
    return protoValue.build()
  }

  public fun MessageHeader.toHapi(): org.hl7.fhir.r4.model.MessageHeader {
    val hapiValue = org.hl7.fhir.r4.model.MessageHeader()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setEvent(event.messageHeaderEventToHapi())
    hapiValue.setMessageDestination(messageDestinationList.map{it.toHapi()})
    hapiValue.setSender(sender.toHapi())
    hapiValue.setEnterer(enterer.toHapi())
    hapiValue.setAuthor(author.toHapi())
    hapiValue.setMessageSource(messageSource.toHapi())
    hapiValue.setResponsible(responsible.toHapi())
    hapiValue.setReason(reason.toHapi())
    hapiValue.setResponse(response.toHapi())
    hapiValue.setFocus(focusList.map{it.toHapi()})
    hapiValue.setDefinitionElement(definition.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.MessageHeader.toProto(): MessageHeader {
    val protoValue = MessageHeader.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setEvent(event.messageHeaderEventToProto())
    .addAllMessageDestination(messageDestination.map{it.toProto()})
    .setSender(sender.toProto())
    .setEnterer(enterer.toProto())
    .setAuthor(author.toProto())
    .setMessageSource(messageSource.toProto())
    .setResponsible(responsible.toProto())
    .setReason(reason.toProto())
    .setResponse(response.toProto())
    .addAllFocus(focus.map{it.toProto()})
    .setDefinition(definitionElement.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.MessageHeader.MessageDestinationComponent.toProto():
      MessageHeader.MessageDestination {
    val protoValue = MessageHeader.MessageDestination.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setName(nameElement.toProto())
    .setTarget(target.toProto())
    .setEndpoint(endpointElement.toProto())
    .setReceiver(receiver.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.MessageHeader.MessageSourceComponent.toProto():
      MessageHeader.MessageSource {
    val protoValue = MessageHeader.MessageSource.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setName(nameElement.toProto())
    .setSoftware(softwareElement.toProto())
    .setVersion(versionElement.toProto())
    .setContact(contact.toProto())
    .setEndpoint(endpointElement.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.MessageHeader.MessageHeaderResponseComponent.toProto():
      MessageHeader.Response {
    val protoValue = MessageHeader.Response.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setIdentifier(identifierElement.toProto())
    .setCode(MessageHeader.Response.CodeCode.newBuilder().setValue(ResponseTypeCode.Value.valueOf(code.toCode().replace("-",
        "_").toUpperCase())).build())
    .setDetails(details.toProto())
    .build()
    return protoValue
  }

  public fun MessageHeader.MessageDestination.toHapi():
      org.hl7.fhir.r4.model.MessageHeader.MessageDestinationComponent {
    val hapiValue = org.hl7.fhir.r4.model.MessageHeader.MessageDestinationComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setTarget(target.toHapi())
    hapiValue.setEndpointElement(endpoint.toHapi())
    hapiValue.setReceiver(receiver.toHapi())
    return hapiValue
  }

  public fun MessageHeader.MessageSource.toHapi():
      org.hl7.fhir.r4.model.MessageHeader.MessageSourceComponent {
    val hapiValue = org.hl7.fhir.r4.model.MessageHeader.MessageSourceComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setSoftwareElement(software.toHapi())
    hapiValue.setVersionElement(version.toHapi())
    hapiValue.setContact(contact.toHapi())
    hapiValue.setEndpointElement(endpoint.toHapi())
    return hapiValue
  }

  public fun MessageHeader.Response.toHapi():
      org.hl7.fhir.r4.model.MessageHeader.MessageHeaderResponseComponent {
    val hapiValue = org.hl7.fhir.r4.model.MessageHeader.MessageHeaderResponseComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifierElement(identifier.toHapi())
    hapiValue.setCode(org.hl7.fhir.r4.model.MessageHeader.ResponseType.valueOf(code.value.name.replace("_","")))
    hapiValue.setDetails(details.toHapi())
    return hapiValue
  }
}
