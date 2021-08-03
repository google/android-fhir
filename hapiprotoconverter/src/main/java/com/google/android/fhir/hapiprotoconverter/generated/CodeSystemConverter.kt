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
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toProto
import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.Code
import com.google.fhir.r4.core.CodeSystem
import com.google.fhir.r4.core.CodeSystem.ConceptDefinition
import com.google.fhir.r4.core.CodeSystem.ConceptDefinition.ConceptProperty
import com.google.fhir.r4.core.CodeSystem.Filter
import com.google.fhir.r4.core.CodeSystem.Property
import com.google.fhir.r4.core.CodeSystemContentModeCode
import com.google.fhir.r4.core.CodeSystemHierarchyMeaningCode
import com.google.fhir.r4.core.Coding
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Decimal
import com.google.fhir.r4.core.FilterOperatorCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Integer
import com.google.fhir.r4.core.PropertyTypeCode
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

public object CodeSystemConverter {
  @JvmStatic
  private fun CodeSystem.ConceptDefinition.ConceptProperty.ValueX.codeSystemConceptPropertyValueToHapi():
    Type {
    if (this.getCode() != Code.newBuilder().defaultInstanceForType) {
      return (this.getCode()).toHapi()
    }
    if (this.getCoding() != Coding.newBuilder().defaultInstanceForType) {
      return (this.getCoding()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    if (this.getInteger() != Integer.newBuilder().defaultInstanceForType) {
      return (this.getInteger()).toHapi()
    }
    if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType) {
      return (this.getBoolean()).toHapi()
    }
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getDecimal() != Decimal.newBuilder().defaultInstanceForType) {
      return (this.getDecimal()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for CodeSystem.concept.property.value[x]")
  }

  @JvmStatic
  private fun Type.codeSystemConceptPropertyValueToProto():
    CodeSystem.ConceptDefinition.ConceptProperty.ValueX {
    val protoValue = CodeSystem.ConceptDefinition.ConceptProperty.ValueX.newBuilder()
    if (this is CodeType) {
      protoValue.setCode(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Coding) {
      protoValue.setCoding(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    if (this is IntegerType) {
      protoValue.setInteger(this.toProto())
    }
    if (this is BooleanType) {
      protoValue.setBoolean(this.toProto())
    }
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is DecimalType) {
      protoValue.setDecimal(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun CodeSystem.toHapi(): org.hl7.fhir.r4.model.CodeSystem {
    val hapiValue = org.hl7.fhir.r4.model.CodeSystem()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setUrlElement(url.toHapi())
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setVersionElement(version.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setTitleElement(title.toHapi())
    hapiValue.setStatus(Enumerations.PublicationStatus.valueOf(status.value.name.replace("_", "")))
    hapiValue.setExperimentalElement(experimental.toHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setPublisherElement(publisher.toHapi())
    hapiValue.setContact(contactList.map { it.toHapi() })
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setUseContext(useContextList.map { it.toHapi() })
    hapiValue.setJurisdiction(jurisdictionList.map { it.toHapi() })
    hapiValue.setPurposeElement(purpose.toHapi())
    hapiValue.setCopyrightElement(copyright.toHapi())
    hapiValue.setCaseSensitiveElement(caseSensitive.toHapi())
    hapiValue.setValueSetElement(valueSet.toHapi())
    hapiValue.setHierarchyMeaning(
      org.hl7.fhir.r4.model.CodeSystem.CodeSystemHierarchyMeaning.valueOf(
        hierarchyMeaning.value.name.replace("_", "")
      )
    )
    hapiValue.setCompositionalElement(compositional.toHapi())
    hapiValue.setVersionNeededElement(versionNeeded.toHapi())
    hapiValue.setContent(
      org.hl7.fhir.r4.model.CodeSystem.CodeSystemContentMode.valueOf(
        content.value.name.replace("_", "")
      )
    )
    hapiValue.setSupplementsElement(supplements.toHapi())
    hapiValue.setCountElement(count.toHapi())
    hapiValue.setFilter(filterList.map { it.toHapi() })
    hapiValue.setProperty(propertyList.map { it.toHapi() })
    hapiValue.setConcept(conceptList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.CodeSystem.toProto(): CodeSystem {
    val protoValue =
      CodeSystem.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setLanguage(languageElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setUrl(urlElement.toProto())
        .addAllIdentifier(identifier.map { it.toProto() })
        .setVersion(versionElement.toProto())
        .setName(nameElement.toProto())
        .setTitle(titleElement.toProto())
        .setStatus(
          CodeSystem.StatusCode.newBuilder()
            .setValue(
              PublicationStatusCode.Value.valueOf(status.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setExperimental(experimentalElement.toProto())
        .setDate(dateElement.toProto())
        .setPublisher(publisherElement.toProto())
        .addAllContact(contact.map { it.toProto() })
        .setDescription(descriptionElement.toProto())
        .addAllUseContext(useContext.map { it.toProto() })
        .addAllJurisdiction(jurisdiction.map { it.toProto() })
        .setPurpose(purposeElement.toProto())
        .setCopyright(copyrightElement.toProto())
        .setCaseSensitive(caseSensitiveElement.toProto())
        .setValueSet(valueSetElement.toProto())
        .setHierarchyMeaning(
          CodeSystem.HierarchyMeaningCode.newBuilder()
            .setValue(
              CodeSystemHierarchyMeaningCode.Value.valueOf(
                hierarchyMeaning.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setCompositional(compositionalElement.toProto())
        .setVersionNeeded(versionNeededElement.toProto())
        .setContent(
          CodeSystem.ContentCode.newBuilder()
            .setValue(
              CodeSystemContentModeCode.Value.valueOf(
                content.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setSupplements(supplementsElement.toProto())
        .setCount(countElement.toProto())
        .addAllFilter(filter.map { it.toProto() })
        .addAllProperty(property.map { it.toProto() })
        .addAllConcept(concept.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.CodeSystem.CodeSystemFilterComponent.toProto():
    CodeSystem.Filter {
    val protoValue =
      CodeSystem.Filter.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCode(codeElement.toProto())
        .setDescription(descriptionElement.toProto())
        .addAllOperator(
          operator.map {
            CodeSystem.Filter.OperatorCode.newBuilder()
              .setValue(
                FilterOperatorCode.Value.valueOf(it.value.toCode().replace("-", "_").toUpperCase())
              )
              .build()
          }
        )
        .setValue(valueElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.CodeSystem.PropertyComponent.toProto(): CodeSystem.Property {
    val protoValue =
      CodeSystem.Property.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCode(codeElement.toProto())
        .setUri(uriElement.toProto())
        .setDescription(descriptionElement.toProto())
        .setType(
          CodeSystem.Property.TypeCode.newBuilder()
            .setValue(PropertyTypeCode.Value.valueOf(type.toCode().replace("-", "_").toUpperCase()))
            .build()
        )
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent.toProto():
    CodeSystem.ConceptDefinition {
    val protoValue =
      CodeSystem.ConceptDefinition.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCode(codeElement.toProto())
        .setDisplay(displayElement.toProto())
        .setDefinition(definitionElement.toProto())
        .addAllDesignation(designation.map { it.toProto() })
        .addAllProperty(property.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionDesignationComponent.toProto():
    CodeSystem.ConceptDefinition.Designation {
    val protoValue =
      CodeSystem.ConceptDefinition.Designation.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setLanguage(languageElement.toProto())
        .setUse(use.toProto())
        .setValue(valueElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.CodeSystem.ConceptPropertyComponent.toProto():
    CodeSystem.ConceptDefinition.ConceptProperty {
    val protoValue =
      CodeSystem.ConceptDefinition.ConceptProperty.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCode(codeElement.toProto())
        .setValue(value.codeSystemConceptPropertyValueToProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun CodeSystem.Filter.toHapi():
    org.hl7.fhir.r4.model.CodeSystem.CodeSystemFilterComponent {
    val hapiValue = org.hl7.fhir.r4.model.CodeSystem.CodeSystemFilterComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCodeElement(code.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    operatorList.forEach {
      hapiValue.addOperator(
        org.hl7.fhir.r4.model.CodeSystem.FilterOperator.valueOf(it.value.name.replace("_", ""))
      )
    }
    hapiValue.setValueElement(value.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun CodeSystem.Property.toHapi(): org.hl7.fhir.r4.model.CodeSystem.PropertyComponent {
    val hapiValue = org.hl7.fhir.r4.model.CodeSystem.PropertyComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCodeElement(code.toHapi())
    hapiValue.setUriElement(uri.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setType(
      org.hl7.fhir.r4.model.CodeSystem.PropertyType.valueOf(type.value.name.replace("_", ""))
    )
    return hapiValue
  }

  @JvmStatic
  private fun CodeSystem.ConceptDefinition.toHapi():
    org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent {
    val hapiValue = org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCodeElement(code.toHapi())
    hapiValue.setDisplayElement(display.toHapi())
    hapiValue.setDefinitionElement(definition.toHapi())
    hapiValue.setDesignation(designationList.map { it.toHapi() })
    hapiValue.setProperty(propertyList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun CodeSystem.ConceptDefinition.Designation.toHapi():
    org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionDesignationComponent {
    val hapiValue = org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionDesignationComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setUse(use.toHapi())
    hapiValue.setValueElement(value.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun CodeSystem.ConceptDefinition.ConceptProperty.toHapi():
    org.hl7.fhir.r4.model.CodeSystem.ConceptPropertyComponent {
    val hapiValue = org.hl7.fhir.r4.model.CodeSystem.ConceptPropertyComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCodeElement(code.toHapi())
    hapiValue.setValue(value.codeSystemConceptPropertyValueToHapi())
    return hapiValue
  }
}
