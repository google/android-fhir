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
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UrlConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UrlConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toProto
import com.google.fhir.r4.core.CapabilityStatement
import com.google.fhir.r4.core.CapabilityStatement.Document
import com.google.fhir.r4.core.CapabilityStatement.Messaging
import com.google.fhir.r4.core.CapabilityStatement.Messaging.SupportedMessage
import com.google.fhir.r4.core.CapabilityStatement.Rest
import com.google.fhir.r4.core.CapabilityStatement.Rest.Resource
import com.google.fhir.r4.core.CapabilityStatement.Rest.Resource.ResourceInteraction
import com.google.fhir.r4.core.CapabilityStatement.Rest.Resource.SearchParam
import com.google.fhir.r4.core.CapabilityStatement.Rest.SystemInteraction
import com.google.fhir.r4.core.CapabilityStatementKindCode
import com.google.fhir.r4.core.ConditionalDeleteStatusCode
import com.google.fhir.r4.core.ConditionalReadStatusCode
import com.google.fhir.r4.core.DocumentModeCode
import com.google.fhir.r4.core.EventCapabilityModeCode
import com.google.fhir.r4.core.FHIRVersionCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.ReferenceHandlingPolicyCode
import com.google.fhir.r4.core.ResourceTypeCode
import com.google.fhir.r4.core.ResourceVersionPolicyCode
import com.google.fhir.r4.core.RestfulCapabilityModeCode
import com.google.fhir.r4.core.SearchParamTypeCode
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.SystemRestfulInteractionValueSet
import com.google.fhir.r4.core.TypeRestfulInteractionValueSet
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Enumerations

