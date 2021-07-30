package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MoneyConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MoneyConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toProto
import com.google.fhir.r4.core.ChargeItemDefinition
import com.google.fhir.r4.core.ChargeItemDefinition.PropertyGroup
import com.google.fhir.r4.core.ChargeItemDefinition.PropertyGroup.PriceComponent
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.InvoicePriceComponentTypeCode
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.String
import org.hl7.fhir.r4.model.Enumerations

public object ChargeItemDefinitionConverter {
  public fun ChargeItemDefinition.toHapi(): org.hl7.fhir.r4.model.ChargeItemDefinition {
    val hapiValue = org.hl7.fhir.r4.model.ChargeItemDefinition()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setUrlElement(url.toHapi())
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setVersionElement(version.toHapi())
    hapiValue.setTitleElement(title.toHapi())
    hapiValue.setDerivedFromUri(derivedFromUriList.map{it.toHapi()})
    hapiValue.setPartOf(partOfList.map{it.toHapi()})
    hapiValue.setReplaces(replacesList.map{it.toHapi()})
    hapiValue.setStatus(Enumerations.PublicationStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setExperimentalElement(experimental.toHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setPublisherElement(publisher.toHapi())
    hapiValue.setContact(contactList.map{it.toHapi()})
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setUseContext(useContextList.map{it.toHapi()})
    hapiValue.setJurisdiction(jurisdictionList.map{it.toHapi()})
    hapiValue.setCopyrightElement(copyright.toHapi())
    hapiValue.setApprovalDateElement(approvalDate.toHapi())
    hapiValue.setLastReviewDateElement(lastReviewDate.toHapi())
    hapiValue.setEffectivePeriod(effectivePeriod.toHapi())
    hapiValue.setCode(code.toHapi())
    hapiValue.setInstance(instanceList.map{it.toHapi()})
    hapiValue.setApplicability(applicabilityList.map{it.toHapi()})
    hapiValue.setPropertyGroup(propertyGroupList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.ChargeItemDefinition.toProto(): ChargeItemDefinition {
    val protoValue = ChargeItemDefinition.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setUrl(urlElement.toProto())
    .addAllIdentifier(identifier.map{it.toProto()})
    .setVersion(versionElement.toProto())
    .setTitle(titleElement.toProto())
    .addAllDerivedFromUri(derivedFromUri.map{it.toProto()})
    .addAllPartOf(partOf.map{it.toProto()})
    .addAllReplaces(replaces.map{it.toProto()})
    .setStatus(ChargeItemDefinition.StatusCode.newBuilder().setValue(PublicationStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setExperimental(experimentalElement.toProto())
    .setDate(dateElement.toProto())
    .setPublisher(publisherElement.toProto())
    .addAllContact(contact.map{it.toProto()})
    .setDescription(descriptionElement.toProto())
    .addAllUseContext(useContext.map{it.toProto()})
    .addAllJurisdiction(jurisdiction.map{it.toProto()})
    .setCopyright(copyrightElement.toProto())
    .setApprovalDate(approvalDateElement.toProto())
    .setLastReviewDate(lastReviewDateElement.toProto())
    .setEffectivePeriod(effectivePeriod.toProto())
    .setCode(code.toProto())
    .addAllInstance(instance.map{it.toProto()})
    .addAllApplicability(applicability.map{it.toProto()})
    .addAllPropertyGroup(propertyGroup.map{it.toProto()})
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.ChargeItemDefinition.ChargeItemDefinitionApplicabilityComponent.toProto():
      ChargeItemDefinition.Applicability {
    val protoValue = ChargeItemDefinition.Applicability.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setDescription(descriptionElement.toProto())
    .setLanguage(languageElement.toProto())
    .setExpression(expressionElement.toProto())
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.ChargeItemDefinition.ChargeItemDefinitionPropertyGroupComponent.toProto():
      ChargeItemDefinition.PropertyGroup {
    val protoValue = ChargeItemDefinition.PropertyGroup.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllPriceComponent(priceComponent.map{it.toProto()})
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.ChargeItemDefinition.ChargeItemDefinitionPropertyGroupPriceComponentComponent.toProto():
      ChargeItemDefinition.PropertyGroup.PriceComponent {
    val protoValue = ChargeItemDefinition.PropertyGroup.PriceComponent.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(ChargeItemDefinition.PropertyGroup.PriceComponent.TypeCode.newBuilder().setValue(InvoicePriceComponentTypeCode.Value.valueOf(type.toCode().replace("-",
        "_").toUpperCase())).build())
    .setCode(code.toProto())
    .setFactor(factorElement.toProto())
    .setAmount(amount.toProto())
    .build()
    return protoValue
  }

  private fun ChargeItemDefinition.Applicability.toHapi():
      org.hl7.fhir.r4.model.ChargeItemDefinition.ChargeItemDefinitionApplicabilityComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.ChargeItemDefinition.ChargeItemDefinitionApplicabilityComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setExpressionElement(expression.toHapi())
    return hapiValue
  }

  private fun ChargeItemDefinition.PropertyGroup.toHapi():
      org.hl7.fhir.r4.model.ChargeItemDefinition.ChargeItemDefinitionPropertyGroupComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.ChargeItemDefinition.ChargeItemDefinitionPropertyGroupComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setPriceComponent(priceComponentList.map{it.toHapi()})
    return hapiValue
  }

  private fun ChargeItemDefinition.PropertyGroup.PriceComponent.toHapi():
      org.hl7.fhir.r4.model.ChargeItemDefinition.ChargeItemDefinitionPropertyGroupPriceComponentComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.ChargeItemDefinition.ChargeItemDefinitionPropertyGroupPriceComponentComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(org.hl7.fhir.r4.model.ChargeItemDefinition.ChargeItemDefinitionPriceComponentType.valueOf(type.value.name.replace("_","")))
    hapiValue.setCode(code.toHapi())
    hapiValue.setFactorElement(factor.toHapi())
    hapiValue.setAmount(amount.toHapi())
    return hapiValue
  }
}
