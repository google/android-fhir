package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toProto
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
import com.google.fhir.r4.core.CompartmentTypeCode
import com.google.fhir.r4.core.GraphCompartmentRuleCode
import com.google.fhir.r4.core.GraphCompartmentUseCode
import com.google.fhir.r4.core.GraphDefinition
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.ResourceTypeCode
import com.google.fhir.r4.core.String
import org.hl7.fhir.r4.model.Enumerations

public object GraphDefinitionConverter {
  public fun GraphDefinition.toHapi(): org.hl7.fhir.r4.model.GraphDefinition {
    val hapiValue = org.hl7.fhir.r4.model.GraphDefinition()
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
    hapiValue.setJurisdiction(jurisdictionList.map{it.toHapi()})
    hapiValue.setPurposeElement(purpose.toHapi())
    hapiValue.setStart(Enumerations.ResourceType.valueOf(start.value.name.replace("_","")))
    hapiValue.setProfileElement(profile.toHapi())
    hapiValue.setLink(linkList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.GraphDefinition.toProto(): GraphDefinition {
    val protoValue = GraphDefinition.newBuilder()
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
    .setStatus(GraphDefinition.StatusCode.newBuilder().setValue(PublicationStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setExperimental(experimentalElement.toProto())
    .setDate(dateElement.toProto())
    .setPublisher(publisherElement.toProto())
    .addAllContact(contact.map{it.toProto()})
    .setDescription(descriptionElement.toProto())
    .addAllUseContext(useContext.map{it.toProto()})
    .addAllJurisdiction(jurisdiction.map{it.toProto()})
    .setPurpose(purposeElement.toProto())
    .setStart(GraphDefinition.StartCode.newBuilder().setValue(ResourceTypeCode.Value.valueOf(start.toCode().replace("-",
        "_").toUpperCase())).build())
    .setProfile(profileElement.toProto())
    .addAllLink(link.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.GraphDefinition.GraphDefinitionLinkComponent.toProto():
      GraphDefinition.Link {
    val protoValue = GraphDefinition.Link.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setPath(pathElement.toProto())
    .setSliceName(sliceNameElement.toProto())
    .setMin(minElement.toProto())
    .setMax(maxElement.toProto())
    .setDescription(descriptionElement.toProto())
    .addAllTarget(target.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.GraphDefinition.GraphDefinitionLinkTargetComponent.toProto():
      GraphDefinition.Link.Target {
    val protoValue = GraphDefinition.Link.Target.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(GraphDefinition.Link.Target.TypeCode.newBuilder().setValue(ResourceTypeCode.Value.valueOf(type.toCode().replace("-",
        "_").toUpperCase())).build())
    .setParams(paramsElement.toProto())
    .setProfile(profileElement.toProto())
    .addAllCompartment(compartment.map{it.toProto()})
    .addAllLink(link.map{it.toProto()})
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.GraphDefinition.GraphDefinitionLinkTargetCompartmentComponent.toProto():
      GraphDefinition.Link.Target.Compartment {
    val protoValue = GraphDefinition.Link.Target.Compartment.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setUse(GraphDefinition.Link.Target.Compartment.UseCode.newBuilder().setValue(GraphCompartmentUseCode.Value.valueOf(use.toCode().replace("-",
        "_").toUpperCase())).build())
    .setCode(GraphDefinition.Link.Target.Compartment.CodeCode.newBuilder().setValue(CompartmentTypeCode.Value.valueOf(code.toCode().replace("-",
        "_").toUpperCase())).build())
    .setRule(GraphDefinition.Link.Target.Compartment.RuleCode.newBuilder().setValue(GraphCompartmentRuleCode.Value.valueOf(rule.toCode().replace("-",
        "_").toUpperCase())).build())
    .setExpression(expressionElement.toProto())
    .setDescription(descriptionElement.toProto())
    .build()
    return protoValue
  }

  public fun GraphDefinition.Link.toHapi():
      org.hl7.fhir.r4.model.GraphDefinition.GraphDefinitionLinkComponent {
    val hapiValue = org.hl7.fhir.r4.model.GraphDefinition.GraphDefinitionLinkComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setPathElement(path.toHapi())
    hapiValue.setSliceNameElement(sliceName.toHapi())
    hapiValue.setMinElement(min.toHapi())
    hapiValue.setMaxElement(max.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setTarget(targetList.map{it.toHapi()})
    return hapiValue
  }

  public fun GraphDefinition.Link.Target.toHapi():
      org.hl7.fhir.r4.model.GraphDefinition.GraphDefinitionLinkTargetComponent {
    val hapiValue = org.hl7.fhir.r4.model.GraphDefinition.GraphDefinitionLinkTargetComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(Enumerations.ResourceType.valueOf(type.value.name.replace("_","")))
    hapiValue.setParamsElement(params.toHapi())
    hapiValue.setProfileElement(profile.toHapi())
    hapiValue.setCompartment(compartmentList.map{it.toHapi()})
    hapiValue.setLink(linkList.map{it.toHapi()})
    return hapiValue
  }

  public fun GraphDefinition.Link.Target.Compartment.toHapi():
      org.hl7.fhir.r4.model.GraphDefinition.GraphDefinitionLinkTargetCompartmentComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.GraphDefinition.GraphDefinitionLinkTargetCompartmentComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setUse(org.hl7.fhir.r4.model.GraphDefinition.GraphCompartmentUse.valueOf(use.value.name.replace("_","")))
    hapiValue.setCode(org.hl7.fhir.r4.model.GraphDefinition.CompartmentCode.valueOf(code.value.name.replace("_","")))
    hapiValue.setRule(org.hl7.fhir.r4.model.GraphDefinition.GraphCompartmentRule.valueOf(rule.value.name.replace("_","")))
    hapiValue.setExpressionElement(expression.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    return hapiValue
  }
}