public object CapabilityStatementConverter {
  @JvmStatic
  public fun CapabilityStatement.toHapi(): org.hl7.fhir.r4.model.CapabilityStatement {
    val hapiValue = org.hl7.fhir.r4.model.CapabilityStatement()
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
    hapiValue.setKind(
      org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementKind.valueOf(
        kind.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (instantiatesCount > 0) {
      hapiValue.setInstantiates(instantiatesList.map { it.toHapi() })
    }
    if (importsCount > 0) {
      hapiValue.setImports(importsList.map { it.toHapi() })
    }
    if (hasSoftware()) {
      hapiValue.setSoftware(software.toHapi())
    }
    if (hasImplementation()) {
      hapiValue.setImplementation(implementation.toHapi())
    }
    hapiValue.setFhirVersion(
      Enumerations.FHIRVersion.valueOf(fhirVersion.value.name.hapiCodeCheck().replace("_", ""))
    )
    formatList.map { hapiValue.addFormat(it.value.hapiCodeCheck()) }
    patchFormatList.map { hapiValue.addPatchFormat(it.value.hapiCodeCheck()) }
    if (implementationGuideCount > 0) {
      hapiValue.setImplementationGuide(implementationGuideList.map { it.toHapi() })
    }
    if (restCount > 0) {
      hapiValue.setRest(restList.map { it.toHapi() })
    }
    if (messagingCount > 0) {
      hapiValue.setMessaging(messagingList.map { it.toHapi() })
    }
    if (documentCount > 0) {
      hapiValue.setDocument(documentList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.CapabilityStatement.toProto(): CapabilityStatement {
    val protoValue = CapabilityStatement.newBuilder().setId(Id.newBuilder().setValue(id))
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
      CapabilityStatement.StatusCode.newBuilder()
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
    protoValue.setKind(
      CapabilityStatement.KindCode.newBuilder()
        .setValue(
          CapabilityStatementKindCode.Value.valueOf(
            kind.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasInstantiates()) {
      protoValue.addAllInstantiates(instantiates.map { it.toProto() })
    }
    if (hasImports()) {
      protoValue.addAllImports(imports.map { it.toProto() })
    }
    if (hasSoftware()) {
      protoValue.setSoftware(software.toProto())
    }
    if (hasImplementation()) {
      protoValue.setImplementation(implementation.toProto())
    }
    protoValue.setFhirVersion(
      CapabilityStatement.FhirVersionCode.newBuilder()
        .setValue(
          FHIRVersionCode.Value.valueOf(
            fhirVersion.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    protoValue.addAllFormat(
      format.map {
        CapabilityStatement.FormatCode.newBuilder().setValue(it.value.protoCodeCheck()).build()
      }
    )
    protoValue.addAllPatchFormat(
      patchFormat.map {
        CapabilityStatement.PatchFormatCode.newBuilder().setValue(it.value.protoCodeCheck()).build()
      }
    )
    if (hasImplementationGuide()) {
      protoValue.addAllImplementationGuide(implementationGuide.map { it.toProto() })
    }
    if (hasRest()) {
      protoValue.addAllRest(rest.map { it.toProto() })
    }
    if (hasMessaging()) {
      protoValue.addAllMessaging(messaging.map { it.toProto() })
    }
    if (hasDocument()) {
      protoValue.addAllDocument(document.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementSoftwareComponent.toProto():
    CapabilityStatement.Software {
    val protoValue =
      CapabilityStatement.Software.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.setName(nameElement.toProto())
    }
    if (hasVersion()) {
      protoValue.setVersion(versionElement.toProto())
    }
    if (hasReleaseDate()) {
      protoValue.setReleaseDate(releaseDateElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementImplementationComponent.toProto():
    CapabilityStatement.Implementation {
    val protoValue =
      CapabilityStatement.Implementation.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.setDescription(descriptionElement.toProto())
    }
    if (hasUrl()) {
      protoValue.setUrl(urlElement.toProto())
    }
    if (hasCustodian()) {
      protoValue.setCustodian(custodian.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestComponent.toProto():
    CapabilityStatement.Rest {
    val protoValue = CapabilityStatement.Rest.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.setMode(
      CapabilityStatement.Rest.ModeCode.newBuilder()
        .setValue(
          RestfulCapabilityModeCode.Value.valueOf(
            mode.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasDocumentation()) {
      protoValue.setDocumentation(documentationElement.toProto())
    }
    if (hasSecurity()) {
      protoValue.setSecurity(security.toProto())
    }
    if (hasResource()) {
      protoValue.addAllResource(resource.map { it.toProto() })
    }
    if (hasInteraction()) {
      protoValue.addAllInteraction(interaction.map { it.toProto() })
    }
    if (hasCompartment()) {
      protoValue.addAllCompartment(compartment.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestSecurityComponent.toProto():
    CapabilityStatement.Rest.Security {
    val protoValue =
      CapabilityStatement.Rest.Security.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCors()) {
      protoValue.setCors(corsElement.toProto())
    }
    if (hasService()) {
      protoValue.addAllService(service.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.setDescription(descriptionElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestResourceComponent.toProto():
    CapabilityStatement.Rest.Resource {
    val protoValue =
      CapabilityStatement.Rest.Resource.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.setType(
      CapabilityStatement.Rest.Resource.TypeCode.newBuilder()
        .setValue(ResourceTypeCode.Value.valueOf(type))
        .build()
    )
    if (hasProfile()) {
      protoValue.setProfile(profileElement.toProto())
    }
    if (hasSupportedProfile()) {
      protoValue.addAllSupportedProfile(supportedProfile.map { it.toProto() })
    }
    if (hasDocumentation()) {
      protoValue.setDocumentation(documentationElement.toProto())
    }
    if (hasInteraction()) {
      protoValue.addAllInteraction(interaction.map { it.toProto() })
    }
    protoValue.setVersioning(
      CapabilityStatement.Rest.Resource.VersioningCode.newBuilder()
        .setValue(
          ResourceVersionPolicyCode.Value.valueOf(
            versioning.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasReadHistory()) {
      protoValue.setReadHistory(readHistoryElement.toProto())
    }
    if (hasUpdateCreate()) {
      protoValue.setUpdateCreate(updateCreateElement.toProto())
    }
    if (hasConditionalCreate()) {
      protoValue.setConditionalCreate(conditionalCreateElement.toProto())
    }
    protoValue.setConditionalRead(
      CapabilityStatement.Rest.Resource.ConditionalReadCode.newBuilder()
        .setValue(
          ConditionalReadStatusCode.Value.valueOf(
            conditionalRead.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasConditionalUpdate()) {
      protoValue.setConditionalUpdate(conditionalUpdateElement.toProto())
    }
    protoValue.setConditionalDelete(
      CapabilityStatement.Rest.Resource.ConditionalDeleteCode.newBuilder()
        .setValue(
          ConditionalDeleteStatusCode.Value.valueOf(
            conditionalDelete.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    protoValue.addAllReferencePolicy(
      referencePolicy.map {
        CapabilityStatement.Rest.Resource.ReferencePolicyCode.newBuilder()
          .setValue(
            ReferenceHandlingPolicyCode.Value.valueOf(
              it.value.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
      }
    )
    if (hasSearchInclude()) {
      protoValue.addAllSearchInclude(searchInclude.map { it.toProto() })
    }
    if (hasSearchRevInclude()) {
      protoValue.addAllSearchRevInclude(searchRevInclude.map { it.toProto() })
    }
    if (hasSearchParam()) {
      protoValue.addAllSearchParam(searchParam.map { it.toProto() })
    }
    if (hasOperation()) {
      protoValue.addAllOperation(operation.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.CapabilityStatement.ResourceInteractionComponent.toProto():
    CapabilityStatement.Rest.Resource.ResourceInteraction {
    val protoValue =
      CapabilityStatement.Rest.Resource.ResourceInteraction.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.setCode(
      CapabilityStatement.Rest.Resource.ResourceInteraction.CodeType.newBuilder()
        .setValue(
          TypeRestfulInteractionValueSet.Value.valueOf(
            code.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasDocumentation()) {
      protoValue.setDocumentation(documentationElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestResourceSearchParamComponent.toProto():
    CapabilityStatement.Rest.Resource.SearchParam {
    val protoValue =
      CapabilityStatement.Rest.Resource.SearchParam.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.setName(nameElement.toProto())
    }
    if (hasDefinition()) {
      protoValue.setDefinition(definitionElement.toProto())
    }
    protoValue.setType(
      CapabilityStatement.Rest.Resource.SearchParam.TypeCode.newBuilder()
        .setValue(
          SearchParamTypeCode.Value.valueOf(
            type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasDocumentation()) {
      protoValue.setDocumentation(documentationElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestResourceOperationComponent.toProto():
    CapabilityStatement.Rest.Resource.Operation {
    val protoValue =
      CapabilityStatement.Rest.Resource.Operation.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.setName(nameElement.toProto())
    }
    if (hasDefinition()) {
      protoValue.setDefinition(definitionElement.toProto())
    }
    if (hasDocumentation()) {
      protoValue.setDocumentation(documentationElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.CapabilityStatement.SystemInteractionComponent.toProto():
    CapabilityStatement.Rest.SystemInteraction {
    val protoValue =
      CapabilityStatement.Rest.SystemInteraction.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.setCode(
      CapabilityStatement.Rest.SystemInteraction.CodeType.newBuilder()
        .setValue(
          SystemRestfulInteractionValueSet.Value.valueOf(
            code.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasDocumentation()) {
      protoValue.setDocumentation(documentationElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementMessagingComponent.toProto():
    CapabilityStatement.Messaging {
    val protoValue =
      CapabilityStatement.Messaging.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasEndpoint()) {
      protoValue.addAllEndpoint(endpoint.map { it.toProto() })
    }
    if (hasReliableCache()) {
      protoValue.setReliableCache(reliableCacheElement.toProto())
    }
    if (hasDocumentation()) {
      protoValue.setDocumentation(documentationElement.toProto())
    }
    if (hasSupportedMessage()) {
      protoValue.addAllSupportedMessage(supportedMessage.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementMessagingEndpointComponent.toProto():
    CapabilityStatement.Messaging.Endpoint {
    val protoValue =
      CapabilityStatement.Messaging.Endpoint.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasProtocol()) {
      protoValue.setProtocol(protocol.toProto())
    }
    if (hasAddress()) {
      protoValue.setAddress(addressElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementMessagingSupportedMessageComponent.toProto():
    CapabilityStatement.Messaging.SupportedMessage {
    val protoValue =
      CapabilityStatement.Messaging.SupportedMessage.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.setMode(
      CapabilityStatement.Messaging.SupportedMessage.ModeCode.newBuilder()
        .setValue(
          EventCapabilityModeCode.Value.valueOf(
            mode.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasDefinition()) {
      protoValue.setDefinition(definitionElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementDocumentComponent.toProto():
    CapabilityStatement.Document {
    val protoValue =
      CapabilityStatement.Document.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.setMode(
      CapabilityStatement.Document.ModeCode.newBuilder()
        .setValue(
          DocumentModeCode.Value.valueOf(
            mode.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasDocumentation()) {
      protoValue.setDocumentation(documentationElement.toProto())
    }
    if (hasProfile()) {
      protoValue.setProfile(profileElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun CapabilityStatement.Software.toHapi():
    org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementSoftwareComponent {
    val hapiValue = org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementSoftwareComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasName()) {
      hapiValue.setNameElement(name.toHapi())
    }
    if (hasVersion()) {
      hapiValue.setVersionElement(version.toHapi())
    }
    if (hasReleaseDate()) {
      hapiValue.setReleaseDateElement(releaseDate.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun CapabilityStatement.Implementation.toHapi():
    org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementImplementationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementImplementationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasDescription()) {
      hapiValue.setDescriptionElement(description.toHapi())
    }
    if (hasUrl()) {
      hapiValue.setUrlElement(url.toHapi())
    }
    if (hasCustodian()) {
      hapiValue.setCustodian(custodian.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun CapabilityStatement.Rest.toHapi():
    org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestComponent {
    val hapiValue = org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    hapiValue.setMode(
      org.hl7.fhir.r4.model.CapabilityStatement.RestfulCapabilityMode.valueOf(
        mode.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasDocumentation()) {
      hapiValue.setDocumentationElement(documentation.toHapi())
    }
    if (hasSecurity()) {
      hapiValue.setSecurity(security.toHapi())
    }
    if (resourceCount > 0) {
      hapiValue.setResource(resourceList.map { it.toHapi() })
    }
    if (interactionCount > 0) {
      hapiValue.setInteraction(interactionList.map { it.toHapi() })
    }
    if (compartmentCount > 0) {
      hapiValue.setCompartment(compartmentList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun CapabilityStatement.Rest.Security.toHapi():
    org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestSecurityComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestSecurityComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasCors()) {
      hapiValue.setCorsElement(cors.toHapi())
    }
    if (serviceCount > 0) {
      hapiValue.setService(serviceList.map { it.toHapi() })
    }
    if (hasDescription()) {
      hapiValue.setDescriptionElement(description.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun CapabilityStatement.Rest.Resource.toHapi():
    org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestResourceComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestResourceComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    hapiValue.setType(type.value.name)
    if (hasProfile()) {
      hapiValue.setProfileElement(profile.toHapi())
    }
    if (supportedProfileCount > 0) {
      hapiValue.setSupportedProfile(supportedProfileList.map { it.toHapi() })
    }
    if (hasDocumentation()) {
      hapiValue.setDocumentationElement(documentation.toHapi())
    }
    if (interactionCount > 0) {
      hapiValue.setInteraction(interactionList.map { it.toHapi() })
    }
    hapiValue.setVersioning(
      org.hl7.fhir.r4.model.CapabilityStatement.ResourceVersionPolicy.valueOf(
        versioning.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasReadHistory()) {
      hapiValue.setReadHistoryElement(readHistory.toHapi())
    }
    if (hasUpdateCreate()) {
      hapiValue.setUpdateCreateElement(updateCreate.toHapi())
    }
    if (hasConditionalCreate()) {
      hapiValue.setConditionalCreateElement(conditionalCreate.toHapi())
    }
    hapiValue.setConditionalRead(
      org.hl7.fhir.r4.model.CapabilityStatement.ConditionalReadStatus.valueOf(
        conditionalRead.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasConditionalUpdate()) {
      hapiValue.setConditionalUpdateElement(conditionalUpdate.toHapi())
    }
    hapiValue.setConditionalDelete(
      org.hl7.fhir.r4.model.CapabilityStatement.ConditionalDeleteStatus.valueOf(
        conditionalDelete.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    referencePolicyList.forEach {
      hapiValue.addReferencePolicy(
        org.hl7.fhir.r4.model.CapabilityStatement.ReferenceHandlingPolicy.valueOf(
          it.value.name.hapiCodeCheck().replace("_", "")
        )
      )
    }
    if (searchIncludeCount > 0) {
      hapiValue.setSearchInclude(searchIncludeList.map { it.toHapi() })
    }
    if (searchRevIncludeCount > 0) {
      hapiValue.setSearchRevInclude(searchRevIncludeList.map { it.toHapi() })
    }
    if (searchParamCount > 0) {
      hapiValue.setSearchParam(searchParamList.map { it.toHapi() })
    }
    if (operationCount > 0) {
      hapiValue.setOperation(operationList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun CapabilityStatement.Rest.Resource.ResourceInteraction.toHapi():
    org.hl7.fhir.r4.model.CapabilityStatement.ResourceInteractionComponent {
    val hapiValue = org.hl7.fhir.r4.model.CapabilityStatement.ResourceInteractionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    hapiValue.setCode(
      org.hl7.fhir.r4.model.CapabilityStatement.TypeRestfulInteraction.valueOf(
        code.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasDocumentation()) {
      hapiValue.setDocumentationElement(documentation.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun CapabilityStatement.Rest.Resource.SearchParam.toHapi():
    org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestResourceSearchParamComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.CapabilityStatement
        .CapabilityStatementRestResourceSearchParamComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasName()) {
      hapiValue.setNameElement(name.toHapi())
    }
    if (hasDefinition()) {
      hapiValue.setDefinitionElement(definition.toHapi())
    }
    hapiValue.setType(
      Enumerations.SearchParamType.valueOf(type.value.name.hapiCodeCheck().replace("_", ""))
    )
    if (hasDocumentation()) {
      hapiValue.setDocumentationElement(documentation.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun CapabilityStatement.Rest.Resource.Operation.toHapi():
    org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestResourceOperationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestResourceOperationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasName()) {
      hapiValue.setNameElement(name.toHapi())
    }
    if (hasDefinition()) {
      hapiValue.setDefinitionElement(definition.toHapi())
    }
    if (hasDocumentation()) {
      hapiValue.setDocumentationElement(documentation.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun CapabilityStatement.Rest.SystemInteraction.toHapi():
    org.hl7.fhir.r4.model.CapabilityStatement.SystemInteractionComponent {
    val hapiValue = org.hl7.fhir.r4.model.CapabilityStatement.SystemInteractionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    hapiValue.setCode(
      org.hl7.fhir.r4.model.CapabilityStatement.SystemRestfulInteraction.valueOf(
        code.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasDocumentation()) {
      hapiValue.setDocumentationElement(documentation.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun CapabilityStatement.Messaging.toHapi():
    org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementMessagingComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementMessagingComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (endpointCount > 0) {
      hapiValue.setEndpoint(endpointList.map { it.toHapi() })
    }
    if (hasReliableCache()) {
      hapiValue.setReliableCacheElement(reliableCache.toHapi())
    }
    if (hasDocumentation()) {
      hapiValue.setDocumentationElement(documentation.toHapi())
    }
    if (supportedMessageCount > 0) {
      hapiValue.setSupportedMessage(supportedMessageList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun CapabilityStatement.Messaging.Endpoint.toHapi():
    org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementMessagingEndpointComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementMessagingEndpointComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasProtocol()) {
      hapiValue.setProtocol(protocol.toHapi())
    }
    if (hasAddress()) {
      hapiValue.setAddressElement(address.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun CapabilityStatement.Messaging.SupportedMessage.toHapi():
    org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementMessagingSupportedMessageComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.CapabilityStatement
        .CapabilityStatementMessagingSupportedMessageComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    hapiValue.setMode(
      org.hl7.fhir.r4.model.CapabilityStatement.EventCapabilityMode.valueOf(
        mode.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasDefinition()) {
      hapiValue.setDefinitionElement(definition.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun CapabilityStatement.Document.toHapi():
    org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementDocumentComponent {
    val hapiValue = org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementDocumentComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    hapiValue.setMode(
      org.hl7.fhir.r4.model.CapabilityStatement.DocumentMode.valueOf(
        mode.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasDocumentation()) {
      hapiValue.setDocumentationElement(documentation.toHapi())
    }
    if (hasProfile()) {
      hapiValue.setProfileElement(profile.toHapi())
    }
    return hapiValue
  }
}
