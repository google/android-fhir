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
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toProto
import com.google.fhir.r4.core.CompartmentDefinition
import com.google.fhir.r4.core.CompartmentDefinition.Resource
import com.google.fhir.r4.core.CompartmentTypeCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.ResourceTypeCode
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Enumerations

object CompartmentDefinitionConverter {
  @JvmStatic
  fun CompartmentDefinition.toHapi(): org.hl7.fhir.r4.model.CompartmentDefinition {
    val hapiValue = org.hl7.fhir.r4.model.CompartmentDefinition()
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
    if (hasPurpose()) {
        hapiValue.purposeElement = purpose.toHapi()
    }
      hapiValue.code = org.hl7.fhir.r4.model.CompartmentDefinition.CompartmentType.valueOf(
          code.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasSearch()) {
        hapiValue.searchElement = search.toHapi()
    }
    if (resourceCount > 0) {
        hapiValue.resource = resourceList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.CompartmentDefinition.toProto(): CompartmentDefinition {
    val protoValue = CompartmentDefinition.newBuilder().setId(Id.newBuilder().setValue(id))
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
      protoValue.status = CompartmentDefinition.StatusCode.newBuilder()
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
    if (hasPurpose()) {
        protoValue.purpose = purposeElement.toProto()
    }
      protoValue.code = CompartmentDefinition.CodeType.newBuilder()
          .setValue(
              CompartmentTypeCode.Value.valueOf(
                  code.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasSearch()) {
        protoValue.search = searchElement.toProto()
    }
    if (hasResource()) {
      protoValue.addAllResource(resource.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.CompartmentDefinition.CompartmentDefinitionResourceComponent.toProto():
    CompartmentDefinition.Resource {
    val protoValue =
      CompartmentDefinition.Resource.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
      protoValue.code = CompartmentDefinition.Resource.CodeType.newBuilder()
          .setValue(ResourceTypeCode.Value.valueOf(code))
          .build()
    if (hasParam()) {
      protoValue.addAllParam(param.map { it.toProto() })
    }
    if (hasDocumentation()) {
        protoValue.documentation = documentationElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun CompartmentDefinition.Resource.toHapi():
    org.hl7.fhir.r4.model.CompartmentDefinition.CompartmentDefinitionResourceComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.CompartmentDefinition.CompartmentDefinitionResourceComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
      hapiValue.code = code.value.name
    if (paramCount > 0) {
        hapiValue.param = paramList.map { it.toHapi() }
    }
    if (hasDocumentation()) {
        hapiValue.documentationElement = documentation.toHapi()
    }
    return hapiValue
  }
}
