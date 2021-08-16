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
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ElementDefinitionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ElementDefinitionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
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
import com.google.fhir.r4.core.ExtensionContextTypeCode
import com.google.fhir.r4.core.FHIRVersionCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.StructureDefinition
import com.google.fhir.r4.core.StructureDefinition.Context
import com.google.fhir.r4.core.StructureDefinitionKindCode
import com.google.fhir.r4.core.TypeDerivationRuleCode
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Enumerations

object StructureDefinitionConverter {
  @JvmStatic
  fun StructureDefinition.toHapi(): org.hl7.fhir.r4.model.StructureDefinition {
    val hapiValue = org.hl7.fhir.r4.model.StructureDefinition()
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
    if (hasPurpose()) {
      hapiValue.purposeElement = purpose.toHapi()
    }
    if (hasCopyright()) {
      hapiValue.copyrightElement = copyright.toHapi()
    }
    if (keywordCount > 0) {
      hapiValue.keyword = keywordList.map { it.toHapi() }
    }
    hapiValue.fhirVersion =
      Enumerations.FHIRVersion.valueOf(fhirVersion.value.name.hapiCodeCheck().replace("_", ""))
    if (mappingCount > 0) {
      hapiValue.mapping = mappingList.map { it.toHapi() }
    }
    hapiValue.kind =
      org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionKind.valueOf(
        kind.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasAbstract()) {
      hapiValue.abstractElement = abstract.toHapi()
    }
    if (contextCount > 0) {
      hapiValue.context = contextList.map { it.toHapi() }
    }
    if (contextInvariantCount > 0) {
      hapiValue.contextInvariant = contextInvariantList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.typeElement = type.toHapi()
    }
    if (hasBaseDefinition()) {
      hapiValue.baseDefinitionElement = baseDefinition.toHapi()
    }
    hapiValue.derivation =
      org.hl7.fhir.r4.model.StructureDefinition.TypeDerivationRule.valueOf(
        derivation.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasSnapshot()) {
      hapiValue.snapshot = snapshot.toHapi()
    }
    if (hasDifferential()) {
      hapiValue.differential = differential.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.StructureDefinition.toProto(): StructureDefinition {
    val protoValue = StructureDefinition.newBuilder().setId(Id.newBuilder().setValue(id))
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
      StructureDefinition.StatusCode.newBuilder()
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
    if (hasPurpose()) {
      protoValue.purpose = purposeElement.toProto()
    }
    if (hasCopyright()) {
      protoValue.copyright = copyrightElement.toProto()
    }
    if (hasKeyword()) {
      protoValue.addAllKeyword(keyword.map { it.toProto() })
    }
    protoValue.fhirVersion =
      StructureDefinition.FhirVersionCode.newBuilder()
        .setValue(
          FHIRVersionCode.Value.valueOf(
            fhirVersion.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasMapping()) {
      protoValue.addAllMapping(mapping.map { it.toProto() })
    }
    protoValue.kind =
      StructureDefinition.KindCode.newBuilder()
        .setValue(
          StructureDefinitionKindCode.Value.valueOf(
            kind.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasAbstract()) {
      protoValue.abstract = abstractElement.toProto()
    }
    if (hasContext()) {
      protoValue.addAllContext(context.map { it.toProto() })
    }
    if (hasContextInvariant()) {
      protoValue.addAllContextInvariant(contextInvariant.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = typeElement.toProto()
    }
    if (hasBaseDefinition()) {
      protoValue.baseDefinition = baseDefinitionElement.toProto()
    }
    protoValue.derivation =
      StructureDefinition.DerivationCode.newBuilder()
        .setValue(
          TypeDerivationRuleCode.Value.valueOf(
            derivation.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasSnapshot()) {
      protoValue.snapshot = snapshot.toProto()
    }
    if (hasDifferential()) {
      protoValue.differential = differential.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionMappingComponent.toProto():
    StructureDefinition.Mapping {
    val protoValue =
      StructureDefinition.Mapping.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasIdentity()) {
      protoValue.identity = identityElement.toProto()
    }
    if (hasUri()) {
      protoValue.uri = uriElement.toProto()
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasComment()) {
      protoValue.comment = commentElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionContextComponent.toProto():
    StructureDefinition.Context {
    val protoValue =
      StructureDefinition.Context.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.type =
      StructureDefinition.Context.TypeCode.newBuilder()
        .setValue(
          ExtensionContextTypeCode.Value.valueOf(
            type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasExpression()) {
      protoValue.expression = expressionElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionSnapshotComponent.toProto():
    StructureDefinition.Snapshot {
    val protoValue =
      StructureDefinition.Snapshot.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasElement()) {
      protoValue.addAllElement(element.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionDifferentialComponent.toProto():
    StructureDefinition.Differential {
    val protoValue =
      StructureDefinition.Differential.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasElement()) {
      protoValue.addAllElement(element.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun StructureDefinition.Mapping.toHapi():
    org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionMappingComponent {
    val hapiValue = org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionMappingComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasIdentity()) {
      hapiValue.identityElement = identity.toHapi()
    }
    if (hasUri()) {
      hapiValue.uriElement = uri.toHapi()
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (hasComment()) {
      hapiValue.commentElement = comment.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun StructureDefinition.Context.toHapi():
    org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionContextComponent {
    val hapiValue = org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionContextComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    hapiValue.type =
      org.hl7.fhir.r4.model.StructureDefinition.ExtensionContextType.valueOf(
        type.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasExpression()) {
      hapiValue.expressionElement = expression.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun StructureDefinition.Snapshot.toHapi():
    org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionSnapshotComponent {
    val hapiValue = org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionSnapshotComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (elementCount > 0) {
      hapiValue.element = elementList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun StructureDefinition.Differential.toHapi():
    org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionDifferentialComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionDifferentialComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (elementCount > 0) {
      hapiValue.element = elementList.map { it.toHapi() }
    }
    return hapiValue
  }
}
