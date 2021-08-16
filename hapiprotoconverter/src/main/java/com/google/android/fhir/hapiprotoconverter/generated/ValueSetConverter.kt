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
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.Uri
import com.google.fhir.r4.core.ValueSet
import com.google.fhir.r4.core.ValueSet.Compose
import com.google.fhir.r4.core.ValueSet.Compose.ConceptSet
import com.google.fhir.r4.core.ValueSet.Compose.ConceptSet.ConceptReference
import com.google.fhir.r4.core.ValueSet.Compose.ConceptSet.Filter
import com.google.fhir.r4.core.ValueSet.Expansion
import com.google.fhir.r4.core.ValueSet.Expansion.Parameter
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
import org.hl7.fhir.r4.model.UriType

object ValueSetConverter {
  @JvmStatic
  private fun ValueSet.Expansion.Parameter.ValueX.valueSetExpansionParameterValueToHapi(): Type {
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    if (this.boolean != Boolean.newBuilder().defaultInstanceForType) {
      return (this.boolean).toHapi()
    }
    if (this.integer != Integer.newBuilder().defaultInstanceForType) {
      return (this.integer).toHapi()
    }
    if (this.decimal != Decimal.newBuilder().defaultInstanceForType) {
      return (this.decimal).toHapi()
    }
    if (this.uri != Uri.newBuilder().defaultInstanceForType) {
      return (this.uri).toHapi()
    }
    if (this.code != Code.newBuilder().defaultInstanceForType) {
      return (this.code).toHapi()
    }
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ValueSet.expansion.parameter.value[x]")
  }

