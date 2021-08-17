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
import com.google.fhir.r4.core.CodeSystem
import com.google.fhir.r4.core.CodeSystem.ConceptDefinition
import com.google.fhir.r4.core.CodeSystem.ConceptDefinition.ConceptProperty
import com.google.fhir.r4.core.CodeSystem.Filter
import com.google.fhir.r4.core.CodeSystem.Property
import com.google.fhir.r4.core.CodeSystemContentModeCode
import com.google.fhir.r4.core.CodeSystemHierarchyMeaningCode
import com.google.fhir.r4.core.FilterOperatorCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.PropertyTypeCode
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

object CodeSystemConverter {
  private fun CodeSystem.ConceptDefinition.ConceptProperty.ValueX.codeSystemConceptPropertyValueToHapi():
    Type {
    if (hasCode()) {
      return (this.code).toHapi()
    }
    if (hasCoding()) {
      return (this.coding).toHapi()
    }
    if (hasStringValue()) {
      return (this.stringValue).toHapi()
    }
    if (hasInteger()) {
      return (this.integer).toHapi()
    }
    if (hasBoolean()) {
      return (this.boolean).toHapi()
    }
    if (hasDateTime()) {
      return (this.dateTime).toHapi()
    }
    if (hasDecimal()) {
      return (this.decimal).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for CodeSystem.concept.property.value[x]")
  }

  private fun Type.codeSystemConceptPropertyValueToProto():
    CodeSystem.ConceptDefinition.ConceptProperty.ValueX {
    val protoValue = CodeSystem.ConceptDefinition.ConceptProperty.ValueX.newBuilder()
    if (this is CodeType) {
      protoValue.code = this.toProto()
    }
    if (this is Coding) {
      protoValue.coding = this.toProto()
    }
    if (this is StringType) {
      protoValue.stringValue = this.toProto()
    }
    if (this is IntegerType) {
      protoValue.integer = this.toProto()
    }
    if (this is BooleanType) {
      protoValue.boolean = this.toProto()
    }
    if (this is DateTimeType) {
      protoValue.dateTime = this.toProto()
    }
    if (this is DecimalType) {
      protoValue.decimal = this.toProto()
    }
    return protoValue.build()
  }

  fun CodeSystem.toHapi(): org.hl7.fhir.r4.model.CodeSystem {
    val hapiValue = org.hl7.fhir.r4.model.CodeSystem()
    if (hasId()) {
      hapiValue.id = id.value
    }
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
    if (hasUrl()) {
      hapiValue.urlElement = url.toHapi()
    }
    if (identifierCount > 0) {
      hapiValue.identifier = identifierList.map { it.toHapi() }
    }
    if (hasVersion()) {
      hapiValue.versionElement = version.toHapi()
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (hasTitle()) {
      hapiValue.titleElement = title.toHapi()
    }
    if (hasStatus()) {
      hapiValue.status =
        Enumerations.PublicationStatus.valueOf(status.value.name.hapiCodeCheck().replace("_", ""))
    }
    if (hasExperimental()) {
      hapiValue.experimentalElement = experimental.toHapi()
    }
    if (hasDate()) {
      hapiValue.dateElement = date.toHapi()
    }
    if (hasPublisher()) {
      hapiValue.publisherElement = publisher.toHapi()
    }
    if (contactCount > 0) {
      hapiValue.contact = contactList.map { it.toHapi() }
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (useContextCount > 0) {
      hapiValue.useContext = useContextList.map { it.toHapi() }
    }
    if (jurisdictionCount > 0) {
      hapiValue.jurisdiction = jurisdictionList.map { it.toHapi() }
    }
    if (hasPurpose()) {
      hapiValue.purposeElement = purpose.toHapi()
    }
    if (hasCopyright()) {
      hapiValue.copyrightElement = copyright.toHapi()
    }
    if (hasCaseSensitive()) {
      hapiValue.caseSensitiveElement = caseSensitive.toHapi()
    }
    if (hasValueSet()) {
      hapiValue.valueSetElement = valueSet.toHapi()
    }
    if (hasHierarchyMeaning()) {
      hapiValue.hierarchyMeaning =
        org.hl7.fhir.r4.model.CodeSystem.CodeSystemHierarchyMeaning.valueOf(
          hierarchyMeaning.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasCompositional()) {
      hapiValue.compositionalElement = compositional.toHapi()
    }
    if (hasVersionNeeded()) {
      hapiValue.versionNeededElement = versionNeeded.toHapi()
    }
    if (hasContent()) {
      hapiValue.content =
        org.hl7.fhir.r4.model.CodeSystem.CodeSystemContentMode.valueOf(
          content.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasSupplements()) {
      hapiValue.supplementsElement = supplements.toHapi()
    }
    if (hasCount()) {
      hapiValue.countElement = count.toHapi()
    }
    if (filterCount > 0) {
      hapiValue.filter = filterList.map { it.toHapi() }
    }
    if (propertyCount > 0) {
      hapiValue.property = propertyList.map { it.toHapi() }
    }
    if (conceptCount > 0) {
      hapiValue.concept = conceptList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.CodeSystem.toProto(): CodeSystem {
    val protoValue = CodeSystem.newBuilder()
    if (hasId()) {
      protoValue.setId(Id.newBuilder().setValue(id))
    }
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
    if (hasUrl()) {
      protoValue.url = urlElement.toProto()
    }
    if (hasIdentifier()) {
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    if (hasVersion()) {
      protoValue.version = versionElement.toProto()
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasTitle()) {
      protoValue.title = titleElement.toProto()
    }
    if (hasStatus()) {
      protoValue.status =
        CodeSystem.StatusCode.newBuilder()
          .setValue(
            PublicationStatusCode.Value.valueOf(
              status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasExperimental()) {
      protoValue.experimental = experimentalElement.toProto()
    }
    if (hasDate()) {
      protoValue.date = dateElement.toProto()
    }
    if (hasPublisher()) {
      protoValue.publisher = publisherElement.toProto()
    }
    if (hasContact()) {
      protoValue.addAllContact(contact.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasUseContext()) {
      protoValue.addAllUseContext(useContext.map { it.toProto() })
    }
    if (hasJurisdiction()) {
      protoValue.addAllJurisdiction(jurisdiction.map { it.toProto() })
    }
    if (hasPurpose()) {
      protoValue.purpose = purposeElement.toProto()
    }
    if (hasCopyright()) {
      protoValue.copyright = copyrightElement.toProto()
    }
    if (hasCaseSensitive()) {
      protoValue.caseSensitive = caseSensitiveElement.toProto()
    }
    if (hasValueSet()) {
      protoValue.valueSet = valueSetElement.toProto()
    }
    if (hasHierarchyMeaning()) {
      protoValue.hierarchyMeaning =
        CodeSystem.HierarchyMeaningCode.newBuilder()
          .setValue(
            CodeSystemHierarchyMeaningCode.Value.valueOf(
              hierarchyMeaning.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasCompositional()) {
      protoValue.compositional = compositionalElement.toProto()
    }
    if (hasVersionNeeded()) {
      protoValue.versionNeeded = versionNeededElement.toProto()
    }
    if (hasContent()) {
      protoValue.content =
        CodeSystem.ContentCode.newBuilder()
          .setValue(
            CodeSystemContentModeCode.Value.valueOf(
              content.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasSupplements()) {
      protoValue.supplements = supplementsElement.toProto()
    }
    if (hasCount()) {
      protoValue.count = countElement.toProto()
    }
    if (hasFilter()) {
      protoValue.addAllFilter(filter.map { it.toProto() })
    }
    if (hasProperty()) {
      protoValue.addAllProperty(property.map { it.toProto() })
    }
    if (hasConcept()) {
      protoValue.addAllConcept(concept.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.CodeSystem.CodeSystemFilterComponent.toProto():
    CodeSystem.Filter {
    val protoValue = CodeSystem.Filter.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.code = codeElement.toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasOperator()) {
      protoValue.addAllOperator(
        operator.map {
          CodeSystem.Filter.OperatorCode.newBuilder()
            .setValue(
              FilterOperatorCode.Value.valueOf(
                it.value.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
            )
            .build()
        }
      )
    }
    if (hasValue()) {
      protoValue.value = valueElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.CodeSystem.PropertyComponent.toProto(): CodeSystem.Property {
    val protoValue = CodeSystem.Property.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.code = codeElement.toProto()
    }
    if (hasUri()) {
      protoValue.uri = uriElement.toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasType()) {
      protoValue.type =
        CodeSystem.Property.TypeCode.newBuilder()
          .setValue(
            PropertyTypeCode.Value.valueOf(
              type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent.toProto():
    CodeSystem.ConceptDefinition {
    val protoValue = CodeSystem.ConceptDefinition.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.code = codeElement.toProto()
    }
    if (hasDisplay()) {
      protoValue.display = displayElement.toProto()
    }
    if (hasDefinition()) {
      protoValue.definition = definitionElement.toProto()
    }
    if (hasDesignation()) {
      protoValue.addAllDesignation(designation.map { it.toProto() })
    }
    if (hasProperty()) {
      protoValue.addAllProperty(property.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionDesignationComponent.toProto():
    CodeSystem.ConceptDefinition.Designation {
    val protoValue = CodeSystem.ConceptDefinition.Designation.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasUse()) {
      protoValue.use = use.toProto()
    }
    if (hasValue()) {
      protoValue.value = valueElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.CodeSystem.ConceptPropertyComponent.toProto():
    CodeSystem.ConceptDefinition.ConceptProperty {
    val protoValue = CodeSystem.ConceptDefinition.ConceptProperty.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.code = codeElement.toProto()
    }
    if (hasValue()) {
      protoValue.value = value.codeSystemConceptPropertyValueToProto()
    }
    return protoValue.build()
  }

  private fun CodeSystem.Filter.toHapi():
    org.hl7.fhir.r4.model.CodeSystem.CodeSystemFilterComponent {
    val hapiValue = org.hl7.fhir.r4.model.CodeSystem.CodeSystemFilterComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCode()) {
      hapiValue.codeElement = code.toHapi()
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (operatorCount > 0) {
      operatorList.forEach {
        hapiValue.addOperator(
          org.hl7.fhir.r4.model.CodeSystem.FilterOperator.valueOf(
            it.value.name.hapiCodeCheck().replace("_", "")
          )
        )
      }
    }
    if (hasValue()) {
      hapiValue.valueElement = value.toHapi()
    }
    return hapiValue
  }

  private fun CodeSystem.Property.toHapi(): org.hl7.fhir.r4.model.CodeSystem.PropertyComponent {
    val hapiValue = org.hl7.fhir.r4.model.CodeSystem.PropertyComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCode()) {
      hapiValue.codeElement = code.toHapi()
    }
    if (hasUri()) {
      hapiValue.uriElement = uri.toHapi()
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (hasType()) {
      hapiValue.type =
        org.hl7.fhir.r4.model.CodeSystem.PropertyType.valueOf(
          type.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    return hapiValue
  }

  private fun CodeSystem.ConceptDefinition.toHapi():
    org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent {
    val hapiValue = org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCode()) {
      hapiValue.codeElement = code.toHapi()
    }
    if (hasDisplay()) {
      hapiValue.displayElement = display.toHapi()
    }
    if (hasDefinition()) {
      hapiValue.definitionElement = definition.toHapi()
    }
    if (designationCount > 0) {
      hapiValue.designation = designationList.map { it.toHapi() }
    }
    if (propertyCount > 0) {
      hapiValue.property = propertyList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun CodeSystem.ConceptDefinition.Designation.toHapi():
    org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionDesignationComponent {
    val hapiValue = org.hl7.fhir.r4.model.CodeSystem.ConceptDefinitionDesignationComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasUse()) {
      hapiValue.use = use.toHapi()
    }
    if (hasValue()) {
      hapiValue.valueElement = value.toHapi()
    }
    return hapiValue
  }

  private fun CodeSystem.ConceptDefinition.ConceptProperty.toHapi():
    org.hl7.fhir.r4.model.CodeSystem.ConceptPropertyComponent {
    val hapiValue = org.hl7.fhir.r4.model.CodeSystem.ConceptPropertyComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCode()) {
      hapiValue.codeElement = code.toHapi()
    }
    if (hasValue()) {
      hapiValue.value = value.codeSystemConceptPropertyValueToHapi()
    }
    return hapiValue
  }
}
