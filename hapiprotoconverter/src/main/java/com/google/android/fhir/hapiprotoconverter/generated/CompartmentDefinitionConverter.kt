package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toProto
import com.google.fhir.r4.core.CompartmentDefinition
import com.google.fhir.r4.core.CompartmentTypeCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.ResourceTypeCode
import com.google.fhir.r4.core.String
import org.hl7.fhir.r4.model.Enumerations

public object CompartmentDefinitionConverter {
  public fun CompartmentDefinition.toHapi(): org.hl7.fhir.r4.model.CompartmentDefinition {
    val hapiValue = org.hl7.fhir.r4.model.CompartmentDefinition()
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
    hapiValue.setStatus(Enumerations.PublicationStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setExperimentalElement(experimental.toHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setPublisherElement(publisher.toHapi())
    hapiValue.setContact(contactList.map{it.toHapi()})
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setUseContext(useContextList.map{it.toHapi()})
    hapiValue.setPurposeElement(purpose.toHapi())
    hapiValue.setCode(org.hl7.fhir.r4.model.CompartmentDefinition.CompartmentType.valueOf(code.value.name.replace("_","")))
    hapiValue.setSearchElement(search.toHapi())
    hapiValue.setResource(resourceList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.CompartmentDefinition.toProto(): CompartmentDefinition {
    val protoValue = CompartmentDefinition.newBuilder()
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
    .setStatus(CompartmentDefinition.StatusCode.newBuilder().setValue(PublicationStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setExperimental(experimentalElement.toProto())
    .setDate(dateElement.toProto())
    .setPublisher(publisherElement.toProto())
    .addAllContact(contact.map{it.toProto()})
    .setDescription(descriptionElement.toProto())
    .addAllUseContext(useContext.map{it.toProto()})
    .setPurpose(purposeElement.toProto())
    .setCode(CompartmentDefinition.CodeType.newBuilder().setValue(CompartmentTypeCode.Value.valueOf(code.toCode().replace("-",
        "_").toUpperCase())).build())
    .setSearch(searchElement.toProto())
    .addAllResource(resource.map{it.toProto()})
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.CompartmentDefinition.CompartmentDefinitionResourceComponent.toProto():
      CompartmentDefinition.Resource {
    val protoValue = CompartmentDefinition.Resource.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setCode(CompartmentDefinition.Resource.CodeType.newBuilder().setValue(ResourceTypeCode.Value.valueOf(code.replace("-",
        "_").toUpperCase())).build())
    .addAllParam(param.map{it.toProto()})
    .setDocumentation(documentationElement.toProto())
    .build()
    return protoValue
  }

  public fun CompartmentDefinition.Resource.toHapi():
      org.hl7.fhir.r4.model.CompartmentDefinition.CompartmentDefinitionResourceComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.CompartmentDefinition.CompartmentDefinitionResourceComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCode(Enumerations.ResourceType.valueOf(code.value.name.replace("_","")).display)
    hapiValue.setParam(paramList.map{it.toHapi()})
    hapiValue.setDocumentationElement(documentation.toHapi())
    return hapiValue
  }
}
