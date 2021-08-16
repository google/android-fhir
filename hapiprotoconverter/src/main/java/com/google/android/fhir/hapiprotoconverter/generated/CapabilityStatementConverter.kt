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

object CapabilityStatementConverter {
  @JvmStatic
  fun CapabilityStatement.toHapi(): org.hl7.fhir.r4.model.CapabilityStatement {
    val hapiValue = org.hl7.fhir.r4.model.CapabilityStatement()
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
    hapiValue.kind =
      org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementKind.valueOf(
        kind.value.name.hapiCodeCheck().replace("_", "")
      )
    if (instantiatesCount > 0) {
      hapiValue.instantiates = instantiatesList.map { it.toHapi() }
    }
    if (importsCount > 0) {
      hapiValue.imports = importsList.map { it.toHapi() }
    }
    if (hasSoftware()) {
      hapiValue.software = software.toHapi()
    }
    if (hasImplementation()) {
      hapiValue.implementation = implementation.toHapi()
    }
    hapiValue.fhirVersion =
      Enumerations.FHIRVersion.valueOf(fhirVersion.value.name.hapiCodeCheck().replace("_", ""))
    formatList.map { hapiValue.addFormat(it.value.hapiCodeCheck()) }
    patchFormatList.map { hapiValue.addPatchFormat(it.value.hapiCodeCheck()) }
    if (implementationGuideCount > 0) {
      hapiValue.implementationGuide = implementationGuideList.map { it.toHapi() }
    }
    if (restCount > 0) {
      hapiValue.rest = restList.map { it.toHapi() }
    }
    if (messagingCount > 0) {
      hapiValue.messaging = messagingList.map { it.toHapi() }
    }
    if (documentCount > 0) {
      hapiValue.document = documentList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.CapabilityStatement.toProto(): CapabilityStatement {
    val protoValue = CapabilityStatement.newBuilder().setId(Id.newBuilder().setValue(id))
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
      CapabilityStatement.StatusCode.newBuilder()
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
    protoValue.kind =
      CapabilityStatement.KindCode.newBuilder()
        .setValue(
          CapabilityStatementKindCode.Value.valueOf(
            kind.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasInstantiates()) {
      protoValue.addAllInstantiates(instantiates.map { it.toProto() })
    }
    if (hasImports()) {
      protoValue.addAllImports(imports.map { it.toProto() })
    }
    if (hasSoftware()) {
      protoValue.software = software.toProto()
    }
    if (hasImplementation()) {
      protoValue.implementation = implementation.toProto()
    }
    protoValue.fhirVersion =
      CapabilityStatement.FhirVersionCode.newBuilder()
        .setValue(
          FHIRVersionCode.Value.valueOf(
            fhirVersion.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
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
      protoValue.name = nameElement.toProto()
    }
    if (hasVersion()) {
      protoValue.version = versionElement.toProto()
    }
    if (hasReleaseDate()) {
      protoValue.releaseDate = releaseDateElement.toProto()
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
      protoValue.description = descriptionElement.toProto()
    }
    if (hasUrl()) {
      protoValue.url = urlElement.toProto()
    }
    if (hasCustodian()) {
      protoValue.custodian = custodian.toProto()
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
    protoValue.mode =
      CapabilityStatement.Rest.ModeCode.newBuilder()
        .setValue(
          RestfulCapabilityModeCode.Value.valueOf(
            mode.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasDocumentation()) {
      protoValue.documentation = documentationElement.toProto()
    }
    if (hasSecurity()) {
      protoValue.security = security.toProto()
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
      protoValue.cors = corsElement.toProto()
    }
    if (hasService()) {
      protoValue.addAllService(service.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
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
    protoValue.type =
      CapabilityStatement.Rest.Resource.TypeCode.newBuilder()
        .setValue(ResourceTypeCode.Value.valueOf(type))
        .build()
    if (hasProfile()) {
      protoValue.profile = profileElement.toProto()
    }
    if (hasSupportedProfile()) {
      protoValue.addAllSupportedProfile(supportedProfile.map { it.toProto() })
    }
    if (hasDocumentation()) {
      protoValue.documentation = documentationElement.toProto()
    }
    if (hasInteraction()) {
      protoValue.addAllInteraction(interaction.map { it.toProto() })
    }
    protoValue.versioning =
      CapabilityStatement.Rest.Resource.VersioningCode.newBuilder()
        .setValue(
          ResourceVersionPolicyCode.Value.valueOf(
            versioning.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasReadHistory()) {
      protoValue.readHistory = readHistoryElement.toProto()
    }
    if (hasUpdateCreate()) {
      protoValue.updateCreate = updateCreateElement.toProto()
    }
    if (hasConditionalCreate()) {
      protoValue.conditionalCreate = conditionalCreateElement.toProto()
    }
    protoValue.conditionalRead =
      CapabilityStatement.Rest.Resource.ConditionalReadCode.newBuilder()
        .setValue(
          ConditionalReadStatusCode.Value.valueOf(
            conditionalRead.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasConditionalUpdate()) {
      protoValue.conditionalUpdate = conditionalUpdateElement.toProto()
    }
    protoValue.conditionalDelete =
      CapabilityStatement.Rest.Resource.ConditionalDeleteCode.newBuilder()
        .setValue(
          ConditionalDeleteStatusCode.Value.valueOf(
            conditionalDelete.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
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
    protoValue.code =
      CapabilityStatement.Rest.Resource.ResourceInteraction.CodeType.newBuilder()
        .setValue(
          TypeRestfulInteractionValueSet.Value.valueOf(
            code.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasDocumentation()) {
      protoValue.documentation = documentationElement.toProto()
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
      protoValue.name = nameElement.toProto()
    }
    if (hasDefinition()) {
      protoValue.definition = definitionElement.toProto()
    }
    protoValue.type =
      CapabilityStatement.Rest.Resource.SearchParam.TypeCode.newBuilder()
        .setValue(
          SearchParamTypeCode.Value.valueOf(
            type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasDocumentation()) {
      protoValue.documentation = documentationElement.toProto()
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
      protoValue.name = nameElement.toProto()
    }
    if (hasDefinition()) {
      protoValue.definition = definitionElement.toProto()
    }
    if (hasDocumentation()) {
      protoValue.documentation = documentationElement.toProto()
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
    protoValue.code =
      CapabilityStatement.Rest.SystemInteraction.CodeType.newBuilder()
        .setValue(
          SystemRestfulInteractionValueSet.Value.valueOf(
            code.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasDocumentation()) {
      protoValue.documentation = documentationElement.toProto()
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
      protoValue.reliableCache = reliableCacheElement.toProto()
    }
    if (hasDocumentation()) {
      protoValue.documentation = documentationElement.toProto()
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
      protoValue.protocol = protocol.toProto()
    }
    if (hasAddress()) {
      protoValue.address = addressElement.toProto()
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
    protoValue.mode =
      CapabilityStatement.Messaging.SupportedMessage.ModeCode.newBuilder()
        .setValue(
          EventCapabilityModeCode.Value.valueOf(
            mode.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasDefinition()) {
      protoValue.definition = definitionElement.toProto()
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
    protoValue.mode =
      CapabilityStatement.Document.ModeCode.newBuilder()
        .setValue(
          DocumentModeCode.Value.valueOf(
            mode.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasDocumentation()) {
      protoValue.documentation = documentationElement.toProto()
    }
    if (hasProfile()) {
      protoValue.profile = profileElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun CapabilityStatement.Software.toHapi():
    org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementSoftwareComponent {
    val hapiValue = org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementSoftwareComponent()
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
    if (hasVersion()) {
      hapiValue.versionElement = version.toHapi()
    }
    if (hasReleaseDate()) {
      hapiValue.releaseDateElement = releaseDate.toHapi()
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
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (hasUrl()) {
      hapiValue.urlElement = url.toHapi()
    }
    if (hasCustodian()) {
      hapiValue.custodian = custodian.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun CapabilityStatement.Rest.toHapi():
    org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestComponent {
    val hapiValue = org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    hapiValue.mode =
      org.hl7.fhir.r4.model.CapabilityStatement.RestfulCapabilityMode.valueOf(
        mode.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasDocumentation()) {
      hapiValue.documentationElement = documentation.toHapi()
    }
    if (hasSecurity()) {
      hapiValue.security = security.toHapi()
    }
    if (resourceCount > 0) {
      hapiValue.resource = resourceList.map { it.toHapi() }
    }
    if (interactionCount > 0) {
      hapiValue.interaction = interactionList.map { it.toHapi() }
    }
    if (compartmentCount > 0) {
      hapiValue.compartment = compartmentList.map { it.toHapi() }
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
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCors()) {
      hapiValue.corsElement = cors.toHapi()
    }
    if (serviceCount > 0) {
      hapiValue.service = serviceList.map { it.toHapi() }
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
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
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    hapiValue.type = type.value.name
    if (hasProfile()) {
      hapiValue.profileElement = profile.toHapi()
    }
    if (supportedProfileCount > 0) {
      hapiValue.supportedProfile = supportedProfileList.map { it.toHapi() }
    }
    if (hasDocumentation()) {
      hapiValue.documentationElement = documentation.toHapi()
    }
    if (interactionCount > 0) {
      hapiValue.interaction = interactionList.map { it.toHapi() }
    }
    hapiValue.versioning =
      org.hl7.fhir.r4.model.CapabilityStatement.ResourceVersionPolicy.valueOf(
        versioning.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasReadHistory()) {
      hapiValue.readHistoryElement = readHistory.toHapi()
    }
    if (hasUpdateCreate()) {
      hapiValue.updateCreateElement = updateCreate.toHapi()
    }
    if (hasConditionalCreate()) {
      hapiValue.conditionalCreateElement = conditionalCreate.toHapi()
    }
    hapiValue.conditionalRead =
      org.hl7.fhir.r4.model.CapabilityStatement.ConditionalReadStatus.valueOf(
        conditionalRead.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasConditionalUpdate()) {
      hapiValue.conditionalUpdateElement = conditionalUpdate.toHapi()
    }
    hapiValue.conditionalDelete =
      org.hl7.fhir.r4.model.CapabilityStatement.ConditionalDeleteStatus.valueOf(
        conditionalDelete.value.name.hapiCodeCheck().replace("_", "")
      )
    referencePolicyList.forEach {
      hapiValue.addReferencePolicy(
        org.hl7.fhir.r4.model.CapabilityStatement.ReferenceHandlingPolicy.valueOf(
          it.value.name.hapiCodeCheck().replace("_", "")
        )
      )
    }
    if (searchIncludeCount > 0) {
      hapiValue.searchInclude = searchIncludeList.map { it.toHapi() }
    }
    if (searchRevIncludeCount > 0) {
      hapiValue.searchRevInclude = searchRevIncludeList.map { it.toHapi() }
    }
    if (searchParamCount > 0) {
      hapiValue.searchParam = searchParamList.map { it.toHapi() }
    }
    if (operationCount > 0) {
      hapiValue.operation = operationList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun CapabilityStatement.Rest.Resource.ResourceInteraction.toHapi():
    org.hl7.fhir.r4.model.CapabilityStatement.ResourceInteractionComponent {
    val hapiValue = org.hl7.fhir.r4.model.CapabilityStatement.ResourceInteractionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    hapiValue.code =
      org.hl7.fhir.r4.model.CapabilityStatement.TypeRestfulInteraction.valueOf(
        code.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasDocumentation()) {
      hapiValue.documentationElement = documentation.toHapi()
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
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (hasDefinition()) {
      hapiValue.definitionElement = definition.toHapi()
    }
    hapiValue.type =
      Enumerations.SearchParamType.valueOf(type.value.name.hapiCodeCheck().replace("_", ""))
    if (hasDocumentation()) {
      hapiValue.documentationElement = documentation.toHapi()
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
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (hasDefinition()) {
      hapiValue.definitionElement = definition.toHapi()
    }
    if (hasDocumentation()) {
      hapiValue.documentationElement = documentation.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun CapabilityStatement.Rest.SystemInteraction.toHapi():
    org.hl7.fhir.r4.model.CapabilityStatement.SystemInteractionComponent {
    val hapiValue = org.hl7.fhir.r4.model.CapabilityStatement.SystemInteractionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    hapiValue.code =
      org.hl7.fhir.r4.model.CapabilityStatement.SystemRestfulInteraction.valueOf(
        code.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasDocumentation()) {
      hapiValue.documentationElement = documentation.toHapi()
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
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (endpointCount > 0) {
      hapiValue.endpoint = endpointList.map { it.toHapi() }
    }
    if (hasReliableCache()) {
      hapiValue.reliableCacheElement = reliableCache.toHapi()
    }
    if (hasDocumentation()) {
      hapiValue.documentationElement = documentation.toHapi()
    }
    if (supportedMessageCount > 0) {
      hapiValue.supportedMessage = supportedMessageList.map { it.toHapi() }
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
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasProtocol()) {
      hapiValue.protocol = protocol.toHapi()
    }
    if (hasAddress()) {
      hapiValue.addressElement = address.toHapi()
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
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    hapiValue.mode =
      org.hl7.fhir.r4.model.CapabilityStatement.EventCapabilityMode.valueOf(
        mode.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasDefinition()) {
      hapiValue.definitionElement = definition.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun CapabilityStatement.Document.toHapi():
    org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementDocumentComponent {
    val hapiValue = org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementDocumentComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    hapiValue.mode =
      org.hl7.fhir.r4.model.CapabilityStatement.DocumentMode.valueOf(
        mode.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasDocumentation()) {
      hapiValue.documentationElement = documentation.toHapi()
    }
    if (hasProfile()) {
      hapiValue.profileElement = profile.toHapi()
    }
    return hapiValue
  }
}
