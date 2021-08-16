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

import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UrlConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UrlConverter.toProto
import com.google.fhir.r4.core.Endpoint
import com.google.fhir.r4.core.EndpointStatusCode
import com.google.fhir.r4.core.Id
import kotlin.jvm.JvmStatic

object EndpointConverter {
  @JvmStatic
  fun Endpoint.toHapi(): org.hl7.fhir.r4.model.Endpoint {
    val hapiValue = org.hl7.fhir.r4.model.Endpoint()
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
    if (identifierCount > 0) {
        hapiValue.identifier = identifierList.map { it.toHapi() }
    }
      hapiValue.status = org.hl7.fhir.r4.model.Endpoint.EndpointStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasConnectionType()) {
        hapiValue.connectionType = connectionType.toHapi()
    }
    if (hasName()) {
        hapiValue.nameElement = name.toHapi()
    }
    if (hasManagingOrganization()) {
        hapiValue.managingOrganization = managingOrganization.toHapi()
    }
    if (contactCount > 0) {
        hapiValue.contact = contactList.map { it.toHapi() }
    }
    if (hasPeriod()) {
        hapiValue.period = period.toHapi()
    }
    if (payloadTypeCount > 0) {
        hapiValue.payloadType = payloadTypeList.map { it.toHapi() }
    }
    payloadMimeTypeList.map { hapiValue.addPayloadMimeType(it.value.hapiCodeCheck()) }
    if (hasAddress()) {
        hapiValue.addressElement = address.toHapi()
    }
    if (headerCount > 0) {
        hapiValue.header = headerList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.Endpoint.toProto(): Endpoint {
    val protoValue = Endpoint.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasIdentifier()) {
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
      protoValue.status = Endpoint.StatusCode.newBuilder()
          .setValue(
              EndpointStatusCode.Value.valueOf(
                  status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasConnectionType()) {
        protoValue.connectionType = connectionType.toProto()
    }
    if (hasName()) {
        protoValue.name = nameElement.toProto()
    }
    if (hasManagingOrganization()) {
        protoValue.managingOrganization = managingOrganization.toProto()
    }
    if (hasContact()) {
      protoValue.addAllContact(contact.map { it.toProto() })
    }
    if (hasPeriod()) {
        protoValue.period = period.toProto()
    }
    if (hasPayloadType()) {
      protoValue.addAllPayloadType(payloadType.map { it.toProto() })
    }
    protoValue.addAllPayloadMimeType(
      payloadMimeType.map {
        Endpoint.PayloadMimeTypeCode.newBuilder().setValue(it.value.protoCodeCheck()).build()
      }
    )
    if (hasAddress()) {
        protoValue.address = addressElement.toProto()
    }
    if (hasHeader()) {
      protoValue.addAllHeader(header.map { it.toProto() })
    }
    return protoValue.build()
  }
}
