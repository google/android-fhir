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
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toProto
import com.google.fhir.r4.core.Coding
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.MessageDefinition
import com.google.fhir.r4.core.MessageDefinition.Focus
import com.google.fhir.r4.core.MessageSignificanceCategoryCode
import com.google.fhir.r4.core.MessageheaderResponseRequestCode
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.ResourceTypeCode
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.Uri
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UriType

object MessageDefinitionConverter {
  @JvmStatic
  private fun MessageDefinition.EventX.messageDefinitionEventToHapi(): Type {
    if (this.coding != Coding.newBuilder().defaultInstanceForType) {
      return (this.coding).toHapi()
    }
    if (this.uri != Uri.newBuilder().defaultInstanceForType) {
      return (this.uri).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for MessageDefinition.event[x]")
  }

  @JvmStatic
  private fun Type.messageDefinitionEventToProto(): MessageDefinition.EventX {
    val protoValue = MessageDefinition.EventX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Coding) {
      protoValue.coding = this.toProto()
    }
    if (this is UriType) {
      protoValue.uri = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun MessageDefinition.toHapi(): org.hl7.fhir.r4.model.MessageDefinition {
    val hapiValue = org.hl7.fhir.r4.model.MessageDefinition()
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
    if (replacesCount > 0) {
      hapiValue.replaces = replacesList.map { it.toHapi() }
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
    if (hasBase()) {
      hapiValue.baseElement = base.toHapi()
    }
    if (parentCount > 0) {
      hapiValue.parent = parentList.map { it.toHapi() }
    }
    if (hasEvent()) {
      hapiValue.event = event.messageDefinitionEventToHapi()
    }
    hapiValue.category =
      org.hl7.fhir.r4.model.MessageDefinition.MessageSignificanceCategory.valueOf(
        category.value.name.hapiCodeCheck().replace("_", "")
      )
    if (focusCount > 0) {
      hapiValue.focus = focusList.map { it.toHapi() }
    }
    hapiValue.responseRequired =
      org.hl7.fhir.r4.model.MessageDefinition.MessageheaderResponseRequest.valueOf(
        responseRequired.value.name.hapiCodeCheck().replace("_", "")
      )
    if (allowedResponseCount > 0) {
      hapiValue.allowedResponse = allowedResponseList.map { it.toHapi() }
    }
    if (graphCount > 0) {
      hapiValue.graph = graphList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.MessageDefinition.toProto(): MessageDefinition {
    val protoValue = MessageDefinition.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasReplaces()) {
      protoValue.addAllReplaces(replaces.map { it.toProto() })
    }
    protoValue.status =
      MessageDefinition.StatusCode.newBuilder()
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
    if (hasBase()) {
      protoValue.base = baseElement.toProto()
    }
    if (hasParent()) {
      protoValue.addAllParent(parent.map { it.toProto() })
    }
    if (hasEvent()) {
      protoValue.event = event.messageDefinitionEventToProto()
    }
    protoValue.category =
      MessageDefinition.CategoryCode.newBuilder()
        .setValue(
          MessageSignificanceCategoryCode.Value.valueOf(
            category.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasFocus()) {
      protoValue.addAllFocus(focus.map { it.toProto() })
    }
    protoValue.responseRequired =
      MessageDefinition.ResponseRequiredCode.newBuilder()
        .setValue(
          MessageheaderResponseRequestCode.Value.valueOf(
            responseRequired.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasAllowedResponse()) {
      protoValue.addAllAllowedResponse(allowedResponse.map { it.toProto() })
    }
    if (hasGraph()) {
      protoValue.addAllGraph(graph.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MessageDefinition.MessageDefinitionFocusComponent.toProto():
    MessageDefinition.Focus {
    val protoValue = MessageDefinition.Focus.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.code =
      MessageDefinition.Focus.CodeType.newBuilder()
        .setValue(ResourceTypeCode.Value.valueOf(code))
        .build()
    if (hasProfile()) {
      protoValue.profile = profileElement.toProto()
    }
    if (hasMin()) {
      protoValue.min = minElement.toProto()
    }
    if (hasMax()) {
      protoValue.max = maxElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MessageDefinition.MessageDefinitionAllowedResponseComponent.toProto():
    MessageDefinition.AllowedResponse {
    val protoValue =
      MessageDefinition.AllowedResponse.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasMessage()) {
      protoValue.message = messageElement.toProto()
    }
    if (hasSituation()) {
      protoValue.situation = situationElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun MessageDefinition.Focus.toHapi():
    org.hl7.fhir.r4.model.MessageDefinition.MessageDefinitionFocusComponent {
    val hapiValue = org.hl7.fhir.r4.model.MessageDefinition.MessageDefinitionFocusComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    hapiValue.code = code.value.name
    if (hasProfile()) {
      hapiValue.profileElement = profile.toHapi()
    }
    if (hasMin()) {
      hapiValue.minElement = min.toHapi()
    }
    if (hasMax()) {
      hapiValue.maxElement = max.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun MessageDefinition.AllowedResponse.toHapi():
    org.hl7.fhir.r4.model.MessageDefinition.MessageDefinitionAllowedResponseComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MessageDefinition.MessageDefinitionAllowedResponseComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasMessage()) {
      hapiValue.messageElement = message.toHapi()
    }
    if (hasSituation()) {
      hapiValue.situationElement = situation.toHapi()
    }
    return hapiValue
  }
}
