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

public object StructureDefinitionConverter {
  @JvmStatic
  public fun StructureDefinition.toHapi(): org.hl7.fhir.r4.model.StructureDefinition {
    val hapiValue = org.hl7.fhir.r4.model.StructureDefinition()
    hapiValue.id = id.value
    if (hasMeta()) {
      hapiValue.setMeta(meta.toHapi())
    }
    if (hasImplicitRules()) {
      hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    }
    if (hasText()) {
      hapiValue.setText(text.toHapi())
    }
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasUrl()) {
      hapiValue.setUrlElement(url.toHapi())
    }
    if (identifierCount > 0) {
      hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    }
    if (hasVersion()) {
      hapiValue.setVersionElement(version.toHapi())
    }
    if (hasName()) {
      hapiValue.setNameElement(name.toHapi())
    }
    if (hasTitle()) {
      hapiValue.setTitleElement(title.toHapi())
    }
    hapiValue.setStatus(
      Enumerations.PublicationStatus.valueOf(status.value.name.hapiCodeCheck().replace("_", ""))
    )
    if (hasExperimental()) {
      hapiValue.setExperimentalElement(experimental.toHapi())
    }
    if (hasDate()) {
      hapiValue.setDateElement(date.toHapi())
    }
    if (hasPublisher()) {
      hapiValue.setPublisherElement(publisher.toHapi())
    }
    if (contactCount > 0) {
      hapiValue.setContact(contactList.map { it.toHapi() })
    }
    if (hasDescription()) {
      hapiValue.setDescriptionElement(description.toHapi())
    }
    if (useContextCount > 0) {
      hapiValue.setUseContext(useContextList.map { it.toHapi() })
    }
    if (jurisdictionCount > 0) {
      hapiValue.setJurisdiction(jurisdictionList.map { it.toHapi() })
    }
    if (hasPurpose()) {
      hapiValue.setPurposeElement(purpose.toHapi())
    }
    if (hasCopyright()) {
      hapiValue.setCopyrightElement(copyright.toHapi())
    }
    if (keywordCount > 0) {
      hapiValue.setKeyword(keywordList.map { it.toHapi() })
    }
    hapiValue.setFhirVersion(
      Enumerations.FHIRVersion.valueOf(fhirVersion.value.name.hapiCodeCheck().replace("_", ""))
    )
    if (mappingCount > 0) {
      hapiValue.setMapping(mappingList.map { it.toHapi() })
    }
    hapiValue.setKind(
      org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionKind.valueOf(
        kind.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasAbstract()) {
      hapiValue.setAbstractElement(abstract.toHapi())
    }
    if (contextCount > 0) {
      hapiValue.setContext(contextList.map { it.toHapi() })
    }
    if (contextInvariantCount > 0) {
      hapiValue.setContextInvariant(contextInvariantList.map { it.toHapi() })
    }
    if (hasType()) {
      hapiValue.setTypeElement(type.toHapi())
    }
    if (hasBaseDefinition()) {
      hapiValue.setBaseDefinitionElement(baseDefinition.toHapi())
    }
    hapiValue.setDerivation(
      org.hl7.fhir.r4.model.StructureDefinition.TypeDerivationRule.valueOf(
        derivation.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasSnapshot()) {
      hapiValue.setSnapshot(snapshot.toHapi())
    }
    if (hasDifferential()) {
      hapiValue.setDifferential(differential.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.StructureDefinition.toProto(): StructureDefinition {
    val protoValue = StructureDefinition.newBuilder().setId(Id.newBuilder().setValue(id))
    if (hasMeta()) {
      protoValue.setMeta(meta.toProto())
    }
    if (hasImplicitRules()) {
      protoValue.setImplicitRules(implicitRulesElement.toProto())
    }
    if (hasText()) {
      protoValue.setText(text.toProto())
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasUrl()) {
      protoValue.setUrl(urlElement.toProto())
    }
    if (hasIdentifier()) {
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    if (hasVersion()) {
      protoValue.setVersion(versionElement.toProto())
    }
    if (hasName()) {
      protoValue.setName(nameElement.toProto())
    }
    if (hasTitle()) {
      protoValue.setTitle(titleElement.toProto())
    }
    protoValue.setStatus(
      StructureDefinition.StatusCode.newBuilder()
        .setValue(
          PublicationStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasExperimental()) {
      protoValue.setExperimental(experimentalElement.toProto())
    }
    if (hasDate()) {
      protoValue.setDate(dateElement.toProto())
    }
    if (hasPublisher()) {
      protoValue.setPublisher(publisherElement.toProto())
    }
    if (hasContact()) {
      protoValue.addAllContact(contact.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.setDescription(descriptionElement.toProto())
    }
    if (hasUseContext()) {
      protoValue.addAllUseContext(useContext.map { it.toProto() })
    }
    if (hasJurisdiction()) {
      protoValue.addAllJurisdiction(jurisdiction.map { it.toProto() })
    }
    if (hasPurpose()) {
      protoValue.setPurpose(purposeElement.toProto())
    }
    if (hasCopyright()) {
      protoValue.setCopyright(copyrightElement.toProto())
    }
    if (hasKeyword()) {
      protoValue.addAllKeyword(keyword.map { it.toProto() })
    }
    protoValue.setFhirVersion(
      StructureDefinition.FhirVersionCode.newBuilder()
        .setValue(
          FHIRVersionCode.Value.valueOf(
            fhirVersion.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasMapping()) {
      protoValue.addAllMapping(mapping.map { it.toProto() })
    }
    protoValue.setKind(
      StructureDefinition.KindCode.newBuilder()
        .setValue(
          StructureDefinitionKindCode.Value.valueOf(
            kind.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasAbstract()) {
      protoValue.setAbstract(abstractElement.toProto())
    }
    if (hasContext()) {
      protoValue.addAllContext(context.map { it.toProto() })
    }
    if (hasContextInvariant()) {
      protoValue.addAllContextInvariant(contextInvariant.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.setType(typeElement.toProto())
    }
    if (hasBaseDefinition()) {
      protoValue.setBaseDefinition(baseDefinitionElement.toProto())
    }
    protoValue.setDerivation(
      StructureDefinition.DerivationCode.newBuilder()
        .setValue(
          TypeDerivationRuleCode.Value.valueOf(
            derivation.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasSnapshot()) {
      protoValue.setSnapshot(snapshot.toProto())
    }
    if (hasDifferential()) {
      protoValue.setDifferential(differential.toProto())
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
      protoValue.setIdentity(identityElement.toProto())
    }
    if (hasUri()) {
      protoValue.setUri(uriElement.toProto())
    }
    if (hasName()) {
      protoValue.setName(nameElement.toProto())
    }
    if (hasComment()) {
      protoValue.setComment(commentElement.toProto())
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
    protoValue.setType(
      StructureDefinition.Context.TypeCode.newBuilder()
        .setValue(
          ExtensionContextTypeCode.Value.valueOf(
            type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasExpression()) {
      protoValue.setExpression(expressionElement.toProto())
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
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasIdentity()) {
      hapiValue.setIdentityElement(identity.toHapi())
    }
    if (hasUri()) {
      hapiValue.setUriElement(uri.toHapi())
    }
    if (hasName()) {
      hapiValue.setNameElement(name.toHapi())
    }
    if (hasComment()) {
      hapiValue.setCommentElement(comment.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun StructureDefinition.Context.toHapi():
    org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionContextComponent {
    val hapiValue = org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionContextComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    hapiValue.setType(
      org.hl7.fhir.r4.model.StructureDefinition.ExtensionContextType.valueOf(
        type.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasExpression()) {
      hapiValue.setExpressionElement(expression.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun StructureDefinition.Snapshot.toHapi():
    org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionSnapshotComponent {
    val hapiValue = org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionSnapshotComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (elementCount > 0) {
      hapiValue.setElement(elementList.map { it.toHapi() })
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
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (elementCount > 0) {
      hapiValue.setElement(elementList.map { it.toHapi() })
    }
    return hapiValue
  }
}
