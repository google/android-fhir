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

import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UrlConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UrlConverter.toProto
import com.google.fhir.r4.core.Coding
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.MessageHeader
import com.google.fhir.r4.core.MessageHeader.Response
import com.google.fhir.r4.core.ResponseTypeCode
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.Uri
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UriType

object MessageHeaderConverter {
  private fun MessageHeader.EventX.messageHeaderEventToHapi(): Type {
    if (this.coding != Coding.newBuilder().defaultInstanceForType) {
      return (this.coding).toHapi()
    }
    if (this.uri != Uri.newBuilder().defaultInstanceForType) {
      return (this.uri).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for MessageHeader.event[x]")
  }

  private fun Type.messageHeaderEventToProto(): MessageHeader.EventX {
    val protoValue = MessageHeader.EventX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Coding) {
      protoValue.coding = this.toProto()
    }
    if (this is UriType) {
      protoValue.uri = this.toProto()
    }
    return protoValue.build()
  }

  fun MessageHeader.toHapi(): org.hl7.fhir.r4.model.MessageHeader {
    val hapiValue = org.hl7.fhir.r4.model.MessageHeader()
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
    if (hasEvent()) {
      hapiValue.event = event.messageHeaderEventToHapi()
    }
    if (destinationCount > 0) {
      hapiValue.destination = destinationList.map { it.toHapi() }
    }
    if (hasSender()) {
      hapiValue.sender = sender.toHapi()
    }
    if (hasEnterer()) {
      hapiValue.enterer = enterer.toHapi()
    }
    if (hasAuthor()) {
      hapiValue.author = author.toHapi()
    }
    if (hasSource()) {
      hapiValue.source = source.toHapi()
    }
    if (hasResponsible()) {
      hapiValue.responsible = responsible.toHapi()
    }
    if (hasReason()) {
      hapiValue.reason = reason.toHapi()
    }
    if (hasResponse()) {
      hapiValue.response = response.toHapi()
    }
    if (focusCount > 0) {
      hapiValue.focus = focusList.map { it.toHapi() }
    }
    if (hasDefinition()) {
      hapiValue.definitionElement = definition.toHapi()
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.MessageHeader.toProto(): MessageHeader {
    val protoValue = MessageHeader.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasEvent()) {
      protoValue.event = event.messageHeaderEventToProto()
    }
    if (hasDestination()) {
      protoValue.addAllDestination(destination.map { it.toProto() })
    }
    if (hasSender()) {
      protoValue.sender = sender.toProto()
    }
    if (hasEnterer()) {
      protoValue.enterer = enterer.toProto()
    }
    if (hasAuthor()) {
      protoValue.author = author.toProto()
    }
    if (hasSource()) {
      protoValue.source = source.toProto()
    }
    if (hasResponsible()) {
      protoValue.responsible = responsible.toProto()
    }
    if (hasReason()) {
      protoValue.reason = reason.toProto()
    }
    if (hasResponse()) {
      protoValue.response = response.toProto()
    }
    if (hasFocus()) {
      protoValue.addAllFocus(focus.map { it.toProto() })
    }
    if (hasDefinition()) {
      protoValue.definition = definitionElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MessageHeader.MessageDestinationComponent.toProto():
    MessageHeader.MessageDestination {
    val protoValue =
      MessageHeader.MessageDestination.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasTarget()) {
      protoValue.target = target.toProto()
    }
    if (hasEndpoint()) {
      protoValue.endpoint = endpointElement.toProto()
    }
    if (hasReceiver()) {
      protoValue.receiver = receiver.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MessageHeader.MessageSourceComponent.toProto():
    MessageHeader.MessageSource {
    val protoValue =
      MessageHeader.MessageSource.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasSoftware()) {
      protoValue.software = softwareElement.toProto()
    }
    if (hasVersion()) {
      protoValue.version = versionElement.toProto()
    }
    if (hasContact()) {
      protoValue.contact = contact.toProto()
    }
    if (hasEndpoint()) {
      protoValue.endpoint = endpointElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MessageHeader.MessageHeaderResponseComponent.toProto():
    MessageHeader.Response {
    val protoValue = MessageHeader.Response.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasIdentifier()) {
      protoValue.identifier = identifierElement.toProto()
    }
    protoValue.code =
      MessageHeader.Response.CodeType.newBuilder()
        .setValue(
          ResponseTypeCode.Value.valueOf(
            code.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasDetails()) {
      protoValue.details = details.toProto()
    }
    return protoValue.build()
  }

  private fun MessageHeader.MessageDestination.toHapi():
    org.hl7.fhir.r4.model.MessageHeader.MessageDestinationComponent {
    val hapiValue = org.hl7.fhir.r4.model.MessageHeader.MessageDestinationComponent()
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
    if (hasTarget()) {
      hapiValue.target = target.toHapi()
    }
    if (hasEndpoint()) {
      hapiValue.endpointElement = endpoint.toHapi()
    }
    if (hasReceiver()) {
      hapiValue.receiver = receiver.toHapi()
    }
    return hapiValue
  }

  private fun MessageHeader.MessageSource.toHapi():
    org.hl7.fhir.r4.model.MessageHeader.MessageSourceComponent {
    val hapiValue = org.hl7.fhir.r4.model.MessageHeader.MessageSourceComponent()
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
    if (hasSoftware()) {
      hapiValue.softwareElement = software.toHapi()
    }
    if (hasVersion()) {
      hapiValue.versionElement = version.toHapi()
    }
    if (hasContact()) {
      hapiValue.contact = contact.toHapi()
    }
    if (hasEndpoint()) {
      hapiValue.endpointElement = endpoint.toHapi()
    }
    return hapiValue
  }

  private fun MessageHeader.Response.toHapi():
    org.hl7.fhir.r4.model.MessageHeader.MessageHeaderResponseComponent {
    val hapiValue = org.hl7.fhir.r4.model.MessageHeader.MessageHeaderResponseComponent()
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
    hapiValue.code =
      org.hl7.fhir.r4.model.MessageHeader.ResponseType.valueOf(
        code.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasDetails()) {
      hapiValue.details = details.toHapi()
    }
    return hapiValue
  }
}