  @JvmStatic
  private fun Type.valueSetExpansionParameterValueToProto(): ValueSet.Expansion.Parameter.ValueX {
    val protoValue = ValueSet.Expansion.Parameter.ValueX.newBuilder()
    if (this is StringType) {
      protoValue.stringValue = this.toProto()
    }
    if (this is BooleanType) {
      protoValue.boolean = this.toProto()
    }
    if (this is IntegerType) {
      protoValue.integer = this.toProto()
    }
    if (this is DecimalType) {
      protoValue.decimal = this.toProto()
    }
    if (this is UriType) {
      protoValue.uri = this.toProto()
    }
    if (this is CodeType) {
      protoValue.code = this.toProto()
    }
    if (this is DateTimeType) {
      protoValue.dateTime = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun ValueSet.toHapi(): org.hl7.fhir.r4.model.ValueSet {
    val hapiValue = org.hl7.fhir.r4.model.ValueSet()
    hapiValue.id = id.value
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
    hapiValue.status =
      Enumerations.PublicationStatus.valueOf(status.value.name.hapiCodeCheck().replace("_", ""))
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
    if (hasImmutable()) {
      hapiValue.immutableElement = immutable.toHapi()
    }
    if (hasPurpose()) {
      hapiValue.purposeElement = purpose.toHapi()
    }
    if (hasCopyright()) {
      hapiValue.copyrightElement = copyright.toHapi()
    }
    if (hasCompose()) {
      hapiValue.compose = compose.toHapi()
    }
    if (hasExpansion()) {
      hapiValue.expansion = expansion.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.ValueSet.toProto(): ValueSet {
    val protoValue = ValueSet.newBuilder().setId(Id.newBuilder().setValue(id))
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
    protoValue.status =
      ValueSet.StatusCode.newBuilder()
        .setValue(
          PublicationStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
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
    if (hasImmutable()) {
      protoValue.immutable = immutableElement.toProto()
    }
    if (hasPurpose()) {
      protoValue.purpose = purposeElement.toProto()
    }
    if (hasCopyright()) {
      protoValue.copyright = copyrightElement.toProto()
    }
    if (hasCompose()) {
      protoValue.compose = compose.toProto()
    }
    if (hasExpansion()) {
      protoValue.expansion = expansion.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ValueSet.ValueSetComposeComponent.toProto(): ValueSet.Compose {
    val protoValue = ValueSet.Compose.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasLockedDate()) {
      protoValue.lockedDate = lockedDateElement.toProto()
    }
    if (hasInactive()) {
      protoValue.inactive = inactiveElement.toProto()
    }
    if (hasInclude()) {
      protoValue.addAllInclude(include.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ValueSet.ConceptSetComponent.toProto():
    ValueSet.Compose.ConceptSet {
    val protoValue =
      ValueSet.Compose.ConceptSet.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSystem()) {
      protoValue.system = systemElement.toProto()
    }
    if (hasVersion()) {
      protoValue.version = versionElement.toProto()
    }
    if (hasConcept()) {
      protoValue.addAllConcept(concept.map { it.toProto() })
    }
    if (hasFilter()) {
      protoValue.addAllFilter(filter.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ValueSet.ConceptReferenceComponent.toProto():
    ValueSet.Compose.ConceptSet.ConceptReference {
    val protoValue =
      ValueSet.Compose.ConceptSet.ConceptReference.newBuilder()
        .setId(String.newBuilder().setValue(id))
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
    if (hasDesignation()) {
      protoValue.addAllDesignation(designation.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ValueSet.ConceptReferenceDesignationComponent.toProto():
    ValueSet.Compose.ConceptSet.ConceptReference.Designation {
    val protoValue =
      ValueSet.Compose.ConceptSet.ConceptReference.Designation.newBuilder()
        .setId(String.newBuilder().setValue(id))
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

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ValueSet.ConceptSetFilterComponent.toProto():
    ValueSet.Compose.ConceptSet.Filter {
    val protoValue =
      ValueSet.Compose.ConceptSet.Filter.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasProperty()) {
      protoValue.property = propertyElement.toProto()
    }
    protoValue.op =
      ValueSet.Compose.ConceptSet.Filter.OpCode.newBuilder()
        .setValue(
          FilterOperatorCode.Value.valueOf(
            op.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasValue()) {
      protoValue.value = valueElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionComponent.toProto():
    ValueSet.Expansion {
    val protoValue = ValueSet.Expansion.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasIdentifier()) {
      protoValue.identifier = identifierElement.toProto()
    }
    if (hasTimestamp()) {
      protoValue.timestamp = timestampElement.toProto()
    }
    if (hasTotal()) {
      protoValue.total = totalElement.toProto()
    }
    if (hasOffset()) {
      protoValue.offset = offsetElement.toProto()
    }
    if (hasParameter()) {
      protoValue.addAllParameter(parameter.map { it.toProto() })
    }
    if (hasContains()) {
      protoValue.addAllContains(contains.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionParameterComponent.toProto():
    ValueSet.Expansion.Parameter {
    val protoValue =
      ValueSet.Expansion.Parameter.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasValue()) {
      protoValue.value = value.valueSetExpansionParameterValueToProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionContainsComponent.toProto():
    ValueSet.Expansion.Contains {
    val protoValue =
      ValueSet.Expansion.Contains.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSystem()) {
      protoValue.system = systemElement.toProto()
    }
    if (hasAbstract()) {
      protoValue.abstract = abstractElement.toProto()
    }
    if (hasInactive()) {
      protoValue.inactive = inactiveElement.toProto()
    }
    if (hasVersion()) {
      protoValue.version = versionElement.toProto()
    }
    if (hasCode()) {
      protoValue.code = codeElement.toProto()
    }
    if (hasDisplay()) {
      protoValue.display = displayElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ValueSet.Compose.toHapi(): org.hl7.fhir.r4.model.ValueSet.ValueSetComposeComponent {
    val hapiValue = org.hl7.fhir.r4.model.ValueSet.ValueSetComposeComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasLockedDate()) {
      hapiValue.lockedDateElement = lockedDate.toHapi()
    }
    if (hasInactive()) {
      hapiValue.inactiveElement = inactive.toHapi()
    }
    if (includeCount > 0) {
      hapiValue.include = includeList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun ValueSet.Compose.ConceptSet.toHapi():
    org.hl7.fhir.r4.model.ValueSet.ConceptSetComponent {
    val hapiValue = org.hl7.fhir.r4.model.ValueSet.ConceptSetComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasSystem()) {
      hapiValue.systemElement = system.toHapi()
    }
    if (hasVersion()) {
      hapiValue.versionElement = version.toHapi()
    }
    if (conceptCount > 0) {
      hapiValue.concept = conceptList.map { it.toHapi() }
    }
    if (filterCount > 0) {
      hapiValue.filter = filterList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun ValueSet.Compose.ConceptSet.ConceptReference.toHapi():
    org.hl7.fhir.r4.model.ValueSet.ConceptReferenceComponent {
    val hapiValue = org.hl7.fhir.r4.model.ValueSet.ConceptReferenceComponent()
    hapiValue.id = id.value
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
    if (designationCount > 0) {
      hapiValue.designation = designationList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun ValueSet.Compose.ConceptSet.ConceptReference.Designation.toHapi():
    org.hl7.fhir.r4.model.ValueSet.ConceptReferenceDesignationComponent {
    val hapiValue = org.hl7.fhir.r4.model.ValueSet.ConceptReferenceDesignationComponent()
    hapiValue.id = id.value
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

  @JvmStatic
  private fun ValueSet.Compose.ConceptSet.Filter.toHapi():
    org.hl7.fhir.r4.model.ValueSet.ConceptSetFilterComponent {
    val hapiValue = org.hl7.fhir.r4.model.ValueSet.ConceptSetFilterComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasProperty()) {
      hapiValue.propertyElement = property.toHapi()
    }
    hapiValue.op =
      org.hl7.fhir.r4.model.ValueSet.FilterOperator.valueOf(
        op.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasValue()) {
      hapiValue.valueElement = value.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun ValueSet.Expansion.toHapi():
    org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionComponent {
    val hapiValue = org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasIdentifier()) {
      hapiValue.identifierElement = identifier.toHapi()
    }
    if (hasTimestamp()) {
      hapiValue.timestampElement = timestamp.toHapi()
    }
    if (hasTotal()) {
      hapiValue.totalElement = total.toHapi()
    }
    if (hasOffset()) {
      hapiValue.offsetElement = offset.toHapi()
    }
    if (parameterCount > 0) {
      hapiValue.parameter = parameterList.map { it.toHapi() }
    }
    if (containsCount > 0) {
      hapiValue.contains = containsList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun ValueSet.Expansion.Parameter.toHapi():
    org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionParameterComponent {
    val hapiValue = org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionParameterComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (hasValue()) {
      hapiValue.value = value.valueSetExpansionParameterValueToHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun ValueSet.Expansion.Contains.toHapi():
    org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionContainsComponent {
    val hapiValue = org.hl7.fhir.r4.model.ValueSet.ValueSetExpansionContainsComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasSystem()) {
      hapiValue.systemElement = system.toHapi()
    }
    if (hasAbstract()) {
      hapiValue.abstractElement = abstract.toHapi()
    }
    if (hasInactive()) {
      hapiValue.inactiveElement = inactive.toHapi()
    }
    if (hasVersion()) {
      hapiValue.versionElement = version.toHapi()
    }
    if (hasCode()) {
      hapiValue.codeElement = code.toHapi()
    }
    if (hasDisplay()) {
      hapiValue.displayElement = display.toHapi()
    }
    return hapiValue
  }
}
