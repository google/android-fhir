package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toProto
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
import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.Code
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Decimal
import com.google.fhir.r4.core.FilterOperatorCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Integer
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.Shareablevalueset
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.Uri
import com.google.fhir.r4.core.ValueSet
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UriType

public object ShareablevaluesetConverter {
  public fun ValueSet.Expansion.Parameter.ValueX.valueSetExpansionParameterValueToHapi(): Type {
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType ) {
      return (this.getStringValue()).toHapi()
    }
    if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType ) {
      return (this.getBoolean()).toHapi()
    }
    if (this.getInteger() != Integer.newBuilder().defaultInstanceForType ) {
      return (this.getInteger()).toHapi()
    }
    if (this.getDecimal() != Decimal.newBuilder().defaultInstanceForType ) {
      return (this.getDecimal()).toHapi()
    }
    if (this.getUri() != Uri.newBuilder().defaultInstanceForType ) {
      return (this.getUri()).toHapi()
    }
    if (this.getCode() != Code.newBuilder().defaultInstanceForType ) {
      return (this.getCode()).toHapi()
    }
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType ) {
      return (this.getDateTime()).toHapi()
    }
    throw IllegalArgumentException("ValueSet.expansion.parameter.value[x]")
  }

  public fun Type.valueSetExpansionParameterValueToProto(): ValueSet.Expansion.Parameter.ValueX {
    val protoValue = ValueSet.Expansion.Parameter.ValueX.newBuilder()
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    if (this is BooleanType) {
      protoValue.setBoolean(this.toProto())
    }
    if (this is IntegerType) {
      protoValue.setInteger(this.toProto())
    }
    if (this is DecimalType) {
      protoValue.setDecimal(this.toProto())
    }
    if (this is UriType) {
      protoValue.setUri(this.toProto())
    }
    if (this is CodeType) {
      protoValue.setCode(this.toProto())
    }
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    return protoValue.build()
  }

  public fun Shareablevalueset.toHapi(): org.hl7.fhir.r4.model.Shareablevalueset {
    val hapiValue = org.hl7.fhir.r4.model.Shareablevalueset()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setUrlElement(url.toHapi())
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
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
    hapiValue.setImmutableElement(immutable.toHapi())
    hapiValue.setPurposeElement(purpose.toHapi())
    hapiValue.setCopyrightElement(copyright.toHapi())
    hapiValue.setCompose(compose.toHapi())
    hapiValue.setExpansion(expansion.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Shareablevalueset.toProto(): Shareablevalueset {
    val protoValue = Shareablevalueset.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setUrl(urlElement.toProto())
    .addAllIdentifier(identifier.map{it.toProto()})
    .setVersion(versionElement.toProto())
    .setName(nameElement.toProto())
    .setTitle(titleElement.toProto())
    .setStatus(Shareablevalueset.StatusCode.newBuilder().setValue(PublicationStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setExperimental(experimentalElement.toProto())
    .setDate(dateElement.toProto())
    .setPublisher(publisherElement.toProto())
    .addAllContact(contact.map{it.toProto()})
    .setDescription(descriptionElement.toProto())
    .addAllUseContext(useContext.map{it.toProto()})
    .addAllJurisdiction(jurisdiction.map{it.toProto()})
    .setImmutable(immutableElement.toProto())
    .setPurpose(purposeElement.toProto())
    .setCopyright(copyrightElement.toProto())
    .setCompose(compose.toProto())
    .setExpansion(expansion.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ValueSet.ValueSetComposeComponent.toProto(): ValueSet.Compose {
    val protoValue = ValueSet.Compose.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setLockedDate(lockedDateElement.toProto())
    .setInactive(inactiveElement.toProto())
    .addAllConceptSet(conceptSet.map{it.toProto()})
    .addAllExclude(exclude.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ValueSet.ConceptSetComponent.toProto():
      ValueSet.Compose.ConceptSet {
    val protoValue = ValueSet.Compose.ConceptSet.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setSystem(systemElement.toProto())
    .setVersion(versionElement.toProto())
    .addAllConceptReference(conceptReference.map{it.toProto()})
    .addAllFilter(filter.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ValueSet.ConceptReferenceComponent.toProto():
      ValueSet.Compose.Include.ConceptReference {
    val protoValue = ValueSet.Compose.Include.ConceptReference.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setCode(codeElement.toProto())
    .setDisplay(displayElement.toProto())
    .addAllDesignation(designation.map{it.toProto()})
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.ValueSet.ValueSetComposeIncludeConceptDesignationComponent.toProto():
      ValueSet.Compose.Include.Concept.Designation {
    val protoValue = ValueSet.Compose.Include.Concept.Designation.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setLanguage(languageElement.toProto())
    .setUse(use.toProto())
    .setValue(valueElement.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ValueSet.ValueSetComposeIncludeFilterComponent.toProto():
      ValueSet.Compose.Include.Filter {
    val protoValue = ValueSet.Compose.Include.Filter.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setProperty(propertyElement.toProto())
    .setOp(Shareablevalueset.Compose.Include.Filter.OpCode.newBuilder().setValue(FilterOperatorCode.Value.valueOf(op.toCode().replace("-",
        "_").toUpperCase())).build())
    .setValue(valueElement.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionComponent.toProto():
      ValueSet.Expansion {
    val protoValue = ValueSet.Expansion.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setIdentifier(identifierElement.toProto())
    .setTimestamp(timestampElement.toProto())
    .setTotal(totalElement.toProto())
    .setOffset(offsetElement.toProto())
    .addAllParameter(parameter.map{it.toProto()})
    .addAllContains(contains.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionParameterComponent.toProto():
      ValueSet.Expansion.Parameter {
    val protoValue = ValueSet.Expansion.Parameter.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setName(nameElement.toProto())
    .setValue(value.valueSetExpansionParameterValueToProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionContainsComponent.toProto():
      ValueSet.Expansion.Contains {
    val protoValue = ValueSet.Expansion.Contains.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setSystem(systemElement.toProto())
    .setAbstract(abstractElement.toProto())
    .setInactive(inactiveElement.toProto())
    .setVersion(versionElement.toProto())
    .setCode(codeElement.toProto())
    .setDisplay(displayElement.toProto())
    .addAllDesignation(designation.map{it.toProto()})
    .addAllContains(contains.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun ValueSet.Compose.toHapi(): org.hl7.fhir.r4.model.ValueSet.ValueSetComposeComponent {
    val hapiValue = org.hl7.fhir.r4.model.ValueSet.ValueSetComposeComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setLockedDateElement(lockedDate.toHapi())
    hapiValue.setInactiveElement(inactive.toHapi())
    hapiValue.setConceptSet(conceptSetList.map{it.toHapi()})
    hapiValue.setExclude(excludeList.map{it.toHapi()})
    return hapiValue
  }

  public fun ValueSet.Compose.ConceptSet.toHapi():
      org.hl7.fhir.r4.model.ValueSet.ConceptSetComponent {
    val hapiValue = org.hl7.fhir.r4.model.ValueSet.ConceptSetComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSystemElement(system.toHapi())
    hapiValue.setVersionElement(version.toHapi())
    hapiValue.setConceptReference(conceptReferenceList.map{it.toHapi()})
    hapiValue.setFilter(filterList.map{it.toHapi()})
    return hapiValue
  }

  public fun ValueSet.Compose.Include.ConceptReference.toHapi():
      org.hl7.fhir.r4.model.ValueSet.ConceptReferenceComponent {
    val hapiValue = org.hl7.fhir.r4.model.ValueSet.ConceptReferenceComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCodeElement(code.toHapi())
    hapiValue.setDisplayElement(display.toHapi())
    hapiValue.setDesignation(designationList.map{it.toHapi()})
    return hapiValue
  }

  public fun ValueSet.Compose.Include.Concept.Designation.toHapi():
      org.hl7.fhir.r4.model.ValueSet.ValueSetComposeIncludeConceptDesignationComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.ValueSet.ValueSetComposeIncludeConceptDesignationComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setUse(use.toHapi())
    hapiValue.setValueElement(value.toHapi())
    return hapiValue
  }

  public fun ValueSet.Compose.Include.Filter.toHapi():
      org.hl7.fhir.r4.model.ValueSet.ValueSetComposeIncludeFilterComponent {
    val hapiValue = org.hl7.fhir.r4.model.ValueSet.ValueSetComposeIncludeFilterComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setPropertyElement(property.toHapi())
    hapiValue.setOp(org.hl7.fhir.r4.model.ValueSet.FilterOperator.valueOf(op.value.name.replace("_","")))
    hapiValue.setValueElement(value.toHapi())
    return hapiValue
  }

  public fun ValueSet.Expansion.toHapi():
      org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionComponent {
    val hapiValue = org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifierElement(identifier.toHapi())
    hapiValue.setTimestampElement(timestamp.toHapi())
    hapiValue.setTotalElement(total.toHapi())
    hapiValue.setOffsetElement(offset.toHapi())
    hapiValue.setParameter(parameterList.map{it.toHapi()})
    hapiValue.setContains(containsList.map{it.toHapi()})
    return hapiValue
  }

  public fun ValueSet.Expansion.Parameter.toHapi():
      org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionParameterComponent {
    val hapiValue = org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionParameterComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setValue(value.valueSetExpansionParameterValueToHapi())
    return hapiValue
  }

  public fun ValueSet.Expansion.Contains.toHapi():
      org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionContainsComponent {
    val hapiValue = org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionContainsComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSystemElement(system.toHapi())
    hapiValue.setAbstractElement(abstract.toHapi())
    hapiValue.setInactiveElement(inactive.toHapi())
    hapiValue.setVersionElement(version.toHapi())
    hapiValue.setCodeElement(code.toHapi())
    hapiValue.setDisplayElement(display.toHapi())
    hapiValue.setDesignation(designationList.map{it.toHapi()})
    hapiValue.setContains(containsList.map{it.toHapi()})
    return hapiValue
  }
}
