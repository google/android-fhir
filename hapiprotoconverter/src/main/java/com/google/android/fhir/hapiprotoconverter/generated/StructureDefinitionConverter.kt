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
import org.hl7.fhir.r4.model.Enumerations

public object StructureDefinitionConverter {
  public fun StructureDefinition.toHapi(): org.hl7.fhir.r4.model.StructureDefinition {
    val hapiValue = org.hl7.fhir.r4.model.StructureDefinition()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
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
    hapiValue.setKeyword(keywordList.map { it.toHapi() })
    hapiValue.setFhirVersion(
      Enumerations.FHIRVersion.valueOf(fhirVersion.value.name.replace("_", ""))
    )
    hapiValue.setMapping(mappingList.map { it.toHapi() })
    hapiValue.setKind(
      org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionKind.valueOf(
        kind.value.name.replace("_", "")
      )
    )
    hapiValue.setAbstractElement(abstract.toHapi())
    hapiValue.setContext(contextList.map { it.toHapi() })
    hapiValue.setContextInvariant(contextInvariantList.map { it.toHapi() })
    hapiValue.setTypeElement(type.toHapi())
    hapiValue.setBaseDefinitionElement(baseDefinition.toHapi())
    hapiValue.setDerivation(
      org.hl7.fhir.r4.model.StructureDefinition.TypeDerivationRule.valueOf(
        derivation.value.name.replace("_", "")
      )
    )
    hapiValue.setSnapshot(snapshot.toHapi())
    hapiValue.setDifferential(differential.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.StructureDefinition.toProto(): StructureDefinition {
    val protoValue =
      StructureDefinition.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setUrl(urlElement.toProto())
        .addAllIdentifier(identifier.map { it.toProto() })
        .setVersion(versionElement.toProto())
        .setName(nameElement.toProto())
        .setTitle(titleElement.toProto())
        .setStatus(
          StructureDefinition.StatusCode.newBuilder()
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
        .addAllKeyword(keyword.map { it.toProto() })
        .setFhirVersion(
          StructureDefinition.FhirVersionCode.newBuilder()
            .setValue(
              FHIRVersionCode.Value.valueOf(fhirVersion.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .addAllMapping(mapping.map { it.toProto() })
        .setKind(
          StructureDefinition.KindCode.newBuilder()
            .setValue(
              StructureDefinitionKindCode.Value.valueOf(
                kind.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setAbstract(abstractElement.toProto())
        .addAllContext(context.map { it.toProto() })
        .addAllContextInvariant(contextInvariant.map { it.toProto() })
        .setType(typeElement.toProto())
        .setBaseDefinition(baseDefinitionElement.toProto())
        .setDerivation(
          StructureDefinition.DerivationCode.newBuilder()
            .setValue(
              TypeDerivationRuleCode.Value.valueOf(
                derivation.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setSnapshot(snapshot.toProto())
        .setDifferential(differential.toProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionMappingComponent.toProto():
    StructureDefinition.Mapping {
    val protoValue =
      StructureDefinition.Mapping.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setIdentity(identityElement.toProto())
        .setUri(uriElement.toProto())
        .setName(nameElement.toProto())
        .setComment(commentElement.toProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionContextComponent.toProto():
    StructureDefinition.Context {
    val protoValue =
      StructureDefinition.Context.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setType(
          StructureDefinition.Context.TypeCode.newBuilder()
            .setValue(
              ExtensionContextTypeCode.Value.valueOf(type.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setExpression(expressionElement.toProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionSnapshotComponent.toProto():
    StructureDefinition.Snapshot {
    val protoValue =
      StructureDefinition.Snapshot.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllElement(element.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionDifferentialComponent.toProto():
    StructureDefinition.Differential {
    val protoValue =
      StructureDefinition.Differential.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllElement(element.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun StructureDefinition.Mapping.toHapi():
    org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionMappingComponent {
    val hapiValue = org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionMappingComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentityElement(identity.toHapi())
    hapiValue.setUriElement(uri.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setCommentElement(comment.toHapi())
    return hapiValue
  }

  private fun StructureDefinition.Context.toHapi():
    org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionContextComponent {
    val hapiValue = org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionContextComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setType(
      org.hl7.fhir.r4.model.StructureDefinition.ExtensionContextType.valueOf(
        type.value.name.replace("_", "")
      )
    )
    hapiValue.setExpressionElement(expression.toHapi())
    return hapiValue
  }

  private fun StructureDefinition.Snapshot.toHapi():
    org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionSnapshotComponent {
    val hapiValue = org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionSnapshotComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setElement(elementList.map { it.toHapi() })
    return hapiValue
  }

  private fun StructureDefinition.Differential.toHapi():
    org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionDifferentialComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.StructureDefinition.StructureDefinitionDifferentialComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setElement(elementList.map { it.toHapi() })
    return hapiValue
  }
}
