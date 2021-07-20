package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UrlConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UrlConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toProto
import com.google.fhir.r4.core.CapabilityStatement
import com.google.fhir.r4.core.CapabilityStatementKindCode
import com.google.fhir.r4.core.ConditionalDeleteStatusCode
import com.google.fhir.r4.core.ConditionalReadStatusCode
import com.google.fhir.r4.core.DocumentModeCode
import com.google.fhir.r4.core.EventCapabilityModeCode
import com.google.fhir.r4.core.FHIRVersionCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.ReferenceHandlingPolicyCode
import com.google.fhir.r4.core.ResourceTypeCode
import com.google.fhir.r4.core.ResourceVersionPolicyCode
import com.google.fhir.r4.core.RestfulCapabilityModeCode
import com.google.fhir.r4.core.SearchParamTypeCode
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.SystemRestfulInteractionValueSet
import com.google.fhir.r4.core.TypeRestfulInteractionValueSet
import org.hl7.fhir.r4.model.Enumerations

public object CapabilityStatementConverter {
  public fun CapabilityStatement.toHapi(): org.hl7.fhir.r4.model.CapabilityStatement {
    val hapiValue = org.hl7.fhir.r4.model.CapabilityStatement()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setUrlElement(url.toHapi())
    hapiValue.setVersionElement(version.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setTitleElement(title.toHapi())
    hapiValue.setStatus(Enumerations.PublicationStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setExperimentalElement(experimental.toHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setPublisherElement(publisher.toHapi())
    hapiValue.setContact(contactList.map{it.toHapi()})
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setUseContext(useContextList.map{it.toHapi()})
    hapiValue.setJurisdiction(jurisdictionList.map{it.toHapi()})
    hapiValue.setPurposeElement(purpose.toHapi())
    hapiValue.setCopyrightElement(copyright.toHapi())
    hapiValue.setKind(org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementKind.valueOf(kind.value.name.replace("_","")))
    hapiValue.setInstantiates(instantiatesList.map{it.toHapi()})
    hapiValue.setImports(importsList.map{it.toHapi()})
    hapiValue.setSoftware(software.toHapi())
    hapiValue.setImplementation(implementation.toHapi())
    hapiValue.setFhirVersion(Enumerations.FHIRVersion.valueOf(fhirVersion.value.name.replace("_","")))
    formatList.map{hapiValue.addFormat(it)}
    patchFormatList.map{hapiValue.addPatchFormat(it)}
    hapiValue.setImplementationGuide(implementationGuideList.map{it.toHapi()})
    hapiValue.setRest(restList.map{it.toHapi()})
    hapiValue.setMessaging(messagingList.map{it.toHapi()})
    hapiValue.setDocument(documentList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.CapabilityStatement.toProto(): CapabilityStatement {
    val protoValue = CapabilityStatement.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setUrl(urlElement.toProto())
    .setVersion(versionElement.toProto())
    .setName(nameElement.toProto())
    .setTitle(titleElement.toProto())
    .setStatus(CapabilityStatement.StatusCode.newBuilder().setValue(PublicationStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setExperimental(experimentalElement.toProto())
    .setDate(dateElement.toProto())
    .setPublisher(publisherElement.toProto())
    .addAllContact(contact.map{it.toProto()})
    .setDescription(descriptionElement.toProto())
    .addAllUseContext(useContext.map{it.toProto()})
    .addAllJurisdiction(jurisdiction.map{it.toProto()})
    .setPurpose(purposeElement.toProto())
    .setCopyright(copyrightElement.toProto())
    .setKind(CapabilityStatement.KindCode.newBuilder().setValue(CapabilityStatementKindCode.Value.valueOf(kind.toCode().replace("-",
        "_").toUpperCase())).build())
    .addAllInstantiates(instantiates.map{it.toProto()})
    .addAllImports(imports.map{it.toProto()})
    .setSoftware(software.toProto())
    .setImplementation(implementation.toProto())
    .setFhirVersion(CapabilityStatement.FhirVersionCode.newBuilder().setValue(FHIRVersionCode.Value.valueOf(fhirVersion.toCode().replace("-",
        "_").toUpperCase())).build())
    .addAllFormat(format.map{CapabilityStatement.FormatCode.newBuilder().setValue(it).build()})
    .addAllPatchFormat(patchFormat.map{CapabilityStatement.PatchFormatCode.newBuilder().setValue(it).build()})
    .addAllImplementationGuide(implementationGuide.map{it.toProto()})
    .addAllRest(rest.map{it.toProto()})
    .addAllMessaging(messaging.map{it.toProto()})
    .addAllDocument(document.map{it.toProto()})
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementSoftwareComponent.toProto():
      CapabilityStatement.Software {
    val protoValue = CapabilityStatement.Software.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setName(nameElement.toProto())
    .setVersion(versionElement.toProto())
    .setReleaseDate(releaseDateElement.toProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementImplementationComponent.toProto():
      CapabilityStatement.Implementation {
    val protoValue = CapabilityStatement.Implementation.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setDescription(descriptionElement.toProto())
    .setUrl(urlElement.toProto())
    .setCustodian(custodian.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestComponent.toProto():
      CapabilityStatement.Rest {
    val protoValue = CapabilityStatement.Rest.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setMode(CapabilityStatement.Rest.ModeCode.newBuilder().setValue(RestfulCapabilityModeCode.Value.valueOf(mode.toCode().replace("-",
        "_").toUpperCase())).build())
    .setDocumentation(documentationElement.toProto())
    .setSecurity(security.toProto())
    .addAllResource(resource.map{it.toProto()})
    .addAllSystemInteraction(systemInteraction.map{it.toProto()})
    .addAllSearchParam(searchParam.map{it.toProto()})
    .addAllOperation(operation.map{it.toProto()})
    .addAllCompartment(compartment.map{it.toProto()})
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestSecurityComponent.toProto():
      CapabilityStatement.Rest.Security {
    val protoValue = CapabilityStatement.Rest.Security.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setCors(corsElement.toProto())
    .addAllService(service.map{it.toProto()})
    .setDescription(descriptionElement.toProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestResourceComponent.toProto():
      CapabilityStatement.Rest.Resource {
    val protoValue = CapabilityStatement.Rest.Resource.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(CapabilityStatement.Rest.Resource.TypeCode.newBuilder().setValue(ResourceTypeCode.Value.valueOf(type.toCode().replace("-",
        "_").toUpperCase())).build())
    .setProfile(profileElement.toProto())
    .addAllSupportedProfile(supportedProfile.map{it.toProto()})
    .setDocumentation(documentationElement.toProto())
    .addAllResourceInteraction(resourceInteraction.map{it.toProto()})
    .setVersioning(CapabilityStatement.Rest.Resource.VersioningCode.newBuilder().setValue(ResourceVersionPolicyCode.Value.valueOf(versioning.toCode().replace("-",
        "_").toUpperCase())).build())
    .setReadHistory(readHistoryElement.toProto())
    .setUpdateCreate(updateCreateElement.toProto())
    .setConditionalCreate(conditionalCreateElement.toProto())
    .setConditionalRead(CapabilityStatement.Rest.Resource.ConditionalReadCode.newBuilder().setValue(ConditionalReadStatusCode.Value.valueOf(conditionalRead.toCode().replace("-",
        "_").toUpperCase())).build())
    .setConditionalUpdate(conditionalUpdateElement.toProto())
    .setConditionalDelete(CapabilityStatement.Rest.Resource.ConditionalDeleteCode.newBuilder().setValue(ConditionalDeleteStatusCode.Value.valueOf(conditionalDelete.toCode().replace("-",
        "_").toUpperCase())).build())
    .addAllReferencePolicy(referencePolicy.map{CapabilityStatement.Rest.Resource.ReferencePolicyCode.newBuilder().setValue(ReferenceHandlingPolicyCode.Value.valueOf(it.value.toCode().replace("-",
        "_").toUpperCase())).build()})
    .addAllSearchInclude(searchInclude.map{it.toProto()})
    .addAllSearchRevInclude(searchRevInclude.map{it.toProto()})
    .addAllSearchParam(searchParam.map{it.toProto()})
    .addAllOperation(operation.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.CapabilityStatement.ResourceInteractionComponent.toProto():
      CapabilityStatement.Rest.Resource.ResourceInteraction {
    val protoValue = CapabilityStatement.Rest.Resource.ResourceInteraction.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setCode(CapabilityStatement.Rest.Resource.Interaction.CodeCode.newBuilder().setValue(TypeRestfulInteractionValueSet.Value.valueOf(code.toCode().replace("-",
        "_").toUpperCase())).build())
    .setDocumentation(documentationElement.toProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestResourceSearchParamComponent.toProto():
      CapabilityStatement.Rest.Resource.SearchParam {
    val protoValue = CapabilityStatement.Rest.Resource.SearchParam.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setName(nameElement.toProto())
    .setDefinition(definitionElement.toProto())
    .setType(CapabilityStatement.Rest.Resource.SearchParam.TypeCode.newBuilder().setValue(SearchParamTypeCode.Value.valueOf(type.toCode().replace("-",
        "_").toUpperCase())).build())
    .setDocumentation(documentationElement.toProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestResourceOperationComponent.toProto():
      CapabilityStatement.Rest.Resource.Operation {
    val protoValue = CapabilityStatement.Rest.Resource.Operation.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setName(nameElement.toProto())
    .setDefinition(definitionElement.toProto())
    .setDocumentation(documentationElement.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.CapabilityStatement.SystemInteractionComponent.toProto():
      CapabilityStatement.Rest.SystemInteraction {
    val protoValue = CapabilityStatement.Rest.SystemInteraction.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setCode(CapabilityStatement.Rest.Interaction.CodeCode.newBuilder().setValue(SystemRestfulInteractionValueSet.Value.valueOf(code.toCode().replace("-",
        "_").toUpperCase())).build())
    .setDocumentation(documentationElement.toProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementMessagingComponent.toProto():
      CapabilityStatement.Messaging {
    val protoValue = CapabilityStatement.Messaging.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllEndpoint(endpoint.map{it.toProto()})
    .setReliableCache(reliableCacheElement.toProto())
    .setDocumentation(documentationElement.toProto())
    .addAllSupportedMessage(supportedMessage.map{it.toProto()})
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementMessagingEndpointComponent.toProto():
      CapabilityStatement.Messaging.Endpoint {
    val protoValue = CapabilityStatement.Messaging.Endpoint.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setProtocol(protocol.toProto())
    .setAddress(addressElement.toProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementMessagingSupportedMessageComponent.toProto():
      CapabilityStatement.Messaging.SupportedMessage {
    val protoValue = CapabilityStatement.Messaging.SupportedMessage.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setMode(CapabilityStatement.Messaging.SupportedMessage.ModeCode.newBuilder().setValue(EventCapabilityModeCode.Value.valueOf(mode.toCode().replace("-",
        "_").toUpperCase())).build())
    .setDefinition(definitionElement.toProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementDocumentComponent.toProto():
      CapabilityStatement.Document {
    val protoValue = CapabilityStatement.Document.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setMode(CapabilityStatement.Document.ModeCode.newBuilder().setValue(DocumentModeCode.Value.valueOf(mode.toCode().replace("-",
        "_").toUpperCase())).build())
    .setDocumentation(documentationElement.toProto())
    .setProfile(profileElement.toProto())
    .build()
    return protoValue
  }

  public fun CapabilityStatement.Software.toHapi():
      org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementSoftwareComponent {
    val hapiValue = org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementSoftwareComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setVersionElement(version.toHapi())
    hapiValue.setReleaseDateElement(releaseDate.toHapi())
    return hapiValue
  }

  public fun CapabilityStatement.Implementation.toHapi():
      org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementImplementationComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementImplementationComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setUrlElement(url.toHapi())
    hapiValue.setCustodian(custodian.toHapi())
    return hapiValue
  }

  public fun CapabilityStatement.Rest.toHapi():
      org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestComponent {
    val hapiValue = org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setMode(org.hl7.fhir.r4.model.CapabilityStatement.RestfulCapabilityMode.valueOf(mode.value.name.replace("_","")))
    hapiValue.setDocumentationElement(documentation.toHapi())
    hapiValue.setSecurity(security.toHapi())
    hapiValue.setResource(resourceList.map{it.toHapi()})
    hapiValue.setSystemInteraction(systemInteractionList.map{it.toHapi()})
    hapiValue.setSearchParam(searchParamList.map{it.toHapi()})
    hapiValue.setOperation(operationList.map{it.toHapi()})
    hapiValue.setCompartment(compartmentList.map{it.toHapi()})
    return hapiValue
  }

  public fun CapabilityStatement.Rest.Security.toHapi():
      org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestSecurityComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestSecurityComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCorsElement(cors.toHapi())
    hapiValue.setService(serviceList.map{it.toHapi()})
    hapiValue.setDescriptionElement(description.toHapi())
    return hapiValue
  }

  public fun CapabilityStatement.Rest.Resource.toHapi():
      org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestResourceComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestResourceComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(Enumerations.ResourceType.valueOf(type.value.name.replace("_","")))
    hapiValue.setProfileElement(profile.toHapi())
    hapiValue.setSupportedProfile(supportedProfileList.map{it.toHapi()})
    hapiValue.setDocumentationElement(documentation.toHapi())
    hapiValue.setResourceInteraction(resourceInteractionList.map{it.toHapi()})
    hapiValue.setVersioning(org.hl7.fhir.r4.model.CapabilityStatement.ResourceVersionPolicy.valueOf(versioning.value.name.replace("_","")))
    hapiValue.setReadHistoryElement(readHistory.toHapi())
    hapiValue.setUpdateCreateElement(updateCreate.toHapi())
    hapiValue.setConditionalCreateElement(conditionalCreate.toHapi())
    hapiValue.setConditionalRead(org.hl7.fhir.r4.model.CapabilityStatement.ConditionalReadStatus.valueOf(conditionalRead.value.name.replace("_","")))
    hapiValue.setConditionalUpdateElement(conditionalUpdate.toHapi())
    hapiValue.setConditionalDelete(org.hl7.fhir.r4.model.CapabilityStatement.ConditionalDeleteStatus.valueOf(conditionalDelete.value.name.replace("_","")))
    referencePolicyList.map{hapiValue.addReferencePolicy(org.hl7.fhir.r4.model.CapabilityStatement.ReferenceHandlingPolicy.valueOf(it.value.name.replace("_","")))}
    hapiValue.setSearchInclude(searchIncludeList.map{it.toHapi()})
    hapiValue.setSearchRevInclude(searchRevIncludeList.map{it.toHapi()})
    hapiValue.setSearchParam(searchParamList.map{it.toHapi()})
    hapiValue.setOperation(operationList.map{it.toHapi()})
    return hapiValue
  }

  public fun CapabilityStatement.Rest.Resource.ResourceInteraction.toHapi():
      org.hl7.fhir.r4.model.CapabilityStatement.ResourceInteractionComponent {
    val hapiValue = org.hl7.fhir.r4.model.CapabilityStatement.ResourceInteractionComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCode(org.hl7.fhir.r4.model.CapabilityStatement.TypeRestfulInteraction.valueOf(code.value.name.replace("_","")))
    hapiValue.setDocumentationElement(documentation.toHapi())
    return hapiValue
  }

  public fun CapabilityStatement.Rest.Resource.SearchParam.toHapi():
      org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestResourceSearchParamComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestResourceSearchParamComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setDefinitionElement(definition.toHapi())
    hapiValue.setType(Enumerations.SearchParamType.valueOf(type.value.name.replace("_","")))
    hapiValue.setDocumentationElement(documentation.toHapi())
    return hapiValue
  }

  public fun CapabilityStatement.Rest.Resource.Operation.toHapi():
      org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestResourceOperationComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestResourceOperationComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setDefinitionElement(definition.toHapi())
    hapiValue.setDocumentationElement(documentation.toHapi())
    return hapiValue
  }

  public fun CapabilityStatement.Rest.SystemInteraction.toHapi():
      org.hl7.fhir.r4.model.CapabilityStatement.SystemInteractionComponent {
    val hapiValue = org.hl7.fhir.r4.model.CapabilityStatement.SystemInteractionComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCode(org.hl7.fhir.r4.model.CapabilityStatement.SystemRestfulInteraction.valueOf(code.value.name.replace("_","")))
    hapiValue.setDocumentationElement(documentation.toHapi())
    return hapiValue
  }

  public fun CapabilityStatement.Messaging.toHapi():
      org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementMessagingComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementMessagingComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setEndpoint(endpointList.map{it.toHapi()})
    hapiValue.setReliableCacheElement(reliableCache.toHapi())
    hapiValue.setDocumentationElement(documentation.toHapi())
    hapiValue.setSupportedMessage(supportedMessageList.map{it.toHapi()})
    return hapiValue
  }

  public fun CapabilityStatement.Messaging.Endpoint.toHapi():
      org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementMessagingEndpointComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementMessagingEndpointComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setProtocol(protocol.toHapi())
    hapiValue.setAddressElement(address.toHapi())
    return hapiValue
  }

  public fun CapabilityStatement.Messaging.SupportedMessage.toHapi():
      org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementMessagingSupportedMessageComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementMessagingSupportedMessageComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setMode(org.hl7.fhir.r4.model.CapabilityStatement.EventCapabilityMode.valueOf(mode.value.name.replace("_","")))
    hapiValue.setDefinitionElement(definition.toHapi())
    return hapiValue
  }

  public fun CapabilityStatement.Document.toHapi():
      org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementDocumentComponent {
    val hapiValue = org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementDocumentComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setMode(org.hl7.fhir.r4.model.CapabilityStatement.DocumentMode.valueOf(mode.value.name.replace("_","")))
    hapiValue.setDocumentationElement(documentation.toHapi())
    hapiValue.setProfileElement(profile.toHapi())
    return hapiValue
  }
}
